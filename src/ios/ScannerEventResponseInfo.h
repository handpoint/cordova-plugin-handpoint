//
// Created by Juan Nuñez on 18/12/2017.
// Copyright (c) 2017 zdv. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ResponseInfo.h"

@protocol ScannerEventResponseInfo <ResponseInfo>
//The code that was scanned.
@property (nonatomic) NSString *scanCode;
@end