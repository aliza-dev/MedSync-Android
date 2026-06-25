package com.medical.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// First screen the app opens on. It just lets the user pick login, patient sign-up, or doctor sign-up.
public class LoginRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        // Connect the three buttons from the layout to this code
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnGoToRegister = findViewById(R.id.btnGoToRegister);
        Button btnGoToDoctorRegister = findViewById(R.id.btnGoToDoctorRegister);

        // Open the login screen
        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));

        // Open the patient registration screen
        btnGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, PatientRegisterActivity.class)));

        // Open the doctor registration screen
        btnGoToDoctorRegister.setOnClickListener(v ->
                startActivity(new Intent(this, DoctorRegisterActivity.class)));
    }
}
