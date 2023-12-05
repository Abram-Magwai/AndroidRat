package com.example.androidrat.responses;

import com.example.androidrat.interfaces.Response;
import com.example.androidrat.models.CallLog;

import java.util.List;

public class CallLogsResponse implements Response {
    public List<CallLog> callLogList;

    public CallLogsResponse(List<CallLog> callLogList) {
        this.callLogList = callLogList;
    }
}
