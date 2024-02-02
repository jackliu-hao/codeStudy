package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;
import java.math.BigDecimal;

public class BigDecimalConverter extends BaseConverter<BigDecimal> {
   public BigDecimalConverter(String optionName) {
      super(optionName);
   }

   public BigDecimal convert(String value) {
      try {
         return new BigDecimal(value);
      } catch (NumberFormatException var3) {
         throw new ParameterException(this.getErrorString(value, "a BigDecimal"));
      }
   }
}
