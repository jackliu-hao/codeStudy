package org.h2.value;

import java.util.Arrays;
import java.util.Locale;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

public final class ExtTypeInfoEnum extends ExtTypeInfo {
   private final String[] enumerators;
   private final String[] cleaned;
   private TypeInfo type;

   public static ExtTypeInfoEnum getEnumeratorsForBinaryOperation(Value var0, Value var1) {
      if (var0.getValueType() == 36) {
         return ((ValueEnum)var0).getEnumerators();
      } else if (var1.getValueType() == 36) {
         return ((ValueEnum)var1).getEnumerators();
      } else {
         throw DbException.get(50004, (String)("type1=" + var0.getValueType() + ", type2=" + var1.getValueType()));
      }
   }

   private static String sanitize(String var0) {
      if (var0 == null) {
         return null;
      } else {
         int var1 = var0.length();
         if (var1 > 1048576) {
            throw DbException.getValueTooLongException("ENUM", var0, (long)var1);
         } else {
            return var0.trim().toUpperCase(Locale.ENGLISH);
         }
      }
   }

   private static StringBuilder toSQL(StringBuilder var0, String[] var1) {
      var0.append('(');

      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var2 != 0) {
            var0.append(", ");
         }

         var0.append('\'');
         String var3 = var1[var2];
         int var4 = 0;

         for(int var5 = var3.length(); var4 < var5; ++var4) {
            char var6 = var3.charAt(var4);
            if (var6 == '\'') {
               var0.append('\'');
            }

            var0.append(var6);
         }

         var0.append('\'');
      }

      return var0.append(')');
   }

   public ExtTypeInfoEnum(String[] var1) {
      int var2;
      if (var1 != null && (var2 = var1.length) != 0) {
         if (var2 > 65536) {
            throw DbException.getValueTooLongException("ENUM", "(" + var2 + " elements)", (long)var2);
         } else {
            String[] var3 = new String[var2];

            for(int var4 = 0; var4 < var2; ++var4) {
               String var5 = sanitize(var1[var4]);
               if (var5 == null || var5.isEmpty()) {
                  throw DbException.get(22032);
               }

               for(int var6 = 0; var6 < var4; ++var6) {
                  if (var5.equals(var3[var6])) {
                     throw DbException.get(22033, (String)toSQL(new StringBuilder(), var1).toString());
                  }
               }

               var3[var4] = var5;
            }

            this.enumerators = var1;
            this.cleaned = Arrays.equals(var3, var1) ? var1 : var3;
         }
      } else {
         throw DbException.get(22032);
      }
   }

   TypeInfo getType() {
      TypeInfo var1 = this.type;
      if (var1 == null) {
         int var2 = 0;
         String[] var3 = this.enumerators;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            int var7 = var6.length();
            if (var7 > var2) {
               var2 = var7;
            }
         }

         this.type = var1 = new TypeInfo(36, (long)var2, 0, this);
      }

      return var1;
   }

   public int getCount() {
      return this.enumerators.length;
   }

   public String getEnumerator(int var1) {
      return this.enumerators[var1];
   }

   public ValueEnum getValue(int var1, CastDataProvider var2) {
      String var3;
      if (var2 != null && var2.zeroBasedEnums()) {
         if (var1 < 0 || var1 >= this.enumerators.length) {
            throw DbException.get(22030, (String[])(this.getTraceSQL(), Integer.toString(var1)));
         }

         var3 = this.enumerators[var1];
      } else {
         if (var1 < 1 || var1 > this.enumerators.length) {
            throw DbException.get(22030, (String[])(this.getTraceSQL(), Integer.toString(var1)));
         }

         var3 = this.enumerators[var1 - 1];
      }

      return new ValueEnum(this, var3, var1);
   }

   public ValueEnum getValue(String var1, CastDataProvider var2) {
      ValueEnum var3 = this.getValueOrNull(var1, var2);
      if (var3 == null) {
         throw DbException.get(22030, (String[])(this.toString(), var1));
      } else {
         return var3;
      }
   }

   private ValueEnum getValueOrNull(String var1, CastDataProvider var2) {
      String var3 = sanitize(var1);
      if (var3 != null) {
         int var4 = 0;

         for(int var5 = var2 != null && var2.zeroBasedEnums() ? 0 : 1; var4 < this.cleaned.length; ++var5) {
            if (var3.equals(this.cleaned[var4])) {
               return new ValueEnum(this, this.enumerators[var4], var5);
            }

            ++var4;
         }
      }

      return null;
   }

   public int hashCode() {
      return Arrays.hashCode(this.enumerators) + 203117;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 != null && var1.getClass() == ExtTypeInfoEnum.class ? Arrays.equals(this.enumerators, ((ExtTypeInfoEnum)var1).enumerators) : false;
      }
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return toSQL(var1, this.enumerators);
   }
}
