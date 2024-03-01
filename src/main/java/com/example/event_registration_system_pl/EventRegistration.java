package com.example.event_registration_system_pl;

import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert;
import javafx.collections.transformation.FilteredList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.IOException;
import java.sql.ResultSet;


public class EventRegistration {

    @FXML
    private Button btn_EventRoster;

    @FXML
    private Button btn_Home;

    @FXML
    private Button btn_delete;

    @FXML
    private Button btn_registerEvent;

    @FXML
    private Button btn_search;

    @FXML
    private Button btn_update;

    @FXML
    private ComboBox<String> cmb_Type;

    @FXML
    private TextField tb_Date;

    @FXML
    private TextField tb_Location;

    @FXML
     private TextField tb_Title;

    @FXML
    private TextField tb_searchEvent;

    @FXML
    private TableColumn<EventRegistrationData, String> clm_Date;

    @FXML
    private TableColumn<EventRegistrationData, String> clm_location;

    @FXML
    private TableColumn<EventRegistrationData, String> clm_title;

    @FXML
    private TableColumn<EventRegistrationData, String> clm_type;
    @FXML
    private TableView<EventRegistrationData> table_Event;

    private int eventID;

    private final String url = "jdbc:mysql://localhost:3306/db_pl_finals";
    private final String username = "root";
    private final String password = "";

    @FXML
    void ClearTextBox() {
        tb_Date.clear();
        tb_Title.clear();
        tb_Location.clear();
        cmb_Type.setValue(null);
    }

    @FXML
    void MovetohomeScene(ActionEvent event) {
        try {
            // Load the FXML file for Scene 5 (Profit and Loss)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
            Parent HomeScene = loader.load();

            // Get the current stage
            Stage stage = (Stage) btn_Home.getScene().getWindow();

            // Create a new scene with Scene 5 content
            Scene Home = new Scene(HomeScene);

            // Set the new scene on the stage
            stage.setScene(Home);
        } catch (IOException e) {
            System.err.println("Error loading Home: " + e.getMessage());
        }
    }

    @FXML
    void RegisterEvent(ActionEvent event) {
        // Get the data from the text fields
        String date = tb_Date.getText();
        String title = tb_Title.getText();
        String location = tb_Location.getText();
        String type = cmb_Type.getValue();

        // Check if any field is empty
        if (date.isEmpty() || title.isEmpty() || location.isEmpty() || type == null) {
            // You may want to display an error message or handle this case differently
            System.out.println("Please fill in all fields.");
            return;
        }

        // Check if the event already exists in the database
        if (isEventAlreadyRegistered(title, date)) {
            showErrorAlert("Error","Event already exists.");
            // You may want to display an error message or handle this case differently
            return;
        }

        if (isEventTitleAlreadyRegistered(title)) {
            showErrorAlert("Error","Event already exists.");
            // You may want to display an error message or handle this case differently
            return;
        }


        // If the event doesn't exist, proceed with registration
        EventRegistrationData eventData = new EventRegistrationData(title, type, date, location);
        ObservableList<EventRegistrationData> items = table_Event.getItems();
        items.add(eventData);
        ClearTextBox();

        // Save the event data to the database
        saveEventToDatabase(eventData);
    }


    private void saveEventToDatabase(EventRegistrationData eventData) {

        String sql = "INSERT INTO tbl_event (Title, Type, Date, Location) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, eventData.getTitle());
            stmt.setString(2, eventData.getType());
            stmt.setString(3, eventData.getDate());
            stmt.setString(4, eventData.getLocation());

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Event registered successfully.");
            } else {
                System.out.println("Failed to register event.");
            }
        } catch (SQLException e) {
            showErrorAlert("Error", "Error saving event data to the database: " + e.getMessage());
        }
    }


    private void loadEventDataFromDatabase() {


        String sql = "SELECT Title, Type, Date, Location FROM tbl_event";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            ObservableList<EventRegistrationData> eventDataList = FXCollections.observableArrayList();

            while (rs.next()) {
                String title = rs.getString("title");
                String type = rs.getString("type");
                String date = rs.getString("date");
                String location = rs.getString("location");

                EventRegistrationData eventData = new EventRegistrationData(title, type, date, location);
                eventDataList.add(eventData);
            }

            // Populate table with loaded data
            table_Event.setItems(eventDataList);
        } catch (SQLException e) {
            showErrorAlert("Error", "Error loading event data from the database: " + e.getMessage());
        }
    }



    private boolean isEventAlreadyRegistered(String title, String date) {


        String sql = "SELECT * FROM tbl_event WHERE Title = ? AND Date = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, date);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // If there's at least one row, event is already registered
        } catch (SQLException e) {
            showErrorAlert("Error", "Error checking event registration: " + e.getMessage());
            return true; // Assume error occurred, to be on the safe side
        }
    }

    private boolean isEventTitleAlreadyRegistered(String title) {

        String sql = "SELECT * FROM tbl_event WHERE Title = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);

            ResultSet rs = stmt.executeQuery();

            // If any rows are returned, it means an event with the same title already exists
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any potential errors
            return false; // Assuming false indicates an error occurred
        }
    }


    @FXML
    void EventRosterScene(ActionEvent event) {
        try {
            // Load the FXML file for Scene 5 (Profit and Loss)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Event-Roster.fxml"));
            Parent EventRoster = loader.load();

            // Get the current stage
            Stage stage = (Stage) btn_EventRoster.getScene().getWindow();

            // Create a new scene with Scene 5 content
            Scene Roster = new Scene(EventRoster);

            // Set the new scene on the stage
            stage.setScene(Roster);
        } catch (IOException e) {
            System.err.println("Error loading Event Registration: " + e.getMessage());
        }
    }

    public void initialize() {

        clm_title.setCellValueFactory(new PropertyValueFactory<>("Title"));
        clm_type.setCellValueFactory(new PropertyValueFactory<>("Type"));
        clm_Date.setCellValueFactory(new PropertyValueFactory<>("Date"));
        clm_location.setCellValueFactory(new PropertyValueFactory<>("Location"));


        ObservableList<String> Rooms = FXCollections.observableArrayList(
                "Birthday parties",
                "Weddings",
                "Graduations" ,
                "Concerts" ,
                "Sporting events" ,
                "Conferences and conventions" ,
                "Music festivals" ,
                "Theater performances" ,
                "Art exhibitions" ,
                "Fundraising events" ,
                "Charity galas" ,
                "Product launches" ,
                "Trade shows" ,
                "Award ceremonies" ,
                "Networking events" ,
                "Family reunions" ,
                "Religious ceremonies" ,
                "Holiday celebrations " ,
                "Parades and marches" ,
                "Political rallies");
        cmb_Type.setItems(Rooms);

            // Populate table with data from the database
            loadEventDataFromDatabase();

    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void SearchEvent(ActionEvent event) {
        // Get the search keyword from the text field
        String keyword = tb_searchEvent.getText().toLowerCase();
        if (keyword == null || keyword.isEmpty()) {
            loadEventDataFromDatabase();
        }

        // Create a filtered list to store the filtered events
        FilteredList<EventRegistrationData> filteredEvents = new FilteredList<>(table_Event.getItems());

        // Set a predicate to filter events by title
        filteredEvents.setPredicate(eventData -> {
            // Convert the title to lowercase for case-insensitive comparison
            String title = eventData.getTitle().toLowerCase();
            return title.contains(keyword); // Filter events where the title contains the keyword
        });

        // Set the table's items to the filtered list
        table_Event.setItems(filteredEvents);
    }


    @FXML
    void DeleteEvent(ActionEvent event) {
        // Get the selected item from the table
        EventRegistrationData selectedEvent = table_Event.getSelectionModel().getSelectedItem();

        // Check if an item is selected
        if (selectedEvent == null) {
            // If no item is selected, display an error message or handle this case differently
            System.out.println("Please select an event to delete.");
            return;
        }

        // Remove the selected item from the table
        table_Event.getItems().remove(selectedEvent);

        // Delete the selected event from the database
        deleteEventFromDatabase(selectedEvent);
    }


    private void deleteEventFromDatabase(EventRegistrationData eventData) {
        String url = "jdbc:mysql://localhost:3306/db_pl_finals";
        String username = "root";
        String password = "";

        String sql = "DELETE FROM tbl_event WHERE Title = ? AND Type = ? AND Date = ? AND Location = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, eventData.getTitle());
            stmt.setString(2, eventData.getType());
            stmt.setString(3, eventData.getDate());
            stmt.setString(4, eventData.getLocation());

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Event deleted successfully.");
            } else {
                System.out.println("Failed to delete event.");
            }
        } catch (SQLException e) {
            showErrorAlert("Error", "Error deleting event data from the database: " + e.getMessage());
        }
    }



}
