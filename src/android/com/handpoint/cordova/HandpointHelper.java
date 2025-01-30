package com.handpoint.cordova;

import android.content.Context;

import com.handpoint.api.HandpointCredentials;
import com.handpoint.api.Hapi;
import com.handpoint.api.HapiFactory;
import com.handpoint.api.HapiManager;
import com.handpoint.api.Settings;
import com.handpoint.api.shared.AuthenticationResponse;
import com.handpoint.api.shared.CardBrands;
import com.handpoint.api.shared.CardTokenizationData;
import com.handpoint.api.shared.ConnectionMethod;
import com.handpoint.api.shared.ConnectionStatus;
import com.handpoint.api.shared.ConverterUtil;
import com.handpoint.api.shared.Currency;
import com.handpoint.api.shared.Device;
import com.handpoint.api.shared.DeviceStatus;
import com.handpoint.api.shared.EventHandler;
import com.handpoint.api.shared.Events;
import com.handpoint.api.shared.HardwareStatus;
import com.handpoint.api.shared.LogLevel;
import com.handpoint.api.shared.NetworkStatus;
import com.handpoint.api.shared.PrintError;
import com.handpoint.api.shared.ReportConfiguration;
import com.handpoint.api.shared.SignatureRequest;
import com.handpoint.api.shared.StatusInfo;
import com.handpoint.api.shared.TransactionResult;
import com.handpoint.api.shared.TransactionType;
import com.handpoint.api.shared.TypeOfResult;
import com.handpoint.api.shared.auth.HapiMPosAuthResponse;
import com.handpoint.api.shared.i18n.SupportedLocales;
import com.handpoint.api.shared.operations.OperationDto;
import com.handpoint.api.shared.operations.Operations;
import com.handpoint.api.shared.options.MerchantAuthOptions;
import com.handpoint.api.shared.options.MoToOptions;
import com.handpoint.api.shared.options.Options;
import com.handpoint.api.shared.options.RefundOptions;
import com.handpoint.api.shared.options.SaleOptions;
import com.handpoint.api.shared.OperationStartResult;
import com.handpoint.api.shared.options.RefundReversalOptions;
import com.handpoint.api.shared.options.SaleReversalOptions;
import com.handpoint.api.shared.resumeoperation.ResumeCallback;
import com.handpoint.api.shared.CustomData;
import com.handpoint.api.shared.CustomDataCallback;
import com.handpoint.api.shared.dependantoperations.ResumeDependantOperation;
import com.handpoint.api.shared.dependantoperations.DependantOperationDTO;
import com.handpoint.api.shared.dependantoperations.DependantOperationAmount;


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
import java.util.logging.Logger;

public class HandpointHelper implements Events.PosRequired, Events.Status, Events.Log, Events.TransactionStarted,
    Events.AuthStatus, Events.MessageHandling, Events.PrinterEvents, Events.ReportResult, Events.CardLanguage,
    Events.PhysicalKeyboardEvent, Events.CardBrandDisplay, Events.Misc, Events.CardTokenization, Events.ReceiptEvent,
    Events.ReceiptUploadingEvent, Events.UnattendedModeEvent, Events.PasswordProtectionEvent, Events.LocaleEvent,
    Events.ScreenBrightnessEvent, Events.TransactionResultEnricher, Events.DependantOperationEvent {

  private static final String TAG = HandpointHelper.class.getSimpleName();
  private final String SET_KIOSK_MODE_COMMAND = "setKioskMode";
  private final String SET_LOCALE_COMMAND = "setLocale";
  private final String SET_PASSWORD_PROTECTION_COMMAND = "setPasswordProtection";
  private final String SET_SCREEN_BRIGHTNESS_COMMAND = "setScreenBrightness";

  Hapi api;
  Device device;
  CallbackContext callbackContext;
  Context context;
  ResumeCallback resumeTokenizedOperationCallback;
  CustomDataCallback resumeEnrichOperationCallback;
  ResumeDependantOperation resumeDependantOperationCallback;
  SysManagerWrapper sysManagerWrapper;
  private OperationState currentOperationState;
  private Logger logger;

  public HandpointHelper(Context context) {
    this.context = context;
    this.resumeTokenizedOperationCallback = null;
    this.resumeEnrichOperationCallback = null;
    this.resumeDependantOperationCallback = null;
    this.sysManagerWrapper = new SysManagerWrapper();
    this.logger = Logger.getLogger("App-Detailed-Logger");
  }

  public void printDetailedLog(CallbackContext callbackContext, JSONObject params) {
    try {
      String log = params.getString("log");
      Logger.getLogger("App-Detailed-Logger").warning("***[APP] -> " + log);
      callbackContext.success("ok");
    } catch (Exception e) {
      callbackContext.error("printDetailedLog Error Message ->  " + e.getMessage());
      callbackContext.error("printDetailedLog Error Cause-> " + e.getCause());
    }
  }

  // An Android Context is required to be able to handle bluetooth
  public void setup(CallbackContext callbackContext, JSONObject params) throws Throwable {
    String sharedSecret = null;
    String cloudApiKey = null;
    boolean supportsMoto = false;
    boolean enrichTransactionResult = false;
    HandpointCredentials handpointCredentials;
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

    try {
      supportsMoto = params.getBoolean("supportsMoto");
    } catch (JSONException ex) {
    }

    if (supportsMoto) {
      try {
        cloudApiKey = params.getString("cloudApiKey");
      } catch (JSONException ex) {
      }
      handpointCredentials = new HandpointCredentials(sharedSecret, cloudApiKey);
    } else {
      handpointCredentials = new HandpointCredentials(sharedSecret);
    }

    try {
      enrichTransactionResult = params.getBoolean("enrichTransactionResult");
    } catch (JSONException ex) {
    }

    if (enrichTransactionResult) {
      // register the implementation of the enrichTransactionResult interface
      this.api = HapiFactory.getAsyncInterface(this, this.context, handpointCredentials, settings, this);
    } else {
      this.api = HapiFactory.getAsyncInterface(this, this.context, handpointCredentials, settings);
    }

    this.setEventsHandler();
    callbackContext.success("ok");
  }

  public void sale(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      boolean tokenize = params.optString("tokenize", "false").equalsIgnoreCase("true");
      SaleOptions options = this.getOptions(params, SaleOptions.class);
      BigInteger amount = new BigInteger(params.getString("amount"));
      Currency currency = Currency.parse(params.getInt("currency"));
      this.currentOperationState = new OperationState(Operations.sale, amount, currency, options);

      OperationStartResult result = tokenize
          ? (this.api.tokenizedOperation(amount, currency, options))
          // ? (options != null ? this.api.tokenizedOperation(amount, currency, options) :
          // this.api.tokenizedOperation(amount, currency))
          : (options != null ? this.api.sale(amount, currency, options) : this.api.sale(amount, currency));

      if (result.getOperationStarted()) {
        callbackContext.success(result.getTransactionReference());
      } else {
        callbackContext.error("Can't send sale operation to device");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send sale operation to device. Incorrect parameters");
    }
  }

  public void saleReversal(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      OperationStartResult result;
      SaleReversalOptions options = new SaleReversalOptions(this.getOptions(params, MerchantAuthOptions.class));
      if (options != null) {
        result = this.api.saleReversal(new BigInteger(params.getString("amount")),
            Currency.parse(params.getInt("currency")), params.getString("originalTransactionID"), options);
      } else {
        result = this.api.saleReversal(new BigInteger(params.getString("amount")),
            Currency.parse(params.getInt("currency")), params.getString("originalTransactionID"));
      }

      if (result.getOperationStarted()) {
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
      OperationStartResult result;
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

      if (result.getOperationStarted()) {
        callbackContext.success(result.getTransactionReference());
      } else {
        callbackContext.error("Can't send refund operation to device");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send refund operation to device. Incorrect parameters");
    }
  }

  public void automaticRefund(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      OperationStartResult result;
      MoToOptions options = this.getOptions(params, MoToOptions.class);
      String originalTxnid = params.getString("originalTransactionID");
      if (options != null) {
        result = this.api.automaticRefund(new BigInteger(params.getString("amount")),
            Currency.parse(params.getInt("currency")), originalTxnid, options);
      } else {
        result = this.api.automaticRefund(new BigInteger(params.getString("amount")),
            Currency.parse(params.getInt("currency")), originalTxnid);
      }

      if (result.getOperationStarted()) {
        callbackContext.success(result.getTransactionReference());
      } else {
        callbackContext.error("Can't send refund operation to device");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send refund operation to device. Incorrect parameters");
    }
  }

  public void refundReversal(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      OperationStartResult result;
      RefundReversalOptions options = new RefundReversalOptions(this.getOptions(params, MerchantAuthOptions.class));
      if (options != null) {
        result = this.api.refundReversal(new BigInteger(params.getString("amount")),
            Currency.parse(params.getInt("currency")), params.getString("originalTransactionID"), options);
      } else {
        result = this.api.refundReversal(new BigInteger(params.getString("amount")),
            Currency.parse(params.getInt("currency")), params.getString("originalTransactionID"));
      }

      if (result.getOperationStarted()) {
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
      OperationStartResult result;
      Options options = this.getOptions(params, Options.class);
      if (options != null) {
        result = this.api.tokenizeCard(options);
      } else {
        result = this.api.tokenizeCard();
      }

      if (result.getOperationStarted()) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't send tokenizeCard operation to device");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send tokenizeCard operation to device. Incorrect parameters");
    }
  }

  /* MoTo operations */
  public void motoSale(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      OperationStartResult result;
      MoToOptions options = this.getOptions(params, MoToOptions.class);
      if (options != null) {
        result = this.api.motoSale(new BigInteger(params.getString("amount")),
            Currency.parse(params.getInt("currency")),
            options);
      } else {
        result = this.api.motoSale(new BigInteger(params.getString("amount")),
            Currency.parse(params.getInt("currency")));
      }

      if (result.getOperationStarted()) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't send motoSale operation to the api");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send motoSale operation to the api. Incorrect parameters");
    }
  }

  public void motoRefund(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      OperationStartResult result;
      MoToOptions options = this.getOptions(params, MoToOptions.class);
      String originalTxnid = params.getString("originalTransactionID");
      boolean hasAmount = params.has("amount");
      // will always bring the originalTransactionID
      if (options != null) {
        result = this.api.motoRefund(new BigInteger(params.getString("amount")),
            Currency.parse(params.getInt("currency")), originalTxnid, options);
      } else {
        result = this.api.motoRefund(new BigInteger(params.getString("amount")),
            Currency.parse(params.getInt("currency")), originalTxnid);
      }

      if (result.getOperationStarted()) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't send motoRefund operation to the api");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send motoRefund operation to the api. Incorrect parameters");
    }
  }

  public void motoReversal(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      OperationStartResult result;
      result = this.api.motoReversal(params.getString("originalTransactionID"));
      if (result.getOperationStarted()) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't send motoReversal operation to the api");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send motoReversal operation to the api. Incorrect parameters");
    }
  }

  public void motoPreauthorization(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      OperationStartResult result;
      BigInteger amount = new BigInteger(params.getString("amount"));
      Currency currency = Currency.parse(params.getInt("currency"));

      MoToOptions options = this.getOptions(params, MoToOptions.class);

      if (options != null) {
        result = this.api.motoPreauthorization(amount, currency, options);
      } else {
        result = this.api.motoPreauthorization(amount, currency);
      }

      if (result.getOperationStarted()) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't send manual entry preAuthorization operation to the api");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send manual entry preAuthorization operation to the api. Incorrect parameters");
    }
  }
  /* End MoTo operations */

  public void preAuthorization(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      OperationStartResult result;
      BigInteger amount = new BigInteger(params.getString("amount"));
      Currency currency = Currency.parse(params.getInt("currency"));
      MerchantAuthOptions options = this.getOptions(params, MerchantAuthOptions.class);

      if (options != null) {
        result = this.api.preAuthorization(amount, currency, options);
      } else {
        result = this.api.preAuthorization(amount, currency);
      }

      if (result.getOperationStarted()) {
        callbackContext.success(result.getTransactionReference());
      } else {
        callbackContext.error("Can't send sale operation to device");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send preAuthorization operation to the api. Incorrect parameters");
    }
  }

  public void preAuthorizationReversal(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      OperationStartResult result;
      String originalTransactionID = params.getString("originalTransactionID");
      Options options = this.getOptions(params, Options.class);

      if (options != null) {
        result = this.api.preAuthorizationReversal(originalTransactionID, options);
      } else {
        result = this.api.preAuthorizationReversal(originalTransactionID);
      }

      if (result.getOperationStarted()) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't send preAuthorizationReversal operation to device");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send preAuthorizationReversal operation to device. Incorrect parameters");
    }
  }

  public void preAuthorizationIncrease(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      OperationStartResult result;
      BigInteger amount = new BigInteger(params.getString("amount"));
      Currency currency = Currency.parse(params.getInt("currency"));
      BigInteger tipAmount = params.has("tipAmount") ? new BigInteger(params.getString("tipAmount")) : null;
      String originalTransactionID = params.getString("originalTransactionID");
      Options options = this.getOptions(params, Options.class);

      if (options != null) {
        result = this.api.preAuthorizationIncrease(amount, currency, originalTransactionID, options);
      } else {
        result = this.api.preAuthorizationIncrease(amount, currency, originalTransactionID);
      }

      if (result.getOperationStarted()) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't send preAuthorizationIncrease operation to the api");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send preAuthorizationIncrease operation to the api. Incorrect parameters");
    }
  }

  public void preAuthorizationCapture(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      OperationStartResult result;
      BigInteger amount = new BigInteger(params.getString("amount"));
      Currency currency = Currency.parse(params.getInt("currency"));
      BigInteger tipAmount = params.has("tipAmount") ? new BigInteger(params.getString("tipAmount")) : null;
      String originalTransactionID = params.getString("originalTransactionID");
      Options options = this.getOptions(params, Options.class);

      if (options != null) {
        result = this.api.preAuthorizationCapture(amount, currency, originalTransactionID, options);
      } else {
        result = this.api.preAuthorizationCapture(amount, currency, originalTransactionID);
      }

      if (result.getOperationStarted()) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Can't send preAuthorizationCapture operation to the api");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't send preAuthorizationCapture operation to the api. Incorrect parameters");
    }
  }

  public void resumeTokenizedOperation(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      BigInteger amount = new BigInteger(params.getString("amount"));
      Currency currency = Currency.parse(params.getInt("currency"));

      if (this.resumeTokenizedOperationCallback != null) {
        OperationDto operation = null;
        if (currentOperationState != null) {
          switch (currentOperationState.type) {
            case sale:
              SaleOptions saleOptions = this.getOptions(params, SaleOptions.class);
              operation = new OperationDto.Sale(amount, currency, saleOptions);
              break;
            case refund:
              RefundOptions refundOptions = this.getOptions(params, RefundOptions.class);
              operation = new OperationDto.Refund(amount, currency, currentOperationState.originalTransactionId,
                  (RefundOptions) refundOptions);
              break;
            default:
              throw new UnsupportedOperationException("Resume not supported for operation: ");
          }
        } else {
          // For cloud operations we only receive the cardTokenized with sale operations
          // currentOperationState is null because sale() or refund method() are not executed previously
          this.logger.info("[resumeTokenizedOperation] cloud operation; params:" + params.toString());
          SaleOptions saleOptions = this.getOptions(params, SaleOptions.class);
          operation = new OperationDto.Sale(amount, currency, saleOptions);
        }
        if (operation != null) {
          this.resumeTokenizedOperationCallback.resume(operation);
          callbackContext.success("ok");
        }
      } else {
        callbackContext.error("Can't resume tokenized operation. No operation to resume");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't resume tokenized operation. Incorrect parameters");
    } finally {
      this.currentOperationState = null;
      this.resumeTokenizedOperationCallback = null;
    }
  }

  public void cancelTokenizedOperation(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.resumeTokenizedOperationCallback != null) {
      this.resumeTokenizedOperationCallback.cancel();
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't cancel tokenized operation. No operation to cancel");
    }
    this.currentOperationState = null;
    this.resumeTokenizedOperationCallback = null;
  }

  public void disableNavigationBar(CallbackContext callbackContext, JSONObject params) throws Throwable {
    this.setNavigationBarStatus(callbackContext, false);
  }

  public void enableNavigationBar(CallbackContext callbackContext, JSONObject params) throws Throwable {
    this.setNavigationBarStatus(callbackContext, true);
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

  public void getDeviceInfo(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      DeviceInfoBean deviceInfo = sysManagerWrapper.getDeviceInfo();
      String jsonString = deviceInfo.toJson().toString();
      callbackContext.success(jsonString);
    } catch (Exception ex) {
      callbackContext.error("Can't execute getDeviceInfo. Error: " + ex.getMessage());
    }
  }

  public void printReceipt(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      this.api.printReceipt(params.getString("receipt"));
      callbackContext.success("ok");
    } catch (JSONException ex) {
      callbackContext.error("Can't execute printReceipt. Incorrect parameters");
    }
  }

  public void getTransactionsReport(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      ReportConfiguration config = this.getOptions(params, ReportConfiguration.class);
      config.setCurrency(Currency.parse(params.getInt("currency")));
      this.api.getTransactionsReport(config);
      callbackContext.success("ok");
    } catch (JSONException ex) {
      callbackContext.error("Can't execute getTransactionsReport. Incorrect parameters");
    }
  }

  public void getTransactionStatus(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      this.api.getTransactionStatus(params.getString("transactionReference"));
      callbackContext.success("ok");
    } catch (JSONException ex) {
      callbackContext.error("Can't execute getTransactionStatus. Incorrect parameters");
    }
  }

  public void mposAuth(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      Class auth = Class.forName("com.handpoint.api.privateops.HapiMposAuthentication");
      Method authMethod = auth.getDeclaredMethod("authenticateMPos", HapiMPosAuthResponse.class, Context.class);

      HapiMPosAuthResponse authenticationResponseHandler = new HapiMPosAuthResponse() {
        @Override
        public void setAuthenticationResult(AuthenticationResponse oneThing) {
          callbackContext.success("ok");
          authStatus(oneThing);
        }
      };
      authMethod.invoke(auth, authenticationResponseHandler, this.context);
    } catch (Exception e) {
      callbackContext.error("Auth Error -> Method not implemented " + e.getMessage());
      callbackContext.error("Auth Error -> " + e.getCause());
    }
  }

  public void updateWebView(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      Class updateWebViewClass = Class.forName("com.handpoint.api.privateops.UpdateWebView");
      Method authMethod = updateWebViewClass.getDeclaredMethod("update");

      EventHandler.getInstance().registerEventsDelegate(this);

      authMethod.invoke(updateWebViewClass);
      callbackContext.success("ok");
    } catch (Exception e) {
      callbackContext.error("UpdateWebView Error -> Method not implemented " + e.getMessage());
      callbackContext.error("UpdateWebView Error -> " + e.getCause());
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
    this.logger.info("***[APP] -> [perf-event] EndOfTransaction Serialization start");
    SDKEvent event = new SDKEvent("endOfTransaction");
    // print transaction result before serializing it
    if (transactionResult != null) {
      Logger.getLogger("App-Detailed-Logger")
          .warning("***[APP] -> endOfTransaction received: " + transactionResult.toJSON());
      event.put("transactionResult", transactionResult);
      event.put("device", device);
      PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
      result.setKeepCallback(true);
      if (this.callbackContext != null) {
        this.callbackContext.sendPluginResult(result);
      }
      this.logger.info("***[APP] -> [perf-event] EndOfTransaction Serialization end");
    } else {
      Logger.getLogger("App-Detailed-Logger").warning("***[APP] -> endOfTransaction received: null");
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

  @Override
  public void cardTokenized(ResumeCallback callback, CardTokenizationData cardTokenizationData) {
    this.resumeTokenizedOperationCallback = callback; // save the callback to resume the operation (in
    // "resumeTokenizedSale" method)
    this.logger.info("***[APP] -> [perf-event] Card Tokenization Serialization start");
    SDKEvent event = new SDKEvent("cardTokenized");
    event.put("callback", callback);
    event.put("cardTokenizationData", cardTokenizationData);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
    this.logger.info("***[APP] -> [perf-event] Card Tokenization Serialization end");
  }

  @Override
  public void dependantRefundReceived(BigInteger amount, Currency currency, String originalTransactionId, ResumeDependantOperation resumeDependantOperation) {
    this.resumeDependantOperationCallback = resumeDependantOperation;
    this.logger.info("***[APP] -> Dependant Refund Serialization start");
    SDKEvent event = new SDKEvent("dependantRefundReceived");
    event.put("amount", amount.toString());
    event.put("currency", currency);
    event.put("originalTransactionId", originalTransactionId);
    event.put("resumeDependantOperation", resumeDependantOperation);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
    this.logger.info("***[APP] -> Dependant Refund Serialization end");
  }

  @Override
  public void dependantReversalReceived(String originalTransactionId, ResumeDependantOperation resumeDependantOperation) {
    this.resumeDependantOperationCallback = resumeDependantOperation;
    this.logger.info("***[APP] -> Dependant Reversal Serialization start");
    SDKEvent event = new SDKEvent("dependantReversalReceived");
    event.put("originalTransactionId", originalTransactionId);
    event.put("resumeDependantOperation", resumeDependantOperation);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
    this.logger.info("***[APP] -> Dependant Reversal Serialization end");
  }

  public void executeDependantOperation(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      BigInteger amount = new BigInteger(params.getString("amount"));
      Currency currency = Currency.parse(params.getInt("currency"));
      String originalTransactionId = params.getString("originalTransactionId");
      String operation = params.getString("operation");

      if (this.resumeDependantOperationCallback != null) {
        if (operation != null) {
          switch (operation) {
            case "refund":
              DependantOperationDTO.Refund operationRefund = new DependantOperationDTO.Refund(new DependantOperationAmount(amount), currency, originalTransactionId);
              this.resumeDependantOperationCallback.executeDependantOperation(operationRefund);
              callbackContext.success("ok");
              break;
            case "saleReversal":
            case "refundReversal":
              DependantOperationDTO.Reversal operationReversal = new DependantOperationDTO.Reversal(new DependantOperationAmount(amount), currency, originalTransactionId);
              this.resumeDependantOperationCallback.executeDependantOperation(operationReversal);
              callbackContext.success("ok");
              break;
            default:
              throw new UnsupportedOperationException("Execute not supported for operation: ");
          }
        }
      } else {
        callbackContext.error("Can't execute dependant operation. No dependant operation to resume");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't execute dependant operation. Incorrect parameters");
    } finally {
      this.currentOperationState = null;
      this.resumeDependantOperationCallback = null;
    }
  }

  public void finishDependantOperationWithoutCardOperation(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.resumeDependantOperationCallback != null) {
      this.resumeDependantOperationCallback.finishWithoutCardOperation();
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't finish without card. No operation to finish");
    }
    this.resumeDependantOperationCallback = null;
  }

  public void cancelDependantOperation(CallbackContext callbackContext, JSONObject params) throws Throwable {
    if (this.resumeDependantOperationCallback != null) {
      this.resumeDependantOperationCallback.cancel();
      callbackContext.success("ok");
    } else {
      callbackContext.error("Can't cancel dependant operation. No operation to cancel");
    }
    this.resumeDependantOperationCallback = null;
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
  public void receiptIsReady(String guid, String merchantReceipt, String customerReceipt) {
    SDKEvent event = new SDKEvent("receiptsReady");
    event.put("merchantReceipt", merchantReceipt);
    event.put("customerReceipt", customerReceipt);
    event.put("guid", guid);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  @Override
  public void receiptsUploaded(String guid, String merchantReceiptUrl, String customerReceiptUrl) {
    SDKEvent event = new SDKEvent("receiptsUploaded");
    event.put("merchantReceiptUrl", merchantReceiptUrl);
    event.put("customerReceiptUrl", customerReceiptUrl);
    event.put("guid", guid);
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
  public void transactionStarted(TransactionType type, BigInteger amount, Currency currency,
      String transactionReference) {
    SDKEvent event = new SDKEvent("transactionStarted");
    event.put("type", type.toString());
    event.put("amount", amount.toString());
    event.put("currency", currency.getAlpha());
    event.put("transactionReference", transactionReference);
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
    JSONObject object = null;
    if (params.has("options") && params.get("options") instanceof JSONObject) {
      object = (JSONObject) params.get("options");
    } else {
      object = new JSONObject();
    }
    return ConverterUtil.getModelObjectFromJSON(object.toString(), tClass);
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

  @Override
  public void reportResult(TypeOfResult type, String report, DeviceStatus status, Device device) {
    SDKEvent event = new SDKEvent("reportResult");
    event.put("htmlReport", report);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  @Override
  public void unattendedModeStatusUpdate(boolean b) {
    this.sendControlCommand(this.SET_KIOSK_MODE_COMMAND, Boolean.toString(b));
  }

  @Override
  public void localeStatusUpdate(String s) {
    this.sendControlCommand(this.SET_LOCALE_COMMAND, s);
  }

  @Override
  public void passwordProtectionStatusUpdate(boolean b) {
    this.sendControlCommand(this.SET_PASSWORD_PROTECTION_COMMAND, Boolean.toString(b));
  }

  @Override
  public void screenBrightnessChanged(int maximum, int minimum) {
    this.sendControlCommand(this.SET_SCREEN_BRIGHTNESS_COMMAND, maximum + "," + minimum);
  }

  public void cardLanguage(SupportedLocales locale) {
    SDKEvent event = new SDKEvent("cardLanguage");
    event.put("locale", locale);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  public void onKeyPressed(String key) {
    SDKEvent event = new SDKEvent("onKeyPressed");
    event.put("key", key);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  public void supportedCardBrands(List<? extends CardBrands> cardBrandsList) {
    SDKEvent event = new SDKEvent("supportedCardBrands");
    event.put("cardBrandsList", ConverterUtil.convertToJSON(cardBrandsList));
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  public void readCard(CardBrands usedCard) {
    SDKEvent event = new SDKEvent("readCard");
    event.put("usedCard", usedCard);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  public void webViewUpdate(boolean success) {
    SDKEvent event = new SDKEvent("webViewUpdated");
    event.put("success", new Boolean(success).toString());
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  public void setApn(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      Class sysManager = Class.forName("com.handpoint.api.privateops.SysManager");
      Method setApnMethod = sysManager.getDeclaredMethod("setApn", String.class, String.class, String.class,
          String.class);
      Object result = setApnMethod.invoke(sysManager, params.getString("name"), params.getString("apn"),
          params.getString("user"), params.getString("passwd"));
      if (Boolean.class.cast(result)) {
        callbackContext.success("ok");
      } else {
        callbackContext.error("Error setting APN");
      }
    } catch (Exception e) {
      callbackContext.error("setApn Error -> Method not implemented " + e.getMessage());
      callbackContext.error("setApn Error -> " + e.getCause());
    }
  }

  public void reboot(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      Class sysManager = Class.forName("com.handpoint.api.privateops.SysManager");
      Method rebootMethod = sysManager.getDeclaredMethod("reboot");
      rebootMethod.invoke(sysManager);
      callbackContext.success("ok");
    } catch (Exception e) {
      callbackContext.error("reboot Error -> Method not implemented " + e.getMessage());
      callbackContext.error("reboot Error -> " + e.getCause());
    }
  }

  public void blockUiBars(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      Class sysManager = Class.forName("com.handpoint.api.privateops.SysManager");
      Method blockUiBarsMethod = sysManager.getDeclaredMethod("blockUiBars");
      blockUiBarsMethod.invoke(sysManager);
      callbackContext.success("ok");
    } catch (Exception e) {
      callbackContext.error("blockUiBars Error -> Method not implemented " + e.getMessage());
      callbackContext.error("blockUiBars Error -> " + e.getCause());
    }
  }

  public void unblockUiBars(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      Class sysManager = Class.forName("com.handpoint.api.privateops.SysManager");
      Method unblockUiBarsMethod = sysManager.getDeclaredMethod("unblockUiBars");
      unblockUiBarsMethod.invoke(sysManager);
      callbackContext.success("ok");
    } catch (Exception e) {
      callbackContext.error("unblockUiBars Error -> Method not implemented " + e.getMessage());
      callbackContext.error("unblockUiBars Error -> " + e.getCause());
    }
  }

  public void enableVolumeKeys(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      Class sysManager = Class.forName("com.handpoint.api.privateops.SysManager");
      Method enableVolumeKeysMethod = sysManager.getDeclaredMethod("enableVolumeKeys");
      enableVolumeKeysMethod.invoke(sysManager);
      callbackContext.success("ok");
    } catch (Exception e) {
      callbackContext.error("enableVolumeKeys Error -> Method not implemented " + e.getMessage());
      callbackContext.error("enableVolumeKeys Error -> " + e.getCause());
    }
  }
  
  public void disableVolumeKeys(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      Class sysManager = Class.forName("com.handpoint.api.privateops.SysManager");
      Method disableVolumeKeysMethod = sysManager.getDeclaredMethod("disableVolumeKeys");
      disableVolumeKeysMethod.invoke(sysManager);
      callbackContext.success("ok");
    } catch (Exception e) {
      callbackContext.error("disableVolumeKeys Error -> Method not implemented " + e.getMessage());
      callbackContext.error("disableVolumeKeys Error -> " + e.getCause());
    }
  }

  public void hasWifiModule(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      Class sysManager = Class.forName("com.handpoint.api.privateops.SysManager");
      Method hasWifiModuleMethod = sysManager.getDeclaredMethod("hasWifiModule");
      Object result = hasWifiModuleMethod.invoke(sysManager);
      callbackContext.success(String.valueOf(result));
    } catch (Exception e) {
      callbackContext.error("hasWifiModule Error -> Method not implemented " + e.getMessage());
      callbackContext.error("hasWifiModule Error -> " + e.getCause());
    }
  }

  public void hasPrinterModule(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      Class sysManager = Class.forName("com.handpoint.api.privateops.SysManager");
      Method hasPrinterModuleMethod = sysManager.getDeclaredMethod("hasPrinterModule");
      Object result = hasPrinterModuleMethod.invoke(sysManager);
      callbackContext.success(String.valueOf(result));
    } catch (Exception e) {
      callbackContext.error("hasPrinterModule Error -> Method not implemented " + e.getMessage());
      callbackContext.error("hasPrinterModule Error -> " + e.getCause());
    }
  }

  public void hasPhysicalKeyboardModule(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      Class sysManager = Class.forName("com.handpoint.api.privateops.SysManager");
      Method hasPhysicalKeyboardModuleMethod = sysManager.getDeclaredMethod("hasPhysicalKeyboardModule");
      Object result = hasPhysicalKeyboardModuleMethod.invoke(sysManager);
      callbackContext.success(String.valueOf(result));
    } catch (Exception e) {
      callbackContext.error("hasPhysicalKeyboardModule Error -> Method not implemented " + e.getMessage());
      callbackContext.error("hasPhysicalKeyboardModule Error -> " + e.getCause());
    }
  }

  public void getPaxSerialNumber(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      Class sysManager = Class.forName("com.handpoint.api.privateops.SysManager");
      Method getPaxSerialNumberMethod = sysManager.getDeclaredMethod("getPaxSerialNumber");
      Object result = getPaxSerialNumberMethod.invoke(sysManager);
      callbackContext.success(String.valueOf(result));
    } catch (Exception e) {
      callbackContext.error("getPaxSerialNumber Error -> Method not implemented " + e.getMessage());
      callbackContext.error("getPaxSerialNumber Error -> " + e.getCause());
    }
  }

  public void getPaxModel(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      Class sysManager = Class.forName("com.handpoint.api.privateops.SysManager");
      Method getPaxModelMethod = sysManager.getDeclaredMethod("getPaxModel");
      Object result = getPaxModelMethod.invoke(sysManager);
      callbackContext.success(String.valueOf(result));
    } catch (Exception e) {
      callbackContext.error("getPaxModel Error -> Method not implemented " + e.getMessage());
      callbackContext.error("getPaxModel Error -> " + e.getCause());
    }
  }

  public void setBrightness(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      this.sysManagerWrapper.setBrightness(params.getInt("brightness"));
      callbackContext.success("ok");
    } catch (Exception ex) {
      callbackContext.error("Can't execute getDeviceInfo. Error: " + ex.getMessage());
    }
  }

  public void turnOffScreen(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      this.sysManagerWrapper.turnOffScreen();
      callbackContext.success("ok");
    } catch (Exception ex) {
      callbackContext.error("Can't execute turnOffScreen. Error: " + ex.getMessage());
    }
  }

  public void turnOnScreen(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      this.sysManagerWrapper.turnOnScreen();
      callbackContext.success("ok");
    } catch (Exception ex) {
      callbackContext.error("Can't execute turnOnScreen. Error: " + ex.getMessage());
    }
  }

  public void turnOffScreenSaver(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      this.sysManagerWrapper.turnOffScreenSaver();
      callbackContext.success("ok");
    } catch (Exception ex) {
      callbackContext.error("Can't execute turnOffScreenSaver. Error: " + ex.getMessage());
    }
  }

  public void turnOnScreenSaver(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      this.sysManagerWrapper.turnOnScreenSaver();
      callbackContext.success("ok");
    } catch (Exception ex) {
      callbackContext.error("Can't execute turnOnScreenSaver. Error: " + ex.getMessage());
    }
  }

  /**
   * Resumes a previously interrupted enrich operation by providing the required custom data.
   * 
   * @param callbackContext - The context for sending results or errors back to the caller.
   * @param params - A JSON object containing the necessary parameters for resuming the operation, 
   *                 including "loyaltyData" used to create the custom data object.
   * 
   * @throws Throwable - May throw exceptions during the execution of the enrichment process.
   */
  public void resumeEnrichOperation(CallbackContext callbackContext, JSONObject params) throws Throwable {
    try {
      if (this.resumeEnrichOperationCallback != null) {
        String loyaltyData = params.getString("loyaltyData");
        CustomData customData = CustomData.create(loyaltyData);
        this.resumeEnrichOperationCallback.resume(customData);
      } else {
        callbackContext.error("Can't resume enrich operation. No enrich operation to resume");
      }
    } catch (JSONException ex) {
      callbackContext.error("Can't resume enrich operation. Incorrect parameters");
    }
    this.resumeEnrichOperationCallback = null;
  }

  /**
   * Handles the enrichment of a transaction result by processing the provided transaction data
   * and preparing it for custom enrichment via a callback.
   * 
   * @param transactionResult  - The transaction result data to be enriched.
   * @param customDataCallback - Callback to handle the enrichment process, allowing customization of the data.
   */
  @Override
  public void enrich(TransactionResult transactionResult, CustomDataCallback customDataCallback) {
    this.resumeEnrichOperationCallback = customDataCallback; // save the callback to resume the enrich operation (in "resumeEnrichOperation" method)
    this.logger.info("***[APP] -> Received enrich transaction result event");
    SDKEvent event = new SDKEvent("enrich");
    event.put("transactionResult", transactionResult);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  @Override
  protected void finalize() {
    this.api.unregisterEventsDelegate(this);
  }

  private void setEventsHandler() {
    // Register class as listener for all events
    this.api.registerEventsDelegate(this);
  }

  private void sendControlCommand(String commandName, Object payload) {
    SDKEvent event = new SDKEvent("controlCommand");
    Logger.getLogger("App-Detailed-Logger").warning("***[APP] -> controlCommand received: " + commandName);
    event.put("command", commandName);
    event.put("payload", payload);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    if (this.callbackContext != null) {
      this.callbackContext.sendPluginResult(result);
    }
  }

  private void setNavigationBarStatus(CallbackContext callbackContext, boolean status) {
    try {
      Class sysManager = Class.forName("com.handpoint.api.privateops.SysManager");
      String methodName = status ? "showNavigationBar" : "hideNavigationBar";
      Method method = sysManager.getDeclaredMethod(methodName);
      Object result = method.invoke(sysManager);
      callbackContext.success("ok");
    } catch (Exception e) {
      callbackContext.error("setNavigationBarStatus Error -> Method not implemented " + e.getMessage());
      callbackContext.error("setNavigationBarStatus Error -> " + e.getCause());
    }
  }

}
