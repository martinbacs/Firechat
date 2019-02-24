package com.android.project.firechat.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


import com.android.project.firechat.R;


public class Tab3XXX extends Fragment{

    private BluetoothAdapter BA;
    private Set<BluetoothDevice>pairedDevices;
    private ArrayAdapter<String> discoveredDevicesAdapter;



    TextView statusText;
    ListView listView;
    Button deviceSearchBtn,makeVisibleBtn,turnOnOff;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3xxx, container, false);



        listView = rootView.findViewById(R.id.listView2);
        deviceSearchBtn = rootView.findViewById(R.id.devices_search);
        makeVisibleBtn = rootView.findViewById(R.id.visibleBtn);
        statusText = rootView.findViewById(R.id.status);
        turnOnOff = rootView.findViewById(R.id.onOffBtn);


        BA = BluetoothAdapter.getDefaultAdapter();


        turnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (BA.isEnabled()){
                    BA.disable();
                }else{
                    Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOn, 0);
                }
            }
        });


        makeVisibleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeVisible();
            }
        });


        deviceSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDevices();
            }
        });



        return rootView;
    }

    public void getDevices(){
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) list.add(bt.getName());
        Toast.makeText(getActivity().getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new  ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, list);

        listView.setAdapter(adapter);
    }
    public void makeVisible(){

        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }


}
