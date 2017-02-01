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

import java.util.ArrayList;


public class Tab3_Completed extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3_completed, container, false);
        ArrayList<String> itemlist = VendorActivity.getcomplete();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_layout, R.id.list_content, itemlist);
        VendorActivity.setcompleteadp(adapter);
        final ListView lv = (ListView) rootView.findViewById(R.id.completelist);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object o = lv.getItemAtPosition(position);
                new AlertDialog.Builder(getActivity())
                        .setTitle("Clear Orders")
                        .setMessage("Clear order history??" )
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                VendorActivity.rmcompleteall();
                                //notifyadapter();
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
        VendorActivity.getcompleteadp().notifyDataSetChanged();
    }
}
