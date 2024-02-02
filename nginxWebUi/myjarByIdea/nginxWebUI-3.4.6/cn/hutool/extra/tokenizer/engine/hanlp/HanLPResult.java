package cn.hutool.extra.tokenizer.engine.hanlp;

import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.Word;
import com.hankcs.hanlp.seg.common.Term;
import java.util.Iterator;
import java.util.List;

public class HanLPResult implements Result {
   Iterator<Term> result;

   public HanLPResult(List<Term> termList) {
      this.result = termList.iterator();
   }

   public boolean hasNext() {
      return this.result.hasNext();
   }

   public Word next() {
      return new HanLPWord((Term)this.result.next());
   }

   public void remove() {
      this.result.remove();
   }
}
