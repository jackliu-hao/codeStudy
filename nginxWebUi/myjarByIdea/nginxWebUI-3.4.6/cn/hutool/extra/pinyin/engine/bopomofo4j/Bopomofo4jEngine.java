package cn.hutool.extra.pinyin.engine.bopomofo4j;

import cn.hutool.extra.pinyin.PinyinEngine;
import com.rnkrsoft.bopomofo4j.Bopomofo4j;
import com.rnkrsoft.bopomofo4j.ToneType;

public class Bopomofo4jEngine implements PinyinEngine {
   public Bopomofo4jEngine() {
      Bopomofo4j.local();
   }

   public String getPinyin(char c) {
      return Bopomofo4j.pinyin(String.valueOf(c), ToneType.WITHOUT_TONE, false, false, "");
   }

   public String getPinyin(String str, String separator) {
      return Bopomofo4j.pinyin(str, ToneType.WITHOUT_TONE, false, false, separator);
   }
}
