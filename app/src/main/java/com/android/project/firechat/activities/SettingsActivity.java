package com.android.project.firechat.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.project.firechat.R;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.CallbackManager;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private static final int GALLERY_PICK = 1;


    private String status, userName, email, image, thumbImage;

    //layout
    private ImageView imageView;
    private TextView emailText, statusText, userNameText;
    private EditText statusUpdateText;
    private Button saveBtn, changeImageBtn, shareButton;

    //facebook
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;


    //firebase
    private DatabaseReference firebaseDatabase;
    private FirebaseUser currentUser;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_settings);

        statusUpdateText = findViewById(R.id.statusUpdateText);
        emailText = findViewById(R.id.emailText);
        statusText = findViewById(R.id.statusText);
        userNameText = findViewById(R.id.userNameText);
        saveBtn = findViewById(R.id.saveBtn);
        changeImageBtn = findViewById(R.id.changeImageBtn);
        imageView = findViewById(R.id.imageView);
        shareButton = findViewById(R.id.shareBtn);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        shareButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(SettingsActivity.this, "share successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(SettingsActivity.this, "share unsuccessful", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(SettingsActivity.this, "error", Toast.LENGTH_SHORT).show();

                    }
                });

                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle("test")
                        .setContentDescription("test test")
                        .setContentUrl(Uri.parse("https://youtube.com"))
                        .build();
                if (ShareDialog.canShow(ShareLinkContent.class)){
                    shareDialog.show(linkContent);

                }
            }
        });



        //get current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = currentUser.getUid();


        //point root to current user
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid);

        //set user info
        email = currentUser.getEmail();
        emailText.setText(email);

        //update values from database when change occurs
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("name").getValue().toString();
                status = dataSnapshot.child("status").getValue().toString();

                Log.d(TAG, "onDataChange: updating profile for user: " + userName);

                userNameText.setText(userName);
                statusText.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        changeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: saving status update for user: " + userName);

                //set new status
                status = statusUpdateText.getText().toString();

                firebaseDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Status updated", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Error occurred, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
