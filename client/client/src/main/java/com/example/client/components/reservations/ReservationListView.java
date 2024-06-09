package com.example.client.components.reservations;

import com.example.client.HotelBookingFXClient;
import com.example.client.HotelBookingServiceClient;
import com.example.models.Room;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ReservationListView extends VBox {
    private HotelBookingServiceClient client;
    private Stage primaryStage;
    private ListView<Room> reservationListView;
    private HotelBookingFXClient mainApp;
    private FilteredList<Room> filteredData;
    private SortedList<Room> sortedData;
    private ComboBox<String> sortComboBox;
    private TextField filterTextField;
    private ThreadPoolExecutor executor;

    public ReservationListView(HotelBookingServiceClient client, Stage primaryStage, HotelBookingFXClient mainApp) {
        this.client = client;
        this.primaryStage = primaryStage;
        this.mainApp = mainApp;
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        initialize();
    }

    private void initialize() {
        StackPane reservationPane = new StackPane();
        Label reservationLabel = new Label("Twoje rezerwacje");
        reservationLabel.setTextFill(Color.WHITE);
        reservationLabel.setFont(Font.font("New Times Roman", 15));

        HBox.setMargin(reservationLabel, new Insets(0, 20, 0, 0));

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Menu");
        menuBar.setStyle("-fx-font-family: 'Times New Roman';");

        MenuItem homeItem = new MenuItem("Strona główna");
        homeItem.setOnAction(event -> mainApp.showRoomListView());
        MenuItem logoutItem = new MenuItem("Wyloguj");
        logoutItem.setOnAction(event -> showLogoutConfirmation());

        menu.getItems().addAll(homeItem, logoutItem);
        menuBar.getMenus().add(menu);

        HBox reservationBox = new HBox();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        reservationBox.getChildren().addAll(spacer, reservationLabel, menuBar);

        reservationPane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        reservationPane.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        reservationPane.setPadding(new Insets(10));
        reservationPane.setAlignment(Pos.CENTER_RIGHT);
        reservationPane.getChildren().add(reservationBox);

        Label reservationListLabel = new Label("Lista Twoich rezerwacji:");
        reservationListLabel.setFont(Font.font("New Times Roman"));
        reservationListLabel.setTextFill(Color.WHITE);

        reservationListView = new ListView<>();
        setupReservationListView();

        sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll("Nazwa", "Cena");
        sortComboBox.setValue("Nazwa");
        sortComboBox.setStyle("-fx-font-family: 'Times New Roman';");
        sortComboBox.setOnAction(e -> applySortAndFilter());

        filterTextField = new TextField();
        filterTextField.setPromptText("Filtruj rezerwacje");
        filterTextField.setStyle("-fx-font-family: 'Times New Roman';");
        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> applySortAndFilter());

        Button refreshButton = new Button("Odśwież");
        refreshButton.setStyle("-fx-font-family: 'Times New Roman';");
        refreshButton.setOnAction(e -> refreshReservations());

        Label sortLabel = new Label("Sortuj wg:");
        sortLabel.setTextFill(Color.WHITE);
        sortLabel.setFont(Font.font("New Times Roman"));

        Label filterLabel = new Label("Filtruj po nazwie:");
        filterLabel.setTextFill(Color.WHITE);
        filterLabel.setFont(Font.font("New Times Roman"));

        HBox filterBox = new HBox(10, sortLabel, sortComboBox, filterLabel, filterTextField);
        filterBox.setPadding(new Insets(10));
        filterBox.setStyle("-fx-font-family: 'Times New Roman';");

        this.setPadding(new Insets(20));
        this.setSpacing(10);

        this.setBackground(new Background(new BackgroundImage(
                new Image(getClass().getResourceAsStream("/images/theme.jpeg")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        )));

        this.getChildren().addAll(reservationPane, reservationListLabel, filterBox, reservationListView, refreshButton);
        refreshReservations();
    }

    private void showLogoutConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Wylogowanie");
        alert.setHeaderText("Czy na pewno chcesz się wylogować?");
        alert.setContentText("Kliknij Tak, aby potwierdzić lub Nie, aby anulować.");

        ButtonType buttonTypeYes = new ButtonType("Tak");
        ButtonType buttonTypeNo = new ButtonType("Nie", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeYes) {
                mainApp.setLoggedInUser(null);
                mainApp.showLoginFormView();
            }
        });
    }

    private void setupReservationListView() {
        reservationListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Room> call(ListView<Room> listView) {
                return new ListCell<>() {
                    private ImageView imageView = new ImageView();
                    private Label roomInfo = new Label();
                    private Button deleteButton = new Button("Usuń");
                    private HBox hBox = new HBox(10);

                    {
                        setupReservationCellLayout();
                    }

                    private void setupReservationCellLayout() {
                        imageView.setFitHeight(50);
                        imageView.setFitWidth(50);
                        roomInfo.setMaxWidth(Double.MAX_VALUE);
                        HBox.setHgrow(roomInfo, Priority.ALWAYS);
                        hBox.setAlignment(Pos.CENTER_LEFT);
                        hBox.getChildren().addAll(imageView, roomInfo, deleteButton);
                        hBox.setSpacing(10);

                        deleteButton.setStyle("-fx-font-family: 'Times New Roman';");

                        deleteButton.setOnAction(event -> {
                            Room selectedRoom = getItem();
                            if (selectedRoom != null) {
                                deleteReservation(selectedRoom);
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Room item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            roomInfo.setText(item.getName() + " / " + item.getReservationDate() + " / " + item.getPrice() + " zł");
                            roomInfo.setStyle("-fx-font-family: 'Times New Roman';");

                            updateRoomImage(item);
                            setGraphic(hBox);
                        }
                    }

                    private void updateRoomImage(Room item) {
                        executor.execute(() -> {
                            try {
                                Image image = new Image(getClass().getResourceAsStream(item.getImagePath()));
                                Platform.runLater(() -> imageView.setImage(image));
                            } catch (NullPointerException e) {
                                System.out.println("Nie znaleziono zdjęcia: " + item.getImagePath());
                            }
                        });
                    }
                };
            }
        });
    }

    private void refreshReservations() {
        executor.execute(() -> {
            try {
                if (mainApp.getLoggedInUser() != null) {
                    List<Room> reservations = client.getUserReservations(mainApp.getUser().getId());
                    Platform.runLater(() -> {
                        filteredData = new FilteredList<>(FXCollections.observableArrayList(reservations), p -> true);
                        sortedData = new SortedList<>(filteredData);
                        reservationListView.setItems(sortedData);
                        applySortAndFilter();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showError("Błąd przy pobieraniu rezerwacji."));
            }
        });
    }

    private void applySortAndFilter() {
        String filterText = filterTextField.getText().toLowerCase();
        filteredData.setPredicate(room -> {
            if (filterText == null || filterText.isEmpty()) {
                return true;
            }
            return room.getName().toLowerCase().contains(filterText);
        });

        String selectedSort = sortComboBox.getValue();
        sortedData.setComparator((room1, room2) -> {
            if (selectedSort.equals("Nazwa")) {
                return room1.getName().compareTo(room2.getName());
            } else {
                return Double.compare(room1.getPrice(), room2.getPrice());
            }
        });
    }

    private void deleteReservation(Room room) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuń rezerwację");
        alert.setHeaderText("Czy na pewno chcesz usunąć tę rezerwację?");
        alert.setContentText("Kliknij Tak, aby potwierdzić lub Nie, aby anulować.");

        ButtonType buttonTypeYes = new ButtonType("Tak");
        ButtonType buttonTypeNo = new ButtonType("Nie", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeYes) {
                executor.execute(() -> {
                    try {
                        boolean success = client.deleteReservation(mainApp.getUser().getId(), room.getId());
                        Platform.runLater(() -> {
                            if (success) {
                                refreshReservations();
                            } else {
                                showError("Nie udało się usunąć rezerwacji.");
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> showError("Wystąpił błąd podczas usuwania rezerwacji."));
                    }
                });
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
