package com.handpoint.cordova.autostart.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.handpoint.cordova.autostart.AppStarter;

public class PackageReplacedReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    AppStarter appStarter = new AppStarter();
    appStarter.run(context, intent, AppStarter.BYPASS_USERPRESENT_MODIFICATION);
  }

}
