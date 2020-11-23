package com.thinkgeniux.focusi.Activities_English;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thinkgeniux.focusi.Config;
import com.thinkgeniux.focusi.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPassword extends AppCompatActivity {
EditText ed_pass_new,ed_confirm_pass;
Button btn_reset_new;
    String lang;
    TextView title;
    private ProgressDialog loading;
    String s_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences=getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        lang= sharedPreferences.getString(Config.SHARED_PREF_LANG,null);

        if (lang.equals(Config.ARABIC_SYMBOL_LANG))
        {
            setContentView(R.layout.activity_reset_password_arabic);
            title=(TextView)findViewById(R.id.title);
            title.setText("Forget Password");
        }else {
            setContentView(R.layout.activity_reset_password);
            title=(TextView)findViewById(R.id.title);
            title.setText("Forget Password");
        }



        Intent intent=getIntent();
        s_email=intent.getStringExtra("email");

        ed_pass_new=(EditText)findViewById(R.id.ed_pass_new);
        ed_confirm_pass=(EditText)findViewById(R.id.ed_confirm_pass);
        btn_reset_new=(Button)findViewById(R.id.btn_reset_new);
        btn_reset_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ed_pass_new.getText().length()==0) {
                    ed_pass_new.requestFocus();
                    if (lang.equals("2"))
                    {
                        ed_pass_new.setError(Html.fromHtml("<font color='red'>رجاءا أدخل بريدك الإلكتروني</font>"));
                    }else {
                        ed_pass_new.setError(Html.fromHtml("<font color='red'>Please Enter Your Email</font>"));
                    }

                } else if (!ed_pass_new.getText().toString().equals(ed_confirm_pass.getText().toString()))
                {
                    if (lang.equals("2"))
                    {
                        ed_confirm_pass.setError(Html.fromHtml("<font color='red'>كلمة المرور لا تتطابق</font>"));
                    }else {
                        ed_confirm_pass.setError(Html.fromHtml("<font color='red'>Password Don't Match</font>"));
                    }
                }



                   else if (view == btn_reset_new) {
                    //RegisterOrder();
                    //  PlaceorderFuc();
                    SendingNewPassword();


                }
            }
        });

    }
    private void SendingNewPassword()
    {
        loading = ProgressDialog.show(ResetPassword.this,"Loading...","Please wait...",false,false);
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_SENDING_NEWPASS, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                JSONArray jsonObject = null;
                try {
                    jsonObject = new JSONArray(response);
                    JSONObject jsonObject1=jsonObject.getJSONObject(0);
                    if (jsonObject1.getString("status").equals("Password updated.")) {
                        Toast.makeText(ResetPassword.this.getApplicationContext(), jsonObject1.getString("status"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPassword.this, SignIn.class);
                        intent.putExtra("cart","main");
                        startActivity(intent);
                    }else
                        {
                            Toast.makeText(ResetPassword.this.getApplicationContext(), jsonObject1.getString("status"), Toast.LENGTH_SHORT).show();

                        }
                } catch (JSONException e) {
                    Toast.makeText(ResetPassword.this.getApplicationContext(), "Check Your PasswordXk;l", Toast.LENGTH_SHORT).show();


                    e.printStackTrace();
                }








                //  tvSurah.setText("Response is: "+ response.substring(0,500));
            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                //  Log.e("Error",error.printStackTrace());
                Toast.makeText(ResetPassword.this.getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", s_email);
                params.put("password", ed_pass_new.getText().toString().trim());
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(ResetPassword.this.getApplicationContext());
        requestQueue.add(request);
    }
}
