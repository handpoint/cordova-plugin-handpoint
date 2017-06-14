package com.handpoint.cordova;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import com.google.gson.*;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SDKEvent {

  private static final String TAG = SDKEvent.class.getSimpleName();

  /** Event name */
  private String name;
  /** Event data */
  private Map<String, Object> data;

  public SDKEvent(String name) {
    this.name = name;
    this.data = new HashMap<String, Object>();
  }

  /**
   * Add event data. Each entry in event data is a key/value pair
   */
  public void put(String key, Object value) {
    this.data.put(key, value);
  }

  @Override
  public String toString() {
    try {
      Gson gson = new Gson();
      JSONObject eventObject = new JSONObject();
      // Add event name
      eventObject.put("event", this.name);
      // Add data map
      if (this.data.size() > 0) {
        JSONObject eventDataObject = new JSONObject();
        final Set<Map.Entry<String, Object>> entries = this.data.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
          eventDataObject.put(entry.getKey(), gson.toJson(entry.getValue()));
        }
        eventObject.put("data", eventDataObject);
      }
      return eventObject.toString();
    } catch (JSONException jse) {
      Log.e(TAG, "Error serializing SDKEvent: " + jse.toString());
      return "";
    }

  }

}
