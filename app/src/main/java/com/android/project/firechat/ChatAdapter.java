package com.android.project.firechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.project.firechat.shared.Constants;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter {
    private List<ChatMessageItem> chatMessageItems;
    private Context mContext;

    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 1;
    private static final int VIEW_TYPE_MESSAGE_SENT = 2;

    public ChatAdapter(List<ChatMessageItem> chatMessageItems, Context mContext) {
        this.chatMessageItems = chatMessageItems;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_msg_sent, parent, false);
            return new SentMessageHolder(v);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_msg_received, parent, false);
            return new ReceivedMessageHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessageItem chatItem = chatMessageItems.get(position);
        switch(holder.getItemViewType()) {
            case (VIEW_TYPE_MESSAGE_SENT):
                ((SentMessageHolder) holder).bind(chatItem);
                break;
            case (VIEW_TYPE_MESSAGE_RECEIVED):
                ((ReceivedMessageHolder) holder).bind(chatItem);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessageItems.get(position).getSenderUid().equals(FirebaseAuth.getInstance().getUid())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    public class SentMessageHolder extends RecyclerView.ViewHolder {
        private TextView chatMsgSent;
        private TextView chatTimestampSent;

        public SentMessageHolder(View itemView) {
            super(itemView);
            chatMsgSent = itemView.findViewById(R.id.chat_message_sent);
            chatTimestampSent = itemView.findViewById(R.id.chat_timestamp_sent);
        }

        public void bind(ChatMessageItem chatMessageItem) {
            String message =  chatMessageItem.getMessage();

            if (message.length() > 7 && message.substring(0,7).equals(Constants.LOCATION_MSG_IDENTIFIER)) {
                final String msg = message.substring(7,message.length()-1);
                Log.d("LOCATION", message);
                chatMsgSent.setText("[Location]");
                chatMsgSent.setTextColor(Color.CYAN);
                chatMsgSent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("LOCATION", "ONCLICK");
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:"+msg));
                        i.setPackage("com.google.android.apps.maps");
                        mContext.startActivity(i);
                    }
                });
            } else {
                chatMsgSent.setText(chatMessageItem.getMessage());
            }
            Date date = new Date(Long.parseLong(chatMessageItem.getTimestamp()));
            SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd HH:mm");
            String formattedDate = formatter.format(date);
            chatTimestampSent.setText(formattedDate);
        }
    }

    public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        private TextView chatSender;
        private TextView chatMsgReceived;
        private TextView chatTimestampReceived;

        public ReceivedMessageHolder(View itemView) {
            super(itemView);
            chatSender = itemView.findViewById(R.id.chat_sender);
            chatMsgReceived =  itemView.findViewById(R.id.chat_message_received);
            chatTimestampReceived =  itemView.findViewById(R.id.chat_timestamp_received);
        }

        public void bind(ChatMessageItem chatMessageItem) {
            chatSender.setText(chatMessageItem.getSenderName());
            String message =  chatMessageItem.getMessage();
            if (message.length() > 7 && message.substring(0,7).equals(Constants.LOCATION_MSG_IDENTIFIER)) {
                final String msg = message.substring(7,message.length()-1);
                Log.d("LOCATION", message);
                chatMsgReceived.setText("[Location]");
                chatMsgReceived.setTextColor(Color.CYAN);
                chatMsgReceived.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("LOCATION", "ONCLICK");
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:"+msg));
                        i.setPackage("com.google.android.apps.maps");
                        mContext.startActivity(i);
                    }
                });
            } else {
                chatMsgReceived.setText(chatMessageItem.getMessage());
            }
            Date date = new Date(Long.parseLong(chatMessageItem.getTimestamp()));
            SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd HH:mm");
            String formattedDate = formatter.format(date);
            chatTimestampReceived.setText(formattedDate);
        }
    }
}
