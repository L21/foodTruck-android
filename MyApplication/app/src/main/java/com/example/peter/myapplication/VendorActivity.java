package com.example.peter.myapplication;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class VendorActivity extends AppCompatActivity {
    private static ArrayList<String> orders = new ArrayList<String>();
    private static ArrayList<String> awaiting = new ArrayList<String>();
    private static ArrayList<String> complete = new ArrayList<String>();
    private static ArrayAdapter<String> orderadp;
    private static ArrayAdapter<String> awaitingadp;
    private static ArrayAdapter<String> completeadp;
    private static HashMap<String, String> orderInfo = new HashMap<String, String>();

    private static String oneSignalToken;

    private static boolean activityStarted;

    IntentFilter filter = new IntentFilter("com.example.peter.myapplication.update");
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String orderId = intent.getStringExtra("orderId");
            String itemName = intent.getStringExtra("itemName");
            String customerId = intent.getStringExtra("customerId");
            addorder(orderId + ": " + itemName);
            addOrderInfo(orderId, itemName, customerId);
        }
    };

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            FileInputStream fis = openFileInput("orders.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            orders = (ArrayList<String>) ois.readObject();
            fis = openFileInput("awaiting.ser");
            ois = new ObjectInputStream(fis);
            awaiting = (ArrayList<String>) ois.readObject();
            fis = openFileInput("complete.ser");
            ois = new ObjectInputStream(fis);
            complete = (ArrayList<String>) ois.readObject();
            fis = openFileInput("orderInfo.ser");
            ois = new ObjectInputStream(fis);
            orderInfo = (HashMap<String, String>) ois.readObject();

        }catch (Exception e){

        }

        this.registerReceiver(receiver, filter);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.None)
                .setNotificationReceivedHandler(new VendortestNotificationReceivedHandler())
                .init();

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                oneSignalToken = userId;
            }
        });
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.red));

        setContentView(R.layout.activity_vendor);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            FileOutputStream fos = openFileOutput("orders.ser", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(orders);
            fos = openFileOutput("awaiting.ser", MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(awaiting);
            fos = openFileOutput("complete.ser", MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(complete);
            fos = openFileOutput("orderInfo.ser", MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(orderInfo);

        }catch(Exception e){

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vendor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean fileExist(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    public static ArrayList<String> getorder() {
        return orders;
    }

    public static ArrayList<String> addorder(String toadd) {
        orders.add(toadd);
        orderadp.notifyDataSetChanged();
        return orders;
    }

    public static ArrayList<String> rmorder(String torm) {
        orders.remove(torm);
        orderInfo.remove(torm);
        orderadp.notifyDataSetChanged();
        return orders;
    }

    public static ArrayList<String> getawaiting() {
        return awaiting;
    }

    public static ArrayList<String> addawaiting(String toadd) {
        awaiting.add(toadd);
        awaitingadp.notifyDataSetChanged();
        return awaiting;
    }

    public static ArrayList<String> rmawaiting(String torm) {
        awaiting.remove(torm);
        awaitingadp.notifyDataSetChanged();
        return awaiting;
    }

    public static ArrayList<String> getcomplete() {
        return complete;
    }

    public static ArrayList<String> addcomplete(String toadd) {
        complete.add(toadd);
        completeadp.notifyDataSetChanged();
        return complete;
    }

    public static ArrayList<String> rmcompleteall() {
        complete.clear();
        completeadp.notifyDataSetChanged();
        return complete;
    }

    public static void addOrderInfo(String orderId, String itemName, String customerId) {
        orderInfo.put(orderId + ": " + itemName, customerId);
    }

    public static String getUserToken() {
        return oneSignalToken;
    }

    public static void setawaitingadp(ArrayAdapter<String> adp) {
        awaitingadp = adp;
    }

    public static ArrayAdapter<String> getawaitingadp() {
        return awaitingadp;
    }

    public static void setorderadp(ArrayAdapter<String> adp) {
        orderadp = adp;
    }

    public static ArrayAdapter<String> getorderadp() {
        return orderadp;
    }

    public static void setcompleteadp(ArrayAdapter<String> adp) {
        completeadp = adp;
    }

    public static ArrayAdapter<String> getcompleteadp() {
        return completeadp;
    }

    public static String getCustomerId(String orderId) {return orderInfo.get(orderId);}

    public static void notifyCustomer(String customerToken) {
        try {
            OneSignal.postNotification(new JSONObject("{'headings': {'en': 'Customer'}, 'contents': {'en':'Your order is ready!'}, 'include_player_ids': ['" + customerToken + "']}"), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Tab1_Order order = new Tab1_Order();
                    return order;
                case 1:
                    Tab2_Awaiting_Pickup await = new Tab2_Awaiting_Pickup();
                    return await;
                case 2:
                    Tab3_Completed complete = new Tab3_Completed();
                    return complete;
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
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Orders";
                case 1:
                    return "Awaiting Pickup";
                case 2:
                    return "Completed";
            }
            return null;
        }
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
                VendorActivity.this);

        alertDialog.setTitle("Log out");
        alertDialog.setMessage("Are you sure you want to log out?");

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent loginIntent = new Intent(VendorActivity.this,
                                LoginActivity.class);
                        VendorActivity.this.startActivity(loginIntent);
                        VendorActivity.this.finish();
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


    public class VendortestNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {

        @Override
        public void notificationReceived(OSNotification notification) {
            JSONObject data = notification.payload.additionalData;
            String customerId;
            String orderId;
            String itemName;
            Intent intent = new Intent();
            System.out.println("received");
            if (data != null) {
                // TODO check JSON object that is notified for correct key names
                customerId = data.optString("userId", null);
                orderId = data.optString("orderId", null);
                itemName = data.optString("item", null);
                if (customerId != null && orderId != null && itemName != null) {
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    intent.setAction("com.example.peter.myapplication.update");
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("itemName", itemName);
                    intent.putExtra("customerId", customerId);
                    sendBroadcast(intent);
                }
            }
        }
    }


}