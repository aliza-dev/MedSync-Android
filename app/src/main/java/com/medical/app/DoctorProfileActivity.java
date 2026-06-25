package com.medical.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
import com.medical.app.models.Doctor;

// Doctor's home screen. Shows their profile and gives buttons to view pending appointments,
// today's appointments, patient history, and log out.
public class DoctorProfileActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvEmail;
    private TextView tvSpecialization;
    private Button btnViewPendingAppointments;
    private Button btnTodaysAppointments;
    private Button btnViewPatientHistory;
    private Button btnLogout;

    // Firebase Auth tells us who is logged in, the database holds their saved profile
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        // Link the profile text views and buttons from the layout
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvSpecialization = findViewById(R.id.tvSpecialization);
        btnViewPendingAppointments = findViewById(R.id.btnViewPendingAppointments);
        btnTodaysAppointments = findViewById(R.id.btnTodaysAppointments);
        btnViewPatientHistory = findViewById(R.id.btnViewPatientHistory);
        btnLogout = findViewById(R.id.btnLogout);

        // Get Firebase Auth and point to the "Users" node
        firebaseAuth = FirebaseAuth.getInstance();
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Load and show the logged-in doctor's details
        loadDoctorProfile();

        // Open the list of pending appointment requests
        btnViewPendingAppointments.setOnClickListener(v ->
                startActivity(new Intent(this, PendingAppointmentsActivity.class)));

        // Open the list of today's appointments
        btnTodaysAppointments.setOnClickListener(v ->
                startActivity(new Intent(this, TodaysAppointmentsActivity.class)));

        // Open the completed patient history
        btnViewPatientHistory.setOnClickListener(v ->
                startActivity(new Intent(this, DoctorPatientHistoryActivity.class)));

        btnLogout.setOnClickListener(v -> logout());
    }

    // Finds the current user's ID and reads their profile from the database to show on screen
    private void loadDoctorProfile() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // If somehow no one is logged in, send them back to the login screen
        if (currentUser == null) {
            Toast.makeText(this, R.string.error_not_logged_in, Toast.LENGTH_SHORT).show();
            navigateToLoginRegister();
            return;
        }

        String uid = currentUser.getUid();

        // Read this doctor's record once from Users/{uid} and fill in the profile fields
        usersDatabaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Doctor doctor = snapshot.getValue(Doctor.class);

                if (doctor != null) {
                    tvName.setText(doctor.getName());
                    tvEmail.setText(doctor.getEmail());
                    tvSpecialization.setText(doctor.getSpecialization());
                } else {
                    Toast.makeText(DoctorProfileActivity.this,
                            R.string.error_profile_not_found,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorProfileActivity.this,
                        R.string.error_profile_load_failed,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Logs the user out of Firebase and returns to the start screen
    private void logout() {
        firebaseAuth.signOut();
        navigateToLoginRegister();
    }

    // Opens the login/register screen and clears the back stack so the user can't return after logout
    private void navigateToLoginRegister() {
        Intent intent = new Intent(DoctorProfileActivity.this, LoginRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
