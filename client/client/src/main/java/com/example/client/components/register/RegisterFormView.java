package com.example.client.components.register;

import com.example.client.HotelBookingFXClient;
import com.example.client.UserServiceClient;
import com.example.enums.GRANTS;
import com.example.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class RegisterFormView extends VBox {
    private UserServiceClient userClient;
    private Stage primaryStage;
    private HotelBookingFXClient mainApp;

    public RegisterFormView(UserServiceClient userClient, Stage primaryStage, HotelBookingFXClient mainApp) {
        this.userClient = userClient;
        this.primaryStage = primaryStage;
        this.mainApp = mainApp;
        initialize();
    }

    private void initialize() {
        // Dodaj ciemne tło do głównego VBox
        this.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.7), CornerRadii.EMPTY, Insets.EMPTY)));

        Label registerTitleLabel = new Label("Panel rejestracji");
        registerTitleLabel.setTextFill(Color.WHITE);
        registerTitleLabel.setFont(Font.font(45));
        registerTitleLabel.setStyle("-fx-font-family: 'Times New Roman';");
        registerTitleLabel.setPadding(new Insets(0, 0, 80, 0));

        Label usernameLabel = new Label("Nazwa użytkownika:");
        usernameLabel.setTextFill(Color.WHITE);
        usernameLabel.setFont(Font.font(16));
        usernameLabel.setStyle("-fx-font-family: 'Times New Roman';");

        TextField usernameField = new TextField();
        usernameField.setStyle("-fx-font-family: 'Times New Roman';");

        Label passwordLabel = new Label("Hasło:");
        passwordLabel.setTextFill(Color.WHITE);
        passwordLabel.setFont(Font.font(16));
        passwordLabel.setStyle("-fx-font-family: 'Times New Roman';");

        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-font-family: 'Times New Roman';");

        Label confirmPasswordLabel = new Label("Potwierdź hasło:");
        confirmPasswordLabel.setTextFill(Color.WHITE);
        confirmPasswordLabel.setFont(Font.font(16));
        confirmPasswordLabel.setStyle("-fx-font-family: 'Times New Roman';");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setStyle("-fx-font-family: 'Times New Roman';");

        Button registerButton = new Button("Zarejestruj");
        registerButton.setStyle("-fx-font-family: 'Times New Roman';");

        Button backButton = new Button("Powrót do logowania");
        backButton.setStyle("-fx-font-family: 'Times New Roman';");

        HBox buttonBox = new HBox(10, backButton, registerButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        VBox contentBox = new VBox(10);
        contentBox.getChildren().addAll(registerTitleLabel, usernameLabel, usernameField, passwordLabel, passwordField, confirmPasswordLabel, confirmPasswordField, buttonBox);
        contentBox.setPadding(new Insets(20, 40, 20, 40));
        contentBox.setAlignment(Pos.CENTER_LEFT);

        StackPane cardPane = new StackPane();
        cardPane.getChildren().add(contentBox);
        cardPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-background-radius: 10;");
        cardPane.setPadding(new Insets(20));

        // Dodaj kartę do VBoxa i ustaw wypełnienie na całe okno
        this.getChildren().add(cardPane);
        VBox.setVgrow(cardPane, Priority.ALWAYS);

        registerButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert("Wszystkie pola muszą być wypełnione.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showAlert("Hasła nie są zgodne.");
                return;
            }

            User user = new User(null, username, password, GRANTS.USER); // Przykład, że domyślnie każdy użytkownik ma uprawnienia USER
            boolean success = userClient.registerUser(user);
            if (success) {
                mainApp.showLoginFormView();
            } else {
                showAlert("Rejestracja nie powiodła się.");
            }
        });

        backButton.setOnAction(event -> mainApp.showLoginFormView());

        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(20));

        // Ustaw tło dla całego VBoxa
        this.setBackground(new Background(new BackgroundImage(
                new Image(getClass().getResourceAsStream("/images/theme.jpeg")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        )));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
