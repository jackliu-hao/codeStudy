package cn.hutool.core.io;

import cn.hutool.core.io.resource.ResourceUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ManifestUtil {
   private static final String[] MANIFEST_NAMES = new String[]{"Manifest.mf", "manifest.mf", "MANIFEST.MF"};

   public static Manifest getManifest(Class<?> cls) throws IORuntimeException {
      URL url = ResourceUtil.getResource((String)null, cls);

      URLConnection connection;
      try {
         connection = url.openConnection();
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }

      if (connection instanceof JarURLConnection) {
         JarURLConnection conn = (JarURLConnection)connection;
         return getManifest(conn);
      } else {
         return null;
      }
   }

   public static Manifest getManifest(File classpathItem) throws IORuntimeException {
      Manifest manifest = null;
      if (classpathItem.isFile()) {
         try {
            JarFile jarFile = new JarFile(classpathItem);
            Throwable var3 = null;

            try {
               manifest = getManifest(jarFile);
            } catch (Throwable var33) {
               var3 = var33;
               throw var33;
            } finally {
               if (jarFile != null) {
                  if (var3 != null) {
                     try {
                        jarFile.close();
                     } catch (Throwable var31) {
                        var3.addSuppressed(var31);
                     }
                  } else {
                     jarFile.close();
                  }
               }

            }
         } catch (IOException var37) {
            throw new IORuntimeException(var37);
         }
      } else {
         File metaDir = new File(classpathItem, "META-INF");
         File manifestFile = null;
         if (metaDir.isDirectory()) {
            String[] var4 = MANIFEST_NAMES;
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               String name = var4[var6];
               File mFile = new File(metaDir, name);
               if (mFile.isFile()) {
                  manifestFile = mFile;
                  break;
               }
            }
         }

         if (null != manifestFile) {
            try {
               FileInputStream fis = new FileInputStream(manifestFile);
               Throwable var41 = null;

               try {
                  manifest = new Manifest(fis);
               } catch (Throwable var32) {
                  var41 = var32;
                  throw var32;
               } finally {
                  if (fis != null) {
                     if (var41 != null) {
                        try {
                           fis.close();
                        } catch (Throwable var30) {
                           var41.addSuppressed(var30);
                        }
                     } else {
                        fis.close();
                     }
                  }

               }
            } catch (IOException var35) {
               throw new IORuntimeException(var35);
            }
         }
      }

      return manifest;
   }

   public static Manifest getManifest(JarURLConnection connection) throws IORuntimeException {
      JarFile jarFile;
      try {
         jarFile = connection.getJarFile();
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }

      return getManifest(jarFile);
   }

   public static Manifest getManifest(JarFile jarFile) throws IORuntimeException {
      try {
         return jarFile.getManifest();
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }
}
