package com.example.event_registration_system_pl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Home {

    @FXML
    private AnchorPane HomeAchorPane;

    @FXML
    private Button btn_event;

    @FXML
    private Button btn_participant;

    @FXML
    void toEventScene(ActionEvent event) {
        try {
            // Load the FXML file for Scene 5 (Profit and Loss)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Event-Registration.fxml"));
            Parent EventRegistration = loader.load();

            // Get the current stage
            Stage stage = (Stage) btn_event.getScene().getWindow();

            // Create a new scene with Scene 5 content
            Scene Event = new Scene(EventRegistration);

            // Set the new scene on the stage
            stage.setScene(Event);
        } catch (IOException e) {
            System.err.println("Error loading Event Registration: " + e.getMessage());
        }

    }

    @FXML
    void toParticipantScene(ActionEvent event) {
        try {
            // Load the FXML file for Scene 5 (Profit and Loss)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Participant-Registration.fxml"));
            Parent ParticipantRegistration = loader.load();

            // Get the current stage
            Stage stage = (Stage) btn_participant.getScene().getWindow();

            // Create a new scene with Scene 5 content
            Scene Participant = new Scene(ParticipantRegistration);

            // Set the new scene on the stage
            stage.setScene(Participant);
        } catch (IOException e) {
            System.err.println("Error loading Event Registration: " + e.getMessage());
        }
    }


}
