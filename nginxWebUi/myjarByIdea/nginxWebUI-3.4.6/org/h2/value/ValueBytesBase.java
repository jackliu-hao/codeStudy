package org.h2.value;

import java.util.Arrays;
import org.h2.engine.CastDataProvider;
import org.h2.util.Bits;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

abstract class ValueBytesBase extends Value {
   byte[] value;
   int hash;

   ValueBytesBase(byte[] var1) {
      this.value = var1;
   }

   public final byte[] getBytes() {
      return Utils.cloneByteArray(this.value);
   }

   public final byte[] getBytesNoCopy() {
      return this.value;
   }

   public final int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      return Bits.compareNotNullUnsigned(this.value, ((ValueBytesBase)var1).value);
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return StringUtils.convertBytesToHex(var1.append("X'"), this.value).append('\'');
   }

   public final int hashCode() {
      int var1 = this.hash;
      if (var1 == 0) {
         var1 = this.getClass().hashCode() ^ Utils.getByteArrayHash(this.value);
         if (var1 == 0) {
            var1 = 1234570417;
         }

         this.hash = var1;
      }

      return var1;
   }

   public int getMemory() {
      return this.value.length + 24;
   }

   public final boolean equals(Object var1) {
      return var1 != null && this.getClass() == var1.getClass() && Arrays.equals(this.value, ((ValueBytesBase)var1).value);
   }
}
