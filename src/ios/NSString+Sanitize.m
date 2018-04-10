//
//  NSString+Sanitize.m
//  Handpoint Express
//
//  Created by Juan Nuñez on 06/04/2018.
//

#import "NSString+Sanitize.h"

@implementation NSString (Sanitize)

- (NSString *)sanitize
{
    NSArray *characters = @[@"\u2028", @"\u2029", @"\\u2028", @"\\u2029", @"\\\\u2028", @"\\\\u2029"];
    NSString *finalString = self;

    for(NSString *character in characters) 
    {
        finalString = [finalString stringByReplacingOccurrencesOfString:character withString:@""];
    }

    return finalString;
}

@end
