package com.example.peter.myapplication;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class OrderNumberActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_number);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView ordernumber = (TextView) findViewById(R.id.Ordernumber);
        ordernumber.setText(setordernumber().toString());
    }


    private Integer setordernumber(){
        //todo randomly generate order number without duplicates by connecting to server
        //hardcoded for now
        int orderId = Integer.parseInt(getIntent().getExtras().getString("orderID"));
        SharedPreferences sharedPref = getSharedPreferences("com.example.myapp.PREFERENCE_FILE_KEY", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("orderID", getIntent().getExtras().getString("orderID"));
        editor.commit();
        return orderId;
    }
}
