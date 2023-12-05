package com.example.androidrat.models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Message implements Serializable {
    public String address;
    public String body;
    public long date;

    public Message(String address, String body, long date) {
        this.address = address;
        this.body = body;
        this.date = date;
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("Address: %s, Body: %s, Date: %d", address, body, date);
    }
}
