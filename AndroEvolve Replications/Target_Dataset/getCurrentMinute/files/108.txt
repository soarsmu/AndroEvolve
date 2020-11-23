package ca.jamesreeve.smarthome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoorSettingsActivity extends AppCompatActivity {

    TimePicker tp, tp2;
    Switch s;
    TimePicker.OnTimeChangedListener timePickerListener;
    Switch.OnCheckedChangeListener switchListener;
    FirebaseDatabase database;
    DatabaseReference doorSettingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_settings);

        s = (Switch) findViewById(R.id.active);
        tp = (TimePicker) findViewById(R.id.lockTime);
        tp2 = (TimePicker) findViewById(R.id.unlockTime);


        database = FirebaseDatabase.getInstance();
        doorSettingsRef = database.getReference("doorsettings");
        doorSettingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("db", "dbok in doorsettings.java");
                tp.setOnTimeChangedListener(null);
                tp2.setOnTimeChangedListener(null);
                s.setOnCheckedChangeListener(null);

                tp.setCurrentHour(((Number) dataSnapshot.child("lockH").getValue()).intValue());
                tp.setCurrentMinute(((Number) dataSnapshot.child("lockM").getValue()).intValue());
                tp2.setCurrentHour(((Number) dataSnapshot.child("unlockH").getValue()).intValue());
                tp2.setCurrentMinute(((Number) dataSnapshot.child("unlockM").getValue()).intValue());
                s.setChecked((boolean) dataSnapshot.child("active").getValue());

                tp.setOnTimeChangedListener(timePickerListener);
                tp2.setOnTimeChangedListener(timePickerListener);
                s.setOnCheckedChangeListener(switchListener);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("db",databaseError.toString());

            }
        });

        timePickerListener = new TimePicker.OnTimeChangedListener(){
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Log.d("deb","activity.updatesettings");

                doorSettingsRef.setValue(new DoorSettingsHelper(
                        s.isChecked(),
                        tp.getCurrentHour(),
                        tp.getCurrentMinute(),
                        tp2.getCurrentHour(),
                        tp2.getCurrentMinute()
                ));
            }

        };

        switchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doorSettingsRef.setValue(new DoorSettingsHelper(
                        s.isChecked(),
                        tp.getCurrentHour(),
                        tp.getCurrentMinute(),
                        tp2.getCurrentHour(),
                        tp2.getCurrentMinute()
                ));
            }
        };

        tp.setOnTimeChangedListener(timePickerListener);
        tp2.setOnTimeChangedListener(timePickerListener);
        s.setOnCheckedChangeListener(switchListener);




    }

}
