package com.demotestpackage.demotest;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Enter Player Names");

        // GridPane for layout
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Player 1 Label and TextField
        Label player1Label = new Label("Player 1's name:");
        player1Label.setFont(Font.font("Arial", 14));
        TextField player1Field = createStyledTextField();

        // Player 2 Label and TextField
        Label player2Label = new Label("Player 2's name:");
        player2Label.setFont(Font.font("Arial", 14));
        TextField player2Field = createStyledTextField();

        // Start Button
        Button startButton = new Button("Start Game");
        startButton.setFont(Font.font("Arial", 14));
        startButton.setStyle("-fx-background-color: #5D2C09; -fx-text-fill: white; -fx-padding: 10 20 10 20;");

        startButton.setOnAction(e -> {
            String player1Name = player1Field.getText().trim();
            String player2Name = player2Field.getText().trim();

            if (!player1Name.isEmpty() && !player2Name.isEmpty()) {
                GameManager gameManager = new GameManager(player1Name, player2Name);

                // Launch the CheckerGUI
                CheckerGUI checkerGUI = new CheckerGUI(gameManager);
                try {
                    Stage checkerStage = new Stage();
                    checkerGUI.start(checkerStage); // Explicitly call the start method
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                primaryStage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter names for both players.");
                alert.showAndWait();
            }
        });

        // Add elements to the GridPane
        gridPane.add(player1Label, 0, 0);
        gridPane.add(player1Field, 1, 0);
        gridPane.add(player2Label, 0, 1);
        gridPane.add(player2Field, 1, 1);
        gridPane.add(startButton, 0, 2, 2, 1);
        GridPane.setMargin(startButton, new Insets(10, 0, 0, 0));

        // Create the scene and show the stage
        Scene scene = new Scene(gridPane, 700, 550);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TextField createStyledTextField() {
        TextField textField = new TextField();
        textField.setFont(Font.font("Arial", 14));
        textField.setStyle("-fx-border-color: #5D2C09; -fx-border-width: 1; -fx-padding: 5;");
        textField.setPrefWidth(200);
        return textField;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
