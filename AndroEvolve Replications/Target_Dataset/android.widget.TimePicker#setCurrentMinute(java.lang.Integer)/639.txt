package interdroid.vdb.avro.control.handler;

import interdroid.vdb.avro.control.handler.value.ValueHandler;
import interdroid.vdb.avro.view.DataFormatUtil;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

/**
 * Handler for time widgets.
 * @author nick &lt;palmer@cs.vu.nl&gt;
 *
 */
public class TimeHandler implements OnTimeChangedListener {
	/** Access to logger. */
	private static final Logger LOG = LoggerFactory
			.getLogger(TimeHandler.class);

	/** The year of the start of the epoch time. */
	private static final int	EPOCH_START_YEAR	= 1970;

	/** The value handler to get data from. */
	private final ValueHandler mValueHandler;

	/**
	 * Construct a time handler.
	 * @param view the picker we handle
	 * @param valueHandler the value handler to get and set data with
	 */
	public TimeHandler(final TimePicker view, final ValueHandler valueHandler) {
		mValueHandler = valueHandler;
		// Set the initial value
		final Calendar value = Calendar.getInstance();
		try {
			final Date date = DataFormatUtil.getTimeAsDate(
					(Long) mValueHandler.getValue());
			value.set(EPOCH_START_YEAR, Calendar.JANUARY, 1,
					date.getHours(), date.getMinutes(), 0);
		} catch (ParseException e) {
			LOG.error("Error parsing time! Defaulting to now.", e);
		}
		LOG.debug("Initializing time to: {} {}",
				value.get(Calendar.HOUR_OF_DAY), value.get(Calendar.MINUTE));
		view.setCurrentHour(value.get(Calendar.HOUR_OF_DAY));
		view.setCurrentMinute(value.get(Calendar.MINUTE));
		view.setOnTimeChangedListener(this);
	}

	@Override
	public final void onTimeChanged(final TimePicker view, final int hourOfDay,
			final int minute) {
		LOG.debug("Updating time: {} {}", hourOfDay, minute);
		// Update the model
		final Calendar value = Calendar.getInstance();
		value.set(EPOCH_START_YEAR, Calendar.JANUARY, 1, hourOfDay, minute, 0);
		final long format = DataFormatUtil.formatTimeForStorage(value);
		LOG.debug("Setting time to: {}", format);
		mValueHandler.setValue(format);
	}

}
