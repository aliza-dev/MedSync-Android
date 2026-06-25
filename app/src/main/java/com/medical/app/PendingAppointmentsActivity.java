package com.medical.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

// Screen where a doctor sees the pending appointment requests addressed to them and can tap one to act on it.
public class PendingAppointmentsActivity extends AppCompatActivity {

    private ListView lvPendingAppointments;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private ArrayAdapter<String> appointmentsAdapter;

    // The text shown in each row, and a matching list of appointment IDs so a tap knows which one was clicked
    private final List<String> pendingAppointmentList = new ArrayList<>();
    private final List<String> pendingAppointmentIds = new ArrayList<>();

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersDatabaseReference;
    private DatabaseReference appointmentsDatabaseReference;
    private String loggedInDoctorName;
    private boolean doctorProfileLoaded;
    private boolean isActivityStarted;
    // Counter used to ignore results from older database reads if the user reopens the screen quickly
    private long patientNamesRequestId;
    private ValueEventListener appointmentsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_appointments);

        // Link the list, loading spinner and empty message from the layout
        lvPendingAppointments = findViewById(R.id.lvPendingAppointments);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        // Get Firebase Auth and point to the "Users" and "Appointments" nodes
        firebaseAuth = FirebaseAuth.getInstance();
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        appointmentsDatabaseReference = FirebaseDatabase.getInstance().getReference("Appointments");

        // Set up the adapter that feeds the row text into the ListView
        appointmentsAdapter = new ArrayAdapter<>(
                this,
                R.layout.list_item_pending_appointment_card,
                android.R.id.text1,
                pendingAppointmentList
        );
        lvPendingAppointments.setAdapter(appointmentsAdapter);

        // When a row is tapped, open the action screen for that appointment
        lvPendingAppointments.setOnItemClickListener((parent, view, position, id) -> {
            if (position < 0 || position >= pendingAppointmentIds.size()) {
                return;
            }

            String appointmentId = pendingAppointmentIds.get(position);
            if (TextUtils.isEmpty(appointmentId)) {
                return;
            }

            // Pass the chosen appointment's ID to the next screen
            Intent intent = new Intent(PendingAppointmentsActivity.this,
                    AppointmentActionActivity.class);
            intent.putExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_ID, appointmentId);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityStarted = true;

        // Make sure a doctor is logged in before loading anything
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, R.string.error_not_logged_in, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showLoading(true);

        // Load the doctor's name once, then start listening for appointments
        if (!doctorProfileLoaded) {
            loadDoctorProfile(currentUser.getUid());
        } else {
            attachAppointmentsListener();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop listening when the screen isn't visible so we don't waste resources or update a dead screen
        isActivityStarted = false;
        detachAppointmentsListener();
        patientNamesRequestId++;
    }

    // Reads the logged-in doctor's name so we can match appointments that belong to them
    private void loadDoctorProfile(String doctorUid) {
        usersDatabaseReference.child(doctorUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot doctorSnapshot) {
                        if (!isActivityStarted || isFinishing()) {
                            return;
                        }

                        Doctor doctor = doctorSnapshot.getValue(Doctor.class);
                        loggedInDoctorName = doctor != null && !TextUtils.isEmpty(doctor.getName())
                                ? doctor.getName().trim()
                                : "";
                        doctorProfileLoaded = true;
                        attachAppointmentsListener();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (!isActivityStarted || isFinishing()) {
                            return;
                        }
                        showLoading(false);
                        Toast.makeText(PendingAppointmentsActivity.this,
                                R.string.error_profile_load_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Starts a live listener that re-runs whenever the appointments in the database change
    private void attachAppointmentsListener() {
        if (!isActivityStarted || isFinishing() || appointmentsListener != null) {
            return;
        }

        // Without the doctor's name we can't match appointments, so just show an empty list
        if (TextUtils.isEmpty(loggedInDoctorName)) {
            showLoading(false);
            bindAppointments(new ArrayList<>(), new ArrayList<>());
            return;
        }

        appointmentsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot appointmentsSnapshot) {
                if (!isActivityStarted || isFinishing()) {
                    return;
                }
                loadPatientNamesAndBind(appointmentsSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isActivityStarted || isFinishing()) {
                    return;
                }
                showLoading(false);
                Toast.makeText(PendingAppointmentsActivity.this,
                        R.string.error_appointments_load_failed,
                        Toast.LENGTH_SHORT).show();
            }
        };

        // Listen to the whole Appointments node and filter for this doctor in code
        appointmentsDatabaseReference.addValueEventListener(appointmentsListener);
    }

    // Removes the live listener so it stops firing once we leave the screen
    private void detachAppointmentsListener() {
        if (appointmentsListener != null) {
            appointmentsDatabaseReference.removeEventListener(appointmentsListener);
            appointmentsListener = null;
        }
    }

    // Loads all user names once, then builds the visible list of this doctor's pending appointments
    private void loadPatientNamesAndBind(DataSnapshot appointmentsSnapshot) {
        // Tag this read so a slower, older read can't overwrite a newer one
        final long requestId = ++patientNamesRequestId;

        usersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                if (!isActivityStarted || isFinishing() || requestId != patientNamesRequestId) {
                    return;
                }

                // Build an ID -> patient name map so we can show names instead of raw IDs
                Map<String, String> patientNames = AppointmentHelper.loadPatientNames(usersSnapshot);
                List<String> displayItems = new ArrayList<>();
                List<String> appointmentIds = new ArrayList<>();

                if (appointmentsSnapshot.exists()) {
                    // Go through every appointment and keep only this doctor's pending ones
                    for (DataSnapshot appointmentSnapshot : appointmentsSnapshot.getChildren()) {
                        Appointment appointment = appointmentSnapshot.getValue(Appointment.class);
                        String appointmentId = appointmentSnapshot.getKey();

                        if (appointment == null || TextUtils.isEmpty(appointmentId)) {
                            continue;
                        }

                        // Skip anything that isn't still pending
                        if (!AppointmentHelper.isPendingStatus(appointment.getStatus())) {
                            continue;
                        }

                        // Skip appointments meant for a different doctor
                        if (!AppointmentHelper.isAppointmentForDoctor(
                                loggedInDoctorName, appointment.getDoctorName())) {
                            continue;
                        }

                        String patientName = patientNames.getOrDefault(
                                appointment.getPatientId(),
                                getString(R.string.unknown_patient)
                        );

                        displayItems.add(getString(
                                R.string.appointment_list_item,
                                patientName,
                                AppointmentHelper.safeText(appointment.getDate()),
                                AppointmentHelper.safeText(appointment.getDisease())
                        ));
                        appointmentIds.add(appointmentId);
                    }
                }

                bindAppointments(displayItems, appointmentIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isActivityStarted || isFinishing() || requestId != patientNamesRequestId) {
                    return;
                }
                showLoading(false);
                Toast.makeText(PendingAppointmentsActivity.this,
                        R.string.error_profile_load_failed,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Swaps in the freshly built data and refreshes the ListView
    private void bindAppointments(List<String> displayItems, List<String> appointmentIds) {
        pendingAppointmentList.clear();
        pendingAppointmentList.addAll(displayItems);
        pendingAppointmentIds.clear();
        pendingAppointmentIds.addAll(appointmentIds);

        // Tell the adapter the data changed so the list redraws
        appointmentsAdapter.notifyDataSetChanged();
        showLoading(false);
        updateEmptyState();
    }

    // Shows or hides the loading spinner
    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    // Shows the "nothing here" message only when the list is empty
    private void updateEmptyState() {
        boolean isEmpty = pendingAppointmentList.isEmpty();
        tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }
}
