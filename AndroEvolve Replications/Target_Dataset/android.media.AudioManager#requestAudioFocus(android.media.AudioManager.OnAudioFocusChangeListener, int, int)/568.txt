package com.telewave.lib.base.util;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;

import java.io.File;
import java.io.IOException;

/**
 * @author liwh
 * @date 2019/6/13
 */

public class RingUtils {
    static MediaPlayer sMediaPlayer;
    static Vibrator sVibrator;
    private static final long[] VIBRATE_PATTERN = new long[]{1000L, 1000L};

    public RingUtils() {
    }

    public static boolean startRing(Context c, String filePath) {
        AudioManager audioManager = (AudioManager) c.getSystemService("audio");
        audioManager.requestAudioFocus((OnAudioFocusChangeListener) null, 2, 2);
        int mode = audioManager.getRingerMode();
        switch (mode) {
            case 0:
            default:
                break;
            case 1:
                if (sVibrator == null) {
                    sVibrator = (Vibrator) c.getSystemService("vibrator");
                }

                sVibrator.vibrate(VIBRATE_PATTERN, 0);
                break;
            case 2:
                if (sVibrator == null) {
                    sVibrator = (Vibrator) c.getSystemService("vibrator");
                }

                try {
                    int value = System.getInt(c.getContentResolver(), "vibrate_when_ringing");
                    if (value == 1) {
                        sVibrator.vibrate(VIBRATE_PATTERN, 0);
                    }
                } catch (SettingNotFoundException var8) {
                    sVibrator.vibrate(VIBRATE_PATTERN, 0);
                }

                if (sMediaPlayer == null) {
                    sMediaPlayer = new MediaPlayer();
                } else if (sMediaPlayer.isPlaying()) {
                    sMediaPlayer.stop();
                    sMediaPlayer.reset();
                }

                sMediaPlayer.setLooping(true);
                sMediaPlayer.setAudioStreamType(2);

                try {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        return false;
                    }

                    sMediaPlayer.setDataSource(filePath);
                    sMediaPlayer.prepare();
                    sMediaPlayer.start();
                } catch (IllegalArgumentException var5) {
                    var5.printStackTrace();
                } catch (IllegalStateException var6) {
                    var6.printStackTrace();
                } catch (IOException var7) {
                    var7.printStackTrace();
                }
        }

        return true;
    }

    public static void startRingBack(Context c, String fileName) {
        if (sMediaPlayer == null) {
            sMediaPlayer = new MediaPlayer();
        } else if (sMediaPlayer.isPlaying()) {
            sMediaPlayer.stop();
            sMediaPlayer.reset();
        }

        sMediaPlayer.setLooping(true);
        sMediaPlayer.setAudioStreamType(0);
        AudioManager audioManager = (AudioManager) c.getSystemService("audio");
        audioManager.requestAudioFocus((OnAudioFocusChangeListener) null, 0, 2);

        try {
            AssetFileDescriptor afd = c.getAssets().openFd(fileName);
            sMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            sMediaPlayer.prepare();
            sMediaPlayer.start();
        } catch (IllegalArgumentException var4) {
            var4.printStackTrace();
        } catch (IllegalStateException var5) {
            var5.printStackTrace();
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    public static boolean startRing(Context c, int resid) {
        AudioManager audioManager = (AudioManager) c.getSystemService("audio");
        audioManager.requestAudioFocus((OnAudioFocusChangeListener) null, 2, 2);
        int mode = audioManager.getRingerMode();
        switch (mode) {
            case 0:
            default:
                break;
            case 1:
                if (sVibrator == null) {
                    sVibrator = (Vibrator) c.getSystemService("vibrator");
                }

                sVibrator.vibrate(VIBRATE_PATTERN, 0);
                break;
            case 2:
                if (sVibrator == null) {
                    sVibrator = (Vibrator) c.getSystemService("vibrator");
                }

                try {
                    int value = System.getInt(c.getContentResolver(), "vibrate_when_ringing");
                    if (value == 1) {
                        sVibrator.vibrate(VIBRATE_PATTERN, 0);
                    }
                } catch (SettingNotFoundException var8) {
                    sVibrator.vibrate(VIBRATE_PATTERN, 0);
                }

                if (sMediaPlayer == null) {
                    sMediaPlayer = new MediaPlayer();
                } else if (sMediaPlayer.isPlaying()) {
                    sMediaPlayer.stop();
                    sMediaPlayer.reset();
                }

                sMediaPlayer.setLooping(true);
                sMediaPlayer.setAudioStreamType(2);

                try {
                    AssetFileDescriptor afd = c.getResources().openRawResourceFd(resid);
                    if (afd == null) {
                        return false;
                    }

                    sMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    afd.close();
                    sMediaPlayer.prepare();
                    sMediaPlayer.start();
                } catch (IllegalArgumentException var5) {
                    var5.printStackTrace();
                    return false;
                } catch (IllegalStateException var6) {
                    var6.printStackTrace();
                    return false;
                } catch (IOException var7) {
                    var7.printStackTrace();
                    return false;
                }
        }

        return true;
    }

    public static void playAudio(Context c, int resid, boolean isLoop) {
        if (sMediaPlayer == null) {
            sMediaPlayer = new MediaPlayer();
        } else if (sMediaPlayer.isPlaying()) {
            sMediaPlayer.stop();
        }

        sMediaPlayer.reset();
        sMediaPlayer.setLooping(isLoop);
        sMediaPlayer.setAudioStreamType(0);
        AudioManager audioManager = (AudioManager) c.getSystemService("audio");
        audioManager.requestAudioFocus((OnAudioFocusChangeListener) null, 0, 2);

        try {
            AssetFileDescriptor afd = c.getResources().openRawResourceFd(resid);
            sMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            sMediaPlayer.prepare();
            sMediaPlayer.start();
        } catch (IllegalArgumentException var5) {
            var5.printStackTrace();
        } catch (IllegalStateException var6) {
            var6.printStackTrace();
        } catch (IOException var7) {
            var7.printStackTrace();
        }

    }

    public static void stop() {
        if (sMediaPlayer != null) {
            sMediaPlayer.stop();
            sMediaPlayer.reset();
            sMediaPlayer.release();
            sMediaPlayer = null;
        }

        if (sVibrator != null) {
            sVibrator.cancel();
            sVibrator = null;
        }

    }
}
