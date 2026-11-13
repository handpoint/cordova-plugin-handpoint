package com.handpoint.cordova.autostart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
      // If the app cannot start from background, request the user to enable ACTION_MANAGE_OVERLAY_PERMISSION in settings
      if (!((HandpointApiCordova) this.cordovaPlugin).canStartFromBackground()) {
        this.logger.info("Cannot start from background, requesting ACTION_MANAGE_OVERLAY_PERMISSION");
        // Config is optional: exec sends [] when enableAutoStart() is called without it
        JSONObject params = args.optJSONObject(0);
        if (params == null) {
          params = new JSONObject();
        }
        String title = params.optString(EnableAutostartOperation.TITLE_PARAM, EnableAutostartOperation.TITLE_DEFAULT);
        String message = params.optString(EnableAutostartOperation.MESSAGE_PARAM, EnableAutostartOperation.MESSAGE_DEFAULT);
        this.showInformationDialog(title, message);
      }
      // The dialog above is non-blocking, so autostart is enabled right away regardless of whether
      // the user ends up granting ACTION_MANAGE_OVERLAY_PERMISSION. This is intentional.
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
      if (((HandpointApiCordova) this.cordovaPlugin).canStartFromBackground()) {
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
