package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;
import java.net.MalformedURLException;
import java.net.URL;

public class URLConverter extends BaseConverter<URL> {
   public URLConverter(String optionName) {
      super(optionName);
   }

   public URL convert(String value) {
      try {
         return new URL(value);
      } catch (MalformedURLException var3) {
         throw new ParameterException(this.getErrorString(value, "a RFC 2396 and RFC 2732 compliant URL"));
      }
   }
}
