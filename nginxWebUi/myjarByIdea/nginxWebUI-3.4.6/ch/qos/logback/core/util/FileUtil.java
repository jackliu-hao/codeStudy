package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class FileUtil extends ContextAwareBase {
   static final int BUF_SIZE = 32768;

   public FileUtil(Context context) {
      this.setContext(context);
   }

   public static URL fileToURL(File file) {
      try {
         return file.toURI().toURL();
      } catch (MalformedURLException var2) {
         throw new RuntimeException("Unexpected exception on file [" + file + "]", var2);
      }
   }

   public static boolean createMissingParentDirectories(File file) {
      File parent = file.getParentFile();
      if (parent == null) {
         return true;
      } else {
         parent.mkdirs();
         return parent.exists();
      }
   }

   public String resourceAsString(ClassLoader classLoader, String resourceName) {
      URL url = classLoader.getResource(resourceName);
      if (url == null) {
         this.addError("Failed to find resource [" + resourceName + "]");
         return null;
      } else {
         InputStreamReader isr = null;

         try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.setUseCaches(false);
            isr = new InputStreamReader(urlConnection.getInputStream());
            char[] buf = new char[128];
            StringBuilder builder = new StringBuilder();
            int count = true;

            int count;
            while((count = isr.read(buf, 0, buf.length)) != -1) {
               builder.append(buf, 0, count);
            }

            String var9 = builder.toString();
            return var9;
         } catch (IOException var19) {
            this.addError("Failed to open " + resourceName, var19);
         } finally {
            if (isr != null) {
               try {
                  isr.close();
               } catch (IOException var18) {
               }
            }

         }

         return null;
      }
   }

   public void copy(String src, String destination) throws RolloverFailure {
      BufferedInputStream bis = null;
      BufferedOutputStream bos = null;

      try {
         bis = new BufferedInputStream(new FileInputStream(src));
         bos = new BufferedOutputStream(new FileOutputStream(destination));
         byte[] inbuf = new byte['耀'];

         int n;
         while((n = bis.read(inbuf)) != -1) {
            bos.write(inbuf, 0, n);
         }

         bis.close();
         bis = null;
         bos.close();
         bos = null;
      } catch (IOException var17) {
         String msg = "Failed to copy [" + src + "] to [" + destination + "]";
         this.addError(msg, var17);
         throw new RolloverFailure(msg);
      } finally {
         if (bis != null) {
            try {
               bis.close();
            } catch (IOException var16) {
            }
         }

         if (bos != null) {
            try {
               bos.close();
            } catch (IOException var15) {
            }
         }

      }
   }
}
