package cn.hutool.extra.tokenizer.engine.ansj;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import org.ansj.splitWord.Analysis;
import org.ansj.splitWord.analysis.ToAnalysis;

public class AnsjEngine implements TokenizerEngine {
   private final Analysis analysis;

   public AnsjEngine() {
      this(new ToAnalysis());
   }

   public AnsjEngine(Analysis analysis) {
      this.analysis = analysis;
   }

   public Result parse(CharSequence text) {
      return new AnsjResult(this.analysis.parseStr(StrUtil.str(text)));
   }
}
