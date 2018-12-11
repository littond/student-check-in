package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TotalTabTableRow extends RecursiveTreeObject<TotalTabTableRow> {

    private StringProperty partName, serialNumber, location, barcode, partID;

    public TotalTabTableRow(String partName, String serialNumber, String location,
                            String barcode, String partID) {
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.location = new SimpleStringProperty(location);
        this.barcode = new SimpleStringProperty(barcode);
        this.partID = new SimpleStringProperty(partID);
    }

    public StringProperty getPartName() {
        return partName;
    }

    public StringProperty getSerialNumber() {
        return serialNumber;
    }

    public StringProperty getLocation() {
        return location;
    }

    public StringProperty getBarcode() {
        return barcode;
    }

    public StringProperty getPartID() {
        return partID;
    }

}