package com.hwjustjava.app;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Hello world!
 */
public class App extends Application {
    private CoffeeManager coffeeManager;
    private Stage primaryStage;
    private Scene landingScene;
    private Scene orderScene;
    private Scene checkoutScene;

    private String currentCustomerId = "";
    private List<String> currentCart = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        coffeeManager = CoffeeManager.GetInstance();

        try {
            Menu menu = new Menu("menu.csv");
            CoffeeManager.GetInstance().Reset();
            coffeeManager = CoffeeManager.GetInstance();
        } catch (InvalidMenuCsvException e) {
            showAlert("Error", "Failed to load menu: " + e.getMessage());
        }

        createLandingScene();

        primaryStage.setTitle("Coffee Shop Simulation");
        primaryStage.setScene(landingScene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            Statistics stats = new Statistics();
            stats.PrintStatistics();

            StringBuilder report = new StringBuilder("Coffee Shop Simulation Report\n\n");
            report.append("Number of customers: ").append(coffeeManager.GetCustomerManager().GetCustomersCount()).append("\n");
            report.append("Number of orders: ").append(coffeeManager.GetFrontDesk().GetCompletedCustomerOrders().size()).append("\n");
            report.append("Total income: $").append(String.format("%.2f", coffeeManager.GetInvoiceManager().GetTotalIncome())).append("\n");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Final Report");
            alert.setHeaderText("Coffee Shop Simulation Report");
            alert.setContentText(report.toString());
            alert.showAndWait();
        });
    }

    private void createLandingScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome to the Coffee Shop!");
        welcomeLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Label instructionLabel = new Label("Please enter your name to continue:");

        TextField customerIdField = new TextField();
        customerIdField.setPromptText("Enter your name");
        customerIdField.setMaxWidth(300);

        Button continueButton = new Button("Continue to Order");
        continueButton.setOnAction(e -> {
            String customerId = customerIdField.getText().trim();
            if (customerId.isEmpty()) {
                showAlert("Error", "Please enter your name");
                return;
            }

            try {
                currentCustomerId = coffeeManager.GetCustomerManager().CreateRecordCustomer(customerId);

                createOrderScene();
                primaryStage.setScene(orderScene);
            } catch (InvalidCustomerException ex) {
                showAlert("Error", "Invalid customer name: " + ex.getMessage());
            }
        });

        Label queueLabel = new Label("Current Order Queue");
        queueLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        ListView<String> orderQueueListView = new ListView<>();
        orderQueueListView.setPrefHeight(200);

        List<Customer> completedOrders = coffeeManager.GetFrontDesk().GetCompletedCustomerOrders();
        ObservableList<String> orderItems = FXCollections.observableArrayList();
        for (Customer customerOrder : completedOrders) {
            for (Order order : customerOrder.GetOrders()) {
                try {
                    IItem item = coffeeManager.GetMenu().GetItem(order.GetItemID());
                    orderItems.add(order.GetCustomerID() + " - " + item.GetDescription());
                } catch (UnknownItemException ex) {
                }
            }
        }
        orderQueueListView.setItems(orderItems);

        root.getChildren().addAll(
                welcomeLabel,
                instructionLabel,
                customerIdField,
                continueButton,
                new Separator(),
                queueLabel,
                orderQueueListView
        );

        landingScene = new Scene(root);
    }

    private void createOrderScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        VBox topSection = new VBox(10);
        topSection.setAlignment(Pos.CENTER);

        Label headerLabel = new Label("Place Your Order");
        headerLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Label customerLabel = new Label("Customer: " + currentCustomerId);
        customerLabel.setStyle("-fx-font-size: 16;");

        topSection.getChildren().addAll(headerLabel, customerLabel);
        root.setTop(topSection);

        TabPane menuTabPane = new TabPane();
        menuTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab hotBeveragesTab = createCategoryTab("Hot Beverages", "Beverage.HotBeverage");
        Tab coldBeveragesTab = createCategoryTab("Cold Beverages", "Beverage.ColdBeverage");
        Tab foodTab = createCategoryTab("Food", "Food");
        Tab merchandiseTab = createCategoryTab("Merchandise", "Merchandise");

        menuTabPane.getTabs().addAll(hotBeveragesTab, coldBeveragesTab, foodTab, merchandiseTab);
        root.setCenter(menuTabPane);

        VBox cartSection = new VBox(10);
        cartSection.setPadding(new Insets(0, 0, 0, 20));
        cartSection.setPrefWidth(250);

        Label cartLabel = new Label("Your Cart");
        cartLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        ListView<String> cartListView = new ListView<>();
        cartListView.setPrefHeight(300);

        Button removeButton = new Button("Remove Selected Item");
        removeButton.setDisable(true);

        cartListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            removeButton.setDisable(newVal == null);
        });

        removeButton.setOnAction(e -> {
            int selectedIndex = cartListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                currentCart.remove(selectedIndex);
                updateCartView(cartListView);
            }
        });

        Label totalLabel = new Label("Total: $0.00");
        totalLabel.setStyle("-fx-font-size: 16;");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button checkoutButton = new Button("Proceed to Checkout");
        checkoutButton.setDisable(true);

        Button clearCartButton = new Button("Clear Cart");
        clearCartButton.setDisable(true);

        checkoutButton.setOnAction(e -> {
            if (!currentCart.isEmpty()) {
                createCheckoutScene();
                primaryStage.setScene(checkoutScene);
            }
        });

        clearCartButton.setOnAction(e -> {
            currentCart.clear();
            updateCartView(cartListView);
            checkoutButton.setDisable(true);
            clearCartButton.setDisable(true);
            totalLabel.setText("Total: $0.00");
        });

        buttonBox.getChildren().addAll(checkoutButton, clearCartButton);

        cartSection.getChildren().addAll(cartLabel, cartListView, removeButton, totalLabel, buttonBox);
        root.setRight(cartSection);

        Button backButton = new Button("Back to Welcome Screen");
        backButton.setOnAction(e -> {
            currentCart.clear();
            primaryStage.setScene(landingScene);
        });

        root.setBottom(backButton);
        BorderPane.setAlignment(backButton, Pos.CENTER);
        BorderPane.setMargin(backButton, new Insets(20, 0, 0, 0));

        currentCart.clear();

        ObservableList<String> cartItems = FXCollections.observableArrayList();
        cartListView.setItems(cartItems);

        updateCartView(cartListView);

        orderScene = new Scene(root);
    }

    private void updateCartView(ListView<String> cartListView) {
        if (cartListView == null) return;

        ObservableList<String> cartItems = cartListView.getItems();
        cartItems.clear();
        double total = 0.0;

        for (String itemId : currentCart) {
            try {
                IItem item = coffeeManager.GetMenu().GetItem(itemId);
                cartItems.add(item.GetDescription() + " - $" + String.format("%.2f", item.GetCost()));
                total += item.GetCost();
            } catch (UnknownItemException ex) {
            }
        }

        if (cartListView.getParent() instanceof VBox) {
            VBox parent = (VBox) cartListView.getParent();
            for (javafx.scene.Node node : parent.getChildren()) {
                if (node instanceof Label && ((Label) node).getText().startsWith("Total:")) {
                    ((Label) node).setText("Total: $" + String.format("%.2f", total));
                    break;
                }
            }
        }

        if (cartListView.getParent() instanceof VBox) {
            VBox parent = (VBox) cartListView.getParent();
            for (javafx.scene.Node node : parent.getChildren()) {
                if (node instanceof HBox) {
                    HBox buttonBox = (HBox) node;
                    for (javafx.scene.Node buttonNode : buttonBox.getChildren()) {
                        if (buttonNode instanceof Button) {
                            Button button = (Button) buttonNode;
                            if (button.getText().contains("Checkout") || button.getText().contains("Clear")) {
                                button.setDisable(currentCart.isEmpty());
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private Tab createCategoryTab(String title, String category) {
        Tab tab = new Tab(title);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(20);
        grid.setVgap(20);

        List<IItem> allItems = coffeeManager.GetMenu().GetItems();
        List<IItem> categoryItems = allItems.stream()
                .filter(item -> item.GetCategory().equals(category))
                .collect(Collectors.toList());

        int row = 0;
        int col = 0;

        for (IItem item : categoryItems) {
            VBox itemBox = new VBox(5);
            itemBox.setPadding(new Insets(10));
            itemBox.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5;");
            itemBox.setPrefWidth(200);

            Label nameLabel = new Label(item.GetDescription());
            nameLabel.setStyle("-fx-font-weight: bold;");

            Label priceLabel = new Label("$" + String.format("%.2f", item.GetCost()));

            Button addButton = new Button("Add to Cart");
            addButton.setOnAction(e -> {
                currentCart.add(item.GetID());
                for (Tab t : ((TabPane) tab.getTabPane()).getTabs()) {
                    if (t.getText().equals(title)) {
                        Scene scene = tab.getTabPane().getScene();
                        if (scene != null) {
                            ListView<String> cartListView = null;
                            BorderPane root = (BorderPane) scene.getRoot();
                            if (root.getRight() instanceof VBox) {
                                VBox rightSection = (VBox) root.getRight();
                                for (javafx.scene.Node node : rightSection.getChildren()) {
                                    if (node instanceof ListView) {
                                        cartListView = (ListView<String>) node;
                                        break;
                                    }
                                }
                            }
                            if (cartListView != null) {
                                updateCartView(cartListView);
                            }
                        }
                        break;
                    }
                }
            });

            itemBox.getChildren().addAll(nameLabel, priceLabel, addButton);

            grid.add(itemBox, col, row);

            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);

        tab.setContent(scrollPane);
        return tab;
    }

    private void createCheckoutScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label headerLabel = new Label("Checkout");
        headerLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Label customerLabel = new Label("Customer: " + currentCustomerId);
        customerLabel.setStyle("-fx-font-size: 16;");

        // Order summary
        VBox orderSummary = new VBox(10);
        orderSummary.setPadding(new Insets(20));
        orderSummary.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5;");
        orderSummary.setMaxWidth(500);

        Label summaryLabel = new Label("Order Summary");
        summaryLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        ListView<String> itemsListView = new ListView<>();
        itemsListView.setPrefHeight(200);

        ObservableList<String> items = FXCollections.observableArrayList();
        double subtotal = 0.0;

        for (String itemId : currentCart) {
            try {
                IItem item = coffeeManager.GetMenu().GetItem(itemId);
                items.add(item.GetDescription() + " - $" + String.format("%.2f", item.GetCost()));
                subtotal += item.GetCost();
            } catch (UnknownItemException ex) {
            }
        }

        itemsListView.setItems(items);

        float discount = 0;
        try {
            discount = coffeeManager.GetFrontDesk().CalculateDiscount(currentCart);
        } catch (InvalidOrderException e) {
            showAlert("Error", "Error calculating discount: " + e.getMessage());
        }

        double discountAmount = subtotal * discount;
        double total = subtotal - discountAmount;

        Label subtotalLabel = new Label("Subtotal: $" + String.format("%.2f", subtotal));
        Label discountLabel = new Label("Discount: $" + String.format("%.2f", discountAmount) +
                " (" + (discount * 100) + "%)");
        Label totalLabel = new Label("Total: $" + String.format("%.2f", total));
        totalLabel.setStyle("-fx-font-weight: bold;");

        orderSummary.getChildren().addAll(summaryLabel, itemsListView, subtotalLabel, discountLabel, totalLabel);

        VBox paymentSection = new VBox(10);
        paymentSection.setPadding(new Insets(20));
        paymentSection.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5;");
        paymentSection.setMaxWidth(500);
        paymentSection.setAlignment(Pos.CENTER);

        Label paymentLabel = new Label("Payment Method");
        paymentLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        ToggleGroup paymentToggle = new ToggleGroup();
        RadioButton creditCardRadio = new RadioButton("Credit Card");
        creditCardRadio.setToggleGroup(paymentToggle);
        RadioButton cashRadio = new RadioButton("Cash");
        cashRadio.setToggleGroup(paymentToggle);

        creditCardRadio.setSelected(true);

        HBox radioBox = new HBox(20);
        radioBox.setAlignment(Pos.CENTER);
        radioBox.getChildren().addAll(creditCardRadio, cashRadio);

        paymentSection.getChildren().addAll(paymentLabel, radioBox);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button backButton = new Button("Back to Order");
        backButton.setOnAction(e -> primaryStage.setScene(orderScene));

        Button completeOrderButton = new Button("Complete Order");
        completeOrderButton.setOnAction(e -> {
            try {
                Invoice invoice = coffeeManager.GetFrontDesk().CreateCustomerOrders(
                        currentCart, currentCustomerId, Instant.now());

                showInvoiceConfirmation(invoice);

                currentCart.clear();
                primaryStage.setScene(landingScene);

                createLandingScene();
                primaryStage.setScene(landingScene);

            } catch (InvalidOrderException ex) {
                showAlert("Error", "Failed to process order: " + ex.getMessage());
            } catch (InvalidCustomerException ex) {
                showAlert("Error", "Failed to process order: " + ex.getMessage());
            }
        });

        buttonBox.getChildren().addAll(backButton, completeOrderButton);

        root.getChildren().addAll(
                headerLabel,
                customerLabel,
                orderSummary,
                paymentSection,
                buttonBox
        );

        checkoutScene = new Scene(root);
    }

    private void showInvoiceConfirmation(Invoice invoice) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Order Confirmation");
        dialog.setHeaderText("Thank you for your order!");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label orderLabel = new Label("Order Details:");
        orderLabel.setStyle("-fx-font-weight: bold;");

        ListView<String> itemsListView = new ListView<>();
        itemsListView.setPrefHeight(150);

        ObservableList<String> items = FXCollections.observableArrayList();

        for (Order order : invoice.GetOrders()) {
            try {
                IItem item = coffeeManager.GetMenu().GetItem(order.GetItemID());
                float discountedPrice = item.GetCost() * (1 - order.GetDiscount());
                items.add(item.GetDescription() + " - $" + String.format("%.2f", discountedPrice) +
                        (order.GetDiscount() > 0 ? " (discounted)" : ""));
            } catch (UnknownItemException ex) {
            }
        }

        itemsListView.setItems(items);

        Label totalLabel = new Label("Total: $" + String.format("%.2f", invoice.GetTotalCost()));
        totalLabel.setStyle("-fx-font-weight: bold;");

        content.getChildren().addAll(orderLabel, itemsListView, totalLabel);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
