package com.github.jaiimageio.impl.common;

import java.io.InputStream;
import java.util.PropertyResourceBundle;

public class I18NImpl {
   protected static final String getString(String className, String key) {
      PropertyResourceBundle bundle = null;

      try {
         InputStream stream = Class.forName(className).getResourceAsStream("properties");
         bundle = new PropertyResourceBundle(stream);
      } catch (Throwable var4) {
         throw new RuntimeException(var4);
      }

      return (String)bundle.handleGetObject(key);
   }
}
