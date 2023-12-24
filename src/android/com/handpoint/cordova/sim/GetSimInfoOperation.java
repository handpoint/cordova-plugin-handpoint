package com.handpoint.cordova.sim;

import org.apache.cordova.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Logger;

import org.apache.cordova.CallbackContext;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import android.content.Context;
import android.Manifest;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

public class GetSimInfoOperation extends BaseSimOperation {

  private static final String PHONE_NUMBER_KEY = "phoneNumber";
  private static final String DEVICE_ID_KEY = "deviceId";
  private static final String DEVICE_SOFTWARE_VERSION_KEY = "deviceSoftwareVersion";
  private static final String SIM_SERIAL_NUMBER_KEY = "simSerialNumber";
  private static final String SUBSCRIBER_ID_KEY = "subscriberId";
  private static final String CARRIER_NAME_KEY = "carrierName";
  private static final String COUNTRY_CODE_KEY = "countryCode";
  private static final String DATA_ACTIVITY_KEY = "dataActivity";
  private static final String PHONE_TYPE_KEY = "phoneType";
  private static final String SIM_STATE_KEY = "simState";
  private static final String MCC_KEY = "mcc";
  private static final String MNC_KEY = "mnc";

  private static final String TAG = GetSimInfoOperation.class.getSimpleName();
  private TelephonyManager manager;
  private Context context;

  private String getSimSerialNumber() throws Exception {
    // Use reflection to call SysManager.getSimSerialNumber()
    Class<?> sysManagerClass = Class.forName("com.handpoint.api.privateops.SysManager");
    Method getSIMInfoMethod = sysManagerClass.getDeclaredMethod("getSIMInfo");
    String[] simInfo = (String[]) getSIMInfoMethod.invoke(null);
    return simInfo[2];
  }

  private String getSimSerialNumberWithFallback() throws Exception {
    try {
      String serialNumber = manager.getSimSerialNumber();
      if (serialNumber != null && !serialNumber.isEmpty()) {
        return serialNumber;
      }
    } catch (Exception ex) {
      this.logger.warning("Error getting SIM serial number: " + ex.getMessage());
    }
    return this.getSimSerialNumber();
  }

  private void addSimInformation(JSONObject result) throws JSONException {
    try {
      result.put(SIM_SERIAL_NUMBER_KEY, getSimSerialNumberWithFallback());
      result.put(PHONE_NUMBER_KEY, manager.getLine1Number());
      result.put(DEVICE_SOFTWARE_VERSION_KEY, manager.getDeviceSoftwareVersion());
      result.put(DEVICE_ID_KEY, manager.getDeviceId());
      result.put(SUBSCRIBER_ID_KEY, manager.getSubscriberId());
    } catch (Exception ex) {
      this.logger.warning("Error getting Sim Information :" + ex.getMessage());
    }
  }

  private void addGeneralInformation(JSONObject result) throws JSONException {
    result.put(CARRIER_NAME_KEY, manager.getSimOperatorName());
    result.put(COUNTRY_CODE_KEY, manager.getSimCountryIso());
    result.put(DATA_ACTIVITY_KEY, manager.getDataActivity());
    result.put(PHONE_TYPE_KEY, manager.getPhoneType());
    result.put(SIM_STATE_KEY, manager.getSimState());
  }

  private void addCarrierInformation(JSONObject result) throws JSONException {
    String simOperator = manager.getSimOperator();
    if (simOperator != null && simOperator.length() >= 3) {
      result.put(MCC_KEY, simOperator.substring(0, 3));
      result.put(MNC_KEY, simOperator.substring(3));
    }
  }

  private JSONObject buildResult() throws JSONException {
    JSONObject result = new JSONObject();

    addSimInformation(result);
    addGeneralInformation(result);
    addCarrierInformation(result);

    return result;
  }

  @Override
  public void initialize(JSONArray args, CallbackContext callbackContext, CordovaInterface cordova,
      CordovaPlugin cordovaPlugin) {
    super.initialize(args, callbackContext, cordova, cordovaPlugin);
    this.context = this.cordova.getActivity().getApplicationContext();
    this.manager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
  }

  /**
   * Executes the process of gathering SIM information and sends the result back
   * through the callback context
   *
   * @throws JSONException If there is an issue with JSON processing.
   */
  @Override
  public void execute() throws JSONException {
    JSONObject result = buildResult();
    callbackContext.success(result);
  }

}
