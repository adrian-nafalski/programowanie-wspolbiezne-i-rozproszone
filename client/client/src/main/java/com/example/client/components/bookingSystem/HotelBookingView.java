package com.example.client.components.bookingSystem;

import com.example.client.HotelBookingFXClient;
import com.example.client.HotelBookingServiceClient;
import com.example.models.Room;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class HotelBookingView extends VBox {
    private HotelBookingServiceClient client;
    private HotelBookingFXClient mainApp;
    private Stage primaryStage;
    private Scene previousScene;
    private String userName;

    public HotelBookingView(HotelBookingServiceClient client, Stage primaryStage, Scene previousScene, String userName, HotelBookingFXClient mainApp) {
        this.client = client;
        this.primaryStage = primaryStage;
        this.previousScene = previousScene;
        this.userName = userName;
        this.mainApp = mainApp;
        initialize();
    }

    private void initialize() {
        ListView<Room> roomListView = new ListView<>();
        List<Room> availableRooms = client.getAvailableRooms();
        roomListView.getItems().addAll(availableRooms);

        Button bookButton = new Button("Zarezerwuj");
        bookButton.setOnAction(e -> {
            Room selectedRoom = roomListView.getSelectionModel().getSelectedItem();
            if (selectedRoom != null) {
                RoomDetailsView roomDetailsView = new RoomDetailsView(client, primaryStage, primaryStage.getScene(), selectedRoom, userName, mainApp);
                Scene roomDetailsScene = new Scene(roomDetailsView, 800, 600);
                primaryStage.setScene(roomDetailsScene);
            }
        });

        Button backButton = new Button("Powrót");
        backButton.setOnAction(e -> primaryStage.setScene(previousScene));

        setPadding(new Insets(10));
        setSpacing(10);
        getChildren().addAll(roomListView, bookButton, backButton);
    }
}
