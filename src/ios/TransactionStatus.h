#pragma once

typedef NS_ENUM(NSInteger, TransactionStatus)
{
    TransactionStatusUndefined = 0,
    TransactionStatusUserCancelled = 0x0012,
    TransactionStatusWaitingForCard = 0x0014,
    TransactionStatusCardInserted = 0x0015,
    TransactionStatusApplicationSelection = 0x0016,
    TransactionStatusApplicationConfirmation = 0x0017,
    TransactionStatusAmountValidation = 0x0018,
    TransactionStatusPinInput = 0x0019,
    TransactionStatusManualCardInput = 0x001A,
    TransactionStatusWaitingForCardRemoval = 0x001B,
    TransactionStatusTipInput = 0x001C,
    TransactionStatusAuthenticatingPos = 0x001E,
    TransactionStatusWaitingForSignature = 0x001F,
    TransactionStatusConnectingToHost = 0x0020,
    TransactionStatusSendingToHost = 0x0021,
    TransactionStatusReceivingFromHost = 0x0022,
    TransactionStatusDisconnectingFromHost = 0x0023,
    TransactionStatusPinInputComplete = 0x0024
};
