package org.noear.solon.core.util;

import java.util.Set;
import java.util.function.Predicate;
import org.noear.solon.Utils;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.ResourceScanner;

public class ScanUtil {
   static ResourceScanner scanner;

   public static void setScanner(ResourceScanner scanner) {
      if (scanner != null) {
         ScanUtil.scanner = scanner;
      }

   }

   public static Set<String> scan(String path, Predicate<String> filter) {
      return scan(JarClassLoader.global(), path, filter);
   }

   public static Set<String> scan(ClassLoader classLoader, String path, Predicate<String> filter) {
      return scanner.scan(classLoader, path, filter);
   }

   static {
      ResourceScanner ext = (ResourceScanner)Utils.newInstance("org.noear.solon.extend.impl.ResourceScannerExt");
      if (ext == null) {
         scanner = new ResourceScanner();
      } else {
         scanner = ext;
      }

   }
}
