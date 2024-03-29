package Database.ObjectClasses;

import Database.AddPart;
import javafx.beans.property.*;

public class Part {

    private SimpleStringProperty partName, serialNumber, manufacturer, vendor, location, faultDesc;
    private final SimpleLongProperty barcode;
    private SimpleDoubleProperty price;
    private SimpleIntegerProperty partID, quantity;
    private final SimpleBooleanProperty fault, checkedOut = new SimpleBooleanProperty(false);
    AddPart addPart = new AddPart();


    //TODO Combine all these constructors I mean my god why do we have three that do the same thing


    public Part(String partName, String serialNumber, String manufacturer, double price, String vendor, String location, long barcode, boolean fault, int partID) {
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.manufacturer = new SimpleStringProperty(manufacturer);
        this.price = new SimpleDoubleProperty(price);
        this.vendor = new SimpleStringProperty(vendor);
        this.location = new SimpleStringProperty(location);
        this.barcode = new SimpleLongProperty(barcode);
        this.fault = new SimpleBooleanProperty(fault);
        this.partID = new SimpleIntegerProperty(partID);
        this.quantity = new SimpleIntegerProperty(0);
        this.faultDesc = new SimpleStringProperty("");
    }

    public Part(String partName, String serialNumber, String manufacturer, double price, String vendor, String location, long barcode, int quantity) {
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.manufacturer = new SimpleStringProperty(manufacturer);
        this.price = new SimpleDoubleProperty(price);
        this.vendor = new SimpleStringProperty(vendor);
        this.location = new SimpleStringProperty(location);
        this.barcode = new SimpleLongProperty(barcode);
        this.quantity = new SimpleIntegerProperty(quantity);
        //Returns the next part id
        this.partID = new SimpleIntegerProperty(addPart.getPartID());
        this.fault = new SimpleBooleanProperty(false);
        this.faultDesc = new SimpleStringProperty("");
    }

    public Part(String partName, String serialNumber, String location, long barcode, boolean fault, int partID, double price) {
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);

        this.location = new SimpleStringProperty(location);
        this.barcode = new SimpleLongProperty(barcode);
        this.fault = new SimpleBooleanProperty(fault);
        this.partID = new SimpleIntegerProperty(partID);
        this.price = new SimpleDoubleProperty(price);

    }

    public String getPartName() {
        return partName.get();
    }

    public void setPartName(String name) {
        this.partName.set(name);
    }

    public String getSerialNumber() {
        return serialNumber.get();
    }

    public void setSerialNumber(String serial) {
        this.serialNumber.set(serial);
    }

    public String getManufacturer() {
        return manufacturer.get();
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer.set(manufacturer);
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public String getVendor() {
        return vendor.get();
    }

    public void setVendor(String vendor) {
        this.vendor.set(vendor);
    }

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public Long getBarcode() {
        return barcode.get();
    }

    public void setBarcode(long barcode) {
        this.barcode.set(barcode);
    }

    public boolean getFault() {
        return fault.get();
    }

    public SimpleBooleanProperty faultProperty() {
        return fault;
    }

    public void setFault(boolean fault) {
        this.fault.set(fault);
    }

    public void setCheckedOut(int checkedOut) {
        this.checkedOut.set(checkedOut == 1 ? true : false);

    }

    public boolean getCheckedOut() {
        return checkedOut.get();
    }

    public int getPartID() {
        return partID.get();
    }

    public void setPartID(int partId) {
        this.partID.set(partId);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public String getFaultDesc() {
        return faultDesc.get();
    }

    public void setFaultDesc(String faultDesc) {
        this.faultDesc.set(faultDesc);
    }

    public void update(String partName, String serialNumber, String manufacturer, double price, String vendor, String location, long barcode) {
        this.partName.set(partName);
        this.serialNumber.set(serialNumber);
        this.manufacturer.set(manufacturer);
        this.price.set(price);
        this.vendor.set(vendor);
        this.location.set(location);
        this.barcode.set(barcode);
    }

    @Override
    public String toString() {
        return "Part Name: " + getPartName() + "\tSerial Number: " + getSerialNumber() + "\tManufacturer: " + getManufacturer() +
                "\tPrice: " + getPrice() + "\tVendor: " + getVendor() +
                "\tLocation: " + getLocation() + "\tBarcode: " + getBarcode() + "\tFault: " + getFault() +
                "\tFault Description: " + getFaultDesc() + "\tPart ID: " + getPartID() + "\tIs Deleted: "
                + "\tIs Checked Out: " + getCheckedOut() + "\n";
    }
}