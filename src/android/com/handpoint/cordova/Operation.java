package com.handpoint.cordova;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public interface Operation {

  void initialize(JSONArray args, CallbackContext callbackContext, CordovaInterface cordova,
      CordovaPlugin cordovaPlugin);

  void execute() throws JSONException;

}
