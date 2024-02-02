package org.h2.value;

import java.nio.charset.Charset;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import org.h2.engine.CastDataProvider;
import org.h2.util.StringUtils;

public class CompareMode implements Comparator<Value> {
   public static final String OFF = "OFF";
   public static final String DEFAULT = "DEFAULT_";
   public static final String ICU4J = "ICU4J_";
   public static final String CHARSET = "CHARSET_";
   private static Locale[] LOCALES;
   private static volatile CompareMode lastUsed;
   private static final boolean CAN_USE_ICU4J;
   private final String name;
   private final int strength;

   protected CompareMode(String var1, int var2) {
      this.name = var1;
      this.strength = var2;
   }

   public static CompareMode getInstance(String var0, int var1) {
      CompareMode var2 = lastUsed;
      if (var2 != null && Objects.equals(var2.name, var0) && var2.strength == var1) {
         return var2;
      } else {
         Object var4;
         if (var0 != null && !var0.equals("OFF")) {
            boolean var3;
            if (var0.startsWith("ICU4J_")) {
               var3 = true;
               var0 = var0.substring("ICU4J_".length());
            } else if (var0.startsWith("DEFAULT_")) {
               var3 = false;
               var0 = var0.substring("DEFAULT_".length());
            } else if (var0.startsWith("CHARSET_")) {
               var3 = false;
            } else {
               var3 = CAN_USE_ICU4J;
            }

            if (var3) {
               var4 = new CompareModeIcu4J(var0, var1);
            } else {
               var4 = new CompareModeDefault(var0, var1);
            }
         } else {
            var4 = new CompareMode(var0, var1);
         }

         lastUsed = (CompareMode)var4;
         return (CompareMode)var4;
      }
   }

   public static Locale[] getCollationLocales(boolean var0) {
      Locale[] var1 = LOCALES;
      if (var1 == null && !var0) {
         LOCALES = var1 = Collator.getAvailableLocales();
      }

      return var1;
   }

   public boolean equalsChars(String var1, int var2, String var3, int var4, boolean var5) {
      char var6 = var1.charAt(var2);
      char var7 = var3.charAt(var4);
      if (var6 == var7) {
         return true;
      } else {
         return var5 && (Character.toUpperCase(var6) == Character.toUpperCase(var7) || Character.toLowerCase(var6) == Character.toLowerCase(var7));
      }
   }

   public int compareString(String var1, String var2, boolean var3) {
      return var3 ? var1.compareToIgnoreCase(var2) : var1.compareTo(var2);
   }

   public static String getName(Locale var0) {
      Locale var1 = Locale.ENGLISH;
      String var2 = var0.getDisplayLanguage(var1) + ' ' + var0.getDisplayCountry(var1) + ' ' + var0.getVariant();
      var2 = StringUtils.toUpperEnglish(var2.trim().replace(' ', '_'));
      return var2;
   }

   static boolean compareLocaleNames(Locale var0, String var1) {
      return var1.equalsIgnoreCase(var0.toString()) || var1.equalsIgnoreCase(var0.toLanguageTag()) || var1.equalsIgnoreCase(getName(var0));
   }

   public static Collator getCollator(String var0) {
      Collator var1 = null;
      if (var0.startsWith("ICU4J_")) {
         var0 = var0.substring("ICU4J_".length());
      } else if (var0.startsWith("DEFAULT_")) {
         var0 = var0.substring("DEFAULT_".length());
      } else if (var0.startsWith("CHARSET_")) {
         return new CharsetCollator(Charset.forName(var0.substring("CHARSET_".length())));
      }

      int var2 = var0.length();
      Locale var3;
      Locale var6;
      if (var2 == 2) {
         var3 = new Locale(StringUtils.toLowerEnglish(var0), "");
         if (compareLocaleNames(var3, var0)) {
            var1 = Collator.getInstance(var3);
         }
      } else if (var2 == 5) {
         int var7 = var0.indexOf(95);
         if (var7 >= 0) {
            String var4 = StringUtils.toLowerEnglish(var0.substring(0, var7));
            String var5 = var0.substring(var7 + 1);
            var6 = new Locale(var4, var5);
            if (compareLocaleNames(var6, var0)) {
               var1 = Collator.getInstance(var6);
            }
         }
      } else if (var0.indexOf(45) > 0) {
         var3 = Locale.forLanguageTag(var0);
         if (!var3.getLanguage().isEmpty()) {
            return Collator.getInstance(var3);
         }
      }

      if (var1 == null) {
         Locale[] var8 = getCollationLocales(false);
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            var6 = var8[var10];
            if (compareLocaleNames(var6, var0)) {
               var1 = Collator.getInstance(var6);
               break;
            }
         }
      }

      return var1;
   }

   public String getName() {
      return this.name == null ? "OFF" : this.name;
   }

   public int getStrength() {
      return this.strength;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof CompareMode)) {
         return false;
      } else {
         CompareMode var2 = (CompareMode)var1;
         if (!this.getName().equals(var2.getName())) {
            return false;
         } else {
            return this.strength == var2.strength;
         }
      }
   }

   public int hashCode() {
      int var1 = 1;
      var1 = 31 * var1 + this.getName().hashCode();
      var1 = 31 * var1 + this.strength;
      return var1;
   }

   public int compare(Value var1, Value var2) {
      return var1.compareTo(var2, (CastDataProvider)null, this);
   }

   static {
      boolean var0 = false;

      try {
         Class.forName("com.ibm.icu.text.Collator");
         var0 = true;
      } catch (Exception var2) {
      }

      CAN_USE_ICU4J = var0;
   }
}
