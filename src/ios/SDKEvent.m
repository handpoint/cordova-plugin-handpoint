//
// Created by Juan Nuñez on 19/10/2017.
//

#import "SDKEvent.h"

@interface SDKEvent ()

@property (atomic) NSDictionary *data;

@end

@implementation SDKEvent

+ (instancetype)eventWithName:(NSString *)name
                         data:(NSDictionary *)data
{
    return [[self class] initWithName:name data:data];
}

- (instancetype)initWithName:(NSString *)name
                        data:(NSDictionary *)data
{
    self = [super init];

    if (self)
    {
        self.data = data;
    }

    return self;
}

- (NSString *)JSON
{
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:self.data options:kNilOptions error:nil];
    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];

    return jsonString;
}


@end