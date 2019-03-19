package ManagePeople;

import Database.Database;
import Database.Objects.Worker;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class EditAdmin {

    @FXML
    private AnchorPane main = new AnchorPane();

    @FXML
    private VBox vbox = new VBox();

    @FXML
    private JFXTextField email, workerName;

    @FXML
    private JFXTextField unmasked, unmaskedPin;

    @FXML
    private JFXPasswordField pass, pin;

    @FXML
    private JFXCheckBox showPass, showPin;

    private static Worker worker;
    private Database database = new Database();
    private static String name;
    private static String workerEmail;
    private static String password;
    private static int adminPin;

    public void setAdmin(Worker w) {
        worker = w;
        workerName.setText(w.getName());
        email.setText(w.getEmail());
        pass.setText(w.getPass());
        pin.setText(w.getPin() + "");
        name = workerName.getText();
        workerEmail = email.getText();
        password = pass.getText();
        adminPin = Integer.parseInt(pin.getText());
        unmasked.setManaged(false);
        unmaskedPin.setManaged(false);
        unmasked.setVisible(false);
        unmaskedPin.setVisible(false);
        unmasked.managedProperty().bind(showPass.selectedProperty());
        unmaskedPin.managedProperty().bind(showPin.selectedProperty());
        unmasked.visibleProperty().bind(showPass.selectedProperty());
        unmaskedPin.visibleProperty().bind(showPin.selectedProperty());
        pass.managedProperty().bind(showPass.selectedProperty().not());
        pin.managedProperty().bind(showPin.selectedProperty().not());
        pass.visibleProperty().bind(showPass.selectedProperty().not());
        pin.visibleProperty().bind(showPin.selectedProperty().not());
        unmasked.textProperty().bindBidirectional(pass.textProperty());
        unmaskedPin.textProperty().bindBidirectional(pin.textProperty());
        unmasked.setText(w.getPass());
        unmaskedPin.setText(w.getPin() + "");
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(5);
    }


    public void save(ActionEvent actionEvent) {
        if (name.equals(workerName.getText()) && password.equals(pass.getText()) && workerEmail.equals(email.getText()) &&
                adminPin == Integer.parseInt(pin.getText())){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No changes detected...");
            alert.setTitle("Edit Failure");
            alert.setHeaderText("No changes were made.");
            alert.showAndWait();
        }else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to make the following changes?\n");
            alert.setTitle("Edit Success");
            alert.setHeaderText("Student worker info changing...");
            if (!name.equals(workerName.getText())) {
                alert.setContentText(alert.getContentText() + "\t" + name + " --> " + workerName.getText() + "\n");
            }
            if (!workerEmail.equals(email.getText())) {
                alert.setContentText(alert.getContentText() + "\t" + workerEmail + " --> " + email.getText() + "\n");
            }
            if (!password.equals(pass.getText())){
                alert.setContentText(alert.getContentText() + "\t" + password + " --> " + pass.getText() + "\n");
            }
            if (adminPin != Integer.parseInt(pin.getText())){
                alert.setContentText(alert.getContentText() + "\t" + adminPin + " --> " + pin.getText() + "\n");
            }
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                worker.setName(workerName.getText());
                worker.setEmail(email.getText());
                worker.setPass(pass.getText());
                worker.setPin(Integer.parseInt(pin.getText()));
                database.updateWorker(worker);
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Admin updated");
                alert1.showAndWait();
            }else if (result.isPresent() && result.get() == ButtonType.CANCEL){
                workerName.setText(name);
                email.setText(workerEmail);
                pass.setText(password);
                pin.setText(adminPin + "");
            }
        }
    }
}