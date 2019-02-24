package com.android.project.firechat.activities;


import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.project.firechat.R;
import com.android.project.firechat.fragments.Tab1Contact;
import com.android.project.firechat.fragments.Tab2Chat;
import com.android.project.firechat.fragments.Tab3XXX;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.view.View;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";

    //layout
    private ViewPager mViewPager;

    //firebase
    private FirebaseAuth mAuth;
    private SectionsPagerAdapter mSectionsPagerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserActivity.this, SearchContactsActivity.class));
            }
        });
    }


    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: checking if user is logged in");
        super.onStart();
        //check if user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(UserActivity.this, MainActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.mainLogoutBtn){
            Log.d(TAG, "onOptionsItemSelected: Log out button pressed");
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(UserActivity.this, MainActivity.class));
        }
        if (item.getItemId() == R.id.mainSettingsBtn){
            Log.d(TAG, "onOptionsItemSelected: Settings button pressed");
            startActivity(new Intent(UserActivity.this, SettingsActivity.class));
        }
        if (item.getItemId() == R.id.mainBluetoothBtn){
            Log.d(TAG, "onOptionsItemSelected: Settings button pressed");
            startActivity(new Intent(UserActivity.this, BluetoothActivity.class));
        }
        if (item.getItemId() == R.id.mainMapsBtn){
            Log.d(TAG, "onOptionsItemSelected: Settings button pressed");
            startActivity(new Intent(UserActivity.this, ActivityMaps.class));
        }
        return true;
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            //returning the current tabs
            switch (position) {
                case 0:
                    Tab1Contact tab1 = new Tab1Contact();
                    return tab1;
                case 1:
                    Tab2Chat tab2 = new Tab2Chat();
                    return tab2;
                case 2:
                    Tab3XXX tab3 = new Tab3XXX();
                    return tab3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }


        @Override
        public CharSequence getPageTitle(int position){
            // names on the tab
            switch (position){
                case 0:
                    return "Contacts";
                case 1:
                    return "Chat";
                case 2:
                    return "Bluetooth Settings";
            }
            return null;
        }
    }
}
