package cn.hutool.extra.tokenizer.engine.word;

import cn.hutool.extra.tokenizer.Word;

public class WordWord implements Word {
   private static final long serialVersionUID = 1L;
   private final org.apdplat.word.segmentation.Word word;

   public WordWord(org.apdplat.word.segmentation.Word word) {
      this.word = word;
   }

   public String getText() {
      return this.word.getText();
   }

   public int getStartOffset() {
      return -1;
   }

   public int getEndOffset() {
      return -1;
   }

   public String toString() {
      return this.getText();
   }
}
