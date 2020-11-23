package com.nemustech.study.sysinfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.drm.DrmManagerClient;
import android.os.Build;

import java.util.ArrayList;

/**
 * Created by cheolgyoon on 2016. 6. 3..
 *
 */
public class DrmInfoProvider extends InfoProvider {
    @SuppressWarnings("unused")
    private static final String TAG = DrmInfoProvider.class.getSimpleName();
    private static ArrayList<InfoItem> sDrmItems;

    DrmInfoProvider(Context context) {
        super(context);
    }

    @SuppressLint("NewApi")
    @Override
    ArrayList<InfoItem> getItems() {
        if (null == sDrmItems) {
            sDrmItems = new ArrayList<InfoItem>();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                sDrmItems.add(new InfoItem(getString(R.string.item_drm), getString(R.string.sdk_version_required, Build.VERSION_CODES.HONEYCOMB)));
            } else {
                DrmManagerClient dmc = new DrmManagerClient(mContext);
                String[] engines = dmc.getAvailableDrmEngines();
                if (null == engines || 0 == engines.length) {
                    sDrmItems.add(new InfoItem(getString(R.string.item_drm), getString(R.string.drm_none)));
                } else {
                    StringBuffer sb = new StringBuffer();
                    sb.append("- ").append(engines[0]);
                    for (int idx = 1; idx < engines.length; ++idx) {
                        sb.append("\n- ").append(engines[idx]);
                    }
                    sDrmItems.add(new InfoItem(getString(R.string.item_drm), sb.toString()));
                }
                if (Build.VERSION_CODES.JELLY_BEAN <= Build.VERSION.SDK_INT) {
                    dmc.release();
                }
            }
        }
        return sDrmItems;
    }

    @Override
    protected InfoItem getItem(int infoId, Object... params) {
        return null;
    }

    @Override
    protected Object[] getInfoParams() {
        return new Object[0];
    }
}
