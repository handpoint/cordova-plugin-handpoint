//
// Created by Juan Nuñez on 20/10/2017.
//

#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>

@interface CDVPlugin (Callback)

- (void)success;
- (void)errorWithMessage:(NSString *)message;
- (void)sendPluginResult:(CDVPluginResult *)result;

@end