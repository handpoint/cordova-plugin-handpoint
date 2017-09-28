#import "HapiCordova.h"
#import <Cordova/CDVPlugin.h>

@implementation HapiCordova

@synthesize heftClient;

- (void)pluginInitialize
{
    HeftManager* manager = [HeftManager sharedManager];
    manager.delegate = self;
    [manager resetDevices];
}

- (void)execute:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* command = [command.arguments objectAtIndex:0];
    NSString* method_name = command.methodName;

    if (method_name != nil && [method_name length] > 0) {

        // we must translate the names, since the methods do not have the same names as in Android/.Net

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
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



@end