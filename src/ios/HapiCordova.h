

#import <Cordova/CDVPlugin.h>

#import "HeftClient.h"
#import "HeftManager.h"
#import "HeftStatusReportPublic.h"


@interface HapiCordova : CDVPlugin <HeftDiscoveryDelegate, HeftStatusReportDelegate>

@property(nonatomic, strong) id<HeftClient> heftClient;

- (void):initialize
- (void):(CDVInvokedUrlCommand*)command;

@end