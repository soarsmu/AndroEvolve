package com.gfarcasiu.cardgameapp;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gfarcasiu.client.Client;
import com.gfarcasiu.client.MultiServer;
import com.gfarcasiu.game.Game;
import com.gfarcasiu.game.Player;
import com.gfarcasiu.game.PlayingCard;
import com.gfarcasiu.utilities.HelperFunctions;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;


public class HandActivity extends Activity {
    private static String uniqueId;
    private boolean isServer;

    private View viewBeingDragged; // TODO think of a better name...
    private HashMap<View, PlayingCard> cardMap = new HashMap<>(); // TODO there must be a better way

    private long commandTimeStamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        setContentView(R.layout.activity_hand);

        // Initialize game and player settings
        isServer = getIntent().getExtras().getBoolean("isServer");

        Log.i("Debug", "<Device is sever: " + isServer + "/>");

        if (getIntent().getExtras().getBoolean("isNewGame")) {
            // Add current player to game
            uniqueId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Player currentPlayer = new Player(uniqueId, 52);

            try {
                executeAction(Game.class.getMethod("addPlayer", Player.class), currentPlayer);
            } catch (NoSuchMethodException e) {
                Log.e("Error", "<Player could not be added/>");
                e.printStackTrace();

                this.onStop();
                return;
            }
        } else {
            // Populate cards
            PlayingCard[] cards = HelperFunctions.getGame(isServer).getPlayer(uniqueId).getCards();
            for (PlayingCard card : cards)
                displayCard(card);
        }

        // Set listeners
        MyDragEventListener dragEventListener = new MyDragEventListener();
        findViewById(R.id.bottom).setOnDragListener(dragEventListener);
        findViewById(R.id.middle).setOnDragListener(dragEventListener);
        findViewById(R.id.top).setOnDragListener(dragEventListener);
        findViewById(R.id.deck_button).setOnDragListener(dragEventListener);
        findViewById(R.id.trash_button).setOnDragListener(dragEventListener);
        findViewById(R.id.table_button).setOnDragListener(dragEventListener);
    }

    public void toTable(View view) {
        this.onStop(); // Perhaps not necessary

        Intent intent = new Intent(this, TableActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("isServer", isServer);
        intent.putExtra("uniqueId", uniqueId);

        startActivity(intent);
    }

    public void drawCard(View view) {
        PlayingCard playingCard = Game.getRandomCard(HelperFunctions.getGame(isServer).getDeck());

        try {
            executeAction(Game.class.getMethod("deckToPlayer",
                PlayingCard.class, String.class), playingCard, uniqueId);
        } catch (NoSuchMethodException e) {
            Log.e("Error", "<Couldn't execute draw card/>");
            e.printStackTrace();
        }

        displayCard(playingCard);
    }

    private final class MyTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view.getParent(), 0);

                ((LinearLayout)view.getParent()).removeView(view);

                cardMap.put(view, HelperFunctions.getPlayingCardFromImageName((String)view.getTag()));

                viewBeingDragged = view;

                return true;
            } else {
                return false;
            }
        }
    }

    private final class MyDragEventListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();

            if (action == DragEvent.ACTION_DROP) {
                Log.i("Debug", "<Card info: " + cardMap.get(viewBeingDragged) + "/>");

                final int containerId = v.getId();
                PlayingCard card = cardMap.get(viewBeingDragged);

                try {
                    switch (containerId) {
                        case R.id.table_button:
                            Log.i("Debug", "<Table button dragged/>");
                            executeAction(
                                Game.class.getMethod("playerToVisible", PlayingCard.class, String.class),
                                card, uniqueId);
                            break;
                        case R.id.trash_button:
                            Log.i("Debug", "<Trash button dragged/>");
                            executeAction(
                                Game.class.getMethod("playerToTrash", PlayingCard.class, String.class),
                                card, uniqueId);
                            break;
                        case R.id.deck_button:
                            Log.i("Debug", "<Deck button dragged/>");
                            executeAction(
                                Game.class.getMethod("playerToDeck", PlayingCard.class, String.class),
                                card, uniqueId);
                            break;
                    }
                } catch (NoSuchMethodException e) {
                    // THIS SHOULD NEVER HAPPEN
                    e.printStackTrace();
                }

                if (containerId != R.id.table_button && containerId != R.id.trash_button
                        && containerId != R.id.deck_button) {
                    ((LinearLayout)findViewById(R.id.middle)).addView(viewBeingDragged);
                } else {
                    cardMap.remove(viewBeingDragged);
                }

                viewBeingDragged = null;
            }

            return true;
        }
    }

    // HELPER METHOD
    private void executeAction(final Method method, final Object...args) {
        // Wait to execute calls if they occur to quckly
        if (System.currentTimeMillis() - commandTimeStamp > 0.25 * Math.pow(10, 9)) {
            if (!isServer)
                Client.getInstance().executeAction(method, args);
            else
                MultiServer.executeAction(method, args);

            commandTimeStamp = System.currentTimeMillis();
        } else {
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (!isServer)
                        Client.getInstance().executeAction(method, args);
                    else
                        MultiServer.executeAction(method, args);
                }
            }.start();

            commandTimeStamp = System.currentTimeMillis() + 200;
        }
    }


    private void displayCard(PlayingCard card) {
        String imageName = HelperFunctions.getImageNameFromPlayingCard(card);

        Context context = getApplicationContext();
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(imageName, "drawable",
                context.getPackageName());
        Drawable drawable = context.getResources().getDrawable(resourceId);

        ImageView cardView = new ImageView(getApplicationContext());

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 115, getResources().getDisplayMetrics());
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());

        // TODO margins are not applying
        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.MarginLayoutParams(width, height);
        layoutParams.setMargins(margin, 0, margin, 0);
        cardView.setLayoutParams(layoutParams);

        cardView.setBackground(drawable);
        cardView.setLayoutParams(layoutParams);
        cardView.setTag(imageName);
        cardView.setOnTouchListener(new MyTouchListener());

        ((LinearLayout) findViewById(R.id.middle)).addView(cardView);

        cardMap.put(cardView, card);
    }
}


