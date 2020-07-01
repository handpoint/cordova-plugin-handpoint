//
// Created by Juan Nuñez on 28/02/2020.
// Copyright (c) 2020 Handpoint. All rights reserved.
//

#import "ConnectionStatus.h"
#import <Foundation/Foundation.h>

@class HeftRemoteDevice;
@protocol ResponseInfo;
@protocol FinanceResponseInfo;

@protocol HandpointSampleBasicEvents <NSObject>
- (void)connectionStatusChanged:(ConnectionStatus)status
                         device:(HeftRemoteDevice *)device;

- (void)currentTransactionStatus:(id <ResponseInfo>)info
                          device:(HeftRemoteDevice *)device;

- (void)endOfTransaction:(id <FinanceResponseInfo>)transactionResult
                  device:(HeftRemoteDevice *)device;

- (void)requestSignature:(NSString *)receipt
                  device:(HeftRemoteDevice *)device;
@end