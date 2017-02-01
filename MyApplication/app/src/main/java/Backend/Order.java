package Backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jyc96 on 2016-11-07.
 */

public class Order implements Serializable {
    private HashMap<FoodItem, Integer> items;
    private int customerId;

    public Order(HashMap<FoodItem,Integer> food, int id){
        items = food;
        customerId = id;
    }

    public int getId(){
        return customerId;
    }

}
