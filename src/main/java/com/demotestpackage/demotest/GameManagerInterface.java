package com.demotestpackage.demotest;

import java.util.List;

public interface GameManagerInterface {
    Player getCurrentPlayer();
    List<int[]> findCapturingPieces(boolean isPlayer1);
    String getWinner();
    boolean isValidPiece(int row, int col, Player currentPlayer);
    boolean hasCaptureMoves(int row, int col);
    boolean hasLegalMoves(int row, int col);
    boolean isValidMove(int startRow, int startCol, int endRow, int endCol);
    char[][] getBoardState();
    boolean makeMove(int startRow, int startCol, int endRow, int endCol);
    boolean isGameOver();
    boolean canContinueCapturing(int row, int col);
    List<int[]> getLegalMoves(int row, int col);
}

