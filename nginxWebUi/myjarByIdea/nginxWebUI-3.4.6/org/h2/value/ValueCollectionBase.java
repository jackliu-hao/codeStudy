package org.h2.value;

import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

public abstract class ValueCollectionBase extends Value {
   final Value[] values;
   private int hash;

   ValueCollectionBase(Value[] var1) {
      this.values = var1;
   }

   public Value[] getList() {
      return this.values;
   }

   public int hashCode() {
      if (this.hash != 0) {
         return this.hash;
      } else {
         int var1 = this.getValueType();
         Value[] var2 = this.values;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Value var5 = var2[var4];
            var1 = var1 * 31 + var5.hashCode();
         }

         this.hash = var1;
         return var1;
      }
   }

   public int compareWithNull(Value var1, boolean var2, CastDataProvider var3, CompareMode var4) {
      if (var1 == ValueNull.INSTANCE) {
         return Integer.MIN_VALUE;
      } else {
         int var6 = this.getValueType();
         int var7 = var1.getValueType();
         if (var7 != var6) {
            throw var1.getDataConversionError(var6);
         } else {
            ValueCollectionBase var8 = (ValueCollectionBase)var1;
            Value[] var9 = this.values;
            Value[] var10 = var8.values;
            int var11 = var9.length;
            int var12 = var10.length;
            if (var11 != var12) {
               if (var6 == 41) {
                  throw DbException.get(21002);
               }

               if (var2) {
                  return 1;
               }
            }

            int var14;
            Value var15;
            Value var16;
            int var17;
            if (var2) {
               boolean var18 = false;

               for(var14 = 0; var14 < var11; ++var14) {
                  var15 = var9[var14];
                  var16 = var10[var14];
                  var17 = var15.compareWithNull(var16, var2, var3, var4);
                  if (var17 != 0) {
                     if (var17 != Integer.MIN_VALUE) {
                        return var17;
                     }

                     var18 = true;
                  }
               }

               return var18 ? Integer.MIN_VALUE : 0;
            } else {
               int var13 = Math.min(var11, var12);

               for(var14 = 0; var14 < var13; ++var14) {
                  var15 = var9[var14];
                  var16 = var10[var14];
                  var17 = var15.compareWithNull(var16, var2, var3, var4);
                  if (var17 != 0) {
                     return var17;
                  }
               }

               return Integer.compare(var11, var12);
            }
         }
      }
   }

   public boolean containsNull() {
      Value[] var1 = this.values;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Value var4 = var1[var3];
         if (var4.containsNull()) {
            return true;
         }
      }

      return false;
   }

   public int getMemory() {
      int var1 = 72 + this.values.length * 8;
      Value[] var2 = this.values;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Value var5 = var2[var4];
         var1 += var5.getMemory();
      }

      return var1;
   }
}
