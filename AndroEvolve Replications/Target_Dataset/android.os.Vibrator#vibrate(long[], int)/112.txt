package org.itzheng.ring.vibrate.impl;

import android.content.Context;
import android.os.Vibrator;

import org.itzheng.ring.vibrate.IVibrate;

/**
 * Title:<br>
 * Description: <br>
 *
 * @email ItZheng@ZoHo.com
 * Created by itzheng on 2018-5-8.
 */
public class MyVibrate implements IVibrate {
    protected Context mContext;
    /**
     * 是否震动
     */
    protected boolean isVibrate = false;
    /**
     * 等待1秒，震动2秒，等待1秒，震动3秒
     */
    private long[] mPattern = {1000, 2000, 1000, 3000};
    int mRepeat = 0;

    public MyVibrate(Context context) {
        mContext = context;
    }

    private Vibrator vibrator;

    /**
     * 获取振动器实例
     *
     * @return
     */
    public Vibrator getVibrator() {
        if (vibrator == null) {
            vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        }
        return vibrator;
    }

    @Override
    public void startVibrate() {
        isVibrate = true;
        getVibrator().vibrate(mPattern, mRepeat);
    }

    @Override
    public void setModel(long[] pattern, int repeat) {
        mPattern = pattern;
        mRepeat = repeat;
        if (isVibrate) {
            startVibrate();
        }
    }

    @Override
    public void cancel() {
        isVibrate = false;
        if (getVibrator().hasVibrator()) {
            getVibrator().cancel();
        }
    }
}
