package com.example.smartalarm;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class AlarmContentFragment extends Fragment {

    static ContentAdapter contentAdapter;

    public static final String SAVED_LIST="alarmList.txt";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView=(RecyclerView)inflater.inflate(R.layout.recycler_view,container,false);
        contentAdapter=new ContentAdapter();
        recyclerView.setAdapter(contentAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView time;
        TextView settingsTime;

        EditText alarmEditText;

        Button timeButton;
        Button settingsBtn;
        Button applySettingsBtn;
        Button deleteAlarmBtn;

        CheckBox smartBtn;
        CheckBox everyDayBtn;
        SwitchCompat switchBtn;

        TimePicker timePicker;

        Dialog settingsDialog;
        Dialog timeDialog;

        RelativeLayout alarmLayout;

        public ViewHolder(View view) {
            super(view);
            final Context context = view.getContext();

            timeDialog = new Dialog(context);
            timeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            settingsDialog = new Dialog(context);
            settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            timeDialog.setContentView(R.layout.time_picker);
            settingsDialog.setContentView(R.layout.settings_dialog);

            name = (TextView)itemView.findViewById(R.id.name);
            time = (TextView)itemView.findViewById(R.id.time);
            settingsTime = (TextView)settingsDialog.findViewById(R.id.settingsTime);
            alarmEditText = (EditText)settingsDialog.findViewById(R.id.editAlarmName);

            timePicker = (TimePicker)timeDialog.findViewById(R.id.timePicker);
            timePicker.setIs24HourView(true);

            smartBtn = (CheckBox)settingsDialog.findViewById(R.id.smartBtn);
            everyDayBtn = (CheckBox)settingsDialog.findViewById(R.id.everyDay);
            switchBtn = (SwitchCompat) itemView.findViewById(R.id.alarmSwitch);

            timeButton = (Button)timeDialog.findViewById(R.id.applyTimeButton);
            settingsBtn = (Button)itemView.findViewById(R.id.alarmSettingsBtn);
            deleteAlarmBtn = (Button)settingsDialog.findViewById(R.id.deleteAlarmBtn);
            applySettingsBtn = (Button)settingsDialog.findViewById(R.id.applySettingsBtn);

            alarmLayout = (RelativeLayout)itemView.findViewById(R.id.alarmLayout);
        }
    }

    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {

        private static final String TAG = "AlarmContentFragment";

        protected static ArrayList<MyAlarmManager> alarms = new ArrayList<>();

        View view;

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder,final int position) {

            if(alarms.get(position).getAlarmId()== - 1) {
                alarms.get(position).setAlarmId(position);
                holder.settingsDialog.show();
            }

            if (alarms.get(position).getTriggerHour() > 19 || alarms.get(position).getTriggerHour() < 5) {
                holder.alarmLayout.setBackgroundResource(R.drawable.alarm_dark_background);
            }
            else {
                holder.alarmLayout.setBackgroundResource(R.drawable.alarm_background);
            }

            holder.switchBtn.setChecked(alarms.get(position).isChecked());
            holder.smartBtn.setChecked(alarms.get(position).isSmart());
            holder.everyDayBtn.setChecked(alarms.get(position).isEveryDay());

            holder.name.setText(alarms.get(position).getName());
            holder.time.setText(alarms.get(position).getTime());
            holder.settingsTime.setText(alarms.get(position).getTime());

            holder.time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.timeDialog.show();
                }
            });

            holder.timeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (alarms.get(position).getTriggerHour() != -1 && alarms.get(position).getTriggerMinute() != -1 && alarms.get(position).isChecked()) alarms.get(position).cancelAlarm(view.getContext());
                    if (holder.smartBtn.isChecked()) {
                        alarms.get(position).setSmartAlarm(view.getContext(), holder.timePicker.getCurrentHour(), holder.timePicker.getCurrentMinute());
                        if (holder.timePicker.getCurrentMinute() < 10) {
                            holder.time.setText(holder.timePicker.getCurrentHour() + ":0" + holder.timePicker.getCurrentMinute());
                            holder.settingsTime.setText(holder.timePicker.getCurrentHour() + ":0" + holder.timePicker.getCurrentMinute());
                        }
                        else {
                            holder.time.setText(holder.timePicker.getCurrentHour() + ":" + holder.timePicker.getCurrentMinute());
                            holder.settingsTime.setText(holder.timePicker.getCurrentHour() + ":" + holder.timePicker.getCurrentMinute());
                        }
                    }
                    else {
                        if (holder.everyDayBtn.isChecked()) {
                            alarms.get(position).setRepareAlarm(view.getContext(), holder.timePicker.getCurrentHour(), holder.timePicker.getCurrentMinute());
                            if (holder.timePicker.getCurrentMinute() < 10) {
                                holder.time.setText(holder.timePicker.getCurrentHour() + ":0" + holder.timePicker.getCurrentMinute());
                                holder.settingsTime.setText(holder.timePicker.getCurrentHour() + ":0" + holder.timePicker.getCurrentMinute());
                            }
                            else {
                                holder.time.setText(holder.timePicker.getCurrentHour() + ":" + holder.timePicker.getCurrentMinute());
                                holder.settingsTime.setText(holder.timePicker.getCurrentHour() + ":" + holder.timePicker.getCurrentMinute());
                            }
                        } else {
                            alarms.get(position).setAlarm(view.getContext(), holder.timePicker.getCurrentHour(), holder.timePicker.getCurrentMinute());
                            if (holder.timePicker.getCurrentMinute() < 10) {
                                holder.time.setText(holder.timePicker.getCurrentHour() + ":0" + holder.timePicker.getCurrentMinute());
                                holder.settingsTime.setText(holder.timePicker.getCurrentHour() + ":0" + holder.timePicker.getCurrentMinute());
                            } else {
                                holder.time.setText(holder.timePicker.getCurrentHour() + ":" + holder.timePicker.getCurrentMinute());
                                holder.settingsTime.setText(holder.timePicker.getCurrentHour() + ":" + holder.timePicker.getCurrentMinute());
                            }
                        }
                    }
                    if (alarms.get(position).getTriggerHour() == -1 ||  alarms.get(position).getTriggerHour() > 19 || alarms.get(position).getTriggerHour() < 7) {
                        holder.alarmLayout.setBackgroundResource(R.drawable.alarm_dark_background);
                    }
                    else {
                        holder.alarmLayout.setBackgroundResource(R.drawable.alarm_background);
                    }
                    holder.switchBtn.setChecked(true);
                    holder.timeDialog.dismiss();
                }
            });

            holder.smartBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) holder.everyDayBtn.setChecked(false);
                }
            });

            holder.everyDayBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) holder.smartBtn.setChecked(false);
                }
            });

            holder.settingsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.settingsDialog.show();
                }
            });

            holder.settingsTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.timeDialog.show();
                }
            });
            holder.settingsDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (alarms.get(position).getTriggerHour() == -1)
                        alarms.remove(position);
                    notifyDataSetChanged();
                }
            });
            holder.switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (alarms.get(position).getTriggerHour() != -1 && alarms.get(position).getTriggerMinute() != -1) {
                        if (isChecked && !holder.smartBtn.isChecked() && !holder.everyDayBtn.isChecked()) {
                            alarms.get(position).setAlarm(view.getContext(), alarms.get(position).getTriggerHour(), alarms.get(position).getTriggerMinute());
                        } else if (isChecked && holder.smartBtn.isChecked()) {
                            alarms.get(position).setSmartAlarm(view.getContext(), alarms.get(position).getTriggerHour(), alarms.get(position).getTriggerMinute());
                        } else if (isChecked && holder.everyDayBtn.isChecked()) {
                            alarms.get(position).setRepareAlarm(view.getContext(), alarms.get(position).getTriggerHour(), alarms.get(position).getTriggerMinute());
                        } else alarms.get(position).cancelAlarm(view.getContext());
                    }
                    else if (alarms.get(position).getTriggerHour() == -1 && alarms.get(position).getTriggerMinute() == -1 && isChecked) {
                        holder.switchBtn.setChecked(false);
                        Toast.makeText(view.getContext(), "Пожалуйста, выберите время срабатывания", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.applySettingsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.alarmEditText!=null) {
                        holder.name.setText(String.valueOf(holder.alarmEditText.getText()));
                        alarms.get(position).setName(String.valueOf(holder.alarmEditText.getText()));
                    }
                    if (alarms.get(position).getTriggerHour() == -1) {
                        //Toast.makeText(view.getContext(), String.valueOf(alarms.get(position).getTriggerHour()) , Toast.LENGTH_SHORT).show();
                        alarms.remove(alarms.get(position));
                        notifyDataSetChanged();
                    }
                    holder.settingsDialog.dismiss();
                }
            });

            holder.deleteAlarmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (alarms.get(position).getTriggerHour() != -1 && alarms.get(position).getTriggerMinute() != -1) {
                        alarms.get(position).cancelAlarm(view.getContext());
                    }
                    alarms.remove(alarms.get(position));
                    notifyDataSetChanged();
                    holder.settingsDialog.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return alarms.size();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveToFile(ContentAdapter.alarms, getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (loadFromFile(getContext())!=null) {
            ContentAdapter.alarms = loadFromFile(getContext());
            contentAdapter.notifyDataSetChanged();
        }
    }

    static void saveToFile(ArrayList<MyAlarmManager> alarms, Context context){
        try {
            Log.d("FILE_DIR", context.getFilesDir().toString());
            FileOutputStream fos = new FileOutputStream(context.getFilesDir().toString() + "/" + SAVED_LIST);
            ObjectOutputStream oos= new ObjectOutputStream(fos);
            oos.writeObject(alarms);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static ArrayList<MyAlarmManager> loadFromFile(Context context){
        try {
            FileInputStream fis = new FileInputStream(context.getFilesDir().toString() + "/" + SAVED_LIST);
            ObjectInputStream ois= new ObjectInputStream(fis);
            ArrayList<MyAlarmManager> alarms=(ArrayList<MyAlarmManager>)ois.readObject();
            ois.close();
            return alarms;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
