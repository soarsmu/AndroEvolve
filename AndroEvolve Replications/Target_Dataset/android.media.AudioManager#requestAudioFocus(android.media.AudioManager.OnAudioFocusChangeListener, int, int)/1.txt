package fr.wildcodeschool.mediaplayer.player.manager;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

import fr.wildcodeschool.mediaplayer.MainActivity;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
import static android.media.AudioManager.AUDIOFOCUS_REQUEST_FAILED;
import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
import static android.media.AudioManager.STREAM_MUSIC;

final public class WildAudioManager implements AudioManager.OnAudioFocusChangeListener {
  // Instance of the WildAudioManager class
  private static final WildAudioManager mInstance = new WildAudioManager();

  private AudioManager mAudioManager;
  private WildAudioManagerListener mListener;

  // Focus state
  private final Object mFocusLock = new Object();
  private Integer mFocusState = AUDIOFOCUS_LOSS;

  // Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
  private AudioFocusRequest mFocusRequest = null;
  private AudioAttributes mAudioAttributes = null;

  /**
   * Singleton accessor
   * @return the unique instance of the class
   */
  public static WildAudioManager getInstance() {
    return mInstance;
  }

  /**
   * Constructor
   */
  private WildAudioManager() {
    // Create audio manager
    mAudioManager = (AudioManager)MainActivity
      .getAppContext()
      .getSystemService(Context.AUDIO_SERVICE);

    // Define usage of audio request
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      mAudioAttributes = new AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build();
    }
  }

  /**
   * Inform the audio manager that the application request the audio focus
   * @return if request is granted or not
   */
  public boolean requestAudioFocus() {
    if (AUDIOFOCUS_GAIN == mFocusState)
      return true;

    int state;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      // Request audio focus
      mFocusRequest = new AudioFocusRequest
        .Builder(AUDIOFOCUS_GAIN)
        .setAudioAttributes(mAudioAttributes)
        .setAcceptsDelayedFocusGain(false)
        .setOnAudioFocusChangeListener(this)
        .build();

      state = mAudioManager.requestAudioFocus(mFocusRequest);
    } else {
      state = mAudioManager.requestAudioFocus(this, STREAM_MUSIC, AUDIOFOCUS_GAIN);
    }

    // Check and return if request is granted or not
    if (AUDIOFOCUS_REQUEST_GRANTED == state) {
      // Intrinsic lock
      synchronized(mFocusLock) {
        mFocusState = AUDIOFOCUS_GAIN;
      }
      return true;
    }
    return false;
  }

  /**
   * Inform the audio manager that the application release the audio focus
   * @return if request is granted or not
   */
  public boolean releaseAudioFocus() {
    int state = AUDIOFOCUS_REQUEST_FAILED;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      if (null != mFocusRequest) {
        state = mAudioManager.abandonAudioFocusRequest(mFocusRequest);
        mFocusRequest = null;
      }
    } else {
      state = mAudioManager.abandonAudioFocus(this);
    }

    // Check and return if request is granted or not
    if (AUDIOFOCUS_REQUEST_GRANTED == state) {
      // Intrinsic lock
      synchronized(mFocusLock) {
        mFocusState = AUDIOFOCUS_LOSS;
      }
      return true;
    }
    return false;
  }

  @Override
  public void onAudioFocusChange(int focusChange) {
    // Intrinsic lock
    synchronized(mFocusLock) {
      mFocusState = focusChange;
    }

    switch (focusChange) {
      case AUDIOFOCUS_GAIN:
        if (null != mListener) mListener.audioFocusGain(true);
        break;
      case AUDIOFOCUS_LOSS:
      case AUDIOFOCUS_LOSS_TRANSIENT:
      case AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
        if (releaseAudioFocus()) {
          if (null != mListener) mListener.audioFocusGain(false);
        }
        break;
      default:
        break;
    }
  }

  /**
   * Store the audioManager listener
   * @param listener new audioManager listener
   */
  public void setAudioManagerListener(WildAudioManagerListener listener) {
    mListener = listener;
  }
}
