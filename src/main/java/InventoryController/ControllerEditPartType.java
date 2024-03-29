package InventoryController;

import Database.EditPart;
import Database.ObjectClasses.Part;
import Database.VendorInformation;
import HelperClasses.StageWrapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * This class acts as the controller for the history tab of the inventory page
 */
public class ControllerEditPartType extends ControllerEditPart {
    @FXML
    private VBox sceneEditPartType;

    @FXML
    private JFXTextField nameField, serialField, manufacturerField, priceField,
            locationField, barcodeField;

    @FXML
    private JFXComboBox editVendorField;

    @FXML
    private JFXSpinner loader;

    @FXML
    private JFXButton saveButton;

    private Part part;

    private EditPart editPart = new EditPart();

    private VendorInformation vendorInformation = new VendorInformation();

    StageWrapper stageWrapper = new StageWrapper();

    /**
     * This method sets the data in the history page.
     * @param location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serialField.setEditable(false);
        setFieldValidator();
        part = null;
    }

    /**
     * Only allows the user to enter digits to the barcode field, only allows the price
     * input to look like a price, and requires that the editable fields not be blank.
     */
    private void setFieldValidator() {
        stageWrapper.requiredInputValidator(nameField);
        stageWrapper.acceptIntegerOnly(barcodeField);
        stageWrapper.acceptIntegerOnly(serialField);
        stageWrapper.requiredInputValidator(barcodeField);
        stageWrapper.requiredInputValidator(priceField);
        stageWrapper.requiredInputValidator(locationField);

        //Not sure exactly what this is for. If to make sure price field entering is only integer, could just
        //do a acceptIntegerOnly on the lines above for the price textfield.
        //Otherwise could change regex so that numbers > 1000 aren't rejected.
        //For now I commented it out.

//        priceField.textProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                if (!newValue.matches("^\\$?[0-9]*\\.?[0-9]{0,2}$")) {
//                    priceField.setText(oldValue);
//                }
//            }
//        });
    }

    /**
     * This method is used to pass data into the tab to initialize the text representing the edited part
     * @param part
     */
    @Override
    public void initPart(Part part) {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        String price = "$" + df.format(part.getPrice());
        if (this.part == null) {
            this.part = part;
            nameField.setText(part.getPartName());
            serialField.setText(part.getSerialNumber());
            manufacturerField.setText(part.getManufacturer());
            priceField.setText(price);
            ArrayList<String> vendors = vendorInformation.getVendorList();
            if (vendors != null) {
                editVendorField.setItems(FXCollections.observableList(vendors));
                editVendorField.getSelectionModel().select(vendorInformation.getVendorFromID(part.getVendor()));
            }
            locationField.setText(part.getLocation());
            barcodeField.setText(part.getBarcode().toString());
        }
    }

    /**
     * Edits the part in database
     */
    @Override
    public void updateItem(){
        if (validateInput()) {
            loader.setVisible(true);
            String originalPartName = part.getPartName();
            long originalBarcode = part.getBarcode();
            Part inputPart = updatePartFromInput();
            if (database.hasUniqueBarcodes(originalPartName)) {
                if (!barcodeField.getText().equals(originalBarcode)) {
                    editPart.editAllOfType(originalPartName, inputPart);
                } else {
                    uniqueBarcodeError(originalPartName);
                }
            } else {
                editPart.editAllOfTypeCommonBarcode(originalPartName, inputPart);
            }
            close();
            partEditedSuccess();
        }
    }

    /**
     * Helper method that sets the part info from the user input
     */
    private Part updatePartFromInput() {
        String partName = "";
        if (nameField.getText() != null) {
            partName = nameField.getText().trim();
        }
        String serialNumber = "";
        if (serialField.getText() != null) {
            serialNumber = serialField.getText().trim();
        }
        String manufacturer = "";
        if (manufacturerField.getText() != null) {
            manufacturer = manufacturerField.getText().trim();
        }
        double price = 0;
        // Note: price multiplied by 100, because it is stored in the database as an integer 100 times
        // larger than actual value.
        if (priceField.getText() != null) {
            price = Double.parseDouble(priceField.getText().replaceAll(",", "").replace("$", "").trim());
        }
        String vendor = "";
        if (editVendorField.getSelectionModel().getSelectedItem() != null) {
            vendor = editVendorField.getSelectionModel().getSelectedItem().toString();
        }
        String location = "";
        if (locationField.getText() != null) {
            location = locationField.getText().trim();
        }
        long barcode = 0;
        if (barcodeField.getText() != null) {
            barcode = Long.parseLong(barcodeField.getText().trim());
        }
        part.update(partName, serialNumber, manufacturer, price, vendor, location, barcode);
        return part;
    }

    /**
     * This method uses helper methods to ensure that he inputs for the part are valid.
     * @return true if the inputs are valid, false otherwise
     */
    private boolean validateInput() {
        boolean isValid = true;
        String vendor = editVendorField.getSelectionModel().getSelectedItem().toString();
        if (!validateAllFieldsFilledIn(nameField.getText(),
                priceField.getText().replaceAll(",", ""),
                locationField.getText(),
                barcodeField.getText(), manufacturerField.getText(), vendor)) {
            isValid = false;
            fieldErrorAlert();
//        } else {
//            ArrayList<String> partNames = database.getUniquePartNames();
//            if (!nameField.getText().equals(part.getPartName())) {
//                for (String name: partNames){
//                    if (name.equals(nameField.getText())){
//                        //isValid = true;
//                        //uniquePartNameError();
//                        nameField.setText(part.getPartName());
//                    }
//                }
            } else if (!barcodeField.getText().equals(part.getBarcode().toString())) {
                if (database.hasUniqueBarcodes(part.getPartName())) {
                    isValid = false;
                    uniqueBarcodeError(part.getPartName());
                    barcodeField.setText(part.getBarcode().toString());
                } else if (!validateUnusedBarcode(Integer.parseInt(barcodeField.getText()))) {
                    isValid = false;
                    unusedBarcodeError(Integer.parseInt(barcodeField.getText()));
                }
            }
            boolean newVendor = true;
            for (String vendors : vendorInformation.getVendorList()) {
                if (vendors.equals(vendor)) {
                    newVendor = false;
                }
            }
            if (newVendor){
                vendorInformation.createNewVendor(vendor, vendorInformation());
            }
        return isValid;
    }

    /**
     * If new vendor is created, this popup asks for vendor information
     * Currently nothing is done with the result of the text
     */
    private String vendorInformation(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Vendor Information");
        dialog.setHeaderText("New vendor created, please enter vendor information");
        dialog.setContentText("Please enter vendor information");
        dialog.showAndWait();
        return dialog.getResult();
    }

    /**
     * This method ensures that the inputted fields are not empty.
     * @return true if the fields are not empty, false otherwise
     */
    protected boolean validateAllFieldsFilledIn(String partName, String price,
                                                String location, String barcode, String manufacturer, String vendor) {
        return partName != null && !partName.trim().equals("")
                && price != null && !price.trim().equals("")
                && location != null && !location.trim().equals("")
                && barcode != null && !barcode.trim().equals("")
                && manufacturer != null && !manufacturer.trim().equals("")
                && vendor != null && !vendor.trim().equals("");
    }

    private boolean validateUnusedBarcode(int barcode) {
        boolean unique = true;
            ArrayList<Integer> barcodes = database.getAllBarcodes(barcode);
            if (barcodes.size() > 0) {
                for (Integer barcd: barcodes) {
                    if (barcd == barcode) {
                        unique = false;
                    }
                }
            }
        return unique;
    }

    /**
     * This method throws an error that a part with the inputted name already exists.
     */
    private void uniquePartNameError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("A part with that name already exists.");
        alert.showAndWait();
    }

    /**
     * This method throws an error that the edited part type has unique barcodes, so the
     * barcode cannot be edited.
     * @param partName the name of the type of part being edited
     */
    private void uniqueBarcodeError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(partName + " parts have unique barcodes, so you cannot change all of them.");
        alert.showAndWait();
    }

    /**
     * This method throws an error that there already exists a part with the given barcode
     */
    private void unusedBarcodeError(int barcode) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("A part with that barcode -" + barcode + "- already exists.");
        alert.showAndWait();
    }

    /**
     * Creates an alert informing user to fill out all fields
     */
    private void fieldErrorAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Please fill out all fields");

        alert.showAndWait();
    }

    /**
     * Creates an alert informing user that part was edited successfully
     * @author Matthew Karcz
     */
    private void partEditedSuccess(){
        new Thread(new Runnable() {
            @Override public void run() {
                Platform.runLater(() -> {
                    Stage owner = new Stage(StageStyle.TRANSPARENT);
                    StackPane root = new StackPane();
                    root.setStyle("-fx-background-color: TRANSPARENT");
                    Scene scene = new Scene(root, 1, 1);
                    owner.setScene(scene);
                    owner.setWidth(1);
                    owner.setHeight(1);
                    owner.toBack();
                    owner.show();
                    Notifications.create().title("Successful!").text("All " + part.getPartName() + " parts edited successfully.").hideAfter(new Duration(5000)).show();
                    PauseTransition delay = new PauseTransition(Duration.seconds(5));
                    delay.setOnFinished( event -> owner.close() );
                    delay.play();
                });
            }
        }).start();
    }

    /**
     * Returns to main inventory page
     */
    public void goBack(){
        close();
    }

    /**
     * Helper method to send close request to total tab, which receives the request and
     * repopulates the table.
     */
    private void close(){
        //sceneAddPart.getScene().getWindow().hide();
        sceneEditPartType.fireEvent(new WindowEvent(((Node) sceneEditPartType).getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
