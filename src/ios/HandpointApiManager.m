#import "HandpointApiManager.h"
#import "ConnectionStatus.h"
#import "Currency.h"

static const NSTimeInterval WAIT_TIME_BETWEEN_RECONNECTIONS = 3.0;

@interface HandpointApiManager () <HeftDiscoveryDelegate, HeftStatusReportDelegate>

@property(nonatomic) HeftManager *manager;
@property(nonatomic, strong) id <HeftClient> api;
@property(nonatomic) NSString *ssk;
@property(nonatomic) BOOL automaticReconnection;
@property(nonatomic) HeftRemoteDevice *preferredDevice;
@property(atomic) NSMutableDictionary *devices;
@property(atomic) NSMutableArray *scannedCodes;
@property(nonatomic) BOOL sendScannerCodesGrouped;
@property(atomic) id <HandpointSampleBasicEvents> delegate;
@property(nonatomic, copy, nullable) ArrayBlock globalDeviceDiscoveryBlock;
@property(nonatomic, copy, nullable) VoidBlock deviceConnectionSuccessBlock;
@property(nonatomic, copy, nullable) VoidBlock deviceConnectionErrorBlock;
@property(nonatomic, copy, nullable) ArrayBlock globalScannedCodesBlock;
@property(nonatomic, copy, nullable) VoidBlock scannerOffBlock;

@end

@implementation HandpointApiManager

- (instancetype)initWithBasicEventsDelegate:(id <HandpointSampleBasicEvents>)delegate
                               sharedSecret:(NSString *)sharedSecret
                      automaticReconnection:(BOOL)automaticReconnection
{
    self = [super init];
    if (self)
    {
        NSLog(@"\n\tinitialize");
        self.manager = [HeftManager sharedManager];
        self.manager.delegate = self;
        self.ssk = sharedSecret;
        self.automaticReconnection = automaticReconnection;
        self.devices = [@{} mutableCopy];
        self.scannedCodes = [@[] mutableCopy];
        self.sendScannerCodesGrouped = NO;
        self.delegate = delegate;

        [self fillDevicesFromconnectedCardReaders];
    }

    return self;
}

- (BOOL)saleWithAmount:(NSInteger)amount
              currency:(Currency *)currency
{
    NSLog(@"\n\tsaleWithAmount: %@ currency: %@", @(amount), currency.alpha);

    return [self.api saleWithAmount:amount
                           currency:currency.sendableCurrencyCode
                         cardholder:YES];
}


- (BOOL)saleAndTokenizeCardWithAmount:(NSInteger)amount
                             currency:(Currency *)currency
{
    NSLog(@"\n\tsaleAndTokenizeCardWithAmount: %@ currency: %@", @(amount), currency.alpha);

    return [self.api saleAndTokenizeCardWithAmount:amount
                                          currency:currency.sendableCurrencyCode];
}

- (BOOL)tokenizeCard
{
    NSLog(@"\n\ttokenizeCard");

    return [self.api tokenizeCard];
}

- (BOOL)saleReversalWithAmount:(NSInteger)amount
                      currency:(Currency *)currency
                 transactionId:(NSString *)transactionId
{
    NSLog(@"\n\tsaleReversalWithAmount: %@ %@ %@", @(amount), currency.alpha, transactionId);

    return [self.api saleVoidWithAmount:amount
                               currency:currency.sendableCurrencyCode
                             cardholder:YES
                            transaction:transactionId];
}

- (BOOL)refundWithAmount:(NSInteger)amount
                currency:(Currency *)currency
{
    NSLog(@"\n\trefundWithAmount: %@ %@", @(amount), currency.alpha);

    return [self.api refundWithAmount:amount
                             currency:currency.sendableCurrencyCode
                           cardholder:YES];
}

- (BOOL)refundWithAmount:(NSInteger)amount
                currency:(Currency *)currency
                transaction:(NSString *)transaction
{
    NSLog(@"\n\trefundWithAmount: %@ %@ %@", @(amount), currency.alpha, transaction);

    return [self.api refundWithAmount:amount
                          currency:currency.sendableCurrencyCode
                          transaction:transaction];
}

- (BOOL)refundReversalWithAmount:(NSInteger)amount
                        currency:(Currency *)currency
                   transactionId:(NSString *)transactionId
{
    NSLog(@"\n\trefundReversalWithAmount: %@ %@ %@", @(amount), currency.alpha, transactionId);

    return [self.api refundVoidWithAmount:amount
                                 currency:currency.sendableCurrencyCode
                               cardholder:YES
                              transaction:transactionId];
}

- (void)tipAdjustmentWithTransactionId:(NSString *)transactionId
                             tipAmount:(NSInteger)tipAmount
        tipAdjustmentCompletionHandler:(tipAdjustmentCompletionHandler)tipAdjustmentCompletionHandler
{
    NSLog(@"\n\ttipAdjustmentWithTransactionId: %@ amount %@", transactionId, @(tipAmount));
    // setupHandpointApiConnection(self.ssk);
    // tipAdjustment(transactionId, tipAmount, tipAdjustmentCompletionHandler);
}

- (void)signatureResult:(BOOL)accepted
{
    NSLog(@"\n\tsignatureResult: %@", @(accepted));
    [self.api acceptSignature:accepted];
}

- (BOOL)enableScannerWithMultiScan:(BOOL)multiScan
                        buttonMode:(BOOL)buttonMode
                    timeoutSeconds:(NSInteger)timeoutSeconds
           sendScannerCodesGrouped:(BOOL)sendScannerCodesGrouped
                      scannedCodes:(ArrayBlock)scannedCodesBlock
                        scannerOff:(VoidBlock)scannerOffBlock
{
    NSLog(@"\n\tenableScanner");

    self.globalScannedCodesBlock = scannedCodesBlock;
    self.scannerOffBlock = scannerOffBlock;

    self.scannedCodes = [@[] mutableCopy];

    self.sendScannerCodesGrouped = sendScannerCodesGrouped;

    return [self.api enableScannerWithMultiScan:multiScan
                                     buttonMode:buttonMode
                                 timeoutSeconds:timeoutSeconds];
}

#pragma mark - Device Management

- (BOOL)connectToDeviceWithAddress:(NSString *)address
                      sharedSecret:(NSString *)sharedSecret
                           success:(VoidBlock)successBlock
                             error:(VoidBlock)errorBlock
{
    NSLog(@"\n\tconnectToDeviceWithAddress: %@", address);

    self.deviceConnectionSuccessBlock = successBlock;
    self.deviceConnectionErrorBlock = errorBlock;

    HeftRemoteDevice *remoteDevice = self.devices[address];

    if (remoteDevice)
    {
        NSString *newSSK = sharedSecret;
        self.ssk = (newSSK && ![newSSK isEqualToString:@""]) ? newSSK : self.ssk;

        BOOL isRemoteDeviceSameAsPreferred = self.preferredDevice &&
                [self.preferredDevice.address isEqualToString:remoteDevice.address];

        // If we are already connected to this device, update shared secret
        if (self.api && isRemoteDeviceSameAsPreferred)
        {
            // May the Force be with me
            self.api.sharedSecret = self.ssk;
        } else
        {
            self.preferredDevice = remoteDevice;

            [self.manager clientForDevice:remoteDevice
                             sharedSecret:self.ssk
                                 delegate:self];
        }

        [self connectionStatusChanged:ConnectionStatusConnecting];
        //TODO do dispatch and block here until we have a client

        return YES;
    } else
    {
        //Can't connect. No device available
        return NO;
    }
}

- (void)disconnect
{
    NSLog(@"\n\tdisconnect");
    [self connectionStatusChanged:ConnectionStatusDisconnected];
    self.api = nil;
    self.preferredDevice = nil;
}

- (BOOL)setLogLevel:(eLogLevel)logLevel
{
    NSLog(@"\n\tsetLogLevel %@", @(logLevel));

    return [self.api logSetLevel:logLevel];
}

- (BOOL)getDeviceLogs
{
    NSLog(@"\n\tgetDeviceLogs");
    return [self.api logGetInfo];
}

- (BOOL)getPendingTransaction
{
    NSLog(@"\n\tgetPendingTransaction");

    BOOL success = NO;

    if ([self.api isTransactionResultPending])
    {
        success = [self.api retrievePendingTransaction];
    }

    return success;
}

- (BOOL)update
{
    NSLog(@"\n\tupdate");
    return [self.api financeInit];
}

- (void)listDevices:(ArrayBlock)devicesBlock
{

    self.globalDeviceDiscoveryBlock = devicesBlock;

    NSArray *devices = [self.manager connectedCardReaders];

    NSLog(@"\n\tlistdevices current: %@", devices);

    if (devices.count)
    {
        for (HeftRemoteDevice *device in devices)
        {
            [self addDevice:device];
        }

        [self didDiscoverFinished];
    } else
    {
        [self.manager startDiscovery];
    }
}

- (NSString *)getVersion
{
    NSLog(@"\n\tgetVersion");
    return self.manager.version;
}

# pragma mark - Callbacks

- (void)didFindAccessoryDevice:(HeftRemoteDevice *)newDevice
{
    NSLog(@"\n\tdidFindAccessoryDevice: %@", newDevice.name);

    [self addDevice:newDevice];
}

- (void)didLostAccessoryDevice:(HeftRemoteDevice *)oldDevice
{
    NSLog(@"\n\tdidLostAccessoryDevice: %@", oldDevice.name);

    [self removeDevice:oldDevice];

    if (self.preferredDevice && [self.preferredDevice.address isEqualToString:oldDevice.address])
    {
        [self connectionStatusChanged:ConnectionStatusDisconnected];

        self.preferredDevice = nil;
        self.api = nil;

        if (self.automaticReconnection)
        {
            [self performSelector:@selector(connectToFirstAvailableDevice)
                       withObject:nil
                       afterDelay:WAIT_TIME_BETWEEN_RECONNECTIONS];
        }
    }
}

- (void)didDiscoverFinished
{
    NSLog(@"\n\tdidDiscoverFinished: %@", [self.manager connectedCardReaders]);

    [self fillDevicesFromconnectedCardReaders];

    NSMutableArray *devices = [@[] mutableCopy];

    for (NSString *key in [self.devices allKeys])
    {
        HeftRemoteDevice *device = self.devices[key];

        [devices addObject:device];
    }

    self.globalDeviceDiscoveryBlock(devices);
    self.globalDeviceDiscoveryBlock = nil;

    NSLog(@"\n\tListDevices result JSON: %@", @(devices.count));
}

- (void)didConnect:(id <HeftClient>)client
{
    NSLog(@"\n\tdidConnect: %@", client.mpedInfo);

    if (client)
    {
        self.api = client;

        [self connectionStatusChanged:ConnectionStatusConnected];
    }
}

- (void)connectionStatusChanged:(ConnectionStatus)status
{
    NSLog(@"\n\tconnectionStatusChanged: %@ device:%@", [self stringFromConnectionStatus:status], self.preferredDevice ? self.preferredDevice.name : @"NULL");

    if (status == ConnectionStatusConnected && self.deviceConnectionSuccessBlock)
    {
        self.deviceConnectionSuccessBlock();
        self.deviceConnectionSuccessBlock = nil;
        self.deviceConnectionErrorBlock = nil;
    } else if (status == ConnectionStatusDisconnected && self.deviceConnectionErrorBlock)
    {
        self.deviceConnectionErrorBlock();
        self.deviceConnectionSuccessBlock = nil;
        self.deviceConnectionErrorBlock = nil;
    }

    [self.delegate connectionStatusChanged:status
                                    device:self.preferredDevice];
}

- (NSString *)stringFromConnectionStatus:(ConnectionStatus)status
{
    switch (status)
    {
        case ConnectionStatusNotConfigured:
            return @"NotConfigured";
        case ConnectionStatusConnected:
            return @"Connected";
        case ConnectionStatusConnecting:
            return @"Connecting";
        case ConnectionStatusDisconnected:
            return @"Disconnected";
        case ConnectionStatusDisconnecting:
            return @"Diconnecting";
        case ConnectionStatusInitializing:
            return @"Initializing";
    }

    return @"Unknown";
}

- (void)responseStatus:(id <ResponseInfo>)info
{
    NSLog(@"\n\tresponseStatus: %@ %@", @(info.statusCode), info.status);


    [self.delegate currentTransactionStatus:info
                                     device:self.preferredDevice];
}

- (void)responseError:(id <ResponseInfo>)info
{
    NSLog(@"\n\tresponseError: %@", info.status);

    [self.delegate currentTransactionStatus:info
                                     device:self.preferredDevice];
}

- (void)responseFinanceStatus:(id <FinanceResponseInfo>)info
{
    NSLog(@"\n\tresponseFinanceStatus: %@", info.toDictionary);

    [self.delegate endOfTransaction:info
                             device:self.preferredDevice];
}

- (void)responseLogInfo:(id <LogInfo>)info
{
    //Not implemented
    NSLog(@"\n\tresponseLogInfo: %@", info.status);
}

- (void)requestSignature:(NSString *)receipt
{
    NSLog(@"\n\trequestSignature");

    [self.delegate requestSignature:receipt
                             device:self.preferredDevice];
}

- (void)responseScannerEvent:(id <ScannerEventResponseInfo>)info
{
    NSLog(@"scanner event: %@", info.scanCode);

    if (self.sendScannerCodesGrouped)
    {
        [self.scannedCodes addObject:info.scanCode];
    } else
    {
        [self sendScannerResults:@[info.scanCode]];
    }
}

- (void)responseScannerDisabled:(id <ScannerDisabledResponseInfo>)info
{
    NSLog(@"Scanner Off");

    if (self.sendScannerCodesGrouped && self.scannedCodes.count)
    {
        [self sendScannerResults:[self.scannedCodes copy]];
    }

    self.scannedCodes = [@[] mutableCopy];

    if(self.scannerOffBlock)
    {
        NSLog(@"\n\tresponseScannerDisabled");
        self.scannerOffBlock();
        self.scannerOffBlock = nil;
    }

    self.globalScannedCodesBlock = nil;
}

- (void)sendScannerResults:(NSArray *)scannedCodes
{
    NSLog(@"\n\tsendScannerResults: %@", scannedCodes);

    if (self.globalScannedCodesBlock)
    {
        self.globalScannedCodesBlock(scannedCodes);
    }
}

- (void)cancelSignature
{
    NSLog(@"\n\tcancelSignature");
}

- (void)addDevice:(HeftRemoteDevice *)device
{
    self.devices[device.address] = device;
}

- (void)removeDevice:(HeftRemoteDevice *)device
{
    if (self.devices[device.address])
    {
        [self.devices removeObjectForKey:device.address];
    }
}

- (void)fillDevicesFromconnectedCardReaders
{
    for (HeftRemoteDevice *device in [self.manager connectedCardReaders])
    {
        [self addDevice:device];
    }
}

- (void)connectToFirstAvailableDevice
{
    NSLog(@"connectToFirstAvailableDevice. Automatic reconnection: %@", self.automaticReconnection ? @"YES" : @"NO");

    if (self.preferredDevice)
    {
        [self disconnect];
    }

    [self listDevices:^(NSArray *devices) {
        // If there is only 1 device available, connect to it
        if (devices && devices.count)
        {
            NSLog(@"Connecting to the first available device");
            HeftRemoteDevice *device = devices.firstObject;
            [self connectToDeviceWithAddress:device.address
                                sharedSecret:self.ssk
                                     success:^{
                                         NSLog(@"Successfully connected.");
                                     } error:^{

                        NSLog(@"Error connecting.");
                        if (self.automaticReconnection)
                        {
                            [self performSelector:@selector(connectToFirstAvailableDevice)
                                       withObject:nil
                                       afterDelay:WAIT_TIME_BETWEEN_RECONNECTIONS];
                        }

                    }];
        } else
        {
            NSLog(@"No devices found, retrying");
            if (self.automaticReconnection)
            {
                [self performSelector:@selector(connectToFirstAvailableDevice)
                           withObject:nil
                           afterDelay:WAIT_TIME_BETWEEN_RECONNECTIONS];
            }
        }
    }];
}

- (void)setSharedSecret:(NSString *)sharedSecret
{
    if (self.api)
    {
        self.api.sharedSecret = sharedSecret;
    }
}

- (void)acceptSignature:(BOOL)accepted
{
    if (self.api)
    {
        [self.api acceptSignature:accepted];
    }
}

@end
