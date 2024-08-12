package com.handpoint.cordova;

import org.json.JSONException;
import org.json.JSONObject;

public class CardReaderCapabilitiesBean {

  private boolean hasWifiModule;
  private boolean hasPrinterModule;
  private boolean hasPhysicalKeyboardModule;
  private boolean hasBattery;
  private int batteryLevel;
  private String androidVersion;
  private String firmwareVersion;
  private String manufacturer;

  // Getters y setters...

  public boolean isHasWifiModule() {
    return hasWifiModule;
  }

  public void setHasWifiModule(boolean hasWifiModule) {
    this.hasWifiModule = hasWifiModule;
  }

  public boolean isHasPrinterModule() {
    return hasPrinterModule;
  }

  public void setHasPrinterModule(boolean hasPrinterModule) {
    this.hasPrinterModule = hasPrinterModule;
  }

  public boolean isHasPhysicalKeyboardModule() {
    return hasPhysicalKeyboardModule;
  }

  public void setHasPhysicalKeyboardModule(boolean hasPhysicalKeyboardModule) {
    this.hasPhysicalKeyboardModule = hasPhysicalKeyboardModule;
  }

  public boolean isHasBattery() {
    return hasBattery;
  }

  public void setHasBattery(boolean hasBattery) {
    this.hasBattery = hasBattery;
  }

  public int getBatteryLevel() {
    return batteryLevel;
  }

  public void setBatteryLevel(int batteryLevel) {
    this.batteryLevel = batteryLevel;
  }

  public String getAndroidVersion() {
    return androidVersion;
  }

  public void setAndroidVersion(String androidVersion) {
    this.androidVersion = androidVersion;
  }

  public String getFirmwareVersion() {
    return firmwareVersion;
  }

  public void setFirmwareVersion(String firmwareVersion) {
    this.firmwareVersion = firmwareVersion;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  // Método para serializar el bean a JSON
  public JSONObject toJson() {
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("hasWifiModule", hasWifiModule);
      jsonObject.put("hasPrinterModule", hasPrinterModule);
      jsonObject.put("hasPhysicalKeyboardModule", hasPhysicalKeyboardModule);
      jsonObject.put("hasBattery", hasBattery);
      jsonObject.put("batteryLevel", batteryLevel);
      jsonObject.put("androidVersion", androidVersion);
      jsonObject.put("firmwareVersion", firmwareVersion);
      jsonObject.put("manufacturer", manufacturer);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jsonObject;
  }
}
