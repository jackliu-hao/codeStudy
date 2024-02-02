package cn.hutool.extra.tokenizer.engine.mmseg;

import cn.hutool.extra.tokenizer.Word;

public class MmsegWord implements Word {
   private static final long serialVersionUID = 1L;
   private final com.chenlb.mmseg4j.Word word;

   public MmsegWord(com.chenlb.mmseg4j.Word word) {
      this.word = word;
   }

   public String getText() {
      return this.word.getString();
   }

   public int getStartOffset() {
      return this.word.getStartOffset();
   }

   public int getEndOffset() {
      return this.word.getEndOffset();
   }

   public String toString() {
      return this.getText();
   }
}
