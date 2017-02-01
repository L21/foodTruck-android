package com.example.peter.myapplication;

import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by Tom on 2016-12-01.
 */

public class VendorNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        JSONObject data = result.notification.payload.additionalData;
        String customerId;
        String orderId;
        String itemName;

        if (data != null) {
            // TODO check JSON object that is notified for correct key names
            customerId = data.optString("userId", null);
            orderId = data.optString("orderId", null);
            itemName = data.optString("item", null);

            if (customerId != null && orderId != null && itemName != null)
                VendorActivity.addOrderInfo(orderId, itemName, customerId);
                VendorActivity.addorder(orderId + ": " + itemName);
        }
    }
}
