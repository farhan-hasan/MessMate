package com.example.messmate.models;

import java.util.HashMap;
import java.util.Map;

public class MessDetailsModel {
    public String mess_key;
    public String admin;
    public String admin_phone;
    public String mess_name;
    public String mess_address;
    public int number_of_seats;
    public int available_seats;
    public int rent_per_seat;


    //public Meal breakfast;
    //public Meal lunch;
    //public Meal dinner;
    Map<String,Object> breakfast = new HashMap<>();
    Map<String,Object> lunch = new HashMap<>();
    Map<String,Object> dinner = new HashMap<>();
    //public MealRequest meal_request;
    Map<String,Object> meal_request = new HashMap<>();
    public Map<String, Object> residents;
    public Map<String, Object> dummy_user;


    // Required default constructor for Firebase object mapping
    public MessDetailsModel() {
        breakfast.put("menu", "");
        breakfast.put("price", 0);
        lunch.put("menu", "");
        lunch.put("price", 0);
        dinner.put("menu", "");
        dinner.put("price", 0);
        meal_request.put("breakfast", 0);
        meal_request.put("lunch", 0);
        meal_request.put("dinner", 0);
    }

}
