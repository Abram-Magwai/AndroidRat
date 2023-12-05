package com.example.androidrat.responses;

import com.example.androidrat.interfaces.Response;
import com.example.androidrat.models.Message;

import java.util.List;

public class ReceivedMessagesResponse implements Response {
    public List<Message> messageList;

    public ReceivedMessagesResponse(List<Message> messageList) {
        this.messageList = messageList;
    }
}
