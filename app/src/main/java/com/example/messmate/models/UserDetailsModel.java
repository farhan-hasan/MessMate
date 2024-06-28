package com.example.messmate.models;

public class UserDetailsModel {

    public String username, email, phone, key, mess_name;
    public Boolean is_resident;

    UserDetailsModel() {
    }

    public UserDetailsModel(String username, String email, String phone, String key, String mess_name, Boolean is_resident) {
        this.username = username;
        this.email= email;
        this.phone = phone;
        this.key = key;
        this.mess_name = mess_name;
        this.is_resident = is_resident;
    }

}
