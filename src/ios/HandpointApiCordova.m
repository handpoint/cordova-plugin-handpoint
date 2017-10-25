#import "HandpointApiCordova.h"

@implementation HandpointApiCordova

- (void)pluginInitialize
{

}

- (void)sale:(CDVInvokedUrlCommand*)command
{

}

- (void)saleReversal:(CDVInvokedUrlCommand*)command
{

}

- (void)refund:(CDVInvokedUrlCommand*)command
{

}

- (void)refundReversal:(CDVInvokedUrlCommand*)command
{

}

- (void)cancelRequest:(CDVInvokedUrlCommand*)command
{

}

- (void)tipAdjustment:(CDVInvokedUrlCommand*)command
{

}

- (void)signatureResult:(CDVInvokedUrlCommand*)command
{

}

- (void)connect:(CDVInvokedUrlCommand*)command
{

}

- (void)disconnect:(CDVInvokedUrlCommand*)command
{

}

- (void)setSharedSecret:(CDVInvokedUrlCommand*)command
{

}

- (void)setup:(CDVInvokedUrlCommand*)command
{

    NSString* name = [[command arguments] objectAtIndex:0];
    NSString* msg = [NSString stringWithFormat: @"Hello, %@", name];

    CDVPluginResult* result = [CDVPluginResult
                               resultWithStatus:CDVCommandStatus_OK
                               messageAsString:msg];

    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)setParameter:(CDVInvokedUrlCommand*)command
{

}

- (void)setLogLevel:(CDVInvokedUrlCommand*)command
{

}

- (void)getDeviceLogs:(CDVInvokedUrlCommand*)command
{

}

- (void)getPendingTransaction:(CDVInvokedUrlCommand*)command
{

}

- (void)update:(CDVInvokedUrlCommand*)command
{

}

- (void)listDevices:(CDVInvokedUrlCommand*)command
{

}

- (void)startMonitoringConnections:(CDVInvokedUrlCommand*)command
{

}

- (void)stopMonitoringConnections:(CDVInvokedUrlCommand*)command
{

}

- (void)eventHandler:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end