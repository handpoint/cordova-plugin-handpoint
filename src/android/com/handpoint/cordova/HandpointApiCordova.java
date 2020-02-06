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

    cordova.getThreadPool().execute(new Runnable() {
      public void run() {

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
          method.invoke(handpointHelper, callbackContext, args.getJSONObject(0));
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
    });
    return true;
  }

}
