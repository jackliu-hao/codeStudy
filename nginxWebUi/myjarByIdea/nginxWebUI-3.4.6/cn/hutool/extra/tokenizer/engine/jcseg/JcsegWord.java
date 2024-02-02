package cn.hutool.extra.tokenizer.engine.jcseg;

import cn.hutool.extra.tokenizer.Word;
import org.lionsoul.jcseg.IWord;

public class JcsegWord implements Word {
   private static final long serialVersionUID = 1L;
   private final IWord word;

   public JcsegWord(IWord word) {
      this.word = word;
   }

   public String getText() {
      return this.word.getValue();
   }

   public int getStartOffset() {
      return this.word.getPosition();
   }

   public int getEndOffset() {
      return this.getStartOffset() + this.word.getLength();
   }

   public String toString() {
      return this.getText();
   }
}
