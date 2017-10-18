#import "HapiCordova.h"
#import <Cordova/CDVPlugin.h>

@implementation HapiCordova

@synthesize heftClient;

- (void)pluginInitialize
{
    HeftManager* manager = [HeftManager sharedManager];
    manager.delegate = self;
<<<<<<< HEAD
    //TODO do we need this?
=======
>>>>>>> ea376fba59c2fffe9d9176efc804cd4caa25426d
    [manager resetDevices];
}

// map method names - but this could even be a map from string to function pointer, instead of function name
NSDictionary *MethodDictionary = @{
    @"sale" : @"saleWithAmount",
    @"refund" : @"refundWithAmount"
    // ... complete list with the rest of the
    // methods..
};

- (void)execute:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    // NSString* command = [command.arguments objectAtIndex:0];
    NSString* method_name = command.methodName;

    if (method_name != nil && [method_name length] > 0) {

        // we must translate the names, since the methods do not have the same names as in Android/.Net
        NSString* translated_method_name = MethodDictionary[method_name];
        if (translated_method_name != nil)
        {
            // call the given method, with the parameters it needs
            if (translated_method_name == 'saleWithAmount')
            {
                NSInteger amount = 
- (BOOL)saleWithAmount:(NSInteger)amount currency:(NSString*)currency cardholder:(BOOL)present

            }
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
        }
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)didFindAccessoryDevice:(HeftRemoteDevice*)newDevice{
    heftClient = nil;
    [[HeftManager sharedManager] clientForDevice:newDevice sharedSecret:[[NSData alloc] initWithBytes:ss length:sizeof(ss)] delegate:self];
}

// example from testclient, just add methods as needed
- (void)startDiscovery:(BOOL)fDiscoverAllDevices
{
    [[HeftManager sharedManager] startDiscovery:fDiscoverAllDevices];
}

- (void)didConnect:(id<HeftClient>)client{
    NSLog(@"didConnect");
    heftClient = client;
}

- (BOOL)saleWithAmount:(NSInteger)amount currency:(NSString*)currency cardholder:(BOOL)present
{

}

- (BOOL)refundWithAmount:(NSInteger)amount currency:(NSString*)currency cardholder:(BOOL)present
{

}

// device management
- (void)resetDevices
{

}

- (void)clientForDevice:(HeftRemoteDevice*)device sharedSecret:(NSData*)sharedSecret delegate:(NSObject<HeftStatusReportDelegate>*)aDelegate
{
    NSLog(@"hpHeftService clientForDevice");
    [[HeftManager sharedManager] clientForDevice:device sharedSecret:sharedSecret delegate:aDelegate];
}

- (BOOL)financeInit
{

}

- (void) logSetLevel:(eLogLevel)level
{

}

- (BOOL) logReset
{

}
- (BOOL) logGetInfo
{

}

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




@end