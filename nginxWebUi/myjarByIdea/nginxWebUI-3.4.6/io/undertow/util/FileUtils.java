package io.undertow.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {
   private FileUtils() {
   }

   public static String readFile(Class<?> testClass, String fileName) {
      URL res = testClass.getResource(fileName);
      return readFile(res);
   }

   public static String readFile(URL url) {
      try {
         return readFile(url.openStream());
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public static String readFile(InputStream file) {
      try {
         BufferedInputStream stream = new BufferedInputStream(file);
         Throwable var2 = null;

         try {
            byte[] buff = new byte[1024];
            StringBuilder builder = new StringBuilder();

            int read;
            while((read = stream.read(buff)) != -1) {
               builder.append(new String(buff, 0, read, StandardCharsets.UTF_8));
            }

            String var6 = builder.toString();
            return var6;
         } catch (Throwable var16) {
            var2 = var16;
            throw var16;
         } finally {
            if (stream != null) {
               if (var2 != null) {
                  try {
                     stream.close();
                  } catch (Throwable var15) {
                     var2.addSuppressed(var15);
                  }
               } else {
                  stream.close();
               }
            }

         }
      } catch (IOException var18) {
         throw new RuntimeException(var18);
      }
   }

   public static void deleteRecursive(Path directory) throws IOException {
      if (Files.isDirectory(directory, new LinkOption[0])) {
         Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
               try {
                  Files.delete(file);
               } catch (IOException var4) {
               }

               return FileVisitResult.CONTINUE;
            }

            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
               try {
                  Files.delete(dir);
               } catch (IOException var4) {
               }

               return FileVisitResult.CONTINUE;
            }
         });
      }
   }
}
