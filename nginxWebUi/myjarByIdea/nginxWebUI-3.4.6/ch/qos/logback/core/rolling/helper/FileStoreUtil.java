package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.rolling.RolloverFailure;
import java.io.File;
import java.lang.reflect.Method;

public class FileStoreUtil {
   static final String PATH_CLASS_STR = "java.nio.file.Path";
   static final String FILES_CLASS_STR = "java.nio.file.Files";

   public static boolean areOnSameFileStore(File a, File b) throws RolloverFailure {
      if (!a.exists()) {
         throw new IllegalArgumentException("File [" + a + "] does not exist.");
      } else if (!b.exists()) {
         throw new IllegalArgumentException("File [" + b + "] does not exist.");
      } else {
         try {
            Class<?> pathClass = Class.forName("java.nio.file.Path");
            Class<?> filesClass = Class.forName("java.nio.file.Files");
            Method toPath = File.class.getMethod("toPath");
            Method getFileStoreMethod = filesClass.getMethod("getFileStore", pathClass);
            Object pathA = toPath.invoke(a);
            Object pathB = toPath.invoke(b);
            Object fileStoreA = getFileStoreMethod.invoke((Object)null, pathA);
            Object fileStoreB = getFileStoreMethod.invoke((Object)null, pathB);
            return fileStoreA.equals(fileStoreB);
         } catch (Exception var10) {
            throw new RolloverFailure("Failed to check file store equality for [" + a + "] and [" + b + "]", var10);
         }
      }
   }
}
