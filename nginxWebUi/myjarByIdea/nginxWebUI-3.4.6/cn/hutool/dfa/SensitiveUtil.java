package cn.hutool.dfa;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SensitiveUtil {
   public static final char DEFAULT_SEPARATOR = ',';
   private static final WordTree sensitiveTree = new WordTree();

   public static boolean isInited() {
      return !sensitiveTree.isEmpty();
   }

   public static void init(Collection<String> sensitiveWords, boolean isAsync) {
      if (isAsync) {
         ThreadUtil.execAsync(() -> {
            init(sensitiveWords);
            return true;
         });
      } else {
         init(sensitiveWords);
      }

   }

   public static void init(Collection<String> sensitiveWords) {
      sensitiveTree.clear();
      sensitiveTree.addWords(sensitiveWords);
   }

   public static void init(String sensitiveWords, char separator, boolean isAsync) {
      if (StrUtil.isNotBlank(sensitiveWords)) {
         init((Collection)StrUtil.split(sensitiveWords, separator), isAsync);
      }

   }

   public static void init(String sensitiveWords, boolean isAsync) {
      init(sensitiveWords, ',', isAsync);
   }

   public static void setCharFilter(Filter<Character> charFilter) {
      if (charFilter != null) {
         sensitiveTree.setCharFilter(charFilter);
      }

   }

   public static boolean containsSensitive(String text) {
      return sensitiveTree.isMatch(text);
   }

   public static boolean containsSensitive(Object obj) {
      return sensitiveTree.isMatch(JSONUtil.toJsonStr(obj));
   }

   public static FoundWord getFoundFirstSensitive(String text) {
      return sensitiveTree.matchWord(text);
   }

   public static FoundWord getFoundFirstSensitive(Object obj) {
      return sensitiveTree.matchWord(JSONUtil.toJsonStr(obj));
   }

   public static List<FoundWord> getFoundAllSensitive(String text) {
      return sensitiveTree.matchAllWords(text);
   }

   public static List<FoundWord> getFoundAllSensitive(String text, boolean isDensityMatch, boolean isGreedMatch) {
      return sensitiveTree.matchAllWords(text, -1, isDensityMatch, isGreedMatch);
   }

   public static List<FoundWord> getFoundAllSensitive(Object bean) {
      return sensitiveTree.matchAllWords(JSONUtil.toJsonStr(bean));
   }

   public static List<FoundWord> getFoundAllSensitive(Object bean, boolean isDensityMatch, boolean isGreedMatch) {
      return getFoundAllSensitive(JSONUtil.toJsonStr(bean), isDensityMatch, isGreedMatch);
   }

   public static <T> T sensitiveFilter(T bean, boolean isGreedMatch, SensitiveProcessor sensitiveProcessor) {
      String jsonText = JSONUtil.toJsonStr(bean);
      Class<T> c = bean.getClass();
      return JSONUtil.toBean(sensitiveFilter(jsonText, isGreedMatch, sensitiveProcessor), c);
   }

   public static String sensitiveFilter(String text) {
      return sensitiveFilter((String)text, true, (SensitiveProcessor)null);
   }

   public static String sensitiveFilter(String text, boolean isGreedMatch, SensitiveProcessor sensitiveProcessor) {
      if (StrUtil.isEmpty(text)) {
         return text;
      } else {
         List<FoundWord> foundWordList = getFoundAllSensitive(text, true, isGreedMatch);
         if (CollUtil.isEmpty((Collection)foundWordList)) {
            return text;
         } else {
            sensitiveProcessor = sensitiveProcessor == null ? new SensitiveProcessor() {
            } : sensitiveProcessor;
            Map<Integer, FoundWord> foundWordMap = new HashMap(foundWordList.size(), 1.0F);
            foundWordList.forEach((foundWord) -> {
               FoundWord var10000 = (FoundWord)foundWordMap.put(foundWord.getStartIndex(), foundWord);
            });
            int length = text.length();
            StringBuilder textStringBuilder = new StringBuilder();

            for(int i = 0; i < length; ++i) {
               FoundWord fw = (FoundWord)foundWordMap.get(i);
               if (fw != null) {
                  textStringBuilder.append(sensitiveProcessor.process(fw));
                  i = (Integer)fw.getEndIndex();
               } else {
                  textStringBuilder.append(text.charAt(i));
               }
            }

            return textStringBuilder.toString();
         }
      }
   }
}
