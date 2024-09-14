package com.example.project4;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Random;

public class workerThread extends Thread {

    private Handler mHandler;
    private int player;
    boolean isRunning = true;
    private Looper mLooper;

    // Constructor to initialize the worker thread
    public workerThread(Handler mHandler, int player) {
        this.mHandler = mHandler;
        this.player = player;
    }

    @Override
    public void run() {
        // Prepare the looper for the thread
        Looper.prepare();
        mLooper = Looper.myLooper();

        while (isRunning) {
            // Generate random location
            Random random = new Random();
            int guessX = random.nextInt(10);
            int guessY = random.nextInt(10);

            // Create a message to send to the UI thread
            Message message = mHandler.obtainMessage();
            message.what = player;
            message.arg1 = guessX;
            message.arg2 = guessY;

            // Send the message to the UI thread
            mHandler.sendMessage(message);

            try {
                // Pause execution for 2 seconds
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        // Enter the message loop
        Looper.loop();
        // Quit the looper when the thread stops
        mLooper.quit();
    }

    public void stopThread() {
        isRunning = false;      // Set the flag to false to stop the thread
        mLooper.quit();         // Quit the looper
    }
}

