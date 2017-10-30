//
// Created by Juan Nuñez on 19/10/2017.
//

#import <Foundation/Foundation.h>


@interface SDKEvent : NSObject

+ (instancetype)eventWithName:(NSString *)name data:(NSDictionary *)data;

- (instancetype)initWithName:(NSString *)name data:(NSDictionary *)data;

- (NSDictionary *)JSON;

@end
