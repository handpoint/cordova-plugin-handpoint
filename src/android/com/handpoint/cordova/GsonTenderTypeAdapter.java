package com.handpoint.cordova;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.handpoint.api.shared.TenderType;

import java.lang.reflect.Type;

public class GsonTenderTypeAdapter implements JsonSerializer<TenderType>, JsonDeserializer<TenderType> {

  @Override
  public JsonElement serialize(TenderType tenderType, Type type, JsonSerializationContext context) {
    return new JsonPrimitive(tenderType.toString());
  }

  @Override
  public TenderType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
    return TenderType.valueOf(jsonElement.getAsString());
  }

}

