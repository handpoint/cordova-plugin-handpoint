//
// Created by Juan Nuñez on 19/10/2017.
//

#import <Foundation/Foundation.h>

#pragma once

typedef NS_ENUM(NSInteger, ConnectionStatus)
{
    ConnectionStatusNotConfigured,
    ConnectionStatusConnected,
    ConnectionStatusConnecting,
    ConnectionStatusDisconnected,
    ConnectionStatusDisconnecting,
    ConnectionStatusInitializing
};