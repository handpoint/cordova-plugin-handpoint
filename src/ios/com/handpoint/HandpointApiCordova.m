#import "HandpointApiCordova.h"
#import "HandpointHelper.h"
#import <Cordova/CDVPlugin.h>

@interface HandpointApiCordova ()

@property (nonatomic) HeftManager* manager;
@property (nonatomic, strong) id<HeftClient> heftClient;
@property (nonatomic) HeftRemoteDevice *selectedDevice;
@property (nonatomic) HandpointHelper *handpointHelper;

@end

@implementation HandpointApiCordova

- (instancetype)init
{
    self = [super init];
    if (self)
    {
        self.handpointHelper = [[HandpointHelper alloc] initWithDelegate:self];
    }
    return self;
}

- (void)execute:(CDVInvokedUrlCommand*)command
{
    [self.handpointHelper processCommand:command
                                delegate:self];
}

@end
