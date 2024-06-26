package com.example.messmate.models;

public class MessListCardModel {
    MessListCardModel(){

    }
    public String availabelSeats;
    public String messName, messAddress, adminPhone;

    public MessListCardModel(String availabelSeats, String messName, String messAddress, String adminPhone) {
        this.availabelSeats = availabelSeats;
        this.messName = messName;
        this.messAddress = messAddress;
        this.adminPhone = adminPhone;
    }

    public String getAvailabelSeats() {
        return availabelSeats;
    }

    public String getMessName() {
        return messName;
    }

    public String getMessAddress() {
        return messAddress;
    }

    public String getAdminPhone() {
        return adminPhone;
    }
}
