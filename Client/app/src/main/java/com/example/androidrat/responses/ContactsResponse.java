package com.example.androidrat.responses;

import com.example.androidrat.interfaces.Response;
import com.example.androidrat.models.Contact;

import java.util.List;

public class ContactsResponse implements Response {
    public List<Contact> contactList;

    public ContactsResponse(List<Contact> contactList) {
        this.contactList = contactList;
    }
}
