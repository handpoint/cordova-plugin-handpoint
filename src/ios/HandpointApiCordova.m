#import "HandpointApiCordova.h"
#import "HeftRemoteDevice+SendableDevice.h"
#import "SDKEvent.h"
#import "ConnectionStatus.h"
#import "Currency.h"
#import "HandpointApiManager.h"

#import "CDVInvokedUrlCommand+Arguments.h"
#import "NSString+Sanitize.h"

NSString *CONNECTION_CALLBACK_ID = @"CONNECTION_CALLBACK_ID";
NSString *LIST_DEVICES_CALLBACK_ID = @"LIST_DEVICES_CALLBACK_ID";

@interface HandpointApiCordova () <HandpointSampleBasicEvents>

@property(atomic) HandpointApiManager *api;
@property(nonatomic) NSString *eventHandlerCallbackId;

@end

@implementation HandpointApiCordova

- (void)pluginInitialize
{
    [super pluginInitialize];

    NSLog(@"\n\tpluginInitialize");

}

- (void)setup:(CDVInvokedUrlCommand *)command
{

    NSLog(@"\n\tsetup: %@", command.params);

    BOOL automaticReconnection = command.params[@"automaticReconnection"] ? [command.params[@"automaticReconnection"] boolValue] : YES;

    NSString *sharedSecret = command.params[@"sharedSecret"] ?: @"";

    self.api = [[HandpointApiManager alloc] initWithBasicEventsDelegate:self
                                                            sharedSecret:sharedSecret
                                                  automaticReconnection:automaticReconnection];

    [self sendSuccessWithCallbackId:command.callbackId];
}


- (void)sale:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\tsale: %@", command.params);

    Currency *currency = [Currency currencyFromCode:command.params[@"currency"]];
    NSInteger amount = [command.params[@"amount"] integerValue];
    
    SaleOptions *options = [SaleOptions new];
    NSString *customerReference = [command.params valueForKeyPath:@"options.customerReference"];
    
    if(customerReference.length) {
        options.customerReference = customerReference;
    }
    
    options.merchantAuth = [self getMerchantAuthFromParams:command.params];
    
    BOOL result = [self.api saleWithAmount:amount
                                  currency:currency
                                   options:options];

    if (result)
    {
        [self sendSuccessWithCallbackId:command.callbackId];
    } else
    {
        [self sendErrorWithMessage:@"Can't send sale operation to device" callbackId:command.callbackId];
    }
}

- (void)saleAndTokenizeCard:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\tsaleAndTokenizeCard: %@", command.params);

    Currency *currency = [Currency currencyFromCode:command.params[@"currency"]];
    NSInteger amount = [command.params[@"amount"] integerValue];

    SaleOptions *options = [SaleOptions new];
    NSString *customerReference = [command.params valueForKeyPath:@"options.customerReference"];
    
    if(customerReference.length) {
        options.customerReference = customerReference;
    }
    
    options.merchantAuth = [self getMerchantAuthFromParams:command.params];
    
    BOOL result = [self.api saleAndTokenizeCardWithAmount:amount
                                                 currency:currency
                                                  options:options];

    if (result)
    {
        [self sendSuccessWithCallbackId:command.callbackId];
    } else
    {
        [self sendErrorWithMessage:@"Can't send saleAndTokenizeCard operation to device" callbackId:command.callbackId];
    }
}

- (void)tokenizeCard:(CDVInvokedUrlCommand *)command
{

    NSLog(@"\n\ttokenizeCard: %@", command.params);

    BOOL result = [self.api tokenizeCard];

    if (result)
    {
        [self sendSuccessWithCallbackId:command.callbackId];
    } else
    {
        [self sendErrorWithMessage:@"Can't send tokenizeCard operation to device" callbackId:command.callbackId];
    }
}

- (void)saleReversal:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\tsaleReversal");

    Currency *currency = [Currency currencyFromCode:command.params[@"currency"]];
    NSInteger amount = [command.params[@"amount"] integerValue];
    NSString *originalTransactionID = command.params[@"originalTransactionID"];
    
    Options *options = [Options new];
    NSString *customerReference = [command.params valueForKeyPath:@"options.customerReference"];
    
    if(customerReference.length) {
        options.customerReference = customerReference;
    }
    
    BOOL result = [self.api saleReversalWithAmount:amount
                                          currency:currency
                                     transactionId:originalTransactionID
                                           options:options];

    if (result)
    {
        [self sendSuccessWithCallbackId:command.callbackId];
    } else
    {
        [self sendErrorWithMessage:@"Can't send saleReversal operation to device" callbackId:command.callbackId];
    }
}

- (void)refund:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\trefund");

    Currency *currency = [Currency currencyFromCode:command.params[@"currency"]];
    NSInteger amount = [command.params[@"amount"] integerValue];
    NSString *originalTransactionID = command.params[@"originalTransactionID"];
    
    MerchantAuthOptions *options = [MerchantAuthOptions new];
    NSString *customerReference = [command.params valueForKeyPath:@"options.customerReference"];
    
    if(customerReference.length) {
        options.customerReference = customerReference;
    }
    
    options.merchantAuth = [self getMerchantAuthFromParams:command.params];
    
    BOOL result = [self.api refundWithAmount:amount
                                    currency:currency
                                 transaction:originalTransactionID
                                     options:options];

    if (result)
    {
        [self sendSuccessWithCallbackId:command.callbackId];
    } else
    {
        [self sendErrorWithMessage:@"Can't send refund operation to device" callbackId:command.callbackId];
    }
}

- (void)refundReversal:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\trefundReversal");

    Currency *currency = [Currency currencyFromCode:command.params[@"currency"]];
    NSInteger amount = [command.params[@"amount"] integerValue];
    NSString *originalTransactionID = command.params[@"originalTransactionID"];
    
    Options *options = [Options new];
    NSString *customerReference = [command.params valueForKeyPath:@"options.customerReference"];
    
    if(customerReference.length) {
        options.customerReference = customerReference;
    }
    
    BOOL result = [self.api refundReversalWithAmount:amount
                                            currency:currency
                                       transactionId:originalTransactionID
                                             options:options];

    if (result)
    {
        [self sendSuccessWithCallbackId:command.callbackId];
    } else
    {
        [self sendErrorWithMessage:@"Can't send refundReversal operation to device" callbackId:command.callbackId];
    }
}

- (void)cancelRequest:(CDVInvokedUrlCommand *)command
{
}

- (void)tipAdjustment:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\ttipAdjustment: %@", command.params);
}

- (void)stopCurrentTransaction:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\tstopCurrentTransaction");
}

- (void)signatureResult:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\tsignatureResult");

    BOOL accepted = [command.params[@"accepted"] boolValue];

    [self.api acceptSignature:accepted];

    [self sendSuccessWithCallbackId:command.callbackId];
}

- (void)enableScanner:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\tenableScanner");

    BOOL multiScan = command.params[@"multiScan"] ? [command.params[@"multiScan"] boolValue] : YES;
    BOOL autoScan = command.params[@"autoScan"] ? [command.params[@"autoScan"] boolValue] : NO;
    BOOL sendScannerCodesGrouped = command.params[@"resultsGrouped"] ? [command.params[@"resultsGrouped"] boolValue] : YES;
    NSUInteger timeout = command.params[@"timeout"] ? [command.params[@"timeout"] integerValue] : 0;

    BOOL buttonEnabled = !autoScan;

    BOOL result = [self.api enableScannerWithMultiScan:multiScan
                                            buttonMode:buttonEnabled
                                        timeoutSeconds:timeout
                                sendScannerCodesGrouped:sendScannerCodesGrouped
                                          scannedCodes:^(NSArray *codes) {
                                              [self sendScannedCodes:codes];
                                          }
                                            scannerOff:^{
                                                SDKEvent *event = [SDKEvent eventWithName:@"scannerOff"
                                                                                      data:@{}];

                                                CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                                                              messageAsDictionary:event.JSON];

                                                NSLog(@"\n\tresponseScannerDisabled JSON: %@", event.JSON);

                                                [self sendPluginResult:pluginResult];
                                            }];

    if (result)
    {
        [self sendSuccessWithCallbackId:command.callbackId];
    } else
    {
        [self sendErrorWithMessage:@"Can't enable the scanner" callbackId:command.callbackId];
    }
}

#pragma mark - Device Management

- (void)connect:(CDVInvokedUrlCommand *)command
{
  NSLog(@"\n\tconnect: %@", command.params);

  // NSDictionary *device = command.params[@"device"];
  // HeftRemoteDevice *remoteDevice = self.devices[device[@"address"]];

  [self.api connectToFirstAvailableDevice];
}

- (void)disconnect:(CDVInvokedUrlCommand *)command
{
  NSLog(@"\n\tdisconnect: %@", command.params);

  //[self connectionStatusChanged:ConnectionStatusDisconnected];

  [self.api disconnect];

  [self sendSuccessWithCallbackId:command.callbackId];
}

- (void)setSharedSecret:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\tsetSharedSecret: %@", command.params);

    NSString *ssk = command.params[@"sharedSecret"] ?: @"";

    [self.api setSharedSecret:ssk];

    [self sendSuccessWithCallbackId:command.callbackId];
}

- (void)setParameter:(CDVInvokedUrlCommand *)command
{

    NSLog(@"\n\tsetParameter: %@", command.params);

    [self sendErrorWithMessage:@"Operation not supported." callbackId:command.callbackId];
}

- (void)setLogLevel:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\tsetLogLevel %@", command.params);

    eLogLevel logLevel = (eLogLevel) [command.params[@"logLevel"] integerValue];

    [self.api setLogLevel:logLevel];
}

- (void)setLocale:(CDVInvokedUrlCommand *)command
{

    NSLog(@"\n\tsetLocale %@", command.params);

    // TODO call SDK method
}

- (void)getDeviceLogs:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\tgetDeviceLogs");

    [self sendErrorWithMessage:@"Operation not supported." callbackId:command.callbackId];
}

- (void)getPendingTransaction:(CDVInvokedUrlCommand *)command
{

    if ([self.api getPendingTransaction])
    {
        [self sendSuccessWithCallbackId:command.callbackId];
    } else
    {
        [self sendErrorWithMessage:@"Can't send getPendingTransaction operation to device" callbackId:command.callbackId];
    }
}

- (void)update:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\tupdate");
    [self.api update];
}

- (void)listDevices:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\tlistdevices callback: %@", command.callbackId);


    [self.api listDevices:^(NSArray *devices) {
    NSMutableArray *sendableDevices = [NSMutableArray new];

    for (HeftRemoteDevice *device in devices)
    {
        [sendableDevices addObject:[device sendableDevice]];
    }

    SDKEvent *event = [SDKEvent eventWithName:@"deviceDiscoveryFinished"
                                          data:@{@"devices": sendableDevices}];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:event.JSON];

    NSLog(@"\n\tListDevices result JSON: %@", event.JSON);

    [self sendPluginResult:pluginResult];
}];

[self sendSuccessWithCallbackId:command.callbackId];
}

- (void)startMonitoringConnections:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\tstartMonitoringConnections");

    [self sendErrorWithMessage:@"Operation not supported" callbackId:command.callbackId];
}

- (void)stopMonitoringConnections:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\tstopMonitoringConnections");

    [self sendErrorWithMessage:@"Operation not supported" callbackId:command.callbackId];
}

- (void)eventHandler:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\teventHandler: %@", command.callbackId);

    self.eventHandlerCallbackId = command.callbackId;

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT];
    [self sendPluginResult:pluginResult callbackId:command.callbackId setKeepCallback:YES];
}

- (void)applicationDidGoBackground:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\tapplicationDidGoBackground");
    [self sendSuccessWithCallbackId:command.callbackId];
}

- (void)getSDKVersion:(CDVInvokedUrlCommand *)command
{
    NSLog(@"\n\tgetSDKVersion");
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsString:self.api.version];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

# pragma mark - Callbacks


- (void)connectionStatusChanged:(ConnectionStatus)status
                         device:(HeftRemoteDevice *)device
{
    NSDictionary *data = @{
            @"status": [self stringFromConnectionStatus:status],
            @"device": [device sendableDevice]
    };

    SDKEvent *event = [SDKEvent eventWithName:@"connectionStatusChanged"
                                         data:data];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:event.JSON];

    [self sendPluginResult:pluginResult];
}

- (NSString *)stringFromConnectionStatus:(ConnectionStatus)status
{
    switch (status)
    {
        case ConnectionStatusConnected:
            return @"Connected";
        case ConnectionStatusConnecting:
            return @"Connecting";
        case ConnectionStatusDisconnected:
            return @"Disconnected";
        case ConnectionStatusDisconnecting:
            return @"Diconnecting";
        case ConnectionStatusInitializing:
            return @"Initializing";
        case ConnectionStatusNotConfigured:
        default:
            return @"NotConfigured";
    }
}

- (void)currentTransactionStatus:(id <ResponseInfo>)info
                          device:(HeftRemoteDevice *)device
{
    NSLog(@"\n\tresponseStatus: %@ %@", @(info.statusCode), info.status);

    StatusInfo *statusInfo = [[StatusInfo alloc] initWithDictionary:info.xml
                                                         statusCode:info.statusCode];

    DeviceStatus *deviceStatus = statusInfo.deviceStatus;

    NSDictionary *data = @{
            @"cancelAllowed": @(statusInfo.cancelAllowed),
            @"deviceStatus": deviceStatus.toDictionary,
            @"message": statusInfo.message ?: @"",
            @"status": statusInfo.statusString ?: @""
    };

    NSLog(@"%@", data);

    SDKEvent *event = [SDKEvent eventWithName:@"currentTransactionStatus"
                                         data:@{
                                                 @"info": data,
                                                 @"device": device.sendableDevice
                                         }];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:event.JSON];

    [self sendPluginResult:pluginResult];
}


- (void)responseError:(id <ResponseInfo>)info
{
    NSLog(@"\n\tresponseError: %@", info.status);

    NSDictionary *data = @{@"message": info.status ?: @""};

    NSLog(@"%@", data);

    SDKEvent *event = [SDKEvent eventWithName:@"exception"
                                         data:data];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:event.JSON];

    [self sendPluginResult:pluginResult];
}

- (void)endOfTransaction:(id <FinanceResponseInfo>)transactionResult
                  device:(HeftRemoteDevice *)device
{
    NSLog(@"\n\tresponseFinanceStatus: %@", transactionResult.toDictionary);

    SDKEvent *event = [SDKEvent eventWithName:@"endOfTransaction"
                                         data:@{@"transactionResult": transactionResult.toDictionary}];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:event.JSON];

    [self sendPluginResult:pluginResult];
}


- (void)requestSignature:(NSString *)receipt device:(HeftRemoteDevice *)device
{
    NSLog(@"\n\trequestSignature");

    SDKEvent *event = [SDKEvent eventWithName:@"signatureRequired"
                                         data:@{
                                                 @"device": device.sendableDevice,
                                                 @"merchantReceipt": [receipt sanitize] ?: @""
                                         }];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:event.JSON];

    [self sendPluginResult:pluginResult];
}

- (void)sendScannedCodes:(NSArray *)scannedCodes
{
    SDKEvent *event = [SDKEvent eventWithName:@"scannerResults"
                                         data:@{@"scannedCodes": scannedCodes}];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:event.JSON];

    NSLog(@"\n\tsendScannerResults JSON: %@", event.JSON);

    [self sendPluginResult:pluginResult];
}

- (void)cancelSignature
{
    NSLog(@"\n\tcancelSignature");
}


- (void)sendErrorMessage:(NSString *)message
{
    [self sendErrorWithMessage:message
                    callbackId:self.eventHandlerCallbackId];
}

- (void)sendErrorWithMessage:(NSString *)message
                  callbackId:(NSString *)callbackId
{
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                      messageAsString:message];

    [self sendPluginResult:pluginResult
                callbackId:callbackId
           setKeepCallback:NO];
}

- (void)sendSuccess
{
    [self sendSuccessWithCallbackId:self.eventHandlerCallbackId];
}

- (void)sendSuccessWithCallbackId:(NSString *)callbackId
{
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];

    [self sendPluginResult:pluginResult
                callbackId:callbackId
           setKeepCallback:NO];
}

- (void)sendPluginResult:(CDVPluginResult *)result
{
    [self sendPluginResult:result
                callbackId:self.eventHandlerCallbackId
           setKeepCallback:YES];
}

- (void)sendPluginResult:(CDVPluginResult *)result
              callbackId:(NSString *)callbackId
         setKeepCallback:(BOOL)shouldSetKeepCallback
{
    if (shouldSetKeepCallback)
    {
        [result setKeepCallbackAsBool:YES];
    }

    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
}

- (MerchantAuth *)getMerchantAuthFromParams:(NSDictionary *)params {
    NSArray *credentials = [params valueForKeyPath:@"options.merchantAuth"];
    
    MerchantAuth *auth = [MerchantAuth new];
    
    for (NSDictionary *credential in credentials) {
        Credential *cred = [Credential new];
        cred.acquirer = [Credential getAcquirerFromString:credential[@"acquirer"]];
        cred.mid = credential[@"mid"];
        cred.tid = credential[@"tid"];
        [auth add:cred];
    }
    
    return auth;
}

@end
