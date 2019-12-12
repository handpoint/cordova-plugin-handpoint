#import "HandpointApiCordova.h"
#import "HeftRemoteDevice+SendableDevice.h"
#import "SDKEvent.h"
#import "ConnectionStatus.h"
#import "Currency.h"
#import "CDVInvokedUrlCommand+Arguments.h"
#import "NSString+Sanitize.h"

NSString* CONNECTION_CALLBACK_ID = @"CONNECTION_CALLBACK_ID";
NSString* LIST_DEVICES_CALLBACK_ID = @"LIST_DEVICES_CALLBACK_ID";

@interface HandpointApiCordova () <HeftDiscoveryDelegate, HeftStatusReportDelegate>

@property (nonatomic) HeftManager* manager;
@property (nonatomic, strong) id<HeftClient> api;
@property (nonatomic) NSString *ssk;
@property (nonatomic) HeftRemoteDevice* preferredDevice;
@property (atomic) NSMutableDictionary *devices;
@property (atomic) NSMutableArray *scannedCodes;
@property (nonatomic) BOOL sendScannerCodesGrouped;
@property (nonatomic) NSString *eventHandlerCallbackId;

@end

@implementation HandpointApiCordova

- (void)pluginInitialize
{
    [super pluginInitialize];

    NSLog(@"\n\tpluginInitialize");
    self.manager = [HeftManager sharedManager];
    self.manager.delegate = self;
    self.devices = [@{} mutableCopy];
    self.scannedCodes = [@[] mutableCopy];
    self.sendScannerCodesGrouped = NO;

    [self fillDevicesFromconnectedCardReaders];
    
    self.ssk = @"";
}

- (void)saleAndTokenizeCard:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tsaleAndTokenizeCard: %@", command.params);
        
        Currency *currency = [Currency currencyFromCode:command.params[@"currency"]];
        NSInteger amount = [command.params[@"amount"] integerValue];
        
        BOOL result = [self.api saleAndTokenizeCardWithAmount:amount
                                                     currency:currency.sendableCurrencyCode];
        
        if (result)
        {
            [self sendSuccessWithCallbackId:command.callbackId];
        }
        else
        {
            [self sendErrorWithMessage:@"Can't send saleAndTokenizeCard operation to device" callbackId:command.callbackId];
        }
    }];
}

- (void)tokenizeCard:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\ttokenizeCard: %@", command.params);
        
        BOOL result = [self.api tokenizeCard];
        
        if (result)
        {
            [self sendSuccessWithCallbackId:command.callbackId];
        }
        else
        {
            [self sendErrorWithMessage:@"Can't send tokenizeCard operation to device" callbackId:command.callbackId];
        }
    }];
}

- (void)sale:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tsale: %@", command.params);
        
        Currency *currency = [Currency currencyFromCode:command.params[@"currency"]];
        NSInteger amount = [command.params[@"amount"] integerValue];
        
        BOOL result = [self.api saleWithAmount:amount
                                      currency:currency.sendableCurrencyCode
                                    cardholder:YES];
        
        if (result)
        {
            [self sendSuccessWithCallbackId:command.callbackId];
        }
        else
        {
            [self sendErrorWithMessage:@"Can't send sale operation to device" callbackId:command.callbackId];
        }
    }];
}

- (void)saleReversal:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^
    {
        NSLog(@"\n\tsaleReversal");

        Currency *currency = [Currency currencyFromCode:command.params[@"currency"]];
        NSInteger amount = [command.params[@"amount"] integerValue];
        NSString *originalTransactionID = command.params[@"originalTransactionID"];
        BOOL result = [self.api saleVoidWithAmount:amount
                                          currency:currency.sendableCurrencyCode
                                        cardholder:YES
                                       transaction:originalTransactionID];

        if (result)
        {
            [self sendSuccessWithCallbackId:command.callbackId];
        }
        else
        {
            [self sendErrorWithMessage:@"Can't send saleReversal operation to device" callbackId:command.callbackId];
        }
    }];
}

- (void)refund:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^
    {
        NSLog(@"\n\trefund");

        Currency *currency = [Currency currencyFromCode:command.params[@"currency"]];
        NSInteger amount = [command.params[@"amount"] integerValue];
        BOOL result = [self.api refundWithAmount:amount
                                        currency:currency.sendableCurrencyCode
                                      cardholder:YES];

        if (result)
        {
            [self sendSuccessWithCallbackId:command.callbackId];
        }
        else
        {
            [self sendErrorWithMessage:@"Can't send refund operation to device" callbackId:command.callbackId];
        }
    }];
}

- (void)refundReversal:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^
    {
        NSLog(@"\n\trefundReversal");

        Currency *currency = [Currency currencyFromCode:command.params[@"currency"]];
        NSInteger amount = [command.params[@"amount"] integerValue];
        NSString *originalTransactionID = command.params[@"originalTransactionID"];
        BOOL result = [self.api refundVoidWithAmount:amount
                                            currency:currency.sendableCurrencyCode
                                          cardholder:YES
                                         transaction:originalTransactionID];

        if (result)
        {
            [self sendSuccessWithCallbackId:command.callbackId];
        }
        else
        {
            [self sendErrorWithMessage:@"Can't send refundReversal operation to device" callbackId:command.callbackId];
        }
    }];
}

- (void)cancelRequest:(CDVInvokedUrlCommand*)command {}

- (void)tipAdjustment:(CDVInvokedUrlCommand*)command
{
    NSLog(@"\n\ttipAdjustment: %@", command.params);
}

- (void)signatureResult:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tsignatureResult");
        
        BOOL accepted = [command.params[@"accepted"] boolValue];
        
        [self.api acceptSignature:accepted];
        
        [self sendSuccessWithCallbackId:command.callbackId];
    }];
}

- (void)enableScanner:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tenableScanner");
        
        self.scannedCodes = [@[] mutableCopy];

        BOOL multiScan = command.params[@"multiScan"] ? [command.params[@"multiScan"] boolValue] : YES;
        BOOL autoScan = command.params[@"autoScan"] ? [command.params[@"autoScan"] boolValue] : NO;
        self.sendScannerCodesGrouped = command.params[@"resultsGrouped"] ? [command.params[@"resultsGrouped"] boolValue] : YES;
        NSUInteger timeout = command.params[@"timeout"] ? [command.params[@"timeout"] integerValue] : 0;

        BOOL buttonEnabled = !autoScan;

        BOOL result = [self.api enableScannerWithMultiScan:multiScan buttonMode:buttonEnabled timeoutSeconds:timeout];
        
        if (result)
        {
            [self sendSuccessWithCallbackId:command.callbackId];
        }
        else
        {
            [self sendErrorWithMessage:@"Can't enable the scanner" callbackId:command.callbackId];
        }
    }];
}

#pragma mark - Device Management

- (void)connect:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tconnect: %@", command.params);
  
        NSDictionary *device = command.params[@"device"];
        HeftRemoteDevice *remoteDevice = self.devices[device[@"address"]];
        
        if(remoteDevice)
        {
            NSString *newSSK = command.params[@"sharedSecret"];
            self.ssk = (newSSK && ![newSSK isEqualToString:@""]) ? newSSK : self.ssk;
            
            BOOL isRemoteDeviceSameAsPreferred = self.preferredDevice &&
            [self.preferredDevice.address isEqualToString:remoteDevice.address];
            
            // If we are already connected to this device, update shared secret
            if (self.api && isRemoteDeviceSameAsPreferred)
            {
                // May the Force be with me
                self.api.sharedSecret = self.ssk;
            }
            else
            {
                self.preferredDevice = remoteDevice;
                
                [self.manager clientForDevice:remoteDevice
                                 sharedSecret:self.ssk
                                     delegate:self];
            }
            
            [self sendSuccessWithCallbackId:command.callbackId];
            
            [self connectionStatusChanged:ConnectionStatusConnecting];
            //TODO do dispatch and block here until we have a client
        }
        else
        {
            [self sendErrorWithMessage:@"Can't connect. No device available"
                            callbackId:command.callbackId];
        }
    }];
}
    
- (void)disconnect:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tdisconnect: %@", command.params);

        [self connectionStatusChanged:ConnectionStatusDisconnected];
        
        self.api = nil;
        self.preferredDevice = nil;
        
        [self sendSuccessWithCallbackId:command.callbackId];
    }];
}
    
- (void)setSharedSecret:(CDVInvokedUrlCommand*)command
{
    
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tsetSharedSecret: %@", command.params);
        
        self.ssk = command.params[@"sharedSecret"];
        // If we are already connected to this device, update shared secret
        if (self.api && self.ssk && ![self.ssk isEqualToString:@""])
        {
            self.api.sharedSecret = self.ssk;
        }
        [self sendSuccessWithCallbackId:command.callbackId];
    }];
}
    
- (void)setup:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tsetup: %@", command.params);
        
        [self sendSuccessWithCallbackId:command.callbackId];
    }];
}
    
- (void)setParameter:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tsetParameter: %@", command.params);
        
        [self sendErrorWithMessage:@"Operation not supported." callbackId:command.callbackId];
    }];
}
    
- (void)setLogLevel:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tsetLogLevel %@", command.params);
        
        eLogLevel logLevel = (int)[command.params[@"logLevel"] integerValue];
        
        [self.api logSetLevel:logLevel];
    }];
}

- (void)setLocale:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tsetLocale %@", command.params);
        
        // TODO call SDK method
    }];
}

- (void)getDeviceLogs:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tgetDeviceLogs");
        [self.api logGetInfo];
    }];
}

- (void)getPendingTransaction:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tgetPendingTransaction");
        
        BOOL success = NO;
        
        if ([self.api isTransactionResultPending])
        {
            success = [self.api retrievePendingTransaction];
        }
        
        if (success)
        {
            [self sendSuccessWithCallbackId:command.callbackId];
        }
        else
        {
            [self sendErrorWithMessage:@"Can't send getPendingTransaction operation to device" callbackId:command.callbackId];
        }
    }];
}

- (void)update:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tupdate");
        [self.api financeInit];
    }];
}

- (void)listDevices:(CDVInvokedUrlCommand*)command
{
    NSArray* devices = [self.manager connectedCardReaders];
    
    NSLog(@"\n\tlistdevices callback: %@ current: %@", command.callbackId, devices);
    
    if(devices.count)
    {
        [self.commandDelegate runInBackground:^{
            for(HeftRemoteDevice* device in devices)
            {
                [self addDevice:device];
            }
            
            [self sendSuccessWithCallbackId:command.callbackId];
            
            [self didDiscoverFinished];
        }];
    }
    else
    {
        [self.manager startDiscovery];
        
        [self sendSuccessWithCallbackId:command.callbackId];
    }
}

- (void)startMonitoringConnections:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tstartMonitoringConnections");
        
        [self sendErrorWithMessage:@"Operation not supported" callbackId:command.callbackId];
    }];
}

- (void)stopMonitoringConnections:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tstopMonitoringConnections");
        
        [self sendErrorWithMessage:@"Operation not supported" callbackId:command.callbackId];
    }];
}

- (void)eventHandler:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\teventHandler: %@", command.callbackId);
    
        self.eventHandlerCallbackId = command.callbackId;
   
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT];
        [self sendPluginResult:pluginResult callbackId:command.callbackId setKeepCallback:YES];
    }];
}

- (void)applicationDidGoBackground:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tapplicationDidGoBackground");
        [self sendSuccessWithCallbackId:command.callbackId];
    }];
}

- (void)getSDKVersion:(CDVInvokedUrlCommand*)command
{
    NSLog(@"\n\tgetSDKVersion");
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
        messageAsString:self.manager.version];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

# pragma mark - Callbacks
    
- (void)didFindAccessoryDevice:(HeftRemoteDevice*)newDevice
{
    NSLog(@"\n\tdidFindAccessoryDevice: %@", newDevice.name);
    
    [self addDevice:newDevice];
}

- (void)didLostAccessoryDevice:(HeftRemoteDevice *)oldDevice
{
    NSLog(@"\n\tdidLostAccessoryDevice: %@", oldDevice.name);
    
    [self removeDevice:oldDevice];
    
    if(self.preferredDevice && [self.preferredDevice.address isEqualToString:oldDevice.address])
    {
        [self connectionStatusChanged:ConnectionStatusDisconnected];
        
        self.preferredDevice = nil;
        self.api = nil;
    }
}

- (void)didDiscoverFinished
{
    NSLog(@"\n\tdidDiscoverFinished: %@", [self.manager connectedCardReaders]);

    [self fillDevicesFromconnectedCardReaders];

    NSMutableArray *sendableDevices = [@[] mutableCopy];
    
    for (NSString *key in [self.devices allKeys])
    {
        HeftRemoteDevice *device = self.devices[key];
        
        [sendableDevices addObject:[device sendableDevice]];
    }

    SDKEvent *event = [SDKEvent eventWithName:@"deviceDiscoveryFinished"
                                         data:@{@"devices": sendableDevices}];
    
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:event.JSON];
    
    NSLog(@"\n\tListDevices result JSON: %@", event.JSON);
    
    [self sendPluginResult:pluginResult];
}

- (void)didConnect:(id <HeftClient>)client
{
    NSLog(@"\n\tdidConnect: %@", client.mpedInfo);
    
    if(client)
    {
        self.api = client;
        
        [self connectionStatusChanged:ConnectionStatusConnected];
    }
}

- (void)connectionStatusChanged:(ConnectionStatus)status
{
    NSLog(@"\n\tconnectionStatusChanged: %@ device:%@", [self stringFromConnectionStatus:status], self.preferredDevice ? self.preferredDevice.name : @"NULL");
    
    if(self.preferredDevice)
    {
        NSDictionary *data = @{
                @"status": [self stringFromConnectionStatus:status],
                @"device": [self.preferredDevice sendableDevice]
        };
        
        SDKEvent *event = [SDKEvent eventWithName:@"connectionStatusChanged"
                                             data:data];
        
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsDictionary:event.JSON];
        
        [self sendPluginResult:pluginResult];
    }
    else
    {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                          messageAsString:@"No preferred device found"];
        
        [self sendPluginResult:pluginResult];
    }
}

- (NSString *)stringFromConnectionStatus:(ConnectionStatus)status
{
    switch(status)
    {
        case ConnectionStatusNotConfigured:
            return @"NotConfigured";
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
    }
}

- (void)responseStatus:(id <ResponseInfo>)info
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
                                         data: @{
                                                 @"info": data,
                                                 @"device": self.preferredDevice.sendableDevice
                                                 }];
    
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:event.JSON];
    
    [self sendPluginResult:pluginResult];
}

- (void)responseError:(id <ResponseInfo>)info
{
    NSLog(@"\n\tresponseError: %@", info.status);
    
    NSDictionary *data = @{@"message": info.status ?: @""};
    
    NSLog(@"%@", data);
    
    SDKEvent *event = [SDKEvent eventWithName:@"exception"
                                         data: data];
    
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:event.JSON];
    
    [self sendPluginResult:pluginResult];
}

- (void)responseFinanceStatus:(id <FinanceResponseInfo>)info
{
    NSLog(@"\n\tresponseFinanceStatus: %@", info.toDictionary);
    
    SDKEvent *event = [SDKEvent eventWithName:@"endOfTransaction"
                                         data: @{@"transactionResult":info.toDictionary}];
    
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:event.JSON];
    
    [self sendPluginResult:pluginResult];
}

- (void)responseLogInfo:(id <LogInfo>)info
{

    NSLog(@"\n\tresponseLogInfo: %@", info.status);
}

- (void)requestSignature:(NSString *)receipt
{
    NSLog(@"\n\trequestSignature");
    
    SDKEvent *event = [SDKEvent eventWithName:@"signatureRequired"
                                         data:@{
                                                @"device": self.preferredDevice.sendableDevice,
                                                @"merchantReceipt" : [receipt sanitize] ?: @""
                                                }];
    
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:event.JSON];
    
    [self sendPluginResult:pluginResult];
}

- (void)responseScannerEvent:(id <ScannerEventResponseInfo>)info
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"scanner event: %@", info.scanCode);

        if (self.sendScannerCodesGrouped)
        {
            [self.scannedCodes addObject:info.scanCode];
        }
        else
        {
            [self sendScannerResults:@[info.scanCode]];
        }
    }];
}

- (void)responseScannerDisabled:(id <ScannerDisabledResponseInfo>)info
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"Scanner Off");

        if (self.sendScannerCodesGrouped && self.scannedCodes.count)
        {
            [self sendScannerResults:[self.scannedCodes copy]];
        }

        self.scannedCodes = [@[] mutableCopy];


        SDKEvent *event = [SDKEvent eventWithName:@"scannerOff"
                                             data:@{}];
        
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsDictionary:event.JSON];
        
        NSLog(@"\n\tresponseScannerDisabled JSON: %@", event.JSON);
        
        [self sendPluginResult:pluginResult];
    }];
}

- (void)sendScannerResults:(NSArray *)scannedCodes
{
    SDKEvent *event = [SDKEvent eventWithName:@"scannerResults"
                                         data:@{@"scannedCodes": scannedCodes}];
    
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:event.JSON];
    
    NSLog(@"\n\tsendScannerResults JSON: %@", event.JSON);
    
    [self sendPluginResult:pluginResult];
}

- (void)cancelSignature
{
    NSLog(@"\n\tcancelSignature");
}

- (void)addDevice:(HeftRemoteDevice *)device
{
    self.devices[device.address] = device;
}

- (void)removeDevice:(HeftRemoteDevice *)device
{
    if (self.devices[device.address])
    {
        [self.devices removeObjectForKey:device.address];
    }
}

- (void)sendErrorMessage:(NSString *)message
{
    [self sendErrorWithMessage:message
                    callbackId:self.eventHandlerCallbackId];
}

- (void)sendErrorWithMessage:(NSString *)message
                  callbackId:(NSString *)callbackId
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
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
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    
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
    if(shouldSetKeepCallback)
    {
        [result setKeepCallbackAsBool:YES];
    }
    
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
}

- (void)fillDevicesFromconnectedCardReaders
{
    for (HeftRemoteDevice *device in [self.manager connectedCardReaders])
    {
        [self addDevice:device];
    }
}
    
@end
