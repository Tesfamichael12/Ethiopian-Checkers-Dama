package com.demotestpackage.demotest;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class GameManager implements GameManagerInterface {
    private final Board board;
    private final Player player1;
    private final Player player2;
    private boolean player1Turn = true;
    private int player1Pieces = 12;
    private int player2Pieces = 12;
    private static final char KING1 = 'K';
    private static final char KING2 = 'Q';
    private static final char PLAYER1 = 'O';
    private static final char PLAYER2 = 'X';

    public GameManager() {
        this("Player 1", "Player 2");
    }

    public GameManager(String player1Name, String player2Name) {
        this.board = new Board();
        this.player1 = new Player1(player1Name, PLAYER1, KING1);
        this.player2 = new Player2(player2Name, PLAYER2, KING2);
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);
        while (!isGameOver()) {
            clearConsole();
            board.printBoard(player1Pieces, player2Pieces);
            Player currentPlayer = player1Turn ? player1 : player2;
            System.out.println(currentPlayer.getName() + "'s turn (" +
                    (player1Turn ? "O/K" : "X/Q") + ")");

            int[] from = getPlayerMove(scanner, currentPlayer);
            int[] to = getDestination(scanner, from);

            makeMove(from, to);
            if (isGameOver()) {
                System.out.println("Game Over! " + (player1Turn ? player1.getName() : player2.getName()) + " wins!");
                break;
            }
            switchTurn();
        }
        scanner.close();
    }

    public void switchTurn() {
        player1Turn = !player1Turn;
    }

    public int[] getPlayerMove(Scanner scanner, Player player) {
    int[] from;
    while (true) {
        System.out.print("Select piece to move (row col, e.g. 34): ");
        String input = scanner.next();

        from = parseInput(input);

        if (from == null) {
            System.out.println("Invalid input format! Use row col (e.g. 34)");
            continue;
        }

        if (!isValidPiece(from[0], from[1], player)) {
            System.out.println("Invalid piece selection! Choose your own piece.");
            continue;
        }

        if (!hasLegalMoves(from[0], from[1])) {
            System.out.println("The piece you have selected has no legal move, choose another one.");
            continue;
        }

        System.out.println("Selected piece: (" + (from[0] + 1) + ", " + (from[1] + 1) + ")");

        // Check if a capture move is required
        boolean hasCapture = false;
        for (int row = 0; row < board.getSIZE(); row++) {
            for (int col = 0; col < board.getSIZE(); col++) {
                if (isValidPiece(row, col, player) && hasCaptureMoves(row, col)) {
                    hasCapture = true;
                    break;
                }
            }
            if (hasCapture) break;
        }

        if (hasCapture && !hasCaptureMoves(from[0], from[1])) {
            // Identify capturing pieces
            List<int[]> capturingPieces = findCapturingPieces(player1Turn);
            if (!capturingPieces.isEmpty()) {
                System.out.print("You must select a piece that can capture. The following pieces can capture:");
                for (int[] pos : capturingPieces) {
                    System.out.print("\tPiece at: " + (pos[0] + 1) + (pos[1] + 1));
                }
                System.out.println("\n");
            }
            continue;
        }

        break;
    }
    return from;
}

    public int[] getDestination(Scanner scanner, int[] from) {
    int[] to;
    while (true) {
        System.out.print("Enter destination (row col, e.g., 45): ");
        String input = scanner.next();
        to = parseInput(input);

        if (to == null) {
            System.out.println("Invalid input format! Use row col (e.g., 45)");
            continue;
        }

        // Validate that the move executes a capture if a capture is available
        if (hasCaptureMoves(from[0], from[1]) && Math.abs(to[0] - from[0]) != 2) {
            System.out.println("You must choose a destination that captures an opponent's piece.");
            continue;
        }

        if (!isValidMove(from[0], from[1], to[0], to[1])) {
            // Check if the selected destination is the piece of the current player
            if (isValidPiece(to[0], to[1], player1Turn ? player1 : player2)) {
                System.out.print("You have selected your own piece as the destination. Do you want to change the selected piece? (yes/no): ");
                String response = scanner.next();
                if (response.equalsIgnoreCase("yes")) {
                    from = to;
                    if (!isValidPiece(from[0], from[1], player1Turn ? player1 : player2)) {
                        System.out.println("Invalid piece selection! Choose your own piece.");
                        continue;
                    }
                    if (!hasLegalMoves(from[0], from[1])) {
                        System.out.println("The piece you have selected has no legal move, select another one.");
                        continue;
                    }
                    System.out.println("Selected piece: (" + (from[0] + 1) + ", " + (from[1] + 1) + ")");
                    continue;
                } else {
                    // Get piece selection
                    while (true) {
                        System.out.print("Select piece to move (row col, e.g., 34): ");
                        input = scanner.next();
                        from = parseInput(input);

                        if (from == null) {
                            System.out.println("Invalid input format! Use row col (e.g., 34)");
                            continue;
                        }

                        if (!isValidPiece(from[0], from[1], player1Turn ? player1 : player2)) {
                            System.out.println("Invalid piece selection! Choose your own piece.");
                            continue;
                        }

                        if (!hasLegalMoves(from[0], from[1])) {
                            System.out.println("The piece you have selected has no legal move, select another one.");
                            continue;
                        }

                        System.out.println("Selected piece: (" + (from[0] + 1) + ", " + (from[1] + 1) + ")");

                        // Check if a capture move is required
                        boolean hasCapture = false;
                        for (int row = 0; row < board.getSIZE(); row++) {
                            for (int col = 0; col < board.getSIZE(); col++) {
                                if (isValidPiece(row, col, player1Turn ? player1 : player2) && hasCaptureMoves(row, col)) {
                                    hasCapture = true;
                                    break;
                                }
                            }
                            if (hasCapture) break;
                        }

                        if (hasCapture && !hasCaptureMoves(from[0], from[1])) {
                            // Identify capturing pieces
                            List<int[]> capturingPieces = findCapturingPieces(player1Turn);
                            if (!capturingPieces.isEmpty()) {
                                System.out.print("You must select a piece that can capture. The following pieces can capture:");
                                for (int[] pos : capturingPieces) {
                                    System.out.print("\tPiece at: " + (pos[0] + 1) + (pos[1] + 1));
                                }
                            }
                            continue;
                        }

                        break;
                    }
                }
            }
            System.out.println("Invalid move! Try again.");
            continue;
        }

        break;
    }
    return to;
}

    public int[] parseInput(String input) {
        try {
            int grid = Integer.parseInt(input);
            int row = (grid / 10) - 1;
            int col = (grid % 10) - 1;
            if (row < 0 || row >= board.getSIZE() || col < 0 || col >= board.getSIZE()) {
                return null;
            }
            return new int[]{row, col};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean canContinueCapturing(int row, int col) {
        return hasCaptureMoves(row, col);
    }
    
    public boolean isValidPiece(int row, int col, Player player) {
        char piece = board.getCell(row, col);
        return piece == player.getPieceSymbol() || piece == player.getKingSymbol();
    }

    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < board.getSIZE() && col >= 0 && col < board.getSIZE();
    }

    public boolean isKing(int row, int col) {
    return board.getCell(row, col) == KING1 || board.getCell(row, col) == KING2;
}

    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
    if (toRow < 0 || toRow >= board.getSIZE() || toCol < 0 || toCol >= board.getSIZE()) {
        return false;
    }
    if (board.getCell(toRow, toCol) != board.getEMPTY()) {
        return false;
    }

    boolean isKingPiece = isKing(fromRow, fromCol);
    int direction = player1Turn ? -1 : 1;

    // Regular move
    if (Math.abs(toCol - fromCol) == 1) {
        if (!isKingPiece && (toRow - fromRow) == direction) {
            return true;
        }
        if (isKingPiece && Math.abs(toRow - fromRow) == 1) {
            return true;
        }
    }

    // Capture move
    if (Math.abs(toCol - fromCol) == 2 && (isKingPiece || (toRow - fromRow) == 2 * direction)) {
        int midRow = (fromRow + toRow) / 2;
        int midCol = (fromCol + toCol) / 2;
        char midPiece = board.getCell(midRow, midCol);

        if (player1Turn) {
            return midPiece == PLAYER2 || midPiece == KING2;
        } else {
            return midPiece == PLAYER1 || midPiece == KING1;
        }
    }

    return false;
}

     public boolean hasLegalMoves(int row, int col) {
    int[][] directions = {
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    for (int[] direction : directions) {
        int newRow = row + direction[0];
        int newCol = col + direction[1];

        if (isWithinBounds(newRow, newCol)) {
            char blockingPiece = board.getCell(newRow, newCol);
            if (blockingPiece == board.getEMPTY() || blockingPiece == (player1Turn ? PLAYER2 : PLAYER1) || blockingPiece == (player1Turn ? KING2 : KING1)) {
                if (isValidMove(row, col, newRow, newCol)) {
                    return true;
                }
            }
        }
    }
    return false;
}

     public List<int[]> getLegalMoves(int row, int col) {
        List<int[]> legalMoves = new ArrayList<>();
        int[][] directions = {
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}  // diagonal directions
        };

        for (int[] direction : directions) {
            // First check regular moves (1 square in any direction)
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            if (isWithinBounds(newRow, newCol)) {
                char blockingPiece = board.getCell(newRow, newCol);
                if (blockingPiece == board.getEMPTY()) {
                    // If it's a valid move, add it to the list
                    if (isValidMove(row, col, newRow, newCol)) {
                        legalMoves.add(new int[]{newRow, newCol});
                    }
                }
            }

            // Now check capture moves (2 squares in any direction)
            newRow = row + direction[0] * 2;
            newCol = col + direction[1] * 2;

            if (isWithinBounds(newRow, newCol)) {
                int midRow = row + direction[0]; // Middle square
                int midCol = col + direction[1]; // Middle square
                char midPiece = board.getCell(midRow, midCol);

                // Check if the middle piece is an opponent's piece
                if (midPiece != board.getEMPTY() && (midPiece == PLAYER1 || midPiece == KING1 || midPiece == PLAYER2 || midPiece == KING2)) {
                    // Ensure the destination is empty for a valid capture
                    char blockingPiece = board.getCell(newRow, newCol);
                    if (blockingPiece == board.getEMPTY()) {
                        legalMoves.add(new int[]{newRow, newCol});  // Add the capture move
                    }
                }
            }
        }

        return legalMoves;
    }


    public boolean makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidMove(fromRow, fromCol, toRow, toCol)) {
            return false;
        }

        char piece = board.getCell(fromRow, fromCol);
        board.setCell(fromRow, fromCol, board.getEMPTY());
        board.setCell(toRow, toCol, piece);

        // Check if the move is a capture
        if (Math.abs(toRow - fromRow) == 2 && Math.abs(toCol - fromCol) == 2) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            board.setCell(midRow, midCol, board.getEMPTY());

            if (player1Turn) {
                player2Pieces--;
            } else {
                player1Pieces--;
            }

            // Check if the player can continue capturing
            if (canContinueCapturing(toRow, toCol)) {
                return true; // Do not switch turns
            }
        }

        // Check if the piece should be kinged
        if (toRow == 0 && piece == PLAYER1) {
            board.setCell(toRow, toCol, KING1);
        } else if (toRow == board.getSIZE() - 1 && piece == PLAYER2) {
            board.setCell(toRow, toCol, KING2);
        }

        switchTurn();
        return true;
    }

    public void makeMove(int[] from, int[] to) {
        char piece = board.getCell(from[0], from[1]);
        board.setCell(from[0], from[1], board.getEMPTY());
        board.setCell(to[0], to[1], piece);

        if (Math.abs(to[0] - from[0]) == 2 && Math.abs(to[1] - from[1]) == 2) {
            int midRow = (from[0] + to[0]) / 2;
            int midCol = (from[1] + to[1]) / 2;
            board.setCell(midRow, midCol, board.getEMPTY());

            if (player1Turn) {
                player2Pieces--;
            } else {
                player1Pieces--;
            }
        }
    }

    public boolean hasCaptureMoves(int row, int col) {
        int direction = player1Turn ? -1 : 1; // Forward direction for the current player
        char opponent = player1Turn ? PLAYER2 : PLAYER1;
        char opponentKing = player1Turn ? KING2 : KING1;

        // Check forward captures
        if (row + 2 * direction >= 0 && row + 2 * direction < board.getSIZE()) {
            // Left capture
            if (col - 2 >= 0 &&
                    (board.getCell(row + direction, col - 1) == opponent || board.getCell(row + direction, col - 1) == opponentKing) &&
                    board.getCell(row + 2 * direction, col - 2) == board.getEMPTY()) {
                return true;
            }
            // Right capture
            if (col + 2 < board.getSIZE() &&
                    (board.getCell(row + direction, col + 1) == opponent || board.getCell(row + direction, col + 1) == opponentKing) &&
                    board.getCell(row + 2 * direction, col + 2) == board.getEMPTY()) {
                return true;
            }
        }

        // For king pieces, check backward captures
        if (isKing(row, col) && row - 2 * direction >= 0 && row - 2 * direction < board.getSIZE()) {
            if (col - 2 >= 0 &&
                    (board.getCell(row - direction, col - 1) == opponent || board.getCell(row - direction, col - 1) == opponentKing) &&
                    board.getCell(row - 2 * direction, col - 2) == board.getEMPTY()) {
                return true;
            }
            return col + 2 < board.getSIZE() &&
                    (board.getCell(row - direction, col + 1) == opponent || board.getCell(row - direction, col + 1) == opponentKing) &&
                    board.getCell(row - 2 * direction, col + 2) == board.getEMPTY();
        }
        return false;
    }

    public List<int[]> findCapturingPieces(boolean forPlayer1) {
        List<int[]> capturingPieces = new ArrayList<>();
        for (int row = 0; row < board.getSIZE(); row++) {
            for (int col = 0; col < board.getSIZE(); col++) {
                if (isValidPiece(row, col, forPlayer1 ? player1 : player2) && hasCaptureMoves(row, col)) {
                    capturingPieces.add(new int[]{row, col});
                }
            }
        }
        return capturingPieces;
    }

    public boolean isGameOver() {
        if (player1Pieces == 0 || player2Pieces == 0) {
            return true;
        }
        return !hasValidMoves(true) || !hasValidMoves(false);
    }

    public boolean hasValidMoves(boolean forPlayer1) {
        char piece = forPlayer1 ? PLAYER1 : PLAYER2;
        char kingPiece = forPlayer1 ? KING1 : KING2;
        int direction = forPlayer1 ? -1 : 1;

        for (int row = 0; row < board.getSIZE(); row++) {
            for (int col = 0; col < board.getSIZE(); col++) {
                if (board.getCell(row, col) == piece || board.getCell(row, col) == kingPiece) {
                    int newRow = row + direction;
                    if (newRow >= 0 && newRow < board.getSIZE()) {
                        if (col - 1 >= 0 && board.getCell(newRow, col - 1) == board.getEMPTY()) {
                            return true;
                        }
                        if (col + 1 < board.getSIZE() && board.getCell(newRow, col + 1) == board.getEMPTY()) {
                            return true;
                        }
                    }

                    int jumpRow = row + (2 * direction);
                    if (jumpRow >= 0 && jumpRow < board.getSIZE()) {
                        if (col - 2 >= 0) {
                            int midCol = col - 1;
                            char midPiece = board.getCell(row + direction, midCol);
                            if (board.getCell(jumpRow, col - 2) == board.getEMPTY() &&
                                    ((forPlayer1 && (midPiece == PLAYER2 || midPiece == KING2)) ||
                                            (!forPlayer1 && (midPiece == PLAYER1 || midPiece == KING1)))) {
                                return true;
                            }
                        }
                        if (col + 2 < board.getSIZE()) {
                            int midCol = col + 1;
                            char midPiece = board.getCell(row + direction, midCol);
                            if (board.getCell(jumpRow, col + 2) == board.getEMPTY() &&
                                    ((forPlayer1 && (midPiece == PLAYER2 || midPiece == KING2)) ||
                                            (!forPlayer1 && (midPiece == PLAYER1 || midPiece == KING1)))) {
                                return true;
                            }
                        }
                    }

                    if (board.getCell(row, col) == kingPiece) {
                        newRow = row - direction;
                        if (newRow >= 0 && newRow < board.getSIZE()) {
                            if (col - 1 >= 0 && board.getCell(newRow, col - 1) == board.getEMPTY()) {
                                return true;
                            }
                            if (col + 1 < board.getSIZE() && board.getCell(newRow, col + 1) == board.getEMPTY()) {
                                return true;
                            }
                        }

                        jumpRow = row - (2 * direction);
                        if (jumpRow >= 0 && jumpRow < board.getSIZE()) {
                            if (col - 2 >= 0) {
                                int midCol = col - 1;
                                char midPiece = board.getCell(row - direction, midCol);
                                if (board.getCell(jumpRow, col - 2) == board.getEMPTY() &&
                                        ((forPlayer1 && (midPiece == PLAYER2 || midPiece == KING2)) ||
                                                (!forPlayer1 && (midPiece == PLAYER1 || midPiece == KING1)))) {
                                    return true;
                                }
                            }
                            if (col + 2 < board.getSIZE()) {
                                int midCol = col + 1;
                                char midPiece = board.getCell(row - direction, midCol);
                                if (board.getCell(jumpRow, col + 2) == board.getEMPTY() &&
                                        ((forPlayer1 && (midPiece == PLAYER2 || midPiece == KING2)) ||
                                                (!forPlayer1 && (midPiece == PLAYER1 || midPiece == KING1)))) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public char[][] getBoardState() {
        return board.getBoard();
    }

    public Player getCurrentPlayer() {
        return player1Turn ? player1 : player2;
    }

    public String getWinner() {
        if (player1Pieces == 0) {
            return player2.getName();
        } else if (player2Pieces == 0) {
            return player1.getName();
        } else {
            return player1Turn ? player2.getName() : player1.getName();
        }
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }
}