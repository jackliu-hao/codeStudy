package cn.hutool.extra.tokenizer.engine.ansj;

import cn.hutool.extra.tokenizer.Word;
import org.ansj.domain.Term;

public class AnsjWord implements Word {
   private static final long serialVersionUID = 1L;
   private final Term term;

   public AnsjWord(Term term) {
      this.term = term;
   }

   public String getText() {
      return this.term.getName();
   }

   public int getStartOffset() {
      return this.term.getOffe();
   }

   public int getEndOffset() {
      return this.getStartOffset() + this.getText().length();
   }

   public String toString() {
      return this.getText();
   }
}
