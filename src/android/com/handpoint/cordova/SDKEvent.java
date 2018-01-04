package com.handpoint.cordova;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.AbstractCollection;
import com.google.gson.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

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
    ArrayList list = new ArrayList();
    Iterator iterator = null;
    JSONObject valueObject = null;
    JSONArray valueObjectList = null;

    try {
      // Is value Object iterable? Then iterate 
      if (value instanceof AbstractCollection) {
        iterator = ((AbstractCollection) value).iterator();
        while (iterator.hasNext()) {
          Object element = iterator.next();
          valueObject = new JSONObject(gson.toJson(element));
          list.add(valueObject);
        }
        valueObjectList = new JSONArray(list);
        data.put(key, valueObjectList);
      } else if (value instanceof Enum) {
        this.data.put(key, value.toString());
      } else if (value instanceof String) {
        this.data.put(key, value);
      } else {
        valueObject = new JSONObject(gson.toJson(value));
        this.data.put(key, valueObject);
      }
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
