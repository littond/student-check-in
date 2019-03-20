package InventoryController;

import Database.Objects.Worker;
import HelperClasses.StageWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerMenu implements IController, Initializable {

    @FXML
    private VBox mainMenuScene;

    @FXML
    private Button inventory, manageStudents, manageWorkers;

    @FXML
    private ImageView msoeBackgroundImage;

    private Worker worker;

    private List <String> studentIDArray = new ArrayList<>();
    private StageWrapper stageWrapper = new StageWrapper();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.worker = null;
        manageStudents.setText("Manage\nStudents");
        manageWorkers.setText("Manage\nWorkers");
        manageWorkers.setDisable(true);
        inventory.setOnAction(event -> openInventory());
        manageStudents.setOnAction(event -> openMangeStudents());
        manageWorkers.setOnAction(event -> openManageWorkers());
        Image image = new Image("images/msoeBackgroundImage.png");
        this.msoeBackgroundImage.setImage(image);
    }

    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null) {
            this.worker = worker;
            if (this.worker != null && (this.worker.isAdmin() || this.worker.isWorker())) {
                manageWorkers.setDisable(false);
            }
        }
    }

    private void openInventory(){
        newStage("/fxml/InventoryPage.fxml");
    }

    private void openMangeStudents() {
        newStage("/fxml/manageStudents.fxml");
    }

    public void openCheckItemsPage(){
        newStage("/fxml/CheckOutItems.fxml");
    }

    private void openManageWorkers() {
        if (worker != null) {
            if (worker.isAdmin() || worker.isWorker()) {
                newStage("/fxml/manageWorkers.fxml");
            } else {
                adminStatusRequiredForManageWorkersError();
            }
        }
    }

    public void logout() {
        worker = null;
        try {
            FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("fxml/Login.fxml"));
            Pane loginPane = loader.load();
            mainMenuScene.getScene().setRoot(loginPane);
        } catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
            invoke.printStackTrace();
        }
    }

    public void newStage(String fxml){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            IController controller = loader.<IController>getController();
            controller.initWorker(worker);
            mainMenuScene.getScene().setRoot(root);
            ((IController) loader.getController()).initWorker(worker);
            // NEEDED?
            //mainMenuScene.getChildren().clear();
        }
        catch(IOException invoke){
            StudentCheckIn.logger.error("No valid stage was found to load. This could likely be because of a database disconnect.");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
            invoke.printStackTrace();
        }
    }

    public void openCheckoutFromScanner(KeyEvent keyEvent){
        studentIDArray.add(keyEvent.getCharacter());
        if(stageWrapper.getStudentID(studentIDArray).matches("^(rfid)$")) {
                newStage("fxml/CheckOutItems.fxml");
            }
    }

    private void adminStatusRequiredForManageWorkersError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Admin status is required to manage workers.\nPlease sign in with an administrator account to manage workers.");
        StudentCheckIn.logger.error("Admin status is required to manage workers.\nPlease sign in with an administrator account to manage workers.");
        alert.showAndWait();
    }

}