package com.handpoint.cordova.autostart;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.handpoint.cordova.HandpointApiCordova;

import org.json.JSONException;

public class EnableAutostartOperation extends AutostartOperation {

  @Override
  public void execute() throws JSONException {
    try {
      // If ACTION_MANAGE_OVERLAY_PERMISSION is disabled and Android >= 10
      if (!((HandpointApiCordova) this.cordovaPlugin).isOverlayPermissionGranted() && Build.VERSION.SDK_INT >= 29) {
        this.requestOverlaPermission();
      }
      this.setAutoStart(cordova.getActivity().getLocalClassName(), true);
    } catch (Exception e) {
      this.logger.severe("Failed to enable autostart: " + e.getMessage());
    }
  }

  private void requestOverlaPermission() {
    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:" + this.cordova.getActivity().getPackageName()));
    this.cordova.startActivityForResult(this.cordovaPlugin, intent,
        ((HandpointApiCordova) this.cordovaPlugin).ENABLE_OVERLAY_PERMISSION_CODE);
    // Handle the result in onActivityResult
  }
}
