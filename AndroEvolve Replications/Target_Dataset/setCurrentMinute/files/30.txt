package com.huiwei.roomreservation.activity;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import com.baidu.platform.comapi.map.v;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservation.view.RoomInfoView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.MemberInfo;
import com.huiwei.roomreservationlib.info.ReservationInfo;
import com.huiwei.roomreservationlib.info.StoreDetailInfo;
import com.huiwei.roomreservationlib.info.TimeSlotInfo;
import com.huiwei.roomreservationlib.task.order.OrderModifyTask;
import com.huiwei.roomreservationlib.task.order.ReservationDetailTask;
import com.huiwei.roomreservationlib.task.pay.ServiceReservationTask;

public class ServiceReservationActivity extends Activity implements
		OnClickListener {
	public static final int RESERVATION_ROOM = 0;
	public static final int RESERVATION_ORDER = 1;
	public static final int RESERVATION_SUB_ORDER = 2;

	private EditText dateTime, otherInfo, contact, phoneNum;
	private LoadingView loadingView;
	private int reserationVia = RESERVATION_ROOM;
	private String subOrderId = "";
	private ReservationInfo reservationinfo = new ReservationInfo();
	private Button btnFemale, btnMale;
	private int sex;
	private int lastMinute = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_reservation);

		reserationVia = getIntent().getIntExtra("reservation_via",
				RESERVATION_ROOM);
		if (reserationVia == RESERVATION_SUB_ORDER)
			subOrderId = getIntent().getStringExtra("sub_order_id");

		loadingView = (LoadingView) findViewById(R.id.loading);
		dateTime = (EditText) findViewById(R.id.et_date);
		otherInfo = (EditText) findViewById(R.id.et_other);
		contact = (EditText) findViewById(R.id.et_name);
		phoneNum = (EditText) findViewById(R.id.et_phone);
		((ImageView) findViewById(R.id.iv_return)).setOnClickListener(this);
		((ImageView) findViewById(R.id.iv_contacts)).setOnClickListener(this);
		((ImageView) findViewById(R.id.iv_cal)).setOnClickListener(this);
		((Button) findViewById(R.id.btn_reservation)).setOnClickListener(this);

		setDefaultInfo();
		if (reserationVia == RESERVATION_ROOM) {
			((TextView) findViewById(R.id.tv_tips)).setText(Html
					.fromHtml(Data.storeDetailInfo.storeTips));
			((RoomInfoView) findViewById(R.id.room_info_view)).setInfo(
					Data.storeDetailInfo.name, "", "服务项目",
					Data.serviceInfo.name, Data.serviceInfo.price, Data.serviceInfo.priceType,
					Data.storeDetailInfo.iconUrl);
		} else {
			if (reserationVia == RESERVATION_SUB_ORDER) {
				reservationinfo.subOrderID = subOrderId;
			}

			getReservationInfo();
		}
	}

	private void setDefaultInfo() {
		contact.setText(Data.memberInfo.realName == null ? ""
				: Data.memberInfo.realName);
		phoneNum.setText(Data.memberInfo.phoneNum);
		btnFemale = (Button) findViewById(R.id.btn_female);
		btnFemale.setOnClickListener(this);
		btnMale = (Button) findViewById(R.id.btn_male);
		btnMale.setOnClickListener(this);
		sex = Data.memberInfo.sex;
		if (sex == MemberInfo.FAMALE) {
			btnMale.setBackgroundResource(R.drawable.type_unselected);
			btnFemale.setBackgroundResource(R.drawable.type_single_selected);
		} else if (sex == MemberInfo.MALE) {
			btnMale.setBackgroundResource(R.drawable.type_single_selected);
			btnFemale.setBackgroundResource(R.drawable.type_unselected);
		}
	}

	private void getReservationInfo() {
		loadingView.setVisibility(View.VISIBLE);
		ReservationDetailTask task = new ReservationDetailTask(this,
				resInfoHandler, subOrderId, reservationinfo);
		task.execute();
	}

	private Handler resInfoHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				reservationinfo = (ReservationInfo) msg.obj;
				((TextView) findViewById(R.id.tv_tips)).setText(Html
						.fromHtml(reservationinfo.storeTips));
				((RoomInfoView) findViewById(R.id.room_info_view)).setInfo(
						reservationinfo.storeName, reservationinfo.storePhone,
						reservationinfo.roomName, reservationinfo.roomInfo,
						reservationinfo.price, reservationinfo.priceType,
						reservationinfo.storeIconUrl);
				setOrderInfo();
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(ServiceReservationActivity.this,
						(String) msg.obj, Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(ServiceReservationActivity.this,
						getResources().getString(R.string.load_data_fail),
						Toast.LENGTH_SHORT).show();
			}

			loadingView.setVisibility(View.GONE);
		};
	};

	private void setOrderInfo() {
		contact.setText(reservationinfo.contacts);
		phoneNum.setText(reservationinfo.phoneNum);
		otherInfo.setText(reservationinfo.otherInfo);
		if (reserationVia == RESERVATION_SUB_ORDER) {
			contact.setKeyListener(null);
			phoneNum.setKeyListener(null);
			otherInfo.setKeyListener(null);
			btnMale.setOnClickListener(null);
			btnFemale.setOnClickListener(null);
		}

		dateTime.setText(reservationinfo.time);
		String date[] = reservationinfo.time.split("-");
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_return:
			finish();
			break;

		case R.id.et_date:
		case R.id.iv_cal:
			popDateTimeDlg();
			break;

		case R.id.btn_reservation: {
			if (checkInput()) {
				reservation();
			}
		}
			break;

		case R.id.iv_contacts:
			startActivityForResult(new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI), 0);
			break;

		case R.id.btn_female: {
			if (sex == MemberInfo.FAMALE) {
				btnFemale.setBackgroundResource(R.drawable.type_unselected);
				sex = 0;
			} else {
				btnMale.setBackgroundResource(R.drawable.type_unselected);
				btnFemale
						.setBackgroundResource(R.drawable.type_single_selected);
				sex = MemberInfo.FAMALE;
			}
		}
			break;

		case R.id.btn_male: {
			if (sex == MemberInfo.MALE) {
				btnMale.setBackgroundResource(R.drawable.type_unselected);
				sex = 0;
			} else {
				btnMale.setBackgroundResource(R.drawable.type_single_selected);
				btnFemale.setBackgroundResource(R.drawable.type_unselected);
				sex = MemberInfo.MALE;
			}
		}
			break;

		default:
			break;
		}
	}

	private boolean checkInput() {
		if (contact.getText().toString().length() == 0) {
			Toast.makeText(
					this,
					getResources()
							.getString(R.string.please_input_contact_name),
					Toast.LENGTH_SHORT).show();
			return false;
		}

		if (phoneNum.getText().toString().length() == 0) {
			Toast.makeText(
					this,
					getResources().getString(
							R.string.please_input_contact_number),
					Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	private void reservation() {
		loadingView
				.setLoadingText(getResources().getString(R.string.submiting));
		loadingView.setVisibility(View.VISIBLE);
		
		reservationinfo.otherInfo = otherInfo.getText().toString();
		reservationinfo.contacts = contact.getText().toString();
		reservationinfo.phoneNum = phoneNum.getText().toString();
		reservationinfo.sex = sex;

		if (reserationVia == RESERVATION_SUB_ORDER) {
			OrderModifyTask task = new OrderModifyTask(this,
					orderModifyHandler, reservationinfo);
			task.execute();
		} else {
			ServiceReservationTask task = new ServiceReservationTask(this,
					reservationHandler, reservationinfo);
			task.execute();
		}
	}

	private Handler reservationHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				List<String> infoList = (List<String>) msg.obj;
				Intent intent = new Intent();
				intent.putExtra("order_id", infoList.get(0));
				intent.putExtra("order_sn", infoList.get(1));
				intent.putExtra("store_name", Data.storeDetailInfo.name);
				intent.putExtra("price_type", Data.serviceInfo.priceType);
				intent.putExtra("price", infoList.get(2));
				intent.putExtra("is_from_order", false);
				intent.putExtra("order_type", StoreDetailInfo.TYPE_SERVICE);
				intent.setClass(ServiceReservationActivity.this,
						PaymentChoseActivity.class);
				startActivity(intent);
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(ServiceReservationActivity.this,
						(String) msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ServiceReservationActivity.this,
						getResources().getString(R.string.upload_info_fail),
						Toast.LENGTH_SHORT).show();
			}

			loadingView.setVisibility(View.GONE);
		};
	};

	private Handler orderModifyHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				Toast.makeText(
						ServiceReservationActivity.this,
						getResources().getString(
								R.string.sub_order_reorder_success),
						Toast.LENGTH_SHORT).show();
				finish();
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(ServiceReservationActivity.this,
						(String) msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(
						ServiceReservationActivity.this,
						getResources().getString(
								R.string.sub_order_reorder_fail),
						Toast.LENGTH_SHORT).show();
			}

			loadingView.setVisibility(View.GONE);
		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {

			Uri contactData = data.getData();

			Cursor cursor = managedQuery(contactData, null, null, null, null);
			cursor.moveToFirst();
			String username = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			String contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			int iPhoneCnt = cursor
					.getInt(cursor
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

			Cursor phone = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+ contactId, null, null);
			phone.moveToFirst();
			String usernumber = phone
					.getString(phone
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			contact.setText(username);
			phoneNum.setText(usernumber.replace("-", ""));
		}
	}

	private void popDateTimeDlg() {
		View view = getLayoutInflater().inflate(R.layout.date_time_set_view,null);
		final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
		final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);
		initTime(datePicker, timePicker);
		new AlertDialog.Builder(this).
				setView(view).
				setCancelable(false).
		setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String text = String.format("%d-%02d-%02d %02d:%02d", 
								datePicker.getYear(), (datePicker.getMonth()+1), datePicker.getDayOfMonth(),
								timePicker.getCurrentHour(), timePicker.getCurrentMinute());
						dateTime.setText(text);
						reservationinfo.time = text;
					}
				}).
		setNegativeButton(R.string.cancel, null).create().show();
	}
	
	@SuppressLint("NewApi")
	private void initTime(DatePicker datePicker, TimePicker timePicker) {
		Calendar calendar=Calendar.getInstance(TimeZone.getDefault());  
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(onTimeChangeListener);
		lastMinute = timePicker.getCurrentMinute();
	    int hour=calendar.get(Calendar.HOUR_OF_DAY);  
	    int minute=calendar.get(Calendar.MINUTE);  
	    timePicker.setCurrentHour(hour);  
	    timePicker.setCurrentMinute(minute); 
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		datePicker.setMinDate(calendar.getTimeInMillis());
		
		if (reservationinfo.time != null && !reservationinfo.time.equals("")) {
			String dateTime[] = reservationinfo.time.split(" ");
			String date[] = dateTime[0].split("-");
			datePicker.updateDate(Integer.valueOf(date[0]),
					Integer.valueOf(date[1])-1, Integer.valueOf(date[2]));
			String time[] = dateTime[1].split(":");
			timePicker.setCurrentHour(Integer.valueOf(time[0]));
			timePicker.setCurrentMinute(Integer.valueOf(time[1]));
		}
	}

}
