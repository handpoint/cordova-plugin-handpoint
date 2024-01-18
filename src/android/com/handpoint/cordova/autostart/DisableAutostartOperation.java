package com.handpoint.cordova.autostart;

import org.json.JSONException;

public class DisableAutostartOperation extends AutostartOperation {

  @Override
  public void execute() throws JSONException {
    try {
      this.setAutoStart(null, false);
    } catch (Exception e) {
      this.logger.severe("Failed to disable autostart: " + e.getMessage());
    }

  }

}
