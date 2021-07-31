package org.nem.nac.application;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.StringRes;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import org.nem.nac.common.models.Size;
import org.nem.nac.ui.activities.NacBaseActivity;

import java.lang.ref.SoftReference;

/**
 * Represents device the app is running on
 */
public final class AppHost {

	private static int _availableProcessors = Runtime.getRuntime().availableProcessors();

	public static int getAvailableProcessors() {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
			? _availableProcessors
			: Runtime.getRuntime().availableProcessors();
	}

	public static final class Camera {

		public static boolean hasCamera() {
			return NacApplication.getAppContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
		}
	}

	public static final class Network {

		public static boolean isAvailable() {
			final ConnectivityManager connectivityManager = (ConnectivityManager)NacApplication.getAppContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		}
	}

	public static final class Screen {

		private static SoftReference<Display> _defaultDisplay = new SoftReference<>(null);

		private static SoftReference<DisplayMetrics> _displayMetrics = new SoftReference<>(null);

		public static float getDensityLogical() {
			DisplayMetrics displayMetrics = _displayMetrics.get();
			if (null == displayMetrics) {
				displayMetrics = NacApplication.getAppContext().getResources().getDisplayMetrics();
				_displayMetrics = new SoftReference<>(displayMetrics);
			}
			return displayMetrics.density;
		}

		/**
		 * Returns density group in DPI ({@link android.util.DisplayMetrics#DENSITY_LOW},
		 * {@link android.util.DisplayMetrics#DENSITY_MEDIUM}, or {@link android.util.DisplayMetrics#DENSITY_HIGH})
		 */
		public static int getDensityDpi() {
			DisplayMetrics displayMetrics = _displayMetrics.get();
			if (null == displayMetrics) {
				displayMetrics = NacApplication.getAppContext().getResources().getDisplayMetrics();
				_displayMetrics = new SoftReference<>(displayMetrics);
			}
			return displayMetrics.densityDpi;
		}

		public static float dpToPx(final float dp) {
			DisplayMetrics displayMetrics = _displayMetrics.get();
			if (null == displayMetrics) {
				displayMetrics = NacApplication.getAppContext().getResources().getDisplayMetrics();
				_displayMetrics = new SoftReference<>(displayMetrics);
			}
			return dp * displayMetrics.density + 0.5f;
		}

		public static int getRotation() {
			return populateDefaultDisplay().getRotation();
		}

		public static Size getSize() {
			final Point size = new Point();
			populateDefaultDisplay().getSize(size);
			return new Size(size.x, size.y);
		}

		public static Size getSizeDpLogical() {
			final Point size = new Point();
			populateDefaultDisplay().getSize(size);
			final float density = getDensityLogical();
			return new Size((int)(size.x / density), (int)(size.y / density));
		}

		private static Display populateDefaultDisplay() {
			Display defaultDisplay = _defaultDisplay.get();
			if (defaultDisplay == null) {
				defaultDisplay = ((WindowManager)NacApplication.getAppContext()
					.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
				_defaultDisplay = new SoftReference<>(defaultDisplay);
			}
			return defaultDisplay;
		}
	}

	public static final class Vibro {

		public static final int SHORT_VIBE  = 100;
		public static final int MEDIUM_VIBE = 300;

		public static void vibrate(final int ms) {
			android.os.Vibrator v = (android.os.Vibrator)NacApplication.getAppContext()
				.getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(ms);
		}

		public static void vibratePattern(final long[] pattern) {
			android.os.Vibrator v = (android.os.Vibrator)NacApplication.getAppContext()
				.getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(pattern, -1);
		}

		public static void vibrateOneShort() {
			vibrate(SHORT_VIBE);
		}

		public static void vibrateOneMedium() {
			vibrate(MEDIUM_VIBE);
		}

		public static void vibrateTwoShort() {
			vibratePattern(new long[] { 0, SHORT_VIBE, SHORT_VIBE, SHORT_VIBE });
		}
	}

	public static class Clipboard {

		public static void copyText(final @StringRes int label, final String text) {
			copyText(NacApplication.getAppContext().getString(label), text);
		}

		public static void copyText(final String label, final String text) {
			final ClipboardManager clipboard = (ClipboardManager)NacApplication.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
			final ClipData clip = ClipData.newPlainText(label, text);
			clipboard.setPrimaryClip(clip);
		}
	}

	public static class SoftKeyboard {

		private static SoftReference<InputMethodManager> _imm = new SoftReference<InputMethodManager>(null);

		public static void forceHide(NacBaseActivity activity) {
			final View focus;
			if (activity != null && (focus = activity.getCurrentFocus()) != null) { getImm().hideSoftInputFromWindow(focus.getWindowToken(), 0); }
		}

		public static void forceHideAsync(final NacBaseActivity activity) {
			NacApplication.getMainHandler().postDelayed(() -> SoftKeyboard.forceHide(activity), 50);
		}

		public static void forceShow() {
			getImm().toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
		}

		public static void forceShowAsync() {
			NacApplication.getMainHandler().postDelayed(SoftKeyboard::forceShow, 50);
		}

		private static InputMethodManager getImm() {
			InputMethodManager imm = _imm.get();
			if (imm == null) {
				imm = (InputMethodManager)NacApplication.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				_imm = new SoftReference<>(imm);
			}
			return imm;
		}
	}
}
