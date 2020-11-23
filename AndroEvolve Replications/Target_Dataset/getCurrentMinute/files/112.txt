package aarne.kyppo.shoplistnotifier.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class NewShoppingListActivity extends ActionBarActivity {

    private TimePicker start;
    private TimePicker end;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_shopping_list);

        start = (TimePicker) findViewById(R.id.start_picker);
        end = (TimePicker) findViewById(R.id.end_picker);
        title = (TextView) findViewById(R.id.newtitle);

        start.setIs24HourView(true);
        end.setIs24HourView(true);
    }

    public void addShoppingList(View v)
    {

        Calendar start_time = new GregorianCalendar();

        Log.d("addShoppingListMinute", start.getCurrentMinute() + "");
        Log.d("addShoppingListHour", start.getCurrentHour() + "");

        start_time.add(Calendar.MINUTE, start.getCurrentMinute());
        start_time.add(Calendar.HOUR_OF_DAY,start.getCurrentHour());

        Log.d("addShoppingListTimeAsString", start_time.toString());

        Calendar end_time = new GregorianCalendar();
        end_time.add(Calendar.MINUTE, end.getCurrentMinute());
        end_time.add(Calendar.HOUR_OF_DAY, end.getCurrentHour());

        ShoppingList sl = new ShoppingList();
        sl.setStart(start.getCurrentHour(),start.getCurrentMinute());
        sl.setEnd(end.getCurrentHour(),end.getCurrentMinute());
        sl.setTitle(title.getText().toString());


        DBHelper helper = new DBHelper(this);
        helper.addShoppingList(sl);

        int id = helper.getEarliestShoppingListID();
        Log.d("EARLIEST ID", id + "");

        helper = null;

        Intent i = new Intent(this,ShoppingListActivity.class);
        i.putExtra("earliest",id);
        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_shopping_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
