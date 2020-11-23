package de.fz_juelich.inm.kicker.kicker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import android.graphics.Color;

public class PlayersRanking extends ActionBarActivity {

    TableLayout table;
    Player[] players;
    Menu mainmenu;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("PlayersRanking", "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_ranking);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.fun_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        queue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onStart(){
        Log.i("Ranking on Start","here");
        super.onStart();

        table = (TableLayout) findViewById(R.id.ranking_table);
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void refresh(){
        Log.i("PlayersRanking","refresh");
        table.removeAllViews();
        players = null;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://dper.de:9898/getplayers/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("refresh_request", "response: " + response);
                try {
                    JSONArray aPlayers = new JSONArray(response);
                    players = new Player[aPlayers.length()];
                    for (int i = 0; i < aPlayers.length(); i++){
                        JSONObject oPlayer = aPlayers.getJSONObject(i);
                        players[i] = new Player(oPlayer.getInt("id"), oPlayer.getString("name"), oPlayer.getInt("score"), oPlayer.getInt("elo"));
                    }
                    Comparator<Player> comp = new EloComparator();
                    Arrays.sort(players, comp);

                    createRanking();
                }
                catch(JSONException e){
                    Log.i("refresh_json", "JSONException when getting players.");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("refresh_request", "volley error: " + error.getMessage());
                CharSequence errortext;
                if (error.getCause() instanceof UnknownHostException) {
                    errortext = "Connection error.";
                }
                else{
                    errortext = "Unknown error.";
                }
                Toast errortoast = Toast.makeText(getApplicationContext(), errortext, Toast.LENGTH_SHORT);
                errortoast.show();
            }
        });


        queue.getCache().clear();
        queue.add(stringRequest);
    }

    void createRanking(){
        Log.i("PlayersRanking","createRanking");

        TableRow row = new TableRow(this);
        row.setMinimumWidth(table.getWidth());
        row.setMinimumHeight(table.getHeight()/(players.length+3));
        table.addView(row);

        row = new TableRow(this);
        row.setMinimumWidth(table.getWidth());
        row.setMinimumHeight(table.getHeight()/(players.length+3));
        TextView tv = new TextView(this);
        tv.setTextAppearance(getApplicationContext(), R.style.RankingTitle);
        tv.setText(" ");
        tv.setGravity(Gravity.RIGHT);
        tv.setGravity(Gravity.BOTTOM);
        tv.setWidth(table.getWidth()*3/8);
        row.addView(tv);

        tv = new TextView(this);
        tv.setText("Player");
        tv.setTextAppearance(getApplicationContext(), R.style.RankingTitle);
        tv.setGravity(Gravity.BOTTOM);
        tv.setWidth(table.getWidth()*3/8);
        row.addView(tv);

        tv = new TextView(this);
        tv.setText(" ELO");
        tv.setTextAppearance(getApplicationContext(), R.style.RankingTitle);
        tv.setGravity(Gravity.BOTTOM);
        tv.setWidth(table.getWidth()*2/8);

        row.addView(tv);
        table.addView(row);


        for (int i = 0; i < players.length; i++){
            row = new TableRow(this);
            row.setMinimumWidth(table.getWidth());
            row.setMinimumHeight(table.getHeight()/(players.length+3));

            tv = new TextView(this);
            tv.setTextAppearance(getApplicationContext(), R.style.RankingEntry);
            tv.setText(i+1+".");
            tv.setGravity(Gravity.CENTER);
            tv.setWidth(table.getWidth() * 3 / 8);
            row.addView(tv);

            tv = new TextView(this);
            tv.setTextAppearance(getApplicationContext(), R.style.RankingEntry);
            tv.setText(players[i].name);
            tv.setWidth(table.getWidth()*3/8);
            row.addView(tv);

            tv = new TextView(this);
            tv.setTextAppearance(getApplicationContext(), R.style.RankingEntry);
            tv.setText(" "+players[i].elo);
            tv.setWidth(table.getWidth()*2/8);
            row.addView(tv);
            if (i % 2 ==0) {
                row.setBackgroundColor(Color.LTGRAY);
            }


            table.addView(row);
                }
            }


  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enter_score, menu);
        mainmenu = menu;
        // disable button which shows current game
        mainmenu.getItem(0).setEnabled(false);
        mainmenu.getItem(0).setVisible(false);
        return true;
    }


/**
 * A placeholder fragment containing a simple view.
 */
public static class PlaceholderFragment extends Fragment {

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_players_ranking, container, false);
    }


    public void onClick(View v) {
        Button b = (Button) v;
        Log.i("player button", b.getText().toString());
    }

    }

}

