//
// Created by Juan Nuñez on 19/10/2017.
//

#import "SDKEvent.h"

@interface SDKEvent ()

@property (atomic) NSDictionary *event;

@end

@implementation SDKEvent

+ (instancetype)eventWithName:(NSString *)name
                         data:(NSDictionary *)data
{
    return [[SDKEvent alloc] initWithName:name
                                     data:data];
}

- (instancetype)initWithName:(NSString *)name
                        data:(NSDictionary *)data
{
    self = [super init];

    if (self)
    {
        self.event = @{
                       @"event": name,
                       @"data": data
                       };
    }

    return self;
}

- (NSDictionary *)JSON
{
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:self.event options:(NSJSONWritingOptions)(NSJSONWritingPrettyPrinted) error:nil];
    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];

    return self.event;
}


@end
