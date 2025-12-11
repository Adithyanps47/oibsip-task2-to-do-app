package com.example.todoapp;

import android.content.Intent;
import android.content.SharedPreferences; // Import added
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvGoToSignup;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- 1. AUTO-LOGIN CHECK ---
        // Check if user is already logged in before loading the layout
        SharedPreferences prefs = getSharedPreferences("ToDoAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Get the saved User ID
            int savedUserId = prefs.getInt("userId", -1);

            // Go straight to Dashboard
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("USER_ID", savedUserId);
            startActivity(intent);
            finish(); // Close LoginActivity
            return;
        }
        // ---------------------------

        setContentView(R.layout.activity_login);

        // Initialize Database
        db = new DatabaseHelper(this);

        // Bind UI elements
        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToSignup = findViewById(R.id.tvGoToSignup);

        // LOGIN BUTTON LOGIC
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = etUsername.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();

                if(user.equals("") || pass.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else {
                    boolean checkUser = db.checkUser(user, pass);
                    if(checkUser) {
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // Get the User ID
                        int userId = db.getUserId(user);

                        // --- 2. SAVE SESSION ---
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putInt("userId", userId);
                        editor.apply();
                        // -----------------------

                        // Navigate to Dashboard
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("USER_ID", userId);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // SIGNUP LINK LOGIC
        tvGoToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}