package cn.hutool.extra.tokenizer.engine.hanlp;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;

public class HanLPEngine implements TokenizerEngine {
   private final Segment seg;

   public HanLPEngine() {
      this(HanLP.newSegment());
   }

   public HanLPEngine(Segment seg) {
      this.seg = seg;
   }

   public Result parse(CharSequence text) {
      return new HanLPResult(this.seg.seg(StrUtil.str(text)));
   }
}
