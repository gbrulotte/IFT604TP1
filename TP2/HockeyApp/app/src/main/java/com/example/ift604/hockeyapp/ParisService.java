package com.example.ift604.hockeyapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ParisService extends Service {
    public ParisService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
