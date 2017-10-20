

#import <Cordova/CDVPlugin.h>

#import "HeftClient.h"
#import "HeftManager.h"
#import "HeftStatusReportPublic.h"


@interface HandpointApiCordova : CDVPlugin

- (void)execute:(CDVInvokedUrlCommand*)command;

@end
