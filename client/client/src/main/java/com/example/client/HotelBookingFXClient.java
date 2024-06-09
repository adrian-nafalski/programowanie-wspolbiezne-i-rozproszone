package com.example.client;

import com.example.client.components.bookingSystem.homePage.RoomListView;
import com.example.client.components.login.LoginFormView;
import com.example.client.components.register.RegisterFormView;
import com.example.client.components.reservations.ReservationListView;
import com.example.models.User;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HotelBookingFXClient extends Application {
    private HotelBookingServiceClient hotelClient;
    private UserServiceClient userClient;
    private Stage primaryStage;
    private User loggedInUser;

    public HotelBookingFXClient() {
        this.hotelClient = new HotelBookingServiceClient();
        this.userClient = new UserServiceClient();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("System Rezerwacji Pokoi Hotelowych");

        showLoginFormView();

        this.primaryStage.show();
    }

    public void showLoginFormView() {
        LoginFormView loginFormView = new LoginFormView(userClient, primaryStage, this);
        Scene loginScene = new Scene(loginFormView, 800, 600);
        primaryStage.setScene(loginScene);
    }

    public void showRoomListView() {
        RoomListView roomListView = new RoomListView(hotelClient, primaryStage, this);
        Scene listScene = new Scene(roomListView, 800, 600);
        primaryStage.setScene(listScene);
    }

    public void showRegisterFormView() {
        RegisterFormView registerFormView = new RegisterFormView(userClient, primaryStage, this);
        Scene registerScene = new Scene(registerFormView, 800, 600);
        primaryStage.setScene(registerScene);
    }

    public void showReservationListView() {
        primaryStage.setTitle("Twoje rezerwacje");
        ReservationListView reservationListView = new ReservationListView(hotelClient, primaryStage, this);
        Scene scene = new Scene(reservationListView, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public User getUser() {
        return loggedInUser;
    }

    public String getLoggedInUser() {
        return loggedInUser.getLogin();
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
