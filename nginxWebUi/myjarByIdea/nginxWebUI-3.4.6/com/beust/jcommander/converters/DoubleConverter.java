package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;

public class DoubleConverter extends BaseConverter<Double> {
   public DoubleConverter(String optionName) {
      super(optionName);
   }

   public Double convert(String value) {
      try {
         return Double.parseDouble(value);
      } catch (NumberFormatException var3) {
         throw new ParameterException(this.getErrorString(value, "a double"));
      }
   }
}
