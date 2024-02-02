package org.apache.commons.compress.harmony.pack200;

public class CPString extends CPConstant {
   private final String string;
   private final CPUTF8 utf8;

   public CPString(CPUTF8 utf8) {
      this.utf8 = utf8;
      this.string = utf8.getUnderlyingString();
   }

   public int compareTo(Object arg0) {
      return this.string.compareTo(((CPString)arg0).string);
   }

   public String toString() {
      return this.string;
   }

   public int getIndexInCpUtf8() {
      return this.utf8.getIndex();
   }
}
