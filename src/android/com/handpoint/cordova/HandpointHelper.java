package com.handpoint.cordova;

import com.handpoint.api.*;
import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.math.BigInteger;

import com.google.gson.*;

public class HandpointHelper implements Events.Required, Events.Status, Events.Log, Events.PendingResults {

  private static final String TAG = HandpointHelper.class.getSimpleName();

  Hapi api;
  Device device;
  CallbackContext callbackContext;
  Context context;

  public HandpointHelper(Context context) {
    this.context = context;
  }

  // An Android Context is required to be able to handle bluetooth
  public void setup(CallbackContext callbackContext, JSONObject params) throws Throwable {
    String sharedSecret = null;

    // Automatic Reconnections are disabled since reconnection is handled in app
    HapiManager.Settings.AutomaticReconnection = false;

    this.api = HapiFactory.getAsyncInterface(this, this.context);
    try {
      sharedSecret = params.getString("sharedSecret");
    } catch (Exception ex) {
    }

    if (sharedSecret != null) {
      this.api.defaultSharedSecret(sharedSecret);
    }

    this.setEventsHandler();
    callbackContext.success("ok");
  }

  public void sale(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.api.sale(new BigInteger(params.getString("amount")), Currency.getCurrency(params.getInt("currency")))) {
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't send sale operation to device");
    }
  }

  public void saleAndTokenizeCard(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.api.saleAndTokenizeCard(new BigInteger(params.getString("amount")),
        Currency.getCurrency(params.getInt("currency")))) {
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't send saleAndTokenizeCard operation to device");
    }
  }

  public void saleReversal(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.api.saleReversal(new BigInteger(params.getString("amount")),
        Currency.getCurrency(params.getInt("currency")), params.getString("originalTransactionID"))) {
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't send saleReversal operation to device");
    }
  }

  public void refund(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.api.refund(new BigInteger(params.getString("amount")), Currency.getCurrency(params.getInt("currency")))) {
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't send refund operation to device");
    }
  }

  public void refundReversal(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.api.refundReversal(new BigInteger(params.getString("amount")),
        Currency.getCurrency(params.getInt("currency")), params.getString("originalTransactionID"))) {
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't send refundReversal operation to device");
    }
  }

  @Deprecated // This operation should be removed
  public void cancelRequest(CallbackContext callbackContext, JSONObject params) throws Throwable {
    callbackContext.error("Can't send cancelRequest operation to device");
  }

  public void tipAdjustment(CallbackContext callbackContext, JSONObject params) throws Throwable {
    // TODO
  }

  public void signatureResult(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.api.signatureResult(params.getBoolean("accepted"))) {
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't send signatureResult operation to device");
    }
  }

  public void connect(CallbackContext callbackContext, JSONObject params) throws Throwable {
    JSONObject device = params.getJSONObject("device");
    this.device = new Device(device.getString("name"), device.getString("address"), device.getString("port"),
        ConnectionMethod.values()[device.getInt("connectionMethod")]);
    if (this.api.connect(this.device)) {
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't connect to device");
    }
  }

  public void disconnect(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.api.disconnect()) {
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't disconnect from device");
    }
  }

  public void setSharedSecret(CallbackContext callbackContext, JSONObject params) throws Throwable {
    this.api.setSharedSecret(params.getString("sharedSecret"));
    callbackContext.success("ok");
  }

  public void setParameter(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.api.setParameter(DeviceParameter.valueOf(params.getString("param")), params.getString("value"))) {
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't send setParameter operation to device");
    }
  }

  public void setLogLevel(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.api.setLogLevel(LogLevel.None.getLogLevel(params.getInt("level")))) {
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't send setLogLevel operation to device");
    }
  }

  public void getDeviceLogs(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.api.getDeviceLogs()) {
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't send getDeviceLogs operation to device");
    }
  }

  public void getPendingTransaction(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.api.getPendingTransaction()) {
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't send getPendingTransaction operation to device");
    }
  }

  public void update(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.api.update()) {
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't send update operation to device");
    }
  }

  public void listDevices(CallbackContext callbackContext, JSONObject params) throws Throwable {
    this.api.listDevices(ConnectionMethod.values()[params.getInt("connectionMethod")]);
    callbackContext.success("ok");
  }

  public void applicationDidGoBackground(CallbackContext callbackContext, JSONObject params) throws Throwable {
    callbackContext.success("ok");
  }

  public void getSDKVersion(CallbackContext callbackContext, JSONObject params) throws Throwable {
    callbackContext.success(HapiManager.getSdkVersion());
  }

  /**
   * Register event handler
   */
  public void eventHandler(CallbackContext callbackContext, JSONObject params) throws Throwable {
    this.callbackContext = callbackContext;

    PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
    result.setKeepCallback(true);
    this.callbackContext.sendPluginResult(result);
  }

  @Override
  public void endOfTransaction(TransactionResult transactionResult, Device device) {
    SDKEvent event = new SDKEvent("endOfTransaction");
    event.put("transactionResult", transactionResult);
    event.put("device", device);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    this.callbackContext.sendPluginResult(result);
  }

  @Override
  public void deviceDiscoveryFinished(List<Device> devices) {
    SDKEvent event = new SDKEvent("deviceDiscoveryFinished");
    event.put("devices", devices);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    this.callbackContext.sendPluginResult(result);
  }

  @Override
  public void signatureRequired(SignatureRequest signatureRequest, Device device) {
    SDKEvent event = new SDKEvent("signatureRequired");
    event.put("merchantReceipt", signatureRequest.getMerchantReceipt());
    event.put("device", device);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    this.callbackContext.sendPluginResult(result);
  }

  /** Status Events */
  @Override
  public void connectionStatusChanged(ConnectionStatus status, Device device) {
    SDKEvent event = new SDKEvent("connectionStatusChanged");
    event.put("status", status);
    event.put("device", device);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    this.callbackContext.sendPluginResult(result);
  }

  @Override
  public void currentTransactionStatus(StatusInfo info, Device device) {
    SDKEvent event = new SDKEvent("currentTransactionStatus");
    event.put("info", info);
    event.put("device", device);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    this.callbackContext.sendPluginResult(result);
  }

  @Override
  public void deviceLogsReady(String logs, Device device) {
    SDKEvent event = new SDKEvent("deviceLogsReady");
    event.put("logs", logs);
    event.put("device", device);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    this.callbackContext.sendPluginResult(result);
  }

  @Override
  public void onMessageLogged(LogLevel level, String message) {
    SDKEvent event = new SDKEvent("onMessageLogged");
    event.put("level", level);
    event.put("message", message);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    this.callbackContext.sendPluginResult(result);
  }

  @Override
  public void pendingTransactionResult(Device device) {
    SDKEvent event = new SDKEvent("pendingTransactionResult");
    event.put("device", device);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    this.callbackContext.sendPluginResult(result);
  }

  @Override
  public void transactionResultReady(TransactionResult transactionResult, Device device) {
    SDKEvent event = new SDKEvent("transactionResultReady");
    event.put("transactionResult", transactionResult);
    event.put("device", device);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    this.callbackContext.sendPluginResult(result);
  }

  protected void finalize() {
    this.api.removeRequiredEventHandler(this);
    this.api.removeStatusNotificationEventHandler(this);
    this.api.removeLogEventHandler(this);
    this.api.removePendingResultsEventHandler(this);
  }

  private void setEventsHandler() {
    // Register class as listener for all events
    this.api.addRequiredEventHandler(this);
    this.api.addStatusNotificationEventHandler(this);
    this.api.addLogEventHandler(this);
    this.api.addPendingResultsEventHandler(this);
  }
}
