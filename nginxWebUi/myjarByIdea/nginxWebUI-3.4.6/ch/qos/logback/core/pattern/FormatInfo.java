package ch.qos.logback.core.pattern;

public class FormatInfo {
   private int min = Integer.MIN_VALUE;
   private int max = Integer.MAX_VALUE;
   private boolean leftPad = true;
   private boolean leftTruncate = true;

   public FormatInfo() {
   }

   public FormatInfo(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public FormatInfo(int min, int max, boolean leftPad, boolean leftTruncate) {
      this.min = min;
      this.max = max;
      this.leftPad = leftPad;
      this.leftTruncate = leftTruncate;
   }

   public static FormatInfo valueOf(String str) throws IllegalArgumentException {
      if (str == null) {
         throw new NullPointerException("Argument cannot be null");
      } else {
         FormatInfo fi = new FormatInfo();
         int indexOfDot = str.indexOf(46);
         String minPart = null;
         String maxPart = null;
         if (indexOfDot != -1) {
            minPart = str.substring(0, indexOfDot);
            if (indexOfDot + 1 == str.length()) {
               throw new IllegalArgumentException("Formatting string [" + str + "] should not end with '.'");
            }

            maxPart = str.substring(indexOfDot + 1);
         } else {
            minPart = str;
         }

         int max;
         if (minPart != null && minPart.length() > 0) {
            max = Integer.parseInt(minPart);
            if (max >= 0) {
               fi.min = max;
            } else {
               fi.min = -max;
               fi.leftPad = false;
            }
         }

         if (maxPart != null && maxPart.length() > 0) {
            max = Integer.parseInt(maxPart);
            if (max >= 0) {
               fi.max = max;
            } else {
               fi.max = -max;
               fi.leftTruncate = false;
            }
         }

         return fi;
      }
   }

   public boolean isLeftPad() {
      return this.leftPad;
   }

   public void setLeftPad(boolean leftAlign) {
      this.leftPad = leftAlign;
   }

   public int getMax() {
      return this.max;
   }

   public void setMax(int max) {
      this.max = max;
   }

   public int getMin() {
      return this.min;
   }

   public void setMin(int min) {
      this.min = min;
   }

   public boolean isLeftTruncate() {
      return this.leftTruncate;
   }

   public void setLeftTruncate(boolean leftTruncate) {
      this.leftTruncate = leftTruncate;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof FormatInfo)) {
         return false;
      } else {
         FormatInfo r = (FormatInfo)o;
         return this.min == r.min && this.max == r.max && this.leftPad == r.leftPad && this.leftTruncate == r.leftTruncate;
      }
   }

   public int hashCode() {
      int result = this.min;
      result = 31 * result + this.max;
      result = 31 * result + (this.leftPad ? 1 : 0);
      result = 31 * result + (this.leftTruncate ? 1 : 0);
      return result;
   }

   public String toString() {
      return "FormatInfo(" + this.min + ", " + this.max + ", " + this.leftPad + ", " + this.leftTruncate + ")";
   }
}
