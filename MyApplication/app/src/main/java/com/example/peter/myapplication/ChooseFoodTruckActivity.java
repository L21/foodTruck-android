package com.example.peter.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ChooseFoodTruckActivity extends AppCompatActivity {
    private LoadMenuTask mMenuTask = null;
    //private View mMenu;
    ProgressDialog progress;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new OrderNotificationOpendHandler())
                .init();
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_food_truck);

        final ArrayList<String> truckNames = getIntent().getStringArrayListExtra("truckNames");
        final ArrayList<Integer> truckIDs = getIntent().getIntegerArrayListExtra("truckIDs");
        Object[] trucknamesArray = truckNames.toArray();
        final String[] truckNamesStrArray = Arrays.copyOf(trucknamesArray, trucknamesArray.length, String[].class);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_layout,
                R.id.list_content, truckNamesStrArray);

        final ListView lv = (ListView)findViewById(R.id.list);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String truckName = truckNames.get(position);
                String truckID = truckIDs.get(position).toString();
                if (mMenuTask != null) {
                    return;
                }
                progress = ProgressDialog.show(ChooseFoodTruckActivity.this, "Please wait",
                        "Loading Menu", true);
                mMenuTask = new LoadMenuTask(truckID, truckName);
                mMenuTask.execute((Void) null);
            }
        });
        lv.setAdapter(adapter);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent objEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyUp(keyCode, objEvent);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    public void goBack() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ChooseFoodTruckActivity.this);

        alertDialog.setTitle("Log out");
        alertDialog.setMessage("Are you sure you want to log out?");

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent loginIntent = new Intent(ChooseFoodTruckActivity.this,
                                LoginActivity.class);
                        ChooseFoodTruckActivity.this.startActivity(loginIntent);
                        ChooseFoodTruckActivity.this.finish();
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public class LoadMenuTask extends AsyncTask<Void, Void, Boolean> {

        private String truckID;
        private String truckName;
        private ArrayList<String> itemNames = new ArrayList<String>();
        private ArrayList<String> itemIDs = new ArrayList<String>();
        private ArrayList<String> itemPrices = new ArrayList<String>();

        public LoadMenuTask(String truckID, String truckName){
            this.truckID = truckID;
            this.truckName = truckName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            //todo connect to server and request foodtrucks
            //hardcoded for now
            SharedPreferences sharedPref = getSharedPreferences("com.example.myapp.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
            String URL = sharedPref.getString("URL", "ERROR");
            RequestQueue queue = Volley.newRequestQueue(ChooseFoodTruckActivity.this);
            String credential = "/menu?app=1&truckId=" + truckID;
            // Request a string response from the provided URL.
            RequestFuture<JSONArray> future = RequestFuture.newFuture();
            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET,
                    URL + credential, null, future, future);
            // Add the request to the RequestQueue.
            queue.add(jsArrayRequest);
            try {
                JSONArray response = future.get(10, TimeUnit.SECONDS); // Blocks for at most 10 seconds.
                int size = response.length();
                for(int i = 0; i < size; i++) {
                    JSONObject menuItem = response.getJSONObject(i);
                    String itemName = menuItem.getString("name");
                    itemNames.add(itemName);
                    itemIDs.add(Integer.toString(menuItem.getInt("id")));
                    itemPrices.add("$" + Integer.toString(menuItem.getInt("price")));
                }
                return true;
            } catch (Exception e){
                e.printStackTrace();
                finish();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
                Intent MenuIntent = new Intent(ChooseFoodTruckActivity.this, MenuDisplayActivity.class);
                MenuIntent.putExtra("truckName", truckName);
                MenuIntent.putExtra("truckID", truckID); //TODO change implementation to request from server for food truck id
                MenuIntent.putExtra("itemNames", itemNames);
                MenuIntent.putExtra("itemIDs", itemIDs);
                MenuIntent.putExtra("itemPrices", itemPrices);
                ChooseFoodTruckActivity.this.startActivity(MenuIntent);
                progress.dismiss();
            }
            mMenuTask = null;
            progress.dismiss();
        }

        @Override
        protected void onCancelled() {
            mMenuTask = null;
            progress.dismiss();
        }
    }
    public class OrderNotificationOpendHandler implements OneSignal.NotificationOpenedHandler {

        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            SharedPreferences sharedPref = getSharedPreferences("com.example.myapp.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
            String orderID = sharedPref.getString("orderID", "ERR");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("orderID", "000");
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), OrderNumberActivity.class);
            intent.putExtra("orderID", orderID);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
