package cn.hutool.extra.tokenizer.engine.jcseg;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.TokenizerException;
import java.io.IOException;
import java.io.StringReader;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.dic.DictionaryFactory;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;

public class JcsegEngine implements TokenizerEngine {
   private final ISegment segment;

   public JcsegEngine() {
      SegmenterConfig config = new SegmenterConfig(true);
      ADictionary dic = DictionaryFactory.createSingletonDictionary(config);
      this.segment = ISegment.COMPLEX.factory.create(config, dic);
   }

   public JcsegEngine(ISegment segment) {
      this.segment = segment;
   }

   public Result parse(CharSequence text) {
      try {
         this.segment.reset(new StringReader(StrUtil.str(text)));
      } catch (IOException var3) {
         throw new TokenizerException(var3);
      }

      return new JcsegResult(this.segment);
   }
}
