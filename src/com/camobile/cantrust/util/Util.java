package com.camobile.cantrust.util;

import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import android.view.Gravity;


public class Util
{
  public static void show_toast (Context context, String s)
  {
    Toast t = Toast.makeText (context, s, Toast.LENGTH_LONG);
    t.setGravity (Gravity.CENTER, 0, 0);
    t.show ();

    Log.d ("CanTrust", s);
  }

  public static String regex_lf2space (String s)
  {
    return s.replaceAll ("\\n", "\\s");
  }
}
