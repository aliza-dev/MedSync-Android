package com.medical.app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medical.app.models.Appointment;
import com.medical.app.models.Doctor;
import com.medical.app.utils.AppointmentHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Screen where a doctor sees the completed appointments of all the patients they have treated.
public class DoctorPatientHistoryActivity extends AppCompatActivity {

    private ListView lvPatientHistory;
    private ArrayAdapter<String> patientHistoryAdapter;
    // Holds the history text shown in each row of the list
    private final List<String> patientHistoryList = new ArrayList<>();

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersDatabaseReference;
    private DatabaseReference appointmentsDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_patient_history);

        // Link the list from the layout
        lvPatientHistory = findViewById(R.id.lvPatientHistory);

        // Get Firebase Auth and point to the "Users" and "Appointments" nodes
        firebaseAuth = FirebaseAuth.getInstance();
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        appointmentsDatabaseReference = FirebaseDatabase.getInstance().getReference("Appointments");

        // Set up the adapter that feeds the row text into the ListView
        patientHistoryAdapter = new ArrayAdapter<>(
                this,
                R.layout.list_item_history_card,
                android.R.id.text1,
                patientHistoryList
        );
        lvPatientHistory.setAdapter(patientHistoryAdapter);

        loadPatientHistory();
    }

    // Step 1: read the logged-in doctor's name so we can match their appointments
    private void loadPatientHistory() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, R.string.error_not_logged_in, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String doctorUid = currentUser.getUid();

        // Read this doctor's profile once to get their name
        usersDatabaseReference.child(doctorUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot doctorSnapshot) {
                if (isFinishing()) {
                    return;
                }

                Doctor doctor = doctorSnapshot.getValue(Doctor.class);
                String doctorName = doctor != null ? doctor.getName() : null;
                loadUsersAndAppointments(doctorName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isFinishing()) {
                    return;
                }
                Toast.makeText(DoctorPatientHistoryActivity.this,
                        R.string.error_profile_load_failed,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Step 2: read all users once to build an ID -> patient name map for display
    private void loadUsersAndAppointments(String doctorName) {
        usersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                if (isFinishing()) {
                    return;
                }

                Map<String, String> patientNames = AppointmentHelper.loadPatientNames(usersSnapshot);
                fetchCompletedAppointments(patientNames, doctorName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isFinishing()) {
                    return;
                }
                Toast.makeText(DoctorPatientHistoryActivity.this,
                        R.string.error_profile_load_failed,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Step 3: read all appointments and keep this doctor's completed ones for the list
    private void fetchCompletedAppointments(Map<String, String> patientNames, String doctorName) {
        appointmentsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot appointmentsSnapshot) {
                if (isFinishing()) {
                    return;
                }

                patientHistoryList.clear();

                if (appointmentsSnapshot.exists()) {
                    // Loop through every appointment and keep this doctor's completed ones
                    for (DataSnapshot appointmentSnapshot : appointmentsSnapshot.getChildren()) {
                        Appointment appointment = appointmentSnapshot.getValue(Appointment.class);

                        // Skip anything that isn't a completed appointment
                        if (appointment == null || !"Completed".equals(appointment.getStatus())) {
                            continue;
                        }

                        // Skip appointments that belong to a different doctor
                        if (!AppointmentHelper.isAppointmentForDoctor(
                                doctorName, appointment.getDoctorName())) {
                            continue;
                        }

                        String patientName = patientNames.getOrDefault(
                                appointment.getPatientId(),
                                getString(R.string.unknown_patient)
                        );

                        // Build the line of text showing the patient, date, disease and prescription
                        String displayText = getString(
                                R.string.doctor_patient_history_item,
                                patientName,
                                AppointmentHelper.safeText(appointment.getDate()),
                                AppointmentHelper.safeText(appointment.getDisease()),
                                AppointmentHelper.safeText(appointment.getPrescription())
                        );

                        patientHistoryList.add(displayText);
                    }
                }

                // Refresh the list, or tell the doctor there is no history yet
                patientHistoryAdapter.notifyDataSetChanged();

                if (patientHistoryList.isEmpty()) {
                    Toast.makeText(DoctorPatientHistoryActivity.this,
                            R.string.no_patient_history,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isFinishing()) {
                    return;
                }
                Toast.makeText(DoctorPatientHistoryActivity.this,
                        R.string.error_appointments_load_failed,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
