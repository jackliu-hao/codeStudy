package cn.hutool.captcha.generator;

import cn.hutool.core.math.Calculator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

public class MathGenerator implements CodeGenerator {
   private static final long serialVersionUID = -5514819971774091076L;
   private static final String operators = "+-*";
   private final int numberLength;

   public MathGenerator() {
      this(2);
   }

   public MathGenerator(int numberLength) {
      this.numberLength = numberLength;
   }

   public String generate() {
      int limit = this.getLimit();
      String number1 = Integer.toString(RandomUtil.randomInt(limit));
      String number2 = Integer.toString(RandomUtil.randomInt(limit));
      number1 = StrUtil.padAfter(number1, this.numberLength, ' ');
      number2 = StrUtil.padAfter(number2, this.numberLength, ' ');
      return StrUtil.builder().append(number1).append(RandomUtil.randomChar("+-*")).append(number2).append('=').toString();
   }

   public boolean verify(String code, String userInputCode) {
      int result;
      try {
         result = Integer.parseInt(userInputCode);
      } catch (NumberFormatException var5) {
         return false;
      }

      int calculateResult = (int)Calculator.conversion(code);
      return result == calculateResult;
   }

   public int getLength() {
      return this.numberLength * 2 + 2;
   }

   private int getLimit() {
      return Integer.parseInt("1" + StrUtil.repeat('0', this.numberLength));
   }
}
