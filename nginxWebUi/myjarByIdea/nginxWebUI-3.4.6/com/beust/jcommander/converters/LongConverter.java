package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;

public class LongConverter extends BaseConverter<Long> {
   public LongConverter(String optionName) {
      super(optionName);
   }

   public Long convert(String value) {
      try {
         return Long.parseLong(value);
      } catch (NumberFormatException var3) {
         throw new ParameterException(this.getErrorString(value, "a long"));
      }
   }
}
