package com.handpoint.cordova;

public interface PermissionResultObserver {
  void onPermissionResult(int requestCode, boolean isGranted);
}
