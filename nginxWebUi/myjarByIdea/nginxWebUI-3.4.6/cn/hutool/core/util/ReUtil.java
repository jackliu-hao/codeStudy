package cn.hutool.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.mutable.Mutable;
import cn.hutool.core.lang.mutable.MutableObj;
import cn.hutool.core.map.MapUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReUtil {
   public static final String RE_CHINESE = "[⺀-\u2eff⼀-\u2fdf㇀-\u31ef㐀-䶿一-\u9fff豈-\ufaff\ud840\udc00-\ud869\udedf\ud869\udf00-\ud86d\udf3f\ud86d\udf40-\ud86e\udc1f\ud86e\udc20-\ud873\udeaf\ud87e\udc00-\ud87e\ude1f]";
   public static final String RE_CHINESES = "[⺀-\u2eff⼀-\u2fdf㇀-\u31ef㐀-䶿一-\u9fff豈-\ufaff\ud840\udc00-\ud869\udedf\ud869\udf00-\ud86d\udf3f\ud86d\udf40-\ud86e\udc1f\ud86e\udc20-\ud873\udeaf\ud87e\udc00-\ud87e\ude1f]+";
   public static final Set<Character> RE_KEYS = CollUtil.newHashSet((Object[])('$', '(', ')', '*', '+', '.', '[', ']', '?', '\\', '^', '{', '}', '|'));

   public static String getGroup0(String regex, CharSequence content) {
      return get((String)regex, content, 0);
   }

   public static String getGroup1(String regex, CharSequence content) {
      return get((String)regex, content, 1);
   }

   public static String get(String regex, CharSequence content, int groupIndex) {
      if (null != content && null != regex) {
         Pattern pattern = PatternPool.get(regex, 32);
         return get(pattern, content, groupIndex);
      } else {
         return null;
      }
   }

   public static String get(String regex, CharSequence content, String groupName) {
      if (null != content && null != regex) {
         Pattern pattern = PatternPool.get(regex, 32);
         return get(pattern, content, groupName);
      } else {
         return null;
      }
   }

   public static String getGroup0(Pattern pattern, CharSequence content) {
      return get((Pattern)pattern, content, 0);
   }

   public static String getGroup1(Pattern pattern, CharSequence content) {
      return get((Pattern)pattern, content, 1);
   }

   public static String get(Pattern pattern, CharSequence content, int groupIndex) {
      if (null != content && null != pattern) {
         MutableObj<String> result = new MutableObj();
         get(pattern, content, (matcher) -> {
            result.set(matcher.group(groupIndex));
         });
         return (String)result.get();
      } else {
         return null;
      }
   }

   public static String get(Pattern pattern, CharSequence content, String groupName) {
      if (null != content && null != pattern && null != groupName) {
         MutableObj<String> result = new MutableObj();
         get(pattern, content, (matcher) -> {
            result.set(matcher.group(groupName));
         });
         return (String)result.get();
      } else {
         return null;
      }
   }

   public static void get(Pattern pattern, CharSequence content, Consumer<Matcher> consumer) {
      if (null != content && null != pattern && null != consumer) {
         Matcher m = pattern.matcher(content);
         if (m.find()) {
            consumer.accept(m);
         }

      }
   }

   public static List<String> getAllGroups(Pattern pattern, CharSequence content) {
      return getAllGroups(pattern, content, true);
   }

   public static List<String> getAllGroups(Pattern pattern, CharSequence content, boolean withGroup0) {
      if (null != content && null != pattern) {
         ArrayList<String> result = new ArrayList();
         Matcher matcher = pattern.matcher(content);
         if (matcher.find()) {
            int startGroup = withGroup0 ? 0 : 1;
            int groupCount = matcher.groupCount();

            for(int i = startGroup; i <= groupCount; ++i) {
               result.add(matcher.group(i));
            }
         }

         return result;
      } else {
         return null;
      }
   }

   public static Map<String, String> getAllGroupNames(Pattern pattern, CharSequence content) {
      if (null != content && null != pattern) {
         Matcher m = pattern.matcher(content);
         Map<String, String> result = MapUtil.newHashMap(m.groupCount());
         if (m.find()) {
            Map<String, Integer> map = (Map)ReflectUtil.invoke(pattern, (String)"namedGroups");
            map.forEach((key, value) -> {
               String var10000 = (String)result.put(key, m.group(value));
            });
         }

         return result;
      } else {
         return null;
      }
   }

   public static String extractMulti(Pattern pattern, CharSequence content, String template) {
      if (null != content && null != pattern && null != template) {
         TreeSet<Integer> varNums = new TreeSet((o1, o2) -> {
            return ObjectUtil.compare(o2, o1);
         });
         Matcher matcherForTemplate = PatternPool.GROUP_VAR.matcher(template);

         while(matcherForTemplate.find()) {
            varNums.add(Integer.parseInt(matcherForTemplate.group(1)));
         }

         Matcher matcher = pattern.matcher(content);
         if (!matcher.find()) {
            return null;
         } else {
            Integer group;
            for(Iterator var6 = varNums.iterator(); var6.hasNext(); template = template.replace("$" + group, matcher.group(group))) {
               group = (Integer)var6.next();
            }

            return template;
         }
      } else {
         return null;
      }
   }

   public static String extractMulti(String regex, CharSequence content, String template) {
      if (null != content && null != regex && null != template) {
         Pattern pattern = PatternPool.get(regex, 32);
         return extractMulti(pattern, content, template);
      } else {
         return null;
      }
   }

   public static String extractMultiAndDelPre(Pattern pattern, Mutable<CharSequence> contentHolder, String template) {
      if (null != contentHolder && null != pattern && null != template) {
         HashSet<String> varNums = (HashSet)findAll((Pattern)PatternPool.GROUP_VAR, template, 1, new HashSet());
         CharSequence content = (CharSequence)contentHolder.get();
         Matcher matcher = pattern.matcher(content);
         if (!matcher.find()) {
            return null;
         } else {
            String var;
            int group;
            for(Iterator var6 = varNums.iterator(); var6.hasNext(); template = template.replace("$" + var, matcher.group(group))) {
               var = (String)var6.next();
               group = Integer.parseInt(var);
            }

            contentHolder.set(StrUtil.sub(content, matcher.end(), content.length()));
            return template;
         }
      } else {
         return null;
      }
   }

   public static String extractMultiAndDelPre(String regex, Mutable<CharSequence> contentHolder, String template) {
      if (null != contentHolder && null != regex && null != template) {
         Pattern pattern = PatternPool.get(regex, 32);
         return extractMultiAndDelPre(pattern, contentHolder, template);
      } else {
         return null;
      }
   }

   public static String delFirst(String regex, CharSequence content) {
      if (StrUtil.hasBlank(new CharSequence[]{regex, content})) {
         return StrUtil.str(content);
      } else {
         Pattern pattern = PatternPool.get(regex, 32);
         return delFirst(pattern, content);
      }
   }

   public static String delFirst(Pattern pattern, CharSequence content) {
      return replaceFirst(pattern, content, "");
   }

   public static String replaceFirst(Pattern pattern, CharSequence content, String replacement) {
      return null != pattern && !StrUtil.isEmpty(content) ? pattern.matcher(content).replaceFirst(replacement) : StrUtil.str(content);
   }

   public static String delLast(String regex, CharSequence str) {
      if (StrUtil.hasBlank(new CharSequence[]{regex, str})) {
         return StrUtil.str(str);
      } else {
         Pattern pattern = PatternPool.get(regex, 32);
         return delLast(pattern, str);
      }
   }

   public static String delLast(Pattern pattern, CharSequence str) {
      if (null != pattern && StrUtil.isNotEmpty(str)) {
         MatchResult matchResult = lastIndexOf(pattern, str);
         if (null != matchResult) {
            return StrUtil.subPre(str, matchResult.start()) + StrUtil.subSuf(str, matchResult.end());
         }
      }

      return StrUtil.str(str);
   }

   public static String delAll(String regex, CharSequence content) {
      if (StrUtil.hasBlank(new CharSequence[]{regex, content})) {
         return StrUtil.str(content);
      } else {
         Pattern pattern = PatternPool.get(regex, 32);
         return delAll(pattern, content);
      }
   }

   public static String delAll(Pattern pattern, CharSequence content) {
      return null != pattern && !StrUtil.isBlank(content) ? pattern.matcher(content).replaceAll("") : StrUtil.str(content);
   }

   public static String delPre(String regex, CharSequence content) {
      if (null != content && null != regex) {
         Pattern pattern = PatternPool.get(regex, 32);
         return delPre(pattern, content);
      } else {
         return StrUtil.str(content);
      }
   }

   public static String delPre(Pattern pattern, CharSequence content) {
      if (null != content && null != pattern) {
         Matcher matcher = pattern.matcher(content);
         return matcher.find() ? StrUtil.sub(content, matcher.end(), content.length()) : StrUtil.str(content);
      } else {
         return StrUtil.str(content);
      }
   }

   public static List<String> findAllGroup0(String regex, CharSequence content) {
      return findAll((String)regex, content, 0);
   }

   public static List<String> findAllGroup1(String regex, CharSequence content) {
      return findAll((String)regex, content, 1);
   }

   public static List<String> findAll(String regex, CharSequence content, int group) {
      return (List)findAll((String)regex, content, group, new ArrayList());
   }

   public static <T extends Collection<String>> T findAll(String regex, CharSequence content, int group, T collection) {
      return null == regex ? collection : findAll(PatternPool.get(regex, 32), content, group, collection);
   }

   public static List<String> findAllGroup0(Pattern pattern, CharSequence content) {
      return findAll((Pattern)pattern, content, 0);
   }

   public static List<String> findAllGroup1(Pattern pattern, CharSequence content) {
      return findAll((Pattern)pattern, content, 1);
   }

   public static List<String> findAll(Pattern pattern, CharSequence content, int group) {
      return (List)findAll((Pattern)pattern, content, group, new ArrayList());
   }

   public static <T extends Collection<String>> T findAll(Pattern pattern, CharSequence content, int group, T collection) {
      if (null != pattern && null != content) {
         Assert.notNull(collection, "Collection must be not null !");
         findAll(pattern, content, (matcher) -> {
            collection.add(matcher.group(group));
         });
         return collection;
      } else {
         return null;
      }
   }

   public static void findAll(Pattern pattern, CharSequence content, Consumer<Matcher> consumer) {
      if (null != pattern && null != content) {
         Matcher matcher = pattern.matcher(content);

         while(matcher.find()) {
            consumer.accept(matcher);
         }

      }
   }

   public static int count(String regex, CharSequence content) {
      if (null != regex && null != content) {
         Pattern pattern = PatternPool.get(regex, 32);
         return count(pattern, content);
      } else {
         return 0;
      }
   }

   public static int count(Pattern pattern, CharSequence content) {
      if (null != pattern && null != content) {
         int count = 0;

         for(Matcher matcher = pattern.matcher(content); matcher.find(); ++count) {
         }

         return count;
      } else {
         return 0;
      }
   }

   public static boolean contains(String regex, CharSequence content) {
      if (null != regex && null != content) {
         Pattern pattern = PatternPool.get(regex, 32);
         return contains(pattern, content);
      } else {
         return false;
      }
   }

   public static boolean contains(Pattern pattern, CharSequence content) {
      return null != pattern && null != content ? pattern.matcher(content).find() : false;
   }

   public static MatchResult indexOf(String regex, CharSequence content) {
      if (null != regex && null != content) {
         Pattern pattern = PatternPool.get(regex, 32);
         return indexOf(pattern, content);
      } else {
         return null;
      }
   }

   public static MatchResult indexOf(Pattern pattern, CharSequence content) {
      if (null != pattern && null != content) {
         Matcher matcher = pattern.matcher(content);
         if (matcher.find()) {
            return matcher.toMatchResult();
         }
      }

      return null;
   }

   public static MatchResult lastIndexOf(String regex, CharSequence content) {
      if (null != regex && null != content) {
         Pattern pattern = PatternPool.get(regex, 32);
         return lastIndexOf(pattern, content);
      } else {
         return null;
      }
   }

   public static MatchResult lastIndexOf(Pattern pattern, CharSequence content) {
      MatchResult result = null;
      if (null != pattern && null != content) {
         for(Matcher matcher = pattern.matcher(content); matcher.find(); result = matcher.toMatchResult()) {
         }
      }

      return result;
   }

   public static Integer getFirstNumber(CharSequence StringWithNumber) {
      return Convert.toInt(get((Pattern)PatternPool.NUMBERS, StringWithNumber, 0), (Integer)null);
   }

   public static boolean isMatch(String regex, CharSequence content) {
      if (content == null) {
         return false;
      } else if (StrUtil.isEmpty(regex)) {
         return true;
      } else {
         Pattern pattern = PatternPool.get(regex, 32);
         return isMatch(pattern, content);
      }
   }

   public static boolean isMatch(Pattern pattern, CharSequence content) {
      return content != null && pattern != null ? pattern.matcher(content).matches() : false;
   }

   public static String replaceAll(CharSequence content, String regex, String replacementTemplate) {
      Pattern pattern = Pattern.compile(regex, 32);
      return replaceAll(content, pattern, replacementTemplate);
   }

   public static String replaceAll(CharSequence content, Pattern pattern, String replacementTemplate) {
      if (StrUtil.isEmpty(content)) {
         return StrUtil.str(content);
      } else {
         Matcher matcher = pattern.matcher(content);
         boolean result = matcher.find();
         if (!result) {
            return StrUtil.str(content);
         } else {
            Set<String> varNums = (Set)findAll((Pattern)PatternPool.GROUP_VAR, replacementTemplate, 1, new HashSet());
            StringBuffer sb = new StringBuffer();

            do {
               String replacement = replacementTemplate;

               String var;
               int group;
               for(Iterator var8 = varNums.iterator(); var8.hasNext(); replacement = replacement.replace("$" + var, matcher.group(group))) {
                  var = (String)var8.next();
                  group = Integer.parseInt(var);
               }

               matcher.appendReplacement(sb, escape(replacement));
               result = matcher.find();
            } while(result);

            matcher.appendTail(sb);
            return sb.toString();
         }
      }
   }

   public static String replaceAll(CharSequence str, String regex, Func1<Matcher, String> replaceFun) {
      return replaceAll(str, Pattern.compile(regex), replaceFun);
   }

   public static String replaceAll(CharSequence str, Pattern pattern, Func1<Matcher, String> replaceFun) {
      if (StrUtil.isEmpty(str)) {
         return StrUtil.str(str);
      } else {
         Matcher matcher = pattern.matcher(str);
         StringBuffer buffer = new StringBuffer();

         while(matcher.find()) {
            try {
               matcher.appendReplacement(buffer, (String)replaceFun.call(matcher));
            } catch (Exception var6) {
               throw new UtilException(var6);
            }
         }

         matcher.appendTail(buffer);
         return buffer.toString();
      }
   }

   public static String escape(char c) {
      StringBuilder builder = new StringBuilder();
      if (RE_KEYS.contains(c)) {
         builder.append('\\');
      }

      builder.append(c);
      return builder.toString();
   }

   public static String escape(CharSequence content) {
      if (StrUtil.isBlank(content)) {
         return StrUtil.str(content);
      } else {
         StringBuilder builder = new StringBuilder();
         int len = content.length();

         for(int i = 0; i < len; ++i) {
            char current = content.charAt(i);
            if (RE_KEYS.contains(current)) {
               builder.append('\\');
            }

            builder.append(current);
         }

         return builder.toString();
      }
   }
}
