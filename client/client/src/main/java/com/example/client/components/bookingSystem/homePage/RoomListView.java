package com.example.client.components.bookingSystem.homePage;

import com.example.client.HotelBookingFXClient;
import com.example.client.HotelBookingServiceClient;
import com.example.client.components.bookingSystem.AddRoomView;
import com.example.client.components.bookingSystem.RoomDetailsView;
import com.example.enums.EQUIPMENT;
import com.example.models.Room;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class RoomListView extends VBox {
    private HotelBookingServiceClient client;
    private Stage primaryStage;
    private ListView<Room> roomListView;
    private HotelBookingFXClient mainApp;
    private ThreadPoolExecutor executor;

    private TextField nameFilterField;
    private TextField minPriceFilterField;
    private TextField maxPriceFilterField;
    private TextField maxPeopleFilterField;
    private ComboBox<EQUIPMENT> equipmentFilterComboBox;

    public RoomListView(HotelBookingServiceClient client, Stage primaryStage, HotelBookingFXClient mainApp) {
        this.client = client;
        this.primaryStage = primaryStage;
        this.mainApp = mainApp;
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        initialize();
    }

    private void initialize() {
        Label welcomeLabel = new Label("Witaj " + mainApp.getLoggedInUser() + "!");
        welcomeLabel.setTextFill(Color.WHITE);
        welcomeLabel.setFont(Font.font("New Times Roman", 15));
        HBox.setMargin(welcomeLabel, new Insets(0, 20, 0, 0));
        StackPane welcomePane = new StackPane(welcomeLabel);

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Menu");
        menuBar.setStyle("-fx-font-family: 'Times New Roman';");

        MenuItem homeItem = new MenuItem("Strona główna");
        MenuItem reservationsItem = new MenuItem("Twoje rezerwacje");
        reservationsItem.setOnAction(event -> mainApp.showReservationListView());
        MenuItem logoutItem = new MenuItem("Wyloguj");
        logoutItem.setOnAction(event -> showLogoutConfirmation());

        menu.getItems().addAll(homeItem, reservationsItem, logoutItem);
        menuBar.getMenus().add(menu);

        HBox welcomeBox = new HBox();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        welcomeBox.getChildren().addAll(spacer, welcomeLabel, menuBar);

        welcomePane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        welcomePane.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        welcomePane.setPadding(new Insets(10));
        welcomePane.setAlignment(Pos.CENTER_RIGHT);
        welcomePane.getChildren().add(welcomeBox);

        HBox filtersBox = new HBox(10);
        filtersBox.setAlignment(Pos.CENTER_LEFT);
        filtersBox.setPadding(new Insets(10));

        nameFilterField = new TextField();
        nameFilterField.setPromptText("Nazwa pokoju");
        nameFilterField.setStyle("-fx-font-family: 'Times New Roman';");

        minPriceFilterField = new TextField();
        minPriceFilterField.setPromptText("Min. cena");
        minPriceFilterField.setStyle("-fx-font-family: 'Times New Roman';");

        maxPriceFilterField = new TextField();
        maxPriceFilterField.setPromptText("Max. cena");
        maxPriceFilterField.setStyle("-fx-font-family: 'Times New Roman';");

        maxPeopleFilterField = new TextField();
        maxPeopleFilterField.setPromptText("Max osób");
        maxPeopleFilterField.setStyle("-fx-font-family: 'Times New Roman';");

        equipmentFilterComboBox = new ComboBox<>();
        equipmentFilterComboBox.getItems().addAll(EQUIPMENT.values());
        equipmentFilterComboBox.setPromptText("Wyposażenie");
        equipmentFilterComboBox.setStyle("-fx-font-family: 'Times New Roman';");

        equipmentFilterComboBox.getItems().add(0, null);
        equipmentFilterComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(EQUIPMENT equipment) {
                return equipment == null ? "Brak" : equipment.toString();
            }

            @Override
            public EQUIPMENT fromString(String string) {
                return null;
            }
        });

        Button applyFiltersButton = new Button("Zastosuj filtry");
        applyFiltersButton.setStyle("-fx-font-family: 'Times New Roman';");

        Button clearFiltersButton = new Button("Cofnij filtry");
        clearFiltersButton.setStyle("-fx-font-family: 'Times New Roman';");

        applyFiltersButton.setOnAction(e -> applyFilters());

        clearFiltersButton.setOnAction(e -> {
            nameFilterField.clear();
            minPriceFilterField.clear();
            maxPriceFilterField.clear();
            maxPeopleFilterField.clear();
            equipmentFilterComboBox.getSelectionModel().clearSelection();
            refreshRooms();
        });

        Label filtersLabel = new Label("Filtry:");
        filtersLabel.setTextFill(Color.WHITE);
        filtersLabel.setStyle("-fx-font-family: 'Times New Roman';");

        filtersBox.getChildren().addAll(
                filtersLabel,
                nameFilterField,
                minPriceFilterField,
                maxPriceFilterField,
                maxPeopleFilterField,
                equipmentFilterComboBox,
                applyFiltersButton,
                clearFiltersButton
        );

        Label roomLabel = new Label("Dostępne pokoje:");
        roomLabel.setTextFill(Color.WHITE);
        roomLabel.setFont(Font.font("New Times Roman"));

        roomListView = new ListView<>();
        setupRoomListView();

        Button refreshButton = new Button("Odśwież");
        refreshButton.setStyle("-fx-font-family: 'Times New Roman';");
        refreshButton.setOnAction(e -> refreshRooms());

        Button addRoomButton = new Button("Dodaj pokój");
        addRoomButton.setStyle("-fx-font-family: 'Times New Roman';");
        addRoomButton.setOnAction(e -> showAddRoomScene());

        this.setPadding(new Insets(20));
        this.setSpacing(10);

        this.setBackground(new Background(new BackgroundImage(
                new Image(getClass().getResourceAsStream("/images/theme.jpeg")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        )));

        refreshRooms();

        this.getChildren().addAll(welcomePane, filtersBox, roomLabel, roomListView, refreshButton, addRoomButton);
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

    private void setupRoomListView() {
        roomListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Room> call(ListView<Room> param) {
                return new ListCell<>() {
                    private ImageView imageView = new ImageView();
                    private Label nameLabel = new Label();
                    private Label priceLabel = new Label();
                    private Button checkTheRoomButton = new Button("Sprawdź");
                    private HBox hbox = new HBox(10);

                    {
                        setupRoomCellLayout();
                    }

                    private void setupRoomCellLayout() {
                        imageView.setFitHeight(125);
                        imageView.setFitWidth(125);
                        nameLabel.setMaxWidth(Double.MAX_VALUE);
                        HBox.setHgrow(nameLabel, Priority.ALWAYS);
                        priceLabel.setAlignment(Pos.CENTER_RIGHT);
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        hbox.getChildren().addAll(imageView, nameLabel, priceLabel, checkTheRoomButton);
                        hbox.setSpacing(10);

                        checkTheRoomButton.setStyle("-fx-font-family: 'Times New Roman';");

                        checkTheRoomButton.setOnAction(event -> {
                            Room selectedRoom = getItem();
                            if (selectedRoom != null) {
                                showRoomDetails(selectedRoom);
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
                            nameLabel.setText(item.getName());
                            nameLabel.setStyle("-fx-font-family: 'Times New Roman';");

                            priceLabel.setText(item.getPrice() + " zł");
                            priceLabel.setStyle("-fx-font-family: 'Times New Roman';");

                            updateRoomImage(item);
                            setGraphic(hbox);
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

        roomListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Room selectedRoom = roomListView.getSelectionModel().getSelectedItem();
                if (selectedRoom != null) {
                    showRoomDetails(selectedRoom);
                }
            }
        });
    }

    private void refreshRooms() {
        executor.execute(() -> {
            if (client != null) {
                List<Room> availableRooms = client.getAvailableRooms();
                if (availableRooms != null) {
                    Platform.runLater(() -> roomListView.getItems().setAll(availableRooms));
                } else {
                    System.out.println("Błąd przy pobieraniu dostępnych pokoi!");
                }
            } else {
                System.out.println("Błąd: brak obiektu klienta (client)");
            }
        });
    }

    private void applyFilters() {
        executor.execute(() -> {
            String nameFilter = nameFilterField.getText().toLowerCase();
            String minPriceText = minPriceFilterField.getText();
            String maxPriceText = maxPriceFilterField.getText();
            String maxPeopleText = maxPeopleFilterField.getText();
            EQUIPMENT equipmentFilter = equipmentFilterComboBox.getValue();

            double minPrice = minPriceText.isEmpty() ? 0 : Double.parseDouble(minPriceText);
            double maxPrice = maxPriceText.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceText);
            int maxPeople = maxPeopleText.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(maxPeopleText);

            List<Room> filteredRooms = client.getAvailableRooms().stream()
                    .filter(room -> room.getName().toLowerCase().contains(nameFilter))
                    .filter(room -> room.getPrice() >= minPrice && room.getPrice() <= maxPrice)
                    .filter(room -> room.getMaxPeopleInTheRoom() <= maxPeople)
                    .filter(room -> equipmentFilter == null || room.getEquipment() == equipmentFilter)
                    .collect(Collectors.toList());

            Platform.runLater(() -> roomListView.getItems().setAll(filteredRooms));
        });
    }

    private void showRoomDetails(Room room) {
        RoomDetailsView roomDetailsView = new RoomDetailsView(client, primaryStage, primaryStage.getScene(), room, mainApp.getLoggedInUser(), mainApp);
        Scene detailScene = new Scene(roomDetailsView, 800, 600);
        primaryStage.setScene(detailScene);
    }

    private void showAddRoomScene() {
        AddRoomView addRoomView = new AddRoomView(client, primaryStage, primaryStage.getScene(), mainApp);
        Scene addRoomScene = new Scene(addRoomView, 800, 600);
        primaryStage.setScene(addRoomScene);
    }
}
