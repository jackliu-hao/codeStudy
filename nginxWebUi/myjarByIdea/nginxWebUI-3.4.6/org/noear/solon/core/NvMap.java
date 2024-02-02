package org.noear.solon.core;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.noear.solon.Utils;
import org.noear.solon.ext.LinkedCaseInsensitiveMap;

public class NvMap extends LinkedCaseInsensitiveMap<String> {
   public NvMap() {
   }

   public NvMap(Map map) {
      if (map != null) {
         map.forEach((k, v) -> {
            if (k != null && v != null) {
               this.put(k.toString(), v.toString());
            }

         });
      }

   }

   public NvMap set(String key, String val) {
      this.put(key, val);
      return this;
   }

   public static NvMap from(String[] args) {
      return from(Arrays.asList(args));
   }

   public static NvMap from(List<String> args) {
      NvMap d = new NvMap();
      if (args != null) {
         Iterator var2 = args.iterator();

         while(var2.hasNext()) {
            String arg = (String)var2.next();
            int index = arg.indexOf(61);
            if (index > 0) {
               String name = arg.substring(0, index);
               String value = arg.substring(index + 1);
               d.put(name.replaceAll("^-*", ""), value);
            } else {
               d.put(arg.replaceAll("^-*", ""), "");
            }
         }
      }

      return d;
   }

   public int getInt(String key) {
      return this.getInt(key, 0);
   }

   public int getInt(String key, int def) {
      String temp = (String)this.get(key);
      return Utils.isEmpty(temp) ? def : Integer.parseInt(temp);
   }

   public long getLong(String key) {
      return this.getLong(key, 0L);
   }

   public long getLong(String key, long def) {
      String temp = (String)this.get(key);
      return Utils.isEmpty(temp) ? def : Long.parseLong(temp);
   }

   public double getDouble(String key) {
      return this.getDouble(key, 0.0);
   }

   public double getDouble(String key, double def) {
      String temp = (String)this.get(key);
      return Utils.isEmpty(temp) ? def : Double.parseDouble(temp);
   }

   public boolean getBool(String key, boolean def) {
      return this.containsKey(key) ? Boolean.parseBoolean((String)this.get(key)) : def;
   }
}
