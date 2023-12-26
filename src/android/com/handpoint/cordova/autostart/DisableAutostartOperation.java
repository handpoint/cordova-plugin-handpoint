package com.handpoint.cordova.autostart;

import com.handpoint.cordova.JSONException;

public class DisableAutostartOperation extends AutostartOperation {

  @Override
  public void execute() throws JSONException {
    this.setAutoStart(null, false);
  }

}
