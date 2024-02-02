package org.h2.value;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Locale;
import org.h2.message.DbException;
import org.h2.util.JdbcUtils;
import org.h2.util.StringUtils;

public class CompareModeIcu4J extends CompareMode {
   private final Comparator<String> collator;
   private volatile CompareModeIcu4J caseInsensitive;

   protected CompareModeIcu4J(String var1, int var2) {
      super(var1, var2);
      this.collator = getIcu4jCollator(var1, var2);
   }

   public int compareString(String var1, String var2, boolean var3) {
      if (var3 && this.getStrength() > 1) {
         CompareModeIcu4J var4 = this.caseInsensitive;
         if (var4 == null) {
            this.caseInsensitive = var4 = new CompareModeIcu4J(this.getName(), 1);
         }

         return var4.compareString(var1, var2, false);
      } else {
         return this.collator.compare(var1, var2);
      }
   }

   public boolean equalsChars(String var1, int var2, String var3, int var4, boolean var5) {
      return this.compareString(var1.substring(var2, var2 + 1), var3.substring(var4, var4 + 1), var5) == 0;
   }

   private static Comparator<String> getIcu4jCollator(String var0, int var1) {
      try {
         Comparator var2 = null;
         Class var3 = JdbcUtils.loadUserClass("com.ibm.icu.text.Collator");
         Method var4 = var3.getMethod("getInstance", Locale.class);
         int var5 = var0.length();
         Locale var9;
         if (var5 == 2) {
            Locale var6 = new Locale(StringUtils.toLowerEnglish(var0), "");
            if (compareLocaleNames(var6, var0)) {
               var2 = (Comparator)var4.invoke((Object)null, var6);
            }
         } else if (var5 == 5) {
            int var11 = var0.indexOf(95);
            if (var11 >= 0) {
               String var7 = StringUtils.toLowerEnglish(var0.substring(0, var11));
               String var8 = var0.substring(var11 + 1);
               var9 = new Locale(var7, var8);
               if (compareLocaleNames(var9, var0)) {
                  var2 = (Comparator)var4.invoke((Object)null, var9);
               }
            }
         }

         if (var2 == null) {
            Locale[] var12 = (Locale[])((Locale[])var3.getMethod("getAvailableLocales").invoke((Object)null));
            int var13 = var12.length;

            for(int var14 = 0; var14 < var13; ++var14) {
               var9 = var12[var14];
               if (compareLocaleNames(var9, var0)) {
                  var2 = (Comparator)var4.invoke((Object)null, var9);
                  break;
               }
            }
         }

         if (var2 == null) {
            throw DbException.getInvalidValueException("collator", var0);
         } else {
            var3.getMethod("setStrength", Integer.TYPE).invoke(var2, var1);
            return var2;
         }
      } catch (Exception var10) {
         throw DbException.convert(var10);
      }
   }
}
