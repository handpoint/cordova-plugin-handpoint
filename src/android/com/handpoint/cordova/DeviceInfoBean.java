package com.handpoint.cordova;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceInfoBean {

  private String serialNumber;
  private String model;
  private CardReaderCapabilitiesBean cardReaderCapabilities;
  private String[] simInfo;
  private boolean isDualSIM;
  private String sdkVersion;

  // Getters y setters...

  public String getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public CardReaderCapabilitiesBean getCardReaderCapabilities() {
    return cardReaderCapabilities;
  }

  public void setCardReaderCapabilities(CardReaderCapabilitiesBean cardReaderCapabilities) {
    this.cardReaderCapabilities = cardReaderCapabilities;
  }

  public String[] getSimInfo() {
    return simInfo;
  }

  public void setSimInfo(String[] simInfo) {
    this.simInfo = simInfo;
  }

  public boolean isDualSIM() {
    return isDualSIM;
  }

  public void setDualSIM(boolean dualSIM) {
    isDualSIM = dualSIM;
  }

  public String getSdkVersion() {
    return sdkVersion;
  }

  public void setSdkVersion(String sdkVersion) {
    this.sdkVersion = sdkVersion;
  }

  // Método para serializar el bean a JSON
  public JSONObject toJson() {
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("serialNumber", serialNumber);
      jsonObject.put("model", model);
      jsonObject.put("cardReaderCapabilities",
          cardReaderCapabilities != null ? cardReaderCapabilities.toJson() : JSONObject.NULL);
      jsonObject.put("simInfo", simInfo != null ? simInfo : JSONObject.NULL);
      jsonObject.put("isDualSIM", isDualSIM);
      jsonObject.put("sdkVersion", sdkVersion);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jsonObject;
  }
}
