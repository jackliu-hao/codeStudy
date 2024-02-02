package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import java.io.File;
import java.net.URI;
import java.net.URL;

public class URIConverter extends AbstractConverter<URI> {
   private static final long serialVersionUID = 1L;

   protected URI convertInternal(Object value) {
      try {
         if (value instanceof File) {
            return ((File)value).toURI();
         } else {
            return value instanceof URL ? ((URL)value).toURI() : new URI(this.convertToStr(value));
         }
      } catch (Exception var3) {
         return null;
      }
   }
}
