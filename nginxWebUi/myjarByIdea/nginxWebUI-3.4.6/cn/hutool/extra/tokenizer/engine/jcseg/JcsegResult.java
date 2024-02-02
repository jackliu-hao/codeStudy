package cn.hutool.extra.tokenizer.engine.jcseg;

import cn.hutool.extra.tokenizer.AbstractResult;
import cn.hutool.extra.tokenizer.TokenizerException;
import cn.hutool.extra.tokenizer.Word;
import java.io.IOException;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.IWord;

public class JcsegResult extends AbstractResult {
   private final ISegment result;

   public JcsegResult(ISegment segment) {
      this.result = segment;
   }

   protected Word nextWord() {
      IWord word;
      try {
         word = this.result.next();
      } catch (IOException var3) {
         throw new TokenizerException(var3);
      }

      return null == word ? null : new JcsegWord(word);
   }
}
