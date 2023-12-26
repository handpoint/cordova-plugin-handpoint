package com.handpoint.cordova;

import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import java.lang.reflect.*;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.content.pm.PackageManager;

import com.handpoint.api.applicationprovider.ApplicationProvider;

public class HandpointApiCordova extends CordovaPlugin {

  private final List<PermissionResultObserver> permissionObservers = Collections.synchronizedList(new ArrayList<>());
  private final List<ActivityResultObserver> activityResultObservers = Collections
      .synchronizedList(new ArrayList<>());

  public static final int ENABLE_LOCATION_CODE = 2000;
  public static final int ENABLE_OVERLAY_PERMISSION_CODE = 2100;
  public static final String ENABLE_LOCATION_ACTION = "enableLocation";
  public static final String DISABLE_BATTERY_OPTIMIZATIONS_ACTION = "disableBatteryOptimizations";
  public static final String IS_BATTERY_OPTIMIZATION_ON_ACTION = "isBatteryOptimizationOn";

  protected Logger logger;

  Context context;
  CordovaInterface mCordova;
  HandpointHelper handpointHelper;
  String error;
  CallbackContext callbackContextActivityResult;
  Operation operation;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    try {
      super.initialize(cordova, webView);
      this.logger = Logger.getLogger(this.getClass().getSimpleName());
      this.mCordova = cordova;
      this.context = this.mCordova.getActivity();
      this.handpointHelper = new HandpointHelper(this.context);
    } catch (Throwable thr) {
      this.error = thr.toString();
    }
  }

  @Override
  public boolean execute(String ac, JSONArray arguments, CallbackContext cbc) throws JSONException {

    final String action = ac;
    final JSONArray args = arguments;
    final CallbackContext callbackContext = cbc;
    final CordovaInterface cordova = this.mCordova;
    final CordovaPlugin cordovaPlugin = this;

    cordova.getActivity().runOnUiThread(new Runnable() {
      public void run() {
        JSONObject parameters;
        try {
          parameters = args.getJSONObject(0);
          operation = OperationFactory.createOperation(action, arguments, callbackContext,
              cordova, cordovaPlugin);
          if (operation != null) {
            operation.execute();
          } else if (action.equals(ENABLE_LOCATION_ACTION)) {
            enableLocation(cbc, parameters);
          } else if (action.equals(DISABLE_BATTERY_OPTIMIZATIONS_ACTION)) {
            disableBatteryOptimizations(cbc, parameters);
          } else if (action.equals(IS_BATTERY_OPTIMIZATION_ON_ACTION)) {
            isBatteryOptimizationOn(cbc, parameters);
          } else {
            executeAction(action, cbc, parameters);
          }
        } catch (JSONException jse) {
          callbackContext.error("Handpoint SDK error getting parameters: " + jse.toString());
        }
      }
    });
    return true;
  }

  public void enableLocation(CallbackContext callbackContext, JSONObject params) throws JSONException {
    final LocationManager manager = (LocationManager) ApplicationProvider
        .getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      callbackContextActivityResult = callbackContext;
      final AlertDialog.Builder builder = new AlertDialog.Builder(context);
      builder.setMessage(params.getString("text"))
          .setCancelable(false)
          .setPositiveButton(params.getString("okBtnText"),
              (dialog, id) -> cordova.startActivityForResult(this,
                  new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), ENABLE_LOCATION_CODE))
          .setNegativeButton(params.getString("cancelBtnText"), (dialog, id) -> dialog.cancel());
      final AlertDialog alert = builder.create();
      alert.show();
    } else {
      PluginResult result = new PluginResult(PluginResult.Status.OK, "canceled action, process this in javascript");
      callbackContext.sendPluginResult(result);
    }
  }

  @Override
  public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    if (requestCode == ENABLE_LOCATION_CODE) {
      enableLocationActivityResult(resultCode, data);
    } else {
      for (ActivityResultObserver observer : activityResultObservers) {
        observer.onActivityResult(requestCode, resultCode, data);
      }
    }
    // Handle other results if exists.
    super.onActivityResult(requestCode, resultCode, data);
  }

  public void addActivityResultObserver(ActivityResultObserver observer) {
    activityResultObservers.add(observer);
  }

  public void removeActivityResultObserver(ActivityResultObserver observer) {
    activityResultObservers.remove(observer);
  }

  @Override
  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
      throws JSONException {

    final boolean permissionGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    for (PermissionResultObserver observer : permissionObservers) {
      observer.onPermissionResult(requestCode, permissionGranted);
    }
  }

  public void addPermissionObserver(PermissionResultObserver observer) {
    permissionObservers.add(observer);
  }

  public void removePermissionObserver(PermissionResultObserver observer) {
    permissionObservers.remove(observer);
  }

  private void enableLocationActivityResult(final int resultCode, final Intent data) {
    // Back from settings, that's all we know
    PluginResult result = new PluginResult(PluginResult.Status.OK, "Back from settings");
    if (this.callbackContextActivityResult != null) {
      callbackContextActivityResult.sendPluginResult(result);
    }
    callbackContextActivityResult = null;
  }

  private void executeAction(String action, CallbackContext callbackContext, JSONObject params) {
    Method method = null;
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    // Get method to invoke by name
    try {
      if (handpointHelper != null) {
        method = handpointHelper.getClass().getMethod(action, CallbackContext.class, JSONObject.class);
      } else {
        callbackContext.error("Error initializing Handpoint SDK " + error);
      }
    } catch (SecurityException e) {
      callbackContext.error("Handpoint SDK method not defined: " + action);
      return;
    } catch (NoSuchMethodException e) {
      callbackContext.error("Handpoint SDK method not defined: " + action);
      return;
    } catch (Throwable thr) {
      callbackContext.error("Handpoint SDK error: " + thr.toString());
      return;
    }

    // invoke SDK method

    try {
      method.invoke(handpointHelper, callbackContext, params);
    } catch (IllegalArgumentException e) {
      e.printStackTrace(pw);
      callbackContext.error("Handpoint SDK method illegal argument error: " + pw.toString());
    } catch (IllegalAccessException e) {
      e.printStackTrace(pw);
      callbackContext.error("Handpoint SDK method illegal access error: " + pw.toString());
    } catch (InvocationTargetException e) {
      e.getTargetException().printStackTrace(pw);
      callbackContext
          .error("Handpoint SDK method " + action + " invocation error: " + e.getTargetException().toString());
    } catch (Throwable thr) {
      thr.printStackTrace(pw);
      callbackContext.error("Handpoint SDK method error: " + pw.toString());
    }
  }

  private void disableBatteryOptimizations(CallbackContext callbackContext, JSONObject params) throws JSONException {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Intent intent = new Intent();
      String packageName = context.getPackageName();
      PowerManager pm = getPowerManager();
      if (!pm.isIgnoringBatteryOptimizations(packageName)) {
        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + packageName));
        this.cordova.getActivity().startActivity(intent);
      }
    }
  }

  private void isBatteryOptimizationOn(CallbackContext callbackContext, JSONObject params) throws JSONException {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      String packageName = context.getPackageName();
      PowerManager pm = getPowerManager();

      PluginResult result = new PluginResult(PluginResult.Status.OK, !pm.isIgnoringBatteryOptimizations(packageName));
      callbackContext.sendPluginResult(result);
    }
  }

  private PowerManager getPowerManager() {
    return (PowerManager) cordova.getActivity().getApplicationContext().getSystemService(Context.POWER_SERVICE);
  }

  public boolean isOverlayPermissionGranted() {
    return Settings.canDrawOverlays(this.cordova.getActivity());
  }

}
