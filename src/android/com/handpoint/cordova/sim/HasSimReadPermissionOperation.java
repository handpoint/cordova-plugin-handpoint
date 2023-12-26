package com.handpoint.cordova.sim;

import org.json.JSONException;

public class HasSimReadPermissionOperation extends BaseSimOperation {

  @Override
  public void execute() throws JSONException {
    callbackContext.success(String.valueOf(simPermissionGranted()));
  }

}
