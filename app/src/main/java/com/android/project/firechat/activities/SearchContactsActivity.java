package com.android.project.firechat.activities;

import com.android.project.firechat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;


public class SearchContactsActivity extends AppCompatActivity {

    //layout
    private ArrayList<Contact> resultList = new ArrayList<>();
    private String searchResult;
    private ListView searchResultView;
    private EditText userNameText,emailText;
    private ProgressBar progressBar;
    private Button searchBtn;

    //firebase
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contacts);
        userNameText = findViewById(R.id.userNameText);
        emailText = findViewById(R.id.emailText);
        searchBtn = findViewById(R.id.searchBtn);

        searchResultView = findViewById(R.id.searchResultView);

        final SearchViewAdapter adapter = new SearchViewAdapter(this, resultList);


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!resultList.isEmpty()){
                    resultList.clear();
                    adapter.clear();
                }

                firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users");
                firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                            String userName = snapshot.child("name").getValue().toString();
                            String userId = snapshot.getKey();
                            String userStatus = snapshot.child("status").getValue().toString();
                            Contact contact = new Contact(userName, userId, userStatus);

                            if (userName.contains(userNameText.getText().toString())){
                                resultList.add(contact);
                            }
                        }
                        searchResultView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

}
