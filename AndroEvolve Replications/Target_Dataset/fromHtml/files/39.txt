package ca.gbc.mobile.ianpadilla.research_monster;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Profile extends ActionBarActivity {

    TextView student_id, email, summary, first_name, last_name,experience;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Button viewProjects = (Button) findViewById(R.id.login_button);

        student_id = (TextView) findViewById(R.id.student_id_label);
        email = (TextView) findViewById(R.id.email_label);
        summary = (TextView) findViewById(R.id.summary_label);
        first_name = (TextView) findViewById(R.id.firstName_label);
        last_name = (TextView) findViewById(R.id.lastName_label);
        experience = (TextView) findViewById(R.id.experience_label);

        Intent intent = getIntent();

        String Mid = intent.getStringExtra("student_id");
        student_id.setText(Html.fromHtml(Mid));

        String Memail = intent.getStringExtra("email");
        email.setText(Html.fromHtml(Memail));

        String Msummary = intent.getStringExtra("summary");
        summary.setText(Html.fromHtml(Msummary.substring(3,Msummary.length()-6)));

        String MfirstName = intent.getStringExtra("first_name");
        first_name.setText(Html.fromHtml(MfirstName));

        String MlastName = intent.getStringExtra("last_name");
        last_name.setText(Html.fromHtml(MlastName));

        String Mexperience = intent.getStringExtra("experience");
        experience.setText(Html.fromHtml(Mexperience.substring(3,Mexperience.length()-6)));

        //This line errors out, not sure why.......
        /*        viewProjects.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this,AllProjects.class);
                Profile.this.startActivity(intent);
            }
        });*/
    }

    public void btnViewProjects(){

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
