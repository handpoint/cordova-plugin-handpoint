#!/bin/bash
rm -f ./libs/*
cp ~/repos/hapi-android/sdk/build/outputs/aar/sdk-release.aar ./libs/sdk.aar
#cp ~/repos/hapi-android/sdk/build/outputs/aar/sdk-release.aar ./src/android/com/handpoint/cordova/
#cp ~/repos/hapi-android/sdk/build/outputs/aar/sdk-debug.aar ./libs/
#cp ~/repos/hapi-android/paymentsdk/build/outputs/aar/paymentsdk-debug.aar ./libs/
cp ~/repos/hapi-android/paymentsdk/build/outputs/aar/paymentsdk-release.aar ./libs/paymentsdk.aar
#cp ~/repos/hapi-android/paymentsdk/build/outputs/aar/paymentsdk-release.aar ./src/android/com/handpoint/cordova/
cp ~/repos/hapi-android/sharedObjects/build/outputs/aar/sharedObjects-release.aar ./libs/sharedObjects.aar
#cp ~/repos/hapi-android/sharedObjects/build/outputs/aar/sharedObjects-debug.aar ./libs/