package com.example.messmate.models;

public class MessListCardModel {
    public String availabelSeats;
    public String messName, messAddress, adminPhone;

    public MessListCardModel(String availabelSeats, String messName, String messAddress, String adminPhone) {
        this.availabelSeats = availabelSeats;
        this.messName = messName;
        this.messAddress = messAddress;
        this.adminPhone = adminPhone;
    }

}
