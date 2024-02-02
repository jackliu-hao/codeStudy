package cn.hutool.extra.tokenizer.engine.ansj;

import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.Word;
import java.util.Iterator;
import org.ansj.domain.Term;

public class AnsjResult implements Result {
   private final Iterator<Term> result;

   public AnsjResult(org.ansj.domain.Result ansjResult) {
      this.result = ansjResult.iterator();
   }

   public boolean hasNext() {
      return this.result.hasNext();
   }

   public Word next() {
      return new AnsjWord((Term)this.result.next());
   }

   public void remove() {
      this.result.remove();
   }

   public Iterator<Word> iterator() {
      return this;
   }
}
