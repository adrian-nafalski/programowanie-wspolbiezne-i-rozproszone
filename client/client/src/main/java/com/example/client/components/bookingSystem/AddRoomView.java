package com.example.client.components.bookingSystem;

import com.example.client.HotelBookingFXClient;
import com.example.client.HotelBookingServiceClient;
import com.example.enums.EQUIPMENT;
import com.example.models.Room;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AddRoomView extends VBox {
    private HotelBookingServiceClient client;
    private HotelBookingFXClient mainApp;
    private Stage primaryStage;
    private Scene previousScene;

    public AddRoomView(HotelBookingServiceClient client, Stage primaryStage, Scene previousScene, HotelBookingFXClient mainApp) {
        this.client = client;
        this.primaryStage = primaryStage;
        this.previousScene = previousScene;
        this.mainApp = mainApp;
        initialize();
    }

    private void initialize() {
        Label addRoomTextLabel = new Label("Dodawanie nowego pokoju");
        addRoomTextLabel.setFont(Font.font("New Times Roman", 24));
        addRoomTextLabel.setPadding(new Insets(0, 0, 50, 0));

        Label nameLabel = new Label("Nazwa pokoju:");
        nameLabel.setStyle("-fx-font-family: 'Times New Roman';");
        TextField nameField = new TextField();
        nameField.setStyle("-fx-font-family: 'Times New Roman';");

        Label priceLabel = new Label("Cena:");
        priceLabel.setStyle("-fx-font-family: 'Times New Roman';");
        TextField priceField = new TextField();
        priceField.setStyle("-fx-font-family: 'Times New Roman';");

        Label maxPeopleLabel = new Label("Maksymalna liczba osób:");
        maxPeopleLabel.setStyle("-fx-font-family: 'Times New Roman';");
        TextField maxPeopleField = new TextField();
        maxPeopleField.setStyle("-fx-font-family: 'Times New Roman';");

        Label equipmentLabel = new Label("Wyposażenie:");
        equipmentLabel.setStyle("-fx-font-family: 'Times New Roman';");

        ComboBox<EQUIPMENT> equipmentComboBox = new ComboBox<>();
        equipmentComboBox.getItems().setAll(EQUIPMENT.values());
        equipmentComboBox.setStyle("-fx-font-family: 'Times New Roman';");

        Label descriptionLabel = new Label("Opis:");
        descriptionLabel.setStyle("-fx-font-family: 'Times New Roman';");
        TextField descriptionField = new TextField();
        descriptionField.setStyle("-fx-font-family: 'Times New Roman';");

        Label imageLabel = new Label("Zdjęcie:");
        imageLabel.setStyle("-fx-font-family: 'Times New Roman';");

        Button imageButton = new Button("Wybierz zdjęcie");
        imageButton.setStyle("-fx-font-family: 'Times New Roman';");

        final String[] imagePath = {null};

        imageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(getClass().getResource("/images/rooms").getFile()));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                imagePath[0] = "/images/rooms/" + selectedFile.getName();
                imageLabel.setText("Wybrany plik: " + imagePath[0]);
            }
        });

        Button saveButton = new Button("Zapisz");
        saveButton.setStyle("-fx-font-family: 'Times New Roman';");

        saveButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                String priceText = priceField.getText();
                String maxPeopleText = maxPeopleField.getText();
                EQUIPMENT equipment = equipmentComboBox.getValue();
                String description = descriptionField.getText();
                boolean active = true; // Pokój jest zawsze aktywny przy dodawaniu

                if (name.isEmpty() || priceText.isEmpty() || maxPeopleText.isEmpty() || equipment == null || description.isEmpty()) {
                    showAlert("Błąd", "Wszystkie pola muszą być wypełnione.");
                    return;
                }

                double price = Double.parseDouble(priceText);
                int maxPeople = Integer.parseInt(maxPeopleText);

                String selectedImagePath = imagePath[0];
                if (selectedImagePath == null) {
                    selectedImagePath = "/images/rooms/default.png";
                }

                Room newRoom = new Room(0, name, price, selectedImagePath, maxPeople, "", equipment, active, description);
                boolean success = client.addRoom(newRoom);
                if (success) {
                    primaryStage.setScene(previousScene);
                } else {
                    showAlert("Błąd", "Błąd przy dodawaniu pokoju!");
                }
            } catch (NumberFormatException ex) {
                showAlert("Błąd", "Cena i maksymalna liczba osób muszą być liczbami.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Błąd", "Wystąpił nieoczekiwany błąd.");
            }
        });

        Button backButton = new Button("Powrót");
        backButton.setStyle("-fx-font-family: 'Times New Roman';");
        backButton.setOnAction(e -> primaryStage.setScene(previousScene));

        VBox contentBox = new VBox(10);
        contentBox.getChildren().addAll(addRoomTextLabel, nameLabel, nameField, priceLabel, priceField, maxPeopleLabel, maxPeopleField, equipmentLabel, equipmentComboBox, descriptionLabel, descriptionField, imageLabel, imageButton, saveButton, backButton);
        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.CENTER_LEFT);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);

        StackPane cardPane = new StackPane(scrollPane);
        cardPane.setStyle("-fx-background-color: rgba(169, 169, 169, 0.9); -fx-background-radius: 10;");
        cardPane.setPadding(new Insets(20));

        this.getChildren().add(cardPane);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(20));
        VBox.setVgrow(cardPane, Priority.ALWAYS);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
