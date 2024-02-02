package org.apache.commons.compress.compressors.pack200;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import org.apache.commons.compress.java.util.jar.Pack200;

public class Pack200Utils {
   private Pack200Utils() {
   }

   public static void normalize(File jar) throws IOException {
      normalize(jar, jar, (Map)null);
   }

   public static void normalize(File jar, Map<String, String> props) throws IOException {
      normalize(jar, jar, props);
   }

   public static void normalize(File from, File to) throws IOException {
      normalize(from, to, (Map)null);
   }

   public static void normalize(File from, File to, Map<String, String> props) throws IOException {
      if (props == null) {
         props = new HashMap();
      }

      ((Map)props).put("pack.segment.limit", "-1");
      File tempFile = File.createTempFile("commons-compress", "pack200normalize");

      try {
         OutputStream fos = Files.newOutputStream(tempFile.toPath());
         Throwable var5 = null;

         try {
            JarFile jarFile = new JarFile(from);
            Throwable var7 = null;

            try {
               Pack200.Packer packer = Pack200.newPacker();
               packer.properties().putAll((Map)props);
               packer.pack(jarFile, fos);
            } catch (Throwable var69) {
               var7 = var69;
               throw var69;
            } finally {
               if (jarFile != null) {
                  if (var7 != null) {
                     try {
                        jarFile.close();
                     } catch (Throwable var67) {
                        var7.addSuppressed(var67);
                     }
                  } else {
                     jarFile.close();
                  }
               }

            }
         } catch (Throwable var72) {
            var5 = var72;
            throw var72;
         } finally {
            if (fos != null) {
               if (var5 != null) {
                  try {
                     fos.close();
                  } catch (Throwable var66) {
                     var5.addSuppressed(var66);
                  }
               } else {
                  fos.close();
               }
            }

         }

         Pack200.Unpacker unpacker = Pack200.newUnpacker();
         JarOutputStream jos = new JarOutputStream(Files.newOutputStream(to.toPath()));
         Throwable var77 = null;

         try {
            unpacker.unpack(tempFile, jos);
         } catch (Throwable var68) {
            var77 = var68;
            throw var68;
         } finally {
            if (jos != null) {
               if (var77 != null) {
                  try {
                     jos.close();
                  } catch (Throwable var65) {
                     var77.addSuppressed(var65);
                  }
               } else {
                  jos.close();
               }
            }

         }
      } finally {
         if (!tempFile.delete()) {
            tempFile.deleteOnExit();
         }

      }

   }
}
