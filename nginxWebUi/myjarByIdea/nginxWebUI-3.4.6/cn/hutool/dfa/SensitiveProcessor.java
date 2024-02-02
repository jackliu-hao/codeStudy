package cn.hutool.dfa;

public interface SensitiveProcessor {
   default String process(FoundWord foundWord) {
      int length = foundWord.getFoundWord().length();
      StringBuilder sb = new StringBuilder(length);

      for(int i = 0; i < length; ++i) {
         sb.append("*");
      }

      return sb.toString();
   }
}
