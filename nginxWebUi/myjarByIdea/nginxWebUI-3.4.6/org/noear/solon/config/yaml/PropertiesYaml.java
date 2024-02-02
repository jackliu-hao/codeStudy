package org.noear.solon.config.yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.yaml.snakeyaml.Yaml;

public class PropertiesYaml extends Properties {
   public synchronized void loadYml(InputStream inputStream) {
      Yaml yaml = new Yaml();
      Object tmp = yaml.load(inputStream);
      String prefix = "";
      this.load0(prefix, tmp);
   }

   public synchronized void loadYml(Reader reader) throws IOException {
      Yaml yaml = new Yaml();
      Object tmp = yaml.load(reader);
      String prefix = "";
      this.load0(prefix, tmp);
   }

   private void load0(String prefix, Object tmp) {
      if (tmp instanceof Map) {
         ((Map)tmp).forEach((k, vx) -> {
            String prefix2 = prefix + "." + k;
            this.load0(prefix2, vx);
         });
      } else if (!(tmp instanceof List)) {
         if (tmp == null) {
            this.put0(prefix, "");
         } else {
            this.put0(prefix, String.valueOf(tmp));
         }

      } else {
         int index = 0;

         for(Iterator var4 = ((List)tmp).iterator(); var4.hasNext(); ++index) {
            Object v = var4.next();
            String prefix2 = prefix + "[" + index + "]";
            this.load0(prefix2, v);
         }

      }
   }

   private void put0(String key, Object val) {
      if (key.startsWith(".")) {
         this.put(key.substring(1), val);
      } else {
         this.put(key, val);
      }

   }
}
