package com.android.project.firechat.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.project.firechat.R;
import com.android.project.firechat.shared.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ContactListAdapter extends ArrayAdapter<Contact>{
    //refers to the contact that is clicked in listview
    //private Contact contact;
    //layout
    private TextView userNameTextView, userStatusTextView;
    private Button removeBtn;
    private ImageView profileImage;

    //firebase
    private DatabaseReference firebaseDatabase;
    private FirebaseUser currentUser;

    private Context mContext;

    public ContactListAdapter(Context context, ArrayList<Contact> users){
        super(context, R.layout.contact_row, users);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //inflate = get rdy for rendering
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customRow = layoutInflater.inflate(R.layout.contact_row, parent, false);


        userNameTextView = (TextView) customRow.findViewById(R.id.userNameText);
        userStatusTextView = (TextView) customRow.findViewById(R.id.userStatusText);
        removeBtn = (Button) customRow.findViewById(R.id.removeBtn);
        profileImage = (ImageView) customRow.findViewById(R.id.userProfileImage);

        //get contact that is clicked
        final Contact contact = getItem(position);
        userNameTextView.setText(contact.getUserName());
        userStatusTextView.setText(contact.getStatus());


        //update statuses for contacts
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(contact.getUserId()).child("status");
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userStatusTextView.setText(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //handle the contact that is clicked
        userNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to chat
                String receiverUid = contact.getUserId();
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra(Constants.RECEIVER_UID_KEY, receiverUid);
                mContext.startActivity(intent);
            }
        });

        //remove contact from database
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String uid = currentUser.getUid();
                //getReference points at root directory in db
                firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Contacts").child(contact.getUserId());
                firebaseDatabase.removeValue();
            }
        });

        return  customRow;

    }


}
