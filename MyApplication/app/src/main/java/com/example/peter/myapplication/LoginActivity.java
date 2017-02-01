package com.example.peter.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    ProgressDialog progress;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    //private String url = "http://192.168.0.11:8000";
    //private String url = "http://www.guoran.tech";
    private String url = "http://10.0.2.2:8000";
    private String oneSignalToken;
    private Boolean connectedToServer = true;
    private Toast toast;
    private Button mEmailSignInButton;
    private static boolean activityStarted;
    private UserLoginTask mAuthTask = null;
    private Boolean isVendor = false;


    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (activityStarted && getIntent() != null
                && (getIntent().getFlags() & Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) != 0) {
            finish();
            return;
        }

        activityStarted = true;


        OneSignal.startInit(this)
                //.setNotificationOpenedHandler(new OrderNotificationOpendHandler())
                .init();
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.activity_login);

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                oneSignalToken = userId;
            }
        });


        //url = "http://www.guoran.tech";

        // Set up the login form.
        new ServerConnectionTask().execute(url);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        // if user get back from register, auto complete
        if (getIntent().hasExtra("email")) {
            mEmailView.setText(getIntent().getStringExtra("email"));
            mPasswordView.requestFocus();
        }

        // open register page
        TextView mSignup = (TextView) findViewById(R.id.tvSignup);
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(signUpIntent);
                LoginActivity.this.finish();
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                final String email = mEmailView.getText().toString().trim();
                final String password = mPasswordView.getText().toString();
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    attemptLogin(password, email);
                    handled = true;
                }
                return handled;
            }
        });
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailView.getText().toString().trim();
                String password = mPasswordView.getText().toString();
                String oneSignalToken = LoginActivity.this.oneSignalToken;
                attemptLogin(password, email);
                }
            });
     }

    public void attemptLogin(String password, String email) {
            if(!connectedToServer){
                Context context = getApplicationContext();
                CharSequence text = "unable to connect to server";
                int duration = Toast.LENGTH_SHORT;
                toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.BOTTOM, 0, (int) mEmailView.getY());
                toast.show();
            }
            else if(email.isEmpty()){
                mEmailView.setError("not valid email!");
                mEmailView.requestFocus();
            }
            else if (!isEmailValid(email)) {
                mEmailView.setError("not valid email!");
                mEmailView.selectAll();
                mEmailView.requestFocus();

            } else if (!isPasswordValid(password)) {
                mPasswordView.setError("password too short!");
                mPasswordView.selectAll();
                mPasswordView.requestFocus();

            } else {
                /*
                mEmailSignInButton.setClickable(false);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this); //queue.add(loginRequest);
                String credential = "/user/login?isEmail=true&" +
                        "email=" + email +
                        "&password=" + password +
                        "&oneSignalToken=" + oneSignalToken;
                // Request a string response from the provided URL.
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                        url + credential, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mEmailSignInButton.setClickable(true);
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                Intent loadFoodTruckIntent = new Intent(LoginActivity.this,
                                        ChooseFoodTruckActivity.class);
                                loadFoodTruckIntent.putExtra("URL", LoginActivity.this.url);

                                //TODO: pass this to next activity
                                JSONObject userInfo = response.getJSONObject("user");
                                SharedPreferences sharedPref = getSharedPreferences("com.example.myapp.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("userID", userInfo.getString("id"));
                                editor.putString("URL", LoginActivity.this.url);
                                editor.apply();

                                loadFoodTruckIntent.putExtra("userJSON", userInfo.toString());

                                //TODO: ADD CODE TO OPEN VENDOR ACTIVITY HERE

                                if(!userInfo.getString("truck").equals("null")){
                                    Intent loadVendorIntent = new Intent(LoginActivity.this,
                                            VendorActivity.class);
                                    LoginActivity.this.startActivity(loadVendorIntent);
                                    LoginActivity.this.finish();
                                }
                                // open user activity
                                else {
                                    LoginActivity.this.startActivity(loadFoodTruckIntent);
                                    LoginActivity.this.finish();
                                }
                            } else {
                                mEmailView.setError("Incorrect email/password");
                                mEmailView.requestFocus();
                                mEmailView.selectAll();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //throw new IllegalArgumentException(error.getMessage()));
                        System.out.println(error.getMessage());
                        // TODO Auto-generated method stub
                        Context context = getApplicationContext();
                        CharSequence text = "login failed";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.setGravity(Gravity.BOTTOM, 0, (int) mEmailView.getY());
                        toast.show();
                    }
                });
                // Add the request to the RequestQueue.
                queue.add(jsObjRequest);
            }
            */
                //}
                if (mAuthTask != null) {
                    return;
                }
                mEmailView.setError(null);
                mPasswordView.setError(null);
                progress = ProgressDialog.show(LoginActivity.this, "Almost there",
                        "Logging you in", true);
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);
            }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 8;
    }


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        //final HashMap<String, Integer> trucks = new HashMap<String, Integer>();
        private ArrayList<String> truckNames = new ArrayList<String>();
        private ArrayList<Integer> truckIDs = new ArrayList<Integer>();

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            //todo connect to server and request foodtrucks
            //hardcoded for now

            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            String credential = "/user/login?isEmail=true&" +
                    "email=" + mEmail +
                    "&password=" + md5(mPassword) +
                    "&oneSignalToken=" + oneSignalToken;
            // Request a string response from the provided URL.
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    url + credential, null, future, future);
            // Add the request to the RequestQueue.
            queue.add(jsObjectRequest);

            try {
                JSONObject response = future.get(10, TimeUnit.SECONDS); // Blocks for at most 10 seconds.
                String status = response.getString("status");
                if (status.equals("success")) {

                    //TODO: pass this to next activity
                    JSONObject userInfo = response.getJSONObject("user");
                    SharedPreferences sharedPref = getSharedPreferences("com.example.myapp.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("userID", userInfo.getString("id"));
                    editor.putString("URL", LoginActivity.this.url);
                    editor.putString("userToken", oneSignalToken);
                    editor.apply();
                    if(!userInfo.getString("truck").equals("null")){
                        isVendor = true;
                    }
                    else {
                        //todo connect to server and request foodtrucks
                        // Request a string response from the provided URL.
                        RequestFuture<JSONArray> truckFuture = RequestFuture.newFuture();
                        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET,
                                LoginActivity.this.url + "/truck?app=1", null, truckFuture, truckFuture);
                        // Add the request to the RequestQueue.
                        queue.add(jsArrayRequest);
                        JSONArray truckResponse = truckFuture.get(10, TimeUnit.SECONDS); // Blocks for at most 10 seconds.
                        int size = truckResponse.length();
                        for (int i = 0; i < size; i++) {
                            JSONObject truck = truckResponse.getJSONObject(i);
                            String truckName = truck.getString("truckName");
                            int truckID = truck.getInt("id");
                            truckNames.add(truckName);
                            truckIDs.add(truckID);
                    }
                    isVendor = false;
                    }

                    return true;
                }
                else{
                    return false;
                }
            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            progress.dismiss();

            if (success) {
                if(isVendor){
                    Intent loadVendorIntent = new Intent(LoginActivity.this,
                            VendorActivity.class);
                    LoginActivity.this.startActivity(loadVendorIntent);
                    LoginActivity.this.finish();
                }
                // open user activity
                else {
                    Intent loadFoodTruckIntent = new Intent(LoginActivity.this,
                            ChooseFoodTruckActivity.class);
                    loadFoodTruckIntent.putStringArrayListExtra("truckNames", truckNames);
                    loadFoodTruckIntent.putIntegerArrayListExtra("truckIDs", truckIDs);
                    LoginActivity.this.startActivity(loadFoodTruckIntent);
                    LoginActivity.this.finish();
                }
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            progress.dismiss();
        }
    }

    private class ServerConnectionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000 /* milliseconds */);
                // Starts the query
                conn.connect();
                connectedToServer = true;
                return "";
            } catch (Exception e) {
                return "failed to connect to server";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if(!result.isEmpty()) {
                Context context = getApplicationContext();
                CharSequence text = result;
                int duration = Toast.LENGTH_SHORT;
                toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.BOTTOM, 0, (int) mEmailView.getY());
                toast.show();
                return;
            }
        }
    }
}
