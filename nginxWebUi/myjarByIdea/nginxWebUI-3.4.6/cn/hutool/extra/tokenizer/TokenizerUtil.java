package cn.hutool.extra.tokenizer;

import cn.hutool.extra.tokenizer.engine.TokenizerFactory;

public class TokenizerUtil {
   public static TokenizerEngine createEngine() {
      return TokenizerFactory.create();
   }
}
