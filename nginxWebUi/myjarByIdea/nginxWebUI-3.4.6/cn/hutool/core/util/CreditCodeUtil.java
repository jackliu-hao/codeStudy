package cn.hutool.core.util;

import cn.hutool.core.lang.PatternPool;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class CreditCodeUtil {
   public static final Pattern CREDIT_CODE_PATTERN;
   private static final int[] WEIGHT;
   private static final char[] BASE_CODE_ARRAY;
   private static final Map<Character, Integer> CODE_INDEX_MAP;

   public static boolean isCreditCodeSimple(CharSequence creditCode) {
      return StrUtil.isBlank(creditCode) ? false : ReUtil.isMatch(CREDIT_CODE_PATTERN, creditCode);
   }

   public static boolean isCreditCode(CharSequence creditCode) {
      if (!isCreditCodeSimple(creditCode)) {
         return false;
      } else {
         int parityBit = getParityBit(creditCode);
         if (parityBit < 0) {
            return false;
         } else {
            return creditCode.charAt(17) == BASE_CODE_ARRAY[parityBit];
         }
      }
   }

   public static String randomCreditCode() {
      StringBuilder buf = new StringBuilder(18);

      int i;
      int num;
      for(i = 0; i < 2; ++i) {
         num = RandomUtil.randomInt(BASE_CODE_ARRAY.length - 1);
         buf.append(Character.toUpperCase(BASE_CODE_ARRAY[num]));
      }

      for(i = 2; i < 8; ++i) {
         num = RandomUtil.randomInt(10);
         buf.append(BASE_CODE_ARRAY[num]);
      }

      for(i = 8; i < 17; ++i) {
         num = RandomUtil.randomInt(BASE_CODE_ARRAY.length - 1);
         buf.append(BASE_CODE_ARRAY[num]);
      }

      String code = buf.toString();
      return code + BASE_CODE_ARRAY[getParityBit(code)];
   }

   private static int getParityBit(CharSequence creditCode) {
      int sum = 0;

      int i;
      for(i = 0; i < 17; ++i) {
         Integer codeIndex = (Integer)CODE_INDEX_MAP.get(creditCode.charAt(i));
         if (null == codeIndex) {
            return -1;
         }

         sum += codeIndex * WEIGHT[i];
      }

      i = 31 - sum % 31;
      return i == 31 ? 0 : i;
   }

   static {
      CREDIT_CODE_PATTERN = PatternPool.CREDIT_CODE;
      WEIGHT = new int[]{1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};
      BASE_CODE_ARRAY = "0123456789ABCDEFGHJKLMNPQRTUWXY".toCharArray();
      CODE_INDEX_MAP = new ConcurrentHashMap();

      for(int i = 0; i < BASE_CODE_ARRAY.length; ++i) {
         CODE_INDEX_MAP.put(BASE_CODE_ARRAY[i], i);
      }

   }
}
