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
import com.medical.app.models.Patient;

// Patient's home screen. Shows their profile and gives buttons to book appointments,
// view treatment/bill history, and log out.
public class PatientHomeActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvEmail;
    private TextView tvAge;
    private Button btnBookAppointment;
    private Button btnTreatmentHistory;
    private Button btnBillsHistory;
    private Button btnLogout;

    // Firebase Auth tells us who is logged in, the database holds their saved profile
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);

        // Link the profile text views and buttons from the layout
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvAge = findViewById(R.id.tvAge);
        btnBookAppointment = findViewById(R.id.btnBookAppointment);
        btnTreatmentHistory = findViewById(R.id.btnTreatmentHistory);
        btnBillsHistory = findViewById(R.id.btnBillsHistory);
        btnLogout = findViewById(R.id.btnLogout);

        // Get Firebase Auth and point to the "Users" node
        firebaseAuth = FirebaseAuth.getInstance();
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Load and show the logged-in patient's details
        loadPatientProfile();

        // Open the book-appointment screen
        btnBookAppointment.setOnClickListener(v ->
                startActivity(new Intent(this, BookAppointmentActivity.class)));

        // Open the treatment history screen
        btnTreatmentHistory.setOnClickListener(v ->
                startActivity(new Intent(this, PatientTreatmentHistoryActivity.class)));

        // Open the bill history screen
        btnBillsHistory.setOnClickListener(v ->
                startActivity(new Intent(this, PatientBillsHistoryActivity.class)));

        btnLogout.setOnClickListener(v -> logout());
    }

    // Finds the current user's ID and reads their profile from the database to show on screen
    private void loadPatientProfile() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // If somehow no one is logged in, send them back to the login screen
        if (currentUser == null) {
            Toast.makeText(this, R.string.error_not_logged_in, Toast.LENGTH_SHORT).show();
            navigateToLoginRegister();
            return;
        }

        String uid = currentUser.getUid();

        // Read this patient's record once from Users/{uid} and fill in the profile fields
        usersDatabaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Patient patient = snapshot.getValue(Patient.class);

                if (patient != null) {
                    tvName.setText(patient.getName());
                    tvEmail.setText(patient.getEmail());
                    tvAge.setText(String.valueOf(patient.getAge()));
                } else {
                    Toast.makeText(PatientHomeActivity.this,
                            R.string.error_profile_not_found,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PatientHomeActivity.this,
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
        Intent intent = new Intent(PatientHomeActivity.this, LoginRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
