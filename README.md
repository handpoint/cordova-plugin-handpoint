# Cordova Handpoint SDK Plugin
Cross-platform Handpoint SDK for Cordova. Take a look at the [Ionic 2 starter application](https://github.com/handpoint/handpoint-ionic-app)

## Installation
**TODO**

## Overview
The plugin creates the object **cordova.plugins.Handpoint** with the following public methods and properties:

### Properties 
| Property             | Description                                   |
| :------------------- | :-------------------------------------------- |
| **ConnectionMethod** | Enum. containing supported connection methods |
| **Currency**         | Enum. containing **ISO4217** currency codes   |
| **LogLevel**         | Enum. containing supported logging levels     |

### Methods
| Method                    | Description                                                                                                                                                        |
| :------------------------ | :----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **setup**                 | Initializes the SDK                                                                                                                                                |
| **setSharedSecret**       | Set shared secret for current card reader                                                                                                                          |
| **eventHandler**          | Adds the event listener.                                                                                                                                           |
| **sale**                  | A sale initiates a payment operation to the card reader                                                                                                            |
| **refund**                | A refund initiates a refund operation to the card reader                                                                                                           |
| **saleReversal**          | A sale Reversal, also called sale VOID allows the user to reverse a previous sale operation                                                                        |
| **refundReversal**        | A Refund Reversal, also called refund VOID allows the merchant to reverse a previous refund operation                                                              |
| **signatureResult**       | When signature is required, we use this method to tell the card reader the result of the signature (usually accepted)                                              |
| **listDevices**           | Starts the search for devices to connect to with the specified ConnectionMethod                                                                                    |
| **connect**               | Connect to a device                                                                                                                                                |
| **disconnect**            | Disconnect will stop the active connection with the device                                                                                                         |
| **getPendingTransaction** | only supported on Card Readers with EFT Software versions 1.7.x and 2.2.x                                                                                          |
| **update**                | The update operation checks for update to the card reader and initiates an update if needed. The update can either be a software update or a configuration update. |
| **setLogLevel**           | Sets the log level for both the card reader and the SDK                                                                                                            |
| **getDeviceLogs**         | Fetches the logs from the device and reports them to the deviceLogsReady event.                                                                                    |

## Quick start
- **1.** Setup the Handpoint SDK Singleton:
```javascript
// Replace sharedSecret with yours
cordova.plugins.Handpoint.setup({
  sharedSecret: "0102030405060708091011121314151617181920212223242526272829303132"
}, function (result) {
  // SDK initialization succeed
}, function (error) {
  // SDK initialization failed
});
```
- **2.** Configure event handler to receive async notifications from the Card Reader
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
- **3.** Scan devices: This is a very expensive operation and you should consider to cache the result in local storage and call it again only if user has paired a new device. **NOTE**: You need to ask the user to pair the device before calling **listDevices**: 
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
- **5.** Send transaction
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
- **6.** Wait for currentTransactionStatus events:
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
});
```

## API Documentation
### Properties
#### ConnectionMethod
Enum. containing supported connection methods:

| Value                                                    | Notes                         |
| :------------------------------------------------------- | :---------------------------- |
| **cordova.plugins.Handpoint.ConnectionMethod.BLUETOOTH** | This is the                   |
| **cordova.plugins.Handpoint.ConnectionMethod.HTTPS**     | Define for future use         |
| **cordova.plugins.Handpoint.ConnectionMethod.SIMULATOR** | Supported only in **Android** |

#### Currency
Enum. containing [ISO 4217 codes](https://en.wikipedia.org/wiki/ISO_4217). f.ex. **cordova.plugins.Handpoint.Currency.ISK** evaluates as **352**.

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

#### setup
Initializes the Handpoint SDK with your shared secret:
```javascript
cordova.plugins.Handpoint.setup({ sharedSecret: '0102030405060708091011121314151617181920212223242526272829303132' }, successCallback, errorCallback)
```

#### setSharedSecret
Validates the app for this session, thus enabling financial transactions:
```javascript
cordova.plugins.Handpoint.setSharedSecret({ sharedSecret: '0102030405060708091011121314151617181920212223242526272829303132' }, successCallback, errorCallback)
```
#### eventHandler
Sets up the SDK event handler
```javascript
cordova.plugins.Handpoint.eventHandler(function(data) {
  // data.event contains the event name
  // data.data contains the event payload
}, errorCallback)
```
#### sale
A sale initiates a payment operation to the card reader.
```javascript 
cordova.plugins.Handpoint.sale({
  amount: 1000, // Amount of funds to charge - in the minor unit of currency (f.ex. 1000 is 10.00 GBP)
  currency: cordova.plugins.Handpoint.Currency.GBP
}, successCallback, errorCallback)
```
#### refund
A refund initiates a refund operation to the card reader. 
```javascript 
cordova.plugins.Handpoint.refund({
  amount: 1000, // Amount of funds to refund - in the minor unit of currency (f.ex. 1000 is 10.00 GBP)
  currency: cordova.plugins.Handpoint.Currency.GBP
}, successCallback, errorCallback)
```
#### saleReversal
A sale Reversal, also called sale VOID allows the user to reverse a previous sale operation. 
```javascript 
cordova.plugins.Handpoint.saleReversal({
  amount: 1000, // Amount of funds to reverse - in the minor unit of currency (f.ex. 1000 is 10.00 GBP)
  currency: cordova.plugins.Handpoint.Currency.GBP,
  originalTransactionID: result.transactionResult.eFTTransactionID // where result is the endOfTransaction event payload
}, successCallback, errorCallback)
```
#### refundReversal
A Refund Reversal, also called Refund VOID allows the merchant to reverse a previous refund operation. 
```javascript 
cordova.plugins.Handpoint.refundReversal({
  amount: 1000, // Amount of funds to reverse - in the minor unit of currency (f.ex. 1000 is 10.00 GBP)
  currency: cordova.plugins.Handpoint.Currency.GBP,
  originalTransactionID: result.transactionResult.eFTTransactionID // where result is the endOfTransaction event payload
}, successCallback, errorCallback)
```
#### signatureResult
When a **signatureRequired** event is fired, the merchant is required to ask the cardholder for signature and approve (or decline) the signature.
```javascript 
cordova.plugins.Handpoint.signatureResult({
  accepted: true
}, successCallback, errorCallback)
```
#### listDevices
Starts the search for devices to connect to with the specified ConnectionMethod.
```javascript 
cordova.plugins.Handpoint.listDevices({
  method: cordova.plugins.Handpoint.ConnectionMethod.SIMULATOR
}, successCallback, errorCallback)
```
#### connect
Configures the device as the preferred device and tries to connect to it. 
```javascript 
cordova.plugins.Handpoint.connect({
  device: device // One of the devices returned from listDevices operation
}, successCallback, errorCallback)
```
#### disconnect
Disconnect will stop the active connection
```javascript 
cordova.plugins.Handpoint.disconnect({
  device: device // One of the devices returned from listDevices operation
}, successCallback, errorCallback)
```
#### getPendingTransaction
In the case of a communication failure between the device and the SDK a TransactionResult might have not been delivered to the SDK. This function fetches a pending TransactionResult (which contains receipts) from the device, if any.
```javascript 
cordova.plugins.Handpoint.getPendingTransaction({}, successCallback, errorCallback)
```
#### update
The update operation checks for update to the current card reader and initiates an update if needed. The update can either be a software update or a configuration update.
```javascript 
cordova.plugins.Handpoint.update({}, successCallback, errorCallback)
```
#### setLogLevel
Sets the log level for both the card reader and the SDK.
```javascript 
cordova.plugins.Handpoint.setLogLevel({
  level: cordova.plugins.Handpoint.LogLevel.Debug,
  device: device
}, successCallback, errorCallback)
```
#### getDeviceLogs
Fetches the logs from the device and reports them to the **deviceLogsReady** event.
```javascript 
cordova.plugins.Handpoint.getDeviceLogs({}, successCallback, errorCallback)
```