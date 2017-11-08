//
// Created by Juan Nuñez on 19/10/2017.
//

#import "HeftRemoteDevice+SendableDevice.h"

@interface EAAccessory ()
    @property (nonatomic, readonly) NSString * macAddress;
@end

@implementation HeftRemoteDevice (SendableDevice)

- (NSDictionary *)sendableDevice
{
    return @{
            @"name": self.name,
            @"address": self.accessory.macAddress
    };
}

- (NSString *)macAddress
{
    return self.accessory.macAddress;
}

@end
