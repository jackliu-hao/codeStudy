/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SelectorUtils
/*     */ {
/*     */   public static final String PATTERN_HANDLER_PREFIX = "[";
/*     */   public static final String PATTERN_HANDLER_SUFFIX = "]";
/*     */   public static final String REGEX_HANDLER_PREFIX = "%regex[";
/*     */   public static final String ANT_HANDLER_PREFIX = "%ant[";
/*  87 */   private static SelectorUtils instance = new SelectorUtils();
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
/*     */   public static SelectorUtils getInstance() {
/* 101 */     return instance;
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
/*     */   public static boolean matchPatternStart(String pattern, String str) {
/* 122 */     return matchPatternStart(pattern, str, true);
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
/*     */   public static boolean matchPatternStart(String pattern, String str, boolean isCaseSensitive) {
/* 146 */     if (pattern.length() > "%regex[".length() + "]".length() + 1 && pattern.startsWith("%regex[") && pattern.endsWith("]"))
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 151 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 155 */     if (pattern.length() > "%ant[".length() + "]".length() + 1 && pattern.startsWith("%ant[") && pattern.endsWith("]"))
/*     */     {
/*     */       
/* 158 */       pattern = pattern.substring("%ant[".length(), pattern.length() - "]".length());
/*     */     }
/*     */ 
/*     */     
/* 162 */     String altStr = str.replace('\\', '/');
/*     */     
/* 164 */     return (matchAntPathPatternStart(pattern, str, File.separator, isCaseSensitive) || matchAntPathPatternStart(pattern, altStr, "/", isCaseSensitive));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean matchAntPathPatternStart(String pattern, String str, String separator, boolean isCaseSensitive) {
/* 175 */     if (str.startsWith(separator) != pattern.startsWith(separator))
/*     */     {
/*     */       
/* 178 */       return false;
/*     */     }
/*     */     
/* 181 */     Vector patDirs = tokenizePath(pattern, separator);
/* 182 */     Vector strDirs = tokenizePath(str, separator);
/*     */     
/* 184 */     int patIdxStart = 0;
/* 185 */     int patIdxEnd = patDirs.size() - 1;
/* 186 */     int strIdxStart = 0;
/* 187 */     int strIdxEnd = strDirs.size() - 1;
/*     */ 
/*     */     
/* 190 */     while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
/*     */       
/* 192 */       String patDir = patDirs.elementAt(patIdxStart);
/* 193 */       if (patDir.equals("**")) {
/*     */         break;
/*     */       }
/*     */       
/* 197 */       if (!match(patDir, strDirs.elementAt(strIdxStart), isCaseSensitive))
/*     */       {
/*     */         
/* 200 */         return false;
/*     */       }
/* 202 */       patIdxStart++;
/* 203 */       strIdxStart++;
/*     */     } 
/*     */     
/* 206 */     if (strIdxStart > strIdxEnd)
/*     */     {
/*     */       
/* 209 */       return true;
/*     */     }
/* 211 */     if (patIdxStart > patIdxEnd)
/*     */     {
/*     */       
/* 214 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 220 */     return true;
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
/*     */   public static boolean matchPath(String pattern, String str) {
/* 237 */     return matchPath(pattern, str, true);
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
/*     */   public static boolean matchPath(String pattern, String str, boolean isCaseSensitive) {
/* 256 */     if (pattern.length() > "%regex[".length() + "]".length() + 1 && pattern.startsWith("%regex[") && pattern.endsWith("]")) {
/*     */ 
/*     */       
/* 259 */       pattern = pattern.substring("%regex[".length(), pattern.length() - "]".length());
/*     */ 
/*     */       
/* 262 */       return str.matches(pattern);
/*     */     } 
/*     */ 
/*     */     
/* 266 */     if (pattern.length() > "%ant[".length() + "]".length() + 1 && pattern.startsWith("%ant[") && pattern.endsWith("]"))
/*     */     {
/*     */       
/* 269 */       pattern = pattern.substring("%ant[".length(), pattern.length() - "]".length());
/*     */     }
/*     */ 
/*     */     
/* 273 */     return matchAntPathPattern(pattern, str, isCaseSensitive);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean matchAntPathPattern(String pattern, String str, boolean isCaseSensitive) {
/* 283 */     if (str.startsWith(File.separator) != pattern.startsWith(File.separator))
/*     */     {
/*     */       
/* 286 */       return false;
/*     */     }
/*     */     
/* 289 */     Vector patDirs = tokenizePath(pattern, File.separator);
/* 290 */     Vector strDirs = tokenizePath(str, File.separator);
/*     */     
/* 292 */     int patIdxStart = 0;
/* 293 */     int patIdxEnd = patDirs.size() - 1;
/* 294 */     int strIdxStart = 0;
/* 295 */     int strIdxEnd = strDirs.size() - 1;
/*     */ 
/*     */     
/* 298 */     while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
/*     */       
/* 300 */       String patDir = patDirs.elementAt(patIdxStart);
/* 301 */       if (patDir.equals("**")) {
/*     */         break;
/*     */       }
/*     */       
/* 305 */       if (!match(patDir, strDirs.elementAt(strIdxStart), isCaseSensitive)) {
/*     */ 
/*     */         
/* 308 */         patDirs = null;
/* 309 */         strDirs = null;
/* 310 */         return false;
/*     */       } 
/* 312 */       patIdxStart++;
/* 313 */       strIdxStart++;
/*     */     } 
/* 315 */     if (strIdxStart > strIdxEnd) {
/*     */ 
/*     */       
/* 318 */       for (int j = patIdxStart; j <= patIdxEnd; j++) {
/*     */         
/* 320 */         if (!patDirs.elementAt(j).equals("**")) {
/*     */           
/* 322 */           patDirs = null;
/* 323 */           strDirs = null;
/* 324 */           return false;
/*     */         } 
/*     */       } 
/* 327 */       return true;
/*     */     } 
/*     */ 
/*     */     
/* 331 */     if (patIdxStart > patIdxEnd) {
/*     */ 
/*     */       
/* 334 */       patDirs = null;
/* 335 */       strDirs = null;
/* 336 */       return false;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 341 */     while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
/*     */       
/* 343 */       String patDir = patDirs.elementAt(patIdxEnd);
/* 344 */       if (patDir.equals("**")) {
/*     */         break;
/*     */       }
/*     */       
/* 348 */       if (!match(patDir, strDirs.elementAt(strIdxEnd), isCaseSensitive)) {
/*     */ 
/*     */         
/* 351 */         patDirs = null;
/* 352 */         strDirs = null;
/* 353 */         return false;
/*     */       } 
/* 355 */       patIdxEnd--;
/* 356 */       strIdxEnd--;
/*     */     } 
/* 358 */     if (strIdxStart > strIdxEnd) {
/*     */ 
/*     */       
/* 361 */       for (int j = patIdxStart; j <= patIdxEnd; j++) {
/*     */         
/* 363 */         if (!patDirs.elementAt(j).equals("**")) {
/*     */           
/* 365 */           patDirs = null;
/* 366 */           strDirs = null;
/* 367 */           return false;
/*     */         } 
/*     */       } 
/* 370 */       return true;
/*     */     } 
/*     */     
/* 373 */     while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
/*     */       
/* 375 */       int patIdxTmp = -1;
/* 376 */       for (int j = patIdxStart + 1; j <= patIdxEnd; j++) {
/*     */         
/* 378 */         if (patDirs.elementAt(j).equals("**")) {
/*     */           
/* 380 */           patIdxTmp = j;
/*     */           break;
/*     */         } 
/*     */       } 
/* 384 */       if (patIdxTmp == patIdxStart + 1) {
/*     */ 
/*     */         
/* 387 */         patIdxStart++;
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 392 */       int patLength = patIdxTmp - patIdxStart - 1;
/* 393 */       int strLength = strIdxEnd - strIdxStart + 1;
/* 394 */       int foundIdx = -1;
/*     */       
/* 396 */       for (int k = 0; k <= strLength - patLength; ) {
/*     */         
/* 398 */         for (int m = 0; m < patLength; m++) {
/*     */           
/* 400 */           String subPat = patDirs.elementAt(patIdxStart + m + 1);
/* 401 */           String subStr = strDirs.elementAt(strIdxStart + k + m);
/* 402 */           if (!match(subPat, subStr, isCaseSensitive)) {
/*     */             k++;
/*     */             
/*     */             continue;
/*     */           } 
/*     */         } 
/* 408 */         foundIdx = strIdxStart + k;
/*     */       } 
/*     */ 
/*     */       
/* 412 */       if (foundIdx == -1) {
/*     */         
/* 414 */         patDirs = null;
/* 415 */         strDirs = null;
/* 416 */         return false;
/*     */       } 
/*     */       
/* 419 */       patIdxStart = patIdxTmp;
/* 420 */       strIdxStart = foundIdx + patLength;
/*     */     } 
/*     */     
/* 423 */     for (int i = patIdxStart; i <= patIdxEnd; i++) {
/*     */       
/* 425 */       if (!patDirs.elementAt(i).equals("**")) {
/*     */         
/* 427 */         patDirs = null;
/* 428 */         strDirs = null;
/* 429 */         return false;
/*     */       } 
/*     */     } 
/*     */     
/* 433 */     return true;
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
/*     */   public static boolean match(String pattern, String str) {
/* 452 */     return match(pattern, str, true);
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
/*     */   public static boolean match(String pattern, String str, boolean isCaseSensitive) {
/* 475 */     char[] patArr = pattern.toCharArray();
/* 476 */     char[] strArr = str.toCharArray();
/* 477 */     int patIdxStart = 0;
/* 478 */     int patIdxEnd = patArr.length - 1;
/* 479 */     int strIdxStart = 0;
/* 480 */     int strIdxEnd = strArr.length - 1;
/*     */ 
/*     */     
/* 483 */     boolean containsStar = false; int i;
/* 484 */     for (i = 0; i < patArr.length; i++) {
/*     */       
/* 486 */       if (patArr[i] == '*') {
/*     */         
/* 488 */         containsStar = true;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 493 */     if (!containsStar) {
/*     */ 
/*     */       
/* 496 */       if (patIdxEnd != strIdxEnd)
/*     */       {
/* 498 */         return false;
/*     */       }
/* 500 */       for (i = 0; i <= patIdxEnd; i++) {
/*     */         
/* 502 */         char c = patArr[i];
/* 503 */         if (c != '?' && !equals(c, strArr[i], isCaseSensitive))
/*     */         {
/* 505 */           return false;
/*     */         }
/*     */       } 
/* 508 */       return true;
/*     */     } 
/*     */     
/* 511 */     if (patIdxEnd == 0)
/*     */     {
/* 513 */       return true;
/*     */     }
/*     */     
/*     */     char ch;
/* 517 */     while ((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
/*     */       
/* 519 */       if (ch != '?' && !equals(ch, strArr[strIdxStart], isCaseSensitive))
/*     */       {
/* 521 */         return false;
/*     */       }
/* 523 */       patIdxStart++;
/* 524 */       strIdxStart++;
/*     */     } 
/* 526 */     if (strIdxStart > strIdxEnd) {
/*     */ 
/*     */ 
/*     */       
/* 530 */       for (i = patIdxStart; i <= patIdxEnd; i++) {
/*     */         
/* 532 */         if (patArr[i] != '*')
/*     */         {
/* 534 */           return false;
/*     */         }
/*     */       } 
/* 537 */       return true;
/*     */     } 
/*     */ 
/*     */     
/* 541 */     while ((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
/*     */       
/* 543 */       if (ch != '?' && !equals(ch, strArr[strIdxEnd], isCaseSensitive))
/*     */       {
/* 545 */         return false;
/*     */       }
/* 547 */       patIdxEnd--;
/* 548 */       strIdxEnd--;
/*     */     } 
/* 550 */     if (strIdxStart > strIdxEnd) {
/*     */ 
/*     */ 
/*     */       
/* 554 */       for (i = patIdxStart; i <= patIdxEnd; i++) {
/*     */         
/* 556 */         if (patArr[i] != '*')
/*     */         {
/* 558 */           return false;
/*     */         }
/*     */       } 
/* 561 */       return true;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 566 */     while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
/*     */       
/* 568 */       int patIdxTmp = -1;
/* 569 */       for (int j = patIdxStart + 1; j <= patIdxEnd; j++) {
/*     */         
/* 571 */         if (patArr[j] == '*') {
/*     */           
/* 573 */           patIdxTmp = j;
/*     */           break;
/*     */         } 
/*     */       } 
/* 577 */       if (patIdxTmp == patIdxStart + 1) {
/*     */ 
/*     */         
/* 580 */         patIdxStart++;
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 585 */       int patLength = patIdxTmp - patIdxStart - 1;
/* 586 */       int strLength = strIdxEnd - strIdxStart + 1;
/* 587 */       int foundIdx = -1;
/*     */       
/* 589 */       for (int k = 0; k <= strLength - patLength; ) {
/*     */         
/* 591 */         for (int m = 0; m < patLength; m++) {
/*     */           
/* 593 */           ch = patArr[patIdxStart + m + 1];
/* 594 */           if (ch != '?' && !equals(ch, strArr[strIdxStart + k + m], isCaseSensitive)) {
/*     */             k++;
/*     */             
/*     */             continue;
/*     */           } 
/*     */         } 
/* 600 */         foundIdx = strIdxStart + k;
/*     */       } 
/*     */ 
/*     */       
/* 604 */       if (foundIdx == -1)
/*     */       {
/* 606 */         return false;
/*     */       }
/*     */       
/* 609 */       patIdxStart = patIdxTmp;
/* 610 */       strIdxStart = foundIdx + patLength;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 615 */     for (i = patIdxStart; i <= patIdxEnd; i++) {
/*     */       
/* 617 */       if (patArr[i] != '*')
/*     */       {
/* 619 */         return false;
/*     */       }
/*     */     } 
/* 622 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean equals(char c1, char c2, boolean isCaseSensitive) {
/* 630 */     if (c1 == c2)
/*     */     {
/* 632 */       return true;
/*     */     }
/* 634 */     if (!isCaseSensitive)
/*     */     {
/*     */       
/* 637 */       if (Character.toUpperCase(c1) == Character.toUpperCase(c2) || Character.toLowerCase(c1) == Character.toLowerCase(c2))
/*     */       {
/*     */         
/* 640 */         return true;
/*     */       }
/*     */     }
/* 643 */     return false;
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
/*     */   public static Vector tokenizePath(String path) {
/* 656 */     return tokenizePath(path, File.separator);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Vector tokenizePath(String path, String separator) {
/* 661 */     Vector ret = new Vector();
/* 662 */     StringTokenizer st = new StringTokenizer(path, separator);
/* 663 */     while (st.hasMoreTokens())
/*     */     {
/* 665 */       ret.addElement(st.nextToken());
/*     */     }
/* 667 */     return ret;
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
/*     */   public static boolean isOutOfDate(File src, File target, int granularity) {
/* 687 */     if (!src.exists())
/*     */     {
/* 689 */       return false;
/*     */     }
/* 691 */     if (!target.exists())
/*     */     {
/* 693 */       return true;
/*     */     }
/* 695 */     if (src.lastModified() - granularity > target.lastModified())
/*     */     {
/* 697 */       return true;
/*     */     }
/* 699 */     return false;
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
/*     */   public static String removeWhitespace(String input) {
/* 712 */     StringBuffer result = new StringBuffer();
/* 713 */     if (input != null) {
/*     */       
/* 715 */       StringTokenizer st = new StringTokenizer(input);
/* 716 */       while (st.hasMoreTokens())
/*     */       {
/* 718 */         result.append(st.nextToken());
/*     */       }
/*     */     } 
/* 721 */     return result.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\SelectorUtils.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */