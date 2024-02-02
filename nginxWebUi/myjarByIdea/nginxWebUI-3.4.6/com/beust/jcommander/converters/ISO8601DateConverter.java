package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ISO8601DateConverter extends BaseConverter<Date> {
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

   public ISO8601DateConverter(String optionName) {
      super(optionName);
   }

   public Date convert(String value) {
      try {
         return DATE_FORMAT.parse(value);
      } catch (ParseException var3) {
         throw new ParameterException(this.getErrorString(value, String.format("an ISO-8601 formatted date (%s)", DATE_FORMAT.toPattern())));
      }
   }
}
