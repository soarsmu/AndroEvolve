package in.grat.esd2;

import in.grat.esd2.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;



import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.widget.RemoteViews;

public class MyWidgetProvider extends AppWidgetProvider {
	static DBHelper dbhelper;
	String lang;
	private static final String LANG_PREFIX_KEY = "lang_widget_";
	private static final String THEME_PREFIX_KEY = "theme_widget_";
	private static final String FONT_SIZE_PREFIX_KEY = "font_size_widget_";

	
	private static DBHelper getDBHelper(Context context) {
		DBHelper dbh = new DBHelper(context); 
        return dbh;	
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
				Intent intent;
		dbhelper = getDBHelper(context);
		Calendar calendar = Calendar.getInstance();
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, MMMM d");
    	String today = dateFormat.format(calendar.getTime());
    	String todayDisplay = displayFormat.format(calendar.getTime());

    	for(int widgetId : appWidgetIds) {
    		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    	    String lang = preferences.getString(LANG_PREFIX_KEY + widgetId, "e");
    	    intent = new Intent(context.getApplicationContext(), ESDailyActivity.class);
    	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	    intent.setData(Uri.withAppendedPath(Uri.parse("esdLang://widget/id/"), String.valueOf(widgetId)));
    	    intent.putExtra("lang", lang);
    	    String theme = preferences.getString(THEME_PREFIX_KEY + widgetId, "e");
    	    int layout = (theme.equalsIgnoreCase("normal")) ? R.layout.widget_layout : R.layout.widget_layout_dark;
    		intent.putExtra("theme", theme);
    	    
    	    int fontSize = preferences.getInt(FONT_SIZE_PREFIX_KEY + widgetId, 14);
	    	
	    	DBHelper.VerseParts vp = dbhelper.getVerseParts(today, "document", lang);
	    	Spanned dateDisplay = Html.fromHtml(vp.date);
	    	todayDisplay = (dateDisplay!=null)? dateDisplay.toString().trim() : todayDisplay;
	    	StringBuilder text = new StringBuilder(Html.fromHtml(vp.verse).toString());
	    	RemoteViews remoteViews = new RemoteViews(context.getPackageName(),layout);
	    	remoteViews.setTextViewText(R.id.tvTodaysDate, todayDisplay);
	    	remoteViews.setTextViewText(R.id.tvTodaysText, text);
	    	remoteViews.setFloat(R.id.tvTodaysDate, "setTextSize", (float) (fontSize +2));
	    	remoteViews.setFloat(R.id.tvTodaysText, "setTextSize", (float) fontSize);
	    	
			remoteViews.setOnClickPendingIntent(R.id.tvTodaysText, PendingIntent.getActivity(context, widgetId, intent, 0));
			
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
    	}

	}
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		for (int widgetId : appWidgetIds) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			preferences.edit().remove(LANG_PREFIX_KEY + widgetId);
			preferences.edit().remove(THEME_PREFIX_KEY + widgetId);
			preferences.edit().commit();
		}
	}
	
	@Override
	public void onDisabled(Context context) { 
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName thisWidget = new ComponentName(context,
				MyWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			preferences.edit().remove(LANG_PREFIX_KEY + widgetId);
			preferences.edit().remove(THEME_PREFIX_KEY + widgetId);
			preferences.edit().commit();
		}
	}
	



	public static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int mAppWidgetId, String langId, String theme) {
		Calendar calendar = Calendar.getInstance();
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, MMMM d");
    	String today = dateFormat.format(calendar.getTime());
    	String todayDisplay = displayFormat.format(calendar.getTime());
    	
    	int layout = (theme.equalsIgnoreCase("normal")) ? R.layout.widget_layout : R.layout.widget_layout_dark;
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layout);
		dbhelper = getDBHelper(context);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		int fontSize = preferences.getInt(FONT_SIZE_PREFIX_KEY + mAppWidgetId, 14);

		DBHelper.VerseParts vp = dbhelper.getVerseParts(today, "document", langId);
		Spanned text = Html.fromHtml(vp.verse);
    	StringBuilder sb = new StringBuilder(text.toString());
    	Spanned dateDisplay = Html.fromHtml(vp.date);
    	todayDisplay = (dateDisplay!=null)? dateDisplay.toString().trim() : todayDisplay;
    	remoteViews.setTextViewText(R.id.tvTodaysText, sb);
    	remoteViews.setTextViewText(R.id.tvTodaysDate, todayDisplay);
    	Intent intent = new Intent(context.getApplicationContext(), ESDailyActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.setData(Uri.withAppendedPath(Uri.parse("esdLang://widget/id/"), String.valueOf(mAppWidgetId)));
	    intent.putExtra("lang", langId);
		intent.putExtra("theme", theme);

	    remoteViews.setFloat(R.id.tvTodaysDate, "setTextSize", (float) (fontSize +2));
    	remoteViews.setFloat(R.id.tvTodaysText, "setTextSize", (float) fontSize);
    	
		remoteViews.setOnClickPendingIntent(R.id.tvTodaysText, PendingIntent.getActivity(context, mAppWidgetId, intent, 0));

        // Tell the widget manager
        appWidgetManager.updateAppWidget(mAppWidgetId, remoteViews);

	}

	
}

