package org.noear.solon.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Properties;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.util.PrintUtil;

public class PropsLoader {
   private static PropsLoader global;

   public static PropsLoader global() {
      return global;
   }

   public static void globalSet(PropsLoader instance) {
      if (instance != null) {
         global = instance;
      }

   }

   public boolean isSupport(String suffix) {
      return suffix == null ? false : suffix.endsWith(".properties");
   }

   public Properties load(URL url) throws IOException {
      if (url == null) {
         return null;
      } else {
         String fileName = url.toString();
         if (fileName.endsWith(".properties")) {
            PrintUtil.info(url);
            Properties tmp = new Properties();
            tmp.load(new InputStreamReader(url.openStream(), Solon.encoding()));
            return tmp;
         } else {
            throw new IllegalStateException("This profile is not supported: " + fileName);
         }
      }
   }

   public Properties build(String txt) throws IOException {
      int idx1 = txt.indexOf("=");
      int idx2 = txt.indexOf(":");
      if (idx1 <= 0 || idx1 >= idx2 && idx2 >= 0) {
         return new Properties();
      } else {
         Properties tmp = new Properties();
         tmp.load(new StringReader(txt));
         return tmp;
      }
   }

   static {
      PropsLoader tmp = (PropsLoader)Utils.newInstance("org.noear.solon.extend.impl.PropsLoaderExt");
      if (tmp == null) {
         global = new PropsLoader();
      } else {
         global = tmp;
      }

   }
}
