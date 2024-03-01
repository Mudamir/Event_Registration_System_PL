package com.example.event_registration_system_pl;

public class EventRegistrationData {
    private static int lastAssignedID = 0;
    private String date;
    private String title;
    private String location;
    private String type;
    private int ID; // Include the eventID field

    public EventRegistrationData(String title, String type, String date, String location) {
        this.ID = ++lastAssignedID; // Initialize eventID
        this.date = date;
        this.title = title;
        this.location = location;
        this.type = type;
    }

    // Getters and setters (optional, but recommended)
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getEventID() {
        return ID;
    }
}


