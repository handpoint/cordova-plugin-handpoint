//
// Created by Juan Nuñez on 19/10/2017.
//

#import <Cordova/CDVPlugin.h>
#import "HandpointHelper.h"
#import "Currency.h"
#import "HeftClient.h"
#import "HeftRemoteDevice.h"
#import "SDKEvent.h"
#import "HeftRemoteDevice+SendableDevice.h"
#import "ConnectionStatus.h"
#import "CDVPlugin+Callback.h"


@interface HandpointHelper ()

@property (nonatomic) HeftManager* manager;
@property (nonatomic) HeftRemoteDevice* preferredDevice;
@property (nonatomic, strong) id<HeftClient> api;
@property (nonatomic) NSString *sharedSecret;
@property (atomic) NSMutableDictionary *devices;
@property (atomic) NSDictionary *methodPointers;
@property (nonatomic) CDVPlugin *delegate;
//@property (nonatomic) id<CDVCommandDelegate> commandDelegate;

@property (nonatomic, copy) NSString *callbackId;
@end

@implementation HandpointHelper

- (instancetype)initWithDelegate:(CDVPlugin *)delegate
{
    self = [super init];
    if (self)
    {
        self.delegate = delegate;
        self.manager = [HeftManager sharedManager];
        self.devices = [@{} mutableCopy];
        self.methodPointers = [HandpointHelper getMethodPointers];
    }
    return self;
}

+ (NSDictionary *)getMethodPointers
{
    return @{
            @"sale": [NSValue valueWithPointer:@selector(sale:params:)],
            @"saleReversal": [NSValue valueWithPointer:@selector(saleReversal:params:)],
            @"refund": [NSValue valueWithPointer:@selector(refund:params:)],
            @"refundReversal": [NSValue valueWithPointer:@selector(refundReversal:params:)],
            @"signatureResult": [NSValue valueWithPointer:@selector(signatureResult:params:)],
            @"connect": [NSValue valueWithPointer:@selector(connect:params:)],
            @"disconnect": [NSValue valueWithPointer:@selector(disconnect:params:)],
            @"setSharedSecret": [NSValue valueWithPointer:@selector(setSharedSecret:params:)],
            @"getPendingTransaction": [NSValue valueWithPointer:@selector(getPendingTransaction:params:)],
            @"listDevices": [NSValue valueWithPointer:@selector(listDevices:params:)],
            @"eventHandler": [NSValue valueWithPointer:@selector(eventHandler:params:)]
    };
}

- (void)setup:(id<CDVCommandDelegate>)delegate params:(NSDictionary *)params
{
    self.sharedSecret = params[@"sharedSecret"];

    if(self.sharedSecret)
    {

        self.manager.delegate = self;
        //TODO do we need this?
        [self.manager resetDevices];
        [self sendSuccess];
    }
    //TODO Else error
}

- (void)processCommand:(CDVInvokedUrlCommand*)command delegate:(CDVPlugin *)delegate
{
    NSDictionary* arguments = ([command.arguments count] > 0) ? [command argumentAtIndex:0] : [NSDictionary new];
    self.callbackId = command.callbackId;

    if (arguments.count == 0)
    {
        //MAYHEM, FAIL
        //CDVPluginResult* pluginResult = [[CDVPluginResult alloc] init];
        //pluginResult.status = CDVCommandStatus_ERROR;
        //[self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

    }
    else
    {
        NSString *methodName = arguments[@"callback"];
        NSString *requestIdStr = arguments[@"requestId"];

        SEL selector = [self.methodPointers[methodName] pointerValue];

        [self performSelector:selector withObject:delegate withObject:arguments];
    }
}

#pragma mark - Transactions

- (void)sale:(id<CDVCommandDelegate>)delegate params:(NSDictionary *)params
{
    Currency *currency = params[@"currency"];
    NSInteger amount = [params[@"amount"] integerValue];

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

- (void)sendErrorWithMessage:(NSString *)string
{
    [self.delegate errorWithMessage:string
                         callbackId:self.callbackId];
}

- (void)sendSuccess
{
    [self.delegate successWithCallbackId:self.callbackId];
}

- (void)saleReversal:(id<CDVCommandDelegate>)delegate params:(NSDictionary *)params
{
    Currency *currency = params[@"currency"];
    NSInteger amount = [params[@"amount"] integerValue];
    NSString *originalTransactionID = params[@"originalTransactionID"];

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

- (void)refund:(id<CDVCommandDelegate>)delegate params:(NSDictionary *)params
{
    Currency *currency = params[@"currency"];
    NSInteger amount = [params[@"amount"] integerValue];

    if ([self.api refundWithAmount:amount
                          currency:currency.sendableCurrencyCode
                        cardholder:YES])
    {
        [self sendSuccess];
    } else {
        [self sendErrorWithMessage:@"Can't send refund operation to device"];
    }
}

- (void)refundReversal:(id<CDVCommandDelegate>)delegate params:(NSDictionary *)params
{
    Currency *currency = params[@"currency"];
    NSInteger amount = [params[@"amount"] integerValue];
    NSString *originalTransactionID = params[@"originalTransactionID"];

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

- (void)signatureResult:(id<CDVCommandDelegate>)delegate params:(NSDictionary *)params
{
    [self.api acceptSignature:YES];
    [self sendSuccess];
}

#pragma mark - Device Management

- (void)connect:(id<CDVCommandDelegate>)delegate params:(NSDictionary *)params
{
    NSDictionary *device = params[@"device"];

    HeftRemoteDevice *remoteDevice = self.devices[device[@"name"]];


    if(remoteDevice)
    {
        self.preferredDevice = remoteDevice;

        self.sharedSecret = params[@"sharedSecret"] ?: self.sharedSecret;

        [self.manager clientForDevice:remoteDevice
                   sharedSecretString:self.sharedSecret
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

- (void)disconnect:(id<CDVCommandDelegate>)delegate params:(NSDictionary *)params
{
    [self sendErrorWithMessage:@"Can't disconnect from device. Not supported."];
}

- (void)setSharedSecret:(id<CDVCommandDelegate>)delegate params:(NSDictionary *)params {
    self.sharedSecret = params[@"sharedSecret"];
    [self sendSuccess];
}

- (void)getPendingTransaction:(id<CDVCommandDelegate>)delegate params:(NSDictionary *)params
{
    //TODO
   /* if (self.api.getPendingTransaction()) {
        [self sendSuccess];
    } else {
        [self sendErrorWithMessage:@"Can't send getPendingTransaction operation to device"];
    }*/
}

- (void)listDevices:(id<CDVCommandDelegate>)delegate params:(NSDictionary *)params
{
    [self.manager resetDevices];
    [self.manager startDiscovery:YES];
    [self sendSuccess];
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
    [self.delegate sendPluginResult:pluginResult
                         callbackId:self.callbackId];
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
        [self.delegate sendPluginResult:pluginResult
                             callbackId:self.callbackId];
    }
    else
    {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                          messageAsString:@"No preferred device found"];
        [self.delegate sendPluginResult:pluginResult
                             callbackId:self.callbackId];
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


@end
