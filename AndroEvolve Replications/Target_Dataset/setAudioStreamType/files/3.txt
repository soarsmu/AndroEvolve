package com.example.franklin.smartreminder;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;



import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        Random R = new Random();
        int random = R.nextInt(24);


        Map<String, String> data = remoteMessage.getData();
        String testing = data.get("body");
        Log.d("GOTMESSAGE", "Message data payload: " + testing);
        // TODO(developer): Handle FCM messages here.

        if (random < 10){
            sendNotification(String.valueOf(random));
        }
        else if (random > 9 && random < 20){
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // TODO notify error to user or play next song
                    return true;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    // TODO Notify to user the completion of song or play next song
                }
            });

            if (random == 10){
                InputStream ins = getResources().openRawResource(com.example.franklin.smartreminder.R.raw.a0);
                try{
                    mediaPlayer = MediaPlayer.create(this, com.example.franklin.smartreminder.R.raw.a0);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
            else if (random == 11){
                InputStream ins = getResources().openRawResource(com.example.franklin.smartreminder.R.raw.a1);
                try{
                    mediaPlayer = MediaPlayer.create(this, com.example.franklin.smartreminder.R.raw.a1);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
            else if (random == 12){
                InputStream ins = getResources().openRawResource(com.example.franklin.smartreminder.R.raw.a2);
                try{
                    mediaPlayer = MediaPlayer.create(this, com.example.franklin.smartreminder.R.raw.a2);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
            else if (random == 13){
                InputStream ins = getResources().openRawResource(com.example.franklin.smartreminder.R.raw.a3);
                try{
                    mediaPlayer = MediaPlayer.create(this, com.example.franklin.smartreminder.R.raw.a3);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
            else if (random == 14){
                InputStream ins = getResources().openRawResource(com.example.franklin.smartreminder.R.raw.a4);
                try{
                    mediaPlayer = MediaPlayer.create(this, com.example.franklin.smartreminder.R.raw.a4);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
            else if (random == 15){
                InputStream ins = getResources().openRawResource(com.example.franklin.smartreminder.R.raw.a5);
                try{
                    mediaPlayer = MediaPlayer.create(this, com.example.franklin.smartreminder.R.raw.a5);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
            else if (random == 16){
                InputStream ins = getResources().openRawResource(com.example.franklin.smartreminder.R.raw.a6);
                try{
                    mediaPlayer = MediaPlayer.create(this, com.example.franklin.smartreminder.R.raw.a6);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
            else if (random == 17){
                InputStream ins = getResources().openRawResource(com.example.franklin.smartreminder.R.raw.a7);
                try{
                    mediaPlayer = MediaPlayer.create(this, com.example.franklin.smartreminder.R.raw.a7);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
            else if (random == 18){
                InputStream ins = getResources().openRawResource(com.example.franklin.smartreminder.R.raw.a8);
                try{
                    mediaPlayer = MediaPlayer.create(this, com.example.franklin.smartreminder.R.raw.a8);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
            else if (random == 19){
                InputStream ins = getResources().openRawResource(com.example.franklin.smartreminder.R.raw.a9);
                try{
                    mediaPlayer = MediaPlayer.create(this, com.example.franklin.smartreminder.R.raw.a9);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }


        }
        else if (random > 19){
            if (random == 20){
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                long[] timing = {0, 200, 200, 200};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createWaveform(timing, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    long[] pattern = {0, 200, 200, 200};
                    v.vibrate(pattern,-1);
                }
            }
            else if (random == 21){
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                long[] timing = {0, 200, 600, 200};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createWaveform(timing, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    long[] pattern = {0, 200, 600, 200};
                    v.vibrate(pattern,-1);
                }
            }
            else if (random == 22){
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                long[] timing = {0, 600, 200, 600};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createWaveform(timing, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    long[] pattern = {0, 600, 200, 600};
                    v.vibrate(pattern,-1);
                }
            }
            else if (random == 23){
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                long[] timing = {0, 600, 600, 600};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createWaveform(timing, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    long[] pattern = {0, 600, 600, 600};
                    v.vibrate(pattern,-1);
                }
            }
        }


        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("GOTMESSAGE", "Message data payload: " + testing);

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
            } else {
                // Handle message within 10 seconds

            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(getString(R.string.fcm_message))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
