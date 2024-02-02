package cn.hutool.extra.pinyin;

import cn.hutool.extra.pinyin.engine.PinyinFactory;

public class PinyinUtil {
   private static final String CHINESE_REGEX = "[\\u4e00-\\u9fa5]";

   public static PinyinEngine getEngine() {
      return PinyinFactory.get();
   }

   public static String getPinyin(char c) {
      return getEngine().getPinyin(c);
   }

   public static String getPinyin(String str) {
      return getPinyin(str, " ");
   }

   public static String getPinyin(String str, String separator) {
      return getEngine().getPinyin(str, separator);
   }

   public static char getFirstLetter(char c) {
      return getEngine().getFirstLetter(c);
   }

   public static String getFirstLetter(String str, String separator) {
      return getEngine().getFirstLetter(str, separator);
   }

   public static boolean isChinese(char c) {
      return 12295 == c || String.valueOf(c).matches("[\\u4e00-\\u9fa5]");
   }
}
