package com.handpoint.cordova.sim;

import org.apache.cordova.*;
import org.json.JSONArray;

public class SimOperationFactory {

  public static final String GET_SIM_INFO = "getSimInfo";
  public static final String HAS_READ_PERMISSION = "hasSimReadPermission";
  public static final String REQUEST_READ_PERMISSION = "requestSimReadPermission";

  public static SimOperation createOperation(String action, JSONArray args, CallbackContext callbackContext,
      CordovaInterface cordova, CordovaPlugin cordovaPlugin) {
    switch (action) {
      case GET_SIM_INFO:
      case HAS_READ_PERMISSION:
      case REQUEST_READ_PERMISSION:
        return getSimOperation(action, args, callbackContext, cordova, cordovaPlugin);
      default:
        return null;
    }
  }

  private static SimOperation getSimOperation(String action, JSONArray args, CallbackContext callbackContext,
      CordovaInterface cordova, CordovaPlugin cordovaPlugin) {
    SimOperation operation;
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

}
