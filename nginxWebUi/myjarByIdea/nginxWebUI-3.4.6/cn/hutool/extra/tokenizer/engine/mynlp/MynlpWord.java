package cn.hutool.extra.tokenizer.engine.mynlp;

import cn.hutool.extra.tokenizer.Word;
import com.mayabot.nlp.segment.WordTerm;

public class MynlpWord implements Word {
   private static final long serialVersionUID = 1L;
   private final WordTerm word;

   public MynlpWord(WordTerm word) {
      this.word = word;
   }

   public String getText() {
      return this.word.getWord();
   }

   public int getStartOffset() {
      return this.word.offset;
   }

   public int getEndOffset() {
      return this.getStartOffset() + this.word.word.length();
   }

   public String toString() {
      return this.getText();
   }
}
