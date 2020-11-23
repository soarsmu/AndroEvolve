package group28.thermostat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.sleepbot.datetimepicker.time.*;


public class addActivity extends Activity {

    private TimePicker timePicker;
    private TimePicker dateTimePicker;
    private Button confirm;
    private Button cancel;
    private RadioButton day, night;
    ArrayList<Integer> daySwitches;
    ArrayList<Integer> nightSwitches;
    String currentDay, dorn;
    private ActionBar actionBar;
    public int lastHour, lastMinute;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        daySwitches = getIntent().getIntegerArrayListExtra("daySwitches");
        nightSwitches = getIntent().getIntegerArrayListExtra("nightSwitches");
        currentDay = getIntent().getStringExtra("day");
        dorn = getIntent().getStringExtra("type");
        day = (RadioButton)findViewById(R.id.radioButton);
        night = (RadioButton)findViewById(R.id.radioButton2);

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        if (dorn.equals("day") || dorn.equals("night")) {
            getActionBar().setTitle("Add Switch for " + currentDay);
            day.setVisibility(View.INVISIBLE);
            night.setVisibility(View.INVISIBLE);
            lastHour = getIntent().getIntExtra("lastHour", 11);
            lastMinute = getIntent().getIntExtra("lastMinute", 00);

            if (lastHour != 23) {
                timePicker.setCurrentHour(lastHour + 1);
            } else {
                timePicker.setCurrentHour(23);
            }

        } else if (dorn.equals("editday")) {
            getActionBar().setTitle("Edit Switch");
            day.setVisibility(View.VISIBLE);
            night.setVisibility(View.VISIBLE);
            day.setChecked(true);
            night.setChecked(false);
            index = Integer.valueOf(getIntent().getStringExtra("pressedSwitch").substring(1, 2)) - 1;
            lastHour = daySwitches.get(index) / 100;
            lastMinute = daySwitches.get(index) % 100;
            timePicker.setCurrentHour(lastHour);
        } else if (dorn.equals("editnight")){
            getActionBar().setTitle("Edit Switch");
            day.setVisibility(View.VISIBLE);
            night.setVisibility(View.VISIBLE);
            day.setChecked(false);
            night.setChecked(true);
            index = Integer.valueOf(getIntent().getStringExtra("pressedSwitch").substring(1, 2)) - 1;
            lastHour = nightSwitches.get(index) / 100;
            lastMinute = nightSwitches.get(index) % 100;
            timePicker.setCurrentHour(lastHour);
        } else if (dorn.equals("edittime")) {
            getActionBar().setTitle("Change time to...");
            day.setVisibility(View.INVISIBLE);
            night.setVisibility(View.INVISIBLE);
            lastHour = getIntent().getIntExtra("lastHour", 12);
            lastMinute = getIntent().getIntExtra("lastMinute", 00);
            timePicker.setCurrentHour(lastHour);
        } else if (dorn.equals("add")) {
            getActionBar().setTitle("Add Switch for " + currentDay);
            day.setVisibility(View.VISIBLE);
            night.setVisibility(View.VISIBLE);
            day.setChecked(true);
            night.setChecked(false);
            lastHour = getIntent().getIntExtra("lastHour", 00);
            lastMinute = getIntent().getIntExtra("lastMinute", 00);
            timePicker.setCurrentHour(lastHour);
        }

        timePicker.setCurrentMinute(lastMinute);

        confirm = (Button) findViewById(R.id.button4);
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            int pickedHour = timePicker.getCurrentHour();
            int pickedMinute = timePicker.getCurrentMinute();
            int time = pickedHour * 100 + pickedMinute;

            if (dorn.equals("day") || dorn.equals("night")) {
                boolean match = false;

                if (dorn.equals("day") && daySwitches.size() == 5) {
                    Toast.makeText(getApplicationContext(), "Maximum number of day switches reached", Toast.LENGTH_LONG).show();
                    return;
                } else if (dorn.equals("night") && nightSwitches.size() == 5) {
                    Toast.makeText(getApplicationContext(), "Maximum number of night switches reached", Toast.LENGTH_LONG).show();
                    return;
                }

                for (int i : daySwitches) {
                    if (time == i) {
                        match = true;
                    }
                }

                for (int i : nightSwitches) {
                    if (time == i) {
                        match = true;
                    }
                }

                if (match) {
                    Toast.makeText(getApplicationContext(), "Duplicate switch found", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent returnIntent = new Intent(getApplicationContext(), DayActivity.class);
                returnIntent.putExtra("lastHour", pickedHour);
                returnIntent.putExtra("lastMinute", pickedMinute);
                returnIntent.putExtra("type", dorn);
                returnIntent.putExtra("time", Integer.toString(time));
                setResult(RESULT_OK, returnIntent);
                finish();
            } else if (dorn.equals("editday")){
                if (day.isChecked()) {
                    int currentTime = daySwitches.get(index);
                    if (time == currentTime) {
                        Intent returnIntent = new Intent(getApplicationContext(), DayActivity.class);
                        setResult(RESULT_CANCELED, returnIntent);
                        finish();
                        return;
                    } else {
                        boolean match = false;

                        for (int i : daySwitches) {
                            if (time == i) {
                                match = true;
                            }
                        }

                        if (match) {
                            Toast.makeText(getApplicationContext(), "Duplicate switch found", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Intent returnIntent = new Intent(getApplicationContext(), DayActivity.class);
                        returnIntent.putExtra("lastHour", pickedHour);
                        returnIntent.putExtra("lastMinute", pickedMinute);
                        returnIntent.putExtra("type", dorn);
                        returnIntent.putExtra("time", Integer.toString(time));
                        returnIntent.putExtra("index", index);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                } else {
                    boolean match = false;

                    if (nightSwitches.size() == 5) {
                        Toast.makeText(getApplicationContext(), "Maximum number of night switches reached", Toast.LENGTH_LONG).show();
                        return;
                    }

                    for (int i : nightSwitches) {
                        if (time == i) {
                            match = true;
                        }
                    }

                    if (match) {
                        Toast.makeText(getApplicationContext(), "Duplicate switch found", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent returnIntent = new Intent(getApplicationContext(), DayActivity.class);
                    returnIntent.putExtra("lastHour", pickedHour);
                    returnIntent.putExtra("lastMinute", pickedMinute);
                    returnIntent.putExtra("type", "editday2");
                    returnIntent.putExtra("time", Integer.toString(time));
                    returnIntent.putExtra("index", index);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            } else if (dorn.equals("editnight")) {
                if (night.isChecked()) {
                    int currentTime = nightSwitches.get(index);
                    if (time == currentTime) {
                        Intent returnIntent = new Intent(getApplicationContext(), DayActivity.class);
                        setResult(RESULT_CANCELED, returnIntent);
                        finish();
                        return;
                    } else {
                        boolean match = false;

                        for (int i : nightSwitches) {
                            if (time == i) {
                                match = true;
                            }
                        }

                        if (match) {
                            Toast.makeText(getApplicationContext(), "Duplicate switch found", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Intent returnIntent = new Intent(getApplicationContext(), DayActivity.class);
                        returnIntent.putExtra("lastHour", pickedHour);
                        returnIntent.putExtra("lastMinute", pickedMinute);
                        returnIntent.putExtra("type", dorn);
                        returnIntent.putExtra("time", Integer.toString(time));
                        returnIntent.putExtra("index", index);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                } else {
                    boolean match = false;

                    if (daySwitches.size() == 5) {
                        Toast.makeText(getApplicationContext(), "Maximum number of night switches reached", Toast.LENGTH_LONG).show();
                        return;
                    }

                    for (int i : daySwitches) {
                        if (time == i) {
                            match = true;
                        }
                    }

                    if (match) {
                        Toast.makeText(getApplicationContext(), "Duplicate switch found", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent returnIntent = new Intent(getApplicationContext(), DayActivity.class);
                    returnIntent.putExtra("lastHour", pickedHour);
                    returnIntent.putExtra("lastMinute", pickedMinute);
                    returnIntent.putExtra("type", "editnight2");
                    returnIntent.putExtra("time", Integer.toString(time));
                    returnIntent.putExtra("index", index);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }

            } else if (dorn.equals("edittime")) {
                Intent returnIntent = new Intent(getApplicationContext(), DayActivity.class);
                returnIntent.putExtra("newHour", pickedHour);
                returnIntent.putExtra("newMinute", pickedMinute);
                returnIntent.putExtra("pickedDay", currentDay);
                setResult(RESULT_OK, returnIntent);
                finish();
            } else if (dorn.equals("add")) {
                boolean match = false;

                if (day.isChecked() && daySwitches.size() == 5) {
                    Toast.makeText(getApplicationContext(), "Maximum number of day switches reached", Toast.LENGTH_LONG).show();
                    return;
                } else if (night.isChecked() && nightSwitches.size() == 5) {
                    Toast.makeText(getApplicationContext(), "Maximum number of night switches reached", Toast.LENGTH_LONG).show();
                    return;
                }

                for (int i : daySwitches) {
                    if (time == i) {
                        match = true;
                    }
                }

                for (int i : nightSwitches) {
                    if (time == i) {
                        match = true;
                    }
                }

                if (match) {
                    Toast.makeText(getApplicationContext(), "Duplicate switch found", Toast.LENGTH_LONG).show();
                    return;
                }

                if (day.isChecked()) {
                    dorn = "day";
                } else if (night.isChecked()) {
                    dorn = "night";
                }

                Intent returnIntent = new Intent(getApplicationContext(), MainActivity.class);
                returnIntent.putExtra("lastHour", pickedHour);
                returnIntent.putExtra("lastMinute", pickedMinute);
                returnIntent.putExtra("type", dorn);
                returnIntent.putExtra("time", Integer.toString(time));
                returnIntent.putExtra("day", currentDay);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
            }
        });

        cancel = (Button) findViewById(R.id.button5);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent returnIntent = new Intent(getApplicationContext(), DayActivity.class);

            setResult(RESULT_CANCELED, returnIntent);
            finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), HelpActivity.class);
            startActivity(i);
            return true;
        }

        if (id == R.id.action_about) {
            Intent i = new Intent(getApplicationContext(), aboutActivity.class);
            startActivity(i);
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }
}

