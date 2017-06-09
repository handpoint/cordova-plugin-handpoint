package com.handpoint.cordova;

import com.handpoint.api.*;
import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import java.util.List;

public class HandpointApiCordova extends CordovaPlugin {

  Context context;
  CordovaInterface mCordova;
  HandpointHelper handpointHelper;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    this.mCordova = cordova;
    this.context = this.mCordova.getActivity().getApplicationContext();
    this.handpointHelper = new HandpointHelper(this.context);
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    try {
      // Echo test method
      if ("sale".equals(action)) {
        // Test Sale
        this.handpointHelper.sale(callbackContext, args.getString(0));
      } else if ("useDevice".equals(action)) {
        // TODO
      } else if ("Sale".equals(action)) {
        // TODO
      } else {
        callbackContext.error("Handpoint SDK method not defined: " + action);
      }
    } catch (Exception ex) {
      callbackContext.error("Handpoint plugin exception: " + ex.toString());
    }
    return super.execute(action, args, callbackContext);
  }

}