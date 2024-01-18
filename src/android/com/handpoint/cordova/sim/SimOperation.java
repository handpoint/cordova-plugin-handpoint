package com.handpoint.cordova.sim;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public interface SimOperation {

  void initialize(JSONArray args, CallbackContext callbackContext, CordovaInterface cordova,
      CordovaPlugin cordovaPlugin);

  void execute() throws JSONException;

  void onRequestPermissionResult(boolean granted);

}
