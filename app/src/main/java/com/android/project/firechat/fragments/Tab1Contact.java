package com.android.project.firechat.fragments;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;


import com.android.project.firechat.R;
import com.android.project.firechat.activities.Contact;
import com.android.project.firechat.activities.ContactListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;




public class Tab1Contact extends Fragment {

    //layout
    private ListView contactListView;
    private ArrayList<Contact> contactList = new ArrayList<>();
    private ContactListAdapter adapter;

    //firebase
    DatabaseReference firebaseDatabase;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        adapter = new ContactListAdapter(getActivity(), contactList);
        if (!contactList.isEmpty()){
            contactList.clear();
            adapter.clear();
        }
        View rootView = inflater.inflate(R.layout.tab1contacts, container, false);

        contactListView = rootView.findViewById(R.id.contactListView);

        contactListView.setAdapter(adapter);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Contacts");

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!contactList.isEmpty()){
                    contactList.clear();
                    adapter.clear();
                }

                //add contacts
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Contact contact = new Contact(snapshot.child("username").getValue().toString(), snapshot.getKey());
                    contactList.add(contact);
                }

                //add status to contact (not the best solution but works)
                for (final Contact c : contactList){
                    firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users").child(c.getUserId()).child("status");
                    firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            c.setStatus(dataSnapshot.getValue().toString());
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        return rootView;
    }

}
