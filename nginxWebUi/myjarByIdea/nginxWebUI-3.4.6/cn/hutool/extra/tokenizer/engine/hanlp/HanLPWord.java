package cn.hutool.extra.tokenizer.engine.hanlp;

import cn.hutool.extra.tokenizer.Word;
import com.hankcs.hanlp.seg.common.Term;

public class HanLPWord implements Word {
   private static final long serialVersionUID = 1L;
   private final Term term;

   public HanLPWord(Term term) {
      this.term = term;
   }

   public String getText() {
      return this.term.word;
   }

   public int getStartOffset() {
      return this.term.offset;
   }

   public int getEndOffset() {
      return this.getStartOffset() + this.term.length();
   }

   public String toString() {
      return this.getText();
   }
}
