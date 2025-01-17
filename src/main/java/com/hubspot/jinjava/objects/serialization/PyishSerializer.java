package com.hubspot.jinjava.objects.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Objects;

public class PyishSerializer extends JsonSerializer<Object> {
  public static final PyishSerializer INSTANCE = new PyishSerializer();
  // Excludes things like "-0", "+5", "02"
  private static final String STRICT_NUMBER_REGEX =
    "^0|((-?[1-9][0-9]*)(\\.[0-9]+)?)|(-?0(\\.[0-9]+))$";

  private PyishSerializer() {}

  @Override
  public void serialize(
    Object object,
    JsonGenerator jsonGenerator,
    SerializerProvider serializerProvider
  )
    throws IOException {
    jsonGenerator.setPrettyPrinter(PyishPrettyPrinter.INSTANCE);
    jsonGenerator.setCharacterEscapes(PyishCharacterEscapes.INSTANCE);
    String string;
    if (object instanceof PyishSerializable) {
      jsonGenerator.writeRaw(((PyishSerializable) object).toPyishString());
    } else {
      string = Objects.toString(object, "");
      try {
        Double.parseDouble(string);
        if (string.matches(STRICT_NUMBER_REGEX)) {
          jsonGenerator.writeNumber(string);
        } else {
          jsonGenerator.writeString(string);
        }
      } catch (NumberFormatException e) {
        if ("true".equalsIgnoreCase(string) || "false".equalsIgnoreCase(string)) {
          jsonGenerator.writeBoolean(Boolean.parseBoolean(string));
        } else {
          jsonGenerator.writeString(string);
        }
      }
    }
  }
}
