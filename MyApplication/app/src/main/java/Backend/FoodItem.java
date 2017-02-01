package Backend;

import java.io.Serializable;

/**
 * Created by jyc96 on 2016-11-07.
 */

public class FoodItem implements Serializable {
    private double price;
    private int foodId;
    private String name;

    public FoodItem(double foodprice, int id, String food){
        price = foodprice;
        foodId = id;
        name = food;
    }

    public void changeName(String newname){
        name = newname;
    }

    public void changePrice(double newprice){
        price = newprice;
    }

    public int getId(){
        return foodId;
    }

}
