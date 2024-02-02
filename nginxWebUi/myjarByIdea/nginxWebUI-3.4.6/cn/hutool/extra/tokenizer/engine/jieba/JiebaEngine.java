package cn.hutool.extra.tokenizer.engine.jieba;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;

public class JiebaEngine implements TokenizerEngine {
   private final JiebaSegmenter jiebaSegmenter;
   private final JiebaSegmenter.SegMode mode;

   public JiebaEngine() {
      this(SegMode.SEARCH);
   }

   public JiebaEngine(JiebaSegmenter.SegMode mode) {
      this.jiebaSegmenter = new JiebaSegmenter();
      this.mode = mode;
   }

   public Result parse(CharSequence text) {
      return new JiebaResult(this.jiebaSegmenter.process(StrUtil.str(text), this.mode));
   }
}
