package com.example.client.components.bookingSystem;

import com.example.client.HotelBookingFXClient;
import com.example.client.HotelBookingServiceClient;
import com.example.client.components.reservations.ReservationListView;
import com.example.models.Room;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class RoomDetailsView extends HBox {
    private HotelBookingServiceClient client;
    private HotelBookingFXClient mainApp;
    private Stage primaryStage;
    private Scene previousScene;
    private Room room;
    private String userName;

    public RoomDetailsView(HotelBookingServiceClient client, Stage primaryStage, Scene previousScene, Room room, String userName, HotelBookingFXClient mainApp) {
        this.client = client;
        this.primaryStage = primaryStage;
        this.previousScene = previousScene;
        this.room = room;
        this.userName = userName;
        this.mainApp = mainApp;
        initialize();
    }

    private void initialize() {
        // Left side details card
        ImageView roomImageView;
        Image roomImage = null;
        try {
            roomImage = new Image(getClass().getResourceAsStream(room.getImagePath()));
            if (roomImage.isError()) {
                throw new RuntimeException("Error loading image");
            }
        } catch (Exception e) {
            roomImage = new Image(getClass().getResourceAsStream("/images/rooms/default.png"));
            if (roomImage.isError()) {
                System.out.println("Error loading default image: " + e.getMessage());
            }
        }

        roomImageView = new ImageView(roomImage);
        roomImageView.setFitWidth(350);
        roomImageView.setFitHeight(350);
        roomImageView.setPreserveRatio(true);

        Label roomNameLabel = new Label(room.getName());
        roomNameLabel.setStyle("-fx-font-family: 'Times New Roman';");
        roomNameLabel.setFont(new Font(24));
        roomNameLabel.setTextFill(Color.WHITE);
        roomNameLabel.setPadding(new Insets(0, 0, 50, 0));

        Label roomDescriptionLabel = new Label("Opis: " + room.getDescription());
        roomDescriptionLabel.setStyle("-fx-font-family: 'Times New Roman';");
        roomDescriptionLabel.setTextFill(Color.WHITE);

        Label roomMaxPeopleLabel = new Label("Max osób: " + room.getMaxPeopleInTheRoom());
        roomMaxPeopleLabel.setStyle("-fx-font-family: 'Times New Roman';");
        roomMaxPeopleLabel.setTextFill(Color.WHITE);

        Label roomEquipmentLabel = new Label("Wyposażenie: " + room.getEquipment().toString());
        roomEquipmentLabel.setStyle("-fx-font-family: 'Times New Roman';");
        roomEquipmentLabel.setTextFill(Color.WHITE);

        VBox detailsBox = new VBox(10, roomImageView, roomNameLabel, roomDescriptionLabel, roomMaxPeopleLabel, roomEquipmentLabel);
        detailsBox.setAlignment(Pos.TOP_LEFT);
        detailsBox.setPadding(new Insets(20, 0, 10, 20));
        detailsBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-background-radius: 10;");

        ScrollPane scrollPane = new ScrollPane(detailsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPadding(new Insets(0, 10, 10, 10));

        // Right side with price and buttons
        Label roomPriceLabel = new Label("Cena: " + room.getPrice());
        roomPriceLabel.setStyle("-fx-font-family: 'Times New Roman';");
        roomPriceLabel.setFont(new Font(24));

        Button bookButton = new Button("Zarezerwuj");
        bookButton.setStyle("-fx-font-family: 'Times New Roman';");

        Stage datePickerStage = new Stage();
        datePickerStage.initModality(Modality.APPLICATION_MODAL);
        datePickerStage.setTitle("Wybierz datę rezerwacji");

        Label reservationQuestionLabel = new Label("Na kiedy chcesz zarezerwować pokój?");
        reservationQuestionLabel.setStyle("-fx-font-family: 'Times New Roman';");

        DatePicker datePicker = new DatePicker();
        datePicker.setStyle("-fx-font-family: 'Times New Roman';");

        Button confirmButton = new Button("Zatwierdź");
        confirmButton.setStyle("-fx-font-family: 'Times New Roman';");

        confirmButton.setOnAction(event -> {
            LocalDate reservationDate = datePicker.getValue();
            if (reservationDate != null) {
                boolean success = client.bookRoom(room.getId(), userName, reservationDate.toString());
                if (success) {
                    System.out.println("Pokój zarezerwowany!");
                    primaryStage.setScene(new Scene(new ReservationListView(client, primaryStage, mainApp), 800, 600));
                } else {
                    System.out.println("Rezerwacja nie powiodła się.");
                }
                datePickerStage.close();
            } else {
                System.out.println("Proszę wybrać datę rezerwacji.");
            }
        });

        VBox datePickerLayout = new VBox(reservationQuestionLabel, datePicker, confirmButton);
        datePickerLayout.setAlignment(Pos.CENTER);
        datePickerLayout.setSpacing(10);
        datePickerLayout.setPadding(new Insets(10));

        Scene datePickerScene = new Scene(datePickerLayout, 300, 200);
        datePickerStage.setScene(datePickerScene);

        bookButton.setOnAction(e -> datePickerStage.showAndWait());

        Button deleteButton = new Button("Usuń pokój");
        deleteButton.setStyle("-fx-font-family: 'Times New Roman';");

        Button updateButton = new Button("Aktualizuj pokój");
        updateButton.setStyle("-fx-font-family: 'Times New Roman';");

        deleteButton.setOnAction(e -> {
            boolean success = client.deleteRoom(room.getId());
            if (success) {
                System.out.println("Pokój usunięty!");
                primaryStage.setScene(previousScene);
            } else {
                System.out.println("Usuwanie pokoju nie powiodło się.");
            }
        });

        updateButton.setOnAction(e -> {
            RoomUpdateFormView updateFormView = new RoomUpdateFormView(client, primaryStage, previousScene, room);
            Scene updateFormScene = new Scene(updateFormView, 800, 600);
            primaryStage.setScene(updateFormScene);
        });

        Button backButton = new Button("Powrót");
        backButton.setStyle("-fx-font-family: 'Times New Roman';");
        backButton.setOnAction(e -> primaryStage.setScene(previousScene));

        VBox buttonsBox = new VBox(10, roomPriceLabel, bookButton, deleteButton, updateButton, backButton);
        buttonsBox.setAlignment(Pos.TOP_RIGHT);
        buttonsBox.setPadding(new Insets(10));

        // Add the two sections to the HBox
        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        HBox.setHgrow(buttonsBox, Priority.NEVER);
        getChildren().addAll(scrollPane, buttonsBox);
        setPadding(new Insets(10));
        setSpacing(10);
    }
}
