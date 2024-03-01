package com.example.event_registration_system_pl;


import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.ArrayList;
import java.util.List;
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
import javafx.collections.transformation.FilteredList;




public class ParticipantRegistration {

    @FXML
    private Button btn_search;
    @FXML
    private Button btn_Delete;

    @FXML
    private Button btn_Home;

    @FXML
    private Button btn_Update;

    @FXML
    private Button btn_cleartextbox;

    @FXML
    private Button btn_registerParticipant;

    @FXML
    private ComboBox<String> cmb_gender;

    @FXML
    private TextField tb_Name;

    @FXML
    private TextField tb_email;

    @FXML
    private TextField tb_searchParticipant;

    @FXML
    private TableColumn<ParticipantRegistrationData, String> clm_EventID;
    @FXML
    private TableColumn<ParticipantRegistrationData, String> clm_Name;

    @FXML
    private TableColumn<ParticipantRegistrationData, String> clm_Email;

    @FXML
    private TableColumn<ParticipantRegistrationData, String> clm_Gender;

    @FXML
    private TableColumn<ParticipantRegistrationData, String> clm_contact;

    @FXML
    private TextField tb_contact;

    @FXML
    private TableView<ParticipantRegistrationData> table_Participants;



    private ObservableList<ParticipantRegistrationData> data = FXCollections.observableArrayList();

    private FilteredList<ParticipantRegistrationData> filteredData = new FilteredList<>(data);



    @FXML
    void ClearTextBox(ActionEvent event) {
        tb_email.clear();
        tb_Name.clear();
        tb_contact.clear(); // Clear selection
        cmb_gender.setValue(null); // Clear selection
    }


    @FXML
    void UpdateRow(ActionEvent event) {
        ParticipantRegistrationData selectedParticipant = table_Participants.getSelectionModel().getSelectedItem();

        if (selectedParticipant == null) {
            System.out.println("Please select a participant to update.");
            return;
        }

        // Get the updated information from the text fields and combo boxes
        String name = tb_Name.getText();
        String email = tb_email.getText();
        String gender = cmb_gender.getValue();
        String contact = tb_contact.getText();

        // Update the selected participant's information
        selectedParticipant.setName(name);
        selectedParticipant.setEmail(email);
        selectedParticipant.setGender(gender);
        selectedParticipant.setContact(contact);

        if (name.isEmpty() || email.isEmpty() || gender == null || contact.isEmpty()) {
            System.out.println("Please fill in all participant details.");
            return;
        }

        // Call the updateParticipantInDatabase method to update the participant in the database
        if (updateParticipantInDatabase(selectedParticipant)) {
            // If the update is successful, clear the text fields and combo boxes
            ClearTextBox(null);
        }

    }

    private boolean updateParticipantInDatabase(ParticipantRegistrationData participant) {
        String url = "jdbc:mysql://localhost:3306/db_pl_finals";
        String username = "root";
        String password = "";

        String sql = "UPDATE tbl_participant SET Participant_name = ?, Participant_email = ?, Participant_gender = ?, Participant_Contact = ? WHERE Participant_ID = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, participant.getName());
            stmt.setString(2, participant.getEmail());
            stmt.setString(3, participant.getGender());
            stmt.setString(4, participant.getContact());
            stmt.setInt(5, participant.getID());

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Participant updated in the database successfully.");
                // Reload the data from the database to refresh the table view
                loadDataFromDatabase();
                return true;
            } else {
                System.out.println("Failed to update participant in the database.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error updating participant in the database: " + e.getMessage());
            return false;
        }
    }

    @FXML
    void handleRowClick(MouseEvent event) {
        if (event.getClickCount() == 1) { // Single click
            // Get the selected row
            ParticipantRegistrationData selectedParticipant = table_Participants.getSelectionModel().getSelectedItem();
            if (selectedParticipant != null) {
                // Populate the text fields and combo boxes with the selected row's data
                tb_Name.setText(selectedParticipant.getName());
                tb_email.setText(selectedParticipant.getEmail());
                cmb_gender.setValue(selectedParticipant.getGender());
                tb_contact.setText(selectedParticipant.getContact());
            }
        }
    }




    @FXML
    void MovetohomeScene(ActionEvent event) {
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

    @FXML
    void RegisterParticipant(ActionEvent event) {
        String name = tb_Name.getText();
        String email = tb_email.getText();
        String gender = cmb_gender.getValue();
        String contact = tb_contact.getText();

        if (name.isEmpty() || email.isEmpty() || gender == null || contact.isEmpty()) {
            showErrorAlert("Error","Please fill in all participant details.");
            return;
        }

        // Check if the name is already used
        if (isNameAlreadyUsed(name)) {
            showErrorAlert("Error","Name is already used. Please choose a different name.");
            return;
        }


        ParticipantRegistrationData participant = new ParticipantRegistrationData(name, email, gender, contact);
        ObservableList<ParticipantRegistrationData> items = table_Participants.getItems();
        items.add(participant);

        ClearTextBox(null);
        saveParticipantToDatabase(participant);


    }

    private boolean isNameAlreadyUsed(String name) {
        String url = "jdbc:mysql://localhost:3306/db_pl_finals";
        String username = "root";
        String password = "";

        String sql = "SELECT * FROM tbl_participant WHERE Participant_name = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // If there's at least one row, name is already used
        } catch (SQLException e) {
            showErrorAlert("Error", "Error checking name: " + e.getMessage());
            return true; // Assume error occurred, to be on the safe side
        }
    }



    public void initialize() {
        ObservableList<String> genderOptions = FXCollections.observableArrayList("Male", "Female");
        cmb_gender.setItems(genderOptions);

        // Initialize columns
        clm_Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        clm_Email.setCellValueFactory(new PropertyValueFactory<>("email"));
        clm_Gender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        clm_contact.setCellValueFactory(new PropertyValueFactory<>("contact"));


        loadDataFromDatabase();
    }

    private void saveParticipantToDatabase(ParticipantRegistrationData participant) {
        String url = "jdbc:mysql://localhost:3306/db_pl_finals";
        String username = "root";
        String password = "";

        String sql = "INSERT INTO tbl_participant (Participant_name, Participant_email, Participant_gender, Participant_Contact) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, participant.getName());
            stmt.setString(2, participant.getEmail());
            stmt.setString(3, participant.getGender());
            stmt.setString(4, participant.getContact());

            stmt.executeUpdate();

            System.out.println("Participant data saved to the database successfully.");
        } catch (SQLException e) {
            showErrorAlert("Error","Error saving participant data: " + e.getMessage());
        }
    }

    private List<ParticipantRegistrationData> fetchDataFromDatabase() {
        List<ParticipantRegistrationData> dataList = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/db_pl_finals";
        String username = "root";
        String password = "";

        String sql = "SELECT * FROM tbl_participant";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int participantID = rs.getInt("Participant_ID");
                String name = rs.getString("Participant_name");
                String email = rs.getString("Participant_email");
                String gender = rs.getString("Participant_gender");
                String contact = rs.getString("Participant_Contact");

                ParticipantRegistrationData participant = new ParticipantRegistrationData(name, email, gender, contact);
                participant.setID(participantID);

                dataList.add(participant);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching participant data: " + e.getMessage());
        }

        return dataList;
    }

    private void loadDataFromDatabase() {
        // Clear existing data in the TableView
        table_Participants.getItems().clear();

        // Retrieve data from the database and populate the table view
        ObservableList<ParticipantRegistrationData> data = FXCollections.observableArrayList();
        // Fetch data from the database and add it to 'data' list
        data.addAll(fetchDataFromDatabase());

        // Set the data to the table view
        table_Participants.setItems(data);

        filteredData = new FilteredList<>(data);
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void deleteParticipantFromDatabase(ParticipantRegistrationData participant) {
        String url = "jdbc:mysql://localhost:3306/db_pl_finals";
        String username = "root";
        String password = "";

        String sql = "DELETE FROM tbl_participant WHERE  Participant_ID = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, participant.getID());

            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Participant deleted from the database successfully.");
            } else {
                System.out.println("Failed to delete participant from the database.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting participant from the database: " + e.getMessage());
        }
    }

    @FXML
    void deleteParticipant(ActionEvent event) {
        // Get the selected row
        ParticipantRegistrationData selectedParticipant = table_Participants.getSelectionModel().getSelectedItem();
        if (selectedParticipant != null) {
            // Remove the selected participant from the TableView
            table_Participants.getItems().remove(selectedParticipant);
            // Delete the participant from the database
            deleteParticipantFromDatabase(selectedParticipant);
        } else {
            System.out.println("Please select a participant to delete.");
        }
    }

    @FXML
    void SearchName(ActionEvent event) {
        String searchText = tb_searchParticipant.getText().toLowerCase(); // Get the search text

        // Set the filter predicate based on the search text
        filteredData.setPredicate(participant -> {
            if (searchText == null || searchText.isEmpty()) {
                return true; // Display all data if search text is empty
            }
            // Filter by participant name (adjust as needed)
            return participant.getName().toLowerCase().contains(searchText);
        });

        // Update the TableView to reflect the filtered data
        table_Participants.setItems(filteredData);

    }


}


