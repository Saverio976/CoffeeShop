package coffeeshop.ui;

import coffeeshop.model.MenuItem;
import coffeeshop.model.Order;
import coffeeshop.simulation.CoffeeShopSimulation;
import coffeeshop.simulation.SimulationObserver;
import coffeeshop.simulation.StaffMember;
import coffeeshop.util.FileManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Map;
import java.util.Queue;

public class CoffeeShopFX extends Application implements SimulationObserver {
    private CoffeeShopSimulation simulation;

    // JavaFX Components
    private ListView<String> queueListView;
    private ListView<String> staffListView;
    private Button addOrderButton;
    private Button startSimulationButton;
    private Button stopSimulationButton;
    private Spinner<Integer> staffCountSpinner;
    private Label statusLabel;
    private ProgressBar simulationProgress;

    // Application theme colors
    private final String PRIMARY_COLOR = "#5D4037";
    private final String SECONDARY_COLOR = "#8D6E63";
    private final String ACCENT_COLOR = "#FF5722";
    private final String BACKGROUND_COLOR = "#EFEBE9";
    private final String TEXT_COLOR = "#3E2723";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        simulation = new CoffeeShopSimulation();
        simulation.addObserver(this);
        simulation.initialize();

        primaryStage.setTitle("Coffee Shop Simulation");

        // Create main layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        // Create header with logo and title
        HBox header = createHeader();
        root.setTop(header);

        // Create control panel
        VBox controlArea = new VBox(10);
        controlArea.setPadding(new Insets(15));
        controlArea.getChildren().addAll(createStatusBar(), createControlPanel());
        root.setBottom(controlArea);

        root.setCenter(createMainPanel());

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();

        updateDisplay();
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + PRIMARY_COLOR + ";" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 2);");

        Label titleLabel = new Label("Coffee Shop Simulation");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        // You could add a coffee cup icon here using an ImageView

        header.getChildren().add(titleLabel);
        return header;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 5;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label statusTitle = new Label("Status:");
        statusTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        statusLabel = new Label("Ready to start simulation");
        statusLabel.setFont(Font.font("Segoe UI", 12));

        simulationProgress = new ProgressBar(0);
        simulationProgress.setPrefWidth(150);
        simulationProgress.setStyle("-fx-accent: " + ACCENT_COLOR + ";");

        statusBar.getChildren().addAll(statusTitle, statusLabel, new Region(), simulationProgress);
        HBox.setHgrow(statusBar.getChildren().get(2), Priority.ALWAYS);

        return statusBar;
    }

    private VBox createControlPanel() {
        VBox controlPanelContainer = new VBox(10);
        controlPanelContainer.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 5;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label controlPanelTitle = new Label("Simulation Controls");
        controlPanelTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        controlPanelTitle.setPadding(new Insets(10, 0, 5, 10));

        HBox controlPanel = new HBox(15);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setAlignment(Pos.CENTER_LEFT);

        // Staff count controls in a styled container
        VBox staffControls = new VBox(5);
        staffControls.setAlignment(Pos.CENTER);
        staffControls.setPadding(new Insets(5));
        staffControls.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 5;");

        Label staffLabel = new Label("Staff Count");
        staffLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        HBox staffButtonsBox = new HBox(5);
        staffButtonsBox.setAlignment(Pos.CENTER);

        staffCountSpinner = new Spinner<>(1, 10, 3);
        staffCountSpinner.setPrefWidth(60);
        staffCountSpinner.setEditable(true);

        Button addStaffButton = createStyledButton("+", "-fx-background-color: " + PRIMARY_COLOR + ";");
        addStaffButton.setTooltip(new Tooltip("Add staff member"));
        addStaffButton.setOnAction(e -> {
            simulation.addStaffMember();
            staffCountSpinner.getValueFactory().setValue(simulation.getStaff().size());
        });

        Button removeStaffButton = createStyledButton("-", "-fx-background-color: " + SECONDARY_COLOR + ";");
        removeStaffButton.setTooltip(new Tooltip("Remove staff member"));
        removeStaffButton.setOnAction(e -> {
            simulation.removeStaffMember();
            staffCountSpinner.getValueFactory().setValue(simulation.getStaff().size());
        });

        staffButtonsBox.getChildren().addAll(staffCountSpinner, addStaffButton, removeStaffButton);
        staffControls.getChildren().addAll(staffLabel, staffButtonsBox);

        // Simulation speed control
        VBox speedControls = new VBox(5);
        speedControls.setAlignment(Pos.CENTER);
        speedControls.setPadding(new Insets(5));
        speedControls.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 5;");

        Label speedLabel = new Label("Simulation Speed");
        speedLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        Slider speedSlider = new Slider(0.1, 2.0, 1.0);
        speedSlider.setPrefWidth(120);
        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(true);
        speedSlider.setMajorTickUnit(0.5);
        speedSlider.setBlockIncrement(0.1);
        speedSlider.setStyle("-fx-control-inner-background: white;");

        Label currentSpeedLabel = new Label("1.0x");
        currentSpeedLabel.setFont(Font.font("Segoe UI", 12));

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double value = Math.round(newVal.doubleValue() * 10) / 10.0;
            currentSpeedLabel.setText(value + "x");
        });

        HBox speedLabelBox = new HBox(10);
        speedLabelBox.setAlignment(Pos.CENTER);
        speedLabelBox.getChildren().addAll(speedSlider, currentSpeedLabel);

        speedControls.getChildren().addAll(speedLabel, speedLabelBox);

        // Main control buttons
        addOrderButton = createStyledButton("Add New Order",
                "-fx-background-color: " + ACCENT_COLOR + ";" +
                        "-fx-font-weight: bold;");
        addOrderButton.setOnAction(e -> openNewOrderDialog());

        startSimulationButton = createStyledButton("Start Simulation",
                "-fx-background-color: #4CAF50;" + // Green
                        "-fx-font-weight: bold;");
        startSimulationButton.setOnAction(e -> startSimulation());

        stopSimulationButton = createStyledButton("Stop Simulation",
                "-fx-background-color: #F44336;" + // Red
                        "-fx-font-weight: bold;");
        stopSimulationButton.setOnAction(e -> stopSimulation());
        stopSimulationButton.setDisable(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        controlPanel.getChildren().addAll(
                staffControls, speedControls, spacer,
                addOrderButton, startSimulationButton, stopSimulationButton
        );

        controlPanelContainer.getChildren().addAll(controlPanelTitle, controlPanel);
        return controlPanelContainer;
    }

    private Button createStyledButton(String text, String additionalStyle) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 8 15 8 15;" +
                        additionalStyle
        );
        button.setOnMouseEntered(e ->
                button.setStyle(button.getStyle() + "-fx-opacity: 0.9;"));
        button.setOnMouseExited(e ->
                button.setStyle(button.getStyle().replace("-fx-opacity: 0.9;", "")));
        return button;
    }

    private SplitPane createMainPanel() {
        SplitPane mainPanel = new SplitPane();
        mainPanel.setStyle("-fx-background-color: transparent;");

        // Queue panel
        VBox queueBox = createStyledPanel("Customer Queue");
        queueListView = createStyledListView();
        queueBox.getChildren().add(queueListView);

        // Staff panel
        VBox staffBox = createStyledPanel("Staff Members");
        staffListView = createStyledListView();
        staffBox.getChildren().add(staffListView);

        mainPanel.getItems().addAll(queueBox, staffBox);
        mainPanel.setDividerPositions(0.5);

        return mainPanel;
    }

    private VBox createStyledPanel(String title) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 5;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web(TEXT_COLOR));

        panel.getChildren().add(titleLabel);
        return panel;
    }

    private ListView<String> createStyledListView() {
        ListView<String> listView = new ListView<>();
        listView.setStyle("-fx-background-radius: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: #E0E0E0;");

        // Custom cell factory for better item styling
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-padding: 10;" +
                            "-fx-border-width: 0 0 1 0;" +
                            "-fx-border-color: #E0E0E0;");

                    // Highlight online orders with accent color
                    if (item.contains("(Online)")) {
                        setStyle(getStyle() + "-fx-background-color: #FFF3E0;"); // Light orange background
                    }
                }
            }
        });

        return listView;
    }

    private void startSimulation() {
        int staffCount = staffCountSpinner.getValue();
        simulation.start(staffCount);

        startSimulationButton.setDisable(true);
        stopSimulationButton.setDisable(false);
        staffCountSpinner.setDisable(true);

        statusLabel.setText("Simulation running");
        animateProgressBar();
    }

    private void animateProgressBar() {
        simulationProgress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    }

    private void stopSimulation() {
        simulation.stop();

        startSimulationButton.setDisable(false);
        stopSimulationButton.setDisable(true);
        staffCountSpinner.setDisable(false);

        statusLabel.setText("Simulation stopped");
        simulationProgress.setProgress(0);
    }

    private void openNewOrderDialog() {
        NewOrderDialogFX dialog = new NewOrderDialogFX(simulation.getMenu());
        dialog.showAndWait();
        dialog.getOrder().ifPresent(order -> {
            simulation.addOrder(order);
            FileManager.logEvent("New order created via GUI: " + order);
            showNotification("New order added", "Order #" + order.getOrderId() + " added to queue");
        });
    }

    @Override
    public void update(CoffeeShopSimulation simulation) {
        Platform.runLater(() -> {
            updateDisplay();
            if (simulation.getOrderQueue().size() >= simulation.getMaxQueueSize()) {
                showQueueFullAlert(null); // Or pass the last rejected order if available
            }
        });
    }

    private void updateDisplay() {
        // Update queue list
        ObservableList<String> queueItems = FXCollections.observableArrayList();
        Queue<Order> orderQueue = simulation.getOrderQueue();

        if (orderQueue.isEmpty()) {
            queueItems.add("No customers in queue");
        } else {
            int position = 1;
            for (Order order : orderQueue) {
                queueItems.add(formatOrder(order, position++));
            }
        }
        queueListView.setItems(queueItems);

        // Update staff list
        ObservableList<String> staffItems = FXCollections.observableArrayList();
        for (StaffMember staff : simulation.getStaff()) {
            staffItems.add(formatStaff(staff));
        }
        staffListView.setItems(staffItems);
    }

    private String formatOrder(Order order, int position) {
        return String.format("Position %d: Order #%d%s - Customer: %s\nItems: %d - Total: $%.2f",
                position,
                order.getOrderId(),
                order.isOnline() ? " (Online)" : "",
                order.getCustomerId(),
                order.getItems().size(),
                order.getTotalAmount());
    }

    private String formatStaff(StaffMember staff) {
        Order currentOrder = staff.getCurrentOrder();
        if (currentOrder != null) {
            return String.format("%s: Processing Order #%d\nCustomer: %s - Items: %d",
                    staff.getName(), currentOrder.getOrderId(),
                    currentOrder.getCustomerId(), currentOrder.getItems().size());
        } else {
            return staff.getName() + ": Available";
        }
    }

    private void showQueueFullAlert(Order order) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Queue Full");
        alert.setHeaderText("Maximum Capacity Reached");
        alert.setContentText("The queue has reached its maximum capacity. Please try again later.");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";" +
                "-fx-border-color: " + ACCENT_COLOR + ";" +
                "-fx-border-width: 2px;");
        dialogPane.setHeaderText("Queue Full");

        alert.showAndWait();
    }

    private void showNotification(String title, String message) {
        Alert notification = new Alert(Alert.AlertType.INFORMATION);
        notification.setTitle(title);
        notification.setHeaderText(null);
        notification.setContentText(message);

        DialogPane dialogPane = notification.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";" +
                "-fx-border-color: " + PRIMARY_COLOR + ";" +
                "-fx-border-width: 2px;");

        notification.show();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                Platform.runLater(() -> notification.close());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}