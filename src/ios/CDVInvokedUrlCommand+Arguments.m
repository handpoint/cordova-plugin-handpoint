
//
// Created by Juan Nuñez.
//

#import "CDVInvokedUrlCommand+Arguments.h"

@implementation CDVPlugin (Callback)

- (NSDictionary *)params
{
  [self.arguments count] ? [command argumentAtIndex:0] : @{};
}

@end
