package ManagePeople;

import CheckItemsController.CheckoutPopUp;
import CheckItemsController.SavedPopUp;
import Database.Database;
import Database.ObjectClasses.Worker;
import Database.OverdueItem;
import Database.ObjectClasses.SavedPart;
import Database.ObjectClasses.Student;
import InventoryController.CheckedOutItems;
import InventoryController.IController;
import InventoryController.OverduePopUpController;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class EditStudent implements IController {

    @FXML
    private AnchorPane main = new AnchorPane();

    @FXML
    private VBox vbox = new VBox();

    @FXML
    private JFXTextField studentName, email, RFID;

    @FXML
    private Label fees, date;

    @FXML
    private JFXTreeTableView coTable;

    @FXML
    private JFXTreeTableView oTable;

    @FXML
    private JFXTreeTableView sTable;

    @FXML
    private JFXTreeTableColumn<CheckedOutItems, String> coTableCol;

    @FXML
    private JFXTreeTableColumn<OverdueItem, String> oTableCol;

    @FXML
    private JFXTreeTableColumn<SavedPart, String> sTableCol;

    private static Student student;
    private Worker worker;
    private Database database;
    private static String name;
    private static int id;
    private static String studentEmail;

    public void setStudent(Student s) {
        student = s;
        database = new Database();
        studentName.setText(student.getName());
        email.setText(student.getEmail());
        RFID.setText(student.getRFID() + "");
        name = studentName.getText();
        id = Integer.parseInt(RFID.getText());
        studentEmail = email.getText();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(5);
        setTables();
    }

    private void setTables() {
        coTableCol = new JFXTreeTableColumn<>("Part Name");
        coTableCol.prefWidthProperty().bind(coTable.widthProperty());
        coTableCol.setStyle("-fx-font-size: 18px");
        coTableCol.setResizable(false);
        coTableCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CheckedOutItems, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CheckedOutItems, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        oTableCol = new JFXTreeTableColumn<>("Part Name");
        oTableCol.prefWidthProperty().bind(oTable.widthProperty());
        oTableCol.setStyle("-fx-font-size: 18px");
        oTableCol.setResizable(false);
        oTableCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OverdueItem, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OverdueItem, String> param) {
                return param.getValue().getValue().getPart();
            }
        });

        sTableCol = new JFXTreeTableColumn<>("Part Name");
        sTableCol.prefWidthProperty().bind(sTable.widthProperty());
        sTableCol.setStyle("-fx-font-size: 18px");
        sTableCol.setResizable(false);
        sTableCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<SavedPart, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<SavedPart, String> param) {
                return param.getValue().getValue().getPartName();
            }
        });

        populateTables();
    }

    private void populateTables() {
        final TreeItem<CheckedOutItems> coItems = new RecursiveTreeItem<>(student.getCheckedOut(), RecursiveTreeObject::getChildren);
        final TreeItem<OverdueItem> oItems = new RecursiveTreeItem<>(student.getOverdueItems(), RecursiveTreeObject::getChildren);
        final TreeItem<SavedPart> sItems = new RecursiveTreeItem<>(student.getSavedItems(), RecursiveTreeObject::getChildren);
        coTable.getColumns().setAll(coTableCol);
        coTable.setRoot(coItems);
        coTable.setShowRoot(false);
        oTable.getColumns().setAll(oTableCol);
        oTable.setRoot(oItems);
        oTable.setShowRoot(false);
        sTable.getColumns().setAll(sTableCol);
        sTable.setRoot(sItems);
        sTable.setShowRoot(false);
    }

    public void coPopUp(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Stage stage = new Stage();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StudentCheckPopUp.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 400, 300);
                stage.setTitle("Checked Out Item");
                stage.initOwner(main.getScene().getWindow());
                stage.setScene(scene);
                int index = coTable.getSelectionModel().getSelectedIndex();
                if (index != -1) {
                    CheckedOutItems item = ((CheckedOutItems) coTable.getSelectionModel().getModelItem(index).getValue());
                    ((CheckoutPopUp) loader.getController()).populate(item);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void oPopUp(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Stage stage = new Stage();
            try {
                URL myFxmlURL = ClassLoader.getSystemResource("fxml/OverduePopup.fxml");
                FXMLLoader loader = new FXMLLoader(myFxmlURL);
                Parent root = loader.load();
                Scene scene = new Scene(root, 400, 300);
                stage.setTitle("Overdue Item");
                stage.initOwner(main.getScene().getWindow());
                stage.setScene(scene);
                int index = oTable.getSelectionModel().getSelectedIndex();
                if (index != -1) {
                    OverdueItem item = ((OverdueItem) oTable.getSelectionModel().getModelItem(index).getValue());
                    ((OverduePopUpController) loader.getController()).populate(item, null);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sPopUp(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Stage stage = new Stage();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SavedPopUp.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 350, 400);
                stage.setTitle("Saved Item");
                stage.initOwner(main.getScene().getWindow());
                stage.setScene(scene);
                int index = sTable.getSelectionModel().getSelectedIndex();
                if (index != -1) {
                    SavedPart item = ((SavedPart) sTable.getSelectionModel().getModelItem(index).getValue());
                    ((SavedPopUp) loader.getController()).populate(item);
                    stage.getIcons().add(new Image("images/msoe.png"));
                    stage.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Student getStudent() {
        return student;
    }

    public void save(ActionEvent actionEvent) {
        if (name.equals(studentName.getText()) && id == Integer.parseInt(RFID.getText()) && studentEmail.equals(email.getText())){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No changes detected...");
            alert.setTitle("Edit Failure");
            alert.setHeaderText("No changes were made.");
            alert.showAndWait();
        }else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to make the following changes?\n");
            alert.setTitle("Edit Success");
            alert.setHeaderText("Student info changing...");
            if (!name.equals(studentName.getText())) {
                alert.setContentText(alert.getContentText() + "\t" + name + " --> " + studentName.getText() + "\n");
            }
            if (id != Integer.parseInt(RFID.getText())) {
                alert.setContentText(alert.getContentText() + "\t" + id + " --> " + RFID.getText() + "\n");
            }
            if (!studentEmail.equals(email.getText())){
                alert.setContentText(alert.getContentText() + "\t" + studentEmail + " --> " + email.getText() + "\n");
            }
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                student.setName(studentName.getText());
                student.setRFID(Integer.parseInt(RFID.getText()));
                database.initWorker(worker);
                database.updateStudent(student);
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Student updated");
                alert1.showAndWait();
            }else if (result.isPresent() && result.get() == ButtonType.CANCEL){
                studentName.setText(name);
                RFID.setText(id + "");
            }
        }
    }

    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null){
            this.worker = worker;
        }
    }
}
