package com.beust.jcommander.converters;

import com.beust.jcommander.ParameterException;
import java.net.URI;
import java.net.URISyntaxException;

public class URIConverter extends BaseConverter<URI> {
   public URIConverter(String optionName) {
      super(optionName);
   }

   public URI convert(String value) {
      try {
         return new URI(value);
      } catch (URISyntaxException var3) {
         throw new ParameterException(this.getErrorString(value, "a RFC 2396 and RFC 2732 compliant URI"));
      }
   }
}
