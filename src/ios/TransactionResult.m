
#import "TransactionResult.h"
#import "Currency.h"
#import "XMLTags.h"
#import "DeviceStatus.h"

@interface TransactionResult ()

@property (nonatomic) NSString *customerReceipt;
@property (nonatomic) NSString *merchantReceipt;

@end

@implementation TransactionResult

- (instancetype)initWithDictionary:(NSDictionary *)dictionary financeResponseInfo:(id <FinanceResponseInfo>)info
{
    self = [super initWithDictionary:dictionary];
    if (self)
    {
        self.customerReceipt = info.customerReceipt ?: @"";
        self.merchantReceipt = info.merchantReceipt ?: @"";
    }
    return self;
}

- (NSString *)statusMessage
{
    return self.dictionary[XMLTags.StatusMessage] ?: @"Undefined";
}

- (NSString *)type
{
    return self.dictionary[XMLTags.TransactionType] ?: @"Undefined";
}

- (NSString *)finStatus
{
    return self.dictionary[XMLTags.FinancialStatus] ?: @"Undefined";
}

- (NSString *)requestedAmount
{
    return self.dictionary[XMLTags.RequestedAmount] ?: @"0";
}

- (NSString *)gratuityAmount
{
    return self.dictionary[XMLTags.GratuityAmount] ?: @"0";
}

- (NSString *)gratuityPercentage
{
    return self.dictionary[XMLTags.GratuityPercentage] ?: @"0%";
}

- (NSString *)totalAmount
{
    return self.dictionary[XMLTags.TotalAmount] ?: @"0";
}

- (NSString *)currency
{
    return self.dictionary[XMLTags.Currency] ?: @"Unknown";
}

- (NSString *)transactionID
{
    return self.dictionary[XMLTags.TransactionID] ?: @"";
}

- (NSString *)eFTTransactionID
{
    return self.dictionary[XMLTags.EFTTransactionID] ?: @"";
}

- (NSString *)originalEFTTransactionID
{
    return self.dictionary[XMLTags.OriginalEFTTransactionID] ?: @"";
}

- (NSString *)eFTTimestamp
{
    return self.dictionary[XMLTags.EFTTimestamp] ?: @"";
}

- (NSString *)authorisationCode
{
    return self.dictionary[XMLTags.AuthorisationCode] ?: @"";
}

- (NSString *)verificationMethod
{
    return self.dictionary[XMLTags.CVM] ?: @"";
}

- (NSString *)cardEntryType
{
   return self.dictionary[XMLTags.CardEntryType] ?: @"";
}

- (NSString *)cardSchemeName
{
    return self.dictionary[XMLTags.CardSchemeName] ?: @"";
}

- (NSString *)errorMessage
{
    return self.dictionary[XMLTags.ErrorMessage] ?: @"";
}

- (NSString *)customerReference
{
    return self.dictionary[XMLTags.CustomerReference] ?: @"";
}

- (NSString *)budgetNumber
{
    return self.dictionary[XMLTags.BudgetNumber] ?: @"";
}

- (BOOL)recoveredTransaction
{
    NSString *recoveredTransaction = self.dictionary[XMLTags.RecoveredTransaction];
    return (recoveredTransaction) ? [recoveredTransaction boolValue] : NO;
}

- (NSString *)cardTypeId
{
    return self.dictionary[XMLTags.CardTypeId] ?: @"";
}

- (DeviceStatus *)deviceStatus
{
    return [[DeviceStatus alloc] initWithDictionary:self.dictionary];
}

- (NSString *)chipTransactionReport
{
    return self.dictionary[XMLTags.ChipTransactionReport] ?: @"";
}

- (NSString *)dueAmount
{
    return self.dictionary[XMLTags.DueAmount] ?: @"0";
}

- (NSString *)balance
{
    return self.dictionary[XMLTags.BalanceAmount] ?: @"0";
}

- (NSDictionary *)toDictionary
{
    return @{
             @"statusMessage": self.statusMessage,
             @"type": self.type,
             @"finStatus": self.finStatus,
             @"requestedAmount": self.requestedAmount,
             @"gratuityAmount": self.gratuityAmount,
             @"gratuityPercentage": self.gratuityPercentage,
             @"totalAmount": self.totalAmount,
             @"currency": self.currency,
             @"transactionID": self.transactionID,
             @"eFTTransactionID": self.eFTTransactionID,
             @"originalEFTTransactionID": self.originalEFTTransactionID,
             @"eFTTimestamp": self.eFTTimestamp,
             @"authorisationCode": self.authorisationCode,
             @"verificationMethod": self.verificationMethod,
             @"cardEntryType": self.cardEntryType,
             @"cardSchemeName": self.cardSchemeName,
             @"errorMessage": self.errorMessage,
             @"customerReference": self.customerReference,
             @"budgetNumber": self.budgetNumber,
             @"recoveredTransaction": @(self.recoveredTransaction),
             @"cardTypeId": self.cardTypeId,
             @"merchantReceipt": self.merchantReceipt,
             @"customerReceipt": self.customerReceipt,
             @"deviceStatus": self.deviceStatus.toDictionary,
             @"chipTransactionReport": self.chipTransactionReport,
             @"dueAmount": self.dueAmount,
             @"balance": self.balance
             };
}
@end
