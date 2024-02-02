/*     */ package cn.hutool.dfa;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WordTree
/*     */   extends HashMap<Character, WordTree>
/*     */ {
/*     */   private static final long serialVersionUID = -4646423269465809276L;
/*  35 */   private final Set<Character> endCharacterSet = new HashSet<>();
/*     */ 
/*     */ 
/*     */   
/*  39 */   private Filter<Character> charFilter = StopChar::isNotStopChar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WordTree setCharFilter(Filter<Character> charFilter) {
/*  59 */     this.charFilter = charFilter;
/*  60 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WordTree addWords(Collection<String> words) {
/*  72 */     if (false == words instanceof Set) {
/*  73 */       words = new HashSet<>(words);
/*     */     }
/*  75 */     for (String word : words) {
/*  76 */       addWord(word);
/*     */     }
/*  78 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WordTree addWords(String... words) {
/*  88 */     for (String word : CollUtil.newHashSet((Object[])words)) {
/*  89 */       addWord(word);
/*     */     }
/*  91 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WordTree addWord(String word) {
/* 101 */     Filter<Character> charFilter = this.charFilter;
/* 102 */     WordTree parent = null;
/* 103 */     WordTree current = this;
/*     */     
/* 105 */     char currentChar = Character.MIN_VALUE;
/* 106 */     int length = word.length();
/* 107 */     for (int i = 0; i < length; i++) {
/* 108 */       currentChar = word.charAt(i);
/* 109 */       if (charFilter.accept(Character.valueOf(currentChar))) {
/* 110 */         WordTree child = current.get(Character.valueOf(currentChar));
/* 111 */         if (child == null) {
/*     */           
/* 113 */           child = new WordTree();
/* 114 */           current.put(Character.valueOf(currentChar), child);
/*     */         } 
/* 116 */         parent = current;
/* 117 */         current = child;
/*     */       } 
/*     */     } 
/* 120 */     if (null != parent) {
/* 121 */       parent.setEnd(Character.valueOf(currentChar));
/*     */     }
/* 123 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMatch(String text) {
/* 134 */     if (null == text) {
/* 135 */       return false;
/*     */     }
/* 137 */     return (null != matchWord(text));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String match(String text) {
/* 147 */     FoundWord foundWord = matchWord(text);
/* 148 */     return (null != foundWord) ? foundWord.toString() : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FoundWord matchWord(String text) {
/* 159 */     if (null == text) {
/* 160 */       return null;
/*     */     }
/* 162 */     List<FoundWord> matchAll = matchAllWords(text, 1);
/* 163 */     return (FoundWord)CollUtil.get(matchAll, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> matchAll(String text) {
/* 175 */     return matchAll(text, -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<FoundWord> matchAllWords(String text) {
/* 186 */     return matchAllWords(text, -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> matchAll(String text, int limit) {
/* 197 */     return matchAll(text, limit, false, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<FoundWord> matchAllWords(String text, int limit) {
/* 209 */     return matchAllWords(text, limit, false, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> matchAll(String text, int limit, boolean isDensityMatch, boolean isGreedMatch) {
/* 224 */     List<FoundWord> matchAllWords = matchAllWords(text, limit, isDensityMatch, isGreedMatch);
/* 225 */     return CollUtil.map(matchAllWords, FoundWord::toString, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<FoundWord> matchAllWords(String text, int limit, boolean isDensityMatch, boolean isGreedMatch) {
/* 241 */     if (null == text) {
/* 242 */       return null;
/*     */     }
/*     */     
/* 245 */     List<FoundWord> foundWords = new ArrayList<>();
/* 246 */     WordTree current = this;
/* 247 */     int length = text.length();
/* 248 */     Filter<Character> charFilter = this.charFilter;
/*     */     
/* 250 */     StringBuilder wordBuffer = StrUtil.builder();
/* 251 */     StringBuilder keyBuffer = StrUtil.builder();
/*     */     
/* 253 */     for (int i = 0; i < length; i++) {
/* 254 */       wordBuffer.setLength(0);
/* 255 */       keyBuffer.setLength(0);
/* 256 */       for (int j = i; j < length; j++) {
/* 257 */         char currentChar = text.charAt(j);
/*     */         
/* 259 */         if (false == charFilter.accept(Character.valueOf(currentChar))) {
/* 260 */           if (wordBuffer.length() > 0) {
/*     */             
/* 262 */             wordBuffer.append(currentChar);
/*     */           } else {
/*     */             
/* 265 */             i++;
/*     */           } 
/*     */         } else {
/* 268 */           if (false == current.containsKey(Character.valueOf(currentChar))) {
/*     */             break;
/*     */           }
/*     */           
/* 272 */           wordBuffer.append(currentChar);
/* 273 */           keyBuffer.append(currentChar);
/* 274 */           if (current.isEnd(Character.valueOf(currentChar))) {
/*     */             
/* 276 */             foundWords.add(new FoundWord(keyBuffer.toString(), wordBuffer.toString(), i, j));
/* 277 */             if (limit > 0 && foundWords.size() >= limit)
/*     */             {
/* 279 */               return foundWords;
/*     */             }
/* 281 */             if (false == isDensityMatch) {
/*     */               
/* 283 */               i = j;
/*     */               break;
/*     */             } 
/* 286 */             if (false == isGreedMatch) {
/*     */               break;
/*     */             }
/*     */           } 
/*     */           
/* 291 */           current = current.get(Character.valueOf(currentChar));
/* 292 */           if (null == current)
/*     */             break; 
/*     */         } 
/*     */       } 
/* 296 */       current = this;
/*     */     } 
/* 298 */     return foundWords;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isEnd(Character c) {
/* 309 */     return this.endCharacterSet.contains(c);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setEnd(Character c) {
/* 318 */     if (null != c) {
/* 319 */       this.endCharacterSet.add(c);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 330 */     super.clear();
/* 331 */     this.endCharacterSet.clear();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\dfa\WordTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */