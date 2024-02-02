package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;

public class IntegerConverter extends BaseConverter<Integer> {
   public IntegerConverter(String optionName) {
      super(optionName);
   }

   public Integer convert(String value) {
      try {
         return Integer.parseInt(value);
      } catch (NumberFormatException var3) {
         throw new ParameterException(this.getErrorString(value, "an integer"));
      }
   }
}
