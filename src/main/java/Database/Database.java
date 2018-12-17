package Database;

import InventoryController.CheckedOutItems;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.sql.*;

public class Database {
    //DB root pass: Userpassword123
    public static final String username = "root";
    public static final String password = "Userpassword123";
    static String host = "jdbc:mysql://localhost:3306";
    static final String dbdriver = "com.mysql.jdbc.Driver";
    static final String dbname = "student_check_in";
    static Connection connection;

    /**
     * This creates a connection to the database
     *
     * @author Bailey Terry
     */
    public Database() {
        // Load the JDBC driver.
        // Library (.jar file) must be added to project build path.
        try {
            Class.forName(dbdriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }

        connection = null;
        try {
            connection = DriverManager.getConnection((host + "/" + dbname),
                    username, password);
            connection.setClientInfo("autoReconnect", "true");
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * This method uses an SQL query to get all items in the databse with a due date less than todays date
     *
     * @return a list of overdue items
     * @author Bailey Terry
     */
    public ObservableList<OverdueItem> getOverdue(ObservableList<OverdueItem> data) {
//        ObservableList<OverdueItem> data = FXCollections.observableArrayList();
        try {
            Date date = gettoday();
            String overdue = "select checkout_parts.partID, checkouts.studentID, students.studentName, students.email, parts.partName," +
                    " parts.serialNumber, checkout_parts.dueAt, parts.price/100 from checkout_parts " +
                    "left join parts on checkout_parts.partID = parts.partID " +
                    "left join checkouts on checkout_parts.checkoutID = checkouts.checkoutID " +
                    "left join students on checkouts.studentID = students.studentID " +
                    "where checkout_parts.dueAt < date('" + date.toString() + "');";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(overdue);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()) {
                data.add(new OverdueItem(resultSet.getInt("checkouts.studentID"), resultSet.getString("students.studentName"),
                        resultSet.getString("students.email"), resultSet.getString("parts.partName"),
                        resultSet.getString("parts.serialNumber"), resultSet.getString("checkout_parts.dueAt"),
                        resultSet.getString("parts.price/100")));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Helper method to get the current date
     *
     * @return todays date
     * @author Bailey Terry
     */
    private static Date gettoday() {
        long date = System.currentTimeMillis();
        return new Date(date);
    }

    /**
     * This uses an SQL query to soft delete an item from the database
     *
     * @param partID a unique part id
     * @author Bailey Terry
     */
    public void deleteItem(int partID) {
        try {
            String delete = "update parts p set p.deletedBy = 'root', p.isDeleted = 1, p.deletedAt = date('" + gettoday() + "') where p.partID = " + partID + ";";
            Statement statement = connection.createStatement();
            statement.executeUpdate(delete);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Notifications.create().title("Successful!").text("Part with ID = " + partID + " has been successfully deleted").hideAfter(new Duration(5000)).show();//.showWarning();
    }

    public Connection getConnection() {
        return connection;
    }

    public static ObservableList getHistory() {
        ObservableList<HistoryItems> data = FXCollections.observableArrayList();
        try {
            String historyQuery = "SELECT studentName, partName, serialNumber, location, " +
                    "checkoutQuantity - checkInQuantity AS 'quantity', CASE " +
                    "WHEN checkouts.checkoutAt < checkout_parts.checkedInAt " +
                    "THEN checkout_parts.checkedInAt ELSE checkouts.checkoutAt END AS 'date' " +
                    "FROM parts " +
                    "INNER JOIN checkout_parts ON parts.partID = checkout_parts.partID " +
                    "INNER JOIN checkouts ON checkout_parts.checkoutID = checkouts.checkoutID " +
                    "INNER JOIN students ON checkouts.studentID = students.studentID " +
                    "WHERE parts.deleted = 0 " +
                    "ORDER BY CASE " +
                    "WHEN checkouts.checkoutAt < checkout_parts.checkedInAt " +
                    "THEN checkout_parts.checkedInAt ELSE checkouts.checkoutAt END DESC;";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(historyQuery);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()) {
                data.add(new HistoryItems(resultSet.getString("studentName"), resultSet.getString("partName"),
                        resultSet.getString("serialNumber"), resultSet.getString("location"),
                        resultSet.getInt("quantity"), resultSet.getString("date")));
                resultSet.close();
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Helper method to get a specific part from the database
     *
     * @param partID unique id of the part
     * @return a Part
     * @author Bailey Terry
     */
    public Part selectPart(int partID) {
        String query = "select * from parts where partID = " + partID;
        Part part = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()) {
                part = new Part(resultSet.getString("partName"), resultSet.getString("serialNumber"),
                        resultSet.getString("manufacturer"), resultSet.getDouble("price"), resultSet.getString("vendorID"),
                        resultSet.getString("location"), resultSet.getString("barcode"), false,
                        resultSet.getInt("partID"), resultSet.getInt("isDeleted"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return part;
    }

    /**
     * Gets a student from the database based on their RFID
     *
     * @param ID RFID to search for
     * @return a student
     * @author Bailey Terry
     */
    public Student selectStudent(int ID){
        String query = "select * from students where studentID = " + ID;
        String coList = "select students.studentName, parts.partName, checkouts.checkoutAt, checkout_parts.checkoutQuantity, checkout_parts.dueAt  \n" +
                "from students\n" +
                "left join checkouts on students.studentID = checkouts.studentID\n" +
                "left join checkout_parts on checkouts.checkoutID = checkout_parts.checkoutID\n" +
                "left join parts on checkout_parts.partID = parts.partID where students.studentID = " + ID  + ";";
        String pList = "select students.studentName, parts.partName, checkouts.checkoutAt, checkout_parts.checkoutQuantity, checkouts.reservedAt, checkout_parts.dueAt\n" +
                "from students\n" +
                "left join checkouts on students.studentID = checkouts.studentID\n" +
                "left join checkout_parts on checkouts.checkoutID = checkout_parts.checkoutID\n" +
                "left join parts on checkout_parts.partID = parts.partID where students.studentID = " + ID + ";";
        Student student = null;
        String name = "";
        String email = "";
        int id = 0;
        ObservableList<CheckedOutItems> checkedOutItems = FXCollections.observableArrayList();
        ObservableList<OverdueItem> overdueItems = FXCollections.observableArrayList();
        ObservableList<SavedPart> savedParts = FXCollections.observableArrayList();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                name = resultSet.getString("studentName");
                email = resultSet.getString("email");
                id = resultSet.getInt("studentID");
            }
            resultSet.close();
            statement.close();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(coList);
            resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                checkedOutItems.add(new CheckedOutItems(resultSet.getString("students.studentName"),
                        resultSet.getString("parts.partName"), resultSet.getInt("checkout_parts.checkoutQuantity"),
                        resultSet.getString("checkouts.checkoutAt"), resultSet.getString("checkout_parts.dueAt")));
            }
            statement.close();
            resultSet.close();
            overdueItems = getOverdue(overdueItems);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(pList);
            resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()){
                savedParts.add(new SavedPart(resultSet.getString("students.studentName"),
                        resultSet.getString("parts.partName"), resultSet.getString("checkouts.checkoutAt"),
                        resultSet.getInt("checkout_parts.checkoutQuantity"), resultSet.getString("checkouts.checkoutAt"),
                        resultSet.getString("checkout_parts.dueAt")));
            }
            statement.close();
            resultSet.close();
            student = new Student(name,id,email,checkedOutItems,overdueItems,savedParts);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return student;
    }

}