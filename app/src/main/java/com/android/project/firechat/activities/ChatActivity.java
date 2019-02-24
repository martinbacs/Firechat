package com.android.project.firechat.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.project.firechat.ChatAdapter;
import com.android.project.firechat.ChatMessageItem;
import com.android.project.firechat.R;
import com.android.project.firechat.shared.Constants;
import com.android.project.firechat.shared.SharedPrefManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {



    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<ChatMessageItem> chatMessageItemList;

    private EditText chatInput;
    private String chatRoomId;

    private String receiverUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ActivityMaps locationMap;

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        chatInput = findViewById(R.id.chat_input);
        chatMessageItemList = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        receiverUid = extras.getString(Constants.RECEIVER_UID_KEY);

        mAdapter = new ChatAdapter(chatMessageItemList, this);
        recyclerView.setAdapter(mAdapter);

        startChat();
    }

    @Override
    public void onNewIntent(Intent newIntent) {
        this.setIntent(newIntent);

        Bundle extras = getIntent().getExtras();
        receiverUid = extras.getString(Constants.RECEIVER_UID_KEY);
    }

    public void startChat() {
        DatabaseReference chatRoomRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("chatrooms");
        chatRoomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ChatRoom value = ds.getValue(ChatRoom.class);
                    if (value.receiver.equals(receiverUid)) {
                        Log.d("ChatActivity:", "chatroom found");
                        chatRoomId = ds.getKey();
                        listenForMessages();
                        return;
                    }
                }
                Log.d("ChatActivity:", "chatroom not found");
                createChatroom();
                listenForMessages();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ChatActivity:SEARCH", databaseError.getDetails());
            }
        });
    }

    public void listenForMessages() {
        Log.d("ChatActivity:", chatRoomId);

        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("Messages").child(chatRoomId);
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessageItem message = dataSnapshot.getValue(ChatMessageItem.class);
                chatMessageItemList.add(message);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ChatActivity:RETRIEVE", databaseError.getDetails());
            }
        });
    }

    public void createChatroom() {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userUid).child("chatrooms");
        dbRef = dbRef.push();
        chatRoomId = dbRef.getKey();
        HashMap<String, String> chatMap = new ChatRoom(receiverUid).toMap();
        dbRef.setValue(chatMap);

        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(receiverUid)
                .child("chatrooms").child(chatRoomId);
        chatMap = new ChatRoom(userUid).toMap();
        dbRef.setValue(chatMap);

        Log.d("ChatActivity:", "Created chatroomid");
    }

    public void send(View v) {
        String senderUid = FirebaseAuth.getInstance().getUid();
        if (SharedPrefManager.getInstance(getApplicationContext()).getDisplayName() == null) {
            FirebaseDatabase.getInstance().getReference("Users/" + senderUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String senderName = dataSnapshot.child("name").getValue().toString();
                    SharedPrefManager.getInstance(getApplicationContext()).storeDisplayName(senderName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("ChatActivity:", databaseError.getDetails());
                }
            });
        }
        String message = chatInput.getText().toString();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String senderName = SharedPrefManager.getInstance(getApplicationContext()).getDisplayName();

        ChatMessageItem result = new ChatMessageItem(message, timestamp, senderName, senderUid, receiverUid);
        HashMap<String, String> chatMsg = result.toMap();

        FirebaseDatabase.getInstance().getReference("Messages").child(chatRoomId).push().setValue(chatMsg);
        chatInput.setText("");
    }

    public void sendLocation(View v) {
        Intent i = new Intent(Constants.ACTION_FC_LOCATION_SENDER);
        startActivityForResult(i, Constants.LOCATION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.LOCATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("SendLocation", "OK");
                Uri location =  data.getData();

                String senderUid = FirebaseAuth.getInstance().getUid();
                if (SharedPrefManager.getInstance(getApplicationContext()).getDisplayName() == null) {
                    FirebaseDatabase.getInstance().getReference("Users/" + senderUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String senderName = dataSnapshot.child("name").getValue().toString();
                            SharedPrefManager.getInstance(getApplicationContext()).storeDisplayName(senderName);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("ChatActivity:", databaseError.getDetails());
                        }
                    });
                }
                String message = Constants.LOCATION_MSG_IDENTIFIER+location;
                String timestamp = String.valueOf(System.currentTimeMillis());
                String senderName = SharedPrefManager.getInstance(getApplicationContext()).getDisplayName();
                ChatMessageItem result = new ChatMessageItem(message, timestamp, senderName, senderUid, receiverUid);
                HashMap<String, String> locationMsg = result.toMap();

                FirebaseDatabase.getInstance().getReference("Messages").child(chatRoomId).push().setValue(locationMsg);
            }
        }
    }

    static private class ChatRoom {
        public String receiver;

        public ChatRoom() {

        }

        public ChatRoom(String receiver) {
            this.receiver = receiver;
        }

        public HashMap<String, String> toMap() {
            HashMap<String, String> chatMap = new HashMap<>();
            chatMap.put("receiver", receiver);
            return chatMap;
        }
    }
}
