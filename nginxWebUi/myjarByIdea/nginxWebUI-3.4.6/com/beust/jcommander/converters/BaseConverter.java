package com.beust.jcommander.converters;

import com.beust.jcommander.IStringConverter;

public abstract class BaseConverter<T> implements IStringConverter<T> {
   private String m_optionName;

   public BaseConverter(String optionName) {
      this.m_optionName = optionName;
   }

   public String getOptionName() {
      return this.m_optionName;
   }

   protected String getErrorString(String value, String to) {
      return "\"" + this.getOptionName() + "\": couldn't convert \"" + value + "\" to " + to;
   }
}
