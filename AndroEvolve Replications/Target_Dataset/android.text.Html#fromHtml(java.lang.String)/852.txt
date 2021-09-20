package com.localsocial.nhw;

import com.localsocial.AppReleaseInfo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.ImageView;
import android.widget.TextView;


public class About extends Activity {

    TextView mNotice;
    ImageView mImage;
    Bitmap bmp;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about); 
 
        mNotice = (TextView) findViewById(R.id.profilesview_title);
        mImage = (ImageView) findViewById(R.id.eventview_image);

        mNotice.setPadding(15, 12, 12, 12);
        mNotice.setTextSize(11);
        mNotice.append(Html.fromHtml("<big>Neighbourhood Watch v 1.0 is powered by LocalSocial.</big>"));
        mNotice.append("\n");
        mNotice.append("\n");
        
        mNotice.append(Html.fromHtml("<big>LocalSocial is designed to simplify the process of " +
                "building new mobile social applications that can re-mix social and local context " +
                "to offer a new, rich mobile experience. LocalSocial combines information about " +
                "people and things close to a user, with information from one or more of their " +
                "social networks, and makes that information available to a mobile application in a simple way.</big>"));
        mNotice.append("\n");
        mNotice.append("\n");
        mNotice.append(Html.fromHtml("<big>if you use other Bluetooth services you may need to end them for this application to work properly.</big>"));
        mNotice.append("\n");
        mNotice.append("\n");
        mNotice.append(Html.fromHtml("<big><b>Find out more at:</b></big>"));
        mNotice.append("\n");
        mNotice.append(Html.fromHtml("<big><b>http://www.mylocalsocial.com</b></big>"));
        mNotice.append("\n"); 
        mNotice.append("\n");
        mNotice.append(Html.fromHtml(new StringBuffer(AppReleaseInfo.getProject()).append("<br/>").toString()));
        mNotice.append(Html.fromHtml(new StringBuffer("\t Version   : ").append(AppReleaseInfo.getVersion()).append("<br/>").toString()));
        mNotice.append(Html.fromHtml(new StringBuffer("\t Build #   : ").append(AppReleaseInfo.getBuildNumber()).append("<br/>").toString()));
        mNotice.append(Html.fromHtml(new StringBuffer("\t Timestamp : ").append(AppReleaseInfo.getBuildTimeStamp()).append("<br/>").toString()));
//        mNotice.append(Html.fromHtml(new StringBuffer("\t URL       : ").append(AppReleaseInfo.getHome()).append("<br/>").toString()));
        mNotice.append("\n");
        
        Linkify.addLinks(mNotice, Linkify.WEB_URLS);

        bmp = BitmapFactory.decodeResource(this
                .getResources(), R.drawable.splash);
        mImage.setImageBitmap(bmp);
    }

}
