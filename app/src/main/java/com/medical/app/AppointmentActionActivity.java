package com.medical.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
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

// Screen where a doctor reviews a pending appointment and either accepts or rejects it.
public class AppointmentActionActivity extends AppCompatActivity {

    private TextView tvPatientName;
    private TextView tvDoctorName;
    private TextView tvAppointmentDate;
    private TextView tvDisease;
    private Button btnAccept;
    private Button btnReject;

    // ID of the appointment we're acting on, plus the two database nodes we read/write
    private String appointmentId;
    private DatabaseReference appointmentsDatabaseReference;
    private DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_action);

        // Link the detail text views and buttons from the layout
        tvPatientName = findViewById(R.id.tvPatientName);
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvAppointmentDate = findViewById(R.id.tvAppointmentDate);
        tvDisease = findViewById(R.id.tvDisease);
        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);

        // Point to the "Appointments" and "Users" nodes in the database
        appointmentsDatabaseReference = FirebaseDatabase.getInstance().getReference("Appointments");
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Read which appointment to show, passed in from the previous list screen
        appointmentId = getIntent().getStringExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_ID);

        // If no ID came through, there's nothing to show, so close the screen
        if (TextUtils.isEmpty(appointmentId)) {
            Toast.makeText(this, R.string.error_appointment_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadAppointmentDetails();

        btnAccept.setOnClickListener(v -> acceptAppointment());
        btnReject.setOnClickListener(v -> rejectAppointment());
    }

    // Reads this appointment from Firebase and shows its details on screen
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
                            Toast.makeText(AppointmentActionActivity.this,
                                    R.string.error_appointment_not_found,
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        tvDoctorName.setText(getString(
                                R.string.doctor_name_label,
                                AppointmentHelper.safeText(appointment.getDoctorName())
                        ));
                        tvAppointmentDate.setText(getString(
                                R.string.appointment_date_label,
                                AppointmentHelper.safeText(appointment.getDate())
                        ));
                        tvDisease.setText(getString(
                                R.string.disease_label,
                                AppointmentHelper.safeText(appointment.getDisease())
                        ));

                        // The appointment only stores the patient's ID, so look up their name separately
                        loadPatientName(appointment.getPatientId());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AppointmentActionActivity.this,
                                R.string.error_appointments_load_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Looks up the patient's name from the Users node using their ID
    private void loadPatientName(String patientId) {
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

                        String name = snapshot.child("name").getValue(String.class);
                        String patientName = TextUtils.isEmpty(name)
                                ? getString(R.string.unknown_patient)
                                : name;

                        tvPatientName.setText(getString(
                                R.string.patient_name_label,
                                patientName
                        ));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AppointmentActionActivity.this,
                                R.string.error_profile_load_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Marks the appointment as Accepted and opens the detail screen to complete the visit
    private void acceptAppointment() {
        // Disable both buttons so the doctor can't accept and reject at once
        btnAccept.setEnabled(false);
        btnReject.setEnabled(false);

        // updateChildren only changes the status field and leaves the rest of the appointment untouched
        Map<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "Accepted");

        appointmentsDatabaseReference.child(appointmentId)
                .updateChildren(statusUpdate)
                .addOnCompleteListener(task -> {
                    btnAccept.setEnabled(true);
                    btnReject.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                R.string.appointment_accepted,
                                Toast.LENGTH_SHORT).show();

                        // Pass the same appointment ID forward so the detail screen knows which one to open
                        Intent intent = new Intent(AppointmentActionActivity.this,
                                AppointmentDetailActivity.class);
                        intent.putExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_ID, appointmentId);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this,
                                R.string.appointment_status_update_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Marks the appointment as Rejected and returns to the doctor's profile screen
    private void rejectAppointment() {
        btnAccept.setEnabled(false);
        btnReject.setEnabled(false);

        // updateChildren only changes the status field and leaves the rest of the appointment untouched
        Map<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "Rejected");

        appointmentsDatabaseReference.child(appointmentId)
                .updateChildren(statusUpdate)
                .addOnCompleteListener(task -> {
                    btnAccept.setEnabled(true);
                    btnReject.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                R.string.appointment_rejected,
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AppointmentActionActivity.this,
                                DoctorProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this,
                                R.string.appointment_status_update_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
