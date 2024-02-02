package cn.hutool.extra.tokenizer.engine.analysis;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.TokenizerException;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

public class AnalysisEngine implements TokenizerEngine {
   private final Analyzer analyzer;

   public AnalysisEngine(Analyzer analyzer) {
      this.analyzer = analyzer;
   }

   public Result parse(CharSequence text) {
      TokenStream stream;
      try {
         stream = this.analyzer.tokenStream("text", StrUtil.str(text));
         stream.reset();
      } catch (IOException var4) {
         throw new TokenizerException(var4);
      }

      return new AnalysisResult(stream);
   }
}
