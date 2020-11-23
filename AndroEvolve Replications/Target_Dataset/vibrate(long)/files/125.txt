package vibradroid;

import org.haxe.extension.Extension;
import android.os.Vibrator;
import android.app.Activity;
import android.content.res.AssetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class Vibradroid extends Extension {
	
	public static void Vibrate(int duration)
	{
		Vibrator vibradroid = (Vibrator) Extension.mainContext.getSystemService(Context.VIBRATOR_SERVICE);
		long d = (long) duration;
		vibradroid.vibrate(d);
	}

	public static void VibrateIndefinitely()
	{
		Vibrator vibradroid = (Vibrator) Extension.mainContext.getSystemService(Context.VIBRATOR_SERVICE);
		long d[] = new long[3];
		d[0] = 1000;
		d[1] = 1000;
		d[2] = 1000;
		vibradroid.vibrate(d, 0);
	}

	public static void VibratePatterns(int[] hxpattern, int repeat)
	{
		Vibrator vibradroid = (Vibrator) Extension.mainContext.getSystemService(Context.VIBRATOR_SERVICE);
		long pattern[] = new long[hxpattern.length];
		for (int i = 0; i < hxpattern.length; i++)
		{
			pattern[i] = (long) hxpattern[i];
		}
		int r = repeat;
		if (r == 0) r = -1;
		vibradroid.vibrate(pattern, r);
	}

}