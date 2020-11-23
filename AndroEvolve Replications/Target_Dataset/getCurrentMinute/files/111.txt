package nozagleh.org.gluttony;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by arnarfreyr on 11.3.15.
 */
public class menu1_Fragment extends Fragment {
    //Initialize View
    View rootview;

    //Initialize elements
    EditText txtComment;
    AutoCompleteTextView txtName;
    DatePicker datePicker;
    TimePicker timePicker;
    Button btnAdd;
    Switch switchType;
    Boolean editflag;

    DBConnector db;

    Food food;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the root view
        rootview = inflater.inflate(R.layout.menu1_layout,container,false);

        //Initialize DB connection
        db = new DBConnector(getActivity());

        editflag = getActivity().getIntent().getBooleanExtra("editflag",false);

        //Set the elements
        //text
        txtName = (AutoCompleteTextView) rootview.findViewById(R.id.txtName);
        txtComment = (EditText) rootview.findViewById(R.id.txtComment);
        txtName.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left));
        txtComment.startAnimation(AnimationUtils.loadAnimation(getActivity(),android.R.anim.slide_in_left));

        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);


        //date
        datePicker = (DatePicker) rootview.findViewById(R.id.datePicker);
        datePicker.setMaxDate(new Date().getTime());
        datePicker.startAnimation(AnimationUtils.loadAnimation(getActivity(),android.R.anim.slide_in_left));
        datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        //time (set to 24 hours)
        timePicker = (TimePicker) rootview.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.startAnimation(AnimationUtils.loadAnimation(getActivity(),android.R.anim.slide_in_left));
        timePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        //button
        btnAdd = (Button) rootview.findViewById(R.id.btnAdd);
        //switch
        switchType = (Switch) rootview.findViewById(R.id.switchType);
        switchType.setChecked(true);
        switchType.startAnimation(AnimationUtils.loadAnimation(getActivity(),android.R.anim.slide_in_left));

        //Set the font for the elements
        String fontpath = "fonts/Roboto/Roboto-Italic.ttf";
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),fontpath);
        //Adds the font to all of the elements
        txtName.setTypeface(font);
        txtComment.setTypeface(font);
        btnAdd.setTypeface(font);
        switchType.setTypeface(font);

        if(editflag){

            final int data = getActivity().getIntent().getIntExtra("id", 0);

            food = db.getFood(data);

            txtName.setText(food.get_name());
            txtComment.setText(food.get_comment());

            String[] originalDate = food.get_time().split(" ");
            String[] dates = originalDate[0].split("-");
            String[] time = originalDate[1].split(":");
            Integer correct_month = Integer.parseInt(dates[1]) - 1;
            datePicker.init(Integer.parseInt(dates[0]),correct_month,Integer.parseInt(dates[2]),null);

            timePicker.setCurrentHour(Integer.parseInt(time[0]));
            timePicker.setCurrentMinute(Integer.parseInt(time[1]));
            if(food.get_type() == 0){
                switchType.setChecked(true);
            }else{
                switchType.setChecked(false);
            }

            btnAdd.setText("Update");
        }



        rootview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtName.clearFocus();
                txtComment.clearFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtName.getWindowToken(),0);
            }
        });

        //connect to the database with the activity
        final DBConnector db = new DBConnector(getActivity());

        //set add button on click listener
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //check if coming from editing option
                    if (editflag) {
                        try {

                            food.set_name(txtName.getText().toString());
                            food.set_comment(txtComment.getText().toString());

                            int cm = datePicker.getMonth() + 1;
                            String corrmonth = String.valueOf(cm);

                            if (timePicker.getCurrentMinute() < 10) {
                                String newMin = "0" + timePicker.getCurrentMinute();
                                String dateAndTime = datePicker.getYear() + "-" + corrmonth + "-" + datePicker.getDayOfMonth() + " " + timePicker.getCurrentHour() + ":" + newMin;
                                food.set_time(dateAndTime);
                            } else {
                                String dateAndTime = datePicker.getYear() + "-" + corrmonth + "-" + datePicker.getDayOfMonth() + " " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
                                food.set_time(dateAndTime);
                            }

                            if (switchType.isChecked()) {
                                food.set_type(0);
                            } else {
                                food.set_type(1);
                            }

                            db.updateFood(food);

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);

                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        if (txtName.length() != 0) {
                            //Set date and time in one string
                            int cm = datePicker.getMonth() + 1;
                            String corrmonth = String.valueOf(cm);
                            String dateAndTime;
                            String newMin = "0" + timePicker.getCurrentMinute();
                            String newHour = "0" + timePicker.getCurrentHour();

                            if (timePicker.getCurrentMinute() < 10) {
                                if (timePicker.getCurrentHour() < 10) {
                                    dateAndTime = datePicker.getYear() + "-" + corrmonth + "-" + datePicker.getDayOfMonth() + " " + newHour + ":" + newMin;
                                } else {
                                    dateAndTime = datePicker.getYear() + "-" + corrmonth + "-" + datePicker.getDayOfMonth() + " " + timePicker.getCurrentHour() + ":" + newMin;
                                }
                            } else {
                                if (timePicker.getCurrentHour() < 10) {
                                    dateAndTime = datePicker.getYear() + "-" + corrmonth + "-" + datePicker.getDayOfMonth() + " " + newHour + ":" + timePicker.getCurrentMinute();
                                } else {
                                    dateAndTime = datePicker.getYear() + "-" + corrmonth + "-" + datePicker.getDayOfMonth() + " " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
                                }

                            }

                            Integer type = 0;

                            if (!switchType.isChecked()) {
                                type = 1;
                            }

                            //add the food to the database
                            if (txtComment.length() != 0)
                                db.addFood(new Food(txtName.getText().toString(), dateAndTime, txtComment.getText().toString(), type));
                            else
                                db.addFood(new Food(txtName.getText().toString(), dateAndTime, "No description", type));
                            //simple message to the user that the food has been added
                            //Toast.makeText(getActivity(), txtName.getText().toString() + " has been added", Toast.LENGTH_SHORT).show();


                            new CountDownTimer(1000,500){
                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                final Dialog dialog = alert.show();
                                @Override
                                public void onTick(long l) {

                                    dialog.setContentView(R.layout.insert_message);
                                    dialog.setTitle(getString(R.string.txt_success));
                                    dialog.setCancelable(false);
                                    // there are a lot of settings, for dialog, check them all out!
                                    final ImageView imgDone = (ImageView) dialog.findViewById(R.id.imgDone);
                                    imgDone.setImageResource(R.drawable.add_big);


                                    // now that the dialog is set up, it's time to show it
                                    dialog.show();

                                }

                                @Override
                                public void onFinish() {
                                    dialog.dismiss();
                                }
                            }.start();



                        } else {
                            //give an error if name is not supplied
                            txtName.setError(getString(R.string.error_no_name));
                        }
                    }
                    txtComment.setText("");
                    txtName.setText("");

                } catch (Exception e) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setIcon(R.drawable.error);
                    alert.setTitle(getString(R.string.alert_error_delete));
                    alert.setMessage(getString(R.string.alert_error_inserting));
                    alert.setNegativeButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();
                }
            }
        });

        TextWatcher tv = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //Call for suggestions when name text is changed
                ArrayList<String> SUGGESTIONS;
                SUGGESTIONS = getSuggestions(db,txtName.getText().toString());

                //Check if empty
                if (!SUGGESTIONS.isEmpty()){
                    //Create the adapter and add to the dropdown list
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,SUGGESTIONS);
                    txtName.setAdapter(adapter);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        //Add the listener to name text
        txtName.addTextChangedListener(tv);

        return rootview;
    }



    /**
     *Gets suggestions from the database for the name column of the application
     * @param db
     * @param text
     * @return String
     */
    public ArrayList<String> getSuggestions(DBConnector db,String text){
        ArrayList<String> strings = new ArrayList<String>();
        List<Food> foods = new ArrayList<Food>();
        Food food = new Food();

        strings = db.getFoodName(text);

        /*for (int i=0; i < foods.size(); i++){
            food = foods.get(i);
            strings.add(food.get_name());
        }*/
        return strings;
    }

}
