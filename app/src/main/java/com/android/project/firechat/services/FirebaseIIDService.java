package com.android.project.firechat.services;

import android.util.Log;

import com.android.project.firechat.shared.SharedPrefManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseIIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIID";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        SharedPrefManager.getInstance(getApplicationContext()).storeToken(refreshedToken);
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            // Saves token for notifications
            FirebaseDatabase.getInstance().getReference().child("Tokens").child(uid)
                    .setValue(SharedPrefManager.getInstance(getApplicationContext()).getToken());
        }
    }
}
