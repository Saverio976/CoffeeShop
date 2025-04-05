package coffeeshop.ui;

import coffeeshop.model.MenuItem;
import coffeeshop.model.Order;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;

public class NewOrderDialogFX extends Stage {
    private Order order;
    private Map<String, MenuItem> menu;
    private Map<MenuItem, Spinner<Integer>> quantities;
    private Label totalLabel;
    private TextField customerIdField;

    public NewOrderDialogFX(Map<String, MenuItem> menu) {
        this.menu = menu;
        this.quantities = new HashMap<>();

        initModality(Modality.APPLICATION_MODAL);
        setTitle("New Order");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Customer ID
        HBox customerBox = new HBox(10);
        customerBox.setAlignment(Pos.CENTER_LEFT);
        customerBox.getChildren().add(new Label("Customer ID:"));
        customerIdField = new TextField("C" + (1000 + new Random().nextInt(9000)));
        customerBox.getChildren().add(customerIdField);

        // Menu items
        TabPane tabPane = new TabPane();
        Map<String, List<MenuItem>> categorizedMenu = new HashMap<>();

        for (MenuItem item : menu.values()) {
            categorizedMenu.computeIfAbsent(item.getCategory(), k -> new ArrayList<>()).add(item);
        }

        for (Map.Entry<String, List<MenuItem>> category : categorizedMenu.entrySet()) {
            Tab tab = new Tab(category.getKey());
            VBox categoryBox = new VBox(5);

            for (MenuItem item : category.getValue()) {
                HBox itemBox = new HBox(10);
                itemBox.setAlignment(Pos.CENTER_LEFT);

                Label nameLabel = new Label(item.getName() + " - $" + String.format("%.2f", item.getPrice()));
                Label descLabel = new Label(item.getDescription());
                Spinner<Integer> quantitySpinner = new Spinner<>(0, 10, 0);
                quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTotal());

                quantities.put(item, quantitySpinner);

                itemBox.getChildren().addAll(nameLabel, descLabel, new Label("Qty:"), quantitySpinner);
                categoryBox.getChildren().add(itemBox);
            }

            ScrollPane scrollPane = new ScrollPane(categoryBox);
            scrollPane.setFitToWidth(true);
            tab.setContent(scrollPane);
            tabPane.getTabs().add(tab);
        }

        // Total and buttons
        HBox totalBox = new HBox();
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        totalLabel = new Label("Total: $0.00");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        totalBox.getChildren().add(totalLabel);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> close());

        Button confirmButton = new Button("Place Order");
        confirmButton.setOnAction(e -> placeOrder());

        buttonBox.getChildren().addAll(cancelButton, confirmButton);

        root.getChildren().addAll(customerBox, tabPane, totalBox, buttonBox);

        Scene scene = new Scene(root, 500, 500);
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a customer ID");
            alert.showAndWait();
            return;
        }

        order = new Order(customerId);
        boolean hasItems = false;

        for (Map.Entry<MenuItem, Spinner<Integer>> entry : quantities.entrySet()) {
            int quantity = entry.getValue().getValue();
            for (int i = 0; i < quantity; i++) {
                order.addItem(entry.getKey());
                hasItems = true;
            }
        }

        if (!hasItems) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select at least one item");
            alert.showAndWait();
            return;
        }

        close();
    }

    @Override
    public void showAndWait() {
        super.showAndWait();
    }

    public Optional<Order> getOrder() {
        return Optional.ofNullable(order);
    }
}