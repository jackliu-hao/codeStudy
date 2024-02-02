package org.apache.commons.compress.harmony.pack200;

public class CPInt extends CPConstant {
   private final int theInt;

   public CPInt(int theInt) {
      this.theInt = theInt;
   }

   public int compareTo(Object obj) {
      if (this.theInt > ((CPInt)obj).theInt) {
         return 1;
      } else {
         return this.theInt == ((CPInt)obj).theInt ? 0 : -1;
      }
   }

   public int getInt() {
      return this.theInt;
   }
}
