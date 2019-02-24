package com.android.project.firechat.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.project.firechat.R;
import com.android.project.firechat.shared.SharedPrefManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //layout
    private Button loginBtn;
    private EditText emailText, passwordText;
    private ProgressBar progressBar;

    //private LoginButton loginButton;

    CallbackManager callbackManager;

    private TextView registerAcc;


    //firebase
    private FirebaseAuth mAuth;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);


        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();
        final String EMAIL = "email";

        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        loginBtn = findViewById(R.id.loginBtn);
        progressBar = findViewById(R.id.progressBar);
        registerAcc = findViewById(R.id.textViewRegister);


        //loginButton = (LoginButton) findViewById(R.id.login_button);
       // loginButton.setReadPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);



        mAuth = FirebaseAuth.getInstance();
/*
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                startActivity(new Intent(MainActivity.this, UserActivity.class));
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
*/
/*

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        startActivity(new Intent(MainActivity.this, UserActivity.class));
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });

*/

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });


        registerAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onCreate: Register button pressed");
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
    }

    public void userLogin(){
        Log.d(TAG, "userLogin: Login button pressed, validating info");

        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        //validation
        if (email.isEmpty()){
            emailText.setError("Email is required!");
            emailText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Please enter a valid emailText");
        }

        if (password.isEmpty()){
            passwordText.setError("Password is required!");
            passwordText.requestFocus();
            return;
        }

        if (password.length() < 6){
            passwordText.setError("Minimum passwordText length is 6");
            passwordText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    Log.d(TAG, "userLogin: login successful");
                    
                    // Saves token for notifications
                    FirebaseDatabase.getInstance().getReference().child("Tokens")
                            .child(FirebaseAuth.getInstance().getUid())
                            .setValue(SharedPrefManager.getInstance(getApplicationContext()).getToken());

                    //start new activity
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    //clear all open activities and open new one
                    //this is to make sure that the back button cant be used passed UserActivity
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else {
                    try{
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }catch (NullPointerException ex){
                        Toast.makeText(getApplicationContext(), "Some error has occurred, please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}
