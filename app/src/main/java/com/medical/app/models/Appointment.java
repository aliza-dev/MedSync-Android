package com.medical.app.models;

// Data class for one appointment. Firebase reads and writes objects of this class directly,
// so the field names must match the keys stored in the database.
public class Appointment {

    private String appointmentId;
    private String patientId;
    private String doctorName;
    private String date;
    private String disease;
    private String status;
    private String prescription;
    private String progress;
    private String billAmount;

    // Firebase needs an empty constructor so it can create the object when reading from the database.
    // A brand new appointment always starts as "Pending" until the doctor acts on it.
    public Appointment() {
        this.status = "Pending";
    }

    // Used when a patient books a new appointment; the treatment/bill fields are filled in later
    public Appointment(String appointmentId, String patientId, String doctorName,
                       String date, String disease) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorName = doctorName;
        this.date = date;
        this.disease = disease;
        this.status = "Pending";
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(String billAmount) {
        this.billAmount = billAmount;
    }
}
