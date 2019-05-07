package InventoryController;

import Database.Database;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.WindowEvent;

import java.text.DecimalFormat;

public class ControllerViewTotalPart {

    DecimalFormat df = new DecimalFormat("#,###,##0.00");

    @FXML
    private VBox sceneViewTotalPart;

    @FXML
    private HBox gridContainer;

    @FXML
    private Font x1;

    @FXML
    private GridPane grid, gridCheckedOut, gridFaulty;

    @FXML
    private JFXTextField studentNameField, studentEmailField, partNameField, barcodeField, serialNumberField, partIDField, checkedOutDateField, dueDateField, priceField;

    @FXML
    private JFXTextArea faultField;

    public void populate(TotalTabTableRow row) {
        // if the part has been checked out before, the student email associated with it will not be empty ("")
        if (!row.getStudentEmail().get().equals("")) {
            studentNameField.setText(row.getStudentName().get());
            studentEmailField.setText(row.getStudentEmail().get());
            checkedOutDateField.setText(row.getCheckedOutAt().get());
            dueDateField.setText(row.getDueDate().get());
        } else {
            gridContainer.getChildren().remove(2);
            gridContainer.getChildren().remove(1);
        }
        partNameField.setText(row.getPartName().get());
        barcodeField.setText(row.getBarcode().get());
        serialNumberField.setText(row.getSerialNumber().get());
        partIDField.setText("" + row.getPartID().get());
        Database database = new Database();
        if (database.isOverdue(row.getDueDate().get())) {
            Label feeLabel = new Label("Fee:");
            feeLabel.setFont(x1);
            JFXTextField feeField = new JFXTextField("$" + df.format(Double.parseDouble(row.getFee().get())/100));
            gridCheckedOut.add(feeLabel, 0, 4);
            gridCheckedOut.add(feeField, 1, 4);
        }
        // if the row is faulty, set info
        // otherwise, remove the column for fault info
        if (row.sFaulty()) {
            priceField.setText("$" + df.format(Double.parseDouble(row.getFee().get())/100));
            faultField.setText(row.getFaultDescription().get());
        } else {
            gridContainer.getChildren().remove(gridContainer.getChildren().size()-1);
            gridContainer.getChildren().remove(gridContainer.getChildren().size()-1);
        }
    }

    public void goBack() {
        sceneViewTotalPart.fireEvent(new WindowEvent(((Node) sceneViewTotalPart).getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

}