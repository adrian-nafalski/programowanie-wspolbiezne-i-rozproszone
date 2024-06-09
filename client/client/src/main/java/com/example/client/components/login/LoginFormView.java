package com.example.client.components.login;

import com.example.client.HotelBookingFXClient;
import com.example.client.UserServiceClient;
import com.example.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class LoginFormView extends VBox {
    private UserServiceClient userClient;
    private Stage primaryStage;
    private HotelBookingFXClient mainApp;

    public LoginFormView(UserServiceClient userClient, Stage primaryStage, HotelBookingFXClient mainApp) {
        this.userClient = userClient;
        this.primaryStage = primaryStage;
        this.mainApp = mainApp;
        initialize();
    }

    private void initialize() {
        // Dodaj ciemne tło do głównego VBox
        this.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.7), CornerRadii.EMPTY, Insets.EMPTY)));

        Label loginTitleLabel = new Label("Logowanie");
        loginTitleLabel.setTextFill(Color.WHITE);
        loginTitleLabel.setFont(Font.font(45));
        loginTitleLabel.setStyle("-fx-font-family: 'Times New Roman';");
        loginTitleLabel.setPadding(new Insets(0, 0, 80, 0));

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

        Text createAccountText = new Text("Nie masz konta?");
        createAccountText.setFill(Color.WHITE);
        createAccountText.setStyle("-fx-font-family: 'Times New Roman';");

        Hyperlink createAccountLink = new Hyperlink("Utwórz konto");
        createAccountLink.setTextFill(Color.SKYBLUE);
        createAccountLink.setStyle("-fx-font-family: 'Times New Roman';");

        TextFlow flow = new TextFlow(createAccountText, createAccountLink);

        Button loginButton = new Button("Zaloguj");
        loginButton.setStyle("-fx-font-family: 'Times New Roman';");

        HBox loginButtonBox = new HBox(loginButton);
        loginButtonBox.setAlignment(Pos.CENTER);

        VBox contentBox = new VBox(10);
        contentBox.getChildren().addAll(loginTitleLabel, usernameLabel, usernameField, passwordLabel, passwordField, flow);
        contentBox.setPadding(new Insets(20, 40, 20, 40));
        contentBox.setAlignment(Pos.CENTER_LEFT);
        contentBox.getChildren().add(loginButtonBox);

        StackPane cardPane = new StackPane();
        cardPane.getChildren().add(contentBox);
        cardPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-background-radius: 10;");
        cardPane.setPadding(new Insets(20));

        // Dodaj kartę do VBoxa i ustaw wypełnienie na całe okno
        this.getChildren().add(cardPane);
        VBox.setVgrow(cardPane, Priority.ALWAYS);

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            User user = userClient.loginUser(username, password);
            if (user != null) {
                mainApp.setLoggedInUser(user);
                mainApp.showRoomListView();
            } else {
                System.out.println("Błędne dane logowania");
            }
        });

        createAccountLink.setOnAction(event -> mainApp.showRegisterFormView());

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
}
