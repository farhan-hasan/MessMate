package com.example.messmate.models;

public class UserDetailsModel {

    private String username, email, phone, key, mess_name;
    private int breakfast_amount, lunch_amount, dinner_amount, meal_amount;
    private Boolean is_resident;

    public int getBreakfast_amount() {
        return breakfast_amount;
    }

    public int getLunch_amount() {
        return lunch_amount;
    }

    public int getDinner_amount() {
        return dinner_amount;
    }

    public int getMeal_amount() {
        return meal_amount;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getKey() {
        return key;
    }

    public String getMess_name() {
        return mess_name;
    }

    public Boolean getIs_resident() {
        return is_resident;
    }

    UserDetailsModel() {
    }

    public UserDetailsModel(String username, String email, String phone, String key, String mess_name, int breakfast_amount, int lunch_amount, int dinner_amount, int meal_amount, Boolean is_resident) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.key = key;
        this.mess_name = mess_name;
        this.breakfast_amount = breakfast_amount;
        this.lunch_amount = lunch_amount;
        this.dinner_amount = dinner_amount;
        this.meal_amount = meal_amount;
        this.is_resident = is_resident;
    }
}
