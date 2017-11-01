#import "BaseModel.h"
#import "StatusInfo.h"
#import "DeviceStatus.h"
#import "XMLTags.h"

//TODO add method calls for the XML methods
@implementation StatusInfo

- (instancetype)initWithDictionary:(NSDictionary *)dictionary
                        statusCode:(int)statusCode
{
    NSMutableDictionary *extendedDictionary = [dictionary mutableCopy];
    extendedDictionary[XMLTags.StatusCode] = @(statusCode);
    
    return [self initWithDictionary:extendedDictionary];
}
    
- (BOOL)cancelAllowed
{
    return [[self dictionary][XMLTags.StatusCode] boolValue];
}

- (TransactionStatus)status
{
    NSString *transactionStatus = [self dictionary][XMLTags.StatusCode];
    return (transactionStatus) ? (TransactionStatus) [transactionStatus intValue] : TransactionStatusUndefined;
}
    
- (NSString *)statusString
{
    switch (self.status)
    {
        case TransactionStatusUserCancelled: return @"UserCancelled";
        case TransactionStatusWaitingForCard: return @"WaitingForCard";
        case TransactionStatusCardInserted: return @"CardInserted";
        case TransactionStatusApplicationSelection: return @"ApplicationSelection";
        case TransactionStatusApplicationConfirmation: return @"ApplicationConfirmation";
        case TransactionStatusAmountValidation: return @"AmountValidation";
        case TransactionStatusPinInput: return @"PinInput";
        case TransactionStatusManualCardInput: return @"ManualCardInput";
        case TransactionStatusWaitingForCardRemoval: return @"WaitingForCardRemoval";
        case TransactionStatusTipInput: return @"TipInput";
        case TransactionStatusAuthenticatingPos: return @"AuthenticatingPos";
        case TransactionStatusWaitingForSignature: return @"WaitingForSignature";
        case TransactionStatusConnectingToHost: return @"ConnectingToHost";
        case TransactionStatusSendingToHost: return @"SendingToHost";
        case TransactionStatusReceivingFromHost: return @"ReceivingFromHost";
        case TransactionStatusDisconnectingFromHost: return @"DisconnectingFromHost";
        case TransactionStatusPinInputComplete: return @"PinInputComplete";
        case TransactionStatusUndefined:
        default:
        return @"Undefined";
    }
}

- (NSString *)message
{
    return [self dictionary][XMLTags.StatusMessage] ?: @"";
}

- (DeviceStatus *)deviceStatus
{
    return [[DeviceStatus alloc] initWithDictionary:[self dictionary]];
}

@end
