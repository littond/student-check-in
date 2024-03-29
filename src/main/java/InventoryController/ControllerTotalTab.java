package InventoryController;

import Database.ObjectClasses.Part;
import Database.ObjectClasses.Worker;
import Database.TotalTab;
import HelperClasses.AdminPinRequestController;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class acts as the controller for the total inventory tab of the inventory.
 */
public class ControllerTotalTab extends ControllerInventoryPage implements Initializable {

    @FXML
    public VBox totalTabPage;

    @FXML
    private ObservableList<TotalTabTableRow> tableRows;

    @FXML
    private JFXTextField searchInput;

    @FXML
    private JFXTreeTableView<TotalTabTableRow> totalTable;

    private TreeItem<TotalTabTableRow> root;

    @FXML
    private JFXTreeTableColumn<TotalTabTableRow, String> partNameCol, locationCol, serialNumberCol;

    @FXML
    private JFXTreeTableColumn<TotalTabTableRow, Integer> partIDCol;

    @FXML
    private JFXTreeTableColumn<TotalTabTableRow, Long> barcodeCol;


    private String partName, partID, serialNumber, loc, barcode;

    private static ObservableList<Part> data
            = FXCollections.observableArrayList();


    private Worker worker;


    private ArrayList<String> selectedFilters = new ArrayList<>();

    private Image editOneImage = new Image("images/edit.png");
    private Image editAllImage = new Image("images/edit_all.png");
    private Image deleteOneImage = new Image("images/delete.png");
    private Image deleteAllImage = new Image("images/delete_all.png");

    private TotalTab totalTab;

    /**
     * This method is called when the inventory is loaded. It populates the table and sets
     * listeners for clicking on rows or the edit/delete buttons.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {


        Label emptyTableLabel = new Label("No parts found.");
        emptyTableLabel.setStyle("-fx-text-fill: white");
        emptyTableLabel.setFont(new Font(18));
        totalTable.setPlaceholder(emptyTableLabel);

        partNameCol = new JFXTreeTableColumn<>("Part Name");
        partNameCol.setCellValueFactory(col -> col.getValue().getValue().getPartName());

        partNameCol.prefWidthProperty().bind(totalTable.widthProperty().divide(5));
        partNameCol.setStyle("-fx-font-size: 18px");
        partNameCol.setResizable(false);
        partNameCol.setCellFactory(new Callback<TreeTableColumn<TotalTabTableRow, String>, TreeTableCell<TotalTabTableRow, String>>() {
            @Override
            public TreeTableCell<TotalTabTableRow, String> call(TreeTableColumn<TotalTabTableRow, String> param) {
                return new TreeTableCell<TotalTabTableRow, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (this.getTreeTableRow().getItem() != null) {
                            String name = this.getTreeTableRow().getItem().getPartName().getValue();
                            Label partNames = new Label();
                            partNames.setText(name);
                            if (empty) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                ImageView editOneImageView = new ImageView(editOneImage);
                                editOneImageView.setFitHeight(12);
                                editOneImageView.setFitWidth(12);
                                final JFXButton editOneButton = new JFXButton();
                                editOneButton.setGraphic(editOneImageView);
                                editOneButton.setButtonType(JFXButton.ButtonType.RAISED);
                                editOneButton.setOnAction(event -> {
                                    if (worker != null && worker.isEdit()) {
                                        editPart(getTreeTableRow().getItem().getPartID().getValue().toString(), false);
                                    } else {
                                        if (worker != null && worker.isAdmin()
                                                || requestAdminPin("edit a part")) {
                                            editPart(getTreeTableRow().getItem().getPartID().getValue().toString(), false);
                                        }
                                    }
                                });
                                editOneButton.setTooltip(new Tooltip("Edit this part"));

                                ImageView editAllImageView = new ImageView(editAllImage);
                                editAllImageView.setFitHeight(12);
                                editAllImageView.setFitWidth(12);
                                final JFXButton editAllButton = new JFXButton();
                                editAllButton.setGraphic(editAllImageView);
                                editAllButton.setButtonType(JFXButton.ButtonType.RAISED);
                                editAllButton.setOnAction(event -> {
                                    if (worker != null && worker.isEdit()) {
                                        editPart(getTreeTableRow().getItem().getPartID().getValue().toString(), true);
                                    } else {
                                        if ((worker != null && worker.isAdmin())
                                                || requestAdminPin("edit parts")) {
                                            editPart(getTreeTableRow().getItem().getPartID().getValue().toString(), true);
                                        }
                                    }
                                });
                                Tooltip editAllTip = new Tooltip("Edit all parts named: " + partNames.getText());
                                editAllButton.setTooltip(editAllTip);

                                ImageView deleteOneImageView = new ImageView(deleteOneImage);
                                deleteOneImageView.setFitHeight(12);
                                deleteOneImageView.setFitWidth(12);
                                final JFXButton deleteOneButton = new JFXButton();
                                deleteOneButton.setGraphic(deleteOneImageView);
                                deleteOneButton.setButtonType(JFXButton.ButtonType.RAISED);
                                deleteOneButton.setOnAction(event -> {
                                    if (worker != null && worker.isRemove()) {
                                        if (!database.getIsCheckedOut("" + getTreeTableRow().getItem().getPartID().getValue())) {
                                            deletePart(getTreeTableRow().getItem().getPartID().getValue().toString());
                                        } else {
                                            deleteCheckedOutPartAlert();
                                        }
                                    } else {
                                        if ((worker != null && worker.isAdmin())
                                                || requestAdminPin("Delete a Part")) {
                                            if (!database.getIsCheckedOut("" + getTreeTableRow().getItem().getPartID().getValue())) {
                                                deletePart(getTreeTableRow().getItem().getPartID().getValue().toString());
                                            } else {
                                                deleteCheckedOutPartAlert();
                                            }
                                        }
                                    }
                                    populateTable();
                                });
                                Tooltip deleteOneTip = new Tooltip("Delete this part");
                                deleteOneButton.setTooltip(deleteOneTip);

                                ImageView deleteAllImageView = new ImageView(deleteAllImage);
                                deleteAllImageView.setFitHeight(12);
                                deleteAllImageView.setFitWidth(12);
                                final JFXButton deleteAllButton = new JFXButton();
                                deleteAllButton.setGraphic(deleteAllImageView);
                                deleteAllButton.setButtonType(JFXButton.ButtonType.RAISED);
                                deleteAllButton.setOnAction(event -> {
                                    if (worker != null && worker.isRemove()) {
                                        boolean typeHasOneCheckedOut = false;
                                        ArrayList<String> partIDs = database.getAllPartIDsForPartName("" + getTreeTableRow().getItem().getPartID().getValue());
                                        for (String id : partIDs) {
                                            if (database.getIsCheckedOut(id)) {
                                                typeHasOneCheckedOut = true;
                                            }
                                        }
                                        if (!typeHasOneCheckedOut) {
                                            deletePartType(getTreeTableRow().getItem().getPartName().getValue());
                                        } else {
                                            typeHasOneCheckedOutError(getTreeTableRow().getItem().getPartName().getValue());
                                        }
                                    } else {
                                        if ((worker != null && worker.isAdmin())
                                                || requestAdminPin("delete parts")) {
                                            boolean typeHasOneCheckedOut = false;
                                            ArrayList<String> partIDs = database.getAllPartIDsForPartName("" + getTreeTableRow().getItem().getPartID().getValue());
                                            for (String id : partIDs) {
                                                if (database.getIsCheckedOut(id)) {
                                                    typeHasOneCheckedOut = true;
                                                }
                                            }
                                            if (!typeHasOneCheckedOut) {
                                                deletePartType(getTreeTableRow().getItem().getPartName().getValue());
                                            } else {
                                                typeHasOneCheckedOutError(getTreeTableRow().getItem().getPartName().getValue());
                                            }
                                        }
                                    }
                                    populateTable();
                                });
                                Tooltip deleteAllTip = new Tooltip("Delete all parts named: " + partNames.getText());
                                deleteAllButton.setTooltip(deleteAllTip);

                                VBox column = new VBox();
                                HBox actionButtons = new HBox();
                                actionButtons.setPrefHeight(column.getHeight() / 5);
                                actionButtons.setAlignment(Pos.TOP_RIGHT);
                                actionButtons.setOpacity(0);
                                actionButtons.hoverProperty().addListener(observable -> {
                                    if (actionButtons.isHover() && actionButtons != null) {
                                        actionButtons.setOpacity(1);
                                    } else {
                                        actionButtons.setOpacity(0);
                                    }
                                });
                                actionButtons.getChildren().addAll(editOneButton, editAllButton, deleteOneButton, deleteAllButton);
                                VBox text = new VBox();
                                text.setMinHeight(30);
                                text.setAlignment(Pos.TOP_CENTER);
                                text.getChildren().add(partNames);
                                column.getChildren().addAll(actionButtons, text);
                                setGraphic(column);
                                setText(null);
                            }
                        }

                    }
                };
            }
        });

        serialNumberCol = new JFXTreeTableColumn<>("Serial Number");
        serialNumberCol.prefWidthProperty().bind(totalTable.widthProperty().divide(5));
        serialNumberCol.setStyle("-fx-font-size: 18px");
        serialNumberCol.setResizable(false);
        serialNumberCol.setCellValueFactory(col -> col.getValue().getValue().getSerialNumber());


        locationCol = new JFXTreeTableColumn<>("Location");
        locationCol.prefWidthProperty().bind(totalTable.widthProperty().divide(5));
        locationCol.setStyle("-fx-font-size: 18px");
        locationCol.setResizable(false);
        locationCol.setCellValueFactory(col -> col.getValue().getValue().getLocation());

        barcodeCol = new JFXTreeTableColumn<>("Barcode");
        barcodeCol.prefWidthProperty().bind(totalTable.widthProperty().divide(5));
        barcodeCol.setStyle("-fx-font-size: 18px");
        barcodeCol.setResizable(false);
        barcodeCol.setCellValueFactory(col -> col.getValue().getValue().getBarcode().asObject());

        partIDCol = new JFXTreeTableColumn<>("Part ID");
        partIDCol.prefWidthProperty().bind(totalTable.widthProperty().divide(5));
        partIDCol.setStyle("-fx-font-size: 18px");
        partIDCol.setResizable(false);
        partIDCol.setCellValueFactory(col -> col.getValue().getValue().getPartID().asObject());

        tableRows = FXCollections.observableArrayList();

        // Click to select if unselected and unselect if selected
        totalTable.setRowFactory(new Callback<TreeTableView<TotalTabTableRow>, TreeTableRow<TotalTabTableRow>>() {
            @Override
            public TreeTableRow<TotalTabTableRow> call(TreeTableView<TotalTabTableRow> param) {
                final TreeTableRow<TotalTabTableRow> row = new TreeTableRow<>();
                row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2) {
                            viewPart(row.getIndex());
                        } else {
                            final int index = row.getIndex();
                            if (index >= 0 && index < totalTable.getCurrentItemsCount() && totalTable.getSelectionModel().isSelected(index)) {
                                totalTable.getSelectionModel().clearSelection();
                                event.consume();
                            }
                        }
                    }
                });
                return row;
            }
        });

        // Updates the search if the user presses enter with the cursor in the search field
        searchInput.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                search();
            }
        });
        populateTable();
    }

    public void exportParts() {
        ObservableList<Part> list = totalTab.getTotalTabParts();
        export.exportPartList(list);
    }

    /**
     * Adds the current worker to the class, so that the class knows whether an administrator
     * or student worker is currently logged in.
     *
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null) {
            this.worker = worker;
        }
    }

    /**
     * Determines which rows fit the search input
     */
    private void filter(TreeItem<TotalTabTableRow> root, String filter, TreeItem<TotalTabTableRow> filteredRoot) {
        TreeItem<TotalTabTableRow> filteredChild;
        for (TreeItem<TotalTabTableRow> child : root.getChildren()) {
            filteredChild = new TreeItem<>();
            filteredChild.setValue(child.getValue());
            filteredChild.setExpanded(true);
            filter(child, filter, filteredChild);
            if (!filteredChild.getChildren().isEmpty() || isMatch(filteredChild.getValue(), filter)) {
                filteredRoot.getChildren().add(filteredChild);
            }
        }
    }

    /**
     * Displays a pop up for viewing everything about the part
     *
     * @param index index in the table for the part to be viewed
     */
    private void viewPart(int index) {
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewTotalPart.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("View Part");
            stage.initOwner(totalTabPage.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            if (index != -1) {
                TreeItem item = totalTable.getSelectionModel().getModelItem(index);
                // null if user clicks on empty row
                if (item != null) {
                    TotalTabTableRow row = ((TotalTabTableRow) item.getValue());
                    ((ControllerViewTotalPart) loader.getController()).populate(row);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the table based on the input text for the search
     */
    @FXML
    private void search() {
        String filter = searchInput.getText();
        String[] filters = filter.split(",");
        if (filter.isEmpty()) {
            totalTable.setRoot(root);
        } else {
            TreeItem<TotalTabTableRow> filteredRoot = new TreeItem<>();
            selectedFilters.removeAll(selectedFilters.subList(0, selectedFilters.size()));
            for (String f : filters) {
                f = f.trim();
                if (f.equalsIgnoreCase("faulty") || f.equalsIgnoreCase("overdue") ||
                        f.equalsIgnoreCase("checked out") || f.equalsIgnoreCase("all")) {
                    f = f.substring(0, 1).toUpperCase() + f.substring(1);
                    if (f.length() > 7) {
                        f = f.substring(0, 8) + f.substring(8, 9).toUpperCase() + f.substring(9);
                    }
                    selectedFilters.add(f);
                    populateTable();
                } else {
                    filter(root, f, filteredRoot);
                    totalTable.setRoot(filteredRoot);
                }
            }
        }
    }

    /**
     * Determines whether a specific row matches the search input
     *
     * @param value  the tested row
     * @param filter the search input
     * @return true if the row matches the search criteria; false otherwise
     */
    private boolean isMatch(TotalTabTableRow value, String filter) {
        String input = filter.toLowerCase();
        partName = value.getPartName().getValue();
        serialNumber = value.getSerialNumber().getValue().toString();
        loc = value.getLocation().getValue();
        barcode = value.getBarcode().getValue().toString();
        partID = value.getPartID().getValue().toString();

        return ((partName != null && partName.toLowerCase().contains(input))
                || (serialNumber != null && serialNumber.toLowerCase().contains(input))
                || (loc != null && loc.toLowerCase().contains(input))
                || (barcode != null && barcode.toLowerCase().contains((input))
                || (partID != null && partID.toLowerCase().contains(input))));
    }


    /**
     * Sets the values for each table column, empties the current table, then calls selectParts to populate it.
     */
    @FXML
    public void populateTable() {
        tableRows.clear();
        totalTable.getColumns().clear();
        totalTab = new TotalTab();
        ObservableList<Part> list = totalTab.getTotalTabParts();
        for (int i = 0; i < list.size(); i++) {
            tableRows.add(new TotalTabTableRow(
                    list.get(i).getPartID(), list.get(i).getBarcode(), list.get(i).getSerialNumber(), list.get(i).getLocation(), list.get(i).getPartName(), list.get(i).getPrice()));
        }
        root = new RecursiveTreeItem<TotalTabTableRow>(
                tableRows, RecursiveTreeObject::getChildren
        );
        totalTable.getColumns().setAll(partNameCol, barcodeCol,
                serialNumberCol, locationCol, partIDCol);
        totalTable.setRoot(root);
        totalTable.setShowRoot(false);
    }


    /**
     * Called to bring up the "AddPart" FXML scene.
     */
    @FXML
    public void addPart() {
        Stage stage = new Stage();
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/AddPart.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load(myFxmlURL);
            Scene scene = new Scene(root, 400, 450);
            stage.setTitle("Add a Part");
            stage.initOwner(totalTabPage.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    populateTable();
                    stage.close();
                }
            });
            stage.show();
        } catch (IOException e) {
            StudentCheckIn.logger.error("IOException: Loading Add Part.");
            e.printStackTrace();
        }
    }

    /**
     * Asks the student worker to enter an admin pin if they try to do something they do
     * not have the privilege to do
     *
     * @param action the privileged action that the worker tried to do
     * @return true if the inputted admin pin is correct; false otherwise
     */
    public boolean requestAdminPin(String action) {
        AtomicBoolean isValid = new AtomicBoolean(false);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminPinRequest.fxml"));
            Parent root = loader.load();
            ((AdminPinRequestController) loader.getController()).setAction(action);
            Scene scene = new Scene(root, 350, 250);
            Stage stage = new Stage();
            stage.setTitle("Admin Pin Required");
            stage.initOwner(totalTabPage.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.setResizable(false);
            stage.setOnCloseRequest(e -> {
                // checks to see whether the pin was submitted or the window was just closed
                if (((AdminPinRequestController) loader.getController()).isSubmitted()) {
                    // checks to see if the input pin is empty. if empty, close pop up
                    if (((AdminPinRequestController) loader.getController()).isNotEmpty()) {
                        // checks to see whether the submitted pin matches one of the admin's pins
                        if (((AdminPinRequestController) loader.getController()).isValid()) {
                            stage.close();
                            isValid.set(true);
                        } else {
                            stage.close();
                            invalidAdminPinAlert();
                            isValid.set(false);
                        }
                    } else {
                        stage.close();
                        isValid.set(false);
                    }
                }
            });
            stage.showAndWait();
        } catch (IOException e) {
            StudentCheckIn.logger.error("IOException: Loading Admin Pin Request.");
            e.printStackTrace();
        }
        return isValid.get();
    }

    /**
     * Called when a row is highlighted in the table and the edit button is clicked.
     */
    @FXML
    public void editPart(String partID, boolean isBatchEdit) {
        try {
            Part part = database.selectPart(Integer.parseInt(partID));
            FXMLLoader loader;
            if (isBatchEdit) {
                loader = new FXMLLoader(getClass().getResource("/fxml/EditPartType.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("/fxml/EditOnePart.fxml"));

            }
            Parent root = loader.load();
            ((ControllerEditPart) loader.getController()).initPart(part);
            Scene scene = new Scene(root, 400, 500);
            Stage stage = new Stage();
            stage.setMinWidth(400);
            stage.setMaxWidth(400);
            stage.setMaxHeight(550);
            stage.setMinHeight(550);
            if (isBatchEdit) {
                stage.setTitle("Edit all " + part.getPartName());
            } else {
                String partName = part.getPartName();
                if (part.getPartName().substring(part.getPartName().length() - 1).equals("s")) {
                    partName = part.getPartName().substring(0, part.getPartName().length() - 1);
                }
                stage.setTitle("Edit a " + partName);
            }
            stage.initOwner(totalTabPage.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    selectedFilters.add("All");
                    populateTable();
                    stage.close();
                }
            });
            stage.show();
        } catch (IOException e) {
            StudentCheckIn.logger.error("IOException: Loading Edit Part.");
            e.printStackTrace();
        }
    }

    /**
     * This method calls the database method to soft delete an item from the inventory list
     * this then updates the gui table
     */
    public void deletePart(String partID) {
        database.initWorker(worker);
        try {
            if (database.selectPart(Integer.parseInt(partID)) != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you wish to delete the part with ID = " + partID + "?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    database.deleteItem(Integer.parseInt(partID));
                    populateTable();
                }
            }
        } catch (Exception e) {
            StudentCheckIn.logger.error("Exception while deleting part.");
            e.printStackTrace();
        }
    }

    public void deletePartType(String partName) {
        database.initWorker(worker);
        try {
            if (database.hasPartName(partName)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you wish to delete all parts named: " + partName + "?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    database.deleteParts(partName);
                    populateTable();
                }
            }
        } catch (Exception e) {
            StudentCheckIn.logger.error("Exception while deleting part type.");
            e.printStackTrace();
        }
    }

    /**
     * Alert that the part is currently checked out, so it cannot be deleted
     */
    private void deleteCheckedOutPartAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("This part is currently checked out and cannot be deleted.");
        StudentCheckIn.logger.error("This part is currently checked out and cannot be deleted.");
        alert.showAndWait();
    }

    /**
     * Alert that the part is currently checked out, so it cannot be deleted
     */
    private void typeHasOneCheckedOutError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("At least one " + partName + " is currently checked out, so " +
                partName + " parts cannot be deleted.");
        StudentCheckIn.logger.error("At least one " + partName + " is currently checked out, so " +
                partName + " parts cannot be deleted.");
        alert.showAndWait();
    }

    /**
     * Alert that the part is currently checked out, so it cannot be deleted
     */
    private void deleteAllCheckedOutPartAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("One part of this type is checked out. You cannot delete all of these parts.");
        StudentCheckIn.logger.error("One part of this type is checked out. You cannot delete all of these parts.");
        alert.showAndWait();
    }

    /**
     * Alert that the pin entered does not match one of the admin pins.
     */
    private void invalidAdminPinAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("The pin entered is invalid.");
        StudentCheckIn.logger.error("The pin entered is invalid.");
        alert.showAndWait();
    }

}