request
null
###CLASS_SEPARATOR###
@TargetApi(Build.VERSION_CODES.O)
class AudioFocusRequestOreo {

    private AudioFocusRequest audioFocusRequest;

    public AudioFocusRequestOreo(AudioManager.OnAudioFocusChangeListener listener) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build();
        audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build()).setAcceptsDelayedFocusGain(true).setWillPauseWhenDucked(true).setOnAudioFocusChangeListener(listener, new Handler()).build();
    }

    public AudioFocusRequest getAudioFocusRequest() {
        return new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build()).setAcceptsDelayedFocusGain(true).setWillPauseWhenDucked(true).setOnAudioFocusChangeListener(listener, new Handler()).build();
    }
}
###SEPARATOR###
