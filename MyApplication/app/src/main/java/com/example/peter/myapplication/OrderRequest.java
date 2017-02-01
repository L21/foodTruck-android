package com.example.peter.myapplication;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Peter on 2016-12-03.
 */

public class OrderRequest extends StringRequest{
    //TODO: add server php information here!
    //private static final String REGISTER_REQUEST_URL = "http://10.0.0.2/order";
    private Map<String, String> params;

    public OrderRequest(String userId, String truckId, String itemId,
                        Response.Listener<String> listener, String URL){
        super(Method.POST, URL, listener, null);
        params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("truckId", truckId);
        params.put("menuId", itemId);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
