package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {
    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check; //boolean comeca sempre com false
    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public ChessMatch() {
        board = new Board(8, 8);
        turn = 1;
        currentPlayer = Color.YELLOW;
        initialSetup();
    }

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean getCheck(){
        return check;
    }

    public ChessPiece[][] getPieces(){
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i = 0; i < board.getRows(); i++){
            for (int j = 0; j < board.getColumns(); j++){
                mat[i][j] = (ChessPiece) board.piece(i,j);
                //upcasting -> as pecas que estao no atributo board sao do tipo ChessPiece, ou seja, ja foi feito um upcasting
                //dowscasting -> chesspiece extends piece
            }
        }
        return mat;
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition){
        Position position = sourcePosition.toPosition(); //upcasting to Position
        validateSourcePosition(position);
        return board.piece(position).possibleMoves(); //acho que o metodo vai vim de uma que tem o possibleMoves implementado
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition){
        Position source = sourcePosition.toPosition(); //de ChessPosition passa a ser Position -> upCasting
        Position target = targetPosition.toPosition(); //de ChessPosition passa a ser Position -> upCasting
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);

        if(testCheck(currentPlayer)){
            undoMove(source, target, capturedPiece);
            throw new ChessException("You can't put yourself in check");
        }

        check = (testCheck(opponent(currentPlayer))) ? true : false;

        nextTurn();
        return (ChessPiece) capturedPiece;
    }

    private Piece makeMove(Position source, Position target){
        Piece p = board.removePiece(source); //retorna a peca com sua posicao valendo null
        Piece capturedPiece = board.removePiece(target); //retira uma possivel peca capturada
        board.placePiece(p, target); //a peca(atributo p) e colocoda na posicao target no board e recebe target como sua posicao

        if (capturedPiece != null){
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }

        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece){
        Piece p = board.removePiece(target);
        board.placePiece(p, source);

        if (capturedPiece != null){
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }
    }

    private void validateSourcePosition(Position position){
        if(!board.thereIsAPiece(position)){
            throw new ChessException("There is no piece on source position");
        }
        if (currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
            //downcasting porque a peca que o board retorna e do tipo mais generico que nao tem o getColor, so o ChessPiece
            throw new ChessException("The chosen piece is not yours");
        }
        if(!board.piece(position).isThereAnyPossibleMove()){
            throw new ChessException("There is no possible moves for the chosen piece");
        }
    }

    private void validateTargetPosition(Position source, Position target){
        if(!board.piece(source).possibleMove(target)){
            //se para a posicao de origem a posicao de destino nao e um movimento possivel...
            throw new ChessException("The chosen piece can't move to target position");
        }
    }

    private void nextTurn(){
        turn++;
        currentPlayer = (currentPlayer == Color.YELLOW) ? Color.RED : Color.YELLOW;
    }

    private Color opponent(Color color){ //dado um cor devolve o oponente dessa cor
        return (color == Color.YELLOW) ? Color.RED : Color.YELLOW;
    }

    private ChessPiece king(Color color){
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p : list){
            if (p instanceof King){
                return (ChessPiece) p;
            }
        }
        throw new IllegalStateException("There is no " + color + " king on the board");
    }

    private boolean testCheck(Color color){
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
        for (Piece p : opponentPieces){
            boolean[][] mat = p.possibleMoves();
            if(mat[kingPosition.getRow()][kingPosition.getColumn()]){
                return true;
            }
        }
        return false;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece){ //upcasting
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
        //upcasting do ChessPiece para Piece
        //o toPosition do ChessPositon retorna um Position
    }

    private void initialSetup(){
        placeNewPiece('c', 1, new Rook(board, Color.YELLOW)); //upcasting
        placeNewPiece('c', 2, new Rook(board, Color.YELLOW));
        placeNewPiece('d', 2, new Rook(board, Color.YELLOW));
        placeNewPiece('e', 2, new Rook(board, Color.YELLOW));
        placeNewPiece('e', 1, new Rook(board, Color.YELLOW));
        placeNewPiece('d', 1, new King(board, Color.YELLOW));

        placeNewPiece('c', 7, new Rook(board, Color.RED));
        placeNewPiece('c', 8, new Rook(board, Color.RED));
        placeNewPiece('d', 7, new Rook(board, Color.RED));
        placeNewPiece('e', 7, new Rook(board, Color.RED));
        placeNewPiece('e', 8, new Rook(board, Color.RED));
        placeNewPiece('d', 8, new King(board, Color.RED));
    }

}
