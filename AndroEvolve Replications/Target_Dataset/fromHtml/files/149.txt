package com.hitechno.sogoods.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.hitechno.sogoods.R;
import com.hitechno.sogoods.models.Notification;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotificationsAdapter extends BaseAdapter{
	Context context;
	int resource;
	List<Notification> lstNotifications;
	
	public NotificationsAdapter(Context context, int resource, List<Notification> lstNoti) {
		this.context = context;
		this.resource = resource;
		this.lstNotifications = lstNoti;
	}

	@Override
	public int getCount() {
		Log.e("size nitifi", "size notifi= " + lstNotifications.size());
		return lstNotifications.size();
	}

	@Override
	public Object getItem(int arg0) {
		return lstNotifications.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final LinearLayout itemView;

		if (convertView == null) {
			itemView = new LinearLayout(context);
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li;
			li = (LayoutInflater) context.getSystemService(inflater);
			li.inflate(resource, itemView, true);

			LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			itemView.setLayoutParams(new GridView.LayoutParams(
					linearLayoutParams));
			itemView.setPadding(5, 5, 5, 5);
		} else {
			itemView = (LinearLayout) convertView;
		}
		TextView txtValNotifi = (TextView)itemView.findViewById(R.id.item_noti_name_label);
		TextView textDate = (TextView)itemView.findViewById(R.id.textDate);
		TextView textNoResult  = (TextView)itemView.findViewById(R.id.item_noti_comment_label);

		if(lstNotifications.size()==0){
			textNoResult.setVisibility(View.VISIBLE);
			textNoResult.setText(context.getResources().getString(R.string.noResult));
		}
		else{
			Notification notification = lstNotifications.get(position);	
			String database = notification.date;
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:m:s");
			SimpleDateFormat simpYear = new SimpleDateFormat("yyyy");
			SimpleDateFormat simpMonth = new SimpleDateFormat("MM");
			SimpleDateFormat simpDay = new SimpleDateFormat("dd");
			SimpleDateFormat simpHour = new SimpleDateFormat("h");
			SimpleDateFormat simpMinutes = new SimpleDateFormat("m");
			SimpleDateFormat simpSecond = new SimpleDateFormat("s");
			
			Date date = new Date();
			//		String a = dateFormat.format(date);

			Date dateData;
			int yearData=0, monthData=0, dayData=0, hourData=0, minuteData=0, secondData=0;
			try {
				dateData = dateFormat.parse(database);
				yearData = Integer.parseInt(simpYear.format(dateData));
				monthData = Integer.parseInt(simpMonth.format(dateData));
				dayData = Integer.parseInt(simpDay.format(dateData));
				hourData = Integer.parseInt(simpHour.format(dateData));
				minuteData = Integer.parseInt(simpMinutes.format(dateData));
				secondData = Integer.parseInt(simpSecond.format(dateData));
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			
			int yearNow = Integer.parseInt(simpYear.format(date));
			int monthNow = Integer.parseInt(simpMonth.format(date));
			int dayNow = Integer.parseInt(simpDay.format(date));
			int hourNow = Integer.parseInt(simpHour.format(date));
			int minuteNow = Integer.parseInt(simpMinutes.format(date));
			int secondNow = Integer.parseInt(simpSecond.format(date));

			String yearAfter, monthAfter, dayAter, hourAfter, minuteAfter, secondAfter;
			String lang = Locale.getDefault().getLanguage();
			if(yearNow-yearData==0){
				yearAfter = "";
			}else{
				if(lang=="fr"){
					yearAfter =context.getResources().getString(R.string.elapsed_years)+String.valueOf(yearNow-yearData)+" "+context.getResources().getString(R.string.elapsed_years_af);
				}else {
					yearAfter = String.valueOf(yearNow-yearData)+" "+context.getResources().getString(R.string.elapsed_years);
				}
				
			}
			if(monthNow-monthData==0){
				monthAfter = "";
			}else{
				if(lang=="fr"){
					monthAfter = context.getResources().getString(R.string.elapsed_months)+String.valueOf(monthNow-monthData)+" "+context.getResources().getString(R.string.elapsed_months_af);
				}else{
					monthAfter = String.valueOf(monthNow-monthData)+" "+context.getResources().getString(R.string.elapsed_months);
				}
			}
			if(dayNow-dayData==0){
				dayAter = "";
			}else{
				if(lang=="fr"){
					dayAter = context.getResources().getString(R.string.elapsed_days)+String.valueOf(dayNow-dayData)+" "+context.getResources().getString(R.string.elapsed_days_af);
				}else{
					dayAter = String.valueOf(dayNow-dayData)+" "+context.getResources().getString(R.string.elapsed_days);
				}
			}
			if(hourNow-hourData==0){
				hourAfter = "";
			}else{
				if(lang=="fr"){
					hourAfter = context.getResources().getString(R.string.elapsed_hours)+String.valueOf(hourNow-hourData)+" "+context.getResources().getString(R.string.elapsed_hours_af);
				}else{
					hourAfter = String.valueOf(hourNow-hourData)+" "+context.getResources().getString(R.string.elapsed_hours);
				}
			}
			if(minuteNow-minuteData==0){
				minuteAfter = "";
			}else{
				if(lang=="fr"){
					minuteAfter = context.getResources().getString(R.string.elapsed_minutes)+String.valueOf(minuteNow-minuteData)+" "+context.getResources().getString(R.string.elapsed_minutes_af);
				}else{
					minuteAfter = String.valueOf(minuteNow-minuteData)+" "+context.getResources().getString(R.string.elapsed_minutes);
				}
			}
			if(secondNow-secondData==0){
				secondAfter = "";
			}else{
				if(lang=="fr"){
					secondAfter = context.getResources().getString(R.string.elapsed_seconds)+String.valueOf(secondNow-secondData)+" "+context.getResources().getString(R.string.elapsed_seconds_af);
				}else{
					secondAfter = String.valueOf(secondNow-secondData)+" "+context.getResources().getString(R.string.elapsed_seconds);
				}
			}
			if(yearAfter!=""){
				textDate.setText(yearAfter);
			}else{
				if(monthAfter!=""){
					textDate.setText(monthAfter);
				}else{
					if(dayAter!=""){
						textDate.setText(dayAter);
					}else{
						if(hourAfter!=""){
							textDate.setText(hourAfter);
						}else{
							if(minuteAfter!=""){
								textDate.setText(minuteAfter);
							}else{
								if(secondAfter!=""){
									textDate.setText(secondAfter);
								}
							}
						}
					}
				}
			}
			if(notification.type.equals("following_add_comment")){
				String html = notification.username+" "+context.getResources().getString(R.string.added_new_comment);
				txtValNotifi.setText(Html.fromHtml(html));
			}else if(notification.type.equals("new_follower")){
				String html = notification.username+" "+context.getResources().getString(R.string.followed_you);
				txtValNotifi.setText(Html.fromHtml(html));
			}else if(notification.type.equals("following_add_product")){
				String html = notification.username+" "+context.getResources().getString(R.string.added_new_product);
				txtValNotifi.setText(Html.fromHtml(html));
			}else if(notification.type.equals("new_comment_on_my_product")){
				String html = context.getResources().getString(R.string.there_is)+" "+ notification.nbcomments+" "+ context.getResources().getString(R.string.new_comment_on_your_product);
				txtValNotifi.setText(Html.fromHtml(html));
			}else if(notification.type.equals("following_add_picture")){
				String html = notification.username+" "+context.getResources().getString(R.string.added_new_picture);
				txtValNotifi.setText(Html.fromHtml(html));
			}
		}
		return itemView;
	}

}
