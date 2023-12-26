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
import org.json.JSONObject;

public class EnableAutostartOperation extends AutostartOperation implements ActivityResultObserver {

  public static final String TITLE_PARAM = "title";
  public static final String MESSAGE_PARAM = "message";
  public static final String TITLE_DEFAULT = "Enable automatic start";
  public static final String MESSAGE_DEFAULT = "To continue, the app needs permission to display over other apps. This is essential for enabling the app to start automatically when your device boots. Please tap 'Accept' to proceed to the settings menu. After adjusting this setting, please return to the app to continue with the setup process.";

  @Override
  public void execute() throws JSONException {
    try {
      // If ACTION_MANAGE_OVERLAY_PERMISSION is disabled and Android >= 10
      // then request the user to enable it in settings
      JSONObject params = args.getJSONObject(0);
      if (!((HandpointApiCordova) this.cordovaPlugin).isOverlayPermissionGranted() && Build.VERSION.SDK_INT >= 29) {
        String title = params.getString(EnableAutostartOperation.TITLE_PARAM) == null
            ? EnableAutostartOperation.TITLE_DEFAULT
            : params.getString(EnableAutostartOperation.TITLE_PARAM);
        String message = params.getString(EnableAutostartOperation.MESSAGE_PARAM) == null
            ? EnableAutostartOperation.MESSAGE_DEFAULT
            : params.getString(EnableAutostartOperation.MESSAGE_PARAM);
        this.showInformationDialog(title, message);
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

  private void showInformationDialog(String title, String message) {
    EnableAutostartOperation enableAutostartOperation = this;
    AlertDialog.Builder builder = new AlertDialog.Builder(this.cordova.getActivity());
    builder.setTitle(title);
    builder.setMessage(message);
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
