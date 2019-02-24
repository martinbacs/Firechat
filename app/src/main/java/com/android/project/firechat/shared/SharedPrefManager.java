package com.android.project.firechat.shared;

import android.content.Context;
import android.content.SharedPreferences;

import static com.android.project.firechat.shared.Constants.DISPLAY_NAME_KEY;
import static com.android.project.firechat.shared.Constants.SHARED_PREFS;
import static com.android.project.firechat.shared.Constants.TOKEN_KEY;

public class SharedPrefManager {
    private static Context mContext;
    private static SharedPrefManager mInstance;

    private SharedPrefManager(Context context) {
        mContext = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void storeToken(String token) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    public String getToken() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFS,
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    public void storeDisplayName(String displayName) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DISPLAY_NAME_KEY, displayName);
        editor.apply();
    }

    public String getDisplayName() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFS,
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(DISPLAY_NAME_KEY, null);
    }
}
