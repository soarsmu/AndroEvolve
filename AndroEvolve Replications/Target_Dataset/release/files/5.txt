package com.example.ithan.drmframework.util;

import android.content.ContentValues;
import android.content.Context;
import android.drm.DrmErrorEvent;
import android.drm.DrmEvent;
import android.drm.DrmInfo;
import android.drm.DrmInfoEvent;
import android.drm.DrmInfoRequest;
import android.drm.DrmInfoStatus;
import android.drm.DrmManagerClient;
import android.drm.DrmRights;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by ithan on 2015. 6. 30..
 */
public class DrmManager {

    private Context mContext = null;
    private DrmManagerClient mDrmManagerClient = null;

    public DrmManager(Context context) {
        mContext = context;

        mDrmManagerClient = new DrmManagerClient(mContext);
        mDrmManagerClient.setOnErrorListener(mOnErrorListener);
        mDrmManagerClient.setOnEventListener(mOnEventListener);
        mDrmManagerClient.setOnInfoListener(mOnInfoListener);
    }

    public void release() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            mDrmManagerClient.release();
        }

        mDrmManagerClient = null;
    }

    public String[] getAvailableDrmEngines() {
        return mDrmManagerClient.getAvailableDrmEngines();
    }

    public boolean canHandle(String path, String mimeType) {
        return mDrmManagerClient.canHandle(path, mimeType);
    }

    public boolean canHandle(Uri uri, String mimeType) {
        return mDrmManagerClient.canHandle(uri, mimeType);
    }

    public int checkRightsStatus(String path) {
        return mDrmManagerClient.checkRightsStatus(path);
    }

    public int checkRightsStatus(String path, int action) {
        return mDrmManagerClient.checkRightsStatus(path, action);
    }

    public int checkRightsStatus(Uri uri) {
        return mDrmManagerClient.checkRightsStatus(uri);
    }

    public int checkRightsStatus(Uri uri, int action) {
        return mDrmManagerClient.checkRightsStatus(uri, action);
    }

    public int getDrmObjectType(String path, String mimeType) {
        return mDrmManagerClient.getDrmObjectType(path, mimeType);
    }

    public int getDrmObjectType(Uri uri, String mimeType) {
        return mDrmManagerClient.getDrmObjectType(uri, mimeType);
    }

    public String getOriginalMimeType(String path) {
        return mDrmManagerClient.getOriginalMimeType(path);
    }

    public String getOriginalMimeType(Uri uri) {
        return mDrmManagerClient.getOriginalMimeType(uri);
    }

    public int acquireRights(DrmInfoRequest drmInfoRequest) {
        return mDrmManagerClient.acquireRights(drmInfoRequest);
    }

    public void processDrmInfo(String path) {
        LogInfo.d("processDrmInfo");
        int convertSession = mDrmManagerClient.openConvertSession(getOriginalMimeType(path));
        DrmInfoRequest drmInfoRequest = new DrmInfoRequest(DrmInfoRequest.TYPE_RIGHTS_ACQUISITION_INFO, getOriginalMimeType(path));
        DrmInfo drmInfo = mDrmManagerClient.acquireDrmInfo(drmInfoRequest);

        if (drmInfo == null) {
            return;
        }

        mDrmManagerClient.processDrmInfo(drmInfo);

        int result = mDrmManagerClient.acquireRights(drmInfoRequest);
        if (result < 0 ) {

        }
        LogInfo.d("processDrmInfo acquireRights result : " + result);

        mDrmManagerClient.closeConvertSession(convertSession);
    }

    public void getConstraints(String path, int action) {
        String basePath = Environment.getExternalStorageDirectory().getPath() + "/Download/playready";
        String rightsPath = basePath + "/Bear_Video_OPLs0/LicenseAcquisition.cms";
        String rights = basePath + "/drm.rights";

        DrmRights drmRights = new DrmRights(rightsPath, getOriginalMimeType(path));
        //String rightsPath = Environment.getExternalStorageDirectory().getPath() + "/Download/playready/rights";
        {
            File file = new File(rights);
            if (file.exists()) {
                LogInfo.d("file is exist");
            } else {
                LogInfo.d("file is not exist");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            int result = mDrmManagerClient.saveRights(drmRights, rights, path);

            LogInfo.d("saveRights result : " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DrmManagerClient.OnErrorListener mOnErrorListener = new DrmManagerClient.OnErrorListener() {

        @Override
        public void onError(DrmManagerClient client, DrmErrorEvent event) {
            LogInfo.d("onError type : " + event.getType());
        }
    };

    private DrmManagerClient.OnEventListener mOnEventListener = new DrmManagerClient.OnEventListener() {

        @Override
        public void onEvent(DrmManagerClient client, DrmEvent event) {
            LogInfo.d("onEvent type : " + event.getType());
        }
    };

    private DrmManagerClient.OnInfoListener mOnInfoListener = new DrmManagerClient.OnInfoListener() {

        @Override
        public void onInfo(DrmManagerClient client, DrmInfoEvent event) {
            LogInfo.d("onInfo type : " + event.getType());
        }
    };
}
