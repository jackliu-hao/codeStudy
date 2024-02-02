package io.undertow.servlet.core;

import io.undertow.servlet.ServletExtension;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServletExtensionHolder {
   private static List<ServletExtension> extensions = new CopyOnWriteArrayList();

   public static List<ServletExtension> getServletExtensions() {
      return extensions;
   }
}
