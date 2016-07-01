package com.sogou.pay.common.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeJsonSerializer extends JsonSerializer<LocalDateTime> {
  static DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  
  @Override
  public void serialize(LocalDateTime dateTime, JsonGenerator jgen, SerializerProvider provider)
    throws IOException, JsonProcessingException {
    jgen.writeString(dateTime.format(dtFmt));
  }
}
