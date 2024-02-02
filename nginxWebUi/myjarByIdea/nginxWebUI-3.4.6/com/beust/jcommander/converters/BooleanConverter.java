package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;

public class BooleanConverter extends BaseConverter<Boolean> {
   public BooleanConverter(String optionName) {
      super(optionName);
   }

   public Boolean convert(String value) {
      if (!"false".equalsIgnoreCase(value) && !"true".equalsIgnoreCase(value)) {
         throw new ParameterException(this.getErrorString(value, "a boolean"));
      } else {
         return Boolean.parseBoolean(value);
      }
   }
}
