package com.example.androidrat.responses;

import com.example.androidrat.interfaces.Response;

import java.io.File;

public class RecordingResponse implements Response {
    public byte[] audioBytes;

    public RecordingResponse(byte[] audioBytes) {
        this.audioBytes = audioBytes;
    }
}
