package com.example.androidrat.models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CallLog  implements Serializable {
    public String name;
    public String phoneNumber;
    public String type;
    public Date date;
    public int duration;

    public CallLog(String name, String phoneNumber, String type, Date date, int duration) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.date = date;
        this.duration = duration;
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        String pattern = "yyyy-MM-dd HH:mm:ss"; // You can customize the pattern
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return String.format("Name: %s, PhoneNumber: %s, Type: %s, Date: %s, Duration: %d", name, phoneNumber, type, dateFormat.format(date), duration);
    }
}
