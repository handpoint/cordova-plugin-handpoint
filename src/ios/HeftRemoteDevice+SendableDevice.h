//
// Created by Juan Nuñez on 19/10/2017.
//

#import <Foundation/Foundation.h>
#import "HeftRemoteDevice.h"

@interface HeftRemoteDevice (SendableDevice)

- (NSDictionary *)sendableDevice;
- (NSString *)macAddress;

@end
