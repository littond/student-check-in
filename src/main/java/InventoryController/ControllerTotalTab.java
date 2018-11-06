package InventoryController;

import Database.Database;
import Database.DatabaseLogin;
import Database.Part;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerTotalTab  extends ControllerInventoryPage implements Initializable {

//    @FXML
//    private TextField searchTotal;

    @FXML
    private Button add, remove, refresh;

    @FXML
    public TableView<Part> tableView;

    @FXML
    private TableColumn<Part,String> partName, serialNumber, manufacturer, price, vendor, location,
            barcode, fault, partID;

    private static ObservableList<Part> data
            = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new Database(DatabaseLogin.username, DatabaseLogin.password);
        populateTable();
        tableView.setRowFactory(tv -> {
            TableRow<Part> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Part rowData = row.getItem();
                    editPart(rowData);
                }
            });
            return row;
        });
    }

    private void populateTable() {
        partName.setCellValueFactory(new PropertyValueFactory("partName"));
        serialNumber.setCellValueFactory(new PropertyValueFactory("serialNumber"));
        manufacturer.setCellValueFactory(new PropertyValueFactory("manufacturer"));
        price.setCellValueFactory(new PropertyValueFactory("price"));
        vendor.setCellValueFactory(new PropertyValueFactory("vendor"));
        location.setCellValueFactory(new PropertyValueFactory("location"));
        barcode.setCellValueFactory(new PropertyValueFactory("barcode"));
        fault.setCellValueFactory(new PropertyValueFactory("fault"));
        partID.setCellValueFactory(new PropertyValueFactory("partID"));

        this.data.clear();
        tableView.getItems().clear();

        this.data = selectParts("SELECT * from parts WHERE deletedBy IS NULL ORDER BY partID", this.data);

        tableView.getItems().setAll(this.data);
    }

    @FXML
    private void refresh(){
        populateTable();
    }

    @FXML
    public void addPart(){
        Stage stage = new Stage();
        try{
            URL myFxmlURL = ClassLoader.getSystemResource("AddPart.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load(myFxmlURL);
            Scene scene = new Scene(root, 400, 400);
            stage.setTitle("Add a Part");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e){
            e.printStackTrace();
        }
        //Called when the "Add" button is clicked
    }

    @FXML
    public void editPart(Part part){
        //Called when a part is double clicked in a table.
        //@param part the part that was double clicked
    }

    @FXML
    public void removePart(){
        if(tableView.getSelectionModel().getSelectedItems().size() == 1){
            database.deleteItem(tableView.getSelectionModel().getSelectedItem().getPartID());
        }
        tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
    }

//    @FXML
//    public void search(){
//        System.out.println(searchTotal.getText());
//    }
}