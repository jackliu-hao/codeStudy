package cn.hutool.dfa;

import cn.hutool.core.lang.DefaultSegment;

public class FoundWord extends DefaultSegment<Integer> {
   private final String word;
   private final String foundWord;

   public FoundWord(String word, String foundWord, int startIndex, int endIndex) {
      super(startIndex, endIndex);
      this.word = word;
      this.foundWord = foundWord;
   }

   public String getWord() {
      return this.word;
   }

   public String getFoundWord() {
      return this.foundWord;
   }

   public String toString() {
      return this.foundWord;
   }
}
