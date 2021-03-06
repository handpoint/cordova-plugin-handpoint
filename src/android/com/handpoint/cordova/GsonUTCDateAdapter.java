package com.handpoint.cordova;

import java.text.*;
import java.util.Date;
import java.util.TimeZone;
import com.google.gson.*;
import java.lang.reflect.Type;

public class GsonUTCDateAdapter implements JsonSerializer<Date>,JsonDeserializer<Date> {

  private final DateFormat dateFormat;

  public GsonUTCDateAdapter() {
    dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
  }

  @Override public synchronized JsonElement serialize(Date date,Type type,JsonSerializationContext jsonSerializationContext) {
      return new JsonPrimitive(dateFormat.format(date));
  }

  @Override public synchronized Date deserialize(JsonElement jsonElement,Type type,JsonDeserializationContext jsonDeserializationContext) {
    try {
      return dateFormat.parse(jsonElement.getAsString());
    } catch (ParseException e) {
      throw new JsonParseException(e);
    }
  }
}
