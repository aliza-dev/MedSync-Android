package com.medical.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.medical.app.models.Patient;

// Screen where a new patient creates an account with their name, email, age and password.
public class PatientRegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etAge;
    private Button btnRegister;

    // Firebase Auth creates the login account, the database stores the patient's profile details
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_register);

        // Link the input fields and button from the layout
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etAge = findViewById(R.id.etAge);
        btnRegister = findViewById(R.id.btnRegister);

        // Get Firebase Auth and point to the "Users" node where profiles are stored
        firebaseAuth = FirebaseAuth.getInstance();
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        btnRegister.setOnClickListener(v -> registerPatient());
    }

    // Checks the form, creates the account, saves the patient, then opens the patient home screen
    private void registerPatient() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String ageText = etAge.getText().toString().trim();

        // Stop and show an error if any required field is empty or the password is too short
        if (TextUtils.isEmpty(name)) {
            etName.setError(getString(R.string.error_name_required));
            etName.requestFocus();
            return;
        }

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

        // Firebase requires passwords to be at least 6 characters
        if (password.length() < 6) {
            etPassword.setError(getString(R.string.error_password_length));
            etPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(ageText)) {
            etAge.setError(getString(R.string.error_age_required));
            etAge.requestFocus();
            return;
        }

        // Age is stored as a number, so convert the typed text to an int
        int age = Integer.parseInt(ageText);

        // Disable the button so registration isn't submitted twice
        btnRegister.setEnabled(false);

        // Step 1: create the login account with Firebase Auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    btnRegister.setEnabled(true);

                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            // Step 2: save the patient profile under their unique user ID in the database
                            Patient patient = new Patient(name, email, age);

                            usersDatabaseReference
                                    .child(firebaseUser.getUid())
                                    .setValue(patient)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(this,
                                                    R.string.register_success,
                                                    Toast.LENGTH_SHORT).show();

                                            // Go to the home screen and close this one so Back doesn't return here
                                            startActivity(new Intent(
                                                    PatientRegisterActivity.this,
                                                    PatientHomeActivity.class));
                                            finish();
                                        } else {
                                            // Account was made but saving the profile failed
                                            Toast.makeText(this,
                                                    R.string.register_db_failed,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // Account creation failed (e.g. email already used), show Firebase's reason
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : getString(R.string.register_auth_failed);
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
