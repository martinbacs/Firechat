package com.android.project.firechat.shared;



public interface Constants {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final int LOCATION_REQUEST_CODE = 133;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Shared preferences constants
    public static final String SHARED_PREFS = "firechat";
    public static final String TOKEN_KEY = "token";
    public static final String DISPLAY_NAME_KEY = "displayname";

    public static final String ACTION_FC_LOCATION_SENDER = "firechat.location.sender";
    public static final String LOCATION_MSG_IDENTIFIER = "####LOC";

    public static final String RECEIVER_UID_KEY = "receiver_uid";

}