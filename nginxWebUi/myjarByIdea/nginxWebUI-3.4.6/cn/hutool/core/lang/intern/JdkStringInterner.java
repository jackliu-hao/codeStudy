package cn.hutool.core.lang.intern;

public class JdkStringInterner implements Interner<String> {
   public String intern(String sample) {
      return null == sample ? null : sample.intern();
   }
}
