package com.example.a85625.seoultour;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    List<Info_item> items;

    ImageView image;
    TextView title_tv, zipcode_tv, tel_tv, telname_tv, homepage_tv, overview_tv;
    String systemLang = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        init();

        Intent intent = getIntent();
        int id = intent.getExtras().getInt("id");

        items = DB.selectCommonInfo(getBaseContext(), id);

        Info_item item = items.get(0);
        systemLang = getResources().getConfiguration().locale.getDefault().getLanguage();  // 시스템설정 언어 가져오기
        if(systemLang.equals("en")){
            title_tv.setTextSize(25);
        }
        title_tv.setText(item.title);
        if(item.firstImage.equals("null")){
            Glide.with(this).load(R.drawable.no_image).into(image);
        }else{
            Glide.with(this).load(item.firstImage).into(image);
        }
        if(item.zipcode.equals("null")){
            if(item.addr1.equals("null")) {
                zipcode_tv.setText("(NO DATA)");
            }else {
                zipcode_tv.setText(item.addr1);
            }
        }else{
            zipcode_tv.setText(item.addr1+" ("+item.zipcode+")");
        }
        if(item.tel.equals("null")){
            tel_tv.setText("(NO DATA)");
        }else{
            tel_tv.setText(item.tel);
        }
        if(item.telname.equals("null")){
            telname_tv.setText("(NO DATA)");
        }else {
            telname_tv.setText(item.telname);
        }
        if(item.homepage.equals("null")){
            homepage_tv.setText(Html.fromHtml( "(NO DATA)"));
        }else{
            homepage_tv.setText(Html.fromHtml( item.homepage));
        }
        homepage_tv.setMovementMethod(LinkMovementMethod.getInstance());
        if(item.overview.equals("null")){
            overview_tv.setText("(NO DATA)");
        }else{
            overview_tv.setText(Html.fromHtml(item.overview));
        }
    }

    void init(){
        items = new ArrayList<Info_item>();

        image = (ImageView)findViewById(R.id.imageView1);
        title_tv = (TextView)findViewById(R.id.title_tv);
        zipcode_tv = (TextView)findViewById(R.id.zipcode_tv);
        tel_tv = (TextView)findViewById(R.id.tel_tv);
        telname_tv = (TextView)findViewById(R.id.telname_tv);
        homepage_tv = (TextView)findViewById(R.id.homepage_tv);
        overview_tv = (TextView)findViewById(R.id.overview_tv);
    }
}

class Info_item {
    public String title;
    public String firstImage;
    public String zipcode;
    public String addr1;
    public String tel;
    public String telname;
    public String homepage;
    public String overview;
    Info_item(){
    }
}
