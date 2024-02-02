package cn.hutool.dfa;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class WordTree extends HashMap<Character, WordTree> {
   private static final long serialVersionUID = -4646423269465809276L;
   private final Set<Character> endCharacterSet = new HashSet();
   private Filter<Character> charFilter = StopChar::isNotStopChar;

   public WordTree setCharFilter(Filter<Character> charFilter) {
      this.charFilter = charFilter;
      return this;
   }

   public WordTree addWords(Collection<String> words) {
      if (!(words instanceof Set)) {
         words = new HashSet((Collection)words);
      }

      Iterator var2 = ((Collection)words).iterator();

      while(var2.hasNext()) {
         String word = (String)var2.next();
         this.addWord(word);
      }

      return this;
   }

   public WordTree addWords(String... words) {
      Iterator var2 = CollUtil.newHashSet((Object[])words).iterator();

      while(var2.hasNext()) {
         String word = (String)var2.next();
         this.addWord(word);
      }

      return this;
   }

   public WordTree addWord(String word) {
      Filter<Character> charFilter = this.charFilter;
      WordTree parent = null;
      WordTree current = this;
      char currentChar = 0;
      int length = word.length();

      for(int i = 0; i < length; ++i) {
         currentChar = word.charAt(i);
         if (charFilter.accept(currentChar)) {
            WordTree child = (WordTree)current.get(currentChar);
            if (child == null) {
               child = new WordTree();
               current.put(currentChar, child);
            }

            parent = current;
            current = child;
         }
      }

      if (null != parent) {
         parent.setEnd(currentChar);
      }

      return this;
   }

   public boolean isMatch(String text) {
      if (null == text) {
         return false;
      } else {
         return null != this.matchWord(text);
      }
   }

   public String match(String text) {
      FoundWord foundWord = this.matchWord(text);
      return null != foundWord ? foundWord.toString() : null;
   }

   public FoundWord matchWord(String text) {
      if (null == text) {
         return null;
      } else {
         List<FoundWord> matchAll = this.matchAllWords(text, 1);
         return (FoundWord)CollUtil.get(matchAll, 0);
      }
   }

   public List<String> matchAll(String text) {
      return this.matchAll(text, -1);
   }

   public List<FoundWord> matchAllWords(String text) {
      return this.matchAllWords(text, -1);
   }

   public List<String> matchAll(String text, int limit) {
      return this.matchAll(text, limit, false, false);
   }

   public List<FoundWord> matchAllWords(String text, int limit) {
      return this.matchAllWords(text, limit, false, false);
   }

   public List<String> matchAll(String text, int limit, boolean isDensityMatch, boolean isGreedMatch) {
      List<FoundWord> matchAllWords = this.matchAllWords(text, limit, isDensityMatch, isGreedMatch);
      return CollUtil.map(matchAllWords, FoundWord::toString, true);
   }

   public List<FoundWord> matchAllWords(String text, int limit, boolean isDensityMatch, boolean isGreedMatch) {
      if (null == text) {
         return null;
      } else {
         List<FoundWord> foundWords = new ArrayList();
         WordTree current = this;
         int length = text.length();
         Filter<Character> charFilter = this.charFilter;
         StringBuilder wordBuffer = StrUtil.builder();
         StringBuilder keyBuffer = StrUtil.builder();

         for(int i = 0; i < length; ++i) {
            wordBuffer.setLength(0);
            keyBuffer.setLength(0);

            for(int j = i; j < length; ++j) {
               char currentChar = text.charAt(j);
               if (!charFilter.accept(currentChar)) {
                  if (wordBuffer.length() > 0) {
                     wordBuffer.append(currentChar);
                  } else {
                     ++i;
                  }
               } else {
                  if (!current.containsKey(currentChar)) {
                     break;
                  }

                  wordBuffer.append(currentChar);
                  keyBuffer.append(currentChar);
                  if (current.isEnd(currentChar)) {
                     foundWords.add(new FoundWord(keyBuffer.toString(), wordBuffer.toString(), i, j));
                     if (limit > 0 && foundWords.size() >= limit) {
                        return foundWords;
                     }

                     if (!isDensityMatch) {
                        i = j;
                        break;
                     }

                     if (!isGreedMatch) {
                        break;
                     }
                  }

                  current = (WordTree)current.get(currentChar);
                  if (null == current) {
                     break;
                  }
               }
            }

            current = this;
         }

         return foundWords;
      }
   }

   private boolean isEnd(Character c) {
      return this.endCharacterSet.contains(c);
   }

   private void setEnd(Character c) {
      if (null != c) {
         this.endCharacterSet.add(c);
      }

   }

   public void clear() {
      super.clear();
      this.endCharacterSet.clear();
   }
}
