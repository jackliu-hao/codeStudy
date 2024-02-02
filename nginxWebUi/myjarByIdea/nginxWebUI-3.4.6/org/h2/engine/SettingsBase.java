package org.h2.engine;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.h2.message.DbException;
import org.h2.util.Utils;

public class SettingsBase {
   private final HashMap<String, String> settings;

   protected SettingsBase(HashMap<String, String> var1) {
      this.settings = var1;
   }

   protected boolean get(String var1, boolean var2) {
      String var3 = this.get(var1, Boolean.toString(var2));

      try {
         return Utils.parseBoolean(var3, var2, true);
      } catch (IllegalArgumentException var5) {
         throw DbException.get(22018, var5, "key:" + var1 + " value:" + var3);
      }
   }

   void set(String var1, boolean var2) {
      this.settings.put(var1, Boolean.toString(var2));
   }

   protected int get(String var1, int var2) {
      String var3 = this.get(var1, Integer.toString(var2));

      try {
         return Integer.decode(var3);
      } catch (NumberFormatException var5) {
         throw DbException.get(22018, var5, "key:" + var1 + " value:" + var3);
      }
   }

   protected String get(String var1, String var2) {
      String var3 = (String)this.settings.get(var1);
      if (var3 != null) {
         return var3;
      } else {
         StringBuilder var4 = new StringBuilder("h2.");
         boolean var5 = false;
         char[] var6 = var1.toCharArray();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            char var9 = var6[var8];
            if (var9 == '_') {
               var5 = true;
            } else {
               var4.append(var5 ? Character.toUpperCase(var9) : Character.toLowerCase(var9));
               var5 = false;
            }
         }

         String var10 = var4.toString();
         var3 = Utils.getProperty(var10, var2);
         this.settings.put(var1, var3);
         return var3;
      }
   }

   protected boolean containsKey(String var1) {
      return this.settings.containsKey(var1);
   }

   public HashMap<String, String> getSettings() {
      return this.settings;
   }

   public Map.Entry<String, String>[] getSortedSettings() {
      Map.Entry[] var1 = (Map.Entry[])this.settings.entrySet().toArray(new Map.Entry[0]);
      Arrays.sort(var1, Comparator.comparing(Map.Entry::getKey));
      return var1;
   }
}
