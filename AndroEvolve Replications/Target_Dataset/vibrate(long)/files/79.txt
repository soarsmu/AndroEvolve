package com.kolserdav.clock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

public class Alert extends AppCompatActivity {

    protected static MediaPlayer mp;
    protected static Handler handler;

    protected static Handler handler1;
    protected static Vibrator vibrator;
    protected static int countVibrate;
    protected static int currentTimeVibrate;
    protected static int currentDurationVibrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        mp = MediaPlayer.create(this, R.raw.sound1);
        playSound();
    }

    public Alert() {
        handler = new Handler();
        handler1 = new Handler();
        countVibrate = 0;
        currentTimeVibrate = 0;
        currentDurationVibrate = 500;
    }

    public void playSound() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrateHandler();
        // TODO добавить вывод потока на динамик
        mp.start();
        final Integer duration = mp.getDuration();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                boolean playing = mp.isPlaying();
                if (!playing) {
                    mp.start();
                }
                handler.postDelayed(this, duration + 5000);
            }
        };
        handler.post(task);
    }

    public void stopSound(View v) {
        handler.removeCallbacksAndMessages(null);
        mp.stop();
        mp.release();
        stopVibrate();
        Context context = getBaseContext();
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
    }

    protected void vibrateHandler() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                getLongVibrate();
                if (vibrator != null && vibrator.hasVibrator()) {
                    vibrator.vibrate(currentTimeVibrate);
                }
                handler1.postDelayed(this, currentDurationVibrate);
            }
        };
        handler1.post(task);
    }

    protected void stopVibrate() {
        handler1.removeCallbacksAndMessages(null);
        vibrator.cancel();
    }

    protected void getLongVibrate() {
        int count = countVibrate;
        switch (count) {
            case 0:
                currentTimeVibrate = 200;
                currentDurationVibrate = 1500;
                countVibrate ++;
                break;
            case 1:
                currentTimeVibrate = 201;
                currentDurationVibrate = 1500;
                countVibrate ++;
                break;
            case 2:
                currentTimeVibrate = 300;
                currentDurationVibrate = 1300;
                countVibrate ++;
                break;
            case 3:
                currentTimeVibrate = 302;
                currentDurationVibrate = 1300;
                countVibrate ++;
                break;
            case 4:
                currentTimeVibrate = 400;
                currentDurationVibrate = 1100;
                countVibrate ++;
                break;
            case 5:
                currentTimeVibrate = 500;
                currentDurationVibrate = 900;
                countVibrate ++;
                break;
            case 6:
                currentTimeVibrate = 600;
                currentDurationVibrate = 700;
                countVibrate ++;
                break;
            case 7:
                currentTimeVibrate = 603;
                currentDurationVibrate = 700;
                countVibrate ++;
                break;
            case 8:
                currentTimeVibrate = 700;
                currentDurationVibrate = 700;
                countVibrate ++;
                break;
            case 9:
                currentTimeVibrate = 700;
                currentDurationVibrate = 700;
                countVibrate = 0;
        }
    }
}
