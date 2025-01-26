package com.demotestpackage.demotest;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.List;

public class CheckerGUI extends Application {
    private Button[][] buttons;
    private GameManager gameManager;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private Circle previousSelectedPiece = null;

    public CheckerGUI() {
        // No-arg constructor for JavaFX application
    }

    public CheckerGUI(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void start(Stage stage) {
        // Initialize the GameManager here if needed
        if (gameManager == null) {
            gameManager = new GameManager("Player 1", "Player 2");
        }

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        buttons = new Button[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Button button = new Button();
                button.setPrefSize(80, 80);
                button.setStyle((row + col) % 2 == 0 ? "-fx-background-color: #E8C38A;" : "-fx-background-color: #5D2C09;");

                int currentRow = row;
                int currentCol = col;

                button.setOnAction(e -> handleClick(currentRow, currentCol));

                buttons[row][col] = button;
                gridPane.add(button, col, row);
            }
        }

        updateBoard(); // Initial rendering of the board

        Scene scene = new Scene(gridPane, 700, 700);
        stage.setTitle("Ethiopian Checkers (Dama)");
        stage.setScene(scene);
        stage.show();
    }

    private void handleClick(int row, int col) {
        Player currentPlayer = gameManager.getCurrentPlayer();
        List<int[]> capturingPieces = gameManager.findCapturingPieces(currentPlayer == gameManager.getPlayer1());

        if (selectedRow == -1 && selectedCol == -1) {
            // First click: select a piece
            if (gameManager.isValidPiece(row, col, currentPlayer)) {
                if (!capturingPieces.isEmpty() && capturingPieces.stream().noneMatch(p -> p[0] == row && p[1] == col)) {
                    showAlert("Error", "You must select a piece that can capture.");
                } else if (gameManager.hasCaptureMoves(row, col) || gameManager.hasLegalMoves(row, col)) {
                    selectedRow = row;
                    selectedCol = col;
                    highlightPiece(row, col);
                    highlightLegalMoves(row, col); // Highlight legal moves
                } else {
                    showAlert("Error", "The selected piece has no legal moves. Choose another piece.");
                }
            } else {
                showAlert("Error", "Invalid piece selected! It's " + currentPlayer.getName() + "'s turn.");
            }
        } else {
            // If the player clicks on another piece of their own, change the selection
            if (gameManager.isValidPiece(row, col, currentPlayer)) {
                if (!capturingPieces.isEmpty() && capturingPieces.stream().noneMatch(p -> p[0] == row && p[1] == col)) {
                    showAlert("Error", "You must select a piece that can capture.");
                } else if (gameManager.hasCaptureMoves(row, col) || gameManager.hasLegalMoves(row, col)) {
                    revertPreviousSelection();
                    removeHighlights(); // Remove highlights from the previous selection
                    selectedRow = row;
                    selectedCol = col;
                    highlightPiece(row, col);
                    highlightLegalMoves(row, col); // Highlight legal moves
                } else {
                    showAlert("Error", "The selected piece has no legal moves. Choose another piece.");
                }
            } else {
                // Second click: attempt to move
                if (gameManager.isValidMove(selectedRow, selectedCol, row, col)) {
                    if (!capturingPieces.isEmpty() && Math.abs(row - selectedRow) != 2) {
                        showAlert("Error", "You must select a destination that captures a piece.");
                    } else {
                        boolean moveSuccessful = gameManager.makeMove(selectedRow, selectedCol, row, col);
                        if (moveSuccessful) {
                            updateBoard();
                            if (gameManager.isGameOver()) {
                                showAlert("Game Over", "Winner: " + gameManager.getWinner());
                                System.exit(0);
                            } else if (Math.abs(row - selectedRow) == 2 && gameManager.canContinueCapturing(row, col)) {
                                showAlert("Notice", "Another capture is possible. Continue capturing.");
                                selectedRow = row;
                                selectedCol = col;
                                return; // Allow the player to continue capturing
                            } else {
                                // Reset selection and switch turns only if no more captures are available
                                revertPreviousSelection();
                                removeHighlights(); // Remove highlights after move
                                selectedRow = -1;
                                selectedCol = -1;
                            }
                        } else {
                            showAlert("Error", "Invalid move! Try again.");
                        }
                    }
                } else {
                    showAlert("Error", "Invalid move! Try again.");
                }
            }
        }
    }

    private void highlightLegalMoves(int row, int col) {
        // Get the list of legal moves for the selected piece
        List<int[]> legalMoves = gameManager.getLegalMoves(row, col);

        for (int[] move : legalMoves) {
            int moveRow = move[0];
            int moveCol = move[1];

            Button button = buttons[moveRow][moveCol];
            button.setStyle("-fx-background-color: #FFFF00;"); // Highlight with yellow
        }
    }

    private void removeHighlights() {
        // Reset all button styles to the default
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                buttons[row][col].setStyle((row + col) % 2 == 0 ? "-fx-background-color: #E8C38A;" : "-fx-background-color: #5D2C09;");
            }
        }
    }

    private void highlightPiece(int row, int col) {
        Button button = buttons[row][col];
        Circle piece = (Circle) button.getGraphic();
        if (piece != null) {
            piece.setScaleX(1.2);
            piece.setScaleY(1.2);
            previousSelectedPiece = piece;
        }
    }

    private void revertPreviousSelection() {
        if (previousSelectedPiece != null) {
            previousSelectedPiece.setScaleX(1.0);
            previousSelectedPiece.setScaleY(1.0);
            previousSelectedPiece = null;
        }
    }

    private void updateBoard() {
        // Update the board based on the game state in GameManager
        char[][] board = gameManager.getBoardState();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char piece = board[row][col];
                Button button = buttons[row][col];
                button.setGraphic(null); // Clear previous graphic

                if (piece != '·') { // If the cell is not empty
                    Circle circle = new Circle(30);
                    circle.setFill(piece == 'X' || piece == 'K' ? Color.BLACK : Color.WHITE); // Black for black pieces, white for white pieces

                    if (piece == 'K' || piece == 'Q') { // If the piece is a king
                        if (piece == 'K') {
                            // White king
                            circle.setFill(Color.WHITE); // White piece
                            javafx.scene.text.Text kingLabel = new javafx.scene.text.Text("K");
                            kingLabel.setFill(Color.BLACK); // Black "K" for white king
                            kingLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

                            // Use a StackPane to combine the circle and the label
                            javafx.scene.layout.StackPane stack = new javafx.scene.layout.StackPane(circle, kingLabel);
                            button.setGraphic(stack);
                        } else if (piece == 'Q') {
                            // Black king
                            circle.setFill(Color.BLACK); // Black piece
                            javafx.scene.text.Text kingLabel = new javafx.scene.text.Text("K");
                            kingLabel.setFill(Color.WHITE); // White "K" for black king
                            kingLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

                            // Use a StackPane to combine the circle and the label
                            javafx.scene.layout.StackPane stack = new javafx.scene.layout.StackPane(circle, kingLabel);
                            button.setGraphic(stack);
                        } else {
                            // Normal pieces
                            button.setGraphic(circle);
                        }
                    } else {
                        // Normal pieces
                        button.setGraphic(circle);
                    }
                }
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
