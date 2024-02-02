package org.apache.commons.compress.harmony.pack200;

public class CPDouble extends CPConstant {
   private final double theDouble;

   public CPDouble(double theDouble) {
      this.theDouble = theDouble;
   }

   public int compareTo(Object obj) {
      return Double.compare(this.theDouble, ((CPDouble)obj).theDouble);
   }

   public double getDouble() {
      return this.theDouble;
   }
}
