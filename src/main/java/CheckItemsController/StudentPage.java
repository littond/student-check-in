package CheckItemsController;

import Database.Student;
import HelperClasses.StageWrapper;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class StudentPage implements Initializable {

    @FXML
    private AnchorPane main;

    @FXML
    private static Label studentName, email, RFID;

    @FXML
    private JFXTreeTableView coTable, oTable, sTable;

    @FXML
    private JFXTreeTableColumn<Student, String> coTableCol, oTableCol, sTableCol;

//    private Database database;
    private Student student;
    private StageWrapper stageWrapper = new StageWrapper();


//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        database = new Database();
//    }

    public void setStudent(Student s){
        student = s;
        studentName = new Label("");
        studentName.setLayoutX(270);
        studentName.setLayoutY(14);
        studentName.setText(student.getName());
        studentName.getStylesheets().add(getClass().getResource("/HeaderStyle.css").toExternalForm());
        email = new Label("");
        RFID = new Label( "");
        main.getChildren().add(studentName);
        System.out.println(studentName.getText());
        email.setText(student.getEmail());
        System.out.println(email.getText());
        RFID.setText(student.getID() + "");
        System.out.println(RFID.getText());
//        setTables();
    }

    private void setTables() {
        coTableCol = new JFXTreeTableColumn<>("Part Name");
        coTableCol.setPrefWidth(200);
        coTableCol.setCellValueFactory((Callback<TreeTableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>) param ->
                new SimpleStringProperty(param.getValue().getValue().getCheckedOut().get(0).getPartName()));

        oTableCol = new JFXTreeTableColumn<>("Part Name");
        oTableCol.setPrefWidth(200);
        oTableCol.setCellValueFactory((Callback<TreeTableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>) param ->
                new SimpleStringProperty(param.getValue().getValue().getOverdueItems().get(0).getPart()));

        sTableCol = new JFXTreeTableColumn<>("Part Name");
        sTableCol.setPrefWidth(200);
        sTableCol.setCellValueFactory((Callback<TreeTableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>) param ->
                new SimpleStringProperty(param.getValue().getValue().getSavedItems().get(0).getPartName()));

        populateTables();
    }

    private void populateTables(){
        coTable.getColumns().clear();
        oTable.getColumns().clear();
        sTable.getColumns().clear();
        coTable.getColumns().setAll(student.getCheckedOut());
        oTable.getColumns().setAll(student.getOverdueItems());
        sTable.getColumns().setAll(student.getSavedItems());
    }

    public void goBack() {
        stageWrapper.newStage("CheckoutItems.fxml", main);
    }

    public void goHome() {
        stageWrapper.newStage("Menu.fxml", main);
    }

    public StudentPage() {
//        setStudent(new Student("Test Student", 00000, "john@msoe.edu", null, null, null));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
 //       setStudent(new Student("Test Student", 00000, "john@msoe.edu", null, null, null));
    }
}
