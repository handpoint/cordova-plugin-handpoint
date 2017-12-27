#import "BaseModel.h"
#import "DeviceStatus.h"
#import "FinanceResponseInfo.h"

@interface TransactionResult : BaseModel

@property (readonly, nonatomic) NSString *statusMessage;
@property (readonly, nonatomic) NSString *type;
@property (readonly, nonatomic) NSString *finStatus;
@property (readonly, nonatomic) NSString *requestedAmount;
@property (readonly, nonatomic) NSString *gratuityAmount;
@property (readonly, nonatomic) NSString *gratuityPercentage;
@property (readonly, nonatomic) NSString *totalAmount;
@property (readonly, nonatomic) NSString *currency;
@property (readonly, nonatomic) NSString *transactionID;
@property (readonly, nonatomic) NSString *eFTTransactionID;
@property (readonly, nonatomic) NSString *originalEFTTransactionID;
@property (readonly, nonatomic) NSString *eFTTimestamp;
@property (readonly, nonatomic) NSString *authorisationCode;
@property (readonly, nonatomic) NSString *verificationMethod;
@property (readonly, nonatomic) NSString *cardEntryType;
@property (readonly, nonatomic) NSString *cardSchemeName;
@property (readonly, nonatomic) NSString *errorMessage;
@property (readonly, nonatomic) NSString *customerReference;
@property (readonly, nonatomic) NSString *budgetNumber;
@property (readonly, nonatomic) BOOL recoveredTransaction;
@property (readonly, nonatomic) NSString *cardTypeId;
@property (readonly, nonatomic) NSString *merchantReceipt;
@property (readonly, nonatomic) NSString *customerReceipt;
@property (readonly, nonatomic) DeviceStatus *deviceStatus;
@property (readonly, nonatomic) NSString *chipTransactionReport;
@property (readonly, nonatomic) NSString *dueAmount;
@property (readonly, nonatomic) NSString *balance;

- (instancetype)initWithDictionary:(NSDictionary *)dictionary financeResponseInfo:(id <FinanceResponseInfo>)info;

- (NSDictionary *)toDictionary;
    
@end
