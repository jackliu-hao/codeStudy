package org.noear.solon.extend.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Properties;
import org.noear.solon.Solon;
import org.noear.solon.config.yaml.PropertiesJson;
import org.noear.solon.config.yaml.PropertiesYaml;
import org.noear.solon.core.PropsLoader;
import org.noear.solon.core.util.PrintUtil;

public class PropsLoaderExt extends PropsLoader {
   public boolean isSupport(String suffix) {
      if (suffix == null) {
         return false;
      } else {
         return suffix.endsWith(".properties") || suffix.endsWith(".yml");
      }
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
         } else if (fileName.endsWith(".yml")) {
            PrintUtil.info(url);
            PropertiesYaml tmp = new PropertiesYaml();
            tmp.loadYml((Reader)(new InputStreamReader(url.openStream(), Solon.encoding())));
            return tmp;
         } else {
            throw new RuntimeException("This profile is not supported: " + fileName);
         }
      }
   }

   public Properties build(String text) throws IOException {
      text = text.trim();
      int idx1 = text.indexOf("=");
      int idx2 = text.indexOf(":");
      PropertiesJson tmp;
      if (text.startsWith("{") && text.endsWith("}")) {
         tmp = new PropertiesJson();
         tmp.loadJson(text);
         return tmp;
      } else if (text.startsWith("[") && text.endsWith("]")) {
         tmp = new PropertiesJson();
         tmp.loadJson(text);
         return tmp;
      } else if (idx1 <= 0 || idx1 >= idx2 && idx2 >= 0) {
         if (idx2 <= 0 || idx2 >= idx1 && idx1 >= 0) {
            return new Properties();
         } else {
            PropertiesYaml tmp = new PropertiesYaml();
            tmp.loadYml((Reader)(new StringReader(text)));
            return tmp;
         }
      } else {
         Properties tmp = new Properties();
         tmp.load(new StringReader(text));
         return tmp;
      }
   }
}
