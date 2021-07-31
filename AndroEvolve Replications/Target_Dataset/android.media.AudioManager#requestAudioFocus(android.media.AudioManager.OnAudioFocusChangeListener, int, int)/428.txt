package com.codewithtimzowen.contactlistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;

public class FamilyActivity extends AppCompatActivity {

    MediaPlayer mMediaPlayer;
    MediaPlayer.OnCompletionListener mOnCompletionListener = mp -> releaseMediaResources();
    AudioManager mAudioManger;
    AudioManager.OnAudioFocusChangeListener changeListener = focusChange -> {
        if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            //Pause the audio and start playing from start if we loose the audio temporarily
            mMediaPlayer.pause();
            mMediaPlayer.seekTo(0);
        } else if (focusChange == AUDIOFOCUS_GAIN) {
            //we have gained back audio thus we need to continue playing audio, but we start from zero
            mMediaPlayer.start();
        } else if (focusChange == AUDIOFOCUS_LOSS) {
            //this means we have lost completely audio, so we release resources
            releaseMediaResources();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        mAudioManger = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        ArrayList<Contacts> contacts = new ArrayList<>();

        contacts.add(new Contacts("Mummny", "0700 222 333", R.drawable.family_mother, R.raw.family_mother));
        contacts.add(new Contacts("Daddy", "0710 456 670", R.drawable.family_father, R.raw.family_father));
        contacts.add(new Contacts("brathe", "0123 345 768", R.drawable.family_older_brother, R.raw.family_older_brother));
        contacts.add(new Contacts("Siz", "0768 885 678", R.drawable.family_older_sister, R.raw.family_older_sister));
        contacts.add(new Contacts("babe", "0790 456 098", R.drawable.family_younger_sister, R.raw.family_younger_sister));
        contacts.add(new Contacts("Mummny", "0700 222 333", R.drawable.family_mother, R.raw.family_mother));
        contacts.add(new Contacts("Daddy", "0710 456 670", R.drawable.family_father, R.raw.family_younger_sister));
        contacts.add(new Contacts("brathe", "0123 345 768", R.drawable.family_older_brother, R.raw.family_younger_brother));
        contacts.add(new Contacts("Siz", "0768 885 678", R.drawable.family_older_sister, R.raw.family_grandfather));
        contacts.add(new Contacts("babe", "0790 456 098", R.drawable.family_younger_sister, R.raw.family_grandmother));
        contacts.add(new Contacts("Mummny", "0700 222 333", R.drawable.family_mother, R.raw.family_son));
        contacts.add(new Contacts("Mummny", "0700 222 333", R.drawable.family_mother, R.raw.family_mother));
        contacts.add(new Contacts("Daddy", "0710 456 670", R.drawable.family_father, R.raw.family_father));
        contacts.add(new Contacts("brathe", "0123 345 768", R.drawable.family_older_brother, R.raw.family_older_brother));
        contacts.add(new Contacts("Siz", "0768 885 678", R.drawable.family_older_sister, R.raw.family_older_sister));
        contacts.add(new Contacts("babe", "0790 456 098", R.drawable.family_younger_sister, R.raw.family_younger_sister));
        contacts.add(new Contacts("Mummny", "0700 222 333", R.drawable.family_mother, R.raw.family_mother));
        contacts.add(new Contacts("Daddy", "0710 456 670", R.drawable.family_father, R.raw.family_younger_sister));
        contacts.add(new Contacts("brathe", "0123 345 768", R.drawable.family_older_brother, R.raw.family_younger_brother));
        contacts.add(new Contacts("Siz", "0768 885 678", R.drawable.family_older_sister, R.raw.family_grandfather));
        contacts.add(new Contacts("babe", "0790 456 098", R.drawable.family_younger_sister, R.raw.family_grandmother));
        contacts.add(new Contacts("Mummny", "0700 222 333", R.drawable.family_mother, R.raw.family_son));


        ContactsAdapter contactsAdapter = new ContactsAdapter(this, contacts, R.color.family_bg);

        ListView lv = findViewById(R.id.list_view);

        lv.setOnItemClickListener(((parent, view, position, id) -> {

            Contacts currentContact = contacts.get(position);

            // Release the media player Resources
            releaseMediaResources();

            int results = mAudioManger.requestAudioFocus(changeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

            if (results == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                mMediaPlayer = MediaPlayer.create(this, currentContact.getSoundResourceID());
                mMediaPlayer.start();

                mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            }
        }));

        lv.setAdapter(contactsAdapter);
    }

    public void releaseMediaResources() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            mAudioManger.abandonAudioFocus(changeListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaResources();
    }
}