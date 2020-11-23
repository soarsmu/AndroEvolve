package com.android.myos.drm.impl;

import android.annotation.TargetApi;
import android.content.Context;
import android.drm.DrmManagerClient;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.android.myos.drm.DrmHelper.IDrmCallback;
import com.android.myos.drm.DrmItem;
import com.android.myos.drm.config.ProductConfig;

public class QcDrm_23 implements IDrm {
    private static final String EXTENSION_DCF = ".dcf";
    private static final String EXTENSION_DM = ".dm";
    private static final String EXTENSION_FL = ".fl";
    private static final String TAG = "QcDrm_23";
    private Context mContext = null;
    private DrmManagerClient mDrmClient = null;

    public QcDrm_23(Context context) {
        this.mContext = context;
        this.mDrmClient = new DrmManagerClient(this.mContext);
    }

    public boolean isDrmFile(String file) {
        if (TextUtils.isEmpty(file) || (!file.endsWith(EXTENSION_FL) && !file.endsWith(".dm") && !file.endsWith(".dcf"))) {
            return false;
        }
        return true;
    }

    public boolean isDrmFile(Uri uri) {
        String file = ProductConfig.convertUriToPath(this.mContext, uri);
        Log.d(TAG, "isDrmFile uri file = " + file);
        if (file != null) {
            return isDrmFile(file);
        }
        return false;
    }

    public int getDrmType(String file) {
        return 0;
    }

    public int getDrmType(Uri uri) {
        return 0;
    }

    public String getOriginalMimeType(String path) {
        return this.mDrmClient.getOriginalMimeType(path);
    }

    public String getOriginalMimeType(Uri uri) {
        return this.mDrmClient.getOriginalMimeType(uri);
    }

    public boolean checkRights(String file, int action) {
        return true;
    }

    public boolean checkRights(Uri uri, int action) {
        return true;
    }

    public int consumeRights(String file, Uri uri, String mineType) {
        return -1;
    }

    public Bitmap decodeImageToBitmap(String file, Options options, boolean consume) {
        return null;
    }

    public byte[] decodeImageToBytes(String file, boolean consume) {
        return null;
    }

    public BitmapRegionDecoder createBitmapRegionDecoder(String path, boolean isShareable) {
        return null;
    }

    public boolean handleDrmFile(DrmItem item, IDrmCallback callback) {
        return false;
    }

    public void showProperties(DrmItem item) {
    }

    public boolean isTokenValid(String file, String token) {
        return false;
    }

    public boolean clearToken(String file, String token) {
        return false;
    }

    @TargetApi(16)
    public void release() {
        this.mDrmClient.release();
        this.mContext = null;
        this.mDrmClient = null;
    }
}
