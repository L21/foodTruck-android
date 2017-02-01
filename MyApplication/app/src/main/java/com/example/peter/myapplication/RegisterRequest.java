package com.example.peter.myapplication;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Peter on 2016-10-15.
 */

public class RegisterRequest extends StringRequest {
    //TODO: add server php information here!
    //private static final String REGISTER_REQUEST_URL = "http://www.guoran.tech/user";
    private Map<String, String> params;

    public RegisterRequest(String email, String phone, String password, String oneSignalToken,
                           Response.Listener<String> listener, String URL){
        super(Method.POST, URL, listener, null);
        params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);
        params.put("phone", phone);
        params.put("oneSignalToken", oneSignalToken);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
