package cn.hutool.core.math;

public class BitStatusUtil {
   public static int add(int states, int stat) {
      check(states, stat);
      return states | stat;
   }

   public static boolean has(int states, int stat) {
      check(states, stat);
      return (states & stat) == stat;
   }

   public static int remove(int states, int stat) {
      check(states, stat);
      return has(states, stat) ? states ^ stat : states;
   }

   public static int clear() {
      return 0;
   }

   private static void check(int... args) {
      int[] var1 = args;
      int var2 = args.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         int arg = var1[var3];
         if (arg < 0) {
            throw new IllegalArgumentException(arg + " 必须大于等于0");
         }

         if ((arg & 1) == 1) {
            throw new IllegalArgumentException(arg + " 不是偶数");
         }
      }

   }
}
