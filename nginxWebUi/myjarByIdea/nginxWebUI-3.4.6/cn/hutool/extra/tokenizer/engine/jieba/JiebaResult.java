package cn.hutool.extra.tokenizer.engine.jieba;

import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.Word;
import com.huaban.analysis.jieba.SegToken;
import java.util.Iterator;
import java.util.List;

public class JiebaResult implements Result {
   Iterator<SegToken> result;

   public JiebaResult(List<SegToken> segTokenList) {
      this.result = segTokenList.iterator();
   }

   public boolean hasNext() {
      return this.result.hasNext();
   }

   public Word next() {
      return new JiebaWord((SegToken)this.result.next());
   }

   public void remove() {
      this.result.remove();
   }
}
