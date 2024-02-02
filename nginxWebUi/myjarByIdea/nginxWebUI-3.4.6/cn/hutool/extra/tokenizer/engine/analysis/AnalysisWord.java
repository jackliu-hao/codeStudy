package cn.hutool.extra.tokenizer.engine.analysis;

import cn.hutool.extra.tokenizer.Word;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Attribute;

public class AnalysisWord implements Word {
   private static final long serialVersionUID = 1L;
   private final Attribute word;

   public AnalysisWord(CharTermAttribute word) {
      this.word = word;
   }

   public String getText() {
      return this.word.toString();
   }

   public int getStartOffset() {
      return this.word instanceof OffsetAttribute ? ((OffsetAttribute)this.word).startOffset() : -1;
   }

   public int getEndOffset() {
      return this.word instanceof OffsetAttribute ? ((OffsetAttribute)this.word).endOffset() : -1;
   }

   public String toString() {
      return this.getText();
   }
}
