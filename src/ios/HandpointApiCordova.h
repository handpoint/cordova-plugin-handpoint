#import <Cordova/CDV.h>
#import <Cordova/CDVPlugin.h>

#import "HandpointAll.h"

FOUNDATION_EXPORT NSString* CONNECTION_CALLBACK_ID;
FOUNDATION_EXPORT NSString* LIST_DEVICES_CALLBACK_ID;

@interface HandpointApiCordova : CDVPlugin
    
- (void)sale:(CDVInvokedUrlCommand*)command;
- (void)saleReversal:(CDVInvokedUrlCommand*)command;
- (void)refund:(CDVInvokedUrlCommand*)command;
- (void)refundReversal:(CDVInvokedUrlCommand*)command;
- (void)cancelRequest:(CDVInvokedUrlCommand*)command;
- (void)tipAdjustment:(CDVInvokedUrlCommand*)command;
- (void)signatureResult:(CDVInvokedUrlCommand*)command;
- (void)connect:(CDVInvokedUrlCommand*)command;
- (void)disconnect:(CDVInvokedUrlCommand*)command;
- (void)setSharedSecret:(CDVInvokedUrlCommand*)command;
- (void)setup:(CDVInvokedUrlCommand*)command;
- (void)setParameter:(CDVInvokedUrlCommand*)command;
- (void)setLogLevel:(CDVInvokedUrlCommand*)command;
- (void)getDeviceLogs:(CDVInvokedUrlCommand*)command;
- (void)getPendingTransaction:(CDVInvokedUrlCommand*)command;
- (void)update:(CDVInvokedUrlCommand*)command;
- (void)listDevices:(CDVInvokedUrlCommand*)command;
- (void)startMonitoringConnections:(CDVInvokedUrlCommand*)command;
- (void)stopMonitoringConnections:(CDVInvokedUrlCommand*)command;
- (void)eventHandler:(CDVInvokedUrlCommand*)command;
- (void)applicationDidGoBackground:(CDVInvokedUrlCommand*)command;

@end