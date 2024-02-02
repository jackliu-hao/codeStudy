package cn.hutool.captcha.generator;

public abstract class AbstractGenerator implements CodeGenerator {
   private static final long serialVersionUID = 8685744597154953479L;
   protected final String baseStr;
   protected final int length;

   public AbstractGenerator(int count) {
      this("abcdefghijklmnopqrstuvwxyz0123456789", count);
   }

   public AbstractGenerator(String baseStr, int length) {
      this.baseStr = baseStr;
      this.length = length;
   }

   public int getLength() {
      return this.length;
   }
}
