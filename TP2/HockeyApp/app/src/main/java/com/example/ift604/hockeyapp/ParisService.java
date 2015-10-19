package com.example.ift604.hockeyapp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.ift604.hockeyapp.models.ParisMessage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ParisService extends Service {

    private static final String TAG = "ParisService";

    //public ArrayList<String> _matchIds;
    public static Handler _serviceHandler = null;
    public static boolean _isServiceRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log("onCreate()");
        //_matchIds = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ThreadMessageHandler threadMessageHandler = new ThreadMessageHandler();
        threadMessageHandler.start();

        ThreadReceiver threadReceiver = new ThreadReceiver();
        threadReceiver.start();

        Log("onStartCommand()");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log("onDestroy()");
        _isServiceRunning = false;
    }

    private void Log(String message) {
        Log.i(TAG, message);
    }

    private class ThreadMessageHandler extends Thread {
        private static final String INNER_TAG = "ThreadMessageHandler";

        public void run() {
            this.setName(INNER_TAG);
            _isServiceRunning = true;
            Looper.prepare();

            _serviceHandler = new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    Log("Message received in ThreadHandler");
                    ParisMessage m = (ParisMessage)msg.obj;
                    //_matchIds.add(m.matchId);
                    try {
                        Socket clientSocket = new Socket(InetAddress.getByName("10.0.2.2"), 8081);
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        outToServer.writeBytes(String.format("Bet~%s~%s~%d", m.matchId, m.team, m.amount));
                        clientSocket.close();
                        Log("Sent bet to server");
                    } catch (Exception ex) {
                        Log(ex.getMessage());
                        if (MatchView._uiHandler != null) {
                            Message msgToUI = new Message();
                            msgToUI.what = 1;
                            MatchView._uiHandler.sendMessage(msgToUI);
                        }
                    }

                }
            };

            Looper.loop();
        }
    }

    private class ThreadReceiver extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Socket clientSocket = new Socket(InetAddress.getByName("10.0.2.2"), 8081);
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String receivedData = inFromServer.readLine();
                    if (receivedData != null) {
                        receivedData = receivedData.trim();
                        Log("Received data : " + receivedData);
                    }
                } catch (IOException ioEx) {
                    ioEx.printStackTrace();
                }
            }
        }
    }
}
