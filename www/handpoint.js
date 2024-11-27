var exec = cordova.require("cordova/exec");

/**
 * @constructor
 * @returns {Handpoint}
 */
function Handpoint() {

  /**
   * Connection Method enum
   * @type Object
   */
  this.ConnectionMethod = {
    "BLUETOOTH": 0,
    "USB": 1,
    "ANDROID_PAYMENT": 2
  };

  /**
   * Currencies enum
   * @type Object
   */
  this.Currency = {
    "AED": 784,
    "AFN": 971,
    "ALL": 8,
    "AMD": 51,
    "ANG": 532,
    "AOA": 973,
    "ARS": 32,
    "AUD": 36,
    "AWG": 533,
    "AZN": 944,
    "BAM": 977,
    "BBD": 52,
    "BDT": 50,
    "BGN": 975,
    "BHD": 48,
    "BIF": 108,
    "BMD": 60,
    "BND": 96,
    "BOB": 68,
    "BOV": 984,
    "BRL": 986,
    "BSD": 44,
    "BTN": 64,
    "BWP": 72,
    "BYR": 974,
    "BZD": 84,
    "CAD": 124,
    "CDF": 976,
    "CHF": 756,
    "CLP": 152,
    "CNY": 156,
    "COP": 170,
    "COU": 970,
    "CRC": 188,
    "CUC": 931,
    "CUP": 192,
    "CVE": 132,
    "CZK": 203,
    "DJF": 262,
    "DKK": 208,
    "DOP": 214,
    "DZD": 12,
    "EGP": 818,
    "ERN": 232,
    "ETB": 230,
    "EUR": 978,
    "FJD": 242,
    "FKP": 238,
    "GBP": 826,
    "GEL": 981,
    "GHS": 936,
    "GIP": 292,
    "GMD": 270,
    "GNF": 324,
    "GTQ": 320,
    "GYD": 328,
    "HKD": 344,
    "HNL": 340,
    "HRK": 191,
    "HTG": 332,
    "HUF": 348,
    "IDR": 360,
    "ILS": 376,
    "INR": 356,
    "IQD": 368,
    "IRR": 364,
    "ISK": 352,
    "JMD": 388,
    "JOD": 400,
    "JPY": 392,
    "KES": 404,
    "KGS": 417,
    "KHR": 116,
    "KMF": 174,
    "KPW": 408,
    "KRW": 410,
    "KWD": 414,
    "KYD": 136,
    "KZT": 398,
    "LAK": 418,
    "LBP": 422,
    "LKR": 144,
    "LRD": 430,
    "LSL": 426,
    "LTL": 440,
    "LYD": 434,
    "MAD": 504,
    "MDL": 498,
    "MGA": 969,
    "MKD": 807,
    "MMK": 104,
    "MNT": 496,
    "MOP": 446,
    "MRO": 478,
    "MUR": 480,
    "MVR": 462,
    "MWK": 454,
    "MXN": 484,
    "MXV": 979,
    "MYR": 458,
    "MZN": 943,
    "NAD": 516,
    "NGN": 566,
    "NIO": 558,
    "NOK": 578,
    "NPR": 524,
    "NZD": 554,
    "OMR": 512,
    "PAB": 590,
    "PEN": 604,
    "PGK": 598,
    "PHP": 608,
    "PKR": 586,
    "PLN": 985,
    "PYG": 600,
    "QAR": 634,
    "RON": 946,
    "RSD": 941,
    "RUB": 643,
    "RWF": 646,
    "SAR": 682,
    "SBD": 90,
    "SCR": 690,
    "SDG": 938,
    "SEK": 752,
    "SGD": 702,
    "SHP": 654,
    "SLL": 694,
    "SOS": 706,
    "SRD": 968,
    "SSP": 728,
    "STD": 678,
    "SYP": 760,
    "SZL": 748,
    "THB": 764,
    "TJS": 972,
    "TMT": 934,
    "TND": 788,
    "TOP": 776,
    "TRY": 949,
    "TTD": 780,
    "TWD": 901,
    "TZS": 834,
    "UAH": 980,
    "UGX": 800,
    "USD": 840,
    "UZS": 860,
    "VEF": 937,
    "VND": 704,
    "VUV": 548,
    "WST": 882,
    "XAF": 950,
    "XCD": 951,
    "XOF": 952,
    "XPF": 953,
    "YER": 886,
    "ZAR": 710,
    "ZMW": 967,
    "ZWL": 932
  };

  this.DeviceParameter = {
    "BluetoothName": "BluetoothName",
    "BluetoothPass": "BluetoothPass",
    "SystemTimeout": "SystemTimeout",
    "ScreenTimeout": "ScreenTimeout",
    "SignatureTimeout": "SignatureTimeout"
  };

  this.LogLevel = {
    "None": 0,
    "Info": 1,
    "Full": 2,
    "Debug": 3
  };
}

/**
 * Change SDK locale
 * @param config.locale Locale code
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.setLocale = function (config, successCallback, errorCallback) {
  this.exec('setLocale', config, successCallback, errorCallback);
};

/**
 * Returns the tokenized version of the card used if successful (not available for all acquirers,
 * please check with Handpoint to know if tokenization is supported for your acquirer of choice)
 * @param {Object} config parameters for tokenizeCard transaction
 * @param config.map A map including extra optional transaction parameters
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.tokenizeCard = function (config, successCallback, errorCallback) {
  this.exec('tokenizeCard', config, successCallback, errorCallback);
};

/**
 * A sale initiates a payment operation to the card reader. In it's simplest form you only have to pass the
 * amount and currency but it also accepts a map with extra parameters.
 * @param {Object} config parameters for sale transaction
 * @param config.amount Amount of funds to charge - in the minor unit of currency (f.ex. 1000 cents is 10.00 GBP)
 * @param config.currency Currency of the charge @see Handpoint.Currency
 * @param config.map A map including extra optional transaction parameters
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.sale = function (config, successCallback, errorCallback) {
  this.exec('sale', config, successCallback, errorCallback);
};

/**
 * A refund initiates a refund operation to the card reader. This operation moves funds from
 * the merchant account to the cardholder´s credit card. In it's simplest form you only have
 * to pass the amount and currency but it also accepts a map with extra parameters.
 * @param {Object} config parameters
 * @param config.amount Amount of funds to charge - in the minor unit of currency (f.ex. 1000 cents is 10.00 GBP)
 * @param config.currency Currency of the charge @see Handpoint.Currency
 * @param config.map A map including extra optional transaction parameters
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.refund = function (config, successCallback, errorCallback) {
  this.exec('refund', config, successCallback, errorCallback);
};

/**
 * An automatic refund initiates a refund operation to the card reader. This operation moves funds from
 * the merchant account to the cardholder´s credit card. In it's simplest form you only have
 * to pass the amount and currency but it also accepts a map with extra parameters.
 * @param {Object} config parameters
 * @param config.amount Amount of funds to charge - in the minor unit of currency (f.ex. 1000 cents is 10.00 GBP)
 * @param config.currency Currency of the charge @see Handpoint.Currency
 * @param config.map A map including extra optional transaction parameters
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.automaticRefund = function (config, successCallback, errorCallback) {
  this.exec('automaticRefund', config, successCallback, errorCallback);
};

/**
 * A sale initiates a payment operation to the card reader. In it's simplest form you only have to pass the
 * amount and currency but it also accepts a map with extra parameters.
 * @param {Object} config parameters
 * @param config.amount Amount of funds to charge - in the minor unit of currency (f.ex. 1000 cents is 10.00 GBP)
 * @param config.currency Currency of the charge @see Handpoint.Currency
 * @param config.originalTransactionID As received from the card reader (EFTTransactionID)
 * @param config.map A map including extra optional transaction parameters
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.saleReversal = function (config, successCallback, errorCallback) {
  this.exec('saleReversal', config, successCallback, errorCallback);
};

/**
 * A Refund Reversal, also called Refund VOID allows the merchant to reverse a previous
 * refund operation. This operation reverts (if possible) a specific refund identified
 * with a transaction id. In it's simplest form you only have to pass the amount, currency
 * and originalTransactionID but it also accepts a map with extra parameters. Note that transactions
 * can only be reversed within the same day as the transaction was made.
 * @param {Object} config parameters
 * @param config.amount Amount of funds to charge - in the minor unit of currency (f.ex. 1000 cents is 10.00 GBP)
 * @param config.currency Currency of the charge @see Handpoint.Currency
 * @param config.originalTransactionID As received from the card reader (EFTTransactionID)
 * @param config.map A map including extra optional transaction parameters
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.refundReversal = function (config, successCallback, errorCallback) {
  this.exec('refundReversal', config, successCallback, errorCallback);
};

/**
 * A manual entry sale. In it's simplest form you only have to pass the
 * amount and currency but it also accepts a map with extra parameters.
 * @param {Object} config parameters for motoSale transaction
 * @param config.amount Amount of funds to charge - in the minor unit of currency (f.ex. 1000 cents is 10.00 GBP)
 * @param config.currency Currency of the charge @see Handpoint.Currency
 * @param config.map A map including extra optional transaction parameters
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.motoSale = function (config, successCallback, errorCallback) {
  this.exec('motoSale', config, successCallback, errorCallback);
};

/**
 * A manual entry refund. In it's simplest form you only have to pass the
 * amount and currency but it also accepts a map with extra parameters.
 * @param {Object} config parameters for motoSale transaction
 * @param config.originalTransactionID As received from the card reader (EFTTransactionID)
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.motoRefund = function (config, successCallback, errorCallback) {
  this.exec('motoRefund', config, successCallback, errorCallback);
};

/**
 * A manual entry reversal. In it's simplest form you only have to pass the
 * amount and currency but it also accepts a map with extra parameters.
 * @param {Object} config parameters for motoSale transaction
 * @param config.amount Amount of funds to charge - in the minor unit of currency (f.ex. 1000 cents is 10.00 GBP)
 * @param config.currency Currency of the charge @see Handpoint.Currency
 * @param config.map A map including extra optional transaction parameters
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.motoReversal = function (config, successCallback, errorCallback) {
  this.exec('motoReversal', config, successCallback, errorCallback);
};


/**
 * Initiates a preauthorization request. In it's simplest form you only have to pass the
 * amount and currency but it also accepts a map with extra parameters.
 * @param {Object} config parameters for sale transaction
 * @param config.amount Amount of funds to charge - in the minor unit of currency (f.ex. 1000 cents is 10.00 GBP)
 * @param config.currency Currency of the charge @see Handpoint.Currency
 * @param config.map A map including extra optional transaction parameters
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.preAuthorization = function (config, successCallback, errorCallback) {
  this.exec('preAuthorization', config, successCallback, errorCallback);
};

/**
 * Initiates a reversal of a preauthorization request. In it's simplest form you only have to pass the
 * originalTransactionID but it also accepts a map with extra parameters.
 * @param {Object} config parameters
 * @param config.originalTransactionID Unique id of the original preAuthorization transaction as received from the card reader (EFTTransactionID)
 * @param config.map A map including extra optional transaction parameters
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.preAuthorizationReversal = function (config, successCallback, errorCallback) {
  this.exec('preAuthorizationReversal', config, successCallback, errorCallback);
};

/**
 * Executes a modification to the previously preauthorized amount. In it's simplest form you only have to pass the
 * amount, currency, [tipAmount] and originalTransactionID but it also accepts a map with extra parameters.
 * @param {Object} config parameters for sale transaction
 * @param config.amount Amount of funds to charge - in the minor unit of currency (f.ex. 1000 cents is 10.00 GBP)
 * @param config.currency Currency of the charge @see Handpoint.Currency
 * @param config.tipAmount Tip amount added to the original (base) transaction amount - in the minor unit of currency
 * @param config.originalTransactionID Unique id of the original preAuthorization transaction as received from the card reader (EFTTransactionID)
 * @param config.map A map including extra optional transaction parameters
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.preAuthorizationIncrease = function (config, successCallback, errorCallback) {
  this.exec('preAuthorizationIncrease', config, successCallback, errorCallback);
};

/**
 * Launch the capture of the preauthorization. In it's simplest form you only have to pass the
 * amount, currency, [tipAmount] and originalTransactionID but it also accepts a map with extra parameters.
 * @param {Object} config parameters for sale transaction
 * @param config.amount Amount of funds to charge - in the minor unit of currency (f.ex. 1000 cents is 10.00 GBP)
 * @param config.currency Currency of the charge @see Handpoint.Currency
 * @param config.tipAmount Tip amount added to the original (base) transaction amount - in the minor unit of currency
 * @param config.originalTransactionID Unique id of the original preAuthorization transaction as received from the card reader (EFTTransactionID)
 * @param config.map A map including extra optional transaction parameters
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.preAuthorizationCapture = function (config, successCallback, errorCallback) {
  this.exec('preAuthorizationCapture', config, successCallback, errorCallback);
};

/**
 * Initiates a manual entry preauthorization request. In it's simplest form you only have to pass the
 * amount and currency but it also accepts a map with extra parameters.
 * @param {Object} config parameters for sale transaction
 * @param config.amount Amount of funds to charge - in the minor unit of currency (f.ex. 1000 cents is 10.00 GBP)
 * @param config.currency Currency of the charge @see Handpoint.Currency
 * @param config.map A map including extra optional transaction parameters
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.motoPreauthorization = function (config, successCallback, errorCallback) {
  this.exec('motoPreauthorization', config, successCallback, errorCallback);
};

/**
 * Enable Scanner allows the merchant to use the QR / Barcode scanner (where available)
 * It accepts certain configuration parameters, such as multiScan (boolean)
 * autoScan (boolean), resultsGrouped (boolean) and timeout (integer),
 * @param {Object} config parameters
 * @param config.multiScan true if you want to scan multiple items before returning to your app.
 * @param config.autoScan true if you want the scanner always on without the pushing of a button
 * @param config.resultsGrouped true if you want scans from a multi scan to come all together when
 * you're finished.
 * @param config.timeout the amount of seconds until the scanner shuts itself off. 0 means no timeout.
 * @param config.map A map including extra optional transaction parameters
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.enableScanner = function (config, successCallback, errorCallback) {
  this.exec('enableScanner', config, successCallback, errorCallback);
};

/**
 * This method attempts to cancel the current transaction on the card reader. Note that
 * operations can only be cancelled before requests are sent to the gateway. There is a
 * flag called cancelAllowed in the currentTransactionStatus event that can be used to check
 * if the transaction is in a state that allows cancel.
 * @param {Object} device This parameter specifies to the system which device you want to use for the
 * operations. If none is supplied the system will attempt to use a default device, if any.
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.cancelRequest = function (config, successCallback, errorCallback) {
  this.exec('cancelRequest', config, successCallback, errorCallback);
};

/**
 * This method attempts to stop the current transaction on the card reader. Note that
 * operations can only be cancelled before requests are sent to the gateway. There is a
 * flag called cancelAllowed in the currentTransactionStatus event that can be used to check
 * if the transaction is in a state that allows cancel.
 * @param {Object} device This parameter specifies to the system which device you want to use for the
 * operations. If none is supplied the system will attempt to use a default device, if any.
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.stopCurrentTransaction = function (config, successCallback, errorCallback) {
  this.exec('stopCurrentTransaction', config, successCallback, errorCallback);
};



/**
 * A tip adjustment operation allows merchants to adjust the tip amount of a sale transaction before the batch
 * of transactions is settled by the processor at the end of the day.
 * Note: This functionality is only available for the restaurant industry in the United States
 * and the processors currently supporting this functionality are TSYS and VANTIV.
 * @param {Object} config parameters
 * @param config.tipAmount Tip amount added to the original (base) transaction amount - in the minor unit of currency
 * @param config.originalTransactionID Unique id of the original sale transaction as received from the card reader (EFTTransactionID)
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.tipAdjustment = function (config, successCallback, errorCallback) {
  this.exec('tipAdjustment', config, successCallback, errorCallback);
};

/**
 * A signatureRequired event is invoked during transaction when signature verification is needed
 * (f.ex when payment is done with a magstripe card). The merchant is required to ask the cardholder
 * for signature and approve (or disapprove) the signature. signatureResult tells the card reader
 * if the signature was approved by passing true in the method. To disapprove then false is passed.
 * @param {Object} config parameters
 * @param config.accepted pass true if merchant accepts customer signature
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.signatureResult = function (config, successCallback, errorCallback) {
  this.exec('signatureResult', config, successCallback, errorCallback);
};

/**
 * Configures the device as the preferred device and tries to connect to it. Everytime a
 * new connection is started the SDK will make 3 attempts to reestablish the connection.
 * If those attempts fail, the connection is considered dead.
 * @param {Object} config parameters
 * @param config.device This parameter specifies to the system which device you want to use for the operations.
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.connect = function (config, successCallback, errorCallback) {
  this.exec('connect', config, successCallback, errorCallback);
};

/**
 * Disconnect will stop the active connection (and reconnection process). Please note
 * that the method does NOT ignore the current state of the card reader. This means that
 * if a disconnect is attempted during a transaction it will not be successful and the
 * method will return false. If a transaction is not in progress the disconnect will take
 * 1-3 seconds to successfully finish and will then return true.
 * @param {Object} config parameters
 * @param config.device This parameter specifies to the system which device you want to use for the operations.
 * If none is supplied the system will attempt to use a default device, if any.
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.disconnect = function (config, successCallback, errorCallback) {
  this.exec('disconnect', config, successCallback, errorCallback);
};

/**
 * Validates the app for this session, thus enabling financial transactions
 * @param {Object} config parameters
 * @param config.sharedSecret 	The shared secret is a key provided by Handpoint when you
 * get your account that enables you to perform live operations with the card reader.
 * However, if you're developing with a starter kit, the test shared secret is specified
 * in the example
 * @param config.device This parameter specifies to the system which device you want to use for
 * the operations. If none is supplied the system will attempt to use a default device, if any.
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.setSharedSecret = function (config, successCallback, errorCallback) {
  this.exec('setSharedSecret', config, successCallback, errorCallback);
  // exec Success callback just after setting SS
  successCallback();
};

/**
 * Init SDK with shared secret
 * @param {Object} config parameters
 * @param config.automaticReconnection If set to true, the SDK will automatically reconnect
 * to the last known device when the connection is lost. The default value is false
 * @param config.sharedSecret 	The shared secret is a key provided by Handpoint when you
 * get your account that enables you to perform live operations with the card reader.
 * However, if you're developing with a starter kit, the test shared secret is specified
 * in the example
 */
Handpoint.prototype.setup = function (config, successCallback, errorCallback) {
  this.exec('setup', config, successCallback, errorCallback);
};

/**
 * Changes values of certain parameters on the card reader.
 * @param {Object} config parameters
 * @param config.param The name of the parameter to change
 * @param config.value New value of the parameter
 * @param config.device This parameter specifies to the system which device you want to use for the operations.
 * If none is supplied the system will attempt to use a default device, if any.
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.setParameter = function (config, successCallback, errorCallback) {
  this.exec('setParameter', config, successCallback, errorCallback);
};

/**
 * Sets the log level (info, debug...) for both the card reader and the API. Note : At the end of a transaction,
 * the card reader logs are always automatically fetched to the API.
 * @param {Object} config parameters
 * @param config.level The desired log level. Can be LogLevel.None, LogLevel.Info, LogLevel.Full, LogLevel.Debug
 * @param config.device This parameter specifies to the system which device you want to use for the operations.
 * If none is supplied the system will attempt to use a default device, if any.
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.setLogLevel = function (config, successCallback, errorCallback) {
  this.exec('setLogLevel', config, successCallback, errorCallback);
};

/**
 * Fetches the logs from the device and reports them to the deviceLogsReady event.
 * @param {Object} config parameters
 * @param config.device This parameter specifies to the system which device you want to use for the operations.
 * If none is supplied the system will attempt to use a default device, if any.
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.getDeviceLogs = function (config, successCallback, errorCallback) {
  this.exec('getDeviceLogs', config, successCallback, errorCallback);
};

/**
 * Please note this method is only supported on Card Readers with EFT Software versions 1.7.x and 2.2.x and up
 * In the case of a communication failure between the device and the API a TransactionResult might have not
 * been delivered to the API. This function fetches a pending TransactionResult (which contains receipts)
 * from the device, if any. If no TransactionResult was pending a result will be delivered containing default
 * fields. In order to receive only valid TransactionResults this function should only be called when
 * pendingTransactionResult event is invoked or when HapiManager.isTransactionResultPending() is true.
 * To receive events when a TransactionResult is pending on the device please add a Events.PendingResults listener.
 * @param {Object} config parameters
 * @param config.device This parameter specifies to the system which device you want to use for the operations.
 * If none is supplied the system will attempt to use a default device, if any.
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.getPendingTransaction = function (config, successCallback, errorCallback) {
  this.exec('getPendingTransaction', config, successCallback, errorCallback);
};

/**
 * The update operation checks for update to the card reader and initiates an update if needed. The update
 * can either be a software update or a configuration update.
 * @param {Object} config parameters
 * @param config.device This parameter specifies to the system which device you want to use for the operations.
 * If none is supplied the system will attempt to use a default device, if any.
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.update = function (config, successCallback, errorCallback) {
  this.exec('update', config, successCallback, errorCallback);
};

/**
 * Starts the search for devices to connect with the specified connectionMethod
 * @param {Object} config parameters
 * @param config.connectionMethod The means of connection you intend to use to talk to the device.
 * (Bluetooth, Serial, USB, etc...)
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.listDevices = function (config, successCallback, errorCallback) {
  this.exec('listDevices', config, successCallback, errorCallback);
};

/**
 * Prints the HTML receipt.
 * @param {Object} config parameters
 * @param config.receipt The receipt in HTML format
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.printReceipt = function (config, successCallback, errorCallback) {
  this.exec('printReceipt', config, successCallback, errorCallback);
};

/**
 * Gets the report data.
 * @param {Object} config parameters
 * @param config.config The configuration for report data
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.getTransactionsReport = function (config, successCallback, errorCallback) {
  this.exec('getTransactionsReport', config, successCallback, errorCallback);
};

/**
 * Gets the transaction status.
 * @param {Object} config parameters
 * @param config.transactionReference The id of the transaction
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.getTransactionStatus = function (config, successCallback, errorCallback) {
  this.exec('getTransactionStatus', config, successCallback, errorCallback);
};

/**
 * Starts a connection monitoring service. The service listens to events sent by the operating system about
 * the connected hardware. If the service notices that a previously connected device suddenly disconnects
 * on the hardware layer it attempts to reconnect to that particular device. Since this is a service it
 * is necessary that the service is turned off before the application ends its life-time. This means that,
 * if the service was running, stopMonitoringConnections() has to be called before the application is exited
 * completely. Note that the service currently only works with BLUETOOTH. In the case of BLUETOOTH the service
 * will attempt to reconnect to the device three times, if unsuccessful the connection is considered Disconnected.
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.startMonitoringConnections = function (successCallback, errorCallback) {
  // Deprecated: removed in hapi-android-4.0.0
  this.exec('startMonitoringConnections', {}, successCallback, errorCallback);
};

/**
 * Stops a connection monitoring service. The service listens to events sent by the operating system about
 * the connected hardware. If the service notices that a previously connected device suddenly disconnects
 * on the hardware layer it attempts to reconnect to that particular device. Since this is a service it
 * is necessary that the service is turned off before the application ends its life-time. This means that,
 * if the service was running, stopMonitoringConnections() has to be called before the application is exited
 * completely. Note that the service currently only works with BLUETOOTH. In the case of BLUETOOTH the
 * service will attempt to reconnect to the device three times, if unsuccessful the connection is considered
 * Disconnected.
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.stopMonitoringConnections = function (successCallback, errorCallback) {
  // Deprecated: removed in hapi-android-4.0.0
  this.exec('stopMonitoringConnections', {}, successCallback, errorCallback);
};

/**
 * Subscribes to all device events
 * @param {Function} successCallback This function will be called every time device dispatches an event
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.eventHandler = function (successCallback, errorCallback) {
  this.exec('eventHandler', {}, successCallback, errorCallback);
};

/**
 * Used to notify SDK about app go to background event
 * @param {Function} successCallback This function will be called every time device dispatches an event
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.applicationDidGoBackground = function (successCallback, errorCallback) {
  this.exec('applicationDidGoBackground', {}, successCallback, errorCallback);
};

/**
 * Returns the underlying SDK version
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.getSDKVersion = function (successCallback, errorCallback) {
  this.exec('getSDKVersion', {}, successCallback, errorCallback);
};

Handpoint.prototype.getDeviceInfo = function (successCallback, errorCallback) {
  this.exec('getDeviceInfo', {}, successCallback, errorCallback);
};

/**
 * Set the brightness of the device
 * @param {*} config Config object with the brightness value
 *  - config.brightness The brightness value to set
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.setBrightness = function (config, successCallback, errorCallback) {
  this.exec('setBrightness', config, successCallback, errorCallback);
};

/**
 * Turn off the screen of the device
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.turnOffScreen = function (successCallback, errorCallback) {
  this.exec('turnOffScreen', {}, successCallback, errorCallback);
};

/**
 * Turn on the screen of the device
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.turnOnScreen = function (successCallback, errorCallback) {
  this.exec('turnOnScreen', {}, successCallback, errorCallback);
}

/**
 * Turn off the screen saver of the device
 * @param {*} successCallback 
 * @param {*} errorCallback 
 */
Handpoint.prototype.turnOffScreenSaver = function (successCallback, errorCallback) {
  this.exec('turnOffScreenSaver', {}, successCallback, errorCallback);
}

/**
 * Turn on the screen saver of the device
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.turnOnScreenSaver = function (successCallback, errorCallback) {
  this.exec('turnOnScreenSaver', {}, successCallback, errorCallback);
}

/**
 * Show location setting dialog
 * @param config.text Text to be shown
 * @param config.okBtnText Accept button text
 * @param config.cancelBtnText Cancel button text
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.enableLocation = function (config, successCallback, errorCallback) {
  this.exec('enableLocation', config, successCallback, errorCallback);
};

/**
 * Disable battery optimization mode
 * @param {*} config
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.disableBatteryOptimizations = function (config, successCallback, errorCallback) {
  this.exec('disableBatteryOptimizations', config, successCallback, errorCallback);
};

/**
 * Check if battery optimization mode is enabled
 * @param {*} config
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.isBatteryOptimizationOn = function (config, successCallback, errorCallback) {
  this.exec('isBatteryOptimizationOn', config, successCallback, errorCallback);
};

/**
 * Authenticates MPOS device
 * @param {Object} config parameters for mposAuth operation
 * @param config.service Service to authenticate to
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.mposAuth = function (config, successCallback, errorCallback) {
  this.exec('mposAuth', config, successCallback, errorCallback);
};

/**
 * Updates the webview of the terminal to the minimum supported version
 * @param {Object} config parameters for mposAuth operation
 * @param config.service Service to authenticate to
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.updateWebView = function (config, successCallback, errorCallback) {
  this.exec('updateWebView', config, successCallback, errorCallback);
};

/**
 * Set the APN settings on a smartPOS device
 * @param {Object} config parameters for setApn operation
 * @param config.name Name
 * @param config.apn APN server
 * @param config.user User name
 * @param config.passwd Password
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.setApn = function (config, successCallback, errorCallback) {
  this.exec('setApn', config, successCallback, errorCallback);
};

/**
 * Enable the Navigation bar on a smartPOS device
 * @param {*} config
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.enableNavigationBar = function (config, successCallback, errorCallback) {
  this.exec('enableNavigationBar', config, successCallback, errorCallback);
};

/**
 * Disable the Navigation bar on a smartPOS device
 * @param {*} config
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.disableNavigationBar = function (config, successCallback, errorCallback) {
  this.exec('disableNavigationBar', config, successCallback, errorCallback);
};

/**
 * Reboots the device
 * @param {Object} config parameters for reboot operation
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.reboot = function (config, successCallback, errorCallback) {
  this.exec('reboot', config, successCallback, errorCallback);
};

/**
 * blocks UI bars on the device
 * @param {Object} config
 * @param {Function} successCallback This function will be called if operation succeed
 * @param {Function} errorCallback This function will be called if an error happened
 */
Handpoint.prototype.blockUiBars = function (config, successCallback, errorCallback) {
  this.exec('blockUiBars', config, successCallback, errorCallback);
};

/*
* blocks UI bars on the device
* @param {Object} config
* @param {Function} successCallback This function will be called if operation succeed
* @param {Function} errorCallback This function will be called if an error happened
*/
Handpoint.prototype.unblockUiBars = function (config, successCallback, errorCallback) {
  this.exec('unblockUiBars', config, successCallback, errorCallback);
};


/*
* enable the volume keys on the device
* @param {Object} config
* @param {Function} successCallback This function will be called if operation succeed
* @param {Function} errorCallback This function will be called if an error happened
*/
Handpoint.prototype.enableVolumeKeys = function (config, successCallback, errorCallback) {
  this.exec('enableVolumeKeys', config, successCallback, errorCallback);
}

/*
* disable the volume keys on the device
* @param {Object} config
* @param {Function} successCallback This function will be called if operation succeed
* @param {Function} errorCallback This function will be called if an error happened
*/
Handpoint.prototype.disableVolumeKeys = function (config, successCallback, errorCallback) {
  this.exec('disableVolumeKeys', config, successCallback, errorCallback);
}

/*
* checks if the device has a WiFi module
* @param {Object} config
* @param {Function} successCallback This function will be called if operation succeed
* @param {Function} errorCallback This function will be called if an error happened
*/
Handpoint.prototype.hasWifiModule = function (config, successCallback, errorCallback) {
  this.exec('hasWifiModule', config, successCallback, errorCallback);
};

/*
* checks if the device has a printer module
* @param {Object} config
* @param {Function} successCallback This function will be called if operation succeed
* @param {Function} errorCallback This function will be called if an error happened
*/
Handpoint.prototype.hasPrinterModule = function (config, successCallback, errorCallback) {
  this.exec('hasPrinterModule', config, successCallback, errorCallback);
};

/*
* checks if the device has physical keyboard
* @param {Object} config
* @param {Function} successCallback This function will be called if operation succeed
* @param {Function} errorCallback This function will be called if an error happened
*/
Handpoint.prototype.hasPhysicalKeyboardModule = function (config, successCallback, errorCallback) {
  this.exec('hasPhysicalKeyboardModule', config, successCallback, errorCallback);
};

/*
* gets the pax device serial number
* @param {Object} config
* @param {Function} successCallback This function will be called if operation succeed
* @param {Function} errorCallback This function will be called if an error happened
*/
Handpoint.prototype.getPaxSerialNumber = function (config, successCallback, errorCallback) {
  this.exec('getPaxSerialNumber', config, successCallback, errorCallback);
};

/*
* gets the pax device model
* @param {Object} config
* @param {Function} successCallback This function will be called if operation succeed
* @param {Function} errorCallback This function will be called if an error happened
*/
Handpoint.prototype.getPaxModel = function (config, successCallback, errorCallback) {
  this.exec('getPaxModel', config, successCallback, errorCallback);
};


/*
* Prints logs with System.err.println to appear on pax detailed logs
* @param {Object} config
* @param {Function} successCallback This function will be called if operation succeed
* @param {Function} errorCallback This function will be called if an error happened
*/
Handpoint.prototype.printDetailedLog = function (config, successCallback, errorCallback) {
  this.exec('printDetailedLog', config, successCallback, errorCallback);
};

/**
 * Gets device SIM info
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.getSimInfo = function (config, successCallback, errorCallback) {
  this.exec('getSimInfo', config, successCallback, errorCallback);
};

/**
 * It checks if the app has permission to read the SIM card
 * @param {*} config
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.hasSimReadPermission = function (config, successCallback, errorCallback) {
  this.exec('hasSimReadPermission', config, successCallback, errorCallback);
};

/**
 * It requests permission to read the SIM card
 * @param {*} config
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.requestSimReadPermission = function (config, successCallback, errorCallback) {
  this.exec('requestSimReadPermission', config, successCallback, errorCallback);
};

/**
 * Enables the auto start of the app on boot
 * @param {*} config
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.enableAutoStart = function (config, successCallback, errorCallback) {
  this.exec('enableAutoStart', config, successCallback, errorCallback);
};

/**
 * Disables the auto start of the app on boot
 * @param {*} config
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.disableAutoStart = function (config, successCallback, errorCallback) {
  this.exec('disableAutoStart', config, successCallback, errorCallback);
};

/**
 *
 * @param {*} config
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.resumeTokenizedOperation = function (config, successCallback, errorCallback) {
  this.exec('resumeTokenizedOperation', config, successCallback, errorCallback);
}

/**
 * Cancels the tokenized operation. In case of a tokenized operation, the card reader will
 * wait for a tokenized operation to be resumed. If the operation is not resumed within a
 * certain time frame, the card reader will cancel the operation.
 * @param {*} config
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.cancelTokenizedOperation = function (successCallback, errorCallback) {
  this.exec('cancelTokenizedOperation', {}, successCallback, errorCallback);
}

/**
 * Resumes an enrich transaction result operation.
 * @param {*} config
 * @param {*} successCallback
 * @param {*} errorCallback
 */
Handpoint.prototype.resumeEnrichOperation = function (config, successCallback, errorCallback) {
  this.exec('resumeEnrichOperation', config, successCallback, errorCallback);
}

Handpoint.prototype.exec = function (method, config, successCallback, errorCallback) {

  if (typeof (config) === 'object') {
    config = [config];
  } else if (!(config instanceof Array)) {
    config = [];
  }

  if (errorCallback && typeof errorCallback != "function") {
    throw new Error("Handpoint." + method + " failure: errorCallback is not a function");
  }

  if (successCallback && typeof successCallback != "function") {
    throw new Error("Handpoint." + method + " failure: successCallback is not a function");
  }

  // Exec
  try {
    exec(function (result) {
      successCallback && successCallback(result);
    }, function (error) {
      errorCallback && errorCallback(error);
    }, 'HandpointApiCordova', method, config);
  } catch (e) {
    errorCallback && errorCallback(e);
  }

};

var handpoint = new Handpoint();
module.exports = handpoint;
