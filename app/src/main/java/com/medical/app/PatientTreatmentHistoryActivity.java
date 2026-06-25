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
import com.medical.app.utils.AppointmentHelper;

import java.util.ArrayList;
import java.util.List;

// Screen where a patient sees the treatment details from all of their completed appointments.
public class PatientTreatmentHistoryActivity extends AppCompatActivity {

    private ListView lvTreatmentHistory;
    private ArrayAdapter<String> treatmentHistoryAdapter;
    // Holds the treatment text shown in each row of the list
    private final List<String> treatmentHistoryList = new ArrayList<>();

    private FirebaseAuth firebaseAuth;
    private DatabaseReference appointmentsDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_treatment_history);

        // Link the list from the layout
        lvTreatmentHistory = findViewById(R.id.lvTreatmentHistory);

        // Get Firebase Auth and point to the "Appointments" node
        firebaseAuth = FirebaseAuth.getInstance();
        appointmentsDatabaseReference = FirebaseDatabase.getInstance().getReference("Appointments");

        // Set up the adapter that feeds the row text into the ListView
        treatmentHistoryAdapter = new ArrayAdapter<>(
                this,
                R.layout.list_item_history_card,
                android.R.id.text1,
                treatmentHistoryList
        );
        lvTreatmentHistory.setAdapter(treatmentHistoryAdapter);

        loadTreatmentHistory();
    }

    // Reads all appointments and keeps only this patient's completed ones to show their treatments
    private void loadTreatmentHistory() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, R.string.error_not_logged_in, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String currentPatientId = currentUser.getUid();

        // Read the Appointments node once and filter the results in code
        appointmentsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot appointmentsSnapshot) {
                if (isFinishing()) {
                    return;
                }

                treatmentHistoryList.clear();

                if (appointmentsSnapshot.exists()) {
                    // Loop through every appointment and pick this patient's completed ones
                    for (DataSnapshot appointmentSnapshot : appointmentsSnapshot.getChildren()) {
                        Appointment appointment = appointmentSnapshot.getValue(Appointment.class);

                        if (appointment != null
                                && currentPatientId.equals(appointment.getPatientId())
                                && "Completed".equals(appointment.getStatus())) {

                            // Build the line of text showing the doctor, date, disease, prescription and progress
                            String displayText = getString(
                                    R.string.treatment_history_item,
                                    AppointmentHelper.safeText(appointment.getDoctorName()),
                                    AppointmentHelper.safeText(appointment.getDate()),
                                    AppointmentHelper.safeText(appointment.getDisease()),
                                    AppointmentHelper.safeText(appointment.getPrescription()),
                                    AppointmentHelper.safeText(appointment.getProgress())
                            );

                            treatmentHistoryList.add(displayText);
                        }
                    }
                }

                // Refresh the list, or tell the patient there is no treatment history yet
                treatmentHistoryAdapter.notifyDataSetChanged();

                if (treatmentHistoryList.isEmpty()) {
                    Toast.makeText(PatientTreatmentHistoryActivity.this,
                            R.string.no_treatment_history,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isFinishing()) {
                    return;
                }
                Toast.makeText(PatientTreatmentHistoryActivity.this,
                        R.string.error_appointments_load_failed,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
