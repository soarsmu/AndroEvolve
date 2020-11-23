package com.travel.flybooking;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.travel.common_handlers.HttpHandler;
import com.travel.common_handlers.UrlParameterBuilder;
import com.travel.flybooking.support.CommonFunctions;
import com.travel.flybooking.support.CustomDatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MyProfileActivity extends Activity {
    final Calendar c = Calendar.getInstance();
    public String[] arrTitle = {"Title", "Mr", "Miss", "Mrs"};
    Spinner SpnTitle, SpnGender, spCountrycode, SpnNationality;
    EditText EdtFname, EdtLname, EdtemailId, edt_dob, edt_mob, pass, cpass;
    TextView txt_Title, txt_fname, txt_lname, txt_DOB, txt_gender,
            txt_mobileno, txt_nationality;
    String[] Countrycode;
    AssetManager am;
    Button Update, Updatedetails;
    ArrayList<String> arrayCountry, arrayCountryisocode;
    String Titles, FirstName, MiddleName, LastName, DateOfBirth, Gender,
            MobileCode, MobileNumber, PassportNumber, Citizenship, PassengerId;
    Dialog loaderDialog;
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);
    int maxYear = mYear - 12;
    int maxMonth = mMonth;
    int maxDay = mDay + 1;
    int minYear = mYear - 100;
    int minMonth = mMonth;
    int minDay = mDay;
    int cday, cmonth, cyear;
    String EmailAddress, ccode, Ncode, gCode;
    SharedPreferences pref;
    ArrayAdapter<CharSequence> adapter1;
    HashMap<String, String> map;
    UrlParameterBuilder urlObj;
    private Locale myLocale;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        loadLocale();
        setContentView(R.layout.activity_myprofile);
        urlObj = new UrlParameterBuilder();
        pref = getApplicationContext().getSharedPreferences("MyLoginPref",
                MODE_PRIVATE);
        EmailAddress = pref.getString("Email", null);

        loadAssets();
        initilize();
        new AccountDetails().execute();
    }

    @SuppressWarnings("unchecked")
    public void initilize() {
        loaderDialog = new Dialog(this, android.R.style.Theme_Translucent);
        loaderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loaderDialog.getWindow().setGravity(Gravity.TOP);
        loaderDialog.setContentView(R.layout.dialog_loader);
        loaderDialog.setCancelable(false);
        SpnGender = (Spinner) findViewById(R.id.Spn_gender);
        spCountrycode = (Spinner) findViewById(R.id.spn_countrycode);
        SpnTitle = (Spinner) findViewById(R.id.Spn_title);
        SpnNationality = (Spinner) findViewById(R.id.spn_nationality);

        EdtemailId = (EditText) findViewById(R.id.edt_emailid);
        EdtFname = (EditText) findViewById(R.id.edt_fname);
        EdtLname = (EditText) findViewById(R.id.edt_lname);
        edt_dob = (EditText) findViewById(R.id.edt_dob);
        edt_mob = (EditText) findViewById(R.id.edt_Mobileno);
        pass = (EditText) findViewById(R.id.edt_new_pass);
        cpass = (EditText) findViewById(R.id.edt_c_pass);

        txt_Title = (TextView) findViewById(R.id.txt_title);
        txt_fname = (TextView) findViewById(R.id.txt_fname);
        txt_lname = (TextView) findViewById(R.id.txt_lname);
        txt_DOB = (TextView) findViewById(R.id.txt_dob);
        txt_gender = (TextView) findViewById(R.id.txt_gender);
        txt_mobileno = (TextView) findViewById(R.id.txt_phno);
        txt_nationality = (TextView) findViewById(R.id.txt_nationality);

        EdtemailId.setFocusable(false);
        EdtemailId.setText(EmailAddress);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.title_spinner_items, R.layout.tv_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        SpnGender.setAdapter(adapter);
        SpnGender
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {

                        if (position == 1) {
                            gCode = "M";
                        } else if (position == 2) {
                            gCode = "F";
                        }
                        // Log.e("SpnGender", gCode);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
        adapter1 = ArrayAdapter.createFromResource(this,
                R.array.my_profile_Title, R.layout.tv_spinner);
        adapter1.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        SpnTitle.setAdapter(adapter1);
        SpnTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                Titles = arrTitle[position];
                Log.e("SpnTitle", Titles);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Countrycode = new String[]{"+1", "+1 284", "+1 340", "+1 345",
                "+1 649", "+1 670", "+1 758", "+1 784", "+1 869", "+1242",
                "+1246", "+1264", "+1268", "+1441", "+1473", "+1664", "+1671",
                "+1684", "+1767", "+1809", "+1876", "+20", "+212", "+213",
                "+216", "+218", "+220", "+221", "+222", "+223", "+224", "+225",
                "+226", "+227", "+228", "+229", "+230", "+231", "+232", "+233",
                "+234", "+235", "+236", "+237", "+238", "+239", "+240", "+241",
                "+242", "+243", "+244", "+245", "+246", "+248", "+249", "+250",
                "+251", "+252", "+253", "+254", "+255", "+256", "+257", "+258",
                "+260", "+261", "+262", "+263", "+264", "+265", "+266", "+267",
                "+268", "+269", "+27", "+290", "+291", "+297", "+298", "+299",
                "+30", "+31", "+32", "+33", "+34", "+350", "+351", "+352",
                "+353", "+354", "+355", "+356", "+357", "+358", "+359", "+36",
                "+370", "+371", "+372", "+373", "+374", "+375", "+376", "+378",
                "+380", "+381", "+382", "+385", "+386", "+387", "+389", "+39",
                "+40", "+41", "+420", "+421", "+423", "+43", "+44", "+45",
                "+46", "+47", "+48", "+49", "+500", "+501", "+502", "+503",
                "+504", "+505", "+506", "+507", "+508", "+509", "+51", "+52",
                "+53", "+54", "+55", "+56", "+57", "+58", "+590", "+591",
                "+592", "+593", "+594", "+595", "+596", "+597", "+598", "+599",
                "+60", "+61", "+62", "+63", "+64", "+65", "+66", "+670",
                "+672", "+673", "+674", "+675", "+676", "+677", "+678", "+679",
                "+680", "+681", "+682", "+683", "+685", "+686", "+687", "+688",
                "+689", "+690", "+691", "+692", "+699", "+7", "+81", "+82",
                "+84", "+850", "+852", "+853", "+855", "+856", "+86", "+880",
                "+886", "+90", "+91", "+92", "+93", "+94", "+95", "+960",
                "+961", "+962", "+963", "+964", "+965", "+966", "+967", "+968",
                "+970", "+971", "+972", "+973", "+974", "+975", "+976", "+977",
                "+98", "+992", "+993", "+994", "+995", "+996", "+998"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                R.layout.tv_spinner, Countrycode);
        adapter2.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        spCountrycode.setAdapter(adapter2);
        spCountrycode
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {

                        ccode = spCountrycode.getSelectedItem().toString();
                        Log.e("Countrycode", ccode);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
                R.layout.tv_spinner, arrayCountry);
        adapter3.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        SpnNationality.setAdapter(adapter3);

        SpnNationality
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {

                        Ncode = SpnNationality.getSelectedItem().toString();
                        Ncode = Ncode.substring(Math.max(Ncode.length() - 2, 0));
                        Log.e("SpnNationality", Ncode);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
        SpnNationality.setSelection(((ArrayAdapter<String>) SpnNationality
                .getAdapter()).getPosition("UNITED STATES - US"));

        String textTitle = "<font color=#000000>Title</font><font color=#e32c18> *</font>";
        txt_Title.setText(Html.fromHtml(textTitle));

        String textfname = "<font color=#000000>First Name</font><font color=#e32c18> *</font>";
        txt_fname.setText(Html.fromHtml(textfname));

        String textlname = "<font color=#000000>Last Name</font><font color=#e32c18> *</font>";
        txt_lname.setText(Html.fromHtml(textlname));

        String textDob = "<font color=#000000>Date of Birth</font><font color=#e32c18> *</font>";
        txt_DOB.setText(Html.fromHtml(textDob));

        String textgender = "<font color=#000000>Gender</font><font color=#e32c18> *</font>";
        txt_gender.setText(Html.fromHtml(textgender));

        String textmobilno = "<font color=#000000>Phone Number</font><font color=#e32c18> *</font>";
        txt_mobileno.setText(Html.fromHtml(textmobilno));

        String textnationality = "<font color=#000000>Nationality</font><font color=#e32c18> *</font>";
        txt_nationality.setText(Html.fromHtml(textnationality));

        if (!CommonFunctions.lang.equals("en")) {
            String textTitlear = "<font color=#000000>اللقب</font><font color=#e32c18> *</font>";
            txt_Title.setText(Html.fromHtml(textTitlear));

            String textfnamear = "<font color=#000000>الاسم الأول</font><font color=#e32c18> *</font>";
            txt_fname.setText(Html.fromHtml(textfnamear));

            String textlnamear = "<font color=#000000>اسم العائلة</font><font color=#e32c18> *</font>";
            txt_lname.setText(Html.fromHtml(textlnamear));

            String textDobar = "<font color=#000000>تاريخ الميلاد</font><font color=#e32c18> *</font>";
            txt_DOB.setText(Html.fromHtml(textDobar));

            String textgenderar = "<font color=#000000>الجنس</font><font color=#e32c18> *</font>";
            txt_gender.setText(Html.fromHtml(textgenderar));

            String textmobilnoar = "<font color=#000000>رقم الهاتف النقال</font><font color=#e32c18> *</font>";
            txt_mobileno.setText(Html.fromHtml(textmobilnoar));

            String textnationalityar = "<font color=#000000>الجنسية</font><font color=#e32c18> *</font>";
            txt_nationality.setText(Html.fromHtml(textnationalityar));
        }
    }

    public void clicker(View v) {
        switch (v.getId()) {
            case R.id.btn_update_t_info:
                if (validateDetails()) {
                    new SaveAccountDetails().execute();
                }
                break;
            case R.id.btn_update_a_info:
                if (validate()) {
                    new UpdatePasswordService().execute();
                }
                break;
            case R.id.edt_dob:
                DateofBirth();
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void loadAssets() {
        // TODO Auto-generated method stub
        am = getAssets();
        String countrylist = null;
        InputStream file1 = null;
        try {
            file1 = am.open("countrylist.txt");
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        BufferedReader reader1 = null;
        try {
            reader1 = new BufferedReader(new InputStreamReader(file1));
            StringBuilder builder1 = new StringBuilder();
            String line1 = null;
            while ((line1 = reader1.readLine()) != null) {
                builder1.append(line1);
            }
            countrylist = builder1.toString();
            arrayCountryisocode = new ArrayList<String>();
            arrayCountry = new ArrayList<String>();
            if (countrylist != null) {
                JSONObject json1 = new JSONObject(countrylist);
                JSONArray airlinelist = json1.getJSONArray("countrylist");
                JSONObject c1 = null;
                for (int i = 0; i < airlinelist.length(); i++) {
                    c1 = airlinelist.getJSONObject(i);
                    arrayCountry.add(c1.getString("CountryName") + " - "
                            + c1.getString("CountryCode"));
                    arrayCountryisocode.add(c1.getString("CountryCode"));
                    // Log.e("CountryName ", arrayCountryisocode.toString());
                }
                airlinelist = null;
            }
            countrylist = null;
            file1.close();

            reader1.close();
            builder1 = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader1 != null) {
                try {
                    reader1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void DateofBirth() {
        // TODO Auto-generated method stub
        datePickerDialog = new CustomDatePickerDialog(MyProfileActivity.this,
                new OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        try {
                            String str_day = String.valueOf(dayOfMonth);
                            if (str_day.length() == 1) {
                                str_day = "0" + String.valueOf(dayOfMonth);
                            }
                            String str_month = String.valueOf(monthOfYear + 1);
                            if (str_month.length() == 1) {
                                str_month = "0"
                                        + String.valueOf(monthOfYear + 1);
                            }
                            String str_year = String.valueOf(year);
                            System.out.println(str_day + "/" + str_month + "/"
                                    + str_year);
                            edt_dob.setText(str_day + "/" + str_month + "/"
                                    + str_year);
                        } catch (Exception e) {

                        }

                    }

                }, minYear, minMonth, minDay, maxYear, maxMonth, maxDay);
        datePickerDialog.updateDate(cyear, cmonth - 1, cday);
        datePickerDialog.show();

    }

    public boolean validate() {
        boolean valid = true;

        String spass = pass.getText().toString();
        String scpass = cpass.getText().toString();

        if (spass.isEmpty()) {
            pass.setError(getResources().getString(R.string.error_invalid_pass));
            valid = false;
        } else {
            pass.setError(null);
        }

        if (scpass.isEmpty() && !scpass.equals(cpass)) {
            cpass.setError(getResources()
                    .getString(R.string.error_invalid_pass));
            valid = false;
        } else {
            cpass.setError(null);
        }
        if (valid && !spass.matches(scpass)) {
            cpass.setError(getString(R.string.error_pass_mismatch));
            valid = false;
        } else if (valid) {
            pass.setError(null);
            cpass.setError(null);
        }
        return valid;
    }

    public boolean validateDetails() {
        boolean valid = true;

        String SpnTitile = SpnTitle.getSelectedItem().toString();

        if (SpnTitile.equals("Title")) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.err_title_req),
                    Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            // pass.setError(null);
        }

        if (EdtFname.getText().toString().isEmpty()) {
            EdtFname.setError(getResources().getString(
                    R.string.error_first_name_req));
            valid = false;
        } else {
            EdtFname.setError(null);
        }
        if (EdtLname.getText().toString().isEmpty()) {
            EdtLname.setError(getResources().getString(
                    R.string.error_last_name_req));
            valid = false;
        } else {
            EdtLname.setError(null);
        }

        if (edt_dob.getText().toString().isEmpty()) {
            edt_dob.setError(getResources().getString(R.string.error_dob_req));
            valid = false;
        }

        if (edt_mob.getText().toString().isEmpty()) {
            edt_mob.setError(getResources()
                    .getString(R.string.error_phn_no_req));
            valid = false;
        } else {
            edt_mob.setError(null);
        }

        if (valid) {
            map = new HashMap<String, String>();
            map.put("Tittle", Titles);
            map.put("FirstName", EdtFname.getText().toString());
            map.put("MiddleName", "");
            map.put("LastName", EdtLname.getText().toString());
            map.put("DateOfBirth", edt_dob.getText().toString());
            map.put("Gender", gCode);
            map.put("MobileCode", ccode);
            map.put("MobileNumber", edt_mob.getText().toString());
            map.put("PassportNumber", "");
            map.put("Citizenship", Ncode);
            map.put("PassengerId", PassengerId);
            map.put("PassportExpiryDate", "18/07/2016");
            map.put("PassportPlaceOfIssue", "");

        }

        return valid;
    }

    private void loadLocale() {
        // TODO Auto-generated method stub
        SharedPreferences sharedpreferences = this.getSharedPreferences(
                "CommonPrefs", Context.MODE_PRIVATE);
        String lang = sharedpreferences.getString("Language", "en");
        System.out.println("Default lang: " + lang);
        if (lang.equalsIgnoreCase("ar")) {
            myLocale = new Locale(lang);
            saveLocale(lang);
            Locale.setDefault(myLocale);
            android.content.res.Configuration config = new android.content.res.Configuration();
            config.locale = myLocale;
            this.getBaseContext()
                    .getResources()
                    .updateConfiguration(
                            config,
                            this.getBaseContext().getResources()
                                    .getDisplayMetrics());
            CommonFunctions.lang = "ar";
        } else {
            myLocale = new Locale(lang);
            saveLocale(lang);
            Locale.setDefault(myLocale);
            android.content.res.Configuration config = new android.content.res.Configuration();
            config.locale = myLocale;
            this.getBaseContext()
                    .getResources()
                    .updateConfiguration(
                            config,
                            this.getBaseContext().getResources()
                                    .getDisplayMetrics());
            CommonFunctions.lang = "en";
        }
    }

    public void saveLocale(String lang) {
        CommonFunctions.lang = lang;
        String langPref = "Language";
        SharedPreferences prefs = this.getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }

    public class AccountDetails extends AsyncTask<Void, Void, String> {

        Boolean blIsloggedIn = false;
        String sessionResult = "";
        JSONObject json1 = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            loaderDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                sessionResult = new HttpHandler()
                        .makeServiceCall(CommonFunctions.main_url
                                + CommonFunctions.lang
                                + urlObj.getGetProfileDetailsUrl());
                System.out.println("result" + sessionResult);
                json1 = new JSONObject(sessionResult);
                if (json1.getBoolean("IsValid")) {
                    blIsloggedIn = json1.getBoolean("IsValid");
                    Log.e("IsValid", "True");
                    return sessionResult;

                }

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            try {
                if (loaderDialog.isShowing())
                    loaderDialog.dismiss();
                if (result != null && blIsloggedIn) {

                    if (json1.getString("Title") != null
                            && !json1.getString("Title").equals("")
                            && !json1.getString("Title").equals("null")) {
                        int pos = 0;
                        for (pos = 0; pos < arrTitle.length; ++pos) {
                            if (arrTitle[pos].equalsIgnoreCase(json1
                                    .getString("Title"))) {
                                break;
                            }
                        }
                        SpnTitle.setSelection(pos);
                    }

                    FirstName = json1.getString("FirstName");
                    if (FirstName != null && !FirstName.equals("")
                            && !FirstName.equals("null"))
                        EdtFname.setText(FirstName);

                    MiddleName = json1.getString("MiddleName");

                    LastName = json1.getString("LastName");

                    if (LastName != null && !LastName.equals("")
                            && !LastName.equals("null"))
                        EdtLname.setText(LastName);

                    DateOfBirth = json1.getString("DateOfBirth");
                    if (DateOfBirth != null && !DateOfBirth.equals("")
                            && !DateOfBirth.equals("null")) {
                        edt_dob.setText(DateOfBirth);
                        String[] out = DateOfBirth.split("/");
                        cday = Integer.parseInt(out[0]);
                        cmonth = Integer.parseInt(out[1]);
                        cyear = Integer.parseInt(out[2]);
                    } else {
                        cday = 01;
                        cmonth = 01;
                        cyear = 1980;
                        edt_dob.setText("");
                    }

                    PassengerId = json1.getString("PassengerId");
                    if (json1.getString("Gender") != null
                            && !json1.getString("Gender").equals("")
                            && !json1.getString("Gender").equals("null")) {
                        Gender = json1.getString("Gender");
                        SpnGender.setSelection(Gender.contains("M") ? 1 : 2);
                    }

                    MobileCode = json1.getString("MobileCode");

                    if (MobileCode != null && !MobileCode.equals("")
                            && !MobileCode.equals("null")) {
                        String s = MobileCode;
                        s = s.replace(" ", "");
                        System.out.println(s);
                        spCountrycode
                                .setSelection(((ArrayAdapter<String>) spCountrycode
                                        .getAdapter()).getPosition(s));
                    }

                    MobileNumber = json1.getString("MobileNumber");

                    if (MobileNumber != null && !MobileNumber.equals("")
                            && !MobileNumber.equals("null")) {
                        edt_mob.setText(MobileNumber);
                    }

                    PassportNumber = json1.getString("PassportNumber");

                    String S = json1.getString("Citizenship");
                    if (S != null && !S.equals("") && !S.equals("null")) {
                        SpnNationality.setSelection(arrayCountryisocode
                                .indexOf(S));
                    }

                    PassengerId = json1.getString("PassengerId");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

    }

    private class SaveAccountDetails extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            loaderDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {

                String UrlParams = urlObj.getProfileParams(map);
                System.out.println("UrlParams " + UrlParams);
                String urlParameters = "accountObject=" + UrlParams.toString();

                String request = CommonFunctions.main_url
                        + CommonFunctions.lang
                        + "/MyAccountApi/SaveAccountProfile";

                String res = new HttpHandler().makeServiceCallWithParams(
                        request, urlParameters);

                System.out.println("res" + res);
                JSONObject json = new JSONObject(res);
                if ((json.getBoolean("IsValid") && json.getBoolean("IsSuccess"))) {
                    return json.getString("IsValid");
                }

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if (loaderDialog.isShowing())
                loaderDialog.dismiss();
            if (result != null && result.equals("true")) {
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(
                                R.string.update_succes_msg_myprofile),
                        Toast.LENGTH_SHORT).show();
                new AccountDetails().execute();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(
                                R.string.update_succes_msg_myprofile),
                        Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }

    private class UpdatePasswordService extends AsyncTask<Void, Void, String> {

        boolean blSuccess = false;
        String cPass = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            loaderDialog.show();
            cPass = cpass.getText().toString();
        }

        @Override
        protected String doInBackground(Void... params) {

            // TODO Auto-generated method stub
            try {
                UrlParameterBuilder urlObj = new UrlParameterBuilder();

                String urlParameters = urlObj.getUpdatePassParams(cPass);

                String request = CommonFunctions.main_url
                        + CommonFunctions.lang + urlObj.getChangePasswordUrl();

                String res = new HttpHandler().makeServiceCallWithParams(
                        request, urlParameters);

                System.out.println("ChangePassword res" + res);

                JSONObject json = new JSONObject(res);

                if (json.getBoolean("IsValid") && json.getBoolean("IsSuccess")) {
                    blSuccess = true;
                    return json.getString("Error");
                } else {
                    return json.getString("Error");
                }

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if (loaderDialog.isShowing())
                loaderDialog.dismiss();
            if (result != null) {

                if (blSuccess) {
                    pass.setText("");
                    cpass.setText("");
                    blSuccess = false;
                }

                Toast.makeText(getApplicationContext(), result,
                        Toast.LENGTH_SHORT).show();

            }
            super.onPostExecute(result);
        }

    }

}
