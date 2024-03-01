package com.example.event_registration_system_pl;

import java.util.Collections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TableColumn;



import java.io.IOException;

public class EventRoster implements Initializable {

    @FXML
    private TableView<EventRosterData> Table_Rooster;

    @FXML
    private Button btn_Home;

    @FXML
    private Button btn_Ingress;

    @FXML
    private TableColumn<EventRosterData, String> clm_EventTitle;

    @FXML
    private TableColumn<EventRosterData, String> clm_ParticipantName;

    @FXML
    private TableColumn<EventRosterData, String> clm_date;

    @FXML
    private ComboBox<String> cmb_event;

    @FXML
    private Button btn_Delete;
    @FXML
    private Button btn_search;

    @FXML
    private ComboBox<String> cmb_participant;

    @FXML
    private TextField tb_date;

    @FXML
    private TextField tb_Search;

    // Declare originalData as a class member variable
    private ObservableList<EventRosterData> originalData;

    @FXML
    void DeleteRoster(ActionEvent event) {
        // Get the selected item from the TableView
        EventRosterData selectedRoster = Table_Rooster.getSelectionModel().getSelectedItem();

        // Check if an item is selected
        if (selectedRoster == null) {
            // If no item is selected, display an error message or handle this case differently
            System.out.println("Please select a row to delete.");
            return;
        }

        // Execute a DELETE SQL query to delete the corresponding record from the database
        deleteRecordFromDatabase(selectedRoster);

        // Update the TableView to reflect the changes
        Table_Rooster.getItems().remove(selectedRoster);
    }

    private void deleteRecordFromDatabase(EventRosterData data) {
        String url = "jdbc:mysql://localhost:3306/db_pl_finals";
        String username = "root";
        String password = "";

        String sql = "DELETE FROM tbl_event_roster WHERE Event_RosterID = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, data.getID());

            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Record deleted successfully.");
            } else {
                System.out.println("Failed to delete record from the database.");
            }
        } catch (SQLException e) {
            // You need to define showErrorAlert method to display error messages
            showErrorAlert("Error", "Error deleting record from the database: " + e.getMessage());
        }
    }



    @FXML
    void SearchRoster(ActionEvent event) {
        // Check if tb_Search is null

        // Get the search keyword from the text field
        String keyword = tb_Search.getText().trim().toLowerCase();

        // If the search keyword is empty, revert to the original table data
        if (keyword.isEmpty()) {
            ObservableList<EventRosterData> data = FXCollections.observableArrayList();
            // Fetch data from the database and add it to 'data' list
            data.addAll(fetchEventDataFromDatabase());

            // Set the data to the table view
            Table_Rooster.setItems(data);// Assuming originalData contains the unfiltered data
            return;
        }

        // Create a filtered list to store the filtered data
        FilteredList<EventRosterData> filteredData = new FilteredList<>(Table_Rooster.getItems());

        // Set a predicate to filter data based on all columns
        filteredData.setPredicate(roosterData -> {
            // Iterate over all columns in the table
            for (TableColumn<EventRosterData, ?> column : Table_Rooster.getColumns()) {
                // Get cell value for the current column
                String cellValue = column.getCellData(roosterData).toString().toLowerCase();
                // Check if the cell value contains the search keyword
                if (cellValue.contains(keyword)) {
                    return true; // If any column contains the keyword, include the row in filtered data
                }
            }
            return false; // If none of the columns contain the keyword, exclude the row from filtered data
        });

        // Set the table's items to the filtered list
        Table_Rooster.setItems(filteredData);
    }



    @FXML
    void ingresstoEvent(ActionEvent event) {
        String participant = cmb_participant.getValue();
        String date = tb_date.getText();
        String eventTitle = cmb_event.getValue();

        if (date.isEmpty() || participant == null || eventTitle == null) {
            showErrorAlert("Error", "Please fill in all participant details.");
            return;
        }

        // Check if the combination of participant, event, and date already exists
        if (isEventRosterAlreadyExists(participant, eventTitle, date)) {
            showErrorAlert("Error", "Event roster entry already exists.");
            return;
        }

        // If the entry doesn't exist, proceed with adding it
        EventRosterData rooster = new EventRosterData(participant, eventTitle, date);
        ObservableList<EventRosterData> items = Table_Rooster.getItems();
        items.add(rooster);

        saveEventRosterToDatabase(rooster);
    }

    private void saveEventRosterToDatabase(EventRosterData rooster) {
        String url = "jdbc:mysql://localhost:3306/db_pl_finals";
        String username = "root";
        String password = "";

        String sql = "INSERT INTO tbl_event_roster (Participant_Name, Event_Title, Event_Date) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rooster.getParticipantName());
            stmt.setString(2, rooster.getEventTitle());
            stmt.setString(3, rooster.getDate());

            stmt.executeUpdate();

            System.out.println("Event roster data saved to the database successfully.");
        } catch (SQLException e) {
            showErrorAlert("Error", "Error saving event roster data: " + e.getMessage());
        }
    }

    // Method to check if the event roster entry already exists in the table
    private boolean isEventRosterAlreadyExists(String participant, String event, String date) {
        ObservableList<EventRosterData> items = Table_Rooster.getItems();
        for (EventRosterData data : items) {
            if (data.getParticipantName().equals(participant) &&
                    data.getEventTitle().equals(event) &&
                    data.getDate().equals(date)) {
                return true; // Entry already exists
            }
        }
        return false; // Entry doesn't exist
    }


    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void toHomeScene(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
            Parent HomeScene = loader.load();
            Stage stage = (Stage) btn_Home.getScene().getWindow();
            Scene Home = new Scene(HomeScene);
            stage.setScene(Home);
        } catch (IOException e) {
            System.err.println("Error loading Home: " + e.getMessage());
        }
    }

    private void fillComboBoxWithParticipantNames() {
        // Connect to the database
        String url = "jdbc:mysql://localhost:3306/db_pl_finals";
        String username = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT Participant_Name FROM tbl_Participant";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Create an ObservableList to store participant names
            ObservableList<String> participantNames = FXCollections.observableArrayList();

            // Iterate through the result set and add participant names to the list
            while (rs.next()) {
                String participantName = rs.getString("Participant_Name");
                participantNames.add(participantName);
            }

            // Sort the participant names alphabetically
            Collections.sort(participantNames);

            // Set the items of the ComboBox to the sorted participant names
            cmb_participant.setItems(participantNames);

            // Add listener to filter items based on user input
            cmb_participant.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.isEmpty()) {
                    // If the text field is empty, show all items
                    cmb_participant.setItems(participantNames);
                } else {
                    // Filter items based on the new value
                    ObservableList<String> filteredList = FXCollections.observableArrayList();
                    for (String name : participantNames) {
                        if (name.toLowerCase().contains(newValue.toLowerCase())) {
                            filteredList.add(name);
                        }
                    }
                    cmb_participant.setItems(filteredList);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any potential errors
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate the cmb_event combo box with event titles
        populateEventComboBox();
        loadEventDataToTable();
        fillComboBoxWithParticipantNames();

        clm_date.setCellValueFactory(new PropertyValueFactory<>("Date"));
        clm_EventTitle.setCellValueFactory(new PropertyValueFactory<>("eventTitle"));
        clm_ParticipantName.setCellValueFactory(new PropertyValueFactory<>("participantName"));



        // Add event listener to cmb_event
        cmb_event.setOnAction(event -> {
            String selectedEventTitle = cmb_event.getValue();
            if (selectedEventTitle != null) {
                // Fetch the corresponding event date from the database
                String eventDate = fetchEventDateFromDatabase(selectedEventTitle);
                // Update the tb_date text field with the fetched event date
                tb_date.setText(eventDate);
            }
        });

    }

    private void populateEventComboBox() {
        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/db_pl_finals";
        String username = "root";
        String password = "";

        // SQL query to fetch event titles from the database
        String sql = "SELECT Title FROM tbl_event";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Create an observable list to store event titles
            ObservableList<String> eventTitles = FXCollections.observableArrayList();

            // Iterate through the result set and add event titles to the list
            while (rs.next()) {
                String eventTitle = rs.getString("Title");
                eventTitles.add(eventTitle);
            }

            // Set the items of cmb_event combo box to the list of event titles
            cmb_event.setItems(eventTitles);

            // Add listener to filter items based on user input
            cmb_event.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.isEmpty()) {
                    // If the text field is empty, show all items
                    cmb_event.setItems(eventTitles);
                } else {
                    // Filter items based on the new value
                    ObservableList<String> filteredList = FXCollections.observableArrayList();
                    for (String title : eventTitles) {
                        if (title.toLowerCase().contains(newValue.toLowerCase())) {
                            filteredList.add(title);
                        }
                    }
                    cmb_event.setItems(filteredList);
                }
            });

        } catch (SQLException e) {
            // Handle any SQL exceptions
            System.err.println("Error populating event combo box: " + e.getMessage());
        }
    }

    private String fetchEventDateFromDatabase(String eventTitle) {
        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/db_pl_finals";
        String username = "root";
        String password = "";

        // SQL query to fetch the event date based on the event title
        String sql = "SELECT Date FROM tbl_event WHERE Title = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the event title as a parameter in the prepared statement
            stmt.setString(1, eventTitle);

            // Execute the query and retrieve the result set
            try (ResultSet rs = stmt.executeQuery()) {
                // Check if there is a result
                if (rs.next()) {
                    // Retrieve the event date from the result set
                    return rs.getString("Date");
                }
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions
            System.err.println("Error fetching event date from database: " + e.getMessage());
        }

        // Return null if no event date is found
        return null;
    }

    private void loadEventDataToTable() {
        // Clear existing data in the TableView
        Table_Rooster.getItems().clear();

        // Retrieve data from the database and populate the table view
        ObservableList<EventRosterData> data = FXCollections.observableArrayList();
        // Fetch data from the database and add it to 'data' list
        data.addAll(fetchEventDataFromDatabase());

        // Set the data to the table view
        Table_Rooster.setItems(data);
    }

    private List<EventRosterData> fetchEventDataFromDatabase() {
        List<EventRosterData> dataList = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/db_pl_finals";
        String username = "root";
        String password = "";

        String sql = "SELECT * FROM tbl_event_roster";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String participantName = rs.getString("Participant_Name");
                String event = rs.getString("Event_Title");
                String date = rs.getString("Event_Date");

                EventRosterData eventData = new EventRosterData(participantName, event, date);

                dataList.add(eventData);
            }
        } catch (SQLException e) {
            showErrorAlert("Error", "Error fetching event roster data: " + e.getMessage());
        }

        return dataList;
    }
}
