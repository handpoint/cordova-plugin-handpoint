# Cordova Handpoint SDK Plugin
Cross-platform [Handpoint](https://www.handpoint.com) SDK for Cordova. This plugin is intended to enable the access to Handpoint Card Readers from any cross-platform application built over Cordova.

You might want to take a look at the [Ionic 2 starter application](https://github.com/handpoint/handpoint-ionic-app) to see a fully functional app using the plugin.

## Installation
You can install handpoint plugin with Cordova CLI, from npm:

```bash
cordova plugin add cordova-plugin-handpoint
```

## Overview
The plugin creates the object **cordova.plugins.Handpoint** with the following public methods and properties:

### Properties 
| Property                                  | Description                                   |
| :---------------------------------------- | :-------------------------------------------- |
| **[ConnectionMethod](#connectionmethod)** | Enum. containing supported connection methods |
| **[Currency](#currency)**                 | Enum. containing **ISO4217** currency codes   |
| **[LogLevel](#loglevel)**                 | Enum. containing supported logging levels     |

### Methods
| Method                                              | Description                                                                                                                                                        |
| :-------------------------------------------------- | :----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **[setup](#setup)**                                 | Initializes the SDK                                                                                                                                                |
| **[setSharedSecret](#setsharedsecret)**             | Set shared secret for current card reader                                                                                                                          |
| **[eventHandler](#eventhandler)**                   | Adds the event listener.                                                                                                                                           |
| **[sale](#sale)**                                   | A sale initiates a payment operation to the card reader                                                                                                            |
| **[saleAndTokenizeCard](#saleAndTokenizeCard)**     | A saleAndTokenizeCard initiates a payment operation and card tokenization request to the card reader                                                               |
| **[refund](#refund)**                               | A refund initiates a refund operation to the card reader                                                                                                           |
| **[saleReversal](#salereversal)**                   | A sale Reversal, also called sale VOID allows the user to reverse a previous sale operation                                                                        |
| **[refundReversal](#refundreversal)**               | A Refund Reversal, also called refund VOID allows the merchant to reverse a previous refund operation                                                              |
| **[enableScanner](#enableScanner)**                 | Enable Scanner allows the merchant to use the QR / Barcode scanner (where available)                                                                               |
| **[signatureResult](#signatureresult)**             | When signature is required, we use this method to tell the card reader the result of the signature (usually accepted)                                              |
| **[listDevices](#listdevices)**                     | Starts the search for devices to connect to with the specified ConnectionMethod                                                                                    |
| **[connect](#connect)**                             | Connect to a device                                                                                                                                                |
| **[disconnect](#disconnect)**                       | Disconnect will stop the active connection with the device                                                                                                         |
| **[getPendingTransaction](#getpendingtransaction)** | only supported on Card Readers with EFT Software versions 1.7.x and 2.2.x                                                                                          |
| **[update](#update)**                               | The update operation checks for update to the card reader and initiates an update if needed. The update can either be a software update or a configuration update. |
| **[setLogLevel](#setloglevel)**                     | Sets the log level for both the card reader and the SDK                                                                                                            |
| **[getDeviceLogs](#getdevicelogs)**                 | Fetches the logs from the device and reports them to the deviceLogsReady event.                                                                                    |
| **[getSDKVersion](#setup)**                         | Returns the underlying SDK version                                                                                                                                 |

## Quick start

- **1.** Setup the Handpoint SDK Singleton with your shared secret. The shared secret is a key provided by Handpoint when you get your account that enables you to perform live operations with the card reader:

```javascript

  // Init Handpoint SDK 
  cordova.plugins.Handpoint.setup({}, () => {
    // SDK initialization succeed
    // set shared secret
    cordova.plugins.Handpoint.setSharedSecret({
      sharedSecret: '0102030405060708091011121314151617181920212223242526272829303132'
    }, () => { 
      // Initialization completed
    }, (error) => {
      // Set shared secret failed        
    });

  }, (error) => {
    // SDK initialization failed  
  });
```

- **2.** Configure event handler to receive async notifications from the Card Reader. Most of the methods of the SDK are asynchronous. Once you send the operation to the Card reader, you will be notified of the status through the event handler function. See [Event Handling](#event-handling) section.

```javascript
// Configure event handler
cordova.plugins.Handpoint.eventHandler(function (event) {
  // Event handling goes here
  // event.event contains the type of event 
  // event.data contains the event payload
  console.log(event);
}, function (error) {
  // Error in event handler setup
  console.log(error);
});   
```

- **3.** Scan devices: This is a very expensive operation and you should consider to cache the result in local storage and call it again only if user has paired a new device. 
**IMPORTANT NOTE**: You need to ask the user to pair the device before it appears in the results:

```javascript
cordova.plugins.Handpoint.listDevices({
  connectionMethod:  cordova.plugins.Handpoint.ConnectionMethod.BLUETOOTH
}, function() {
  // Call succeed. You will receive the list of devices in the event handler declared above. Event 'deviceDiscoveryFinished'
}, function() {
  // Call failed
});
```

- **4.** Connect to one the devices scanned above:

```javascript
cordova.plugins.Handpoint.connect({
  device: {
    name: device.name,
    address: device.address,
    port: "1",
    connectionMethod: cordova.plugins.Handpoint.ConnectionMethod.BLUETOOTH
  }
}, (result) => {
  // Nothing to do. Wait for Connected event or timeout
}, (error) => {
  // Error connecting to device
});
```

- **5.** Start transaction:

```javascript
cordova.plugins.Handpoint.sale({
  amount: 120,
  currency: cordova.plugins.Handpoint.Currency.ISK,
}, function() {
  // Success. Wait for currentTransactionStatus events
}, function() {
  // Error
});
```

- **6.** Wait for currentTransactionStatus events. F. ex. you might want to listen to **currentTransactionStatus** event to notify user of different Card Reader statuses, and of course **endOfTransaction** event to know the final result of the transaction. See [Event Handling](#event-handling) section.

```javascript
// Handler declared in second step
cordova.plugins.Handpoint.eventHandler(function (event) {
  if (event.event == 'currentTransactionStatus') {
    // Show status to the user
    switch (event.data.info.status) {
      case "WaitingForCard": 
      case "WaitingForCardRemoval": 
      case "TipInput":
      case "WaitingCustomerReceipt":
      case "PrintingMerchantReceipt":
      case "PrintingCustomerReceipt":
      case "WaitingSignature":
      case "ApplicationSelection":
      case "AmountValidation":
      case "AccountTypeSelection":
      case "PaymentCodeSelection":
      case "WaitingHostConnect":
      case "WaitingHostSend":
      case "WaitingHostReceive":
      case "WaitingHostDisconnect":
      case "WaitingHostMessageRequest":
      case "WaitingHostMessageResponse":
      case "PinInputComplete":
      case "PinInput":
    }
  } 
  // Wait for transaction end
  else if (event.event == 'endOfTransaction') { 
    switch (event.data.transactionResult.finStatus) {
      case "AUTHORISED": // Succeed. Inspect event.data.transactionResult for details
        break;
      case "DECLINED": // Failed
      case "CANCELLED":
      case "FAILED":
        break;
    }
  }
});
```

## API Documentation
### Properties
#### ConnectionMethod
Enum. containing supported connection methods. You have to pass the ConnectionMethod when searching for devices or connect to one of them:

| Value                                                    | Notes                         |
| :------------------------------------------------------- | :---------------------------- |
| **cordova.plugins.Handpoint.ConnectionMethod.BLUETOOTH** | This is the                   |
| **cordova.plugins.Handpoint.ConnectionMethod.HTTPS**     | Define for future use         |
| **cordova.plugins.Handpoint.ConnectionMethod.SIMULATOR** | Supported only in **Android** |

#### Currency
Enum. containing [ISO 4217 codes](https://en.wikipedia.org/wiki/ISO_4217). f.ex. **cordova.plugins.Handpoint.Currency.ISK** evaluates as **352**. You need to speficy the currency in financial transactions **sale**, **refund**, **saleReversal** and **refundReversal**:

#### LogLevel

Enum. containing supported logging levels:

| Value                                        | Notes                          |
| :------------------------------------------- | :----------------------------- |
| **cordova.plugins.Handpoint.LogLevel.None**  | No logging                     |
| **cordova.plugins.Handpoint.LogLevel.Info**  | Info logging level             |
| **cordova.plugins.Handpoint.LogLevel.Full**  | Full logging level             |
| **cordova.plugins.Handpoint.LogLevel.Debug** | Provides highest logging level |

### Methods
All the methods of the plugin are asynchronous and return the result of the execution either to a success or an error callback:
`cordova.plugins.Handpoint.methodName([config], successCallback, errorCallback)`

#### <span style="color: #6C7E8F">setup</span>
Initializes the Handpoint SDK. Don't forget to execute it before trying to use the SDK:

```javascript
cordova.plugins.Handpoint.setup({}, successCallback, errorCallback)
```

| Parameter           | Description                              |
| :------------------ | :--------------------------------------- |
| **successCallback** | Executed if the method execution succeed |
| **errorCallback**   | Executed if the method execution failed  |

#### <span style="color: #6C7E8F">setSharedSecret</span>
Validates the app for this session, thus enabling financial transactions. Don't forget to set shared secret before connect to a device:

```javascript
cordova.plugins.Handpoint.setSharedSecret({ sharedSecret: '0102030405060708091011121314151617181920212223242526272829303132' }, successCallback, errorCallback)
```

| Parameter               | Description                              |
| :---------------------- | :--------------------------------------- |
| **config.sharedSecret** | Your shared secret                       |
| **successCallback**     | Executed if the method execution succeed |
| **errorCallback**       | Executed if the method execution failed  |


#### <span style="color: #6C7E8F">eventHandler</span>
Sets up the SDK event handler. See [Event Handling](#event-handling) section.

```javascript
cordova.plugins.Handpoint.eventHandler(function(data) {
  // data.event contains the event name
  // data.data contains the event payload
}, errorCallback)
```

| Parameter           | Description                                             |
| :------------------ | :------------------------------------------------------ |
| **successCallback** | This function will received all the events from the SDK |
| **errorCallback**   | Executed if event handler was not configured            |

#### <span style="color: #6C7E8F">sale</span>
A sale initiates a payment operation to the card reader.

```javascript 
cordova.plugins.Handpoint.sale({
  amount: 1000, // Amount of funds to charge - in the minor unit of currency (f.ex. 1000 is 10.00 GBP)
  currency: cordova.plugins.Handpoint.Currency.GBP
}, successCallback, errorCallback)
```

| Parameter           | Description                                                                 |
| :------------------ | :-------------------------------------------------------------------------- |
| **config.amount**   | Amount to pay in the minor unit of currency (f.ex. 1000 is 10.00 GBP)       |
| **config.currency** | Currency for this transaction. F. ex cordova.plugins.Handpoint.Currency.ISK |
| **successCallback** | Executed if the method execution succeed                                    |
| **errorCallback**   | Executed if the method execution failed                                     |

#### <span style="color: #6C7E8F">saleAndTokenizeCard</span>
A saleAndTokenizeCard initiates a payment operation and card tokenization request to the card reader 

```javascript 
cordova.plugins.Handpoint.saleAndTokenizeCard({
  amount: 1000, // Amount of funds to charge - in the minor unit of currency (f.ex. 1000 is 10.00 GBP)
  currency: cordova.plugins.Handpoint.Currency.GBP
}, successCallback, errorCallback)
```

| Parameter           | Description                                                                 |
| :------------------ | :-------------------------------------------------------------------------- |
| **config.amount**   | Amount to pay in the minor unit of currency (f.ex. 1000 is 10.00 GBP)       |
| **config.currency** | Currency for this transaction. F. ex cordova.plugins.Handpoint.Currency.ISK |
| **successCallback** | Executed if the method execution succeed                                    |
| **errorCallback**   | Executed if the method execution failed                                     |

#### <span style="color: #6C7E8F">refund</span>
A refund initiates a refund operation to the card reader. 

```javascript 
cordova.plugins.Handpoint.refund({
  amount: 1000, // Amount of funds to refund - in the minor unit of currency (f.ex. 1000 is 10.00 GBP)
  currency: cordova.plugins.Handpoint.Currency.GBP
}, successCallback, errorCallback)
```

| Parameter           | Description                                                                 |
| :------------------ | :-------------------------------------------------------------------------- |
| **config.amount**   | Amount to refund in the minor unit of currency (f.ex. 1000 is 10.00 GBP)    |
| **config.currency** | Currency for this transaction. F. ex cordova.plugins.Handpoint.Currency.ISK |
| **successCallback** | Executed if the method execution succeed                                    |
| **errorCallback**   | Executed if the method execution failed                                     |

#### <span style="color: #6C7E8F">saleReversal</span>
A sale Reversal, also called sale VOID allows the user to reverse a previous sale operation. 

```javascript 
cordova.plugins.Handpoint.saleReversal({
  amount: 1000, // Amount of funds to reverse - in the minor unit of currency (f.ex. 1000 is 10.00 GBP)
  currency: cordova.plugins.Handpoint.Currency.GBP,
  originalTransactionID: result.transactionResult.eFTTransactionID // where result is the endOfTransaction event payload
}, successCallback, errorCallback)
```

| Parameter           | Description                                                                 |
| :------------------ | :-------------------------------------------------------------------------- |
| **config.amount**   | Amount to reverse in the minor unit of currency (f.ex. 1000 is 10.00 GBP)   |
| **config.currency** | Currency for this transaction. F. ex cordova.plugins.Handpoint.Currency.ISK |
| **successCallback** | Executed if the method execution succeed                                    |
| **errorCallback**   | Executed if the method execution failed                                     |

#### <span style="color: #6C7E8F">refundReversal</span>
A Refund Reversal, also called Refund VOID allows the merchant to reverse a previous refund operation. 

```javascript 
cordova.plugins.Handpoint.refundReversal({
  amount: 1000, // Amount of funds to reverse - in the minor unit of currency (f.ex. 1000 is 10.00 GBP)
  currency: cordova.plugins.Handpoint.Currency.GBP,
  originalTransactionID: result.transactionResult.eFTTransactionID // where result is the endOfTransaction event payload
}, successCallback, errorCallback)
```

| Parameter           | Description                                                                 |
| :------------------ | :-------------------------------------------------------------------------- |
| **config.amount**   | Amount to reverse in the minor unit of currency (f.ex. 1000 is 10.00 GBP)   |
| **config.currency** | Currency for this transaction. F. ex cordova.plugins.Handpoint.Currency.ISK |
| **successCallback** | Executed if the method execution succeed                                    |
| **errorCallback**   | Executed if the method execution failed                                     |

#### <span style="color: #6C7E8F">enableScanner</span>
Enable Scanner allows the merchant to use the QR / Barcode scanner (where available)

```javascript 
cordova.plugins.Handpoint.enableScanner({
  multiScan: false,
  autoScan: false,
  resultsGrouped: true,
  timeout: 0
}, successCallback, errorCallback)
```

| Parameter                 | Description                                                                                                                                                             |
| :------------------------ | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **config.multiScan**      | true if you want to scan multiple items before returning to your app.                                                                                                   |
| **config.autoScan**       | true if you want the scanner always on without the pushing of a button                                                                                                  |
| **config.resultsGrouped** | true if you want scans from a multi scan to come all together when true if you want scans from a multi scan to come all together when you're finished. you're finished. |
| **config.timeout**        | the amount of seconds until the scanner shuts itself off. 0 means no timeout.                                                                                           |
| **successCallback**       | Executed if the method execution succeed                                                                                                                                |
| **errorCallback**         | Executed if the method execution failed                                                                                                                                 |


#### <span style="color: #6C7E8F">signatureResult</span>
When a **signatureRequired** event is fired, the merchant is required to ask the cardholder for signature and approve or decline it. After that you can execute this method to inform on the result

```javascript 
cordova.plugins.Handpoint.signatureResult({
  accepted: true
}, successCallback, errorCallback)
```

| Parameter           | Description                                                      |
| :------------------ | :--------------------------------------------------------------- |
| **config.accepted** | **true** if customer signature was accepter, **false** otherwise |
| **successCallback** | Executed if the method execution succeed                         |
| **errorCallback**   | Executed if the method execution failed                          |

#### <span style="color: #6C7E8F">listDevices</span>
Starts the search for devices to connect to with the specified ConnectionMethod.

```javascript 
cordova.plugins.Handpoint.listDevices({
  connectionMethod: cordova.plugins.Handpoint.ConnectionMethod.SIMULATOR
}, successCallback, errorCallback)
```

| Parameter                   | Description                                                 |
| :-------------------------- | :---------------------------------------------------------- |
| **config.connectionMethod** | One of the [accepted connection methods](#connectionmethod) |
| **successCallback**         | Executed if the method execution succeed                    |
| **errorCallback**           | Executed if the method execution failed                     |

#### <span style="color: #6C7E8F">connect</span>
Configures the device as the preferred device and tries to connect to it. 

```javascript 
cordova.plugins.Handpoint.connect({
  device: device // One of the devices returned from listDevices operation
}, successCallback, errorCallback)
```

| Parameter           | Description                                                                               |
| :------------------ | :---------------------------------------------------------------------------------------- |
| **config.device**   | Device to connect to. One of device object returned by [listDevices method](#listdevices) |
| **successCallback** | Executed if the method execution succeed                                                  |
| **errorCallback**   | Executed if the method execution failed                                                   |

#### <span style="color: #6C7E8F">disconnect</span>
Disconnect will stop the active connection

```javascript 
cordova.plugins.Handpoint.disconnect({
  device: device // One of the devices returned from listDevices operation
}, successCallback, errorCallback)
```

| Parameter           | Description                                                                                    |
| :------------------ | :--------------------------------------------------------------------------------------------- |
| **config.device**   | Device to disconnect from. One of device object returned by [listDevices method](#listdevices) |
| **successCallback** | Executed if the method execution succeed                                                       |
| **errorCallback**   | Executed if the method execution failed                                                        |

#### <span style="color: #6C7E8F">getPendingTransaction</span>
In the case of a communication failure between the device and the SDK a TransactionResult might have not been delivered to the SDK. This function fetches a pending TransactionResult (which contains receipts) from the device, if any.

```javascript 
cordova.plugins.Handpoint.getPendingTransaction({}, successCallback, errorCallback)
```

| Parameter           | Description                              |
| :------------------ | :--------------------------------------- |
| **successCallback** | Executed if the method execution succeed |
| **errorCallback**   | Executed if the method execution failed  |

#### <span style="color: #6C7E8F">update</span>
The update operation checks for update to the current card reader and initiates an update if needed. The update can either be a software update or a configuration update.

```javascript 
cordova.plugins.Handpoint.update({}, successCallback, errorCallback)
```

| Parameter           | Description                              |
| :------------------ | :--------------------------------------- |
| **successCallback** | Executed if the method execution succeed |
| **errorCallback**   | Executed if the method execution failed  |

#### <span style="color: #6C7E8F">setLogLevel</span>
Sets the log level for both the card reader and the SDK.

```javascript 
cordova.plugins.Handpoint.setLogLevel({
  level: cordova.plugins.Handpoint.LogLevel.Debug
}, successCallback, errorCallback)
```

| Parameter           | Description                                                  |
| :------------------ | :----------------------------------------------------------- |
| **config.level**    | Log level. See the list of [supported log levels](#loglevel) |
| **successCallback** | Executed if the method execution succeed                     |
| **errorCallback**   | Executed if the method execution failed                      |

#### <span style="color: #6C7E8F">getDeviceLogs</span>
Fetches the logs from the device and reports them to the **deviceLogsReady** event.

```javascript 
cordova.plugins.Handpoint.getDeviceLogs({}, successCallback, errorCallback)
```

| Parameter           | Description                              |
| :------------------ | :--------------------------------------- |
| **successCallback** | Executed if the method execution succeed |
| **errorCallback**   | Executed if the method execution failed  |

#### <span style="color: #6C7E8F">getSDKVersion</span>
Returns the underlying SDK version in the success callback.

```javascript 
cordova.plugins.Handpoint.getSDKVersion({}, (version) => {
  console.log(version);
}, errorCallback)
```

| Parameter           | Description                              |
| :------------------ | :--------------------------------------- |
| **successCallback** | Executed if the method execution succeed |
| **errorCallback**   | Executed if the method execution failed  |

## Event Handling
As explained before, all the methods of the plugin are asynchronous and return the result of the execution either to a success or an error callback.

If you don't want to deal with callbacks and you prefer to turn **Callbacks** into **Promises** you might want to publish the events received in the callback function passed to `cordova.plugins.Handpoint.eventHandler` method to a [publish-subscribe style event system](https://ionicframework.com/docs/api/util/Events/) and then encapsulate the event handling logic in an [SDK wrapper module](https://github.com/handpoint/handpoint-ionic-app/blob/master/src/services/sdk.service.ts).

Let's suppose that you want to implement a **sale** which returns a **Promise** that resolves when the transaction is completed, or rejects when it is failed.

First we need to publish **sale** related events to our event system:

```javascript
cordova.plugins.Handpoint.eventHandler(function (event) {
  …
  // Publish event
  if (event.event == 'endOfTransaction') {
    events.publish("handpoint:" + event.event + ":" + event.data.transactionResult.finStatus, event.data);
  }
  …
}, function (error) {
  …
});
```
Now we are publishing Handpoint events as **handpoint:endOfTransaction:AUTHORISED**, **handpoint:endOfTransaction:DECLINED**, etc.

And finally encapsulate the Handpoint SDK **sale** method in a function that returns a Promise which deals with event subscriptions:

```javascript
function salePromise(): Promise<any> {
  return new Promise((resolve, reject) => {
    cordova.plugins.Handpoint.sale(params, ()=> {
      // Subscribe to success events and resolve the promise: handpoint:endOfTransaction:AUTHORISED
      … 
      successEvents.subscribe((result) => {
        resolve(result);
      });
      // Subscribe to error events and reject the promise: handpoint:endOfTransaction:DECLINE handpoint:endOfTransaction:FAILED handpoint:endOfTransaction:CANCELLED
      … 
      errorEvents.subscribe((result) => {
        reject(result);
      });
      // Clean up and unsubscribe
      …
    }, (error) => {
      reject(error);
    });
  })
}
```

**NOTE**: the example above is pseudocode, you have a [fully implemented wrapper here](https://github.com/handpoint/handpoint-ionic-app/blob/master/src/services/sdk.service.ts).
