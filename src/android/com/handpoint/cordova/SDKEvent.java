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
    Gson gson = new Gson();

    try {
      JSONObject valueObject = new JSONObject(gson.toJson(value));
      this.data.put(key, valueObject);
    } catch (JSONException jse) {
      Log.e(TAG, "Error serializing SDKEvent value: " + jse.toString());
    }
  }

  public JSONObject toJSONObject() {
    try {
      JSONObject eventObject = new JSONObject();
      // Add event name
      eventObject.put("event", this.name);
      // Add data map
      eventObject.put("data", new JSONObject(this.data));
      return eventObject;
    } catch (JSONException jse) {
      Log.e(TAG, "Error serializing SDKEvent: " + jse.toString());
      return null;
    }
  }

}
