package javax.activation;

import java.io.File;

public abstract class FileTypeMap {
   private static FileTypeMap defaultMap = null;

   public abstract String getContentType(File var1);

   public abstract String getContentType(String var1);

   public static void setDefaultFileTypeMap(FileTypeMap map) {
      SecurityManager security = System.getSecurityManager();
      if (security != null) {
         try {
            security.checkSetFactory();
         } catch (SecurityException var3) {
            if (FileTypeMap.class.getClassLoader() != map.getClass().getClassLoader()) {
               throw var3;
            }
         }
      }

      defaultMap = map;
   }

   public static FileTypeMap getDefaultFileTypeMap() {
      if (defaultMap == null) {
         defaultMap = new MimetypesFileTypeMap();
      }

      return defaultMap;
   }
}
