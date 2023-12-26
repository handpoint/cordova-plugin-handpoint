package com.handpoint.cordova.autostart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.handpoint.cordova.HandpointApiCordova;
import com.handpoint.cordova.ActivityResultObserver;
import org.json.JSONException;

public class EnableAutostartOperation extends AutostartOperation implements ActivityResultObserver {

  @Override
  public void execute() throws JSONException {
    try {
      // If ACTION_MANAGE_OVERLAY_PERMISSION is disabled and Android >= 10
      // then request the user to enable it in settings
      if (!((HandpointApiCordova) this.cordovaPlugin).isOverlayPermissionGranted() && Build.VERSION.SDK_INT >= 29) {
        this.showInformationDialog();
      }
      this.setAutoStart(cordova.getActivity().getLocalClassName(), true);
    } catch (Exception e) {
      this.logger.severe("Failed to enable autostart: " + e.getMessage());
    }
  }

  @Override
  public void onActivityResult(int requestCode, final int resultCode, final Intent data) {
    if (requestCode == ((HandpointApiCordova) this.cordovaPlugin).ENABLE_OVERLAY_PERMISSION_CODE) {
      ((HandpointApiCordova) this.cordovaPlugin).removeActivityResultObserver(this);
      // Check again if permission has been granted
      if (!((HandpointApiCordova) this.cordovaPlugin).isOverlayPermissionGranted()) {
        this.logger.info("ACTION_MANAGE_OVERLAY_PERMISSION allowed");
      } else {
        this.logger.warning("ACTION_MANAGE_OVERLAY_PERMISSION not allowed");
      }
    }
  }

  private void showInformationDialog() {
    EnableAutostartOperation enableAutostartOperation = this;
    AlertDialog.Builder builder = new AlertDialog.Builder(this.cordova.getActivity());
    builder.setTitle("Important Permission Required");
    builder.setMessage(
        "To continue, the app needs permission to display over other apps. This is essential for [Explain why your app needs this permission]. Please tap 'Accept' to proceed to the settings.");

    builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        // User clicked Accept button
        enableAutostartOperation.requestOverlayPermission();
      }
    });

    AlertDialog dialog = builder.create();
    dialog.show();
  }

  private void requestOverlayPermission() {
    ((HandpointApiCordova) this.cordovaPlugin).addActivityResultObserver(this);
    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:" + this.cordova.getActivity().getPackageName()));
    this.cordova.startActivityForResult(this.cordovaPlugin, intent,
        ((HandpointApiCordova) this.cordovaPlugin).ENABLE_OVERLAY_PERMISSION_CODE);

  }
}
