package com.handpoint.cordova.autostart;

import com.handpoint.cordova.JSONException;

public class EnableAutostartOperation extends AutostartOperation {

  @Override
  public void execute() throws JSONException {
    this.setAutoStart(cordova.getActivity().getLocalClassName(), false);
  }

}
