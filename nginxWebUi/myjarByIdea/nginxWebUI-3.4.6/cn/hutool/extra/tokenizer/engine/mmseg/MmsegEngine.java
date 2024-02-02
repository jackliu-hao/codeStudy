package cn.hutool.extra.tokenizer.engine.mmseg;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import java.io.StringReader;

public class MmsegEngine implements TokenizerEngine {
   private final MMSeg mmSeg;

   public MmsegEngine() {
      Dictionary dict = Dictionary.getInstance();
      ComplexSeg seg = new ComplexSeg(dict);
      this.mmSeg = new MMSeg(new StringReader(""), seg);
   }

   public MmsegEngine(MMSeg mmSeg) {
      this.mmSeg = mmSeg;
   }

   public Result parse(CharSequence text) {
      this.mmSeg.reset(StrUtil.getReader(text));
      return new MmsegResult(this.mmSeg);
   }
}
