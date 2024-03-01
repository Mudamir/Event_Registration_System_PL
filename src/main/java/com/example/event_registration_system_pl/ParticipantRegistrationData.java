package com.example.event_registration_system_pl;

public class ParticipantRegistrationData {
    private static int lastAssignedID = 0;

    private int ID;
    private String name;
    private String email;
    private String gender;
    private String contact;

    public ParticipantRegistrationData(String name, String email, String gender, String contact) {
        this.ID = ++lastAssignedID;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.contact = contact;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
