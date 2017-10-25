//
// Created by Juan Nuñez on 20/10/2017.
//

#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>

@interface CDVPlugin (Callback)

- (void)successWithCallbackId:(NSString *)callbackId;

- (void)errorWithMessage:(NSString *)message callbackId:(NSString *)callbackId;
- (void)sendPluginResult:(CDVPluginResult *)result callbackId:(NSString *)callbackId;

@end
