package org.h2.value;

import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

public final class ValueArray extends ValueCollectionBase {
   public static final ValueArray EMPTY;
   private TypeInfo type;
   private TypeInfo componentType;

   private ValueArray(TypeInfo var1, Value[] var2, CastDataProvider var3) {
      super(var2);
      int var4 = var2.length;
      if (var4 > 65536) {
         String var6 = getTypeName(this.getValueType());
         throw DbException.getValueTooLongException(var6, var6, (long)var4);
      } else {
         for(int var5 = 0; var5 < var4; ++var5) {
            var2[var5] = var2[var5].castTo(var1, var3);
         }

         this.componentType = var1;
      }
   }

   public static ValueArray get(Value[] var0, CastDataProvider var1) {
      return new ValueArray(TypeInfo.getHigherType(var0), var0, var1);
   }

   public static ValueArray get(TypeInfo var0, Value[] var1, CastDataProvider var2) {
      return new ValueArray(var0, var1, var2);
   }

   public TypeInfo getType() {
      TypeInfo var1 = this.type;
      if (var1 == null) {
         TypeInfo var2 = this.getComponentType();
         this.type = var1 = TypeInfo.getTypeInfo(this.getValueType(), (long)this.values.length, 0, var2);
      }

      return var1;
   }

   public int getValueType() {
      return 40;
   }

   public TypeInfo getComponentType() {
      return this.componentType;
   }

   public String getString() {
      StringBuilder var1 = (new StringBuilder()).append('[');

      for(int var2 = 0; var2 < this.values.length; ++var2) {
         if (var2 > 0) {
            var1.append(", ");
         }

         var1.append(this.values[var2].getString());
      }

      return var1.append(']').toString();
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      ValueArray var4 = (ValueArray)var1;
      if (this.values == var4.values) {
         return 0;
      } else {
         int var5 = this.values.length;
         int var6 = var4.values.length;
         int var7 = Math.min(var5, var6);

         for(int var8 = 0; var8 < var7; ++var8) {
            Value var9 = this.values[var8];
            Value var10 = var4.values[var8];
            int var11 = var9.compareTo(var10, var3, var2);
            if (var11 != 0) {
               return var11;
            }
         }

         return Integer.compare(var5, var6);
      }
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      var1.append("ARRAY [");
      int var3 = this.values.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         if (var4 > 0) {
            var1.append(", ");
         }

         this.values[var4].getSQL(var1, var2);
      }

      return var1.append(']');
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ValueArray)) {
         return false;
      } else {
         ValueArray var2 = (ValueArray)var1;
         if (this.values == var2.values) {
            return true;
         } else {
            int var3 = this.values.length;
            if (var3 != var2.values.length) {
               return false;
            } else {
               for(int var4 = 0; var4 < var3; ++var4) {
                  if (!this.values[var4].equals(var2.values[var4])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   static {
      EMPTY = get(TypeInfo.TYPE_NULL, Value.EMPTY_VALUES, (CastDataProvider)null);
   }
}
