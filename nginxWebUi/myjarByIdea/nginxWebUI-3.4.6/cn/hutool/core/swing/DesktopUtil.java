package cn.hutool.core.swing;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.URLUtil;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class DesktopUtil {
   public static Desktop getDsktop() {
      return Desktop.getDesktop();
   }

   public static void browse(String url) {
      browse(URLUtil.toURI(url));
   }

   public static void browse(URI uri) {
      Desktop dsktop = getDsktop();

      try {
         dsktop.browse(uri);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public static void open(File file) {
      Desktop dsktop = getDsktop();

      try {
         dsktop.open(file);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public static void edit(File file) {
      Desktop dsktop = getDsktop();

      try {
         dsktop.edit(file);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public static void print(File file) {
      Desktop dsktop = getDsktop();

      try {
         dsktop.print(file);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public static void mail(String mailAddress) {
      Desktop dsktop = getDsktop();

      try {
         dsktop.mail(URLUtil.toURI(mailAddress));
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }
}
