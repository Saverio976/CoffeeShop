package coffeeshop.ui;

import coffeeshop.model.MenuItem;
import coffeeshop.model.Order;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;

public class NewOrderDialogFX extends Stage {
    private Order order;
    private Map<String, MenuItem> menu;
    private Map<MenuItem, Spinner<Integer>> quantities;
    private Label totalLabel;
    private TextField customerIdField;
    private CheckBox onlineOrderCheckBox;

    // Application theme colors
    private final String PRIMARY_COLOR = "#5D4037";
    private final String SECONDARY_COLOR = "#8D6E63";
    private final String ACCENT_COLOR = "#FF5722";
    private final String BACKGROUND_COLOR = "#EFEBE9";
    private final String TEXT_COLOR = "#3E2723";

    public NewOrderDialogFX(Map<String, MenuItem> menu) {
        this.menu = menu;
        this.quantities = new HashMap<>();

        initModality(Modality.APPLICATION_MODAL);
        setTitle("New Order");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        // Header
        Label headerLabel = new Label("Create New Order");
        headerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        headerLabel.setTextFill(Color.web(PRIMARY_COLOR));

        // Customer panel
        VBox customerPanel = new VBox(10);
        customerPanel.setPadding(new Insets(15));
        customerPanel.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 5;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label customerSectionLabel = new Label("Customer Information");
        customerSectionLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        customerSectionLabel.setTextFill(Color.web(TEXT_COLOR));

        HBox customerBox = new HBox(15);
        customerBox.setAlignment(Pos.CENTER_LEFT);

        Label customerIdLabel = new Label("Customer ID:");
        customerIdLabel.setFont(Font.font("Segoe UI", 12));
        customerIdField = new TextField("C" + (1000 + new Random().nextInt(9000)));
        customerIdField.setPrefWidth(150);
        customerIdField.setStyle("-fx-background-radius: 3;");

        onlineOrderCheckBox = new CheckBox("Online Order (Priority)");
        onlineOrderCheckBox.setFont(Font.font("Segoe UI", 12));
        onlineOrderCheckBox.setTooltip(new Tooltip("Online orders get priority in the queue"));
        onlineOrderCheckBox.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        customerBox.getChildren().addAll(customerIdLabel, customerIdField, onlineOrderCheckBox);
        customerPanel.getChildren().addAll(customerSectionLabel, customerBox);

        // Menu items
        Label menuHeaderLabel = new Label("Menu Items");
        menuHeaderLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        menuHeaderLabel.setTextFill(Color.web(TEXT_COLOR));
        menuHeaderLabel.setPadding(new Insets(10, 0, 0, 0));

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 5;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Map<String, List<MenuItem>> categorizedMenu = new HashMap<>();
        for (MenuItem item : menu.values()) {
            categorizedMenu.computeIfAbsent(item.getCategory(), k -> new ArrayList<>()).add(item);
        }

        for (Map.Entry<String, List<MenuItem>> category : categorizedMenu.entrySet()) {
            Tab tab = new Tab(category.getKey());
            tab.setStyle("-fx-background-color: " + SECONDARY_COLOR + ";" +
                    "-fx-text-fill: white;");

            // Grid layout for items in each category
            GridPane itemGrid = new GridPane();
            itemGrid.setHgap(20);
            itemGrid.setVgap(15);
            itemGrid.setPadding(new Insets(15));

            int row = 0;
            for (MenuItem item : category.getValue()) {
                VBox itemCard = new VBox(5);
                itemCard.setPadding(new Insets(10));
                itemCard.setStyle("-fx-background-color: #F8F8F8;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-color: #E0E0E0;" +
                        "-fx-border-radius: 5;");
                itemCard.setPrefWidth(200);

                Label nameLabel = new Label(item.getName());
                nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                nameLabel.setTextFill(Color.web(PRIMARY_COLOR));

                Label priceLabel = new Label("$" + String.format("%.2f", item.getPrice()));
                priceLabel.setFont(Font.font("Segoe UI", 14));
                priceLabel.setTextFill(Color.web(ACCENT_COLOR));

                Label descLabel = new Label(item.getDescription());
                descLabel.setFont(Font.font("Segoe UI", 12));
                descLabel.setWrapText(true);
                descLabel.setMaxWidth(180);

                HBox quantityBox = new HBox(5);
                quantityBox.setAlignment(Pos.CENTER_LEFT);
                Label qtyLabel = new Label("Qty:");
                qtyLabel.setFont(Font.font("Segoe UI", 12));

                Spinner<Integer> quantitySpinner = new Spinner<>(0, 10, 0);
                quantitySpinner.setEditable(true);
                quantitySpinner.setPrefWidth(70);
                quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTotal());
                quantities.put(item, quantitySpinner);

                quantityBox.getChildren().addAll(qtyLabel, quantitySpinner);

                // Stack layout for the name and price
                HBox nameAndPrice = new HBox();
                nameAndPrice.setAlignment(Pos.CENTER_LEFT);
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                nameAndPrice.getChildren().addAll(nameLabel, spacer, priceLabel);

                itemCard.getChildren().addAll(nameAndPrice, descLabel, quantityBox);

                // Add to grid layout - 2 columns
                itemGrid.add(itemCard, row % 2, row / 2);
                row++;
            }

            ScrollPane scrollPane = new ScrollPane(itemGrid);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background: white;");

            tab.setContent(scrollPane);
            tabPane.getTabs().add(tab);
        }

        // Order summary panel
        VBox summaryPanel = new VBox(10);
        summaryPanel.setPadding(new Insets(15));
        summaryPanel.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 5;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label summaryLabel = new Label("Order Summary");
        summaryLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        totalLabel = new Label("Total: $0.00");
        totalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        totalLabel.setTextFill(Color.web(ACCENT_COLOR));

        summaryPanel.getChildren().addAll(summaryLabel, totalLabel);

        // Button panel
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #9E9E9E;" + // Gray
                "-fx-text-fill: white;" +
                "-fx-background-radius: 4;" +
                "-fx-padding: 10 20 10 20;");
        cancelButton.setOnAction(e -> close());

        Button confirmButton = new Button("Place Order");
        confirmButton.setStyle("-fx-background-color: " + ACCENT_COLOR + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 4;" +
                "-fx-padding: 10 20 10 20;" +
                "-fx-font-weight: bold;");
        confirmButton.setOnAction(e -> placeOrder());

        buttonBox.getChildren().addAll(cancelButton, confirmButton);

        root.getChildren().addAll(headerLabel, customerPanel, menuHeaderLabel, tabPane, summaryPanel, buttonBox);

        Scene scene = new Scene(root, 600, 700);
        setScene(scene);
    }

    private void updateTotal() {
        double total = 0.0;
        for (Map.Entry<MenuItem, Spinner<Integer>> entry : quantities.entrySet()) {
            int quantity = entry.getValue().getValue();
            total += entry.getKey().getPrice() * quantity;
        }

        totalLabel.setText("Total: $" + String.format("%.2f", total));
    }

    private void placeOrder() {
        String customerId = customerIdField.getText().trim();
        if (customerId.isEmpty()) {
            showErrorAlert("Please enter a customer ID");
            return;
        }

        // Create order with online flag
        boolean isOnline = onlineOrderCheckBox.isSelected();
        order = new Order(customerId, isOnline);
        boolean hasItems = false;

        for (Map.Entry<MenuItem, Spinner<Integer>> entry : quantities.entrySet()) {
            int quantity = entry.getValue().getValue();
            for (int i = 0; i < quantity; i++) {
                order.addItem(entry.getKey());
                hasItems = true;
            }
        }

        if (!hasItems) {
            showErrorAlert("Please select at least one item");
            return;
        }

        close();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";" +
                "-fx-border-color: #F44336;" +
                "-fx-border-width: 2px;");

        alert.showAndWait();
    }

    @Override
    public void showAndWait() {
        super.showAndWait();
    }

    public Optional<Order> getOrder() {
        return Optional.ofNullable(order);
    }
}