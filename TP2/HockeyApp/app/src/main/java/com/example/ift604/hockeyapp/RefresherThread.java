package com.example.ift604.hockeyapp;

import android.os.Handler;

/**
 * Created by veip1702 on 2015-10-06.
 */
public class RefresherThread implements Runnable {
    private volatile Thread _thread;
    private int _refresherInterval;
    private Runnable _methodToRun;
    private Handler _handler;
    private boolean _isThreadPaused;

    private RefresherThread() {
        _isThreadPaused = false;
    }

    public RefresherThread(int interval, Handler handler, Runnable methodToRun) {
        _refresherInterval = interval;
        _methodToRun = methodToRun;
        _handler = handler;
    }

    public void start() {
        _thread = new Thread(this);
        _thread.start();
    }

    public void pause() {
        _isThreadPaused = true;
    }

    public void resume() {
        synchronized (_thread) {
            _isThreadPaused = false;
            _thread.notify();
        }
    }

    public void stop() {
        synchronized (_thread) {
            _thread.interrupt();
            _thread = null;
        }
    }

    public void run() {
        Thread thisThread = Thread.currentThread();
        while (_thread == thisThread) {
            try {
                Thread.sleep(_refresherInterval);
                if (_isThreadPaused) {
                    synchronized (_thread) {
                        while (_isThreadPaused)
                            _thread.wait();
                    }
                }
                _handler.post(_methodToRun);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isPaused() {
        return _isThreadPaused;
    }
}
