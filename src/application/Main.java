package application;

import boardgame.Board;
import boardgame.Position;
import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;

public class Main {
    public static void main(String[]args){
        Scanner sc = new Scanner(System.in);
        ChessMatch chessMatch = new ChessMatch();
        List<ChessPiece> captured = new ArrayList<>();

        while(!chessMatch.getCheckMate()){
            try{
                UI.printMatch(chessMatch, captured);
                System.out.println();
                System.out.print("Source: ");
                ChessPosition source = UI.readChessPosition(sc); //retorna um novo objeto com as posicoes

                boolean[][] possibleMoves = chessMatch.possibleMoves(source);
                UI.printBoard(chessMatch.getPieces(), possibleMoves);

                System.out.println();
                System.out.print("Target: ");
                ChessPosition target = UI.readChessPosition(sc); //retorna um novo objeto com as posicoes

                ChessPiece capturedPiece = chessMatch.performChessMove(source, target); //retorna a peca que foi capturada

                if(capturedPiece != null){
                    captured.add(capturedPiece);
                }
                if (chessMatch.getPromoted() != null){
                    System.out.print("Enter piece for promotion (B/N/R/Q): ");
                    String type = sc.nextLine();
                    chessMatch.replacePromotedPiece(type);
                }
                
            }catch(ChessException e){
                System.out.println(e.getMessage());
                sc.nextLine();
            }
        }
        UI.printMatch(chessMatch, captured);
    }
}
