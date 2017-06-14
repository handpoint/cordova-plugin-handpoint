package com.handpoint.cordova;

import com.handpoint.api.*;
import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.*;

import android.content.Context;

import java.util.List;
import java.lang.reflect.*;

import java.io.StringWriter;
import java.io.PrintWriter;

public class HandpointApiCordova extends CordovaPlugin {

  Context context;
  CordovaInterface mCordova;
  HandpointHelper handpointHelper;
  String error;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    try {
      super.initialize(cordova, webView);
      this.mCordova = cordova;
      this.context = this.mCordova.getActivity().getApplicationContext();
      this.handpointHelper = new HandpointHelper(this.context);
    } catch (Throwable thr) {
      this.error = thr.toString();
    }
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    Method method = null;
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    // Get method to invoke by name
    try {
      if (this.handpointHelper != null) {
        method = this.handpointHelper.getClass().getMethod(action, CallbackContext.class, JSONObject.class);
      } else {
        callbackContext.error("Error initializing Handpoint SDK " + this.error);
      }
    } catch (SecurityException e) {
      callbackContext.error("Handpoint SDK method not defined: " + action);
      return false;
    } catch (NoSuchMethodException e) {
      callbackContext.error("Handpoint SDK method not defined: " + action);
      return false;
    } catch (Throwable thr) {
      callbackContext.error("Handpoint SDK error: " + thr.toString());
      return false;
    }

    // invoke SDK method
    try {
      method.invoke(this.handpointHelper, callbackContext, args.getJSONObject(0));
    } catch (IllegalArgumentException e) {
      e.printStackTrace(pw);
      callbackContext.error("Handpoint SDK method illegal argument error: " + pw.toString());
      return false;
    } catch (IllegalAccessException e) {
      e.printStackTrace(pw);
      callbackContext.error("Handpoint SDK method illegal access error: " + pw.toString());
      return false;
    } catch (InvocationTargetException e) {
      e.getTargetException().printStackTrace(pw);
      callbackContext
          .error("Handpoint SDK method " + action + " invocation error: " + e.getTargetException().toString());
      return false;
    } catch (Throwable thr) {
      thr.printStackTrace(pw);
      callbackContext.error("Handpoint SDK method error: " + pw.toString());
      return false;
    }

    return true;
  }

}
