package com.android.project.firechat.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.project.firechat.R;
import com.android.project.firechat.shared.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    //layout
    private Button backBtn, registerBtn;
    private EditText userNameText,emailText, passwordText;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    //firebase
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userNameText = findViewById(R.id.userNameText);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        backBtn = findViewById(R.id.backBtn);
        registerBtn = findViewById(R.id.searchBtn);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

    }

    public void registerUser(){
        final String email = emailText.getText().toString().trim();
        final String password = passwordText.getText().toString().trim();
        final String userName = userNameText.getText().toString().trim();

        //validation
        if (userName.isEmpty() || userName.length() > 10){
            userNameText.setError("Username is required");
            userNameText.requestFocus();
            return;
        }

        if (email.isEmpty()){
            emailText.setError("Email is required!");
            emailText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Please enter a valid email");
        }

        if (password.isEmpty()){
            passwordText.setError("Password is required!");
            passwordText.requestFocus();
            return;
        }

        if (password.length() < 6){
            passwordText.setError("Minimum password length is 6");
            passwordText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "User register successful", Toast.LENGTH_SHORT).show();

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = currentUser.getUid();

                    //getReference points at root directory in db
                    firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    //save user to database
                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", userName);
                    userMap.put("status", "Firechat is da bomb yo!");
                    userMap.put("image", "default");
                    userMap.put("thumbImage", "default");
                    userMap.put("Contacts", "");



                    firebaseDatabase.setValue(userMap);

                    // Saves token for notifications
                    FirebaseDatabase.getInstance().getReference().child("Tokens").child(uid)
                            .setValue(SharedPrefManager.getInstance(getApplicationContext()).getToken());

                    //start new activity
                    Intent intent = new Intent(RegisterActivity.this, UserActivity.class);
                    //clear all open activities and open new one
                    //this is to make sure that the back button cant be used passed UserActivity
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else {
                    //Email is already registered
                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(), "This email is already registered", Toast.LENGTH_SHORT).show();
                    }else{
                        //else try to get message that caused the error
                        try{
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }catch (NullPointerException ex){
                            Toast.makeText(getApplicationContext(), "Some error has occurred, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
}
