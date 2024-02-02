package cn.hutool.core.date;

public enum Quarter {
   Q1(1),
   Q2(2),
   Q3(3),
   Q4(4);

   private final int value;

   private Quarter(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }

   public static Quarter of(int intValue) {
      switch (intValue) {
         case 1:
            return Q1;
         case 2:
            return Q2;
         case 3:
            return Q3;
         case 4:
            return Q4;
         default:
            return null;
      }
   }
}
