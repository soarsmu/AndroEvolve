package com.android.providers.downloads;

import android.content.Context;
import android.drm.DrmManagerClient;
import java.io.File;

public class DownloadDrmHelper
{
  public static String getOriginalMimeType(Context paramContext, File paramFile, String paramString)
  {
    DrmManagerClient localDrmManagerClient = new DrmManagerClient(paramContext);
    try
    {
      String str1 = paramFile.toString();
      if (localDrmManagerClient.canHandle(str1, null))
      {
        String str2 = localDrmManagerClient.getOriginalMimeType(str1);
        return str2;
      }
      return paramString;
    }
    finally
    {
      localDrmManagerClient.release();
    }
  }
}


/* Location:              /Users/charlesfoxw/Desktop/dex2jar-0.0.9.15/classes-dex2jar.jar!/com/android/providers/downloads/DownloadDrmHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */