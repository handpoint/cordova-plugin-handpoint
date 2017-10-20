//
// Created by Juan Nuñez on 19/10/2017.
//

#import "HeftRemoteDevice+SendableDevice.h"


@implementation HeftRemoteDevice (SendableDevice)

- (NSDictionary *)sendableDevice
{
    {
        return @{
                @"name": self.name,
                @"address": self.address
        };
    }
}

@end