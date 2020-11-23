package com.example.stas.shutdown;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TimePicker timePicker = (TimePicker) findViewById(R.id.Zegar);
        final Button buttonWylacz = (Button) findViewById(R.id.buttonWylacz);


        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(0);
//        timePicker.setHour(0);
        timePicker.setCurrentMinute(0);
//        timePicker.setMinute(0);

        Intent intent = getIntent();

        buttonWylacz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int godzina = timePicker.getCurrentHour();
                final int minuta = timePicker.getCurrentMinute();
                Intent intent = getIntent();
                String login = intent.getStringExtra("login");

                Response.Listener<String> responseListener = new Response.Listener<String>()
                {


                    @Override
                    public void onResponse(String response) {
                        String message = "Błąd";

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success)
                            {
                                if (godzina > 0 && minuta > 0)
                                {

                                    message = "Komputer zostanie wyłączony za "+ Integer.toString(timePicker.getCurrentHour())+
                                                    "h i " + Integer.toString(timePicker.getCurrentMinute()) + "min";
                                }

                                else if (godzina > 0 && minuta == 0)
                                {
                                    message = "Komputer zostanie wyłączony za "+ Integer.toString(timePicker.getCurrentHour())+ "h";
                                }

                                else if (godzina == 0 && minuta > 0)
                                {
                                    message = "Komputer zostanie wyłączony za "+ Integer.toString(timePicker.getCurrentMinute()) + "min";
                                }

                                else if (godzina == 0 && minuta == 0) {
                                    message = "Komputer zostanie wyłączony natychmiast";
                                }
                                else
                                {
                                    message = "Błąd połączenia";
                                }

                                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                                toast.show();

                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Register Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                SetTimeRequest loginRequest = new SetTimeRequest(login, godzina*360+minuta*60+1, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(loginRequest);

//                Animation animacja = new AlphaAnimation(1.0f, 0.0f);
//                animacja.setDuration(1500);
//                buttonWylacz.startAnimation(animacja);


            }
        });




    }
}
