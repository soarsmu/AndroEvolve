package io.intercom.android.sdk.commons.utilities;

import android.os.Build.VERSION;
import android.text.Html;
import android.text.Spanned;

public class HtmlCompat
{
  public static Spanned fromHtml(String paramString)
  {
    if (Build.VERSION.SDK_INT >= 24) {
      return Html.fromHtml(paramString, 0);
    }
    return Html.fromHtml(paramString);
  }
}


/* Location:              /home/qerwtr546/Documents/Vent Source/dex2jar-2.0/classes-dex2jar.jar!/io/intercom/android/sdk/commons/utilities/HtmlCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */