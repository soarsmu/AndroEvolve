package com.infinitum_micro_tech.proxy_setter_pro.Activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.infinitum_micro_tech.proxy_setter_pro.Config;
import com.infinitum_micro_tech.proxy_setter_pro.ConnectionStatictic;
import com.infinitum_micro_tech.proxy_setter_pro.Language;
import com.infinitum_micro_tech.proxy_setter_pro.Proxy.Proxy;
import com.infinitum_micro_tech.proxy_setter_pro.Proxy.ProxyData;
import com.infinitum_micro_tech.proxy_setter_pro.Proxy.ProxyFindConfig;
import com.infinitum_micro_tech.proxy_setter_pro.R;
import com.startad.lib.SADView;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {



    //Buttons
    ImageButton SetProxy;
    ImageButton UnSetProxyButton;
    ImageButton OpenProxyConfigButton;
    ImageButton copy_ip_main;
    Button ChooseLanguageButton;

    //Text
    static TextSwitcher ipTextView;
    static TextSwitcher typeTextView;
    static  TextSwitcher anonTextView;
    static TextSwitcher delayTextView;
    static TextSwitcher countryTextView;
    static TextSwitcher proxy_source_text;
    static TextSwitcher proxy_source_text_;
    static TextSwitcher proxy_status_text;
    static TextView Info;

    //list
    ListView language_listveiw;

    //Swipe
    SwipeRefreshLayout swipeRefreshLayout;

    //Anim
    Animation in_text_animationUser;
    Animation out_text_animation;

    //bool
    private boolean isReceiverRegistered = false;
    boolean button_delete_state=false;

    //Strings
    String TAG="myapp";
    final static  String SAD_APPLICAITON_ID="5b6b431ce12c346e008b4567";

    WifiReceiverConnect receiverConnect;

    protected SADView sadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Init Language
        Config.ConfigInit(getApplicationContext());
        if (!Config.language.equals("")) {
            String languageToLoad = Config.language; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
        setContentView(R.layout.activity_main);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Sad
        this.sadView = new SADView(this, SAD_APPLICAITON_ID);

        this.sadView.loadAd(((getString(R.string.this_language).toLowerCase().equals("ru")) ? SADView.LANGUAGE_RU : SADView.LANGUAGE_EN)); //or this.sadView.loadAd(SADView.LANGUAGE_RU);
        //swipe
        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#236391"));


        //Find
        SetProxy=findViewById(R.id.set_proxy);
        UnSetProxyButton=findViewById(R.id.unset_proxy_button);
        OpenProxyConfigButton=findViewById(R.id.proxy_config);
        ChooseLanguageButton=findViewById(R.id.choose_language_button);
        ipTextView = findViewById(R.id.info_ip);
        typeTextView=findViewById(R.id.info_type);
        anonTextView=findViewById(R.id.info_anon);
        delayTextView=findViewById(R.id.info_delay);
        countryTextView=findViewById(R.id.info_country);
        proxy_source_text=findViewById(R.id.proxy_source_m);
        proxy_source_text_=findViewById(R.id.proxy_source_);
        proxy_status_text=findViewById(R.id.info_p_status);
        copy_ip_main=findViewById(R.id.copy_ip_main);


        ChooseLanguageButton.setText(getString(R.string.this_language));


        in_text_animationUser= AnimationUtils.loadAnimation(this, R.anim.alpha_text_in_user);
        out_text_animation=AnimationUtils.loadAnimation(this,R.anim.alpha_text_out);

        ViewSwitcher.ViewFactory viewFactory = new ViewSwitcher.ViewFactory() {
            @SuppressLint("ResourceAsColor")
            @Override
            public View makeView() {
                TextView textView = new TextView(MainActivity.this);
                textView.setTextSize(16);
                textView.setTextColor(Color.parseColor("#3b4644"));
                return textView;
            }
        };

        receiverConnect=new WifiReceiverConnect();

        ipTextView.setInAnimation(in_text_animationUser);
        typeTextView.setInAnimation(in_text_animationUser);
        anonTextView.setInAnimation(in_text_animationUser);
        countryTextView.setInAnimation(in_text_animationUser);
        delayTextView.setInAnimation(in_text_animationUser);
        proxy_source_text.setInAnimation(in_text_animationUser);
        proxy_source_text_.setInAnimation(in_text_animationUser);
        proxy_status_text.setInAnimation(in_text_animationUser);

        anonTextView.setOutAnimation(out_text_animation);
        delayTextView.setOutAnimation(out_text_animation);
        countryTextView.setOutAnimation(out_text_animation);
        ipTextView.setOutAnimation(out_text_animation);
        typeTextView.setOutAnimation(out_text_animation);
        proxy_source_text.setOutAnimation(out_text_animation);
        proxy_source_text_.setOutAnimation(out_text_animation);
        proxy_status_text.setOutAnimation(out_text_animation);

        ipTextView.setFactory(viewFactory);

        typeTextView.setFactory(viewFactory);
        anonTextView.setFactory(viewFactory);
        countryTextView.setFactory(viewFactory);
        proxy_source_text.setFactory(viewFactory);
        delayTextView.setFactory(viewFactory);

        proxy_status_text.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(MainActivity.this);
                textView.setTextSize(16);
                textView.setTextColor(Color.parseColor("#FF236391"));
                return textView;
            }
        });
        proxy_source_text_.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(MainActivity.this);
                textView.setTextSize(16);
                textView.setTextColor(Color.parseColor("#FF236391"));
                return textView;
            }
        });





        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverConnect);

    }

    @Override
    protected void onResume() {
        registerReceiver(receiverConnect,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onResume();

    }

    void setTextForInfo(final ProxyData data) {

        if (data.proxy_is) {

            UnSetProxyButton.setEnabled(true);
            copy_ip_main.setVisibility(View.VISIBLE);
            copy_ip_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipData clipData = ClipData.newPlainText("text", data.host +
                            ":" + String.valueOf(data.port));
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(getApplicationContext(), "IP (" + data.host +
                            ":" + String.valueOf(data.port) + ")" + getString(R.string.Is_Copyies), Toast.LENGTH_SHORT).show();
                }
            });
            String htmlTaggedString = "<u><strong>IP</strong></u>: " + data.host + ":" + String.valueOf(data.port);
            Spanned textSpan = android.text.Html.fromHtml(htmlTaggedString);

            ipTextView.setText(textSpan);
            htmlTaggedString = "<u><strong>https://hidemyna.me/en/proxy-list/</strong></u>";
            textSpan = android.text.Html.fromHtml(htmlTaggedString);
            proxy_source_text_.setText(textSpan);
            proxy_source_text_.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://hidemyna.me/en/proxy-list/"));
                   startActivity(intent);
                }
            });
            htmlTaggedString = "<u><strong>" + getString(R.string.Proxy_Source) + "</strong></u> ";
            textSpan = android.text.Html.fromHtml(htmlTaggedString);
            proxy_source_text.setText(textSpan);

            htmlTaggedString = "<strong>" + getString(R.string.ProxyIsSet) + "</strong> ";
            textSpan = android.text.Html.fromHtml(htmlTaggedString);
            proxy_status_text.setText(textSpan);


            htmlTaggedString = "<u><strong>" + getString(R.string.Proxy_Type) + "</strong></u>: " + data.type;
            textSpan = android.text.Html.fromHtml(htmlTaggedString);
            typeTextView.setText(textSpan);

            if (data.delay != 0) {
                htmlTaggedString = "<u><strong>" + getString(R.string.Server_Delay) + "</strong></u>: " + String.valueOf(data.delay)+"ms";
                textSpan = android.text.Html.fromHtml(htmlTaggedString);
                delayTextView.setText(textSpan);

            } else {
                htmlTaggedString = "<u><strong>" + getString(R.string.Server_Delay) + "</strong></u>: " + getString(R.string.Dont_have_data);
                textSpan = android.text.Html.fromHtml(htmlTaggedString);
                delayTextView.setText(textSpan);

            }

            htmlTaggedString = "<u><strong>" + getString(R.string.Country) + "</strong></u>: " + data.country;
            textSpan = android.text.Html.fromHtml(htmlTaggedString);
            countryTextView.setText(textSpan);

            htmlTaggedString = "<u><strong>" + getString(R.string.Anonimity) + "</strong></u>: " + data.anonymity;
            textSpan = android.text.Html.fromHtml(htmlTaggedString);

            anonTextView.setText(textSpan);

        } else {

                typeTextView.setText("");
                anonTextView.setText("");
                delayTextView.setText("");
                countryTextView.setText("");
                proxy_source_text.setText("");
                proxy_source_text_.setText("");
                ipTextView.setText("");
                copy_ip_main.setVisibility(View.GONE);
                UnSetProxyButton.setEnabled(false);
                button_delete_state = false;
                Spanned textSpan=null;
                if (ConnectionStatictic.WifiConnected(getApplicationContext())) {
                    String htmlTaggedString = "<strong>" + getString(R.string.Proxy_dont_set) + "</strong>";
                     textSpan= android.text.Html.fromHtml(htmlTaggedString);
                }else {
                    String htmlTaggedString = "<strong>" + getString(R.string.NoNetwork) + "</strong>";
                   textSpan = android.text.Html.fromHtml(htmlTaggedString);
                }
                proxy_status_text.setText(textSpan);

            }
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setEnabled(false);

    }


    public void SetProxy(View view) {
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setRefreshing(true);

        if (ConnectionStatictic.WifiConnected(getApplicationContext())) {

            if (!ConnectionStatictic.NowProxyConnection(getApplicationContext()).proxy_is) {

                if (ProxyFindConfig.use_my_proxy){
                    SettingProxy(Proxy.getMyProxy());
                }else {
                    SetProxy mt = new SetProxy("https://www.yandex.ru/");
                    mt.execute();

                }


            }else {
                swipeRefreshLayout.setEnabled(false);
                swipeRefreshLayout.setRefreshing(false);
                Dialog(getString(R.string.This_Connection_use_proxy));


            }
        }else {
            swipeRefreshLayout.setEnabled(false);
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(MainActivity.this,getString(R.string.Connect_to_network),Toast.LENGTH_LONG).show();}



    }
    private void FirstConnectionDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.set_proxy_dialog);

        final Button ok = (Button) dialog.findViewById(R.id.ok_proxy_dialog);
        TextView t = dialog.findViewById(R.id.text_view);
        t.setText(getString(R.string.first_connection_dialog));
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId()==ok.getId()){

                    Intent intent= new Intent(MainActivity.this,FirstConnectionActivity.class);
                    startActivity(intent);

                    dialog.dismiss();

                }

            }
        };
        ok.setOnClickListener(onClickListener);

        dialog.show();
    }
    private void Dialog(String text) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.set_proxy_dialog);

        final Button ok = (Button) dialog.findViewById(R.id.ok_proxy_dialog);
        TextView t = dialog.findViewById(R.id.text_view);
        t.setText(text);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId()==ok.getId()){



                    dialog.dismiss();

                }

            }
        };
        ok.setOnClickListener(onClickListener);

        dialog.show();
    }
    public void UnsetProxy(View view) {

        if (ConnectionStatictic.WifiConnected(getApplicationContext())) {
            if (ConnectionStatictic.NowProxyConnection(getApplicationContext()).proxy_is) {

                Proxy.UnsetProxy(this);
                swipeRefreshLayout.setRefreshing(true);
            } else {

                Toast.makeText(this, getString(R.string.Connect_dont_use_proxy), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, getString(R.string.You_dont_connected_to_nework), Toast.LENGTH_SHORT).show();

        }

    }



    public void OpenProxyConfig(View view) {

             Intent intent = new Intent(this, ProxyConfigActivity.class);
             startActivity(intent);

    }


    void SettingProxy(ProxyData pd){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (Proxy.ItsMyNetwork(getApplicationContext())) {
                Log.d(TAG, "Is My Network");
                Proxy.SetProxy(getApplicationContext(), pd.host, pd.port);

            } else {
                Proxy.NowHost = pd.host;
                Proxy.NowPort = pd.port;
                FirstConnectionDialog();


            }
        } else {
            Proxy.SetProxy(getApplicationContext(), pd.host, pd.port);


        }
        Config.SaveNowProxy(getApplicationContext(),pd);
    }

    public void ShowProxyList(View view) {
        if (ConnectionStatictic.WifiConnected(getApplicationContext())) {
            if (!ConnectionStatictic.NowProxyConnection(getApplicationContext()).proxy_is) {
                Intent intent = new Intent(this, ProxyListActivity.class);
                startActivity(intent);
            } else {
                Dialog(getString(R.string.This_Connection_use_proxy_3));
            }


        }else{
                Toast.makeText(MainActivity.this, getString(R.string.Connect_to_network), Toast.LENGTH_LONG).show();
            }


    }

    public void ShowChooseLanguageDialog(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.choose_language_dialog);
        language_listveiw=dialog.findViewById(R.id.language_list);
        language_listveiw.setAdapter(new LanguageBoxAdapter(this,Config.languageArrayList));
        dialog.show();
    }
    class LanguageBoxAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater lInflater;
        ArrayList<Language> objects;

        LanguageBoxAdapter(Context context, ArrayList<Language> languages) {
            ctx = context;
            objects = languages;
            lInflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // кол-во элементов
        @Override
        public int getCount() {
            return objects.size();
        }

        // элемент по позиции
        @Override
        public Object getItem(int position) {
            return objects.get(position);
        }

        // id по позиции
        @Override
        public long getItemId(int position) {
            return position;
        }

        // пункт списка
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // используем созданные, но не используемые view
            View view = convertView;
            if (view == null) {
                view = lInflater.inflate(R.layout.choose_language_item, parent, false);
            }

            Language p = getProduct(position);


            CheckBox cbBuy = (CheckBox) view.findViewById(R.id.box_lan_item);

            cbBuy.setOnCheckedChangeListener(null);
            cbBuy.setTag(position);
            cbBuy.setText(p.language);

            cbBuy.setChecked(p.use);
            cbBuy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // меняем данные товара (в корзине или нет)
                    getProduct((Integer) buttonView.getTag()).use = isChecked;
                    int id = (Integer) buttonView.getTag();
                    if (isChecked) {
                        for (int i = 0; i < objects.size(); i++) {
                            Language m = objects.get(i);
                            if (i == id) {
                                m.use = true;
                                Config.SaveLanguage(getApplicationContext(), objects.get(i).language_code);
                                String languageToLoad = Config.language; // your language
                                Locale locale = new Locale(languageToLoad);
                                Locale.setDefault(locale);
                                Configuration config = new Configuration();
                                config.locale = locale;
                                getBaseContext().getResources().updateConfiguration(config,
                                        getBaseContext().getResources().getDisplayMetrics());
                            } else {
                                m.use = false;
                            }
                            objects.set(i, m);

                        }

                        LanguageBoxAdapter adapter = new LanguageBoxAdapter(getApplicationContext(), objects);
                        language_listveiw.setAdapter(adapter);
                        Config.InitLanguagesArray(getApplicationContext());
                        ChooseLanguageButton.setText(getString(R.string.this_language));


                    } else {


                        Locale locale = Locale.getDefault();
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getBaseContext().getResources().updateConfiguration(config,
                                getBaseContext().getResources().getDisplayMetrics());
                        Config.SaveLanguage(getApplicationContext(), getString(R.string.this_language).toLowerCase());
                        for (int i = 0; i < objects.size(); i++) {
                            Language m = objects.get(i);
                            if (objects.get(i).language_code.equals(Config.language)) {
                                m.use = true;

                            } else {
                                m.use = false;
                            }
                            objects.set(i, m);

                        }
                        LanguageBoxAdapter adapter = new LanguageBoxAdapter(getApplicationContext(), objects);
                        language_listveiw.setAdapter(adapter);
                        Config.InitLanguagesArray(getApplicationContext());
                        ChooseLanguageButton.setText(getString(R.string.this_language));


                    }
                }
            });

            Log.d(TAG, "getView: "+p.language);
            return view;
        }

        // товар по позиции
        Language getProduct(int position) {
            return ((Language) getItem(position));
        }




    }

    public void FindProxy(View view) {
        if (ConnectionStatictic.WifiConnected(getApplicationContext())) {
            if (!ConnectionStatictic.NowProxyConnection(getApplicationContext()).proxy_is) {
                swipeRefreshLayout.setEnabled(true);
                swipeRefreshLayout.setRefreshing(true);
                FindProxyTask findProxyTask = new FindProxyTask("https://www.yandex.ru/");
                findProxyTask.execute();
            } else {
                Dialog(getString(R.string.This_Connection_use_proxy_2));
            }
        }else {
            Toast.makeText(MainActivity.this,getString(R.string.Connect_to_network),Toast.LENGTH_LONG).show();}

    }

    void FindProxyDialog(final ProxyData data){
        Dialog view=new Dialog(this);
        view.setContentView(R.layout.find_proxy_dialog);

        ImageButton copy_ip_button;
        ImageButton set_proxy_button;
        TextView ip=view.findViewById(R.id.ip_itemm);
        TextView delay=view.findViewById(R.id.delay_itemm);
        TextView anon=view.findViewById(R.id.anon_itemm);
        TextView type=view.findViewById(R.id.type_itemm);
        TextView country=view.findViewById(R.id.country_itemm);
        TextView source_p=view.findViewById(R.id.source_dialog);

        source_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://hidemyna.me/en/proxy-list/"));
                startActivity(intent);
            }
        });
        String htmlTaggedString  = "<u><strong>IP</strong></u>: "+data.host+":"+String.valueOf(data.port);
        Spanned textSpan  =  android.text.Html.fromHtml(htmlTaggedString);

        ip.setText(textSpan);


        htmlTaggedString="<u><strong>"+getString(R.string.Proxy_Type)+"</strong></u>: "+data.type;
        textSpan  =  android.text.Html.fromHtml(htmlTaggedString);
        type.setText(textSpan);

        htmlTaggedString="<u><strong>"+getString(R.string.Server_Delay)+"</strong></u>: "+String.valueOf(data.delay)+"ms";
        textSpan  =  android.text.Html.fromHtml(htmlTaggedString);
        delay.setText(textSpan);


        htmlTaggedString="<u><strong>"+getString(R.string.Country)+"</strong></u>: "+data.country;
        textSpan  =  android.text.Html.fromHtml(htmlTaggedString);
        country.setText(textSpan);

        htmlTaggedString="<u><strong>"+getString(R.string.Anonimity)+"</strong></u>: "+data.anonymity;
        textSpan  =  android.text.Html.fromHtml(htmlTaggedString);

        anon.setText(textSpan);


        copy_ip_button=view.findViewById(R.id.copy_ipp);
        set_proxy_button=view.findViewById(R.id.set_thiss);



        copy_ip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipData clipData = ClipData.newPlainText("text",data.host+
                        ":"+String.valueOf(data.port));
                ClipboardManager clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(getApplicationContext(),"IP ("+data.host+
                        ":"+String.valueOf(data.port)+") "+getString(R.string.Is_Copyies) ,Toast.LENGTH_SHORT).show();
            }
        });
        set_proxy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.setEnabled(true);
                swipeRefreshLayout.setRefreshing(true);
                SettingProxy(data);

            }
        });
        view.show();
    }


    class FindProxyTask extends AsyncTask<Void, Void, Void> {

        String txt="";//Тут храним значение заголовка сайта
        String test_link="";
        ProxyData proxyData;
        FindProxyTask(String s){
            test_link=s;
        }
        @Override
        protected Void doInBackground(Void... params) {

            Time time_now = new Time();   time_now.setToNow();
            Time last_save=Config.getLastSaveTime(getApplicationContext());



            ProxyData pd= Proxy.FindProxy(test_link,getApplicationContext(),DataOld(time_now,last_save));
            proxyData=pd;
            txt= "Proxy ("+test_link+") "+pd.host+":"+String.valueOf(pd.port)+" delay: "+String.valueOf(pd.delay);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute: i life");
            if (proxyData.error.equals("")) {
                  FindProxyDialog(proxyData);
            }else {
                Dialog(getString(R.string.proxy_not_found));
            }
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setEnabled(false);
        }
    }


    class SetProxy extends AsyncTask<Void, Void, Void> {

        String txt="";//Тут храним значение заголовка сайта
        String test_link="";
        ProxyData proxyData;
        SetProxy(String s){
            test_link=s;
        }
        @Override
        protected Void doInBackground(Void... params) {

            Time time_now = new Time();   time_now.setToNow();
            Time last_save=Config.getLastSaveTime(getApplicationContext());



            ProxyData pd= Proxy.FindProxy(test_link,getApplicationContext(),DataOld(time_now,last_save));
            proxyData=pd;
            txt= "Proxy ("+test_link+") "+pd.host+":"+String.valueOf(pd.port)+" delay: "+String.valueOf(pd.delay);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute: i life");
            if (proxyData.error.equals("")) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    if (Proxy.ItsMyNetwork(getApplicationContext())) {
                        Log.d(TAG, "Is My Network");
                        Proxy.SetProxy(getApplicationContext(), proxyData.host, proxyData.port);

                    } else {
                        Proxy.NowHost = proxyData.host;
                        Proxy.NowPort = proxyData.port;
                        swipeRefreshLayout.setRefreshing(false);
                        FirstConnectionDialog();


                    }
                } else {
                    Proxy.SetProxy(getApplicationContext(), proxyData.host, proxyData.port);
                }
                Config.SaveNowProxy(getApplicationContext(),proxyData);

            }else {
                Dialog(getString(R.string.proxy_not_found));
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(false);

            }
            return;
        }
    }

    class WifiReceiverConnect extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            String action = intent.getAction();
            if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
                Log.d(TAG, "onReceive: connection_changes");
                if (ConnectionStatictic.WifiConnected(getApplicationContext())){
                    try {
                        ProxyData proxyData =ConnectionStatictic.NowProxyConnection(getApplicationContext());
                        if (proxyData.proxy_is) {
                            setTextForInfo(Config.NowProxy(getApplicationContext()));
                        }else {
                            ProxyData p = new ProxyData();
                            p.proxy_is=false;
                            setTextForInfo(p);
                        }

                    }catch (Exception e){
                        ProxyData p = new ProxyData();
                        p.proxy_is=false;
                        setTextForInfo(p);
                    }

                }else {
                    ProxyData p = new ProxyData();
                    p.proxy_is=false;
                    setTextForInfo(p);
                }
            }


        }

    }

    static boolean DataOld(Time now_time, Time last_save){
        Log.d("myapp", "DataOld: time now:"+now_time.year+"."+now_time.month+"."+now_time.monthDay+"   "+now_time.hour+":"+now_time.minute);
        Log.d("myapp", "DataOld: time lastsave:"+last_save.year+"."+last_save.month+"."+last_save.monthDay+"   "+last_save.hour+":"+last_save.minute);
        int min_now=now_time.hour*60+now_time.minute;
        int min_lastsave=last_save.hour*60+last_save.minute;

        if (now_time.year!=last_save.year)
        {
            return true;
        }

        if (now_time.month!=last_save.month){
            return true;
        }

        if (now_time.monthDay!=last_save.monthDay){
            return true;
        }

        if (min_now-30>=min_lastsave){
            return true;
        }
        return false;

    }
}
