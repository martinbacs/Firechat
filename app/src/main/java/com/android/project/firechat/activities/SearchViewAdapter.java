package com.android.project.firechat.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.project.firechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

class SearchViewAdapter extends ArrayAdapter<Contact>{

    //refers to the contact that is clicked in listview
    //private Contact contact;
    //layout
    private TextView userNameTextView, userStatusTextView;
    private Button addBtn;
    private ImageView profileImage;

    //firebase
    private DatabaseReference firebaseDatabase;
    private FirebaseUser currentUser;


    SearchViewAdapter(Context context, ArrayList<Contact> users){
        super(context, R.layout.contact_row, users);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customRow = layoutInflater.inflate(R.layout.contact_row, parent, false);

        userNameTextView = (TextView) customRow.findViewById(R.id.userNameText);
        userStatusTextView = (TextView) customRow.findViewById(R.id.userStatusText);
        addBtn = (Button) customRow.findViewById(R.id.removeBtn);
        profileImage = (ImageView) customRow.findViewById(R.id.userProfileImage);

        //get contact that is clicked
        final Contact contact = getItem(position);


        addBtn.setText(R.string.add);
        userNameTextView.setText(contact.getUserName());
        userStatusTextView.setText(contact.getStatus());

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add contact to database
                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String uid = currentUser.getUid();
                //getReference points at root directory in db
                firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Contacts").child(contact.getUserId());
                //save user to database
               HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("username", contact.getUserName());

                firebaseDatabase.updateChildren(userMap);
                Toast.makeText(getContext(), contact.getUserName() + " added to contacts", Toast.LENGTH_SHORT).show();
            }
        });


        return  customRow;

    }
}
