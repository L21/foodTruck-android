package com.example.peter.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity {
    ProgressDialog progress;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.activity_payment);

        final SharedPreferences sharedPref = getSharedPreferences("com.example.myapp.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        if(!sharedPref.contains("userID")){
            System.out.println("userID not found!!!!");
        }


        Toolbar toolbar = (Toolbar)findViewById(R.id.paymentToolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);

        final String userID = sharedPref.getString("userID", "not found");
        final String menuID = getIntent().getExtras().getString("menuID");
        final String truckID = getIntent().getExtras().getString("truckID");

        final Button btPay = (Button) findViewById(R.id.btPay);
        final EditText etCard = (EditText) findViewById(R.id.etCardNum);
        final EditText etCVC = (EditText) findViewById(R.id.etCVC);
        final EditText etExp = (EditText) findViewById(R.id.etExpire);
        final TextView tvItem = (TextView) findViewById(R.id.itemName);
        final RadioButton rbVisa = (RadioButton) findViewById(R.id.rbVisa);
        final RadioButton rbAmex = (RadioButton) findViewById(R.id.rbAmex);
        final RadioButton rbMaster = (RadioButton) findViewById(R.id.rbMaster);

        tvItem.setText("your order:\n" + getIntent().getStringExtra("foodName"));
        etCard.addTextChangedListener(new TextWatcher() {

            private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
            private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
            private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
            private static final char DIVIDER = '-';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // noop
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // noop
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrecntString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                }
            }

            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
                boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
                for (int i = 0; i < s.length(); i++) { // chech that every element is right
                    if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect &= divider == s.charAt(i);
                    } else {
                        isCorrect &= Character.isDigit(s.charAt(i));
                    }
                }
                return isCorrect;
            }

            private String buildCorrecntString(char[] digits, int dividerPosition, char divider) {
                final StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digits.length; i++) {
                    if (digits[i] != 0) {
                        formatted.append(digits[i]);
                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                            formatted.append(divider);
                        }
                    }
                }

                return formatted.toString();
            }

            private char[] getDigitArray(final Editable s, final int size) {
                char[] digits = new char[size];
                int index = 0;
                for (int i = 0; i < s.length() && index < size; i++) {
                    char current = s.charAt(i);
                    if (Character.isDigit(current)) {
                        digits[index] = current;
                        index++;
                    }
                }
                return digits;
            }
        });
        etExp.addTextChangedListener(new TextWatcher() {
            private static final int TOTAL_SYMBOLS = 5; // size of pattern 00/00
            private static final int TOTAL_DIGITS = 4; // max numbers of digits in pattern: 00 x 2
            private static final int DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
            private static final char DIVIDER = '/';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // noop
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // noop
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrecntString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                }
            }

            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
                boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
                for (int i = 0; i < s.length(); i++) { // chech that every element is right
                    if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect &= divider == s.charAt(i);
                    } else {
                        isCorrect &= Character.isDigit(s.charAt(i));
                    }
                }
                return isCorrect;
            }

            private String buildCorrecntString(char[] digits, int dividerPosition, char divider) {
                final StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digits.length; i++) {
                    if (digits[i] != 0) {
                        formatted.append(digits[i]);
                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                            formatted.append(divider);
                        }
                    }
                }

                return formatted.toString();
            }

            private char[] getDigitArray(final Editable s, final int size) {
                char[] digits = new char[size];
                int index = 0;
                for (int i = 0; i < s.length() && index < size; i++) {
                    char current = s.charAt(i);
                    if (Character.isDigit(current)) {
                        digits[index] = current;
                        index++;
                    }
                }
                return digits;
            }
        });

        rbVisa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rbAmex.setChecked(false);
                    rbMaster.setChecked(false);
                }
            }
        });

        rbMaster.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rbVisa.setChecked(false);
                    rbAmex.setChecked(false);
                }
            }
        });

        rbAmex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rbVisa.setChecked(false);
                    rbMaster.setChecked(false);
                }
            }
        });

        btPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etCard.getText().length() == 0){
                    etCard.setError("please enter card number");
                    etCard.requestFocus();
                    return;
                }
                else if(etCard.getText().length() != 19){
                    etCard.setError("incorrect card number");
                    etCard.requestFocus();
                    return;
                }
                if(etCVC.getText().length() == 0){
                    etCVC.setError("please enter CVV number");
                    etCVC.requestFocus();
                    return;
                }
                else if(etCVC.getText().length() != 3){
                    etCard.setError("incorrect CVV number");
                    etCard.requestFocus();
                    return;
                }
                else if(etExp.getText().length() == 0){
                    etExp.setError("please enter expiry date");
                    etExp.requestFocus();
                    return;
                }
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String truckToken = jsonResponse.getString("truckToken");
                            String orderID = jsonResponse.getString("orderId");
                            //String orderID = jsonResponse.getString("")
                            Intent OrderIntent = new Intent(PaymentActivity.this, OrderNumberActivity.class);
                            OrderIntent.putExtra("orderID", orderID);
                            OrderIntent.putExtra("GoingToOrderPage", "true");
                            try {
                                OneSignal.postNotification(new JSONObject("{'contents': {'en':'Test Message'}, 'data' : {'item':'"
                                        + getIntent().getStringExtra("foodName") +
                                        "','userId' : '" + sharedPref.getString("userToken", "null") +
                                        "','orderId' : '" + orderID +
                                        "'}, 'include_player_ids': ['" + truckToken + "']}"), null);
                                progress.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            PaymentActivity.this.startActivity(OrderIntent);
                            PaymentActivity.this.finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                SharedPreferences sharedPref = getSharedPreferences("com.example.myapp.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
                String URL = sharedPref.getString("URL", "ERROR");
                OrderRequest orderRequest = new OrderRequest(userID, truckID, menuID, responseListener, URL + "/order");
                RequestQueue queue = Volley.newRequestQueue(PaymentActivity.this);
                queue.add(orderRequest);
                progress = ProgressDialog.show(PaymentActivity.this, "Place Order",
                        "Placing your order", true);
            }
        });

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
}
