package Database;

import HelperClasses.DatabaseHelper;
import HelperClasses.StageWrapper;
import InventoryController.StudentCheckIn;


import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CheckingOutPart {

    private final String url = Database.host + "/student_check_in";
    private final String addToCheckouts = "INSERT INTO checkout (partID, studentID, barcode, checkoutAt, dueAt)\n" +
            "VALUE(?,?,?,?,?);";
    private final String getPartIDtoAdd = "SELECT partID \n" +
            "FROM parts \n" +
            "WHERE barcode = ? \n" +
            "    AND isCheckedout = 0\n" +
            "    LIMIT 1";
    private final String getPartIDtoCheckin = "SELECT partID \n" +
            "FROM parts \n" +
            "WHERE barcode = ? \n" +
            "    AND isCheckedout = 1\n" +
            "    LIMIT 1";

    private final String setPartStatusCheckedOut = "UPDATE parts SET isCheckedOut = 1 WHERE partID = ?";
    private final String getCheckedOutItems = "select barcode, studentID from checkout \n" +
            "where checkinAt is NULL";

    private final String setPartStatusCheckedIn = "UPDATE parts SET isCheckedOut = 0 WHERE partID = ?";
    private final String setDate = "update checkout\n" +
            "set checkinAt =? \n" +
            "where checkoutID = ?";
    private final String getCheckoutIDFromPartID = "select checkoutID from checkout where (partID = ? and checkinAt is null) ";

    private DatabaseHelper helper = new DatabaseHelper();
    private List<CheckedOutPartsObject> checkedOutItems = new ArrayList<>();
    private StageWrapper stageWrapper = new StageWrapper();


    /**
     * Adds a new checkout item to the database
     * @param barcode
     * @param studentID
     */
    public void addNewCheckoutItem(int barcode, int studentID){
        if(barcodeExists(barcode)) {
            try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
                PreparedStatement statement = connection.prepareStatement(addToCheckouts);
                addNewCheckoutHelper(barcode, studentID, statement).execute();
                statement.close();
            } catch (SQLException e) {
                StudentCheckIn.logger.error("SQLException: Can't connect to the database when adding new checkout item.");
                throw new IllegalStateException("Cannot connect to the database", e);
            }
        }
        else {
            stageWrapper.errorAlert("Barcode was not found in database, part was not checked out");
        }
    }

    /**
     * Helper for adding checkout item to DB, also sets isCheckedout in parts table to 1
     * @param barcode Barcode of part
     * @param studentID Student ID entered
     * @param preparedStatement Statement to be executed
     * @return
     */
    private PreparedStatement addNewCheckoutHelper(int barcode, int studentID, PreparedStatement preparedStatement){
        int partID = getPartIDFromBarcode(barcode, getPartIDtoAdd);
        try {
            preparedStatement.setInt(1, partID);
            preparedStatement.setInt(2, studentID);
            preparedStatement.setInt(3, barcode);
            preparedStatement.setString(4, helper.getCurrentDate());
            preparedStatement.setString(5, helper.setDueDate());
        }catch (SQLException e){
            StudentCheckIn.logger.error("SQLException: Can't connect to the database.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        setPartStatus(partID, setPartStatusCheckedOut); //This will set the partID found above to a checked out status
        return preparedStatement;
    }

    /**
     * This method takes a barcode as parameter and returns the corresponding partID to be added to checkout table.
     * @param barcode barcode of part
     * @return Part ID to return
     */
    int getPartIDFromBarcode(int barcode, String status){
        int partID = 0;

        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(status);
            statement.setInt(1, barcode);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                partID = rs.getInt("partID");
            }
            statement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database when getting part ID from barcode.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return partID;
    }

    private boolean barcodeExists(int barcode){
        List<Integer> barcodes = new LinkedList<>();
        final String getAllBarcodes = "select barcode from parts";
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(getAllBarcodes);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                barcodes.add(rs.getInt("barcode"));
            }
            statement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database when checking if barcode exists.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return (barcodes.contains(barcode));
    }

    /**
     * Sets part checkout status to 1 to signify the part is checked out
     * @param partID Part ID of part
     */
    void setPartStatus(int partID, String status){
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(status);
            preparedStatement.setInt(1,partID);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database when setting part status.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    /**
     * Returns barcodes of items that are checked out
     * @return A list of barcodes in the checked out tab
     */
    public List<CheckedOutPartsObject> returnCheckedOutObjects(){
        if(checkedOutItems.size()!=0){
            checkedOutItems.clear();
        }
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(getCheckedOutItems);
            while(resultSet.next()){
                checkedOutItems.add(new CheckedOutPartsObject(resultSet.getInt("barcode"), resultSet.getInt("studentID")));
            }
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database.");
            throw new IllegalStateException("Cannot connect the database", e);
        }
        return checkedOutItems;
    }

    /**
     * Takes a partID and returns the corresponding checkoutID
     * @param partID PartID to be passed in
     * @return
     */
    int getCheckoutIDfromPartID(int partID){
        int checkoutID = 0;
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(getCheckoutIDFromPartID);
            statement.setInt(1, partID);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                checkoutID = rs.getInt("checkoutID");
            }
            statement.close();
        } catch (SQLException e) {
            StudentCheckIn.logger.error("SQLException: Can't connect to the database when getting checkout from part ID.");
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return checkoutID;
    }

    /**
     * Method to return an item to checked in status
     * @param barcode Barcode of item
     */
    public void setItemtoCheckedin(int barcode){
        int partID = getPartIDFromBarcode(barcode, getPartIDtoCheckin);
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement statement = connection.prepareStatement(setDate);
            statement.setString(1, helper.getCurrentDate());
            statement.setInt(2, getCheckoutIDfromPartID(partID));
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        setPartStatus(partID, setPartStatusCheckedIn); //Sets part to checkedin

    }
}
