package org.apache.commons.compress.harmony.pack200;

public class CPLong extends CPConstant {
   private final long theLong;

   public CPLong(long theLong) {
      this.theLong = theLong;
   }

   public int compareTo(Object obj) {
      if (this.theLong > ((CPLong)obj).theLong) {
         return 1;
      } else {
         return this.theLong == ((CPLong)obj).theLong ? 0 : -1;
      }
   }

   public long getLong() {
      return this.theLong;
   }

   public String toString() {
      return "" + this.theLong;
   }
}
