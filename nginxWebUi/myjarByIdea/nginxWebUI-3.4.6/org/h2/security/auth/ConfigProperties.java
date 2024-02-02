package org.h2.security.auth;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.h2.util.Utils;

public class ConfigProperties {
   private HashMap<String, String> properties;

   public ConfigProperties() {
      this.properties = new HashMap();
   }

   public ConfigProperties(PropertyConfig... var1) {
      this((Collection)(var1 == null ? null : Arrays.asList(var1)));
   }

   public ConfigProperties(Collection<PropertyConfig> var1) {
      this.properties = new HashMap();
      if (var1 != null) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            PropertyConfig var3 = (PropertyConfig)var2.next();
            if (this.properties.putIfAbsent(var3.getName(), var3.getValue()) != null) {
               throw new AuthConfigException("duplicate property " + var3.getName());
            }
         }
      }

   }

   public String getStringValue(String var1, String var2) {
      String var3 = (String)this.properties.get(var1);
      return var3 == null ? var2 : var3;
   }

   public String getStringValue(String var1) {
      String var2 = (String)this.properties.get(var1);
      if (var2 == null) {
         throw new AuthConfigException("missing config property " + var1);
      } else {
         return var2;
      }
   }

   public int getIntValue(String var1, int var2) {
      String var3 = (String)this.properties.get(var1);
      return var3 == null ? var2 : Integer.parseInt(var3);
   }

   public int getIntValue(String var1) {
      String var2 = (String)this.properties.get(var1);
      if (var2 == null) {
         throw new AuthConfigException("missing config property " + var1);
      } else {
         return Integer.parseInt(var2);
      }
   }

   public boolean getBooleanValue(String var1, boolean var2) {
      String var3 = (String)this.properties.get(var1);
      return var3 == null ? var2 : Utils.parseBoolean(var3, var2, true);
   }
}
