package ab.myApp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timePicker = findViewById(R.id.timePick);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void ShowTime(View v) {
        int hours = timePicker.getCurrentHour();
        int minutes = timePicker.getCurrentMinute();

        if (hours > 12) {
            hours = hours - 12;
            String h = Integer.toString(hours);
            String m = Integer.toString(minutes);
            Toast.makeText(this, h + ":" + m + " PM", Toast.LENGTH_SHORT).show();

        }
        else {
            String h = Integer.toString(hours);
            String m = Integer.toString(minutes);
            Toast.makeText(this, h + ":" + m + " AM", Toast.LENGTH_SHORT).show();
        }
    }
}