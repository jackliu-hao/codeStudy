package cn.hutool.extra.tokenizer.engine.ikanalyzer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import java.io.Reader;
import org.wltea.analyzer.core.IKSegmenter;

public class IKAnalyzerEngine implements TokenizerEngine {
   private final IKSegmenter seg;

   public IKAnalyzerEngine() {
      this(new IKSegmenter((Reader)null, true));
   }

   public IKAnalyzerEngine(IKSegmenter seg) {
      this.seg = seg;
   }

   public Result parse(CharSequence text) {
      this.seg.reset(StrUtil.getReader(text));
      return new IKAnalyzerResult(this.seg);
   }
}
