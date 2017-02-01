package com.example.peter.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {
    private String oneSignalToken = "";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OneSignal.startInit(this).init();
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.activity_register_acrivity);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPhone = (EditText) findViewById(R.id.etPhone);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final EditText etPasswordValidation = (EditText) findViewById(R.id.etPasswordValidation);
        final Button bSignup = (Button) findViewById(R.id.bSignup);
        final TextView tvLogin = (TextView) findViewById(R.id.tvLogin);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                oneSignalToken = userId;
            }
        });

        // Directly go back to log in page when user already registered
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(intent);
                RegisterActivity.this.finish();
            }
        });

        etPasswordValidation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    attemptRegister(etEmail, etPhone, etPassword, etPasswordValidation);
                    handled = true;
                }
                return handled;
            }
        });

        // Attempt to register to database with user information
        bSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister(etEmail, etPhone, etPassword, etPasswordValidation);
            }
        });
    }

    private void attemptRegister(EditText etEmail, EditText etPhone,
                                 EditText etPassword, EditText etPasswordValid){
        final String email = etEmail.getText().toString();
        final String phone = etPhone.getText().toString();
        final String password = etPassword.getText().toString();
        final String passwordValid = etPasswordValid.getText().toString();
        final String oneSignalToken = RegisterActivity.this.oneSignalToken;
        if(!isEmailValid(email)){
            etEmail.selectAll();
            etEmail.setError("invalid email format");
            etEmail.requestFocus();
        }
        else if(!isPhoneValid(phone)){
            etPhone.selectAll();
            etPhone.setError("invalid phone number");
            etPhone.requestFocus();
        }
        else if(!isPasswordValid(password)){
            etPassword.selectAll();
            etPassword.setError("password should be at least 8 characters");
            etPassword.requestFocus();
        }
        else if(!password.equals(passwordValid)){
            etPassword.selectAll();
            etPasswordValid.setText("");
            etPassword.setError("two passwords don't match");
            etPassword.requestFocus();
        }
        else {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        if (status.equals("success")) {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.putExtra("email", email);
                            RegisterActivity.this.startActivity(intent);
                            RegisterActivity.this.finish();

                        } else {
                            if (status.contains("duplicated email and phone number")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("This email has already been registered, would you like to log in now?")
                                        .setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                intent.putExtra("email", email);
                                                RegisterActivity.this.startActivity(intent);
                                                RegisterActivity.this.finish();
                                            }
                                        })
                                        .setNegativeButton("NO", null)
                                        .create()
                                        .show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("Something went wrong :( Please contact us")
                                        .setNegativeButton("OKAY", null)
                                        .create()
                                        .show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            //String URL = getIntent().getExtras().getString("URL");
            SharedPreferences sharedPref = getSharedPreferences("com.example.myapp.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
            String URL = sharedPref.getString("URL", null);
            RegisterRequest registerRequest = new RegisterRequest(email, phone, md5(password), oneSignalToken, responseListener, URL + "/user");
            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
            queue.add(registerRequest);
        }
    }
    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPhoneValid(String phone){
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

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
}