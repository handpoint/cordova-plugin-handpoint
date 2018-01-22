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
        case TransactionStatusSuccess: return @"Success";
        case TransactionStatusInvalidData: return @"InvalidData";
        case TransactionStatusProcessingError: return @"ProcessingError";
        case TransactionStatusCommandNotAllowed: return @"CommandNotAllowed";
        case TransactionStatusNotInitialised: return @"NotInitialised";
        case TransactionStatusConnectTimeout: return @"ConnectTimeout";
        case TransactionStatusConnectError: return @"ConnectError";
        case TransactionStatusSendingError: return @"SendingError";
        case TransactionStatusReceivingError: return @"ReceivingError";
        case TransactionStatusNoDataAvailable: return @"NoDataAvailable";
        case TransactionStatusTransactionNotAllowed: return @"TransactionNotAllowed";
        case TransactionStatusUnsupportedCurrency: return @"UnsupportedCurrency";
        case TransactionStatusNoHostAvailable: return @"NoHostAvailable";
        case TransactionStatusCardReaderError: return @"CardReaderError";
        case TransactionStatusCardReadingFailed: return @"CardReadingFailed";
        case TransactionStatusInvalidCard: return @"InvalidCard";
        case TransactionStatusInputTimeout: return @"InputTimeout";
        case TransactionStatusUserCancelled: return @"UserCancelled";
        case TransactionStatusInvalidSignature: return @"InvalidSignature";
        case TransactionStatusWaitingForCard: return @"WaitingForCard";
        case TransactionStatusCardInserted: return @"CardInserted";
        case TransactionStatusApplicationSelection: return @"ApplicationSelection";
        case TransactionStatusApplicationConfirmation: return @"ApplicationConfirmation";
        case TransactionStatusAmountValidation: return @"AmountValidation";
        case TransactionStatusPinInput: return @"PinInput";
        case TransactionStatusManualCardInput: return @"ManualCardInput";
        case TransactionStatusWaitingForCardRemoval: return @"WaitingForCardRemoval";
        case TransactionStatusTipInput: return @"TipInput";
        case TransactionStatusSharedSecretInvalid: return @"SharedSecretInvalid";
        case TransactionStatusSharedSecretAuth: return @"SharedSecretAuth";
        case TransactionStatusWaitingSignature: return @"WaitingSignature";
        case TransactionStatusWaitingHostConnect: return @"WaitingHostConnect";
        case TransactionStatusWaitingHostSend: return @"WaitingHostSend";
        case TransactionStatusWaitingHostReceive: return @"WaitingHostReceive";
        case TransactionStatusWaitingHostDisconnect: return @"WaitingHostDisconnect";
        case TransactionStatusPinInputCompleted: return @"PinInputCompleted";
        case TransactionStatusPosCancelled: return @"PosCancelled";
        case TransactionStatusRequestInvalid: return @"RequestInvalid";
        case TransactionStatusCardCancelled: return @"CardCancelled";
        case TransactionStatusCardBlocked: return @"CardBlocked";
        case TransactionStatusRequestAuthTimeout: return @"RequestAuthTimeout";
        case TransactionStatusRequestPaymentTimeout: return @"RequestPaymentTimeout";
        case TransactionStatusResponseAuthTimeout: return @"ResponseAuthTimeout";
        case TransactionStatusResponsePaymentTimeout: return @"ResponsePaymentTimeout";
        case TransactionStatusIccCardSwiped: return @"IccCardSwiped";
        case TransactionStatusRemoveCard: return @"RemoveCard";
        case TransactionStatusScannerIsNotSupported: return @"ScannerIsNotSupported";
        case TransactionStatusScannerEvent: return @"ScannerEvent";
        case TransactionStatusBatteryTooLow: return @"BatteryTooLow";
        case TransactionStatusAccountTypeSelection: return @"AccountTypeSelection";
        case TransactionStatusBtIsNotSupported: return @"BtIsNotSupported";
        case TransactionStatusPaymentCodeSelection: return @"PaymentCodeSelection";
        case TransactionStatusPartialApproval: return @"PartialApproval";
        case TransactionStatusAmountDueValidation: return @"AmountDueValidation";
        case TransactionStatusInvalidUrl: return @"InvalidUrl";
        case TransactionStatusWaitingCustomerReceipt: return @"WaitingCustomerReceipt";
        case TransactionStatusPrintingMerchantReceipt: return @"PrintingMerchantReceipt";
        case TransactionStatusPrintingCustomerReceipt: return @"PrintingCustomerReceipt";
        case TransactionStatusInitialisationComplete: return @"InitialisationComplete";
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
