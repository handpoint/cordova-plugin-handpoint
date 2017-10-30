#import "HandpointApiCordova.h"
#import "HeftRemoteDevice+SendableDevice.h"
#import "SDKEvent.h"
#import "HeftClient.h"
#import "ConnectionStatus.h"
#import "Currency.h"
#import "CDVInvokedUrlCommand+Arguments.h"

NSString* CONNECTION_CALLBACK_ID = @"CONNECTION_CALLBACK_ID";
NSString* LIST_DEVICES_CALLBACK_ID = @"LIST_DEVICES_CALLBACK_ID";

@interface HandpointApiCordova ()

@property (nonatomic) HeftManager* manager;
@property (nonatomic, strong) id<HeftClient> api;
@property (nonatomic) NSString *ssk;
@property (nonatomic) HeftRemoteDevice* preferredDevice;
@property (atomic) NSMutableDictionary *devices;
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
    
    [self initDevices];
    
    self.ssk = @"";
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
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tsaleReversal");
        
        Currency *currency = [Currency currencyFromCode:command.params[@"currency"]];
        NSInteger amount = [command.params[@"amount"] integerValue];
        NSString *originalTransactionID = command.params[@"originalTransactionID"];
        if ([self.api saleVoidWithAmount:amount
                                currency:currency.sendableCurrencyCode
                              cardholder:YES
                             transaction:originalTransactionID])
        {
            [self sendSuccessWithCallbackId:command.callbackId];
        } else {
            [self sendErrorWithMessage:@"Can't send saleReversal operation to device" callbackId:command.callbackId];
        }
    }];
}

- (void)refund:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\trefund");
        
        Currency *currency = [Currency currencyFromCode:command.params[@"currency"]];
        NSInteger amount = [command.params[@"amount"] integerValue];
        if ([self.api refundWithAmount:amount
                              currency:currency.sendableCurrencyCode
                            cardholder:YES])
        {
            [self sendSuccessWithCallbackId:command.callbackId];
        } else {
            [self sendErrorWithMessage:@"Can't send refund operation to device" callbackId:command.callbackId];
        }
    }];
}

- (void)refundReversal:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\trefundReversal");
        
        Currency *currency = [Currency currencyFromCode:command.params[@"currency"]];
        NSInteger amount = [command.params[@"amount"] integerValue];
        NSString *originalTransactionID = command.params[@"originalTransactionID"];
        if ([self.api refundVoidWithAmount:amount
                                  currency:currency.sendableCurrencyCode
                                cardholder:YES
                               transaction:originalTransactionID])
        {
            [self sendSuccessWithCallbackId:command.callbackId];
        } else {
            [self sendErrorWithMessage:@"Can't send refundReversal operation to device" callbackId:command.callbackId];
        }
    }];
}

- (void)cancelRequest:(CDVInvokedUrlCommand*)command
{
    NSLog(@"\n\tcancelRequest");
}

- (void)tipAdjustment:(CDVInvokedUrlCommand*)command
{
    NSLog(@"\n\ttipAdjustment");
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

#pragma mark - Device Management

- (void)connect:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tconnect: %@", command.params);
  
        NSDictionary *device = command.params[@"device"];
        HeftRemoteDevice *remoteDevice = self.devices[device[@"name"]];
        
        if(remoteDevice)
        {
            self.preferredDevice = remoteDevice;
            self.ssk = command.params[@"sharedSecret"] ?: self.ssk;
            [self.manager clientForDevice:remoteDevice
                       sharedSecretString:self.ssk
                                 delegate:self];
            
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
    NSLog(@"\n\tdisconnect");
    /**
    [self sendErrorWithMessage:@"Can't disconnect from device. Not supported."];
    */
}

- (void)setSharedSecret:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSLog(@"\n\tsetSharedSecret: %@", command.params);
        
        self.ssk = command.params[@"sharedSecret"];
        [self sendSuccessWithCallbackId:command.callbackId];
    }];
}

- (void)setup:(CDVInvokedUrlCommand*)command
{

    NSLog(@"\n\tsetup: %@", command.params);
    /**
    self.ssk = command.params[@"sharedSecret"];
    if(self.ssk)
    {
        self.manager.delegate = self;
        //TODO do we need this?
        [self.manager resetDevices];
    }
    //TODO Else error
    */
    
    [self sendSuccessWithCallbackId:command.callbackId];
}

- (void)setParameter:(CDVInvokedUrlCommand*)command
{

    NSLog(@"\n\tsetParameter");
}

- (void)setLogLevel:(CDVInvokedUrlCommand*)command
{

    NSLog(@"\n\tsetLogLevel");
}

- (void)getDeviceLogs:(CDVInvokedUrlCommand*)command
{

    NSLog(@"\n\tgetDeviceLogs");
}

- (void)getPendingTransaction:(CDVInvokedUrlCommand*)command
{
    NSLog(@"\n\tgetPendingTransaction");
    /**
    // TODO
    if (self.api.getPendingTransaction()) {
       [self sendSuccessWithCallbackId:command.callbackId];
    } else {
       [self sendErrorWithMessage:@"Can't send getPendingTransaction operation to device"];
    }
    */
}

- (void)update:(CDVInvokedUrlCommand*)command
{

    NSLog(@"\n\tupdate");
}

- (void)listDevices:(CDVInvokedUrlCommand*)command
{
    NSArray* devices = [self.manager devicesCopy];
    
    NSLog(@"\n\tlistdevices callback: %@ current: %@", command.callbackId, devices);
    
    if(devices.count)
    {
        for(HeftRemoteDevice* device in devices)
        {
            [self didFindAccessoryDevice:device];
        }
        
        [self sendSuccessWithCallbackId:command.callbackId];
        
        [self didDiscoverFinished];
    }
    else
    {
        [self.manager startDiscovery:YES];
        
        [self sendSuccessWithCallbackId:command.callbackId];
    }
}

- (void)startMonitoringConnections:(CDVInvokedUrlCommand*)command
{

    NSLog(@"\n\tstartMonitoringConnections");
}

- (void)stopMonitoringConnections:(CDVInvokedUrlCommand*)command
{
    NSLog(@"\n\tstopMonitoringConnections");
}

- (void)eventHandler:(CDVInvokedUrlCommand*)command
{
    NSLog(@"\n\teventHandler: %@", command.callbackId);
    
    self.eventHandlerCallbackId = command.callbackId;
   
    [self.commandDelegate runInBackground:^{
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT];
        [self sendPluginResult:pluginResult callbackId:command.callbackId setKeepCallback:YES];
    }];
}

-(void)applicationDidGoBackground:(CDVInvokedUrlCommand*)command
{
    NSLog(@"\n\tapplicationDidGoBackground");
    [self.commandDelegate runInBackground:^{
        
        if(self.preferredDevice)
        {
            //TODO change this to a disconnect and clean
            [self didLostAccessoryDevice:self.preferredDevice];
        }
        
        [self sendSuccessWithCallbackId:command.callbackId];
    }];
}

# pragma mark - Callbacks
    
- (void)didFindAccessoryDevice:(HeftRemoteDevice*)newDevice
{
    NSLog(@"\n\tdidFindAccessoryDevice: %@", newDevice.name);
    
    self.devices[newDevice.name] = newDevice;
}

- (void)didLostAccessoryDevice:(HeftRemoteDevice *)oldDevice
{
    NSLog(@"\n\tdidLostAccessoryDevice: %@", oldDevice.name);
    
    if (self.devices[oldDevice.name])
    {
        [self.devices removeObjectForKey:oldDevice.name];
    }
    
    if(self.preferredDevice && self.preferredDevice.name == oldDevice.name)
    {
        [self connectionStatusChanged:ConnectionStatusDisconnected];
        
        self.preferredDevice = nil;
    }
}

- (void)didDiscoverFinished
{
    NSLog(@"\n\tdidDiscoverFinished: %@", [self.manager devicesCopy]);
    
    NSMutableArray *sendableDevices = [@[] mutableCopy];
    
    for (NSString *key in [self.devices allKeys])
    {
        HeftRemoteDevice *device = self.devices[key];
        
        [sendableDevices addObject:[device sendableDevice]];
    }
    
    [self initDevices];
    
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
    
    self.api = client;
    [self connectionStatusChanged:ConnectionStatusConnected];
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
}

- (void)responseError:(id <ResponseInfo>)info
{

    NSLog(@"\n\tresponseError: %@ %@", @(info.statusCode), info.status);
}

- (void)responseFinanceStatus:(id <FinanceResponseInfo>)info
{
    
    NSLog(@"\n\tresponseError: %@", info.status);
    
    /*SDKEvent event = new SDKEvent("endOfTransaction");
    event.put("transactionResult", transactionResult);
    event.put("device", device);
    PluginResult result = new PluginResult(PluginResult.Status.OK, event.toJSONObject());
    result.setKeepCallback(true);
    this.callbackContext.sendPluginResult(result);
    SDKEvent *event = [SDKEvent eventWithName:@"endOfTransaction"
                                         data:@{@"devices": self.devices}];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsString:event.JSON];
    [self sendPluginResult:pluginResult];

    NSMutableSet *saleSet = [NSMutableSet setWithObjects:@"APPROVED", @"AUTHORISED", @"DECLINED", @"CANCELLED", @"CARD BLOCKED", nil];
    NSLog(@"\n\thpHeftService responseFinanceStatus");
    NSLog(@"\n\t%@", info.status);
    NSLog(@"\n\t%@", info.xml.description);
    NSString* financialStatus = [info.xml objectForKey:@"FinancialStatus"];
    if ([saleSet containsObject:financialStatus])
    {
        receipt = [self generateReceipt:info];
        if ([[info customerReceipt] length] > 0 || [[info merchantReceipt] length] > 0) {
            [receiptDelegate addItem:receipt];
        }
        webReceipt = [[UIWebView alloc] initWithFrame:CGRectMake(0, 0, 595, 0)];
        [webReceipt setDelegate:self];
        [webReceipt loadHTMLString:receipt.merchantReceipt baseURL:nil];
        NSLog(@"\n\tWebview is loading...");
    }
    // and then return some data to javascripot
    [self.commandDelegate sendPluginResult:X callbackId:command.callbackId];*/

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
                                                @"merchantReceipt" : receipt
                                                }];
    
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:event.JSON];
    
    [self sendPluginResult:pluginResult];
}

- (void)cancelSignature
{

    NSLog(@"\n\trequestSignature:");
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

- (void)initDevices
{
    for (HeftRemoteDevice *device in [self.manager devicesCopy])
    {
        [self didFindAccessoryDevice:device];
    }
}
    
@end
