#import <Foundation/Foundation.h>
#import "HandpointAll.h"
#import "HandpointSampleBasicEvents.h"

@class Currency;

typedef void (^ArrayBlock) (NSArray *array);
typedef void (^VoidBlock) (void);
typedef void (^ErrorBlock) (NSError *error);

@interface HandpointApiManager: NSObject

@property (readonly) NSString *version;

- (instancetype)initWithBasicEventsDelegate:(id <HandpointSampleBasicEvents>)delegate
                               sharedSecret:(NSString *)sharedSecret
                      automaticReconnection:(BOOL)automaticReconnection;

- (BOOL)saleWithAmount:(NSInteger)amount
              currency:(Currency *)currency
               options:(SaleOptions *)options;

- (BOOL)saleAndTokenizeCardWithAmount:(NSInteger)amount
                             currency:(Currency *)currency
                              options:(SaleOptions *)options;

- (BOOL)tokenizeCard;

- (BOOL)saleReversalWithAmount:(NSInteger)amount
                      currency:(Currency *)currency
                 transactionId:(NSString *)transactionId
                       options:(Options *)options;

- (BOOL)refundWithAmount:(NSInteger)amount
                currency:(Currency *)currency
                 options:(MerchantAuthOptions *)options;

- (BOOL)refundWithAmount:(NSInteger)amount
                currency:(Currency *)currency
             transaction:(NSString*)transaction
                 options:(MerchantAuthOptions *)options;

- (BOOL)refundReversalWithAmount:(NSInteger)amount
                        currency:(Currency *)currency
                   transactionId:(NSString *)transactionId
                         options:(Options *)options;

- (void)tipAdjustmentWithTransactionId:(NSString *)transactionId
                             tipAmount:(NSInteger)tipAmount
        tipAdjustmentCompletionHandler:(tipAdjustmentCompletionHandler)tipAdjustmentCompletionHandler;

- (void)signatureResult:(BOOL)accepted;

-(BOOL)enableScannerWithMultiScan:(BOOL)multiScan
                       buttonMode:(BOOL)buttonMode
                   timeoutSeconds:(NSInteger)timeoutSeconds
          sendScannerCodesGrouped:(BOOL)sendScannerCodesGrouped
                     scannedCodes:(ArrayBlock)scannedCodesBlock
                       scannerOff:(VoidBlock)scannerOffBlock;

- (void)disconnect;

- (BOOL)setLogLevel:(eLogLevel)logLevel;

- (BOOL)getDeviceLogs;

- (BOOL)getPendingTransaction;

- (BOOL)update;

- (void)listDevices:(ArrayBlock)devicesBlock;

- (void)connectToFirstAvailableDevice;

- (void)setSharedSecret:(NSString *)sharedSecret;

- (void)acceptSignature:(BOOL)accepted;
@end
