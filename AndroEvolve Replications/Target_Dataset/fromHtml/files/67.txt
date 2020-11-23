package com.stacktips.speechtotext;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech tts;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private ImageButton mSpeakBtn;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String PREFS = "prefs";
    private static final String NAME = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(PREFS,0);
        editor = preferences.edit();

        mVoiceInputTv = (TextView) findViewById(R.id.voiceInput);
        mSpeakBtn = (ImageButton) findViewById(R.id.btnSpeak);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    speak("Hello, What is your name");

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }
    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);

        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                  //  mVoiceInputTv.setText(result.get(0));
                    if(result != null && result.size() > 0) {
                        mVoiceInputTv.append(Html.fromHtml("<p style=\"color:blue;\">User : "+result.get(0)+"</p>"));
                        // If user says hello, Ask for User's name & show the Greeting Text with users name.
                        if(result.get(0).equalsIgnoreCase("hello")) {
                            tts.speak("What is your name", TextToSpeech.QUEUE_FLUSH, null);
                            mVoiceInputTv.append(Html.fromHtml("<p style=\"color:red;\">Speaker : What is your name ?</p>"));
                        }else if(result.get(0).contains("name")){
                            // Set the Greeting by indexing
                            String name = result.get(0).substring(result.get(0).lastIndexOf(' ') + 1);
                            // Setting into Editor
                            editor.putString("name", name).apply();
                            tts.speak("Hello, "+name,
                                    TextToSpeech.QUEUE_FLUSH, null);
                            mVoiceInputTv.append(Html.fromHtml("<p style=\"color:red;\">Speaker : Hello, "+name+"</p>"));
                        }else if(result.get(0).contains("not feeling good")){
                            tts.speak("I can understand. Please tell your symptoms in short",
                                    TextToSpeech.QUEUE_FLUSH, null);
                            mVoiceInputTv.append(Html.fromHtml("<p style=\"color:red;\">Speaker : I can understand. Please tell your symptoms in short</p>"));
                        }else if(result.get(0).contains("thank you")){
                            tts.speak("Thank you too, "+preferences.getString("name","")+" Take care.", TextToSpeech.QUEUE_FLUSH, null);
                            mVoiceInputTv.append(Html.fromHtml("<p style=\"color:red;\">Speaker : Thank you too, "+preferences.getString("name","")+" Take care.</p>"));
                        }else if(result.get(0).contains("what time")){
                            // Speaking the Time for the User
                            SimpleDateFormat sdfDate =new SimpleDateFormat("HH:mm");//dd/MM/yyyy
                            Date now = new Date();
                            String[] strDate = sdfDate.format(now).split(":");
                            if(strDate[1].contains("00"))strDate[1] = "o'clock";
                            tts.speak("The time is : "+sdfDate.format(now), TextToSpeech.QUEUE_FLUSH, null);
                            mVoiceInputTv.append(Html.fromHtml("<p style=\"color:red;\">Speaker : The time is : "+sdfDate.format(now)+"</p>"));
                        }else if(result.get(0).contains("medicine")){
                            tts.speak("I think you have fever. Please take this medicine.",
                                    TextToSpeech.QUEUE_FLUSH, null);
                            mVoiceInputTv.append(Html.fromHtml("<p style=\"color:red;\">Speaker : I think you have fever. Please take this medicine.</p>"));
                        } else {
                            tts.speak("Sorry, I cant help you with that", TextToSpeech.QUEUE_FLUSH, null);
                            mVoiceInputTv.append(Html.fromHtml("<p style=\"color:red;\">Speaker : Sorry, I cant help you with that</p>"));
                        }
                    }
                }
                break;
            }

        }
    }
}
