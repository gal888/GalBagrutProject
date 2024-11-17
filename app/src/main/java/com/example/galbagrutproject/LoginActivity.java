package com.example.galbagrutproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextUsername, editTextPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);


        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        DocumentReference userRef = firestore.collection("users").document(user.getUid());
                        userRef.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Toast.makeText(LoginActivity.this, "logged in successfully", Toast.LENGTH_SHORT).show();
                             //   Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                               // startActivity(i);
                            } else {
                                Toast.makeText(LoginActivity.this, "invalid details", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {

                    }
                });
    }


    @Override
    public void onClick(View view) {
        if(view == btnLogin) {
            String email = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        }
        if(view == btnRegister) {
             Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
             startActivity(i);
        }
    }
}
