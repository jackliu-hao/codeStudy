package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathConverter extends AbstractConverter<Path> {
   private static final long serialVersionUID = 1L;

   protected Path convertInternal(Object value) {
      try {
         if (value instanceof URI) {
            return Paths.get((URI)value);
         } else if (value instanceof URL) {
            return Paths.get(((URL)value).toURI());
         } else {
            return value instanceof File ? ((File)value).toPath() : Paths.get(this.convertToStr(value));
         }
      } catch (Exception var3) {
         return null;
      }
   }
}
