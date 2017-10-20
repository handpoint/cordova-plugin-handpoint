//
// Created by Juan Nuñez on 20/10/2017.
//

#import "CDVPlugin+Callback.h"
#import "CDVCommandDelegate.h"


@implementation CDVPlugin (Callback)

- (void)success
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];

    [self sendPluginResult:pluginResult];
}

- (void)errorWithMessage:(NSString *)message
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                      messageAsString:message];
    [self sendPluginResult:pluginResult];
}

- (void)sendPluginResult:(CDVPluginResult *)result
{
    [result setKeepCallbackAsBool:YES];

    [self.commandDelegate sendPluginResult:result];
}

@end