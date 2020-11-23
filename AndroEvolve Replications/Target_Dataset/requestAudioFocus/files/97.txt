package com.aobri.omaccollection.helper;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;

/**
 * Handler for audio playback in the miwok app activities.
 * Takes care of initializing media player and audio manager variables and listeners and retrieving required permissions for
 * audio focus and audio management.
 */
public class AudioPlaybackManager {

    private Context mContext;
    /**
     * Member variables used for managing audio focus for this activity.
     */
    private AudioManager mAudioManager;
    private AudioFocusRequest mAudioFocusRequest;
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    /**
     * Member varaibles necessary for word translation audio playback.
     */
    private MediaPlayer mMediaPlayer;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;

    public AudioPlaybackManager(Context context) {
        this.mContext = context;
    }

    public void initialize() {

        //request the AudioManager service from the android system.
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        // this is required setup! initialization of private variables and listeners used in the AudioManager and MediaPlayer setup.
        prepareAudioConfigurations();
        prepareAudioListeners();
    }


    public void playAudio(int audioRawResourceId) {


        // release the media player to stop any previous playbacks.
        releaseMediaPlayer();

        // request audio focus from the AudioManager and store the system response in audioFocusResult to be handled later.
        int audioFocusResult = requestAudioFocus();

        // handle the audio focus request result
        switch (audioFocusResult) {
            case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                // if focus is granted create a media player with the clicked word's audio resource ID as input.
                mMediaPlayer = MediaPlayer.create(mContext, audioRawResourceId);
                // set the OnCompletionListener to automatically handle what is done after playback is finished.
                mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
                // start audio playback
                mMediaPlayer.start();
                break;
            // other cases to be handled if needed.
            case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                break;
            case AudioManager.AUDIOFOCUS_REQUEST_DELAYED:
                break;
        }
    }

    /**
     * Prepares the global variables used for audio playback and audio focus requests.
     * These listeners and objects are necessary for handling focus and playback and must be declared and prepared.
     * Specifically, mOnAudioFocusChangeListener , mOnCompletionListener in all android versions.
     * In OREO (API 26 ) and above mAudioPlaybackAttributes and mAudioFocusRequest are also prepared.
     */
    private void prepareAudioConfigurations() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Handler mAudioFocusHandler = new Handler();
            AudioAttributes mAudioPlaybackAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(mAudioPlaybackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(mOnAudioFocusChangeListener, mAudioFocusHandler)
                    .build();
        }
    }

    private void prepareAudioListeners() {

        mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                releaseMediaPlayer();
            }
        };

        // Initialization of OnAudioFocusChangeListener.
        mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int i) {
                switch (i) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        // if audio focus is gained after a request, start audio/media playback.
                        mMediaPlayer.start();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        // if audio focus is lost release the media player and abandon focus.
                        releaseMediaPlayer();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        // if audio focus is lost temporarily (when changing to other apps),
                        // pause and restart the audio playback without releasing media player.
                        // in case of DUCKing, volume should be lowered instead but not required in this app.
                        mMediaPlayer.pause();
                        mMediaPlayer.seekTo(0);
                        break;
                }
            }
        };
    }


    /**
     * Request transient audio focus from the android system depending on the API used
     * for OREO or above the new AudioFocusRequest object is used
     * for older versions OnAudioFocusChangeListener is used directly.
     *
     * @return result the result of the audio focus request(used to handle audio playback on list item click).
     */
    private int requestAudioFocus() {
        // the result of audio focus request.
        int result;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // in OREO or above use new AudioFocusRequest (which supersedes the older version) to prepare for requests
            result = mAudioManager.requestAudioFocus(mAudioFocusRequest);

//        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
        } else {
            // in other older android SDK versions use the old audio focus request method.
            result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
        return result;
    }

    /**
     * Tells the system that the app no longer needs audio focus, and focus is abandoned.
     * implementation takes new OREO changes into account.
     * for OREO or above the new AudioFocusRequest object is used
     * for older versions OnAudioFocusChangeListener is used directly.
     */
    private void abandonAudioFocus() {

        // if android build SDK version is OREO or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // tell the AudioManager service to abandon audio focus using the previously prepared
            // in onCreate method , mAudioFocusRequest object.
            mAudioManager.abandonAudioFocusRequest(mAudioFocusRequest);

            // if android build SDK is older versions
        } else {
            // tell the AudioManager service to abandon audio focus using the previously prepared
            // in onCreate method , mOnAudioFocusChangeListener object.
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    /**
     * Clean up the media player by releasing its resources and abandoning audio focus.
     * This is important to help freeing up resources for other apps and should be called whenever
     * media player resources are not needed.
     */
    public void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            // since the media player is release audio focus should be abandoned
            abandonAudioFocus();

        }
    }
}
