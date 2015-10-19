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

    public static Handler _serviceHandler = null;
    public static boolean _isServiceRunning = false;
    private ArrayList<ThreadReceiver> _receivers;

    @Override
    public void onCreate() {
        super.onCreate();
        Log("onCreate()");
        _receivers = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ThreadMessageHandler threadMessageHandler = new ThreadMessageHandler();
        threadMessageHandler.start();
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
        for (ThreadReceiver r : _receivers) {
            r.interrupt();
        }
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
                        Socket clientSocket = new Socket(InetAddress.getByName(MatchView.SERVER_IP), 8081);
                        //clientSocket.setTcpNoDelay(true);
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        outToServer.writeBytes(String.format("Bet~%s~%s~%d\n", m.matchId, m.team, m.amount));
                        //outToServer.flush();
                        Log("Sent bet to server");

                        ThreadReceiver t = new ThreadReceiver(clientSocket);
                        t.start();
                        _receivers.add(t);
                        //clientSocket.close();
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

        private Socket _socket;

        private ThreadReceiver() {}

        public ThreadReceiver(Socket clientSocket) {
            _socket = clientSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
                String receivedData = inFromServer.readLine();
                Log("Received some data from server !");
                if (receivedData != null) {
                    receivedData = receivedData.trim();
                    Log("Received data : " + receivedData);
                    _receivers.remove(this);
                    _socket.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
