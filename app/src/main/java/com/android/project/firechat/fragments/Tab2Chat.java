package com.android.project.firechat.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.project.firechat.R;



public class Tab2Chat extends Fragment{
    private Button chatButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2chat, container, false);
        //chatButton = rootView.findViewById(R.id.chatButton);

        return rootView;
    }

}
