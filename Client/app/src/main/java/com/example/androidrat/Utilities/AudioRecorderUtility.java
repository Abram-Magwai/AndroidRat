package com.example.androidrat.Utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AudioRecorderUtility {
    private MediaRecorder mediaRecorder;
    private final String outputFile;

    public AudioRecorderUtility(Context context) {

        // Set the output file path
        while(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Waiting for audio permission to be granted");
        }
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_record.mp3";

        // Initialize the MediaRecorder
        mediaRecorder = new MediaRecorder();
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        }catch (Exception e) {
            Log.e("This", e.toString());
        }
        try {
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        }catch (Exception e) {
            System.out.println(e);
        }
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);
    }

    public void startRecording() {
        try {
            mediaRecorder.prepare();
        }catch (IOException e) {
            Log.e("RecordingUtility", "Failed preparing");
        }
        mediaRecorder.start();
    }

    public void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }
    public File getAudioFile() {
        return new File(outputFile);
    }
}
