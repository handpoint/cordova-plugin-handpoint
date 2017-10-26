
//
// Created by Juan Nuñez.
//

#import "CDVInvokedUrlCommand+Arguments.h"

@implementation CDVInvokedUrlCommand (Arguments)

- (NSDictionary *)params
{
  return [self.arguments count] ? [self argumentAtIndex:0] : @{};
}

@end
