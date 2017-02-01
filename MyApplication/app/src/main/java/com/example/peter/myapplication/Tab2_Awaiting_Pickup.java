package com.example.peter.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.ArrayList;


public class Tab2_Awaiting_Pickup extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_awaiting_pickup, container, false);
        ArrayList<String> itemlist = VendorActivity.getawaiting();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_layout, R.id.list_content, itemlist);
        VendorActivity.setawaitingadp(adapter);
        final ListView lv = (ListView) rootView.findViewById(R.id.awaitinglist);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Object o = lv.getItemAtPosition(position);
                VendorActivity.rmawaiting(o.toString());
                VendorActivity.addcomplete(o.toString());
                //notifyadapter();
            }
        });
        lv.setAdapter(adapter);
        return rootView;
    }
    public void notifyadapter(){
        VendorActivity.getawaitingadp().notifyDataSetChanged();
        VendorActivity.getcompleteadp().notifyDataSetChanged();
    }
}
