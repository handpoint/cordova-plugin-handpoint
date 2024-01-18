package com.handpoint.cordova;

import org.apache.cordova.*;
import org.json.JSONArray;

import com.handpoint.cordova.sim.*;
import com.handpoint.cordova.autostart.*;

public class OperationFactory {

  public static final String GET_SIM_INFO = "getSimInfo";
  public static final String HAS_READ_PERMISSION = "hasSimReadPermission";
  public static final String REQUEST_READ_PERMISSION = "requestSimReadPermission";
  public static final String ENABLE_AUTOSTART = "enableAutoStart";
  public static final String DISABLE_AUTOSTART = "disableAutoStart";

  public static Operation createOperation(String action, JSONArray args, CallbackContext callbackContext,
      CordovaInterface cordova, CordovaPlugin cordovaPlugin) {
    switch (action) {
      case GET_SIM_INFO:
      case HAS_READ_PERMISSION:
      case REQUEST_READ_PERMISSION:
        return getSimOperation(action, args, callbackContext, cordova, cordovaPlugin);
      case ENABLE_AUTOSTART:
      case DISABLE_AUTOSTART:
        return getAutostartOperation(action, args, callbackContext, cordova, cordovaPlugin);
      default:
        return null;
    }
  }

  private static Operation getSimOperation(String action, JSONArray args, CallbackContext callbackContext,
      CordovaInterface cordova, CordovaPlugin cordovaPlugin) {
    Operation operation;
    switch (action) {
      case GET_SIM_INFO:
        operation = new GetSimInfoOperation();
        break;
      case HAS_READ_PERMISSION:
        operation = new HasSimReadPermissionOperation();
        break;
      case REQUEST_READ_PERMISSION:
        operation = new RequestSimReadPermissionOperation();
        break;
      default:
        throw new IllegalArgumentException("Invalid SIM operation type");
    }
    operation.initialize(args, callbackContext, cordova, cordovaPlugin);
    return operation;
  }

  private static Operation getAutostartOperation(String action, JSONArray args, CallbackContext callbackContext,
      CordovaInterface cordova, CordovaPlugin cordovaPlugin) {
    Operation operation;
    switch (action) {
      case ENABLE_AUTOSTART:
        operation = new EnableAutostartOperation();
        break;
      case DISABLE_AUTOSTART:
        operation = new DisableAutostartOperation();
        break;
      default:
        throw new IllegalArgumentException("Invalid autostart operation type");
    }
    operation.initialize(args, callbackContext, cordova, cordovaPlugin);
    return operation;
  }

}
