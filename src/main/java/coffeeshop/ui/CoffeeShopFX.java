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
import javafx.stage.Stage;

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
        root.setTop(createControlPanel());
        root.setCenter(createMainPanel());

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        updateDisplay();
    }

    private HBox createControlPanel() {
        HBox controlPanel = new HBox(10);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setStyle("-fx-border-color: #999; -fx-border-width: 0 0 1 0;");

        // Staff count spinner
        Label staffLabel = new Label("Staff Count:");
        staffCountSpinner = new Spinner<>(1, 10, 3);
        staffCountSpinner.setPrefWidth(60);

        Label speedLabel = new Label("Simulation Speed:");
        Slider speedSlider = new Slider(0.1, 2.0, 1.0);
        speedSlider.setPrefWidth(100);

        // Staff management buttons (ADD THESE)
        Button addStaffButton = new Button("+ Staff");
        addStaffButton.setOnAction(e -> simulation.addStaffMember());

        Button removeStaffButton = new Button("- Staff");
        removeStaffButton.setOnAction(e -> simulation.removeStaffMember());

        // Buttons
        addOrderButton = new Button("Add New Order");
        addOrderButton.setOnAction(e -> openNewOrderDialog());

        startSimulationButton = new Button("Start Simulation");
        startSimulationButton.setOnAction(e -> startSimulation());

        stopSimulationButton = new Button("Stop Simulation");
        stopSimulationButton.setOnAction(e -> stopSimulation());
        stopSimulationButton.setDisable(true);

        controlPanel.getChildren().addAll(
                staffLabel, staffCountSpinner,speedSlider, addStaffButton, removeStaffButton,
                addOrderButton, startSimulationButton, stopSimulationButton
        );

        return controlPanel;
    }

    private SplitPane createMainPanel() {
        SplitPane mainPanel = new SplitPane();

        // Queue panel
        VBox queueBox = new VBox();
        queueBox.setPadding(new Insets(10));
        queueBox.setSpacing(10);
        Label queueLabel = new Label("Customer Queue");
        queueLabel.setStyle("-fx-font-weight: bold;");
        queueListView = new ListView<>();
        queueBox.getChildren().addAll(queueLabel, queueListView);

        // Staff panel
        VBox staffBox = new VBox();
        staffBox.setPadding(new Insets(10));
        staffBox.setSpacing(10);
        Label staffLabel = new Label("Staff Members");
        staffLabel.setStyle("-fx-font-weight: bold;");
        staffListView = new ListView<>();
        staffBox.getChildren().addAll(staffLabel, staffListView);

        mainPanel.getItems().addAll(queueBox, staffBox);
        mainPanel.setDividerPositions(0.5);

        return mainPanel;
    }

    private void startSimulation() {
        int staffCount = staffCountSpinner.getValue();
        simulation.start(staffCount);

        startSimulationButton.setDisable(true);
        stopSimulationButton.setDisable(false);
        staffCountSpinner.setDisable(true);
    }

    private void stopSimulation() {
        simulation.stop();

        startSimulationButton.setDisable(false);
        stopSimulationButton.setDisable(true);
        staffCountSpinner.setDisable(false);
    }

    private void openNewOrderDialog() {
        NewOrderDialogFX dialog = new NewOrderDialogFX(simulation.getMenu());
        dialog.showAndWait();
        dialog.getOrder().ifPresent(order -> {
            simulation.addOrder(order);
            FileManager.logEvent("New order created via GUI: " + order);
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
        return String.format("Position %d: Order #%d - Customer: %s\nItems: %d - Total: $%.2f",
                position, order.getOrderId(), order.getCustomerId(),
                order.getItems().size(), order.getTotalAmount());
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
        alert.setHeaderText("Cannot accept order #" + order.getOrderId());
        alert.setContentText("Maximum queue capacity reached. Please try again later.");
        alert.showAndWait();
    }
}