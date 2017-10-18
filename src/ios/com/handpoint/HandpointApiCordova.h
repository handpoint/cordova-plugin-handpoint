

#import <Cordova/CDVPlugin.h>

#import "HeftClient.h"
#import "HeftManager.h"
#import "HeftStatusReportPublic.h"


@interface HapiCordova : CDVPlugin <HeftDiscoveryDelegate, HeftStatusReportDelegate>
{
    id<HeftClient> heftClient;
    HeftRemoteDevice *selectedDevice;
}


@property(nonatomic, strong) id<HeftClient> heftClient;

- (void):initialize
- (void):(CDVInvokedUrlCommand*)command;

// example from testclient, just add methods as needed
- (void)startDiscovery:(BOOL)fDiscoverAllDevices;
- (BOOL)saleWithAmount:(NSInteger)amount currency:(NSString*)currency cardholder:(BOOL)present;
- (BOOL)refundWithAmount:(NSInteger)amount currency:(NSString*)currency cardholder:(BOOL)present;

// device management
- (void)resetDevices;
- (void)clientForDevice:(HeftRemoteDevice*)device sharedSecret:(NSData*)sharedSecret delegate:(NSObject<HeftStatusReportDelegate>*)aDelegate;
- (BOOL)financeInit;

- (void) logSetLevel:(eLogLevel)level;
- (BOOL) logReset;
- (BOOL) logGetInfo;

@end