package com.example.androidrat.models;

import java.io.Serializable;

public class Contact implements Serializable {
    public String name;
    public String phoneNumber;

    public Contact(String phoneNumber, String name) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return String.format("Name: %s, PhoneNumber: %s", name, phoneNumber);
    }
}