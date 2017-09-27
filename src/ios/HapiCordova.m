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

    if (echo != nil && [echo length] > 0) {



        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end