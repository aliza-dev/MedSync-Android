package com.medical.app.models;

// Simple data class that represents a patient. Firebase reads and writes objects of this class
// directly to/from the database, so the field names must match the keys stored in the database.
public class Patient {

    private String name;
    private String email;
    private int age;
    private String role;

    // Firebase needs an empty constructor so it can create the object when reading from the database.
    // We default the role to "patient" so login knows which home screen to open.
    public Patient() {
        this.role = "patient";
    }

    // Used when registering a new patient from the form
    public Patient(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.role = "patient";
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
