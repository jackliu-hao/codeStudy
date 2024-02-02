package cn.hutool.core.compiler;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ZipUtil;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;
import javax.tools.JavaFileObject;

public class JavaFileObjectUtil {
   public static List<JavaFileObject> getJavaFileObjects(File file) {
      List<JavaFileObject> result = new ArrayList();
      String fileName = file.getName();
      if (isJavaFile(fileName)) {
         result.add(new JavaSourceFileObject(file.toURI()));
      } else if (isJarOrZipFile(fileName)) {
         result.addAll(getJavaFileObjectByZipOrJarFile(file));
      }

      return result;
   }

   public static boolean isJarOrZipFile(String fileName) {
      return FileNameUtil.isType(fileName, "jar", "zip");
   }

   public static boolean isJavaFile(String fileName) {
      return FileNameUtil.isType(fileName, "java");
   }

   private static List<JavaFileObject> getJavaFileObjectByZipOrJarFile(File file) {
      List<JavaFileObject> collection = new ArrayList();
      ZipFile zipFile = ZipUtil.toZipFile(file, (Charset)null);
      ZipUtil.read(zipFile, (zipEntry) -> {
         String name = zipEntry.getName();
         if (isJavaFile(name)) {
            collection.add(new JavaSourceFileObject(name, ZipUtil.getStream(zipFile, zipEntry)));
         }

      });
      return collection;
   }
}
