package cn.hutool.extra.pinyin.engine.houbbpinyin;

import cn.hutool.extra.pinyin.PinyinEngine;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;

public class HoubbPinyinEngine implements PinyinEngine {
   PinyinStyleEnum format;

   public HoubbPinyinEngine() {
      this((PinyinStyleEnum)null);
   }

   public HoubbPinyinEngine(PinyinStyleEnum format) {
      this.init(format);
   }

   public void init(PinyinStyleEnum format) {
      if (null == format) {
         format = PinyinStyleEnum.NORMAL;
      }

      this.format = format;
   }

   public String getPinyin(char c) {
      String result = PinyinHelper.toPinyin(String.valueOf(c), this.format);
      return result;
   }

   public String getPinyin(String str, String separator) {
      String result = PinyinHelper.toPinyin(str, this.format, separator);
      return result;
   }
}
