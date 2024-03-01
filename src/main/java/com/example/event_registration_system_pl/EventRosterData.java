package com.example.event_registration_system_pl;

public class EventRosterData {

    private static int lastAssignedId = 0;
    private int ID;
    private String participantName;
    private String eventTitle;
    private String date;

    // Constructor
    public EventRosterData(String participantName, String eventTitle, String date) {
        this.ID = ++lastAssignedId;
        this.participantName = participantName;
        this.eventTitle = eventTitle;
        this.date = date;
    }

    // Getters and Setters
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
