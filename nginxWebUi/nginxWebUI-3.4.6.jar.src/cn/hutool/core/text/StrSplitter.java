/*     */ package cn.hutool.core.text;
/*     */ 
/*     */ import cn.hutool.core.lang.PatternPool;
/*     */ import cn.hutool.core.text.finder.CharFinder;
/*     */ import cn.hutool.core.text.finder.CharMatcherFinder;
/*     */ import cn.hutool.core.text.finder.LengthFinder;
/*     */ import cn.hutool.core.text.finder.PatternFinder;
/*     */ import cn.hutool.core.text.finder.StrFinder;
/*     */ import cn.hutool.core.text.finder.TextFinder;
/*     */ import cn.hutool.core.text.split.SplitIter;
/*     */ import cn.hutool.core.util.CharUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.function.Function;
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
/*     */ public class StrSplitter
/*     */ {
/*     */   public static List<String> splitPath(CharSequence str) {
/*  35 */     return splitPath(str, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String[] splitPathToArray(CharSequence str) {
/*  46 */     return toArray(splitPath(str));
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
/*     */   public static List<String> splitPath(CharSequence str, int limit) {
/*  58 */     return split(str, '/', limit, true, true);
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
/*     */   public static String[] splitPathToArray(CharSequence str, int limit) {
/*  70 */     return toArray(splitPath(str, limit));
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
/*     */   public static List<String> splitTrim(CharSequence str, char separator, boolean ignoreEmpty) {
/*  83 */     return split(str, separator, 0, true, ignoreEmpty);
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
/*     */   public static List<String> split(CharSequence str, char separator, boolean isTrim, boolean ignoreEmpty) {
/*  97 */     return split(str, separator, 0, isTrim, ignoreEmpty);
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
/*     */   public static List<String> splitTrim(CharSequence str, char separator, int limit, boolean ignoreEmpty) {
/* 111 */     return split(str, separator, limit, true, ignoreEmpty, false);
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
/*     */   public static List<String> split(CharSequence str, char separator, int limit, boolean isTrim, boolean ignoreEmpty) {
/* 126 */     return split(str, separator, limit, isTrim, ignoreEmpty, false);
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
/*     */   public static <R> List<R> split(CharSequence str, char separator, int limit, boolean ignoreEmpty, Function<String, R> mapping) {
/* 142 */     return split(str, separator, limit, ignoreEmpty, false, mapping);
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
/*     */   public static List<String> splitIgnoreCase(CharSequence text, char separator, int limit, boolean isTrim, boolean ignoreEmpty) {
/* 157 */     return split(text, separator, limit, isTrim, ignoreEmpty, true);
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
/*     */   public static List<String> split(CharSequence text, char separator, int limit, boolean isTrim, boolean ignoreEmpty, boolean ignoreCase) {
/* 172 */     return split(text, separator, limit, ignoreEmpty, ignoreCase, trimFunc(isTrim));
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
/*     */   public static <R> List<R> split(CharSequence text, char separator, int limit, boolean ignoreEmpty, boolean ignoreCase, Function<String, R> mapping) {
/* 191 */     if (null == text) {
/* 192 */       return new ArrayList<>(0);
/*     */     }
/* 194 */     SplitIter splitIter = new SplitIter(text, (TextFinder)new CharFinder(separator, ignoreCase), limit, ignoreEmpty);
/* 195 */     return splitIter.toList(mapping);
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
/*     */   public static String[] splitToArray(CharSequence str, char separator, int limit, boolean isTrim, boolean ignoreEmpty) {
/* 210 */     return toArray(split(str, separator, limit, isTrim, ignoreEmpty));
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
/*     */   public static List<String> split(CharSequence str, String separator, boolean isTrim, boolean ignoreEmpty) {
/* 226 */     return split(str, separator, -1, isTrim, ignoreEmpty, false);
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
/*     */   public static List<String> splitTrim(CharSequence str, String separator, boolean ignoreEmpty) {
/* 239 */     return split(str, separator, true, ignoreEmpty);
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
/*     */   public static List<String> split(CharSequence str, String separator, int limit, boolean isTrim, boolean ignoreEmpty) {
/* 254 */     return split(str, separator, limit, isTrim, ignoreEmpty, false);
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
/*     */   public static List<String> splitTrim(CharSequence str, String separator, int limit, boolean ignoreEmpty) {
/* 268 */     return split(str, separator, limit, true, ignoreEmpty);
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
/*     */   public static List<String> splitIgnoreCase(CharSequence str, String separator, int limit, boolean isTrim, boolean ignoreEmpty) {
/* 283 */     return split(str, separator, limit, isTrim, ignoreEmpty, true);
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
/*     */   public static List<String> splitTrimIgnoreCase(CharSequence str, String separator, int limit, boolean ignoreEmpty) {
/* 297 */     return split(str, separator, limit, true, ignoreEmpty, true);
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
/*     */   public static List<String> split(CharSequence text, String separator, int limit, boolean isTrim, boolean ignoreEmpty, boolean ignoreCase) {
/* 314 */     if (null == text) {
/* 315 */       return new ArrayList<>(0);
/*     */     }
/* 317 */     SplitIter splitIter = new SplitIter(text, (TextFinder)new StrFinder(separator, ignoreCase), limit, ignoreEmpty);
/* 318 */     return splitIter.toList(isTrim);
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
/*     */   public static String[] splitToArray(CharSequence str, String separator, int limit, boolean isTrim, boolean ignoreEmpty) {
/* 333 */     return toArray(split(str, separator, limit, isTrim, ignoreEmpty));
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
/*     */   public static List<String> split(CharSequence text, int limit) {
/* 349 */     if (null == text) {
/* 350 */       return new ArrayList<>(0);
/*     */     }
/* 352 */     SplitIter splitIter = new SplitIter(text, (TextFinder)new CharMatcherFinder(CharUtil::isBlankChar), limit, true);
/* 353 */     return splitIter.toList(false);
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
/*     */   public static String[] splitToArray(String str, int limit) {
/* 365 */     return toArray(split(str, limit));
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
/*     */   public static List<String> splitByRegex(String text, String separatorRegex, int limit, boolean isTrim, boolean ignoreEmpty) {
/* 381 */     Pattern pattern = PatternPool.get(separatorRegex);
/* 382 */     return split(text, pattern, limit, isTrim, ignoreEmpty);
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
/*     */   public static List<String> split(String text, Pattern separatorPattern, int limit, boolean isTrim, boolean ignoreEmpty) {
/* 398 */     if (null == text) {
/* 399 */       return new ArrayList<>(0);
/*     */     }
/* 401 */     SplitIter splitIter = new SplitIter(text, (TextFinder)new PatternFinder(separatorPattern), limit, ignoreEmpty);
/* 402 */     return splitIter.toList(isTrim);
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
/*     */   public static String[] splitToArray(String str, Pattern separatorPattern, int limit, boolean isTrim, boolean ignoreEmpty) {
/* 417 */     return toArray(split(str, separatorPattern, limit, isTrim, ignoreEmpty));
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
/*     */   public static String[] splitByLength(CharSequence text, int len) {
/* 429 */     if (null == text) {
/* 430 */       return new String[0];
/*     */     }
/* 432 */     SplitIter splitIter = new SplitIter(text, (TextFinder)new LengthFinder(len), -1, false);
/* 433 */     return splitIter.toArray(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String[] toArray(List<String> list) {
/* 444 */     return list.<String>toArray(new String[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Function<String, String> trimFunc(boolean isTrim) {
/* 454 */     return str -> isTrim ? StrUtil.trim(str) : str;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\StrSplitter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */