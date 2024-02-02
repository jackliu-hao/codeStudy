package cn.hutool.extra.tokenizer.engine.jieba;

import cn.hutool.extra.tokenizer.Word;
import com.huaban.analysis.jieba.SegToken;

public class JiebaWord implements Word {
   private static final long serialVersionUID = 1L;
   private final SegToken segToken;

   public JiebaWord(SegToken segToken) {
      this.segToken = segToken;
   }

   public String getText() {
      return this.segToken.word;
   }

   public int getStartOffset() {
      return this.segToken.startOffset;
   }

   public int getEndOffset() {
      return this.segToken.endOffset;
   }

   public String toString() {
      return this.getText();
   }
}
