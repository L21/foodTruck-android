package Backend;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jyc96 on 2016-11-07.
 */

public class FoodMenu implements Serializable{
    private ArrayList<FoodItem> selection;

    public FoodMenu(){

    }

    public void addfood(FoodItem food){
        selection.add(food);
    }

    public void removefood(int id){
        FoodItem food = getFoodById(id);
        if(food != null) {
            selection.remove(food);
        }
    }

    public FoodItem getFoodById(int id){
        for(int i = 0; i < selection.size(); i++ ){
            if(selection.get(i).getId() == id){
                return selection.get(i);
            }
        }
        return null;
    }

}
