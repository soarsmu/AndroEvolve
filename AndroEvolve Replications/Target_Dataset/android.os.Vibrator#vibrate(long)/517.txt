package smd.ufc.br.easycontext.fence.action;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.google.android.gms.awareness.fence.FenceState;

import smd.ufc.br.easycontext.fence.FenceAction;


/**
 * Created by davitabosa on 20/08/2018.
 */

public class VibrateAction implements FenceAction {
    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public VibrateAction(long millis) {
        this.millis = millis;
    }

    private long millis = 500;
    public VibrateAction() {
    }

    @Override
    public void doOperation(Context context, FenceState state, Bundle data) {
        long milliseconds = data.getLong("ms");

        if(state.getCurrentState() == FenceState.TRUE){
            Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
            if(vibrator != null){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    VibrationEffect effect = VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE);
                    vibrator.vibrate(effect);
                } else {

                    vibrator.vibrate(millis);
                }
            }
        }
    }
}
