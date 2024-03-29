package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents all of the info about parts on the faulty inventory tab
 */
public class FaultyPartTabTableRow extends RecursiveTreeObject<FaultyPartTabTableRow> {

    private final StringProperty studentName;
    private final StringProperty studentEmail;
    private final StringProperty partName;
    private final LongProperty barcode;
    private final StringProperty description;
    private final StringProperty price;
    private final StringProperty location;
    private final StringProperty date;

    public FaultyPartTabTableRow(String studentName, String studentEmail, String partName, long barcode, String description, String price, String location, String date) {
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.partName = new SimpleStringProperty(partName);
        this.barcode = new SimpleLongProperty(barcode);
        this.description = new SimpleStringProperty(description);
        this.price = new SimpleStringProperty(price);
        this.location = new SimpleStringProperty(location);
        this.date = new SimpleStringProperty(date);
    }

    public StringProperty getLocation() {
        return location;
    }



    public void setLocation(String location) {
        this.location.set(location);
    }

    public StringProperty getStudentName() {
        return studentName;
    }


    public void setStudentName(String studentName) {
        this.studentName.set(studentName);
    }

    public StringProperty getStudentEmail() {
        return studentEmail;
    }


    public void setStudentEmail(String studentEmail) {
        this.studentEmail.set(studentEmail);
    }

    public StringProperty getPartName() {
        return partName;
    }


    public void setPartName(String partName) {
        this.partName.set(partName);
    }

    public LongProperty getBarcode() {
        return barcode;
    }


    public StringProperty getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty getPrice() {
        return price;
    }


    public void setPrice(String price) {
        this.price.set(price);
    }

    public StringProperty getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date.set(date);
    }



}
