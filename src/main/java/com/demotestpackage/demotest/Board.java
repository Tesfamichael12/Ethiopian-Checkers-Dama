package com.demotestpackage.demotest;

// Board Class
class Board {
    private static final int SIZE = 8;
    private static final char EMPTY = '·';
    private final char[][] grid;

    public Board() {
        grid = new char[SIZE][SIZE];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if ((row + col) % 2 != 0) {
                    if (row < 3) {
                        grid[row][col] = 'X';
                    } else if (row > 4) {
                        grid[row][col] = 'O';
                    } else {
                        grid[row][col] = EMPTY;
                    }
                } else {
                    grid[row][col] = EMPTY;
                }
            }
        }
    }

    public void printBoard(int player1Pieces, int player2Pieces) {
        System.out.println("\n  1 2 3 4 5 6 7 8");
        System.out.println("  ---------------");
        for (int row = 0; row < SIZE; row++) {
            System.out.print((row + 1) + "|");
            for (int col = 0; col < SIZE; col++) {
                System.out.print(grid[row][col] + " ");
            }
            System.out.println("|" + (row + 1));
        }
        System.out.println("  ---------------");
        System.out.println("  1 2 3 4 5 6 7 8\n");
        System.out.println("Player 1 (O/K) pieces: " + player1Pieces);
        System.out.println("Player 2 (X/Q) pieces: " + player2Pieces + "\n");
    }

    public char getCell(int row, int col) {
        return grid[row][col];
    }

    public void setCell(int row, int col, char value) {
        grid[row][col] = value;
    }

    public int getSIZE() {
        return SIZE;
    }

    public char[][] getBoard(){
        return grid;
    }

    public boolean isEmptyCell(int row, int col) {
        return grid[row][col] == EMPTY;
    }

    public void clearCell(int row, int col) {
        grid[row][col] = EMPTY;
    }

    public char getEMPTY() {
        return EMPTY;
    }
}
