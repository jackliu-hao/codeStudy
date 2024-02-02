package cn.hutool.extra.tokenizer.engine.mmseg;

import cn.hutool.extra.tokenizer.AbstractResult;
import cn.hutool.extra.tokenizer.TokenizerException;
import cn.hutool.extra.tokenizer.Word;
import com.chenlb.mmseg4j.MMSeg;
import java.io.IOException;

public class MmsegResult extends AbstractResult {
   private final MMSeg mmSeg;

   public MmsegResult(MMSeg mmSeg) {
      this.mmSeg = mmSeg;
   }

   protected Word nextWord() {
      com.chenlb.mmseg4j.Word next;
      try {
         next = this.mmSeg.next();
      } catch (IOException var3) {
         throw new TokenizerException(var3);
      }

      return null == next ? null : new MmsegWord(next);
   }
}
