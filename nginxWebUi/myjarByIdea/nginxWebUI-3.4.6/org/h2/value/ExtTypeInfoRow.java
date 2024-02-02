package org.h2.value;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.h2.message.DbException;
import org.h2.util.ParserUtil;

public final class ExtTypeInfoRow extends ExtTypeInfo {
   private final LinkedHashMap<String, TypeInfo> fields;
   private int hash;

   public ExtTypeInfoRow(Typed[] var1) {
      this(var1, var1.length);
   }

   public ExtTypeInfoRow(Typed[] var1, int var2) {
      if (var2 > 16384) {
         throw DbException.get(54011, (String)"16384");
      } else {
         LinkedHashMap var3 = new LinkedHashMap((int)Math.ceil((double)var2 / 0.75));
         int var4 = 0;

         while(var4 < var2) {
            TypeInfo var5 = var1[var4].getType();
            StringBuilder var10001 = (new StringBuilder()).append("C");
            ++var4;
            var3.put(var10001.append(var4).toString(), var5);
         }

         this.fields = var3;
      }
   }

   public ExtTypeInfoRow(LinkedHashMap<String, TypeInfo> var1) {
      if (var1.size() > 16384) {
         throw DbException.get(54011, (String)"16384");
      } else {
         this.fields = var1;
      }
   }

   public Set<Map.Entry<String, TypeInfo>> getFields() {
      return this.fields.entrySet();
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      var1.append('(');
      boolean var3 = false;
      Iterator var4 = this.fields.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry var5 = (Map.Entry)var4.next();
         if (var3) {
            var1.append(", ");
         }

         var3 = true;
         ParserUtil.quoteIdentifier(var1, (String)var5.getKey(), var2).append(' ');
         ((TypeInfo)var5.getValue()).getSQL(var1, var2);
      }

      return var1.append(')');
   }

   public int hashCode() {
      int var1 = this.hash;
      if (var1 != 0) {
         return var1;
      } else {
         var1 = 67378403;

         Map.Entry var3;
         for(Iterator var2 = this.fields.entrySet().iterator(); var2.hasNext(); var1 = (var1 * 31 + ((String)var3.getKey()).hashCode()) * 37 + ((TypeInfo)var3.getValue()).hashCode()) {
            var3 = (Map.Entry)var2.next();
         }

         return this.hash = var1;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1.getClass() != ExtTypeInfoRow.class) {
         return false;
      } else {
         LinkedHashMap var2 = ((ExtTypeInfoRow)var1).fields;
         int var3 = this.fields.size();
         if (var3 != var2.size()) {
            return false;
         } else {
            Iterator var4 = this.fields.entrySet().iterator();
            Iterator var5 = var2.entrySet().iterator();

            Map.Entry var6;
            Map.Entry var7;
            do {
               if (!var4.hasNext()) {
                  return true;
               }

               var6 = (Map.Entry)var4.next();
               var7 = (Map.Entry)var5.next();
            } while(((String)var6.getKey()).equals(var7.getKey()) && ((TypeInfo)var6.getValue()).equals(var7.getValue()));

            return false;
         }
      }
   }
}
