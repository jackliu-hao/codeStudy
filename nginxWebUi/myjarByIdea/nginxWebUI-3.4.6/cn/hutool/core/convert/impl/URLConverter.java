package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import java.io.File;
import java.net.URI;
import java.net.URL;

public class URLConverter extends AbstractConverter<URL> {
   private static final long serialVersionUID = 1L;

   protected URL convertInternal(Object value) {
      try {
         if (value instanceof File) {
            return ((File)value).toURI().toURL();
         } else {
            return value instanceof URI ? ((URI)value).toURL() : new URL(this.convertToStr(value));
         }
      } catch (Exception var3) {
         return null;
      }
   }
}
