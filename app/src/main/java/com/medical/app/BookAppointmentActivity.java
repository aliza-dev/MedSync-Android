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
import com.medical.app.models.Appointment;

// Screen where a patient books a new appointment by entering the doctor's name, date and problem.
public class BookAppointmentActivity extends AppCompatActivity {

    private EditText etDoctorName;
    private EditText etDate;
    private EditText etDisease;
    private Button btnSubmitRequest;

    // Auth gives us the logged-in patient's ID, the database stores the appointment request
    private FirebaseAuth firebaseAuth;
    private DatabaseReference appointmentsDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        // Link the input fields and button from the layout
        etDoctorName = findViewById(R.id.etDoctorName);
        etDate = findViewById(R.id.etDate);
        etDisease = findViewById(R.id.etDisease);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);

        // Get Firebase Auth and point to the "Appointments" node
        firebaseAuth = FirebaseAuth.getInstance();
        appointmentsDatabaseReference = FirebaseDatabase.getInstance().getReference("Appointments");

        btnSubmitRequest.setOnClickListener(v -> submitAppointmentRequest());
    }

    // Checks the form, saves the new appointment to Firebase, then goes back to the patient home
    private void submitAppointmentRequest() {
        String doctorName = etDoctorName.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String disease = etDisease.getText().toString().trim();

        // Stop and show an error if any field is empty
        if (TextUtils.isEmpty(doctorName)) {
            etDoctorName.setError(getString(R.string.error_doctor_name_required));
            etDoctorName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(date)) {
            etDate.setError(getString(R.string.error_date_required));
            etDate.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(disease)) {
            etDisease.setError(getString(R.string.error_disease_required));
            etDisease.requestFocus();
            return;
        }

        // We need the patient's ID to link the appointment to them
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, R.string.error_not_logged_in, Toast.LENGTH_SHORT).show();
            return;
        }

        String patientId = currentUser.getUid();

        // Disable the button so the request isn't submitted twice
        btnSubmitRequest.setEnabled(false);

        // push() makes a new unique key under "Appointments" for this booking
        DatabaseReference newAppointmentRef = appointmentsDatabaseReference.push();
        String appointmentId = newAppointmentRef.getKey();

        // Build the appointment object (its status starts as "Pending" by default)
        Appointment appointment = new Appointment(
                appointmentId,
                patientId,
                doctorName,
                date,
                disease
        );

        // Save the appointment under its unique key in the database
        newAppointmentRef.setValue(appointment)
                .addOnCompleteListener(task -> {
                    btnSubmitRequest.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                R.string.appointment_success,
                                Toast.LENGTH_SHORT).show();

                        // Return to the patient home screen and close this one
                        startActivity(new Intent(BookAppointmentActivity.this,
                                PatientHomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this,
                                R.string.appointment_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
