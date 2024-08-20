package com.handpoint.cordova;

import android.util.Log;
import java.lang.reflect.Method;

public class SysManagerWrapper {

  private static final String TAG = "SysManagerWrapper";

  public DeviceInfoBean getDeviceInfo() {
    DeviceInfoBean deviceInfoBean = new DeviceInfoBean();

    try {
      Class<?> sysManagerClass = Class.forName("com.handpoint.api.privateops.SysManager");

      deviceInfoBean.setSerialNumber(invokeStringMethod(sysManagerClass, "getPaxSerialNumber"));
      deviceInfoBean.setModel(invokeStringMethod(sysManagerClass, "getPaxModel"));

      CardReaderCapabilitiesBean cardReaderCapabilitiesBean = getCardReaderCapabilities(sysManagerClass);
      deviceInfoBean.setCardReaderCapabilities(cardReaderCapabilitiesBean);

      deviceInfoBean.setSimInfo(invokeStringArrayMethod(sysManagerClass, "getSIMInfo"));
      deviceInfoBean.setDualSIM(invokeBooleanMethod(sysManagerClass, "isDualSIM"));
      deviceInfoBean.setSdkVersion(invokeStringMethod(sysManagerClass, "getSdkVersion"));

    } catch (Exception e) {
      Log.e(TAG, "Error getting device info via reflection: " + e.getMessage(), e);
    }

    return deviceInfoBean;
  }

  public void setBrightness(int brightness) {
    try {
      Class<?> sysManagerClass = Class.forName("com.handpoint.api.privateops.SysManager");

      Method setBrightnessMethod = sysManagerClass.getDeclaredMethod("setBrightness", int.class);
      setBrightnessMethod.setAccessible(true);
      setBrightnessMethod.invoke(null, brightness);
    } catch (Exception e) {
      Log.e(TAG, "Error setting brightness via reflection: " + e.getMessage(), e);
    }
  }

  public void turnOffScreen() {
    try {
      Class<?> sysManagerClass = Class.forName("com.handpoint.api.privateops.SysManager");

      Method turnOffScreenMethod = sysManagerClass.getDeclaredMethod("turnOffScreen");
      turnOffScreenMethod.setAccessible(true);
      turnOffScreenMethod.invoke(null);
    } catch (Exception e) {
      Log.e(TAG, "Error turning off screen via reflection: " + e.getMessage(), e);
    }
  }

  private CardReaderCapabilitiesBean getCardReaderCapabilities(Class<?> sysManagerClass) {
    try {
      Method getCardReaderCapabilitiesMethod = sysManagerClass.getDeclaredMethod("getCardReaderCapabilities");
      getCardReaderCapabilitiesMethod.setAccessible(true);
      Object capabilities = getCardReaderCapabilitiesMethod.invoke(null);

      if (capabilities != null) {
        CardReaderCapabilitiesBean cardReaderCapabilitiesBean = new CardReaderCapabilitiesBean();

        Class<?> capabilitiesClass = capabilities.getClass();

        cardReaderCapabilitiesBean
            .setHasWifiModule(invokeBooleanMethod(capabilitiesClass, capabilities, "hasWifiModule"));
        cardReaderCapabilitiesBean
            .setHasPrinterModule(invokeBooleanMethod(capabilitiesClass, capabilities, "hasPrinterModule"));
        cardReaderCapabilitiesBean.setHasPhysicalKeyboardModule(
            invokeBooleanMethod(capabilitiesClass, capabilities, "hasPhysicalKeyboardModule"));
        cardReaderCapabilitiesBean.setHasBattery(invokeBooleanMethod(capabilitiesClass, capabilities, "hasBattery"));
        cardReaderCapabilitiesBean.setBatteryLevel(invokeIntMethod(capabilitiesClass, capabilities, "getBatteryLevel"));
        cardReaderCapabilitiesBean
            .setAndroidVersion(invokeStringMethod(capabilitiesClass, capabilities, "getAndroidVersion"));
        cardReaderCapabilitiesBean
            .setFirmwareVersion(invokeStringMethod(capabilitiesClass, capabilities, "getFirmwareVersion"));
        cardReaderCapabilitiesBean
            .setManufacturer(invokeStringMethod(capabilitiesClass, capabilities, "getManufacturer"));

        return cardReaderCapabilitiesBean;
      }
    } catch (Exception e) {
      Log.e(TAG, "Error invoking getCardReaderCapabilities via reflection: " + e.getMessage(), e);
    }
    return null;
  }

  private String invokeStringMethod(Class<?> clazz, Object instance, String methodName) {
    try {
      Method method = clazz.getDeclaredMethod(methodName);
      method.setAccessible(true);
      return (String) method.invoke(instance);
    } catch (Exception e) {
      Log.e(TAG, "Error invoking method " + methodName + ": " + e.getMessage(), e);
    }
    return null;
  }

  private boolean invokeBooleanMethod(Class<?> clazz, Object instance, String methodName) {
    try {
      Method method = clazz.getDeclaredMethod(methodName);
      method.setAccessible(true);
      return (boolean) method.invoke(instance);
    } catch (Exception e) {
      Log.e(TAG, "Error invoking method " + methodName + ": " + e.getMessage(), e);
    }
    return false;
  }

  private int invokeIntMethod(Class<?> clazz, Object instance, String methodName) {
    try {
      Method method = clazz.getDeclaredMethod(methodName);
      method.setAccessible(true);
      return (int) method.invoke(instance);
    } catch (Exception e) {
      Log.e(TAG, "Error invoking method " + methodName + ": " + e.getMessage(), e);
    }
    return -1;
  }

  private String[] invokeStringArrayMethod(Class<?> clazz, String methodName) {
    try {
      Method method = clazz.getDeclaredMethod(methodName);
      method.setAccessible(true);
      return (String[]) method.invoke(null);
    } catch (Exception e) {
      Log.e(TAG, "Error invoking method " + methodName + ": " + e.getMessage(), e);
    }
    return null;
  }

  private String invokeStringMethod(Class<?> clazz, String methodName) {
    return invokeStringMethod(clazz, null, methodName);
  }

  private boolean invokeBooleanMethod(Class<?> clazz, String methodName) {
    return invokeBooleanMethod(clazz, null, methodName);
  }
}
