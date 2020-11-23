package com.ashoksm.atozforkids;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashoksm.atozforkids.dto.ItemsDTO;
import com.ashoksm.atozforkids.utils.DataStore;
import com.ashoksm.atozforkids.utils.DecodeSampledBitmapFromResource;
import com.ashoksm.atozforkids.utils.RandomNumber;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DragAndDropActivity extends AppCompatActivity {

    private static List<String> statusValues = new ArrayList<>();

    static {
        statusValues.add("Well Done!!!");
        statusValues.add("Great Job!!!");
        statusValues.add("Excellent!!!");
        statusValues.add("Marvellous!!!");
        statusValues.add("Bravo!!!");
    }

    private ImageView right;
    private Animation fadeInAnimation;
    private TextToSpeech textToSpeech;
    private int count = 0;
    private int currentCount = 0;
    private int randInt;
    private ItemsDTO itemsDTO;
    private ImageView mainBG;
    private Bitmap balloonBlue;
    private Bitmap balloonRed;
    private Bitmap balloonGreen;
    private Bitmap balloonPurple;
    private Bitmap balloonYellow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_and_drop);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        loadAd();

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.i("SliderActivity", "TextToSpeech onInit status::::::::" + status);
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.getDefault());
                    textToSpeech.setPitch(0.8f);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        try {
                            Set<Voice> voices = textToSpeech.getVoices();
                            for (Voice v : voices) {
                                if (v.getLocale().equals(Locale.getDefault())) {
                                    textToSpeech.setVoice(v);
                                }
                            }
                        } catch (Exception ex) {
                            Log.e("Voice not found", ex.getMessage());
                        }
                    }
                } else {
                    Log.i("SliderActivity",
                            "TextToSpeech onInit failed with status::::::::" + status);
                }
            }
        });

        mainBG = (ImageView) findViewById(R.id.main_bg);
        right = (ImageView) findViewById(R.id.right);
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        loadContent();

        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                right.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                right.setVisibility(View.GONE);
                if (count == currentCount) {
                    count = 0;
                    currentCount = 0;
                    loadContent();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        balloonBlue = DecodeSampledBitmapFromResource.execute(getResources(), R.drawable
                .ic_action_balloon_blue, 25, 25);
        balloonGreen = DecodeSampledBitmapFromResource.execute(getResources(), R.drawable
                .ic_action_balloon_green, 25, 25);
        balloonPurple = DecodeSampledBitmapFromResource.execute(getResources(), R.drawable
                .ic_action_balloon_purple, 25, 25);
        balloonRed = DecodeSampledBitmapFromResource.execute(getResources(), R.drawable
                .ic_action_balloon_red, 25, 25);
        balloonYellow = DecodeSampledBitmapFromResource.execute(getResources(), R.drawable
                .ic_action_balloon_yellow, 25, 25);

    }

    private void loadContent() {
        while (true) {
            List<ItemsDTO> itemsDTOs = null;
            int choice = RandomNumber.randInt(1, 4);
            switch (choice) {
                case 1:
                    itemsDTOs = DataStore.getInstance().getAlphabets();
                    break;
                case 2:
                    itemsDTOs = DataStore.getInstance().getAnimals();
                    break;
                case 3:
                    itemsDTOs = DataStore.getInstance().getFruits();
                    break;
                case 4:
                    itemsDTOs = DataStore.getInstance().getVegetables();
                    break;
            }
            if (itemsDTOs != null) {
                itemsDTO = itemsDTOs.get(RandomNumber.randInt(0, itemsDTOs.size() - 1));
                if (itemsDTO.getItemName().length() <= 5) {
                    break;
                }
            }
        }

        mainBG.setImageBitmap(DecodeSampledBitmapFromResource.execute(getResources(), itemsDTO
                .getImageResource(), 500, 500));

        List<String> chars = new ArrayList<>();
        for (char c : itemsDTO.getItemName().toCharArray()) {
            chars.add(String.valueOf(c));
        }
        Collections.shuffle(chars);
        count = itemsDTO.getItemName().length();

        for (int i = 1; i <= 5; i++) {
            int optionId = getResources().getIdentifier("option_" + i, "id", getPackageName());
            int choiceId = getResources().getIdentifier("choice_" + i, "id", getPackageName());
            TextView option = (TextView) findViewById(optionId);
            TextView choice = (TextView) findViewById(choiceId);
            if (itemsDTO.getItemName().length() >= i) {
                option.setVisibility(View.VISIBLE);
                choice.setVisibility(View.VISIBLE);
                option.setOnTouchListener(new ChoiceTouchListener());
                choice.setOnDragListener(new ChoiceDragListener());
                option.setText(chars.get(i - 1).toUpperCase());
                choice.setText(String.valueOf(itemsDTO.getItemName().charAt(i - 1)).toUpperCase());
                choice.setTextColor(Color.parseColor("#E5E4E2"));
                choice.setTag(null);
            } else {
                option.setVisibility(View.GONE);
                choice.setVisibility(View.GONE);
            }
        }
    }

    private void speak(String s) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech.speak(s, TextToSpeech.QUEUE_ADD, null, s);
            } else {
                textToSpeech.speak(s, TextToSpeech.QUEUE_ADD, null);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No TTS Found!!!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadAd() {
        // load ad
        final LinearLayout adParent = (LinearLayout) this.findViewById(R.id.adLayout);
        final AdView ad = new AdView(this);
        ad.setAdUnitId(getString(R.string.admob_banner_id));
        ad.setAdSize(AdSize.SMART_BANNER);

        final AdListener listener = new AdListener() {
            @Override
            public void onAdLoaded() {
                adParent.setVisibility(View.VISIBLE);
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                adParent.setVisibility(View.GONE);
                super.onAdFailedToLoad(errorCode);
            }
        };

        ad.setAdListener(listener);

        adParent.addView(ad);
        AdRequest.Builder builder = new AdRequest.Builder();
        AdRequest adRequest = builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        ad.loadAd(adRequest);
    }

    /**
     * ChoiceTouchListener will handle touch events on draggable views
     */
    private final class ChoiceTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                /*
                 * Drag details: we only need default behavior
				 * - clip data could be set to pass data as part of drag
				 * - shadow can be tailored
				 */
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                //start dragging the item touched
                int flags = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, shadowBuilder, view, flags);
                } else {
                    view.startDrag(data, shadowBuilder, view, flags);
                }
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * DragListener will handle dragged views being dropped on the drop area
     * - only the drop action will have processing added to it as we are not
     * - amending the default behavior for other parts of the drag process
     */
    private class ChoiceDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    //handle the dragged view being dropped over a drop view
                    View view = (View) event.getLocalState();
                    //view dragged item is being dropped on
                    TextView dropTarget = (TextView) v;
                    //view being dragged and dropped
                    TextView dropped = (TextView) view;
                    if (dropTarget.getText().equals(dropped.getText())) {
                        //stop displaying the view where it was before it was dragged
                        view.setVisibility(View.INVISIBLE);
                        //update the text in the target view to reflect the data being dropped
                        dropTarget.setText(dropped.getText());
                        //make it bold to highlight the fact that an item has been dropped
                        dropTarget.setTypeface(Typeface.DEFAULT_BOLD);
                        dropTarget.setTextColor(Color.parseColor("#AA00FF"));
                        //if an item has already been dropped here, there will be a tag
                        Object tag = dropTarget.getTag();
                        //if there is already an item here, set it back visible in its original place
                        if (tag != null) {
                            //the tag is the view id already dropped here
                            int existingID = (Integer) tag;
                            //set the original view visible again
                            findViewById(existingID).setVisibility(View.VISIBLE);
                        }
                        //set the tag in the target view being dropped on - to the ID of the view being dropped
                        dropTarget.setTag(dropped.getId());
                        right.startAnimation(fadeInAnimation);
                        currentCount++;

                        speak(dropped.getText().toString());
                        if (count == currentCount) {
                            speak(itemsDTO.getItemName());
                            while (true) {
                                int temp = RandomNumber.randInt(1, statusValues.size() - 1);
                                if (temp != randInt) {
                                    randInt = temp;
                                    break;
                                }
                            }
                            speak(statusValues.get(randInt));

                            new ParticleSystem(DragAndDropActivity.this, 20, balloonBlue, 5000)
                                    .setSpeedRange(0.1f, 0.2f).oneShot(mainBG, 20);
                            new ParticleSystem(DragAndDropActivity.this, 20, balloonGreen, 5000)
                                    .setSpeedRange(0.1f, 0.2f).oneShot(mainBG, 20);
                            new ParticleSystem(DragAndDropActivity.this, 20, balloonPurple, 5000)
                                    .setSpeedRange(0.1f, 0.2f).oneShot(mainBG, 20);
                            new ParticleSystem(DragAndDropActivity.this, 20, balloonRed, 5000)
                                    .setSpeedRange(0.1f, 0.2f).oneShot(mainBG, 20);
                            new ParticleSystem(DragAndDropActivity.this, 20, balloonYellow, 5000)
                                    .setSpeedRange(0.1f, 0.2f).oneShot(mainBG, 20);
                        }
                    } else {
                        speak("Try Again");
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}
