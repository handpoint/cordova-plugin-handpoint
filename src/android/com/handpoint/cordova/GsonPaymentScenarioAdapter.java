package com.handpoint.cordova;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.handpoint.api.shared.PaymentScenario;

import java.lang.reflect.Type;

public class GsonPaymentScenarioAdapter implements JsonSerializer<PaymentScenario>, JsonDeserializer<PaymentScenario> {

  @Override public synchronized JsonElement serialize(PaymentScenario paymentScenario, Type type, JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(paymentScenario.toString());
  }

  @Override public synchronized PaymentScenario deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
    return PaymentScenario.getPaymentScenario(jsonElement.getAsString());
  }

}
