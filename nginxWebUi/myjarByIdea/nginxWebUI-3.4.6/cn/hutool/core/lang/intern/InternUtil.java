package cn.hutool.core.lang.intern;

public class InternUtil {
   public static <T> Interner<T> createWeakInterner() {
      return new WeakInterner();
   }

   public static Interner<String> createJdkInterner() {
      return new JdkStringInterner();
   }

   public static Interner<String> createStringInterner(boolean isWeak) {
      return isWeak ? createWeakInterner() : createJdkInterner();
   }
}
