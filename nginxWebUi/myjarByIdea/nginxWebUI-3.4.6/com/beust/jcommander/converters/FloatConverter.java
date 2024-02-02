package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;

public class FloatConverter extends BaseConverter<Float> {
   public FloatConverter(String optionName) {
      super(optionName);
   }

   public Float convert(String value) {
      try {
         return Float.parseFloat(value);
      } catch (NumberFormatException var3) {
         throw new ParameterException(this.getErrorString(value, "a float"));
      }
   }
}
