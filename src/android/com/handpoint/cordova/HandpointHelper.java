package com.handpoint.cordova;

import com.handpoint.api.*;
import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import java.util.List;
import java.math.BigInteger;

public class HandpointHelper implements Events.Required {

  Hapi api;
  Device device;
  CallbackContext callbackContext;

  public HandpointHelper(Context context) {
    initApi(context);
  }

  //An Android Context is required to be able to handle bluetooth
  public void initApi(Context context) {
    String sharedSecret = "0102030405060708091011121314151617181920212223242526272829303132";
    this.api = HapiFactory.getAsyncInterface(this, context).defaultSharedSecret(sharedSecret);
    // The api is now initialized. Yay! we've even set a default shared secret!
    // The shared secret is a unique string shared between the card reader and your mobile application.
    // It prevents other people to connect to your card reader.
    // You have to replace this default shared secret by the one sent by our support team.
    // List devices
  }

  public void sale(CallbackContext callbackContext, String amount) {
    this.callbackContext = callbackContext;
    // User Device SureSwipe3708 68:AA:D2:02:89:B6 
    this.device = new Device("SureSwipe3708", "68:AA:D2:02:89:B6", "1", ConnectionMethod.BLUETOOTH);
    this.api.useDevice(this.device);
    // Initiate a sale for 10.00 in Great British Pound
    this.api.sale(new BigInteger(amount), Currency.GBP);
  }

  @Override
  public void deviceDiscoveryFinished(List<Device> devices) {
    // here you get a list of Bluetooth devices paired with your android device
    for (Device device : devices) {
      // TODO
    }
  }

  @Override
  public void signatureRequired(SignatureRequest signatureRequest, Device device) {
    // You'll be notified here if a sale process needs a signature verification
    // A signature verification is needed if the cardholder uses an MSR card or a chip & signature card
    // This method will not be invoked if a transaction is made with a Chip & PIN card
    // At this step, you should display the merchant receipt to the cardholder on the android device
    // The cardholder must have the possibility to accept or decline the transaction
    // If the cardholder clicks on decline, the transaction is VOID
    // If the cardholder clicks on accept he is then asked to sign electronically the receipt
    this.api.signatureResult(true);
    // This line means that the cardholder ALWAYS accepts to sign the receipt
    // For this sample app we are not going to implement the whole signature process
  }

  @Override
  public void endOfTransaction(TransactionResult transactionResult, Device device) {
    // The object TransactionResult holds the different receipts
    // Other information can be accessed through this object like the transaction ID, the amount...
    this.callbackContext.success(transactionResult.getStatusMessage());
  }

}