package Database;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;

import java.util.Date;

public class OverdueItem extends RecursiveTreeObject {
    private SimpleIntegerProperty ID;
    private SimpleStringProperty part;
    private SimpleLongProperty barcode;
    private ObjectProperty<Date> date;
    private SimpleDoubleProperty price;
    private SimpleStringProperty name;
    private SimpleStringProperty email;
    private SimpleStringProperty checkID;

    public OverdueItem(int studentID, String name, String email, String partCon, long barcodeCon, Date date, String checkID) {
        this.ID = new SimpleIntegerProperty(studentID);
        this.part = new SimpleStringProperty(partCon);
        this.barcode = new SimpleLongProperty(barcodeCon);
        this.date = new SimpleObjectProperty<>(date);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.checkID = new SimpleStringProperty(checkID);
    }

    public OverdueItem(int studentID, String name, String email, String partCon, long barcodeCon, Date date, String checkID, double price) {
        this.ID = new SimpleIntegerProperty(studentID);
        this.part = new SimpleStringProperty(partCon);
        this.barcode = new SimpleLongProperty(barcodeCon);
        this.date = new SimpleObjectProperty<>(date);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.checkID = new SimpleStringProperty(checkID);
        this.price = new SimpleDoubleProperty(price);
    }
    public SimpleIntegerProperty getID() {
        return ID;
    }

    public SimpleIntegerProperty IDProperty() {
        return ID;
    }

    public void setID(int ID) {
        this.ID.set(ID);
    }

    public SimpleDoubleProperty getPrice() {
        return price;
    }


    public SimpleStringProperty getPart() {
        return part;
    }

    public SimpleStringProperty partProperty() {
        return part;
    }

    public void setPart(String part) {
        this.part.set(part);
    }

    public SimpleLongProperty getBarcode() {
        return barcode;
    }


    public ObjectProperty<Date> getDate() {
        return date;
    }

    public SimpleStringProperty getName() {
        return name;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public SimpleStringProperty getEmail() {
        return email;
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getCheckID() {
        return checkID.get();
    }

    public SimpleStringProperty checkIDProperty() {
        return checkID;
    }

    public void setCheckID(String checkID) {
        this.checkID.set(checkID);
    }
}
