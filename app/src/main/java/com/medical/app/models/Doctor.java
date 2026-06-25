package com.medical.app.models;

// Simple data class that represents a doctor. Firebase reads and writes objects of this class
// directly to/from the database, so the field names must match the keys stored in the database.
public class Doctor {

    private String name;
    private String email;
    private String specialization;
    private String role;

    // Firebase needs an empty constructor so it can create the object when reading from the database.
    // We default the role to "doctor" so login knows which home screen to open.
    public Doctor() {
        this.role = "doctor";
    }

    // Used when registering a new doctor from the form
    public Doctor(String name, String email, String specialization) {
        this.name = name;
        this.email = email;
        this.specialization = specialization;
        this.role = "doctor";
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
