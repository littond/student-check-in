package CheckItemsController;

import Database.Database;
import Database.SavedPart;
import Database.Student;
import HelperClasses.StageWrapper;
import InventoryController.CheckedOutItems;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class CheckoutPopUp extends StudentPage {

    @FXML
    private JFXTextField student, part, quantity, date, dueDate;

    @FXML
    private Label cID;

    @FXML
    private AnchorPane main;

    private Database database;

    public void populate(CheckedOutItems checked){
        student.setText(checked.getStudentName().get());
        part.setText(checked.getPartName().get());
        quantity.setText(checked.getQuantity().get() + "");
        date.setText(checked.getCheckedOutAt().get());
        dueDate.setText(checked.getDueDate().get());
        cID.setText("Checkout ID: " + checked.getCheckID().get());
    }

    public void savePart(ActionEvent actionEvent) {
        database = new Database();
        Student s = StudentPage.getStudent();
//        if (!s.getSavedItems().contains(new SavedPart(student.getText(), part.getText(), date.getText(),
//                Integer.parseInt(quantity.getText()), new Date(System.currentTimeMillis()).toString(), dueDate.getText(), cID.getText()))) {
            String prof = JOptionPane.showInputDialog(null, "Please enter a Professors name");
            String course = JOptionPane.showInputDialog(null, "Please enter a course code (i.e. CS3840)");
            String reason = JOptionPane.showInputDialog(null, "Please enter a short description why they need it saved");
            s.getSavedItems().add(new SavedPart(student.getText(), part.getText(), date.getText(), Integer.parseInt(quantity.getText()),
                    new Date(System.currentTimeMillis()).toString(), dueDate.getText(), cID.getText(), prof, course, reason));
//        }else {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Part has already been saved");
//            alert.showAndWait();
//        }
        try {
            Connection connection = database.getConnection();
            Statement statement = connection.createStatement();
            long date = System.currentTimeMillis();
            java.sql.Date d = new java.sql.Date(date);
            statement.executeUpdate("UPDATE checkouts SET  reservedAt = date('" + d.toString() + "') WHERE studentID = " + s.getID() + " and checkoutID = " + cID.getText().substring(13) + ";");
        }catch (SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not update database");
            alert.showAndWait();
            e.printStackTrace();
        }
        main.getScene().getWindow().hide();
    }
}