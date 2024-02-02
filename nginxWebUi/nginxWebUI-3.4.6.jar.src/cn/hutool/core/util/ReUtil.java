/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.lang.PatternPool;
/*     */ import cn.hutool.core.lang.func.Func1;
/*     */ import cn.hutool.core.lang.mutable.Mutable;
/*     */ import cn.hutool.core.lang.mutable.MutableObj;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.regex.MatchResult;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ 
/*     */ public class ReUtil
/*     */ {
/*     */   public static final String RE_CHINESE = "[⺀-⻿⼀-⿟㇀-㇯㐀-䶿一-鿿豈-﫿𠀀-𪛟𪜀-𫜿𫝀-𫠟𫠠-𬺯丽-𯨟]";
/*     */   public static final String RE_CHINESES = "[⺀-⻿⼀-⿟㇀-㇯㐀-䶿一-鿿豈-﫿𠀀-𪛟𪜀-𫜿𫝀-𫠟𫠠-𬺯丽-𯨟]+";
/*  47 */   public static final Set<Character> RE_KEYS = CollUtil.newHashSet((Object[])new Character[] { Character.valueOf('$'), Character.valueOf('('), Character.valueOf(')'), Character.valueOf('*'), Character.valueOf('+'), Character.valueOf('.'), Character.valueOf('['), Character.valueOf(']'), Character.valueOf('?'), Character.valueOf('\\'), Character.valueOf('^'), Character.valueOf('{'), Character.valueOf('}'), Character.valueOf('|') });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getGroup0(String regex, CharSequence content) {
/*  58 */     return get(regex, content, 0);
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
/*     */   public static String getGroup1(String regex, CharSequence content) {
/*  70 */     return get(regex, content, 1);
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
/*     */   public static String get(String regex, CharSequence content, int groupIndex) {
/*  82 */     if (null == content || null == regex) {
/*  83 */       return null;
/*     */     }
/*     */     
/*  86 */     Pattern pattern = PatternPool.get(regex, 32);
/*  87 */     return get(pattern, content, groupIndex);
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
/*     */   public static String get(String regex, CharSequence content, String groupName) {
/*  99 */     if (null == content || null == regex) {
/* 100 */       return null;
/*     */     }
/*     */     
/* 103 */     Pattern pattern = PatternPool.get(regex, 32);
/* 104 */     return get(pattern, content, groupName);
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
/*     */   public static String getGroup0(Pattern pattern, CharSequence content) {
/* 116 */     return get(pattern, content, 0);
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
/*     */   public static String getGroup1(Pattern pattern, CharSequence content) {
/* 128 */     return get(pattern, content, 1);
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
/*     */   public static String get(Pattern pattern, CharSequence content, int groupIndex) {
/* 140 */     if (null == content || null == pattern) {
/* 141 */       return null;
/*     */     }
/*     */     
/* 144 */     MutableObj<String> result = new MutableObj();
/* 145 */     get(pattern, content, matcher -> result.set(matcher.group(groupIndex)));
/* 146 */     return (String)result.get();
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
/*     */   public static String get(Pattern pattern, CharSequence content, String groupName) {
/* 159 */     if (null == content || null == pattern || null == groupName) {
/* 160 */       return null;
/*     */     }
/*     */     
/* 163 */     MutableObj<String> result = new MutableObj();
/* 164 */     get(pattern, content, matcher -> result.set(matcher.group(groupName)));
/* 165 */     return (String)result.get();
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
/*     */   public static void get(Pattern pattern, CharSequence content, Consumer<Matcher> consumer) {
/* 178 */     if (null == content || null == pattern || null == consumer) {
/*     */       return;
/*     */     }
/* 181 */     Matcher m = pattern.matcher(content);
/* 182 */     if (m.find()) {
/* 183 */       consumer.accept(m);
/*     */     }
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
/*     */   public static List<String> getAllGroups(Pattern pattern, CharSequence content) {
/* 196 */     return getAllGroups(pattern, content, true);
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
/*     */   public static List<String> getAllGroups(Pattern pattern, CharSequence content, boolean withGroup0) {
/* 209 */     if (null == content || null == pattern) {
/* 210 */       return null;
/*     */     }
/*     */     
/* 213 */     ArrayList<String> result = new ArrayList<>();
/* 214 */     Matcher matcher = pattern.matcher(content);
/* 215 */     if (matcher.find()) {
/* 216 */       int startGroup = withGroup0 ? 0 : 1;
/* 217 */       int groupCount = matcher.groupCount();
/* 218 */       for (int i = startGroup; i <= groupCount; i++) {
/* 219 */         result.add(matcher.group(i));
/*     */       }
/*     */     } 
/* 222 */     return result;
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
/*     */   
/*     */   public static Map<String, String> getAllGroupNames(Pattern pattern, CharSequence content) {
/* 239 */     if (null == content || null == pattern) {
/* 240 */       return null;
/*     */     }
/* 242 */     Matcher m = pattern.matcher(content);
/* 243 */     Map<String, String> result = MapUtil.newHashMap(m.groupCount());
/* 244 */     if (m.find()) {
/*     */       
/* 246 */       Map<String, Integer> map = ReflectUtil.<Map<String, Integer>>invoke(pattern, "namedGroups", new Object[0]);
/* 247 */       map.forEach((key, value) -> (String)result.put(key, m.group(value.intValue())));
/*     */     } 
/* 249 */     return result;
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
/*     */   public static String extractMulti(Pattern pattern, CharSequence content, String template) {
/* 263 */     if (null == content || null == pattern || null == template) {
/* 264 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 268 */     TreeSet<Integer> varNums = new TreeSet<>((o1, o2) -> ObjectUtil.compare(o2, o1));
/* 269 */     Matcher matcherForTemplate = PatternPool.GROUP_VAR.matcher(template);
/* 270 */     while (matcherForTemplate.find()) {
/* 271 */       varNums.add(Integer.valueOf(Integer.parseInt(matcherForTemplate.group(1))));
/*     */     }
/*     */     
/* 274 */     Matcher matcher = pattern.matcher(content);
/* 275 */     if (matcher.find()) {
/* 276 */       for (Integer group : varNums) {
/* 277 */         template = template.replace("$" + group, matcher.group(group.intValue()));
/*     */       }
/* 279 */       return template;
/*     */     } 
/* 281 */     return null;
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
/*     */   public static String extractMulti(String regex, CharSequence content, String template) {
/* 296 */     if (null == content || null == regex || null == template) {
/* 297 */       return null;
/*     */     }
/*     */     
/* 300 */     Pattern pattern = PatternPool.get(regex, 32);
/* 301 */     return extractMulti(pattern, content, template);
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
/*     */   public static String extractMultiAndDelPre(Pattern pattern, Mutable<CharSequence> contentHolder, String template) {
/* 317 */     if (null == contentHolder || null == pattern || null == template) {
/* 318 */       return null;
/*     */     }
/*     */     
/* 321 */     HashSet<String> varNums = findAll(PatternPool.GROUP_VAR, template, 1, new HashSet<>());
/*     */     
/* 323 */     CharSequence content = (CharSequence)contentHolder.get();
/* 324 */     Matcher matcher = pattern.matcher(content);
/* 325 */     if (matcher.find()) {
/* 326 */       for (String var : varNums) {
/* 327 */         int group = Integer.parseInt(var);
/* 328 */         template = template.replace("$" + var, matcher.group(group));
/*     */       } 
/* 330 */       contentHolder.set(StrUtil.sub(content, matcher.end(), content.length()));
/* 331 */       return template;
/*     */     } 
/* 333 */     return null;
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
/*     */   public static String extractMultiAndDelPre(String regex, Mutable<CharSequence> contentHolder, String template) {
/* 347 */     if (null == contentHolder || null == regex || null == template) {
/* 348 */       return null;
/*     */     }
/*     */     
/* 351 */     Pattern pattern = PatternPool.get(regex, 32);
/* 352 */     return extractMultiAndDelPre(pattern, contentHolder, template);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String delFirst(String regex, CharSequence content) {
/* 363 */     if (StrUtil.hasBlank(new CharSequence[] { regex, content })) {
/* 364 */       return StrUtil.str(content);
/*     */     }
/*     */     
/* 367 */     Pattern pattern = PatternPool.get(regex, 32);
/* 368 */     return delFirst(pattern, content);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String delFirst(Pattern pattern, CharSequence content) {
/* 379 */     return replaceFirst(pattern, content, "");
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
/*     */   public static String replaceFirst(Pattern pattern, CharSequence content, String replacement) {
/* 392 */     if (null == pattern || StrUtil.isEmpty(content)) {
/* 393 */       return StrUtil.str(content);
/*     */     }
/*     */     
/* 396 */     return pattern.matcher(content).replaceFirst(replacement);
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
/*     */   public static String delLast(String regex, CharSequence str) {
/* 408 */     if (StrUtil.hasBlank(new CharSequence[] { regex, str })) {
/* 409 */       return StrUtil.str(str);
/*     */     }
/*     */     
/* 412 */     Pattern pattern = PatternPool.get(regex, 32);
/* 413 */     return delLast(pattern, str);
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
/*     */   public static String delLast(Pattern pattern, CharSequence str) {
/* 425 */     if (null != pattern && StrUtil.isNotEmpty(str)) {
/* 426 */       MatchResult matchResult = lastIndexOf(pattern, str);
/* 427 */       if (null != matchResult) {
/* 428 */         return StrUtil.subPre(str, matchResult.start()) + StrUtil.subSuf(str, matchResult.end());
/*     */       }
/*     */     } 
/*     */     
/* 432 */     return StrUtil.str(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String delAll(String regex, CharSequence content) {
/* 443 */     if (StrUtil.hasBlank(new CharSequence[] { regex, content })) {
/* 444 */       return StrUtil.str(content);
/*     */     }
/*     */     
/* 447 */     Pattern pattern = PatternPool.get(regex, 32);
/* 448 */     return delAll(pattern, content);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String delAll(Pattern pattern, CharSequence content) {
/* 459 */     if (null == pattern || StrUtil.isBlank(content)) {
/* 460 */       return StrUtil.str(content);
/*     */     }
/*     */     
/* 463 */     return pattern.matcher(content).replaceAll("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String delPre(String regex, CharSequence content) {
/* 474 */     if (null == content || null == regex) {
/* 475 */       return StrUtil.str(content);
/*     */     }
/*     */     
/* 478 */     Pattern pattern = PatternPool.get(regex, 32);
/* 479 */     return delPre(pattern, content);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String delPre(Pattern pattern, CharSequence content) {
/* 490 */     if (null == content || null == pattern) {
/* 491 */       return StrUtil.str(content);
/*     */     }
/*     */     
/* 494 */     Matcher matcher = pattern.matcher(content);
/* 495 */     if (matcher.find()) {
/* 496 */       return StrUtil.sub(content, matcher.end(), content.length());
/*     */     }
/* 498 */     return StrUtil.str(content);
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
/*     */   public static List<String> findAllGroup0(String regex, CharSequence content) {
/* 510 */     return findAll(regex, content, 0);
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
/*     */   public static List<String> findAllGroup1(String regex, CharSequence content) {
/* 522 */     return findAll(regex, content, 1);
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
/*     */   public static List<String> findAll(String regex, CharSequence content, int group) {
/* 535 */     return findAll(regex, content, group, new ArrayList<>());
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
/*     */   public static <T extends Collection<String>> T findAll(String regex, CharSequence content, int group, T collection) {
/* 549 */     if (null == regex) {
/* 550 */       return collection;
/*     */     }
/*     */     
/* 553 */     return findAll(PatternPool.get(regex, 32), content, group, collection);
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
/*     */   public static List<String> findAllGroup0(Pattern pattern, CharSequence content) {
/* 565 */     return findAll(pattern, content, 0);
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
/*     */   public static List<String> findAllGroup1(Pattern pattern, CharSequence content) {
/* 577 */     return findAll(pattern, content, 1);
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
/*     */   public static List<String> findAll(Pattern pattern, CharSequence content, int group) {
/* 590 */     return findAll(pattern, content, group, new ArrayList<>());
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
/*     */   public static <T extends Collection<String>> T findAll(Pattern pattern, CharSequence content, int group, T collection) {
/* 604 */     if (null == pattern || null == content) {
/* 605 */       return null;
/*     */     }
/* 607 */     Assert.notNull(collection, "Collection must be not null !", new Object[0]);
/*     */     
/* 609 */     findAll(pattern, content, matcher -> collection.add(matcher.group(group)));
/* 610 */     return collection;
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
/*     */   public static void findAll(Pattern pattern, CharSequence content, Consumer<Matcher> consumer) {
/* 622 */     if (null == pattern || null == content) {
/*     */       return;
/*     */     }
/*     */     
/* 626 */     Matcher matcher = pattern.matcher(content);
/* 627 */     while (matcher.find()) {
/* 628 */       consumer.accept(matcher);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int count(String regex, CharSequence content) {
/* 640 */     if (null == regex || null == content) {
/* 641 */       return 0;
/*     */     }
/*     */     
/* 644 */     Pattern pattern = PatternPool.get(regex, 32);
/* 645 */     return count(pattern, content);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int count(Pattern pattern, CharSequence content) {
/* 656 */     if (null == pattern || null == content) {
/* 657 */       return 0;
/*     */     }
/*     */     
/* 660 */     int count = 0;
/* 661 */     Matcher matcher = pattern.matcher(content);
/* 662 */     while (matcher.find()) {
/* 663 */       count++;
/*     */     }
/*     */     
/* 666 */     return count;
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
/*     */   public static boolean contains(String regex, CharSequence content) {
/* 678 */     if (null == regex || null == content) {
/* 679 */       return false;
/*     */     }
/*     */     
/* 682 */     Pattern pattern = PatternPool.get(regex, 32);
/* 683 */     return contains(pattern, content);
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
/*     */   public static boolean contains(Pattern pattern, CharSequence content) {
/* 695 */     if (null == pattern || null == content) {
/* 696 */       return false;
/*     */     }
/* 698 */     return pattern.matcher(content).find();
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
/*     */   public static MatchResult indexOf(String regex, CharSequence content) {
/* 710 */     if (null == regex || null == content) {
/* 711 */       return null;
/*     */     }
/*     */     
/* 714 */     Pattern pattern = PatternPool.get(regex, 32);
/* 715 */     return indexOf(pattern, content);
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
/*     */   public static MatchResult indexOf(Pattern pattern, CharSequence content) {
/* 727 */     if (null != pattern && null != content) {
/* 728 */       Matcher matcher = pattern.matcher(content);
/* 729 */       if (matcher.find()) {
/* 730 */         return matcher.toMatchResult();
/*     */       }
/*     */     } 
/*     */     
/* 734 */     return null;
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
/*     */   public static MatchResult lastIndexOf(String regex, CharSequence content) {
/* 746 */     if (null == regex || null == content) {
/* 747 */       return null;
/*     */     }
/*     */     
/* 750 */     Pattern pattern = PatternPool.get(regex, 32);
/* 751 */     return lastIndexOf(pattern, content);
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
/*     */   public static MatchResult lastIndexOf(Pattern pattern, CharSequence content) {
/* 763 */     MatchResult result = null;
/* 764 */     if (null != pattern && null != content) {
/* 765 */       Matcher matcher = pattern.matcher(content);
/* 766 */       while (matcher.find()) {
/* 767 */         result = matcher.toMatchResult();
/*     */       }
/*     */     } 
/*     */     
/* 771 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Integer getFirstNumber(CharSequence StringWithNumber) {
/* 781 */     return Convert.toInt(get(PatternPool.NUMBERS, StringWithNumber, 0), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isMatch(String regex, CharSequence content) {
/* 792 */     if (content == null)
/*     */     {
/* 794 */       return false;
/*     */     }
/*     */     
/* 797 */     if (StrUtil.isEmpty(regex))
/*     */     {
/* 799 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 803 */     Pattern pattern = PatternPool.get(regex, 32);
/* 804 */     return isMatch(pattern, content);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isMatch(Pattern pattern, CharSequence content) {
/* 815 */     if (content == null || pattern == null)
/*     */     {
/* 817 */       return false;
/*     */     }
/* 819 */     return pattern.matcher(content).matches();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String replaceAll(CharSequence content, String regex, String replacementTemplate) {
/* 841 */     Pattern pattern = Pattern.compile(regex, 32);
/* 842 */     return replaceAll(content, pattern, replacementTemplate);
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
/*     */   public static String replaceAll(CharSequence content, Pattern pattern, String replacementTemplate) {
/* 856 */     if (StrUtil.isEmpty(content)) {
/* 857 */       return StrUtil.str(content);
/*     */     }
/*     */     
/* 860 */     Matcher matcher = pattern.matcher(content);
/* 861 */     boolean result = matcher.find();
/* 862 */     if (result) {
/* 863 */       Set<String> varNums = findAll(PatternPool.GROUP_VAR, replacementTemplate, 1, new HashSet<>());
/* 864 */       StringBuffer sb = new StringBuffer();
/*     */       while (true) {
/* 866 */         String replacement = replacementTemplate;
/* 867 */         for (String var : varNums) {
/* 868 */           int group = Integer.parseInt(var);
/* 869 */           replacement = replacement.replace("$" + var, matcher.group(group));
/*     */         } 
/* 871 */         matcher.appendReplacement(sb, escape(replacement));
/* 872 */         result = matcher.find();
/* 873 */         if (!result)
/* 874 */         { matcher.appendTail(sb);
/* 875 */           return sb.toString(); } 
/*     */       } 
/* 877 */     }  return StrUtil.str(content);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static String replaceAll(CharSequence str, String regex, Func1<Matcher, String> replaceFun) {
/* 896 */     return replaceAll(str, Pattern.compile(regex), replaceFun);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static String replaceAll(CharSequence str, Pattern pattern, Func1<Matcher, String> replaceFun) {
/* 915 */     if (StrUtil.isEmpty(str)) {
/* 916 */       return StrUtil.str(str);
/*     */     }
/*     */     
/* 919 */     Matcher matcher = pattern.matcher(str);
/* 920 */     StringBuffer buffer = new StringBuffer();
/* 921 */     while (matcher.find()) {
/*     */       try {
/* 923 */         matcher.appendReplacement(buffer, (String)replaceFun.call(matcher));
/* 924 */       } catch (Exception e) {
/* 925 */         throw new UtilException(e);
/*     */       } 
/*     */     } 
/* 928 */     matcher.appendTail(buffer);
/* 929 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String escape(char c) {
/* 939 */     StringBuilder builder = new StringBuilder();
/* 940 */     if (RE_KEYS.contains(Character.valueOf(c))) {
/* 941 */       builder.append('\\');
/*     */     }
/* 943 */     builder.append(c);
/* 944 */     return builder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String escape(CharSequence content) {
/* 954 */     if (StrUtil.isBlank(content)) {
/* 955 */       return StrUtil.str(content);
/*     */     }
/*     */     
/* 958 */     StringBuilder builder = new StringBuilder();
/* 959 */     int len = content.length();
/*     */     
/* 961 */     for (int i = 0; i < len; i++) {
/* 962 */       char current = content.charAt(i);
/* 963 */       if (RE_KEYS.contains(Character.valueOf(current))) {
/* 964 */         builder.append('\\');
/*     */       }
/* 966 */       builder.append(current);
/*     */     } 
/* 968 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\ReUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */