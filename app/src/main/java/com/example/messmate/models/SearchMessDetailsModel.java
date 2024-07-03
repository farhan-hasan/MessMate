package com.example.messmate.models;

import com.example.messmate.adapters.SearchMessListRecyclerAdapter;

public class SearchMessDetailsModel {
    public String mess_name, mess_address, admin_phone, name_key, address_key;
    public Long available_seats;


    public SearchMessDetailsModel(){

    }

    public SearchMessDetailsModel(String mess_name, String mess_address, String admin_phone, Long available_seats) {
        this.mess_name = mess_name;
        this.mess_address = mess_address;
        this.admin_phone = admin_phone;
        this.available_seats = available_seats;
    }
    public String getMess_name() {
        return mess_name;
    }

    public String getMess_address() {
        return mess_address;
    }

    public String getAdmin_phone() {
        return admin_phone;
    }

    public Long getAvailable_seats() {
        return available_seats;
    }
}
