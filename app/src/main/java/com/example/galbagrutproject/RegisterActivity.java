package com.example.galbagrutproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import com.example.galbagrutproject.models.User;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail, editTextUsername, editTextPassword;
    private Button btnSignUp, btnLoginRedirect;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private TextView selectedDateTextView;
    private String selectedDate; // Variable to hold the selected date

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Bind views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        btnPickDate = findViewById(R.id.btnPickDate);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLoginRedirect = findViewById(R.id.btnLoginRedirect);


        // Set up listeners
        btnSignUp.setOnClickListener(this);
        btnLoginRedirect.setOnClickListener(this);
        btnPickDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnSignUp) {
            String email = editTextEmail.getText().toString();
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || selectedDate == null) {
                Toast.makeText(RegisterActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(email, username, password, selectedDate);
        } else if (view == btnLoginRedirect) {
            // Redirect to login activity
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        } else if (view == btnPickDate) {
            // Open the Date Picker Dialog
            showDatePickerDialog();
        }
    }

    private void registerUser(String email, String username, String password, String dateOfBirth) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        User newUser = new User(email, username, dateOfBirth);

                        // Save user data to Firestore
                        if (user != null) {
                            DocumentReference userRef = firestore.collection("users").document(user.getUid());
                            userRef.set(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                        // Redirect to login page
                                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(i);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RegisterActivity.this, "Error saving user data", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Method to open Date Picker Dialog
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedMonth = selectedMonth + 1;  // Add 1 because months are 0-indexed
                    selectedDate = selectedDay + "/" + selectedMonth + "/" + selectedYear;
                    selectedDateTextView.setText("Selected Date: " + selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}
