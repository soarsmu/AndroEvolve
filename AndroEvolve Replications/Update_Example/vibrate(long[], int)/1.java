package com.dbondarenko.shpp.simplealarmclock;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

/**
 * File: AlarmPlaySoundIntentService.java
 * Service in which the alarm sounds. The service is working in the foreground.
 * With this service you can start AlarmActivity to turn off the alarm.
 * Created by Dmitro Bondarenko on 22.09.2017.
 */
public class AlarmPlaySoundIntentService extends IntentService {

    private static final String LOG_TAG = "sound_service";
    private static final String ACTION_ALARM_PLAY_SOUND =
            "com.dbondarenko.shpp.simplealarmclock.action.AlarmPlaySound";
    private static final String ALARM_CLOCK_CHANNEL_ID =
            "com.dbondarenko.shpp.simplealarmclock.action.AlarmClock";
    private static final String ALARM_CLOCK_CHANNEL_NAME = "AlarmClockChannel";
    // Service identifier AlarmPlaceSoundInternetService to work in the foreground.
    private static final int ALARM_NOTIFICATION_ID = 1;

    private MediaPlayer mediaPlayerAlarm;
    private Vibrator vibratorAlarm;

    // Variable is used to stop the work of the service. If the value of the variable is true,
    // then the service should be stopped, if it is false, then the service continues to work.
    private boolean isAlarmTurnOff;

    public AlarmPlaySoundIntentService() {
        super("AlarmPlaySoundIntentService");
        // Set the ability to restart the service if it was stopped and its work is not completed.
        setIntentRedelivery(true);
    }

    /**
     * Returns the intent to run this service to perform the AlarmPlaySound action with
     * the specified parameters.
     *
     * @param context The Context of the application package implementing this class.
     * @return The Intent supplied to startService(Intent), as given.
     */
    public static Intent newIntent(Context context) {
        Utility.checkForNull(context);
        Intent intentToStartAlarmService = new Intent(context, AlarmPlaySoundIntentService.class);
        intentToStartAlarmService.setAction(ACTION_ALARM_PLAY_SOUND);
        return intentToStartAlarmService;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Set the value of the isAlarmTurnOff variable to true to cancel the alarm
        // (stop the service).
        isAlarmTurnOff = true;
        Log.d(LOG_TAG, "onDestroy()");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Utility.checkForNull(intent);
        final String action = intent.getAction();
        if (ACTION_ALARM_PLAY_SOUND.equals(action)) {
            startForeground(ALARM_NOTIFICATION_ID, getAlarmNotification());
            startAlarmActions();
            while (true) {
                if (isAlarmTurnOff) {
                    stopAlarmActions();
                    stopForeground(true);
                    break;
                }
            }
        }
    }

    /**
     * Start the action of music playback and turn off vibration.
     */
    private void startAlarmActions() {
        Log.d(LOG_TAG, "startAlarmActions()");
        playAlarmSound();
        turnOnVibration();
    }

    /**
     * Get notification about alarm clock.
     *
     * @return Notification about alarm clock.
     */
    private Notification getAlarmNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createAlarmNotificationChannel();
            return new Notification.Builder(getApplicationContext(), ALARM_CLOCK_CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(getPendingIntentToStartAlarmActivity())
                    .build();
        } else {
            return new Notification.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(getPendingIntentToStartAlarmActivity())
                    .build();
        }
    }

    /**
     * Create a notification channel for the alarm.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createAlarmNotificationChannel() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel alarmClockChannel = new NotificationChannel(ALARM_CLOCK_CHANNEL_ID,
                ALARM_CLOCK_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        // Sets whether notifications posted to this channel should display notification lights
        alarmClockChannel.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        alarmClockChannel.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        alarmClockChannel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        alarmClockChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(alarmClockChannel);
    }

    /**
     * Get PendingIntent to run AlarmActivity.
     *
     * @return PendingIntent to run AlarmActivity.
     */
    private PendingIntent getPendingIntentToStartAlarmActivity() {
        Intent intentToStartAlarmActivity = new Intent(getApplicationContext(), AlarmActivity.class);
        // Set this action as the beginning of a new task in this history stack.
        intentToStartAlarmActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(this, 0, intentToStartAlarmActivity, 0);
    }

    /**
     * Play alarm sound.
     */
    private void playAlarmSound() {
        Log.d(LOG_TAG, "playAlarmSound()");
        String filePath = AlarmPreference.getAlarmPreference()
                .getRingtoneSettings(getApplicationContext());
        // If the path to the file is empty, then play the default melody,
        // otherwise play the selected melody
        if (TextUtils.isEmpty(filePath)) {
            mediaPlayerAlarm = MediaPlayer.create(this, R.raw.alarm_sound);
        } else {
            Log.d(LOG_TAG, "playAlarmSound()" + filePath);
            mediaPlayerAlarm = new MediaPlayer();
            setAudioStream();
            try {
                mediaPlayerAlarm.setDataSource(filePath);
                mediaPlayerAlarm.prepare();
            } catch (IOException e) {
                Log.e(LOG_TAG, "playAlarmSound()" + e);
                e.printStackTrace();
            }
        }
        // Set  repeat the sound.
        mediaPlayerAlarm.setLooping(true);
        mediaPlayerAlarm.start();
    }

    /**
     * Set an audio stream for the MediaPlayer that plays the alarm sound.
     */
    private void setAudioStream() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayerAlarm.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build());
        } else {
            mediaPlayerAlarm.setAudioStreamType(AudioManager.STREAM_ALARM);
        }
    }

    /**
     * Turn on vibration.
     */
    private void turnOnVibration() {
        Log.d(LOG_TAG, "turnOnVibration()");
        vibratorAlarm = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // If there is vibration on this device, then run it.
        if (vibratorAlarm.hasVibrator()) {
            long[] pattern = {0, 400, 800};
            int repeat = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            	VibrationEffect effect = VibrationEffect.createWaveform(pattern, repeat);
                vibratorAlarm.vibrate(effect);
            } else {
                vibratorAlarm.vibrate(pattern, repeat);
            }
        }
    }

    /**
     * Stop the action of music playback and turn off vibration.
     */
    private void stopAlarmActions() {
        Log.d(LOG_TAG, "stopAlarmActions()");
        mediaPlayerAlarm.stop();
        vibratorAlarm.cancel();
    }
}