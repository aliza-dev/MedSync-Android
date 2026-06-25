package com.medical.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
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

// One login screen for both patients and doctors. After login we read the user's role
// from the database and send them to the correct home screen.
public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;

    // Firebase Auth checks the email/password, the database stores the user's profile and role
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_login);

        // Link the input fields and button from the layout
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Get Firebase Auth and point to the "Users" node in the Realtime Database
        firebaseAuth = FirebaseAuth.getInstance();
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        btnLogin.setOnClickListener(v -> loginUser());
    }

    // Reads the typed email/password, validates them, then asks Firebase to log the user in
    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Make sure neither field is empty before contacting Firebase
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_email_required));
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_password_required));
            etPassword.requestFocus();
            return;
        }

        // Disable the button so the user can't tap login twice while it's working
        btnLogin.setEnabled(false);

        // Ask Firebase Auth to sign in with this email and password
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // If sign-in failed, re-enable the button and show why
                    if (!task.isSuccessful()) {
                        btnLogin.setEnabled(true);
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : getString(R.string.login_failed);
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser == null) {
                        btnLogin.setEnabled(true);
                        Toast.makeText(this, R.string.error_not_logged_in, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Login worked, now find out if this user is a doctor or a patient
                    fetchUserRoleAndNavigate(currentUser.getUid());
                });
    }

    // Looks up the user in the database by their ID and opens the right home screen for their role
    private void fetchUserRoleAndNavigate(String uid) {
        // Read this user's record once from Users/{uid}
        usersDatabaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                btnLogin.setEnabled(true);

                // If there's no profile saved, the account isn't usable, so sign back out
                if (!snapshot.exists()) {
                    Toast.makeText(LoginActivity.this,
                            R.string.error_profile_not_found,
                            Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    return;
                }

                // Read the saved role and route to the matching screen
                String role = snapshot.child("role").getValue(String.class);

                if ("doctor".equals(role)) {
                    startActivity(new Intent(LoginActivity.this, DoctorProfileActivity.class));
                    finish();
                } else if ("patient".equals(role)) {
                    startActivity(new Intent(LoginActivity.this, PatientHomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this,
                            R.string.error_unknown_role,
                            Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Database read failed, so cancel the login and let the user try again
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this,
                        R.string.error_profile_load_failed,
                        Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
            }
        });
    }
}
