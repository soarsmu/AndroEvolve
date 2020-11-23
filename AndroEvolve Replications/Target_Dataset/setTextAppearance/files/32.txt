package com.mahoneydev.usdafmexchange.pages;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.mahoneydev.usdafmexchange.AppCodeResources;
import com.mahoneydev.usdafmexchange.ClickOnceListener;
import com.mahoneydev.usdafmexchange.FetchTask;
import com.mahoneydev.usdafmexchange.PageOperations;
import com.mahoneydev.usdafmexchange.R;
import com.mahoneydev.usdafmexchange.UserFileUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.security.PrivilegedActionException;
import java.util.Hashtable;

/**
 * Created by bichongg on 7/25/2016.
 */
public class page_016_vendorpage extends PageOperations{
    public static void showVendorpage() {
        Hashtable<String, String> ht = new Hashtable<String, String>();
        ht.put("vendorname", getRecentPage().params.get("username"));
        new FetchTask() {
            @Override
            protected void executeSuccess(JSONObject result) throws JSONException {
                TableLayout tl = (TableLayout) hashelements.get("vendorpageScrollTable");
                tl.removeAllViews();
                JSONObject vendorprofile = result.getJSONObject("vendorprofile");
                if (vendorprofile == null){
                    TextView non = new TextView(context);
                    non.setText("This vendor didn't set up profile.");
                }else {
                    TableRow lv = new TableRow(context);
                    lv.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, height / 5));
                    LinearLayout ll = new LinearLayout(context);
                    ll.setOrientation(LinearLayout.VERTICAL);

                    //Business Name
                    TextView pn = new TextView(context);
                    pn.setTextAppearance(context, R.style.Bold);
                    pn.setTextSize(width / 33);
                    pn.setText(vendorprofile.getString("business_name"));
                    pn.setGravity(Gravity.CENTER);
                    ll.addView(pn);
                    //
                    TextView br1 = new TextView(context);
                    br1.setText("");
                    ll.addView(br1);

                    //TableLayout
                    TableLayout tl_in = new TableLayout(context);
                    //Email
                    TableRow tr1 = new TableRow(context);
                    tr1.setPadding(0,10,0,0);
                    TextView email1 = new TextView(context);
                    email1.setTextAppearance(context, R.style.Bold);
                    email1.setTextSize(width / 45);
                    email1.setText("Email:");
                    tr1.addView(email1);
                    TextView email = new TextView(context);
                    email.setLayoutParams(new TableRow.LayoutParams((int) (width * 6 / 10), TableRow.LayoutParams.WRAP_CONTENT));
                    email.setTextAppearance(context, R.style.Normal);
                    email.setTextSize(width / 45);
                    if (vendorprofile.has("business_email")){
                        email.setText(vendorprofile.getString("business_email"));
                    }

                    tr1.addView(email);
                    tl_in.addView(tr1);
                    //Phone
                    TableRow tr2 = new TableRow(context);
                    tr2.setPadding(0,10,0,0);
                    TextView phone1 = new TextView(context);
                    phone1.setTextAppearance(context, R.style.Bold);
                    phone1.setTextSize(width / 45);
                    phone1.setText("Phone:");
                    tr2.addView(phone1);
                    TextView phone = new TextView(context);
                    phone.setLayoutParams(new TableRow.LayoutParams((int) (width * 6 / 10), TableRow.LayoutParams.WRAP_CONTENT));
                    phone.setTextAppearance(context, R.style.Normal);
                    phone.setTextSize(width / 45);
                    if (vendorprofile.has("business_phone")){
                        phone.setText(vendorprofile.getString("business_phone"));
                    }
                    tr2.addView(phone);
                    tl_in.addView(tr2);
                    //Address
                    TableRow tr3 = new TableRow(context);
                    tr3.setPadding(0,10,0,0);
                    TextView address1 = new TextView(context);
                    address1.setTextAppearance(context, R.style.Bold);
                    address1.setTextSize(width / 45);
                    address1.setText("Address:");
                    tr3.addView(address1);
                    TextView address = new TextView(context);
                    address.setLayoutParams(new TableRow.LayoutParams((int) (width * 6 / 10), TableRow.LayoutParams.WRAP_CONTENT));
                    address.setTextAppearance(context, R.style.Normal);
                    address.setTextSize(width / 45);
                    address.setText(vendorprofile.getString("business_street") + vendorprofile.getString("business_street") + ", "
                            + vendorprofile.getString("business_city") + ", " + vendorprofile.getString("business_state") + ", " + vendorprofile.getString("business_zip"));
                    tr3.addView(address);
                    tl_in.addView(tr3);
                    //Website
                    TableRow tr4 = new TableRow(context);
                    tr4.setPadding(0,10,0,0);
                    TextView web1 = new TextView(context);
                    web1.setTextAppearance(context, R.style.Bold);
                    web1.setTextSize(width / 45);
                    web1.setText("Website:");
                    tr4.addView(web1);
                    TextView web = new TextView(context);
                    web.setLayoutParams(new TableRow.LayoutParams((int) (width * 6 / 10), TableRow.LayoutParams.WRAP_CONTENT));
                    web.setTextAppearance(context, R.style.Normal);
                    web.setTextSize(width / 45);
                    web.setText(vendorprofile.getString("business_website"));
                    tr4.addView(web);
                    tl_in.addView(tr4);
                    //Social media
                    TableRow tr5 = new TableRow(context);
                    tr5.setPadding(0,10,0,0);
                    TextView media1 = new TextView(context);
                    media1.setTextAppearance(context, R.style.Bold);
                    media1.setTextSize(width / 45);
                    media1.setText("Facebook:");
                    tr5.addView(media1);
                    TextView media = new TextView(context);
                    media.setLayoutParams(new TableRow.LayoutParams((int) (width * 6 / 10), TableRow.LayoutParams.WRAP_CONTENT));
                    media.setTextAppearance(context, R.style.Normal);
                    media.setTextSize(width / 45);
                    media.setText(vendorprofile.getString("business_facebook"));
                    tr5.addView(media);
                    tl_in.addView(tr5);
                    TableRow tr51 = new TableRow(context);
                    tr51.setPadding(0,10,0,0);
                    TextView media11 = new TextView(context);
                    media11.setTextAppearance(context, R.style.Bold);
                    media11.setTextSize(width / 45);
                    media11.setText("Twitter:");
                    tr51.addView(media11);
                    TextView media12 = new TextView(context);
                    media12.setLayoutParams(new TableRow.LayoutParams((int) (width * 6 / 10), TableRow.LayoutParams.WRAP_CONTENT));
                    media12.setTextAppearance(context, R.style.Normal);
                    media12.setTextSize(width / 45);
                    media12.setText(vendorprofile.getString("business_twitter"));
                    tr51.addView(media12);
                    tl_in.addView(tr51);
                    //Organic
                    TableRow tr6 = new TableRow(context);
                    tr6.setPadding(0,10,0,10);
                    TextView org1 = new TextView(context);
                    org1.setLayoutParams(new TableRow.LayoutParams((int) (width * 3 / 10), TableRow.LayoutParams.WRAP_CONTENT));
                    org1.setTextAppearance(context, R.style.Bold);
                    org1.setTextSize(width / 45);
                    org1.setText("Organic:");
                    tr6.addView(org1);
                    LinearLayout ll_in = new LinearLayout(context);
                    ImageView org = new ImageView(context);
                    org.setLayoutParams(new TableRow.LayoutParams((int) (width * 6 / 10), TableRow.LayoutParams.WRAP_CONTENT));
                    String data = "business_usdaorganic";
                    Object json = new JSONTokener(data).nextValue();
                    if(json instanceof JSONArray){
                        JSONArray organic = vendorprofile.getJSONArray("business_usdaorganic");
                        if (organic.getString(0).equals("yes")) {
                            org.setImageResource(R.drawable.usda_organic);
                            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(50, 50);
                            parms.gravity = Gravity.START;
                            org.setLayoutParams(parms);
                        }
                        ll_in.addView(org);
                    }
                    tr6.addView(ll_in);
                    tl_in.addView(tr6);

                    ll.addView(tl_in);

                    //
                    TextView br2 = new TextView(context);
                    br2.setText("");
                    ll.addView(br2);

                    RelativeLayout ll_bt = new RelativeLayout(context);
                    RelativeLayout.LayoutParams lparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (width / 10));
                    ll_bt.setLayoutParams(lparam);
                    //Request Friend
                    Button rf = new Button(context);
                    RelativeLayout.LayoutParams rfparams = new RelativeLayout.LayoutParams((int) (width / 3),RelativeLayout.LayoutParams.MATCH_PARENT);
                    rfparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    rf.setLayoutParams(rfparams);
                    rf.setBackgroundColor(Color.parseColor("#A2D25A"));
                    rf.setTextAppearance(context, R.style.White);
                    rf.setTextSize(width / 50);
                    rf.setText("Request friend");
                    rf.setTransformationMethod(null);
                    ll_bt.addView(rf);

                    rf.setOnClickListener(new requestFriendListener(rf));
                    //Add to list
                    Button al = new Button(context);
                    RelativeLayout.LayoutParams alparams = new RelativeLayout.LayoutParams((int) (width / 3),RelativeLayout.LayoutParams.MATCH_PARENT);
                    alparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    al.setLayoutParams(alparams);
                    al.setBackgroundColor(Color.parseColor("#A2D25A"));
                    al.setTextAppearance(context, R.style.White);
                    al.setTextSize(width / 50);
                    al.setText("Add to list");
                    al.setTransformationMethod(null);
                    ll_bt.addView(al);

                    ll.addView(ll_bt);

                    //
                    TextView br3 = new TextView(context);
                    br3.setText("");
                    ll.addView(br3);

                    ll.setLayoutParams(new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
                    lv.addView(ll);
                    tl.addView(lv);
                }
                setupUI(playout);

            }
        }.execute(AppCodeResources.postUrl("usdatestchongguang", "public_display_vendorprofile_bypost", ht));
    }

    public static class requestFriendListener extends ClickOnceListener {
        public requestFriendListener(View button) {
            super(button);
        }
        public void action(){
            Hashtable<String,String> ht=new Hashtable<>();
            String token_s = UserFileUtility.get_token();
            ht.put("os", "Android");
            ht.put("token", token_s);
            ht.put("fname", getRecentPage().params.get("username"));
            new FetchTask(){
                @Override
                protected void executeSuccess(JSONObject result) throws JSONException {
                    Toast toast = Toast.makeText(context, result.getString("results"), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }.execute(AppCodeResources.postUrl("usdafriendship", "friends_request_friendship", ht));
        }
    }
}
