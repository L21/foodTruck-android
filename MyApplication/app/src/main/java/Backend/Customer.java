package Backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.id;

/**
 * Created by Tom on 2016-11-07.
 */

public class Customer {
    private int cId;
    private String cName;
    private String cEmail;
    private String cPassword;
    private ArrayList<FoodTruck> truckList = new ArrayList < FoodTruck >();

    public Customer(int id, String name, String email, String password){
        cId = id;
        cName = name;
        cEmail = email;
        cPassword = password;
    }

    public void MakeOrder(HashMap<FoodItem, Integer> foodItems) {
        Order order = new Order(foodItems, cId);
        //TODO notify vendor of order
    }


}