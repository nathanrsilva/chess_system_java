package chess;

import boardgame.Position;

public class ChessPosition {
    private char column;
    private int row;

    public ChessPosition(char column, int row) {
        if (column < 'a' || column > 'h' || row < 1 || row > 8){
            throw new ChessException("Error instantiating ChessPosition. Valid values are from a1 to h8.");
        }
        this.column = column;
        this.row = row;
    }

    public char getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    protected Position toPosition(){
        //8 - chessrow -> 8 - 6 = 2
        //character - 'a' = 63(c) - 61(a) = 2
        return new Position(8 - row, column - 'a');
    }

    protected static ChessPosition fromPosition(Position position){
        //8 - chessrow = 8 - 4 = 4
        //'a' + column = a + 3 = 64/'d'
        return new ChessPosition((char)('a' + position.getColumn()), (8 - position.getRow()));
    }

    @Override
    public String toString(){
        return "" + column + row; //as aspas para forcar o compilador a entender que e uma concatenacao de strings
    }



}
