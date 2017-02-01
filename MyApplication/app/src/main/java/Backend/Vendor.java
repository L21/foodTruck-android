package Backend;

import java.util.ArrayList;

/**
 * Created by Tom on 2016-11-07.
 */

public class Vendor {
    private int vId;
    private String vName;
    private String vEmail;
    private String vPassword;
    //public FoodTruck truck;
    private ArrayList<Order> orderList = new ArrayList<Order>();

    public Vendor(int id, String name, String email, String password) {
        vId = id;
        vName = name;
        vEmail = email;
        vPassword = password;
    }

    public void AcceptOrder() {
        //TODO deserialize the order from a custormer if order is accepted
    }

    public void CompleteOrder(Order order) {
        orderList.remove(orderList.indexOf(order));
    }
}