package com.example.messmate.models;

public class MessListCardModel {
    public String availableSeats;
    public String messName, messAddress, adminPhone;

    public MessListCardModel(String availabelSeats, String messName, String messAddress, String adminPhone) {
        this.availableSeats = availabelSeats;
        this.messName = messName;
        this.messAddress = messAddress;
        this.adminPhone = adminPhone;
    }

}
