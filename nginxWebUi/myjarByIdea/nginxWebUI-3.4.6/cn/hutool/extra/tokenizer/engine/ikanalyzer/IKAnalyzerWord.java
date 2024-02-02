package cn.hutool.extra.tokenizer.engine.ikanalyzer;

import cn.hutool.extra.tokenizer.Word;
import org.wltea.analyzer.core.Lexeme;

public class IKAnalyzerWord implements Word {
   private static final long serialVersionUID = 1L;
   private final Lexeme word;

   public IKAnalyzerWord(Lexeme word) {
      this.word = word;
   }

   public String getText() {
      return this.word.getLexemeText();
   }

   public int getStartOffset() {
      return this.word.getBeginPosition();
   }

   public int getEndOffset() {
      return this.word.getEndPosition();
   }

   public String toString() {
      return this.getText();
   }
}
