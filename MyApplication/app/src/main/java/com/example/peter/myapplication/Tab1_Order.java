package com.example.peter.myapplication;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Backend.Vendor;

public class Tab1_Order extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ArrayList <String> itemlist = VendorActivity.getorder();
        View rootView = inflater.inflate(R.layout.tab1_order, container, false);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_layout, R.id.list_content, itemlist);
        VendorActivity.setorderadp(adapter);
        final ListView lv = (ListView) rootView.findViewById(R.id.orderlist);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object o = lv.getItemAtPosition(position);
                final String temp = o.toString();
                new AlertDialog.Builder(getActivity())
                        .setTitle("Order")
                        .setMessage("Order ready for pickup" )
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String customerToken = VendorActivity.getCustomerId(temp);
                                VendorActivity.rmorder(temp);
                                VendorActivity.addawaiting(temp);
                                VendorActivity.notifyCustomer(customerToken);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        lv.setAdapter(adapter);
        return rootView;
    }
    public void notifyadapter(){
        VendorActivity.getorderadp().notifyDataSetChanged();
        VendorActivity.getawaitingadp().notifyDataSetChanged();
    }


}
