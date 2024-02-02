package cn.hutool.poi;

import cn.hutool.core.exceptions.DependencyException;
import cn.hutool.core.util.ClassLoaderUtil;

public class PoiChecker {
   public static final String NO_POI_ERROR_MSG = "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2";

   public static void checkPoiImport() {
      try {
         Class.forName("org.apache.poi.ss.usermodel.Workbook", false, ClassLoaderUtil.getClassLoader());
      } catch (NoClassDefFoundError | ClassNotFoundException var1) {
         throw new DependencyException(var1, "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2", new Object[0]);
      }
   }
}
