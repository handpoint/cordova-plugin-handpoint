package com.handpoint.cordova.autostart;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;

import com.handpoint.cordova.autostart.receivers.UserPresentReceiver;

public class AppStarter {

  public static final int BYPASS_USERPRESENT_MODIFICATION = -1;
  private static final String CORDOVA_AUTOSTART = "mpos_autostart";

  public void run(Context context, Intent intent, int componentState) {
    this.run(context, intent, componentState, false);
  }

  public void run(Context context, Intent intent, int componentState, boolean onAutostart) {
    // Enable or Disable UserPresentReceiver (or bypass the modification)
    if (componentState != BYPASS_USERPRESENT_MODIFICATION) {
      ComponentName receiver = new ComponentName(context, UserPresentReceiver.class);
      PackageManager pm = context.getPackageManager();
      pm.setComponentEnabledSetting(receiver, componentState, PackageManager.DONT_KILL_APP);
    }

    SharedPreferences sp = context.getSharedPreferences(AutostartOperation.PREFS, Context.MODE_PRIVATE);
    String packageName = context.getPackageName();
    String activityClassName = sp.getString(AutostartOperation.ACTIVITY_CLASS_NAME, "");
    if (!activityClassName.equals("")) {
      Intent activityIntent = new Intent();
      activityIntent.setClassName(
          context, String.format("%s.%s", packageName, activityClassName));
      activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      if (onAutostart) {
        activityIntent.putExtra(CORDOVA_AUTOSTART, true);
      }
      context.startActivity(activityIntent);
    }
  }
}
