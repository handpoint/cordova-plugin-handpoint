package com.handpoint.cordova.sim;

import android.Manifest;
import android.content.pm.PackageManager;

import com.handpoint.cordova.HandpointApiCordova;
import com.handpoint.cordova.PermissionResultObserver;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class RequestSimReadPermissionOperation extends BaseSimOperation implements PermissionResultObserver {

  public static final int REQUEST_READ_PHONE_STATE = 12000;

  @Override
  public void execute() throws JSONException {
    if (!simPermissionGranted()) {
      ((HandpointApiCordova) this.cordovaPlugin).addPermissionObserver(this);
      cordova.requestPermission(this.cordovaPlugin, REQUEST_READ_PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
    } else {
      callbackContext.success(Boolean.TRUE.toString());
    }
  }

  @Override
  public void onPermissionResult(int requestCode, boolean isGranted) {
    if (requestCode == REQUEST_READ_PHONE_STATE) {
      callbackContext.success(isGranted ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
      ((HandpointApiCordova) this.cordovaPlugin).removePermissionObserver(this);
    }
  }
}
