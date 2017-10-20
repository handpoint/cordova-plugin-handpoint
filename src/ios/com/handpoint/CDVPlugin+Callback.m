//
// Created by Juan Nuñez on 20/10/2017.
//

#import "CDVPlugin+Callback.h"

@implementation CDVPlugin (Callback)

- (void)successWithCallbackId:(NSString *)callbackId
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];

    [self sendPluginResult:pluginResult callbackId:callbackId];
}

- (void)errorWithMessage:(NSString *)message callbackId:(NSString *)callbackId
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                      messageAsString:message];
    [self sendPluginResult:pluginResult callbackId:callbackId];
}

- (void)sendPluginResult:(CDVPluginResult *)result callbackId:(NSString *)callbackId
{
    [result setKeepCallbackAsBool:YES];

    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
}

@end
