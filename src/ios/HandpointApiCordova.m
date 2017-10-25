#import "HandpointApiCordova.h"
#import "ConnectionStatus.h"
#import "Currency.h"
#import "SDKEvent.h"
#import <Cordova/CDV.h>
#import <Cordova/CDVPlugin.h>
#import "HeftRemoteDevice.h"
#import "CDVInvokedUrlCommand+Arguments.h"
#import "HeftRemoteDevice+SendableDevice.h"
#import "CDVPlugin+Callback.h"

@interface HandpointApiCordova ()

@property (nonatomic) HeftManager* manager;
@property (nonatomic) HeftRemoteDevice* preferredDevice;
@property (nonatomic, strong) id<HeftClient> api;
@property (nonatomic) NSString *ssk;
@property (atomic) NSMutableDictionary *devices;
@property (atomic) NSDictionary *methodPointers;

@property (nonatomic, copy) NSString *callbackId;
@end

@implementation HandpointApiCordova

- (void)pluginInitialize
{
    [super pluginInitialize];
    self.manager = [HeftManager sharedManager];
    self.devices = [@{} mutableCopy];
}

- (void)sale:(CDVInvokedUrlCommand*)command
{
    Currency *currency = command.params[@"currency"];
    NSInteger amount = [command.params[@"amount"] integerValue];

    if ([self.api saleWithAmount:amount
                        currency:currency.sendableCurrencyCode
                      cardholder:YES])
    {
        [self sendSuccess];
    }
    else
    {
        [self sendErrorWithMessage:@"Can't send sale operation to device"];
    }
}

- (void)saleReversal:(CDVInvokedUrlCommand*)command
{
    Currency *currency = command.params[@"currency"];
    NSInteger amount = [command.params[@"amount"] integerValue];
    NSString *originalTransactionID = command.params[@"originalTransactionID"];

    if ([self.api saleVoidWithAmount:amount
                            currency:currency.sendableCurrencyCode
                          cardholder:YES
                         transaction:originalTransactionID])
    {
        [self sendSuccess];
    } else {
        [self sendErrorWithMessage:@"Can't send saleReversal operation to device"];
    }
}

- (void)refund:(CDVInvokedUrlCommand*)command
{
    Currency *currency = command.params[@"currency"];
    NSInteger amount = [command.params[@"amount"] integerValue];

    if ([self.api refundWithAmount:amount
                          currency:currency.sendableCurrencyCode
                        cardholder:YES])
    {
        [self sendSuccess];
    } else {
        [self sendErrorWithMessage:@"Can't send refund operation to device"];
    }
}

- (void)refundReversal:(CDVInvokedUrlCommand*)command
{
    Currency *currency = command.params[@"currency"];
    NSInteger amount = [command.params[@"amount"] integerValue];
    NSString *originalTransactionID = command.params[@"originalTransactionID"];

    if ([self.api refundVoidWithAmount:amount
                              currency:currency.sendableCurrencyCode
                            cardholder:YES
                           transaction:originalTransactionID])
    {
        [self sendSuccess];
    } else {
        [self sendErrorWithMessage:@"Can't send refundReversal operation to device"];
    }
}

- (void) cancelRequest:(CDVInvokedUrlCommand*)command
{

}

- (void) tipAdjustment:(CDVInvokedUrlCommand*)command
{
    
}

- (void) signatureResult:(CDVInvokedUrlCommand*)command
{
    [self.api acceptSignature:YES];
    [self sendSuccess];
}

#pragma mark - Device Management

- (void)connect:(CDVInvokedUrlCommand*)command
{
    NSDictionary *device = command.params[@"device"];

    HeftRemoteDevice *remoteDevice = self.devices[device[@"name"]];


    if(remoteDevice)
    {
        self.preferredDevice = remoteDevice;

        self.ssk = command.params[@"sharedSecret"] ?: self.ssk;

        [self.manager clientForDevice:remoteDevice
                   sharedSecretString:self.ssk
                             delegate:self];

        [self sendSuccess];

        [self connectionStatusChanged:ConnectionStatusConnecting];
        //TODO do dispatch and block here until we have a client
    }
    else
    {
        [self sendErrorWithMessage:@"Can't connect. No device available"];
    }
}

- (void)disconnect:(CDVInvokedUrlCommand*)command
{
    [self sendErrorWithMessage:@"Can't disconnect from device. Not supported."];
}

- (void)setSharedSecret:(CDVInvokedUrlCommand*)command {
    self.ssk = command.params[@"sharedSecret"];
    [self sendSuccess];
}

- (void)setup:(CDVInvokedUrlCommand*)command
{
    self.ssk = command.params[@"sharedSecret"];

    if(self.ssk)
    {

        self.manager.delegate = self;
        //TODO do we need this?
        [self.manager resetDevices];
        [self sendSuccess];
    }
    //TODO Else error
}

- (void) setParameter:(CDVInvokedUrlCommand*)command
{
    
}

- (void) setLogLevel:(CDVInvokedUrlCommand*)command
{
    
}

- (void) getDeviceLogs:(CDVInvokedUrlCommand*)command
{
    
}

- (void)getPendingTransaction:(CDVInvokedUrlCommand*)command
{
    //TODO
   /* if (self.api.getPendingTransaction()) {
        [self sendSuccess];
    } else {
        [self sendErrorWithMessage:@"Can't send getPendingTransaction operation to device"];
    }*/
}

- (void) update:(CDVInvokedUrlCommand*)command
{
    
}

- (void)listDevices:(CDVInvokedUrlCommand*)command
{
    [self.manager resetDevices];
    [self.manager startDiscovery:YES];
    [self sendSuccess];
}

- (void) startMonitoringConnections:(CDVInvokedUrlCommand*)command
{
    
}

- (void) stopMonitoringConnections:(CDVInvokedUrlCommand*)command
{
    
}

- (void) eventHandler:(CDVInvokedUrlCommand*)command
{
    self.callbackId = command.callbackId;
    /**this.callbackContext = callbackContext;

    PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
    result.setKeepCallback(true);
    this.callbackContext.sendPluginResult(result);*/
}
    
- (void)didFindAccessoryDevice:(HeftRemoteDevice *)newDevice
{
    self.devices[newDevice.name] = newDevice;
}

- (void)didLostAccessoryDevice:(HeftRemoteDevice *)oldDevice
{
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
    NSMutableArray *sendableDevices = [@[] mutableCopy];

    for (HeftRemoteDevice *device in self.devices)
    {
        [sendableDevices addObject:device.sendableDevice];
    }

    SDKEvent *event = [SDKEvent eventWithName:@"deviceDiscoveryFinished"
                                         data:@{@"devices": sendableDevices}];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsString:event.JSON];
    [self sendPluginResult:pluginResult];
}

- (void)didConnect:(id <HeftClient>)client
{
    [self connectionStatusChanged:ConnectionStatusConnected];
}

- (void)connectionStatusChanged:(ConnectionStatus)status
{
    if(self.preferredDevice)
    {

        NSDictionary *data = @{
                @"status": [self stringFromConnectionStatus:status],
                @"device": [self.preferredDevice sendableDevice]
        };
        SDKEvent *event = [SDKEvent eventWithName:@"connectionStatusChanged"
                                             data:data];
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                          messageAsString:event.JSON];
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
            return @"Not Configured";
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

}

- (void)responseError:(id <ResponseInfo>)info
{

}

- (void)responseFinanceStatus:(id <FinanceResponseInfo>)info
{
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
    [self sendPluginResult:pluginResult];*/

    /*

- (void)responseFinanceStatus:(id<FinanceResponseInfo>)info;
{
    NSMutableSet *saleSet = [NSMutableSet setWithObjects:@"APPROVED", @"AUTHORISED", @"DECLINED", @"CANCELLED", @"CARD BLOCKED", nil];

    NSLog(@"hpHeftService responseFinanceStatus");
    NSLog(@"%@", info.status);
    NSLog(@"%@", info.xml.description);
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
        NSLog(@"Webview is loading...");
    }

    // and then return some data to javascripot
    [self.commandDelegate sendPluginResult:X callbackId:command.callbackId];

}


*/
}

- (void)responseLogInfo:(id <LogInfo>)info
{

}

- (void)requestSignature:(NSString *)receipt
{

}

- (void)cancelSignature
{

}

- (void)sendErrorWithMessage:(NSString *)string
{
    [self errorWithMessage:string callbackId:self.callbackId];
}

- (void)sendSuccess
{
    [self successWithCallbackId:self.callbackId];
}

- (void)sendPluginResult:(CDVPluginResult *)result
{
    [self.commandDelegate sendPluginResult:result callbackId:self.callbackId];
}

@end
