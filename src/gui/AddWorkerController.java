package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.CheckBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddWorkerController implements Initializable {

    @FXML
    private VBox scene;

    @FXML
    private Button addButtonAddWorkerPage, cancelButtonAddWorkerPage;

    @FXML
    private TextField nameInputAddWorkerPage,
            emailInputAddWorkerPage,
            adminPinInputAddWorkerPage;

    @FXML
    private CheckBox isAdminCheckBoxAddWorkerPage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addButtonAddWorkerPage.setAlignment(Pos.CENTER);
    }

    public void addWorker() {
        String name = nameInputAddWorkerPage.getText();
        String email = emailInputAddWorkerPage.getText();
        boolean isAdmin = isAdminCheckBoxAddWorkerPage.isSelected();
        String adminPin = adminPinInputAddWorkerPage.getText();
        if (!email.matches("^(.+)@msoe\\.edu$")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, invalid email was entered.\nNeeds to be an MSOE email");
            alert.showAndWait();
        } else if (!name.equals("")) {
            Worker worker = null;
            if (isAdmin) {
                if (!adminPin.equals("")) {
                    worker = new Administrator(name, email, isAdmin, adminPin);
                }
            } else {
                worker = new StudentWorker(name, email);
            }
            if (worker != null) {
                writeWorker(worker);
                scene.getScene().getWindow().hide();
            }
        }
    }

    private void writeWorker(Worker worker) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/workers.txt", true))) {
            bw.write(worker.writeWorker() + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void cancel() {
        scene.getScene().getWindow().hide();
    }

}