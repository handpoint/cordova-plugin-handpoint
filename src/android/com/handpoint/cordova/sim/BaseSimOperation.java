package com.handpoint.cordova.sim;

import android.Manifest;
import android.os.Build;

import org.apache.cordova.*;
import org.json.JSONArray;

import com.handpoint.cordova.Operation;

import java.util.logging.Logger;

import org.apache.cordova.CordovaInterface;

public abstract class BaseSimOperation implements Operation {

  protected JSONArray args;
  protected CallbackContext callbackContext;
  protected CordovaInterface cordova;
  protected CordovaPlugin cordovaPlugin;
  protected Logger logger;

  abstract void onRequestPermissionResult(boolean granted);

  @Override
  public void initialize(JSONArray args, CallbackContext callbackContext, CordovaInterface cordova,
      CordovaPlugin cordovaPlugin) {
    this.args = args;
    this.callbackContext = callbackContext;
    this.cordova = cordova;
    this.cordovaPlugin = cordovaPlugin;
    this.logger = Logger.getLogger(this.getClass().getSimpleName());
  }

  @Override
  public void onRequestPermissionResult(boolean granted) {
    if (granted) {
      callbackContext.success();
    } else {
      callbackContext.error("Permission denied");
    }
  }

  protected boolean simPermissionGranted() {
    // Below Android 6.0 all permissions are granted at install time
    if (Build.VERSION.SDK_INT < 23) {
      return true;
    }
    return this.cordova.hasPermission(Manifest.permission.READ_PHONE_STATE);
  }

}
