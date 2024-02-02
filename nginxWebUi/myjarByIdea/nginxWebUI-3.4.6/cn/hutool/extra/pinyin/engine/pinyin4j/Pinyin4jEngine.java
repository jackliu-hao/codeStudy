package cn.hutool.extra.pinyin.engine.pinyin4j;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinEngine;
import cn.hutool.extra.pinyin.PinyinException;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class Pinyin4jEngine implements PinyinEngine {
   HanyuPinyinOutputFormat format;

   public Pinyin4jEngine() {
      this((HanyuPinyinOutputFormat)null);
   }

   public Pinyin4jEngine(HanyuPinyinOutputFormat format) {
      this.init(format);
   }

   public void init(HanyuPinyinOutputFormat format) {
      if (null == format) {
         format = new HanyuPinyinOutputFormat();
         format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
         format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
         format.setVCharType(HanyuPinyinVCharType.WITH_V);
      }

      this.format = format;
   }

   public String getPinyin(char c) {
      String result;
      try {
         String[] results = PinyinHelper.toHanyuPinyinStringArray(c, this.format);
         result = ArrayUtil.isEmpty((Object[])results) ? String.valueOf(c) : results[0];
      } catch (BadHanyuPinyinOutputFormatCombination var4) {
         result = String.valueOf(c);
      }

      return result;
   }

   public String getPinyin(String str, String separator) {
      StrBuilder result = StrUtil.strBuilder();
      boolean isFirst = true;
      int strLen = str.length();

      try {
         for(int i = 0; i < strLen; ++i) {
            if (isFirst) {
               isFirst = false;
            } else {
               result.append((CharSequence)separator);
            }

            String[] pinyinStringArray = PinyinHelper.toHanyuPinyinStringArray(str.charAt(i), this.format);
            if (ArrayUtil.isEmpty((Object[])pinyinStringArray)) {
               result.append(str.charAt(i));
            } else {
               result.append((CharSequence)pinyinStringArray[0]);
            }
         }
      } catch (BadHanyuPinyinOutputFormatCombination var8) {
         throw new PinyinException(var8);
      }

      return result.toString();
   }
}
