package com.medical.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medical.app.models.Appointment;
import com.medical.app.utils.AppointmentHelper;

import java.util.HashMap;
import java.util.Map;

// Screen where the doctor finishes an accepted appointment: they fill in the treatment details
// and bill amount, which marks the appointment as Completed.
public class AppointmentDetailActivity extends AppCompatActivity {

    // Key used to pass the appointment ID between screens via Intent
    public static final String EXTRA_APPOINTMENT_ID = "APPOINTMENT_ID";

    private TextView tvPatientName;
    private TextView tvPatientEmail;
    private TextView tvPatientAge;
    private TextView tvAppointmentDate;
    private EditText etDisease;
    private EditText etPrescription;
    private EditText etProgress;
    private EditText etBillAmount;
    private Button btnCompleteAppointment;

    // ID of the appointment being completed, plus the database nodes we read/write
    private String appointmentId;
    private DatabaseReference appointmentsDatabaseReference;
    private DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        // Link the text views, input fields and button from the layout
        tvPatientName = findViewById(R.id.tvPatientName);
        tvPatientEmail = findViewById(R.id.tvPatientEmail);
        tvPatientAge = findViewById(R.id.tvPatientAge);
        tvAppointmentDate = findViewById(R.id.tvAppointmentDate);
        etDisease = findViewById(R.id.etDisease);
        etPrescription = findViewById(R.id.etPrescription);
        etProgress = findViewById(R.id.etProgress);
        etBillAmount = findViewById(R.id.etBillAmount);
        btnCompleteAppointment = findViewById(R.id.btnCompleteAppointment);

        // Point to the "Appointments" and "Users" nodes in the database
        appointmentsDatabaseReference = FirebaseDatabase.getInstance().getReference("Appointments");
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Read which appointment to complete, passed in from the previous screen
        appointmentId = getIntent().getStringExtra(EXTRA_APPOINTMENT_ID);

        // If no ID came through, there's nothing to complete, so close the screen
        if (TextUtils.isEmpty(appointmentId)) {
            Toast.makeText(this, R.string.error_appointment_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadAppointmentDetails();
        btnCompleteAppointment.setOnClickListener(v -> completeAppointment());
    }

    // Reads this appointment from Firebase to show the date and pre-fill the disease field
    private void loadAppointmentDetails() {
        appointmentsDatabaseReference.child(appointmentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (isFinishing()) {
                            return;
                        }

                        Appointment appointment = snapshot.getValue(Appointment.class);

                        // If the appointment was deleted/not found, close the screen
                        if (appointment == null) {
                            Toast.makeText(AppointmentDetailActivity.this,
                                    R.string.error_appointment_not_found,
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        tvAppointmentDate.setText(getString(
                                R.string.appointment_date_label,
                                AppointmentHelper.safeText(appointment.getDate())
                        ));

                        // Pre-fill the disease the patient originally entered, if any
                        if (!TextUtils.isEmpty(appointment.getDisease())) {
                            etDisease.setText(appointment.getDisease());
                        }

                        // The appointment only stores the patient's ID, so load their profile next
                        loadPatientDetails(appointment.getPatientId());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AppointmentDetailActivity.this,
                                R.string.error_appointments_load_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Loads the patient's name, email and age from the Users node to show on screen
    private void loadPatientDetails(String patientId) {
        if (TextUtils.isEmpty(patientId)) {
            return;
        }

        usersDatabaseReference.child(patientId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (isFinishing()) {
                            return;
                        }

                        // Read each field directly since we only need a few values here
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        Long age = snapshot.child("age").getValue(Long.class);

                        tvPatientName.setText(getString(
                                R.string.patient_name_label,
                                AppointmentHelper.safeText(name)
                        ));
                        tvPatientEmail.setText(getString(
                                R.string.patient_email_label,
                                AppointmentHelper.safeText(email)
                        ));
                        tvPatientAge.setText(getString(
                                R.string.patient_age_label,
                                age != null ? age.intValue() : 0
                        ));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AppointmentDetailActivity.this,
                                R.string.error_profile_load_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Validates the treatment form, then updates the appointment as Completed with the bill
    private void completeAppointment() {
        String disease = etDisease.getText().toString().trim();
        String prescription = etPrescription.getText().toString().trim();
        String progress = etProgress.getText().toString().trim();
        String billAmount = etBillAmount.getText().toString().trim();

        // Stop and show an error if any field is empty
        if (TextUtils.isEmpty(disease)) {
            etDisease.setError(getString(R.string.error_disease_required));
            etDisease.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(prescription)) {
            etPrescription.setError(getString(R.string.error_prescription_required));
            etPrescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(progress)) {
            etProgress.setError(getString(R.string.error_progress_required));
            etProgress.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(billAmount)) {
            etBillAmount.setError(getString(R.string.error_bill_amount_required));
            etBillAmount.requestFocus();
            return;
        }

        // Disable the button so we don't submit the completion twice
        btnCompleteAppointment.setEnabled(false);

        // Collect the fields we want to save (status becomes Completed so it shows up in history/bills)
        Map<String, Object> appointmentUpdates = new HashMap<>();
        appointmentUpdates.put("status", "Completed");
        appointmentUpdates.put("disease", disease);
        appointmentUpdates.put("prescription", prescription);
        appointmentUpdates.put("progress", progress);
        appointmentUpdates.put("billAmount", billAmount);

        // updateChildren only changes these fields without overwriting the whole appointment
        appointmentsDatabaseReference.child(appointmentId)
                .updateChildren(appointmentUpdates)
                .addOnCompleteListener(task -> {
                    btnCompleteAppointment.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                R.string.appointment_complete_success,
                                Toast.LENGTH_SHORT).show();

                        // Go back to the doctor profile, clearing screens above it
                        Intent intent = new Intent(AppointmentDetailActivity.this,
                                DoctorProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this,
                                R.string.appointment_complete_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
