package org.h2.value;

import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.result.SimpleResult;

public final class ValueRow extends ValueCollectionBase {
   public static final ValueRow EMPTY;
   private TypeInfo type;

   private ValueRow(TypeInfo var1, Value[] var2) {
      super(var2);
      int var3 = var2.length;
      if (var3 > 16384) {
         throw DbException.get(54011, (String)"16384");
      } else {
         if (var1 != null) {
            if (var1.getValueType() != 41 || ((ExtTypeInfoRow)var1.getExtTypeInfo()).getFields().size() != var3) {
               throw DbException.getInternalError();
            }

            this.type = var1;
         }

      }
   }

   public static ValueRow get(Value[] var0) {
      return new ValueRow((TypeInfo)null, var0);
   }

   public static ValueRow get(ExtTypeInfoRow var0, Value[] var1) {
      return new ValueRow(new TypeInfo(41, -1L, -1, var0), var1);
   }

   public static ValueRow get(TypeInfo var0, Value[] var1) {
      return new ValueRow(var0, var1);
   }

   public TypeInfo getType() {
      TypeInfo var1 = this.type;
      if (var1 == null) {
         this.type = var1 = TypeInfo.getTypeInfo(41, 0L, 0, new ExtTypeInfoRow(this.values));
      }

      return var1;
   }

   public int getValueType() {
      return 41;
   }

   public String getString() {
      StringBuilder var1 = new StringBuilder("ROW (");

      for(int var2 = 0; var2 < this.values.length; ++var2) {
         if (var2 > 0) {
            var1.append(", ");
         }

         var1.append(this.values[var2].getString());
      }

      return var1.append(')').toString();
   }

   public SimpleResult getResult() {
      SimpleResult var1 = new SimpleResult();
      int var2 = 0;
      int var3 = this.values.length;

      while(var2 < var3) {
         Value var4 = this.values[var2++];
         var1.addColumn("C" + var2, var4.getType());
      }

      var1.addRow(this.values);
      return var1;
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      ValueRow var4 = (ValueRow)var1;
      if (this.values == var4.values) {
         return 0;
      } else {
         int var5 = this.values.length;
         if (var5 != var4.values.length) {
            throw DbException.get(21002);
         } else {
            for(int var6 = 0; var6 < var5; ++var6) {
               Value var7 = this.values[var6];
               Value var8 = var4.values[var6];
               int var9 = var7.compareTo(var8, var3, var2);
               if (var9 != 0) {
                  return var9;
               }
            }

            return 0;
         }
      }
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      var1.append("ROW (");
      int var3 = this.values.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         if (var4 > 0) {
            var1.append(", ");
         }

         this.values[var4].getSQL(var1, var2);
      }

      return var1.append(')');
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ValueRow)) {
         return false;
      } else {
         ValueRow var2 = (ValueRow)var1;
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
      EMPTY = get(Value.EMPTY_VALUES);
   }
}
