//
// Created by Juan Nuñez on 19/10/2017.
//

#import <Foundation/Foundation.h>
#import "HeftStatusReportPublic.h"
#import "HeftManager.h"

@interface HandpointHelper : NSObject <HeftDiscoveryDelegate, HeftStatusReportDelegate>

- (void)processCommand:(CDVInvokedUrlCommand *)command delegate:(CDVPlugin *)delegate;

@end