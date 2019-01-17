package Database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * This class uses a query to edit a part in the database
 */
public class EditPart {
    private final String url = "jdbc:mysql://localhost:3306/student_check_in";
    private String editQuery = "UPDATE parts SET partName = ?, serialNumber = ?, manufacturer = ?, " +
            "price = ?, vendorID = ?, location = ?, barcode = ?, " +
            "updatedAt = ? WHERE partID = ?;";

    private String editAllQuery = "UPDATE parts SET partName = ?, serialNumber = ?, manufacturer = ?, " +
            "price = ?, vendorID = ?, location = ?, barcode = ?, " +
            "updatedAt = ? WHERE partName = ?;";

    VendorInformation vendorInformation = new VendorInformation();

    /**
     * This method edits an item in the database
     * @param part The part to be edited
     */
    public void editItem(Part part){
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(editQuery);
            preparedStatement = editQuery(part, preparedStatement);
            preparedStatement.execute();
            preparedStatement.close();
            vendorInformation.getVendorList();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

    /**
     * This method gets the current date
     * @return Current date
     */
    private String getCurrentDate(){
        return LocalDateTime.now().toString();
    }

    /**
     * This method sets the information from a part to the item being edited in the database
     * @param part The part being edited in the database
     * @param preparedStatement The statement that has items being set to it
     * @return the statement that has items being set to it
     */
    private PreparedStatement editQuery(Part part, PreparedStatement preparedStatement){
        try {
            preparedStatement.setString(1, part.getPartName());
            preparedStatement.setString(2, part.getSerialNumber());
            preparedStatement.setString(3, part.getManufacturer());
            preparedStatement.setDouble(4, part.getPrice());
            preparedStatement.setInt(5, vendorInformation.getVendorIDFromVendor(part.getVendor()));
            preparedStatement.setString(6, part.getLocation());
            preparedStatement.setString(7, part.getBarcode());
            preparedStatement.setString(8, getCurrentDate());
            preparedStatement.setString(9, "" + part.getPartID());
        }catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return preparedStatement;
    }

    /**
     * This method sets the information from a part to the item being edited in the database
     * @param part The part being edited in the database
     * @param preparedStatement The statement that has items being set to it
     * @return the statement that has items being set to it
     */
    private PreparedStatement editAllQuery(String originalPartName, Part part, PreparedStatement preparedStatement){
        try {
            preparedStatement.setString(1, part.getPartName());
            preparedStatement.setString(2, part.getSerialNumber());
            preparedStatement.setString(3, part.getManufacturer());
            preparedStatement.setDouble(4, part.getPrice());
            preparedStatement.setInt(5, vendorInformation.getVendorIDFromVendor(part.getVendor()));
            preparedStatement.setString(6, part.getLocation());
            preparedStatement.setString(7, part.getBarcode());
            preparedStatement.setString(8, getCurrentDate());
            preparedStatement.setString(9, originalPartName);
        }catch (SQLException e){
            throw new IllegalStateException("Cannot connect to the database", e);
        }
        return preparedStatement;
    }

    public void editAllOfType(String originalPartName, Part updatedPart) {
        try (Connection connection = DriverManager.getConnection(url, Database.username, Database.password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(editAllQuery);
            preparedStatement = editAllQuery(originalPartName, updatedPart, preparedStatement);
            preparedStatement.execute();
            preparedStatement.close();
            vendorInformation.getVendorList(); //NEEDED?
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database", e);
        }
    }

}
