/*     */ package cn.hutool.core.text;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public class AntPathMatcher
/*     */ {
/*     */   public static final String DEFAULT_PATH_SEPARATOR = "/";
/*     */   private static final int CACHE_TURNOFF_THRESHOLD = 65536;
/*  51 */   private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{[^/]+?}");
/*     */   
/*  53 */   private static final char[] WILDCARD_CHARS = new char[] { '*', '?', '{' };
/*     */   
/*     */   private String pathSeparator;
/*     */   
/*     */   private PathSeparatorPatternCache pathSeparatorPatternCache;
/*     */   
/*     */   private boolean caseSensitive = true;
/*     */   
/*     */   private boolean trimTokens = false;
/*     */   
/*     */   private volatile Boolean cachePatterns;
/*     */   
/*  65 */   private final Map<String, String[]> tokenizedPatternCache = (Map)new ConcurrentHashMap<>(256);
/*     */   
/*  67 */   private final Map<String, AntPathStringMatcher> stringMatcherCache = new ConcurrentHashMap<>(256);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AntPathMatcher() {
/*  74 */     this("/");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AntPathMatcher(String pathSeparator) {
/*  84 */     if (null == pathSeparator) {
/*  85 */       pathSeparator = "/";
/*     */     }
/*  87 */     setPathSeparator(pathSeparator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AntPathMatcher setPathSeparator(String pathSeparator) {
/*  98 */     if (null == pathSeparator) {
/*  99 */       pathSeparator = "/";
/*     */     }
/* 101 */     this.pathSeparator = pathSeparator;
/* 102 */     this.pathSeparatorPatternCache = new PathSeparatorPatternCache(this.pathSeparator);
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AntPathMatcher setCaseSensitive(boolean caseSensitive) {
/* 113 */     this.caseSensitive = caseSensitive;
/* 114 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AntPathMatcher setTrimTokens(boolean trimTokens) {
/* 124 */     this.trimTokens = trimTokens;
/* 125 */     return this;
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
/*     */   public AntPathMatcher setCachePatterns(boolean cachePatterns) {
/* 143 */     this.cachePatterns = Boolean.valueOf(cachePatterns);
/* 144 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPattern(String path) {
/* 154 */     if (path == null) {
/* 155 */       return false;
/*     */     }
/* 157 */     boolean uriVar = false;
/* 158 */     int length = path.length();
/*     */     
/* 160 */     for (int i = 0; i < length; i++) {
/* 161 */       char c = path.charAt(i);
/*     */       
/* 163 */       if (c == '*' || c == '?') {
/* 164 */         return true;
/*     */       }
/* 166 */       if (c == '{') {
/* 167 */         uriVar = true;
/*     */       
/*     */       }
/* 170 */       else if (c == '}' && uriVar) {
/* 171 */         return true;
/*     */       } 
/*     */     } 
/* 174 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean match(String pattern, String path) {
/* 185 */     return doMatch(pattern, path, true, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matchStart(String pattern, String path) {
/* 196 */     return doMatch(pattern, path, false, null);
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
/*     */   protected boolean doMatch(String pattern, String path, boolean fullMatch, Map<String, String> uriTemplateVariables) {
/* 209 */     if (path == null || path.startsWith(this.pathSeparator) != pattern.startsWith(this.pathSeparator)) {
/* 210 */       return false;
/*     */     }
/*     */     
/* 213 */     String[] pattDirs = tokenizePattern(pattern);
/* 214 */     if (fullMatch && this.caseSensitive && false == isPotentialMatch(path, pattDirs)) {
/* 215 */       return false;
/*     */     }
/*     */     
/* 218 */     String[] pathDirs = tokenizePath(path);
/* 219 */     int pattIdxStart = 0;
/* 220 */     int pattIdxEnd = pattDirs.length - 1;
/* 221 */     int pathIdxStart = 0;
/* 222 */     int pathIdxEnd = pathDirs.length - 1;
/*     */ 
/*     */     
/* 225 */     while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
/* 226 */       String pattDir = pattDirs[pattIdxStart];
/* 227 */       if ("**".equals(pattDir)) {
/*     */         break;
/*     */       }
/* 230 */       if (notMatchStrings(pattDir, pathDirs[pathIdxStart], uriTemplateVariables)) {
/* 231 */         return false;
/*     */       }
/* 233 */       pattIdxStart++;
/* 234 */       pathIdxStart++;
/*     */     } 
/*     */     
/* 237 */     if (pathIdxStart > pathIdxEnd) {
/*     */       
/* 239 */       if (pattIdxStart > pattIdxEnd) {
/* 240 */         return (pattern.endsWith(this.pathSeparator) == path.endsWith(this.pathSeparator));
/*     */       }
/* 242 */       if (false == fullMatch) {
/* 243 */         return true;
/*     */       }
/* 245 */       if (pattIdxStart == pattIdxEnd && pattDirs[pattIdxStart].equals("*") && path.endsWith(this.pathSeparator)) {
/* 246 */         return true;
/*     */       }
/* 248 */       for (int j = pattIdxStart; j <= pattIdxEnd; j++) {
/* 249 */         if (false == pattDirs[j].equals("**")) {
/* 250 */           return false;
/*     */         }
/*     */       } 
/* 253 */       return true;
/* 254 */     }  if (pattIdxStart > pattIdxEnd)
/*     */     {
/* 256 */       return false; } 
/* 257 */     if (false == fullMatch && "**".equals(pattDirs[pattIdxStart]))
/*     */     {
/* 259 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 263 */     while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
/* 264 */       String pattDir = pattDirs[pattIdxEnd];
/* 265 */       if (pattDir.equals("**")) {
/*     */         break;
/*     */       }
/* 268 */       if (notMatchStrings(pattDir, pathDirs[pathIdxEnd], uriTemplateVariables)) {
/* 269 */         return false;
/*     */       }
/* 271 */       pattIdxEnd--;
/* 272 */       pathIdxEnd--;
/*     */     } 
/* 274 */     if (pathIdxStart > pathIdxEnd) {
/*     */       
/* 276 */       for (int j = pattIdxStart; j <= pattIdxEnd; j++) {
/* 277 */         if (false == pattDirs[j].equals("**")) {
/* 278 */           return false;
/*     */         }
/*     */       } 
/* 281 */       return true;
/*     */     } 
/*     */     
/* 284 */     while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
/* 285 */       int patIdxTmp = -1;
/* 286 */       for (int j = pattIdxStart + 1; j <= pattIdxEnd; j++) {
/* 287 */         if (pattDirs[j].equals("**")) {
/* 288 */           patIdxTmp = j;
/*     */           break;
/*     */         } 
/*     */       } 
/* 292 */       if (patIdxTmp == pattIdxStart + 1) {
/*     */         
/* 294 */         pattIdxStart++;
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 299 */       int patLength = patIdxTmp - pattIdxStart - 1;
/* 300 */       int strLength = pathIdxEnd - pathIdxStart + 1;
/* 301 */       int foundIdx = -1;
/*     */ 
/*     */       
/* 304 */       for (int k = 0; k <= strLength - patLength; ) {
/* 305 */         for (int m = 0; m < patLength; m++) {
/* 306 */           String subPat = pattDirs[pattIdxStart + m + 1];
/* 307 */           String subStr = pathDirs[pathIdxStart + k + m];
/* 308 */           if (notMatchStrings(subPat, subStr, uriTemplateVariables)) {
/*     */             k++; continue;
/*     */           } 
/*     */         } 
/* 312 */         foundIdx = pathIdxStart + k;
/*     */       } 
/*     */ 
/*     */       
/* 316 */       if (foundIdx == -1) {
/* 317 */         return false;
/*     */       }
/*     */       
/* 320 */       pattIdxStart = patIdxTmp;
/* 321 */       pathIdxStart = foundIdx + patLength;
/*     */     } 
/*     */     
/* 324 */     for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
/* 325 */       if (false == pattDirs[i].equals("**")) {
/* 326 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 330 */     return true;
/*     */   }
/*     */   
/*     */   private boolean isPotentialMatch(String path, String[] pattDirs) {
/* 334 */     if (!this.trimTokens) {
/* 335 */       int pos = 0;
/* 336 */       for (String pattDir : pattDirs) {
/* 337 */         int skipped = skipSeparator(path, pos, this.pathSeparator);
/* 338 */         pos += skipped;
/* 339 */         skipped = skipSegment(path, pos, pattDir);
/* 340 */         if (skipped < pattDir.length()) {
/* 341 */           return (skipped > 0 || (pattDir.length() > 0 && isWildcardChar(pattDir.charAt(0))));
/*     */         }
/* 343 */         pos += skipped;
/*     */       } 
/*     */     } 
/* 346 */     return true;
/*     */   }
/*     */   
/*     */   private int skipSegment(String path, int pos, String prefix) {
/* 350 */     int skipped = 0;
/* 351 */     for (int i = 0; i < prefix.length(); i++) {
/* 352 */       char c = prefix.charAt(i);
/* 353 */       if (isWildcardChar(c)) {
/* 354 */         return skipped;
/*     */       }
/* 356 */       int currPos = pos + skipped;
/* 357 */       if (currPos >= path.length()) {
/* 358 */         return 0;
/*     */       }
/* 360 */       if (c == path.charAt(currPos)) {
/* 361 */         skipped++;
/*     */       }
/*     */     } 
/* 364 */     return skipped;
/*     */   }
/*     */   
/*     */   private int skipSeparator(String path, int pos, String separator) {
/* 368 */     int skipped = 0;
/* 369 */     while (path.startsWith(separator, pos + skipped)) {
/* 370 */       skipped += separator.length();
/*     */     }
/* 372 */     return skipped;
/*     */   }
/*     */   
/*     */   private boolean isWildcardChar(char c) {
/* 376 */     for (char candidate : WILDCARD_CHARS) {
/* 377 */       if (c == candidate) {
/* 378 */         return true;
/*     */       }
/*     */     } 
/* 381 */     return false;
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
/*     */   protected String[] tokenizePattern(String pattern) {
/* 393 */     String[] tokenized = null;
/* 394 */     Boolean cachePatterns = this.cachePatterns;
/* 395 */     if (cachePatterns == null || cachePatterns.booleanValue()) {
/* 396 */       tokenized = this.tokenizedPatternCache.get(pattern);
/*     */     }
/* 398 */     if (tokenized == null) {
/* 399 */       tokenized = tokenizePath(pattern);
/* 400 */       if (cachePatterns == null && this.tokenizedPatternCache.size() >= 65536) {
/*     */ 
/*     */ 
/*     */         
/* 404 */         deactivatePatternCache();
/* 405 */         return tokenized;
/*     */       } 
/* 407 */       if (cachePatterns == null || cachePatterns.booleanValue()) {
/* 408 */         this.tokenizedPatternCache.put(pattern, tokenized);
/*     */       }
/*     */     } 
/* 411 */     return tokenized;
/*     */   }
/*     */   
/*     */   private void deactivatePatternCache() {
/* 415 */     this.cachePatterns = Boolean.valueOf(false);
/* 416 */     this.tokenizedPatternCache.clear();
/* 417 */     this.stringMatcherCache.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String[] tokenizePath(String path) {
/* 427 */     return StrSplitter.splitToArray(path, this.pathSeparator, 0, this.trimTokens, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean notMatchStrings(String pattern, String str, Map<String, String> uriTemplateVariables) {
/* 438 */     return (false == getStringMatcher(pattern).matchStrings(str, uriTemplateVariables));
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
/*     */   protected AntPathStringMatcher getStringMatcher(String pattern) {
/* 456 */     AntPathStringMatcher matcher = null;
/* 457 */     Boolean cachePatterns = this.cachePatterns;
/* 458 */     if (cachePatterns == null || cachePatterns.booleanValue()) {
/* 459 */       matcher = this.stringMatcherCache.get(pattern);
/*     */     }
/* 461 */     if (matcher == null) {
/* 462 */       matcher = new AntPathStringMatcher(pattern, this.caseSensitive);
/* 463 */       if (cachePatterns == null && this.stringMatcherCache.size() >= 65536) {
/*     */ 
/*     */ 
/*     */         
/* 467 */         deactivatePatternCache();
/* 468 */         return matcher;
/*     */       } 
/* 470 */       if (cachePatterns == null || cachePatterns.booleanValue()) {
/* 471 */         this.stringMatcherCache.put(pattern, matcher);
/*     */       }
/*     */     } 
/* 474 */     return matcher;
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
/*     */   public String extractPathWithinPattern(String pattern, String path) {
/* 495 */     String[] patternParts = tokenizePath(pattern);
/* 496 */     String[] pathParts = tokenizePath(path);
/* 497 */     StringBuilder builder = new StringBuilder();
/* 498 */     boolean pathStarted = false;
/*     */     
/* 500 */     for (int segment = 0; segment < patternParts.length; segment++) {
/* 501 */       String patternPart = patternParts[segment];
/* 502 */       if (patternPart.indexOf('*') > -1 || patternPart.indexOf('?') > -1) {
/* 503 */         for (; segment < pathParts.length; segment++) {
/* 504 */           if (pathStarted || (segment == 0 && !pattern.startsWith(this.pathSeparator))) {
/* 505 */             builder.append(this.pathSeparator);
/*     */           }
/* 507 */           builder.append(pathParts[segment]);
/* 508 */           pathStarted = true;
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 513 */     return builder.toString();
/*     */   }
/*     */   
/*     */   public Map<String, String> extractUriTemplateVariables(String pattern, String path) {
/* 517 */     Map<String, String> variables = new LinkedHashMap<>();
/* 518 */     boolean result = doMatch(pattern, path, true, variables);
/* 519 */     if (!result) {
/* 520 */       throw new IllegalStateException("Pattern \"" + pattern + "\" is not a match for \"" + path + "\"");
/*     */     }
/* 522 */     return variables;
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
/*     */   public String combine(String pattern1, String pattern2) {
/* 555 */     if (StrUtil.isEmpty(pattern1) && StrUtil.isEmpty(pattern2)) {
/* 556 */       return "";
/*     */     }
/* 558 */     if (StrUtil.isEmpty(pattern1)) {
/* 559 */       return pattern2;
/*     */     }
/* 561 */     if (StrUtil.isEmpty(pattern2)) {
/* 562 */       return pattern1;
/*     */     }
/*     */     
/* 565 */     boolean pattern1ContainsUriVar = (pattern1.indexOf('{') != -1);
/* 566 */     if (!pattern1.equals(pattern2) && !pattern1ContainsUriVar && match(pattern1, pattern2))
/*     */     {
/*     */       
/* 569 */       return pattern2;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 574 */     if (pattern1.endsWith(this.pathSeparatorPatternCache.getEndsOnWildCard())) {
/* 575 */       return concat(pattern1.substring(0, pattern1.length() - 2), pattern2);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 580 */     if (pattern1.endsWith(this.pathSeparatorPatternCache.getEndsOnDoubleWildCard())) {
/* 581 */       return concat(pattern1, pattern2);
/*     */     }
/*     */     
/* 584 */     int starDotPos1 = pattern1.indexOf("*.");
/* 585 */     if (pattern1ContainsUriVar || starDotPos1 == -1 || this.pathSeparator.equals("."))
/*     */     {
/* 587 */       return concat(pattern1, pattern2);
/*     */     }
/*     */     
/* 590 */     String ext1 = pattern1.substring(starDotPos1 + 1);
/* 591 */     int dotPos2 = pattern2.indexOf('.');
/* 592 */     String file2 = (dotPos2 == -1) ? pattern2 : pattern2.substring(0, dotPos2);
/* 593 */     String ext2 = (dotPos2 == -1) ? "" : pattern2.substring(dotPos2);
/* 594 */     boolean ext1All = (ext1.equals(".*") || ext1.isEmpty());
/* 595 */     boolean ext2All = (ext2.equals(".*") || ext2.isEmpty());
/* 596 */     if (!ext1All && !ext2All) {
/* 597 */       throw new IllegalArgumentException("Cannot combine patterns: " + pattern1 + " vs " + pattern2);
/*     */     }
/* 599 */     String ext = ext1All ? ext2 : ext1;
/* 600 */     return file2 + ext;
/*     */   }
/*     */   
/*     */   private String concat(String path1, String path2) {
/* 604 */     boolean path1EndsWithSeparator = path1.endsWith(this.pathSeparator);
/* 605 */     boolean path2StartsWithSeparator = path2.startsWith(this.pathSeparator);
/*     */     
/* 607 */     if (path1EndsWithSeparator && path2StartsWithSeparator)
/* 608 */       return path1 + path2.substring(1); 
/* 609 */     if (path1EndsWithSeparator || path2StartsWithSeparator) {
/* 610 */       return path1 + path2;
/*     */     }
/* 612 */     return path1 + this.pathSeparator + path2;
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
/*     */   
/*     */   public Comparator<String> getPatternComparator(String path) {
/* 635 */     return new AntPatternComparator(path);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static class AntPathStringMatcher
/*     */   {
/* 646 */     private static final Pattern GLOB_PATTERN = Pattern.compile("\\?|\\*|\\{((?:\\{[^/]+?}|[^/{}]|\\\\[{}])+?)}");
/*     */     
/*     */     private static final String DEFAULT_VARIABLE_PATTERN = "((?s).*)";
/*     */     
/*     */     private final String rawPattern;
/*     */     
/*     */     private final boolean caseSensitive;
/*     */     
/*     */     private final boolean exactMatch;
/*     */     
/*     */     private final Pattern pattern;
/*     */     
/* 658 */     private final List<String> variableNames = new ArrayList<>();
/*     */     
/*     */     public AntPathStringMatcher(String pattern, boolean caseSensitive) {
/* 661 */       this.rawPattern = pattern;
/* 662 */       this.caseSensitive = caseSensitive;
/* 663 */       StringBuilder patternBuilder = new StringBuilder();
/* 664 */       Matcher matcher = GLOB_PATTERN.matcher(pattern);
/* 665 */       int end = 0;
/* 666 */       while (matcher.find()) {
/* 667 */         patternBuilder.append(quote(pattern, end, matcher.start()));
/* 668 */         String match = matcher.group();
/* 669 */         if ("?".equals(match)) {
/* 670 */           patternBuilder.append('.');
/* 671 */         } else if ("*".equals(match)) {
/* 672 */           patternBuilder.append(".*");
/* 673 */         } else if (match.startsWith("{") && match.endsWith("}")) {
/* 674 */           int colonIdx = match.indexOf(':');
/* 675 */           if (colonIdx == -1) {
/* 676 */             patternBuilder.append("((?s).*)");
/* 677 */             this.variableNames.add(matcher.group(1));
/*     */           } else {
/* 679 */             String variablePattern = match.substring(colonIdx + 1, match.length() - 1);
/* 680 */             patternBuilder.append('(');
/* 681 */             patternBuilder.append(variablePattern);
/* 682 */             patternBuilder.append(')');
/* 683 */             String variableName = match.substring(1, colonIdx);
/* 684 */             this.variableNames.add(variableName);
/*     */           } 
/*     */         } 
/* 687 */         end = matcher.end();
/*     */       } 
/*     */       
/* 690 */       if (end == 0) {
/* 691 */         this.exactMatch = true;
/* 692 */         this.pattern = null;
/*     */       } else {
/* 694 */         this.exactMatch = false;
/* 695 */         patternBuilder.append(quote(pattern, end, pattern.length()));
/* 696 */         this
/* 697 */           .pattern = this.caseSensitive ? Pattern.compile(patternBuilder.toString()) : Pattern.compile(patternBuilder.toString(), 2);
/*     */       } 
/*     */     }
/*     */     
/*     */     private String quote(String s, int start, int end) {
/* 702 */       if (start == end) {
/* 703 */         return "";
/*     */       }
/* 705 */       return Pattern.quote(s.substring(start, end));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean matchStrings(String str, Map<String, String> uriTemplateVariables) {
/* 716 */       if (this.exactMatch)
/* 717 */         return this.caseSensitive ? this.rawPattern.equals(str) : this.rawPattern.equalsIgnoreCase(str); 
/* 718 */       if (this.pattern != null) {
/* 719 */         Matcher matcher = this.pattern.matcher(str);
/* 720 */         if (matcher.matches()) {
/* 721 */           if (uriTemplateVariables != null) {
/* 722 */             if (this.variableNames.size() != matcher.groupCount()) {
/* 723 */               throw new IllegalArgumentException("The number of capturing groups in the pattern segment " + this.pattern + " does not match the number of URI template variables it defines, which can occur if capturing groups are used in a URI template regex. Use non-capturing groups instead.");
/*     */             }
/*     */ 
/*     */ 
/*     */             
/* 728 */             for (int i = 1; i <= matcher.groupCount(); i++) {
/* 729 */               String name = this.variableNames.get(i - 1);
/* 730 */               if (name.startsWith("*")) {
/* 731 */                 throw new IllegalArgumentException("Capturing patterns (" + name + ") are not supported by the AntPathMatcher. Use the PathPatternParser instead.");
/*     */               }
/*     */               
/* 734 */               String value = matcher.group(i);
/* 735 */               uriTemplateVariables.put(name, value);
/*     */             } 
/*     */           } 
/* 738 */           return true;
/*     */         } 
/*     */       } 
/* 741 */       return false;
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
/*     */   protected static class AntPatternComparator
/*     */     implements Comparator<String>
/*     */   {
/*     */     private final String path;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public AntPatternComparator(String path) {
/* 765 */       this.path = path;
/*     */     }
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
/*     */     public int compare(String pattern1, String pattern2) {
/* 779 */       PatternInfo info1 = new PatternInfo(pattern1);
/* 780 */       PatternInfo info2 = new PatternInfo(pattern2);
/*     */       
/* 782 */       if (info1.isLeastSpecific() && info2.isLeastSpecific())
/* 783 */         return 0; 
/* 784 */       if (info1.isLeastSpecific())
/* 785 */         return 1; 
/* 786 */       if (info2.isLeastSpecific()) {
/* 787 */         return -1;
/*     */       }
/*     */       
/* 790 */       boolean pattern1EqualsPath = pattern1.equals(this.path);
/* 791 */       boolean pattern2EqualsPath = pattern2.equals(this.path);
/* 792 */       if (pattern1EqualsPath && pattern2EqualsPath)
/* 793 */         return 0; 
/* 794 */       if (pattern1EqualsPath)
/* 795 */         return -1; 
/* 796 */       if (pattern2EqualsPath) {
/* 797 */         return 1;
/*     */       }
/*     */       
/* 800 */       if (info1.isPrefixPattern() && info2.isPrefixPattern())
/* 801 */         return info2.getLength() - info1.getLength(); 
/* 802 */       if (info1.isPrefixPattern() && info2.getDoubleWildcards() == 0)
/* 803 */         return 1; 
/* 804 */       if (info2.isPrefixPattern() && info1.getDoubleWildcards() == 0) {
/* 805 */         return -1;
/*     */       }
/*     */       
/* 808 */       if (info1.getTotalCount() != info2.getTotalCount()) {
/* 809 */         return info1.getTotalCount() - info2.getTotalCount();
/*     */       }
/*     */       
/* 812 */       if (info1.getLength() != info2.getLength()) {
/* 813 */         return info2.getLength() - info1.getLength();
/*     */       }
/*     */       
/* 816 */       if (info1.getSingleWildcards() < info2.getSingleWildcards())
/* 817 */         return -1; 
/* 818 */       if (info2.getSingleWildcards() < info1.getSingleWildcards()) {
/* 819 */         return 1;
/*     */       }
/*     */       
/* 822 */       if (info1.getUriVars() < info2.getUriVars())
/* 823 */         return -1; 
/* 824 */       if (info2.getUriVars() < info1.getUriVars()) {
/* 825 */         return 1;
/*     */       }
/*     */       
/* 828 */       return 0;
/*     */     }
/*     */ 
/*     */     
/*     */     private static class PatternInfo
/*     */     {
/*     */       private final String pattern;
/*     */       
/*     */       private int uriVars;
/*     */       
/*     */       private int singleWildcards;
/*     */       
/*     */       private int doubleWildcards;
/*     */       
/*     */       private boolean catchAllPattern;
/*     */       private boolean prefixPattern;
/*     */       private Integer length;
/*     */       
/*     */       public PatternInfo(String pattern) {
/* 847 */         this.pattern = pattern;
/* 848 */         if (this.pattern != null) {
/* 849 */           initCounters();
/* 850 */           this.catchAllPattern = this.pattern.equals("/**");
/* 851 */           this.prefixPattern = (!this.catchAllPattern && this.pattern.endsWith("/**"));
/*     */         } 
/* 853 */         if (this.uriVars == 0) {
/* 854 */           this.length = Integer.valueOf((this.pattern != null) ? this.pattern.length() : 0);
/*     */         }
/*     */       }
/*     */       
/*     */       protected void initCounters() {
/* 859 */         int pos = 0;
/* 860 */         if (this.pattern != null) {
/* 861 */           while (pos < this.pattern.length()) {
/* 862 */             if (this.pattern.charAt(pos) == '{') {
/* 863 */               this.uriVars++;
/* 864 */               pos++; continue;
/* 865 */             }  if (this.pattern.charAt(pos) == '*') {
/* 866 */               if (pos + 1 < this.pattern.length() && this.pattern.charAt(pos + 1) == '*') {
/* 867 */                 this.doubleWildcards++;
/* 868 */                 pos += 2; continue;
/* 869 */               }  if (pos > 0 && !this.pattern.substring(pos - 1).equals(".*")) {
/* 870 */                 this.singleWildcards++;
/* 871 */                 pos++; continue;
/*     */               } 
/* 873 */               pos++;
/*     */               continue;
/*     */             } 
/* 876 */             pos++;
/*     */           } 
/*     */         }
/*     */       }
/*     */ 
/*     */       
/*     */       public int getUriVars() {
/* 883 */         return this.uriVars;
/*     */       }
/*     */       
/*     */       public int getSingleWildcards() {
/* 887 */         return this.singleWildcards;
/*     */       }
/*     */       
/*     */       public int getDoubleWildcards() {
/* 891 */         return this.doubleWildcards;
/*     */       }
/*     */       
/*     */       public boolean isLeastSpecific() {
/* 895 */         return (this.pattern == null || this.catchAllPattern);
/*     */       }
/*     */       
/*     */       public boolean isPrefixPattern() {
/* 899 */         return this.prefixPattern;
/*     */       }
/*     */       
/*     */       public int getTotalCount() {
/* 903 */         return this.uriVars + this.singleWildcards + 2 * this.doubleWildcards;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public int getLength() {
/* 912 */         if (this.length == null) {
/* 913 */           this.length = Integer.valueOf((this.pattern != null) ? AntPathMatcher
/* 914 */               .VARIABLE_PATTERN.matcher(this.pattern).replaceAll("#").length() : 0);
/*     */         }
/* 916 */         return this.length.intValue();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class PathSeparatorPatternCache
/*     */   {
/*     */     private final String endsOnWildCard;
/*     */ 
/*     */     
/*     */     private final String endsOnDoubleWildCard;
/*     */ 
/*     */     
/*     */     public PathSeparatorPatternCache(String pathSeparator) {
/* 932 */       this.endsOnWildCard = pathSeparator + "*";
/* 933 */       this.endsOnDoubleWildCard = pathSeparator + "**";
/*     */     }
/*     */     
/*     */     public String getEndsOnWildCard() {
/* 937 */       return this.endsOnWildCard;
/*     */     }
/*     */     
/*     */     public String getEndsOnDoubleWildCard() {
/* 941 */       return this.endsOnDoubleWildCard;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\AntPathMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */