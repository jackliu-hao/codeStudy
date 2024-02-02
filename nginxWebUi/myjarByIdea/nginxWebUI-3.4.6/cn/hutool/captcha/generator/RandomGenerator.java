package cn.hutool.captcha.generator;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

public class RandomGenerator extends AbstractGenerator {
   private static final long serialVersionUID = -7802758587765561876L;

   public RandomGenerator(int count) {
      super(count);
   }

   public RandomGenerator(String baseStr, int length) {
      super(baseStr, length);
   }

   public String generate() {
      return RandomUtil.randomString(this.baseStr, this.length);
   }

   public boolean verify(String code, String userInputCode) {
      return StrUtil.isNotBlank(userInputCode) ? StrUtil.equalsIgnoreCase(code, userInputCode) : false;
   }
}
