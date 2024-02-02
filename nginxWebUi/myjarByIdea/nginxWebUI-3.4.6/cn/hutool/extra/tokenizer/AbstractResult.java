package cn.hutool.extra.tokenizer;

import cn.hutool.core.collection.ComputeIter;

public abstract class AbstractResult extends ComputeIter<Word> implements Result {
   protected abstract Word nextWord();

   protected Word computeNext() {
      return this.nextWord();
   }
}
