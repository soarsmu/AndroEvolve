package foodcenter.android.activities.coworkers;

import java.lang.reflect.Field;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import foodcenter.android.R;

public class RangeTimePickerFragment extends DialogFragment
{

    private final static String[] DISPLAYED_MINS = { "0", "10", "20", "30", "40", "50" };

    public interface RangeTimePickerListener
    {
        /**
         * called after a reserve click
         * 
         * @param dialog
         */
        public void onReserveClick(RangeTimePickerFragment dialog);

        /**
         * called after a cancel click
         * 
         * @param dialog
         */
        public void onCancelClick(RangeTimePickerFragment dialog);
    }

    
    private RangeTimePickerListener listener;

    private int startHr;
    private int startMin;
    private int endHr;
    private int endMin;

    // Override the Fragment.onAttach() method to instantiate the LoginDialogListener
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try
        {
            // Instantiate the DialogListener so we can send events to the host
            listener = (RangeTimePickerListener) activity;
        }
        catch (ClassCastException e)
        {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.range_time_picker, null);

        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = 0;

        final TimePicker dpStartDate = (TimePicker) view.findViewById(R.id.dpStartDate);
        dpStartDate.setIs24HourView(true);
        dpStartDate.setCurrentHour(hour);
        dpStartDate.setCurrentMinute(minute);
        NumberPicker startMinSpiner = getMinuteSpinner(dpStartDate);
        if (null != startMinSpiner)
        {
            startMinSpiner.setMinValue(0);
            startMinSpiner.setMaxValue(DISPLAYED_MINS.length - 1);
            startMinSpiner.setDisplayedValues(DISPLAYED_MINS);
        }

        final TimePicker dpEndDate = (TimePicker) view.findViewById(R.id.dpEndDate);
        dpEndDate.setIs24HourView(true);
        dpEndDate.setCurrentHour(hour + 1);
        dpEndDate.setCurrentMinute(minute);
        NumberPicker endMinSpiner = getMinuteSpinner(dpEndDate);
        if (null != endMinSpiner)
        {
            endMinSpiner.setMinValue(0);
            endMinSpiner.setMaxValue(DISPLAYED_MINS.length - 1);
            endMinSpiner.setDisplayedValues(DISPLAYED_MINS);
        }

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view); // Set the view of the dialog to your custom layout
        builder.setTitle("Select start and end date");
        builder.setPositiveButton(R.string.reserve_button, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                startHr = dpStartDate.getCurrentHour();
                startMin = Integer.parseInt(DISPLAYED_MINS[dpStartDate.getCurrentMinute()]);

                endHr = dpEndDate.getCurrentHour();
                endMin = Integer.parseInt(DISPLAYED_MINS[dpEndDate.getCurrentMinute()]);

                listener.onReserveClick(RangeTimePickerFragment.this);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                listener.onCancelClick(RangeTimePickerFragment.this);
            }
        });

        Dialog res = builder.create();
        res.setCanceledOnTouchOutside(false);
        return res;
    }

    public int getStartHr()
    {
        return startHr;
    }

    public void setStartHr(int startHr)
    {
        this.startHr = startHr;
    }

    public int getStartMin()
    {
        return startMin;
    }

    public void setStartMin(int startMin)
    {
        this.startMin = startMin;
    }

    public int getEndHr()
    {
        return endHr;
    }

    public void setEndHr(int endHr)
    {
        this.endHr = endHr;
    }

    public int getEndMin()
    {
        return endMin;
    }

    public void setEndMin(int endMin)
    {
        this.endMin = endMin;
    }

    private NumberPicker getMinuteSpinner(TimePicker t)
    {
        try
        {
            Field f = t.getClass().getDeclaredField("mMinuteSpinner"); // NoSuchFieldException
            f.setAccessible(true);
            return (NumberPicker) f.get(t); // IllegalAccessException
        }
        catch (Exception e)
        {
            return null;
        }
    }

}
