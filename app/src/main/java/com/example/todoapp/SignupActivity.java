package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.DatabaseHelper;
import com.example.todoapp.R;

public class SignupActivity extends AppCompatActivity {

    EditText etUser, etPass, etRepass;
    Button btnRegister;
    TextView tvLogin;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        db = new DatabaseHelper(this);

        etUser = findViewById(R.id.etSignupUsername);
        etPass = findViewById(R.id.etSignupPassword);
        etRepass = findViewById(R.id.etSignupConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvGoToLogin);

        // REGISTER BUTTON LOGIC
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = etUser.getText().toString().trim();
                String pass = etPass.getText().toString().trim();
                String repass = etRepass.getText().toString().trim();

                if(user.equals("") || pass.equals("") || repass.equals("")) {
                    Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    if(pass.equals(repass)) {
                        // Check if user already exists
                        if(!db.checkUser(user, pass)) { // simplified check, ideally check username only
                            boolean insert = db.registerUser(user, pass);
                            if(insert) {
                                Toast.makeText(SignupActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                finish(); // Returns to Login Activity
                            } else {
                                Toast.makeText(SignupActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // LINK TO LOGIN LOGIC
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Just close this activity to go back to Login
            }
        });
    }
}