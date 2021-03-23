package com.handpoint.cordova;

import android.content.Context;

import com.handpoint.api.HandpointCredentials;
import com.handpoint.api.Hapi;
import com.handpoint.api.HapiFactory;
import com.handpoint.api.HapiManager;
import com.handpoint.api.Settings;
import com.handpoint.api.shared.AuthenticationResponse;
import com.handpoint.api.shared.ConnectionMethod;
import com.handpoint.api.shared.ConnectionStatus;
import com.handpoint.api.shared.ConverterUtil;
import com.handpoint.api.shared.Currency;
import com.handpoint.api.shared.Device;
import com.handpoint.api.shared.Events;
import com.handpoint.api.shared.HapiMPosAuthResponse;
import com.handpoint.api.shared.HardwareStatus;
import com.handpoint.api.shared.LogLevel;
import com.handpoint.api.shared.NetworkStatus;
import com.handpoint.api.shared.PrintError;
import com.handpoint.api.shared.SignatureRequest;
import com.handpoint.api.shared.StatusInfo;
import com.handpoint.api.shared.TransactionResult;
import com.handpoint.api.shared.TransactionType;
import com.handpoint.api.shared.i18n.SupportedLocales;
import com.handpoint.api.shared.options.MerchantAuthOptions;
import com.handpoint.api.shared.options.Options;
import com.handpoint.api.shared.options.RefundOptions;
import com.handpoint.api.shared.options.SaleOptions;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HandpointHelper implements Events.Required, Events.Status, Events.Log, Events.TransactionStarted,
    Events.AuthStatus, Events.MessageHandling, Events.PrinterEvents, Events.CardLanguage {

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
    Settings settings = new Settings();

    // Automatic Reconnections are disabled since reconnection is handled in app
    try {
      settings.automaticReconnection = params.getBoolean("automaticReconnection");
    } catch (JSONException ex) {
      settings.automaticReconnection = true;
    }

    try {
      settings.getReceiptsAsURLs = params.getBoolean("getReceiptsAsURLs");
    } catch (JSONException ex) {
      settings.getReceiptsAsURLs = false;
    }

    try {
      sharedSecret = params.getString("sharedSecret");
    } catch (JSONException ex) {
    }

    HandpointCredentials handpointCredentials = new HandpointCredentials(sharedSecret);

    this.api = HapiFactory.getAsyncInterface(this, this.context, handpointCredentials, settings);

    this.setEventsHandler();
    callbackContext.success("ok");
  }

  public void sale(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      boolean result;
      SaleOptions options = this.getOptions(params, SaleOptions.class);
      if (options != null) {
        result = this.api.sale(new BigInteger(params.getString("amount")), Currency.parse(params.getInt("currency")),
            options);
      } else {
        result = this.api.sale(new BigInteger(params.getString("amount")), Currency.parse(params.getInt("currency")));
      }

      if (result) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't send sale operation to device");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send sale operation to device. Incorrect parameters");
    }
  }

  public void saleAndTokenizeCard(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      boolean result;
      SaleOptions options = this.getOptions(params, SaleOptions.class);
      if (options != null) {
        result = this.api.saleAndTokenizeCard(new BigInteger(params.getString("amount")),
            Currency.parse(params.getInt("currency")), options);
      } else {
        result = this.api.saleAndTokenizeCard(new BigInteger(params.getString("amount")),
            Currency.parse(params.getInt("currency")));
      }

      if (result) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't send saleAndTokenizeCard operation to device");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send saleAndTokenizeCard operation to device. Incorrect parameters");
    }
  }

  public void saleReversal(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      boolean result;
      MerchantAuthOptions options = this.getOptions(params, MerchantAuthOptions.class);
      if (options != null) {
        result = this.api.saleReversal(new BigInteger(params.getString("amount")),
            Currency.parse(params.getInt("currency")), params.getString("originalTransactionID"), options);
      } else {
        result = this.api.saleReversal(new BigInteger(params.getString("amount")),
            Currency.parse(params.getInt("currency")), params.getString("originalTransactionID"));
      }

      if (result) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't send saleReversal operation to device");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send saleReversal operation to device. Incorrect parameters");
    }
  }

  public void refund(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      boolean result;
      RefundOptions options = this.getOptions(params, RefundOptions.class);
      String originalTxnid = params.getString("originalTransactionID");
      if (options != null) {
        if (originalTxnid != null && !originalTxnid.isEmpty()) {
          result = this.api.refund(new BigInteger(params.getString("amount")),
              Currency.parse(params.getInt("currency")), originalTxnid, options);
        } else {
          result = this.api.refund(new BigInteger(params.getString("amount")),
              Currency.parse(params.getInt("currency")), options);
        }
      } else {
        if (originalTxnid != null && !originalTxnid.isEmpty()) {
          result = this.api.refund(new BigInteger(params.getString("amount")),
              Currency.parse(params.getInt("currency")), originalTxnid);
        } else {
          result = this.api.refund(new BigInteger(params.getString("amount")),
              Currency.parse(params.getInt("currency")));
        }
      }

      if (result) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't send refund operation to device");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send refund operation to device. Incorrect parameters");
    }
  }

  public void refundReversal(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      boolean result;
      MerchantAuthOptions options = this.getOptions(params, MerchantAuthOptions.class);
      if (options != null) {
        result = this.api.refundReversal(new BigInteger(params.getString("amount")),
            Currency.parse(params.getInt("currency")), params.getString("originalTransactionID"), options);
      } else {
        result = this.api.refundReversal(new BigInteger(params.getString("amount")),
            Currency.parse(params.getInt("currency")), params.getString("originalTransactionID"));
      }

      if (result) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't send refundReversal operation to device");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send refundReversal operation to device. Incorrect parameters");
    }
  }

  public void tokenizeCard(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      boolean result;
      Options options = this.getOptions(params, Options.class);
      if (options != null) {
        result = this.api.tokenizeCard(options);
      } else {
        result = this.api.tokenizeCard();
      }

      if (result) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't send tokenizeCard operation to device");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send tokenizeCard operation to device. Incorrect parameters");
    }
  }

  @Deprecated // This operation should be removed
  public void cancelRequest(CallbackContext callbackContext, JSONObject params) throws Throwable {
    callbackContext.error("Can't send cancelRequest operation to device");
  }

  public void stopCurrentTransaction(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.api.stopCurrentTransaction()) {
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't sop current transaction");
    }
  }

  public void tipAdjustment(CallbackContext callbackContext, JSONObject params) throws Throwable {
    // TODO
  }

  public void signatureResult(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      if (this.api.signatureResult(params.getBoolean("accepted"))) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't send signatureResult operation to device");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send signatureResult operation to device. Incorrect parameters");
    }
  }

  public void connect(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      JSONObject device = params.getJSONObject("device");
      this.device = new Device(device.getString("name"), device.getString("address"), device.getString("port"),
          ConnectionMethod.values()[device.getInt("connectionMethod")]);
          
      try {
        this.device.setForceReconnect(device.getBoolean("forceReconnect"));
      } catch (JSONException ex) {
        this.device.setForceReconnect(false);
      }

      if (this.api.connect(this.device)) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't connect to device");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't connect to device. Incorrect parameters");
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
    callbackContext.success("ok");
  }

  public void setLogLevel(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      if (this.api.setLogLevel(LogLevel.None.getLogLevel(params.getInt("level")))) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't send setLogLevel operation to device");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send setLogLevel operation to device. Incorrect parameters");
    }
  }

  public void getPendingTransaction(CallbackContext callbackContext, JSONObject params) throws Throwable {
    callbackContext.error("Can't send getPendingTransaction operation to device");
  }

  public void update(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.api.update()) {
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't send update operation to device");
    }
  }

  public void listDevices(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      this.api.searchDevices(ConnectionMethod.values()[params.getInt("connectionMethod")]);
      callbackContext.success("ok");
    } catch (JSONException ex) {
      callbackContext.error("Can't execute listDevices. Incorrect parameters");
    }

  }

  public void applicationDidGoBackground(CallbackContext callbackContext, JSONObject params) throws Throwable {
    callbackContext.success("ok");
  }

  public void getSDKVersion(CallbackContext callbackContext, JSONObject params) throws Throwable {
    callbackContext.success(HapiManager.getSdkVersion());
  }

  public void printReceipt(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      this.api.printReceipt(params.getString("receipt"));
      callbackContext.success("ok");
    } catch (JSONException ex) {
      callbackContext.error("Can't execute printReceipt. Incorrect parameters");
    }
  }

  public void mposAuth(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      Class auth = Class.forName("com.handpoint.api.privateops.HapiMposAuthentication");
      Method authMethod = auth.getDeclaredMethod("authenticateMPos", HapiMPosAuthResponse.class, Context.class);

      HapiMPosAuthResponse authenticationResponseHandler = new HapiMPosAuthResponse() {
        @Override
        public void setAuthenticationResult(AuthenticationResponse oneThing) {
          authStatus(oneThing);
        }
      };
      authMethod.invoke(auth, authenticationResponseHandler, this.context);
    } catch (Exception e) {
      callbackContext.error("Auth Error -> Method not implemented " + e.getMessage());
      callbackContext.error("Auth Error -> " + e.getCause());
    }
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

  public void setLocale(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      this.api.setLocale(SupportedLocales.fromString(params.getString("locale")));
      callbackContext.success("ok");
    } catch (JSONException ex) {
      callbackContext.error("Can not execute setLocale. Incorrect parameters");
    }
  }

  @Override
  public void endOfTransaction(TransactionResult transactionResult, Device device) {
    SDKEvent event = new SDKEvent("endOfTransaction");
    event.put("transactionResult", transactionResult);
    event.put("device", device);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  @Override
  public void deviceDiscoveryFinished(List<Device> devices) {
    SDKEvent event = new SDKEvent("deviceDiscoveryFinished");
    event.put("devices", devices);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  @Override
  public void signatureRequired(SignatureRequest signatureRequest, Device device) {
    SDKEvent event = new SDKEvent("signatureRequired");
    event.put("merchantReceipt", signatureRequest.getMerchantReceipt());
    event.put("device", device);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  /**
   * Status Events
   */
  @Override
  public void connectionStatusChanged(ConnectionStatus status, Device device) {
    SDKEvent event = new SDKEvent("connectionStatusChanged");
    event.put("status", status);
    event.put("device", device);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  @Override
  public void networkStatusChanged(NetworkStatus networkStatus, Device device) {
    SDKEvent event = new SDKEvent("networkStatusChanged");
    event.put("networkStatus", networkStatus);
    event.put("device", device);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  @Override
  public void currentTransactionStatus(StatusInfo info, Device device) {
    SDKEvent event = new SDKEvent("currentTransactionStatus");
    event.put("info", info);
    event.put("device", device);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  @Override
  public void deviceLogsReady(String logs, Device device) {
    SDKEvent event = new SDKEvent("deviceLogsReady");
    event.put("logs", logs);
    event.put("device", device);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  @Override
  public void onMessageLogged(LogLevel level, String message) {
    SDKEvent event = new SDKEvent("onMessageLogged");
    event.put("level", level);
    event.put("message", message);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  @Override
  public void transactionResultReady(TransactionResult transactionResult, Device device) {
    SDKEvent event = new SDKEvent("transactionResultReady");
    event.put("transactionResult", transactionResult);
    event.put("device", device);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  @Override
  public void transactionStarted(TransactionType type, BigInteger amount, Currency currency) {
    SDKEvent event = new SDKEvent("transactionStarted");
    event.put("type", type.toString());
    event.put("amount", amount.toString());
    event.put("currency", currency.getAlpha());
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  @Override
  public void authStatus(AuthenticationResponse authStatus) {
    SDKEvent event = new SDKEvent("authStatus");
    event.put("info", authStatus);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  @Override
  public void showMessage(String message, boolean dismissible, int duration) {
    SDKEvent event = new SDKEvent("showMessage");
    event.put("message", message);
    event.put("dismissible", Boolean.toString(dismissible));
    event.put("duration", String.valueOf(duration));
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  @Override
  public void hideMessage(String message) {
    SDKEvent event = new SDKEvent("hideMessage");
    event.put("message", message);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  public void hardwareStatusChanged(HardwareStatus status, ConnectionMethod hardware) {
    SDKEvent event = new SDKEvent("hardwareStatusChanged");
    event.put("status", status);
    event.put("connectionMethod", hardware);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  protected <T> T getOptions(JSONObject params, Class<T> tClass) throws JSONException {
    if (params.has("options")) {
      JSONObject object = (JSONObject) params.get("options");
      return ConverterUtil.getModelObjectFromJSON(object.toString(), tClass);
    } else {
      return null;
    }
  }

  protected static Map<String, String> jsonToMap(JSONObject json) throws JSONException {
    Map<String, String> retMap = new HashMap<String, String>();

    if (json != JSONObject.NULL) {
      retMap = toMap(json);
    }
    return retMap;
  }

  protected static Map<String, String> toMap(JSONObject object) throws JSONException {
    Map<String, String> map = new HashMap<String, String>();

    Iterator<String> keysItr = object.keys();
    while (keysItr.hasNext()) {
      String key = keysItr.next();
      String value = object.get(key).toString();
      map.put(key, value);
    }
    return map;
  }

  @Override
  public void printError(PrintError printError) {
    SDKEvent event = new SDKEvent("printError");
    event.put("error", printError);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  @Override
  public void printSuccess() {
    SDKEvent event = new SDKEvent("printSuccess");
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  public void cardLanguage(SupportedLocales language) {
    SDKEvent event = new SDKEvent("cardLanguage");
    event.put("language", language);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  protected void finalize() {
    this.api.unregisterEventsDelegate(this);
  }

  private void setEventsHandler() {
    // Register class as listener for all events
    this.api.registerEventsDelegate(this);
  }
}
