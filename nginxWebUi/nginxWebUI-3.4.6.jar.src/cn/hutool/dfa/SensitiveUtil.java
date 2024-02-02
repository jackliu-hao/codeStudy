/*     */ package cn.hutool.dfa;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.thread.ThreadUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.json.JSONUtil;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SensitiveUtil
/*     */ {
/*     */   public static final char DEFAULT_SEPARATOR = ',';
/*  22 */   private static final WordTree sensitiveTree = new WordTree();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isInited() {
/*  28 */     return (false == sensitiveTree.isEmpty());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void init(Collection<String> sensitiveWords, boolean isAsync) {
/*  38 */     if (isAsync) {
/*  39 */       ThreadUtil.execAsync(() -> {
/*     */             init(sensitiveWords);
/*     */             return Boolean.valueOf(true);
/*     */           });
/*     */     } else {
/*  44 */       init(sensitiveWords);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void init(Collection<String> sensitiveWords) {
/*  54 */     sensitiveTree.clear();
/*  55 */     sensitiveTree.addWords(sensitiveWords);
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
/*     */   public static void init(String sensitiveWords, char separator, boolean isAsync) {
/*  67 */     if (StrUtil.isNotBlank(sensitiveWords)) {
/*  68 */       init(StrUtil.split(sensitiveWords, separator), isAsync);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void init(String sensitiveWords, boolean isAsync) {
/*  79 */     init(sensitiveWords, ',', isAsync);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setCharFilter(Filter<Character> charFilter) {
/*  90 */     if (charFilter != null) {
/*  91 */       sensitiveTree.setCharFilter(charFilter);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean containsSensitive(String text) {
/* 102 */     return sensitiveTree.isMatch(text);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean containsSensitive(Object obj) {
/* 112 */     return sensitiveTree.isMatch(JSONUtil.toJsonStr(obj));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FoundWord getFoundFirstSensitive(String text) {
/* 123 */     return sensitiveTree.matchWord(text);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FoundWord getFoundFirstSensitive(Object obj) {
/* 133 */     return sensitiveTree.matchWord(JSONUtil.toJsonStr(obj));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<FoundWord> getFoundAllSensitive(String text) {
/* 144 */     return sensitiveTree.matchAllWords(text);
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
/*     */   public static List<FoundWord> getFoundAllSensitive(String text, boolean isDensityMatch, boolean isGreedMatch) {
/* 158 */     return sensitiveTree.matchAllWords(text, -1, isDensityMatch, isGreedMatch);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<FoundWord> getFoundAllSensitive(Object bean) {
/* 169 */     return sensitiveTree.matchAllWords(JSONUtil.toJsonStr(bean));
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
/*     */   public static List<FoundWord> getFoundAllSensitive(Object bean, boolean isDensityMatch, boolean isGreedMatch) {
/* 184 */     return getFoundAllSensitive(JSONUtil.toJsonStr(bean), isDensityMatch, isGreedMatch);
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
/*     */   public static <T> T sensitiveFilter(T bean, boolean isGreedMatch, SensitiveProcessor sensitiveProcessor) {
/* 197 */     String jsonText = JSONUtil.toJsonStr(bean);
/* 198 */     Class<T> c = (Class)bean.getClass();
/* 199 */     return (T)JSONUtil.toBean(sensitiveFilter(jsonText, isGreedMatch, sensitiveProcessor), c);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sensitiveFilter(String text) {
/* 210 */     return sensitiveFilter(text, true, (SensitiveProcessor)null);
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
/*     */   public static String sensitiveFilter(String text, boolean isGreedMatch, SensitiveProcessor sensitiveProcessor) {
/* 222 */     if (StrUtil.isEmpty(text)) {
/* 223 */       return text;
/*     */     }
/*     */ 
/*     */     
/* 227 */     List<FoundWord> foundWordList = getFoundAllSensitive(text, true, isGreedMatch);
/* 228 */     if (CollUtil.isEmpty(foundWordList)) {
/* 229 */       return text;
/*     */     }
/* 231 */     sensitiveProcessor = (sensitiveProcessor == null) ? new SensitiveProcessor() {  } : sensitiveProcessor;
/*     */ 
/*     */     
/* 234 */     Map<Integer, FoundWord> foundWordMap = new HashMap<>(foundWordList.size(), 1.0F);
/* 235 */     foundWordList.forEach(foundWord -> (FoundWord)foundWordMap.put(foundWord.getStartIndex(), foundWord));
/* 236 */     int length = text.length();
/* 237 */     StringBuilder textStringBuilder = new StringBuilder();
/* 238 */     for (int i = 0; i < length; i++) {
/* 239 */       FoundWord fw = foundWordMap.get(Integer.valueOf(i));
/* 240 */       if (fw != null) {
/* 241 */         textStringBuilder.append(sensitiveProcessor.process(fw));
/* 242 */         i = ((Integer)fw.getEndIndex()).intValue();
/*     */       } else {
/* 244 */         textStringBuilder.append(text.charAt(i));
/*     */       } 
/*     */     } 
/* 247 */     return textStringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\dfa\SensitiveUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */