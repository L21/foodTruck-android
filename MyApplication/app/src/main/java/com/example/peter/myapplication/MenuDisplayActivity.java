package com.example.peter.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MenuDisplayActivity extends AppCompatActivity {
    private ListView mMenu;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.activity_menu_display);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);

        mMenu = (ListView)findViewById(R.id.menu);

        Intent intent = getIntent();
        final String truckID = intent.getStringExtra("truckID");
        final String truckName = intent.getStringExtra("truckName");
        TextView tvTruckName = (TextView)findViewById(R.id.truckName);
        tvTruckName.setText(truckName);
        //String[] menu = getmenu(fid);

        final ArrayList<String> itemNames = getIntent().getStringArrayListExtra("itemNames");
        final ArrayList<String> itemIDs = getIntent().getStringArrayListExtra("itemIDs");
        final ArrayList<String> itemPrices = getIntent().getStringArrayListExtra("itemPrices");

        Object[] objList = itemNames.toArray();
        final String[] menu = Arrays.copyOf(objList, objList.length, String[].class);
        System.out.println(menu.length);
        /*
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(MenuDisplayActivity.this,
                        R.layout.list_item_layout,
                        R.id.list_content,
                        menu);
        */

        CustomAdapter customAdapter = new CustomAdapter(this, itemNames, itemPrices);
        //final ListView lv = (ListView)findViewById(R.id.menu);

        mMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final Object o = mMenu.getItemAtPosition(position);
                final Context context = MenuDisplayActivity.this;
                final String itemName = itemNames.get(position);
                final String itemID = itemIDs.get(position).toString();
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Order")
                        .setMessage("Are you sure you want to order " + o.toString() + "?" )
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent OrderIntent = new Intent(MenuDisplayActivity.this, PaymentActivity.class);
                                OrderIntent.putExtra("foodName", itemName);
                                OrderIntent.putExtra("menuID", itemID);
                                OrderIntent.putExtra("truckID", truckID);
                                MenuDisplayActivity.this.startActivity(OrderIntent);
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
        mMenu.setAdapter(customAdapter);
        //mMenu.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle "up" button behavior here.
            finish();
            return true;
        } else {
            // handle other items here
        }
        // return true if you handled the button click, otherwise return false.
        return true;
    }



    public class CustomAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ArrayList<String> items;
        private ArrayList<String> prices;

        private class ViewHolder {
            TextView textView1;
            TextView textView2;
        }

        public CustomAdapter(Context context, ArrayList<String> items, ArrayList<String> prices) {
            inflater = LayoutInflater.from(context);
            this.items = items;
            this.prices = prices;
        }

        public int getCount() {
            return items.size();
        }

        public String getItem(int position) {
            return items.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list_item_layout_custom, null);
                holder.textView1 = (TextView) convertView.findViewById(R.id.list_content_name);
                holder.textView2 = (TextView) convertView.findViewById(R.id.list_content_price);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView1.setText(items.get(position));
            holder.textView2.setText(prices.get(position));
            return convertView;
        }
    }
}
