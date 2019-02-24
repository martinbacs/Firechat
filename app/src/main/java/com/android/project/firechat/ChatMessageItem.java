package com.android.project.firechat;

import java.util.HashMap;
import java.util.Map;


public class ChatMessageItem {
    private String message;
    private String timestamp;
    private String senderName;
    private String senderUid;
    private String receiverUid;

    public ChatMessageItem() {
        // Default constructor required for calls to DataSnapshot.getValue(ChatMessageItem.class)
    }

    public ChatMessageItem(String message, String timestamp, String username, String senderUid, String receiverUid) {
        this.message = message;
        this.timestamp = timestamp;
        this.senderName = username;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderUid() {
        return this.senderUid;
    }

    public HashMap<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("message", this.message);
        result.put("timestamp", this.timestamp);
        result.put("senderName", this.senderName);
        result.put("senderUid", this.senderUid);
        result.put("receiverUid", this.receiverUid);
        return result;
    }

}
