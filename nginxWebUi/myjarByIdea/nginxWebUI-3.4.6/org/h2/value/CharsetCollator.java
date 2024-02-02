package org.h2.value;

import java.nio.charset.Charset;
import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import org.h2.util.Bits;

public class CharsetCollator extends Collator {
   static final Comparator<byte[]> COMPARATOR = Bits::compareNotNullSigned;
   private final Charset charset;

   public CharsetCollator(Charset var1) {
      this.charset = var1;
   }

   public Charset getCharset() {
      return this.charset;
   }

   public int compare(String var1, String var2) {
      return COMPARATOR.compare(this.toBytes(var1), this.toBytes(var2));
   }

   byte[] toBytes(String var1) {
      if (this.getStrength() <= 1) {
         var1 = var1.toUpperCase(Locale.ROOT);
      }

      return var1.getBytes(this.charset);
   }

   public CollationKey getCollationKey(String var1) {
      return new CharsetCollationKey(var1);
   }

   public int hashCode() {
      return 255;
   }

   private class CharsetCollationKey extends CollationKey {
      private final byte[] bytes;

      CharsetCollationKey(String var2) {
         super(var2);
         this.bytes = CharsetCollator.this.toBytes(var2);
      }

      public int compareTo(CollationKey var1) {
         return CharsetCollator.COMPARATOR.compare(this.bytes, var1.toByteArray());
      }

      public byte[] toByteArray() {
         return this.bytes;
      }
   }
}
