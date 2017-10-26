//
// Created by Juan Nuñez on 20/10/2017.
//

#import <Foundation/Foundation.h>
#import <Cordova/CDVInvokedUrlCommand.h>

@interface CDVInvokedUrlCommand (Arguments)

@property (readonly, nonatomic) NSDictionary *params;

@end
