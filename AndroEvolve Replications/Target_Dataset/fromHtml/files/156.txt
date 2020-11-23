package app.android.pmdlocker.com.pmd_locker.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import app.android.pmdlocker.com.pmd_locker.R;
import app.android.pmdlocker.com.pmd_locker.database.LangDataManager;
import app.android.pmdlocker.com.pmd_locker.interfaces.ICallbackAskDialog;
import app.android.pmdlocker.com.pmd_locker.interfaces.ICallbackChooseButton;
import app.android.pmdlocker.com.pmd_locker.models.objects.HUserRegister;


/**
 * Created by Ca. Phan Thanh on 3/28/2017.
 */

public class Utility {


    public static boolean checkIsLogin()
    {
//        if(GlobalVariable.hUser!=null && !TextUtils.isEmpty(GlobalVariable.hUser.getToken()))
//        {
//            return true;
//        }
//        else
            return false;
    }
    public static boolean isAppInstalled(Context context,String packageName)
    {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return pm.getApplicationInfo(packageName, 0).enabled;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean openAppInstalled(Context context,String packageName)
    {
        try {
            Intent i = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(i);
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return false;
        }
    }
    public static boolean uninstallApp(Context context,String packageName)
    {
        try {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + packageName));
            context.startActivity(intent);
            return true;
        }catch (Exception e)
        {
            return false;
        }
    }
    private static Dialog dialogMessage;
    public static void closeDialogMessage()
    {
        if(dialogMessage!=null && dialogMessage.isShowing())
        {
            dialogMessage.dismiss();
        }
    }
    public static void showDialogMessage(final Activity activity,final String message,final String nameButton)
    {
        showDialogMessage(activity,true,null,message,null,nameButton,null);
    }
    public static void showDialogMessage(final Activity activity,final String message,final String nameButton,ICallbackAskDialog icallback)
    {
        showDialogMessage(activity,true,null,message,null,nameButton,icallback);
    }
    public static boolean isTextEmptyOrNull(String text)
    {
        if(TextUtils.isEmpty(text)|| text.equalsIgnoreCase("null"))
            return true;
        return false;
    }
    private static ProgressDialog dialogLoading;
    public static void showDialogLoading(Context context)
    {
        closeDialogLoading();
        dialogLoading = ProgressDialog.show(context, "",
                Utility.getText(R.string.text_wait_loading,context), true);
    }
    public static void closeDialogLoading()
    {
        if(dialogLoading!=null && dialogLoading.isShowing())
            dialogLoading.dismiss();
    }
    public static void showDialogMessage(final Activity activity, final boolean isShowTitle, final String title,
                                         final String mess, final String nameButton1, final String nameButton2, final ICallbackAskDialog icallback) {
        closeDialogMessage();
        (activity).runOnUiThread(new Runnable() {
            public void run() {
                dialogMessage = new Dialog(activity, R.style.CustomDialog);
                View root = LayoutInflater.from(activity).inflate(R.layout.dialog_message, null);
                dialogMessage.setContentView(root);
                TextView tvTitle = (TextView) root.findViewById(R.id.textVTitle);
                if (isShowTitle) {
                    tvTitle.setText(Utility.getText(R.string.text_title_message,activity));
                    tvTitle.setVisibility(View.VISIBLE);
                }
                else
                    tvTitle.setVisibility(View.GONE);
                if(!TextUtils.isEmpty(title))
                    tvTitle.setText(Html.fromHtml(title));


                TextView tvMessage = (TextView) root.findViewById(R.id.textVMessage);
                tvMessage.setText(Html.fromHtml(mess));
                TextView btn = (TextView) root.findViewById(R.id.btnOk);

                btn.setText(Html.fromHtml(nameButton2));
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(icallback!=null)
                            icallback.onChooseYes();
                        closeDialogMessage();
                    }
                });
                if(nameButton1==null) {
                    View v = (View) root.findViewById(R.id.vHCenter);
                    v.setVisibility(View.GONE);
                    btn = (TextView) root.findViewById(R.id.btnCancel);
                    btn.setVisibility(View.GONE);
                }
                else
                {
                    btn = (TextView) root.findViewById(R.id.btnCancel);
                    btn.setText(Html.fromHtml(nameButton1));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(icallback!=null)
                                icallback.onChooseNo();
                            closeDialogMessage();
                        }
                    });
                }
                dialogMessage.setCancelable(true);
                dialogMessage.show();
            }
        });

    }
    static Dialog dialogButton;
    public static void closeDialogMessageButton()
    {
        if(dialogButton!=null && dialogButton.isShowing())
            dialogButton.dismiss();
    }
    public static void showDialogMessageButton(final Activity activity, final boolean isShowTitle, final String title,
                                         final String mess, final String nameButton1, final String nameButton2, final String nameButton3,final ICallbackChooseButton icallback) {
        closeDialogMessageButton();
        (activity).runOnUiThread(new Runnable() {
            public void run() {
                dialogButton = new Dialog(activity, R.style.CustomDialog);
                View root = LayoutInflater.from(activity).inflate(R.layout.dialog_message_button, null);
                dialogButton.setContentView(root);
                TextView tvTitle = (TextView) root.findViewById(R.id.textVTitle);
                if (isShowTitle) {
                    tvTitle.setText(Utility.getText(R.string.text_title_message,activity));
                    tvTitle.setVisibility(View.VISIBLE);
                }
                else
                    tvTitle.setVisibility(View.GONE);
                if(!TextUtils.isEmpty(title))
                    tvTitle.setText(Html.fromHtml(title));


                TextView tvMessage = (TextView) root.findViewById(R.id.textVMessage);
                tvMessage.setText(Html.fromHtml(mess));
                TextView btn = (TextView) root.findViewById(R.id.btn3);

                btn.setText(Html.fromHtml(nameButton3));
                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(icallback!=null)
                            icallback.onButton3();
                        closeDialogMessageButton();
                    }
                });
                if(nameButton2==null) {
                    btn = (TextView) root.findViewById(R.id.btn2);
                    btn.setVisibility(View.GONE);
                }
                else
                {
                    btn = (TextView) root.findViewById(R.id.btn2);
                    btn.setText(Html.fromHtml(nameButton2));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(icallback!=null)
                                icallback.onButton2();
                            closeDialogMessageButton();
                        }
                    });
                }
                if(nameButton1==null) {
                    btn = (TextView) root.findViewById(R.id.btn1);
                    btn.setVisibility(View.GONE);
                }
                else
                {
                    btn = (TextView) root.findViewById(R.id.btn1);
                    btn.setText(Html.fromHtml(nameButton1));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(icallback!=null)
                                icallback.onButton1();
                            closeDialogMessageButton();
                        }
                    });
                }
                dialogButton.setCancelable(true);
                dialogButton.show();
            }
        });

    }
    public static void saveUserInfo(HUserRegister hUser, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_AUTHEN_TOKEN,hUser.getAuthen_token(),prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_EMAIL,hUser.getEmail(),prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_FB_ACCESS_TOKEN,hUser.getFb_access_token(),prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_FB_ID,hUser.getFb_id(),prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_FIRST_NAME,hUser.getFirstName(),prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_LAST_NAME,hUser.getLastName(),prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_NAME,hUser.getUserName(),prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_PHONE,hUser.getPhone(),prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_STATUS,hUser.getStatus(),prefs);

        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_USAGE_LOCKER,GlobalVariable.userLogin.getUsageLocker(),prefs);
        if(GlobalVariable.userLogin.getUsageDuration()!=null)
            Utility.saveKeyInt(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_USAGE_DURATION,GlobalVariable.userLogin.getUsageDuration(),prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_USAGE_LOKCER_LOCATION,GlobalVariable.userLogin.getPreferredLockerLocation(),prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_USAGE_LOKCER_LOCATION_NAME,GlobalVariable.userLogin.getPreferredLockerLocationName(),prefs);
        Utility.saveKeyInt(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_USAGE_WALLET,GlobalVariable.userLogin.getWalletAmount(),prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_PASSWORD,GlobalVariable.userLogin.getPassword(),prefs);
        GlobalVariable.ACCESSTOKEN = hUser.getAuthen_token();
    }
    public static HUserRegister getUserInfo(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String authen_token = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_AUTHEN_TOKEN,"",prefs);
        String email = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_EMAIL,"",prefs);
        String fb_access_token = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_FB_ACCESS_TOKEN,"",prefs);
        String fb_id = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_FB_ID,"",prefs);
        String firstName = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_FIRST_NAME,"",prefs);
        String lastName = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_LAST_NAME,"",prefs);
        String usrName = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_NAME,"",prefs);
        String phone = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_PHONE,"",prefs);
        String status = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_STATUS,"",prefs);
        String usageLocker = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_USAGE_LOCKER,"",prefs);
        Integer usageDuration = Utility.getKeyInt(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_USAGE_DURATION,0,prefs);
        String usageLockerLocation = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_USAGE_LOKCER_LOCATION,"",prefs);
        String usageLockerLocationName = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_USAGE_LOKCER_LOCATION_NAME,"",prefs);
        Integer wallet = Utility.getKeyInt(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_USAGE_WALLET,0,prefs);
        String password = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_PASSWORD,"",prefs);
        GlobalVariable.ACCESSTOKEN = authen_token;
        HUserRegister hUserRegister =  new HUserRegister(firstName,lastName,usrName,email,phone,authen_token,fb_id,fb_access_token,status,usageLocker,usageDuration,usageLockerLocation,usageLockerLocationName,wallet);
        hUserRegister.setPassword(password);
        return  hUserRegister;
    }
    public static void clearUser(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_STATUS,"",prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_PHONE,"",prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_NAME,"0",prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_LAST_NAME,"",prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_AUTHEN_TOKEN,"",prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_EMAIL,"'",prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_FIRST_NAME,"'",prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_FB_ACCESS_TOKEN,"'",prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_FB_ID,"'",prefs);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKeyUser.KEY_USER_PASSWORD,"",prefs);
        GlobalVariable.ACCESSTOKEN = "";
        GlobalVariable.userLogin = getUserInfo(context);
    }

    public static boolean isLogin(HUserRegister user)
    {
        if(user == null||TextUtils.isEmpty(user.getAuthen_token()) || TextUtils.isEmpty(user.getUserName()))
        {
            return false;
        }
        return true;
    }
    public static int getLanguage(Context activity)
    {
        Resources res = activity.getResources();
        Configuration c = res.getConfiguration();
        String lang ;//= Locale.getDefault().getLanguage();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        lang = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKey.KEY_LANGUAGE,null,prefs);
        if(lang==null)
            lang = Locale.getDefault().getLanguage();
        int index = -1;
        for(int i=0;i<ConstantGlobalVariable.ARRAY_SUPPORT_LANGUAGE_NAME.length;i++)
        {
            if(lang.equalsIgnoreCase(ConstantGlobalVariable.ARRAY_SUPPORT_LANGUAGE[i]))
            {
                index = i;
                break;
            }
        }
        if(index==-1)
        {
            lang = ConstantGlobalVariable.DEFAULT_LANGUAGE;
            for(int i=0;i<ConstantGlobalVariable.ARRAY_SUPPORT_LANGUAGE_NAME.length;i++)
            {
                if(lang.equalsIgnoreCase(ConstantGlobalVariable.ARRAY_SUPPORT_LANGUAGE[i]))
                {
                    index = i;
                    break;
                }
            }
        }
        return index;
//        if(Locale.getDefault().getLanguage().contentEquals("en"))
//        {
//            if(c.locale.getDisplayName().startsWith("Vietnam"))
//            {
//                return 0;
//            }
//            if(c.locale.getDisplayName().startsWith("English"))
//            {
//                return 1;
//            }
//        }else{
//            if(c.locale.getDisplayName().startsWith("Tiáº¿ng Viá»‡t"))
//            {
//                return 0;
//            }
//            if(c.locale.getDisplayName().startsWith("Tiáº¿ng Anh"))
//            {
//                return 1;
//            }
//        }
//
//        return 0;

    }

    public static String getTextLanguage(Context activity)
    {
        if(activity==null)
            return ConstantGlobalVariable.DEFAULT_LANGUAGE;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
//        int index = prefs.getInt(Common.KEY_LANGUAGE, Common.INDEX_LANGUAGE_DEFAULT);
//        switch (index) {
//            case 0: // vietnamese
//                return "vi";
//            case 1: // english
//                //c.locale = Locale.ENGLISH;
//                return "en";
//        }
//        return "vi";
        String lang = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKey.KEY_LANGUAGE,null,prefs);
        if(lang==null)
        {
            Resources res = activity.getResources();
            Configuration c = res.getConfiguration();
            String _lang = Locale.getDefault().getLanguage();
            for(int i=0;i<ConstantGlobalVariable.ARRAY_SUPPORT_LANGUAGE.length;i++)
                if(_lang.equalsIgnoreCase(ConstantGlobalVariable.ARRAY_SUPPORT_LANGUAGE[i]))
                    return _lang;
                return ConstantGlobalVariable.DEFAULT_LANGUAGE;
        }
        else
            return lang;
    }
    //SET LANGUAGE
//    public static boolean changeLanguage(Context activity,int index)
//    {
//        Resources res = activity.getResources();
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
//        String lang = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKey.KEY_LANGUAGE,"",prefs);
//        String curLang = ConstantGlobalVariable.ARRAY_SUPPORT_LANGUAGE[index];
//        Configuration c = res.getConfiguration();
//        c.locale =new Locale(curLang);
//        res.updateConfiguration(c, res.getDisplayMetrics());
//        if(curLang.equalsIgnoreCase(lang)==false)
//        {
//            return true;
//        }
//        return false;
//
//
//    }
    public static boolean changeLanguage(Context activity,String curLang)
    {
        Resources res = activity.getResources();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String lang = Utility.getKeyString(ConstantGlobalVariable.SharedPreferencesKey.KEY_LANGUAGE,"",prefs);
        Configuration c = res.getConfiguration();
        c.locale =new Locale(curLang);
        res.updateConfiguration(c, res.getDisplayMetrics());
        if(curLang.equalsIgnoreCase(lang)==false)
        {
            return true;
        }
        return false;


    }
    public static void switchLangauge(Activity activity,String curLang) {
        Resources res = activity.getResources();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Utility.saveKeyString(ConstantGlobalVariable.SharedPreferencesKey.KEY_LANGUAGE,curLang,prefs);
        activity.setResult(Activity.RESULT_OK);
        activity.finishActivity(ConstantGlobalVariable.REQUEST_ACTIVITY_SETTINGS);
    }

    public static void forceShowKeyboard(Context context, EditText edText)
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void forceHideKeyboard(Context context,EditText edText)
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edText.getWindowToken(), 0);
    }
    public static String getText(int idString,Context context)
    {
        if(GlobalVariable.Language.equalsIgnoreCase(ConstantGlobalVariable.LANGUAGE_VI))
            return LangDataManager.getLangDataManagerInstant(context).getText(idString, LangDataManager.COLUMN_Lang_TextVI);
        if(GlobalVariable.Language.equalsIgnoreCase(ConstantGlobalVariable.LANGUAGE_EN))
            return LangDataManager.getLangDataManagerInstant(context).getText(idString,LangDataManager.COLUMN_Lang_TextEN);

        return LangDataManager.getLangDataManagerInstant(context).getText(idString,LangDataManager.COLUMN_Lang_TextVI);
    }
    public static Spanned getTextHtml(int idString, Context context)
    {
        String text = "";
        String txtDefault = "";
        if(GlobalVariable.Language.equalsIgnoreCase(ConstantGlobalVariable.LANGUAGE_VI)) {
            text = LangDataManager.getLangDataManagerInstant(context).getText(idString, LangDataManager.COLUMN_Lang_TextVI);
            if(!Utility.isTextEmptyOrNull(text))
            {
                return Html.fromHtml(text);
            }
            else
                return new SpannableString(txtDefault);
        }
        if(GlobalVariable.Language.equalsIgnoreCase(ConstantGlobalVariable.LANGUAGE_EN)) {
            text = LangDataManager.getLangDataManagerInstant(context).getText(idString, LangDataManager.COLUMN_Lang_TextEN);
            if(!Utility.isTextEmptyOrNull(text))
            {
                return Html.fromHtml(text);
            }
            else
                return new SpannableString(txtDefault);
        }

        text = LangDataManager.getLangDataManagerInstant(context).getText(idString,LangDataManager.COLUMN_Lang_TextVI);
        if(!Utility.isTextEmptyOrNull(text))
        {
            return Html.fromHtml(text);
        }
        else
            return new SpannableString(txtDefault);
    }
    public static String getKeyString(String key, String defValue, SharedPreferences shared) {
        String result = shared.getString(key, defValue);
        return result;
    }

    public static void saveKeyString(String key, String value, SharedPreferences shared) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(key, value);
        editor.apply();
        editor.commit();

    }
    public static int getKeyInt(String key, int defValue, SharedPreferences shared) {
        return shared.getInt(key, defValue);
    }
    public static void saveKeyInt(String key, int value, SharedPreferences shared) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(key, value);
        editor.apply();
        editor.commit();
    }

    public static void checkAndCreateDirectory(String dirName, File rootDir) {
        File new_dir = new File(rootDir + dirName);
        if (!new_dir.exists()) {
            new_dir.mkdirs();
        }
    }
    public static String unescapeJava(String escaped) {
        if(escaped.indexOf("\\u")==-1)
            return escaped;

        String processed="";

        int position=escaped.indexOf("\\u");
        while(position!=-1) {
            if(position!=0)
                processed+=escaped.substring(0,position);
            String token=escaped.substring(position+2,position+6);
            escaped=escaped.substring(position+6);
            processed+=(char)Integer.parseInt(token,16);
            position=escaped.indexOf("\\u");
        }
        processed+=escaped;


        return processed;
    }

    public static class AccentRemover {

        private static char[] SPECIAL_CHARACTERS = { ' ', '!', '"', '#', '$', '%',
                '*', '+', ',', ':', '<', '=', '>', '?', '@', '[', '\\', ']', '^',
                '`', '|', '~', 'À', 'Á', 'Â', 'Ã', 'È', 'É', 'Ê', 'Ì', 'Í', 'Ò',
                'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â', 'ã', 'è', 'é', 'ê',
                'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý', 'Ă', 'ă', 'Đ', 'đ',
                'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ', 'ạ', 'Ả', 'ả', 'Ấ',
                'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ', 'Ắ', 'ắ', 'Ằ', 'ằ',
                'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ', 'ẻ', 'Ẽ', 'ẽ', 'Ế',
                'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ', 'Ỉ', 'ỉ', 'Ị', 'ị',
                'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ', 'ổ', 'Ỗ', 'ỗ', 'Ộ',
                'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ', 'Ợ', 'ợ', 'Ụ', 'ụ',
                'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ', 'ữ', 'Ự', 'ự', };

        private static char[] REPLACEMENTS = { '_', '\0', '\0', '\0', '\0', '\0',
                '\0', '_', '\0', '_', '\0', '\0', '\0', '\0', '\0', '\0', '_',
                '\0', '\0', '\0', '\0', '\0', 'A', 'A', 'A', 'A', 'E', 'E', 'E',
                'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a', 'a', 'a',
                'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u', 'y', 'A',
                'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u', 'A', 'a',
                'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
                'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e', 'E', 'e',
                'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'I',
                'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
                'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
                'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
                'U', 'u', };

        public static String toUrlFriendly(String s) {
            int maxLength = Math.min(s.length(), 236);
            char[] buffer = new char[maxLength];
            int n = 0;
            for (int i = 0; i < maxLength; i++) {
                char ch = s.charAt(i);
                buffer[n] = removeAccent(ch);
                // skip not printable characters
                if (buffer[n] > 31) {
                    n++;
                }
            }
            // skip trailing slashes
            while (n > 0 && buffer[n - 1] == '/') {
                n--;
            }
            return String.valueOf(buffer, 0, n);
        }

        public static char removeAccent(char ch) {
            int index = Arrays.binarySearch(SPECIAL_CHARACTERS, ch);
            if (index >= 0) {
                ch = REPLACEMENTS[index];
            }
            return ch;
        }

        public static String removeAccent(String s) {
            StringBuilder sb = new StringBuilder(s);
            for (int i = 0; i < sb.length(); i++) {
                sb.setCharAt(i, removeAccent(sb.charAt(i)));
            }
            return sb.toString();
        }

    }

    public static String getHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                return keyHash;
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        return null;
    }
    public static InputStream loadDataFromAssets(String name, Context context) {
        InputStream stream = null;
        try {
            stream = context.getAssets().open(name);
        } catch (Exception e) {

        }
        return stream;
    }

    public static String formatDate(Date date)
    {
        if(date==null)
            return Utility.getText(R.string.text_not_update,null);
        return date.getDay()+"/"+date.getMonth()+"/"+date.getYear();
    }
    public static Bitmap getBitmap(Context context,int drawableRes) {
        Drawable drawable = context.getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public static String getStringFromAssets(String name, Context context) {
        String str = "";
        try {
            InputStream input = loadDataFromAssets(name, context);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    str += line;
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {

        }
        return str;
    }

}
