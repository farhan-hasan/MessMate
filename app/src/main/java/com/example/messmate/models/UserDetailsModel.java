package com.example.messmate.models;

public class UserDetailsModel {

    private String username, email, phone, key, mess_name;
    private Boolean is_resident;

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

    public UserDetailsModel(String username, String email, String phone, String key, String mess_name, Boolean is_resident) {
        this.username = username;
        this.email= email;
        this.phone = phone;
        this.key = key;
        this.mess_name = mess_name;
        this.is_resident = is_resident;
    }

}
