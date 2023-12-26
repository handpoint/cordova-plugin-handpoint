package com.handpoint.cordova.autostart;

import java.util.logging.Logger;

import com.handpoint.cordova.CallbackContext;
import com.handpoint.cordova.CordovaInterface;
import com.handpoint.cordova.CordovaPlugin;
import com.handpoint.cordova.JSONArray;
import com.handpoint.cordova.JSONException;
import com.handpoint.cordova.Operation;
import com.handpoint.cordova.autostart.receivers.BootCompletedReceiver;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.ComponentName;

public abstract class AutostartOperation implements Operation {

  public static final String PREFS = "autostart";
  public static final String ACTIVITY_CLASS_NAME = "class";

  protected JSONArray args;
  protected CallbackContext callbackContext;
  protected CordovaInterface cordova;
  protected CordovaPlugin cordovaPlugin;
  protected Logger logger;

  @Override
  public void initialize(JSONArray args, CallbackContext callbackContext, CordovaInterface cordova,
      CordovaPlugin cordovaPlugin) {
    this.args = args;
    this.callbackContext = callbackContext;
    this.cordova = cordova;
    this.cordovaPlugin = cordovaPlugin;
    this.logger = Logger.getLogger(this.getClass().getSimpleName());
  }

  protected void setAutoStart(final String className, boolean enabled) {
    Context context = cordova.getActivity().getApplicationContext();
    int componentState;
    SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();
    if (enabled) {
      componentState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
      // Store the class name of your service or main activity for AppStarter
      final String preferenceKey = ACTIVITY_CLASS_NAME;
      editor.putString(preferenceKey, className);
    } else {
      componentState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
      editor.remove(ACTIVITY_CLASS_NAME);
    }
    editor.commit();
    // Enable or Disable BootCompletedReceiver
    ComponentName bootCompletedReceiver = new ComponentName(context, BootCompletedReceiver.class);
    PackageManager pm = context.getPackageManager();
    pm.setComponentEnabledSetting(bootCompletedReceiver, componentState, PackageManager.DONT_KILL_APP);

  }

}
