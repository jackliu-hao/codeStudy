package cn.hutool.extra.pinyin.engine.tinypinyin;

import cn.hutool.extra.pinyin.PinyinEngine;
import com.github.promeg.pinyinhelper.Pinyin;

public class TinyPinyinEngine implements PinyinEngine {
   public TinyPinyinEngine() {
      this((Pinyin.Config)null);
   }

   public TinyPinyinEngine(Pinyin.Config config) {
      Pinyin.init(config);
   }

   public String getPinyin(char c) {
      return !Pinyin.isChinese(c) ? String.valueOf(c) : Pinyin.toPinyin(c).toLowerCase();
   }

   public String getPinyin(String str, String separator) {
      return Pinyin.toPinyin(str, separator).toLowerCase();
   }
}
