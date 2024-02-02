package cn.hutool.extra.pinyin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import java.util.List;

public interface PinyinEngine {
   String getPinyin(char var1);

   String getPinyin(String var1, String var2);

   default char getFirstLetter(char c) {
      return this.getPinyin(c).charAt(0);
   }

   default String getFirstLetter(String str, String separator) {
      String splitSeparator = StrUtil.isEmpty(separator) ? "#" : separator;
      List<String> split = StrUtil.split(this.getPinyin(str, splitSeparator), splitSeparator);
      return CollUtil.join(split, separator, (s) -> {
         return String.valueOf(s.length() > 0 ? s.charAt(0) : "");
      });
   }
}
