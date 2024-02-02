/*      */ package cn.hutool.core.text;
/*      */ 
/*      */ import cn.hutool.core.collection.CollUtil;
/*      */ import cn.hutool.core.comparator.VersionComparator;
/*      */ import cn.hutool.core.convert.Convert;
/*      */ import cn.hutool.core.lang.Assert;
/*      */ import cn.hutool.core.lang.Filter;
/*      */ import cn.hutool.core.lang.Matcher;
/*      */ import cn.hutool.core.lang.func.Func1;
/*      */ import cn.hutool.core.text.finder.CharFinder;
/*      */ import cn.hutool.core.text.finder.StrFinder;
/*      */ import cn.hutool.core.util.ArrayUtil;
/*      */ import cn.hutool.core.util.CharUtil;
/*      */ import cn.hutool.core.util.CharsetUtil;
/*      */ import cn.hutool.core.util.DesensitizedUtil;
/*      */ import cn.hutool.core.util.NumberUtil;
/*      */ import cn.hutool.core.util.ReUtil;
/*      */ import cn.hutool.core.util.StrUtil;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.charset.Charset;
/*      */ import java.text.MessageFormat;
/*      */ import java.text.Normalizer;
/*      */ import java.util.HashSet;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.function.Function;
/*      */ import java.util.function.Predicate;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class CharSequenceUtil
/*      */ {
/*      */   public static final int INDEX_NOT_FOUND = -1;
/*      */   public static final String NULL = "null";
/*      */   public static final String EMPTY = "";
/*      */   public static final String SPACE = " ";
/*      */   
/*      */   public static boolean isBlank(CharSequence str) {
/*      */     int length;
/*   91 */     if (str == null || (length = str.length()) == 0) {
/*   92 */       return true;
/*      */     }
/*      */     
/*   95 */     for (int i = 0; i < length; i++) {
/*      */       
/*   97 */       if (false == CharUtil.isBlankChar(str.charAt(i))) {
/*   98 */         return false;
/*      */       }
/*      */     } 
/*      */     
/*  102 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotBlank(CharSequence str) {
/*  130 */     return (false == isBlank(str));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasBlank(CharSequence... strs) {
/*  156 */     if (ArrayUtil.isEmpty((Object[])strs)) {
/*  157 */       return true;
/*      */     }
/*      */     
/*  160 */     for (CharSequence str : strs) {
/*  161 */       if (isBlank(str)) {
/*  162 */         return true;
/*      */       }
/*      */     } 
/*  165 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isAllBlank(CharSequence... strs) {
/*  191 */     if (ArrayUtil.isEmpty((Object[])strs)) {
/*  192 */       return true;
/*      */     }
/*      */     
/*  195 */     for (CharSequence str : strs) {
/*  196 */       if (isNotBlank(str)) {
/*  197 */         return false;
/*      */       }
/*      */     } 
/*  200 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmpty(CharSequence str) {
/*  230 */     return (str == null || str.length() == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotEmpty(CharSequence str) {
/*  256 */     return (false == isEmpty(str));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String emptyIfNull(CharSequence str) {
/*  268 */     return nullToEmpty(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String nullToEmpty(CharSequence str) {
/*  278 */     return nullToDefault(str, "");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String nullToDefault(CharSequence str, String defaultStr) {
/*  296 */     return (str == null) ? defaultStr : str.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String emptyToDefault(CharSequence str, String defaultStr) {
/*  315 */     return isEmpty(str) ? defaultStr : str.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String blankToDefault(CharSequence str, String defaultStr) {
/*  334 */     return isBlank(str) ? defaultStr : str.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String emptyToNull(CharSequence str) {
/*  344 */     return isEmpty(str) ? null : str.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasEmpty(CharSequence... strs) {
/*  371 */     if (ArrayUtil.isEmpty((Object[])strs)) {
/*  372 */       return true;
/*      */     }
/*      */     
/*  375 */     for (CharSequence str : strs) {
/*  376 */       if (isEmpty(str)) {
/*  377 */         return true;
/*      */       }
/*      */     } 
/*  380 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isAllEmpty(CharSequence... strs) {
/*  407 */     if (ArrayUtil.isEmpty((Object[])strs)) {
/*  408 */       return true;
/*      */     }
/*      */     
/*  411 */     for (CharSequence str : strs) {
/*  412 */       if (isNotEmpty(str)) {
/*  413 */         return false;
/*      */       }
/*      */     } 
/*  416 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isAllNotEmpty(CharSequence... args) {
/*  444 */     return (false == hasEmpty(args));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isAllNotBlank(CharSequence... args) {
/*  455 */     return (false == hasBlank(args));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNullOrUndefined(CharSequence str) {
/*  466 */     if (null == str) {
/*  467 */       return true;
/*      */     }
/*  469 */     return isNullOrUndefinedStr(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmptyOrUndefined(CharSequence str) {
/*  480 */     if (isEmpty(str)) {
/*  481 */       return true;
/*      */     }
/*  483 */     return isNullOrUndefinedStr(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isBlankOrUndefined(CharSequence str) {
/*  494 */     if (isBlank(str)) {
/*  495 */       return true;
/*      */     }
/*  497 */     return isNullOrUndefinedStr(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean isNullOrUndefinedStr(CharSequence str) {
/*  507 */     String strString = str.toString().trim();
/*  508 */     return ("null".equals(strString) || "undefined".equals(strString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String trim(CharSequence str) {
/*  531 */     return (null == str) ? null : trim(str, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String trimToEmpty(CharSequence str) {
/*  550 */     return (str == null) ? "" : trim(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String trimToNull(CharSequence str) {
/*  569 */     String trimStr = trim(str);
/*  570 */     return "".equals(trimStr) ? null : trimStr;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String trimStart(CharSequence str) {
/*  592 */     return trim(str, -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String trimEnd(CharSequence str) {
/*  614 */     return trim(str, 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String trim(CharSequence str, int mode) {
/*  625 */     return trim(str, mode, CharUtil::isBlankChar);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String trim(CharSequence str, int mode, Predicate<Character> predicate) {
/*      */     String result;
/*  639 */     if (str == null) {
/*  640 */       result = null;
/*      */     } else {
/*  642 */       int length = str.length();
/*  643 */       int start = 0;
/*  644 */       int end = length;
/*  645 */       if (mode <= 0) {
/*  646 */         while (start < end && predicate.test(Character.valueOf(str.charAt(start)))) {
/*  647 */           start++;
/*      */         }
/*      */       }
/*  650 */       if (mode >= 0) {
/*  651 */         while (start < end && predicate.test(Character.valueOf(str.charAt(end - 1)))) {
/*  652 */           end--;
/*      */         }
/*      */       }
/*  655 */       if (start > 0 || end < length) {
/*  656 */         result = str.toString().substring(start, end);
/*      */       } else {
/*  658 */         result = str.toString();
/*      */       } 
/*      */     } 
/*      */     
/*  662 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean startWith(CharSequence str, char c) {
/*  675 */     if (isEmpty(str)) {
/*  676 */       return false;
/*      */     }
/*  678 */     return (c == str.charAt(0));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean startWith(CharSequence str, CharSequence prefix, boolean ignoreCase) {
/*  692 */     return startWith(str, prefix, ignoreCase, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean startWith(CharSequence str, CharSequence prefix, boolean ignoreCase, boolean ignoreEquals) {
/*  712 */     if (null == str || null == prefix) {
/*  713 */       if (ignoreEquals) {
/*  714 */         return false;
/*      */       }
/*  716 */       return (null == str && null == prefix);
/*      */     } 
/*      */ 
/*      */     
/*  720 */     boolean isStartWith = str.toString().regionMatches(ignoreCase, 0, prefix.toString(), 0, prefix.length());
/*      */     
/*  722 */     if (isStartWith) {
/*  723 */       return (false == ignoreEquals || false == equals(str, prefix, ignoreCase));
/*      */     }
/*  725 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean startWith(CharSequence str, CharSequence prefix) {
/*  736 */     return startWith(str, prefix, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean startWithIgnoreEquals(CharSequence str, CharSequence prefix) {
/*  747 */     return startWith(str, prefix, false, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean startWithIgnoreCase(CharSequence str, CharSequence prefix) {
/*  758 */     return startWith(str, prefix, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean startWithAny(CharSequence str, CharSequence... prefixes) {
/*  771 */     if (isEmpty(str) || ArrayUtil.isEmpty((Object[])prefixes)) {
/*  772 */       return false;
/*      */     }
/*      */     
/*  775 */     for (CharSequence suffix : prefixes) {
/*  776 */       if (startWith(str, suffix, false)) {
/*  777 */         return true;
/*      */       }
/*      */     } 
/*  780 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean startWithAnyIgnoreCase(CharSequence str, CharSequence... suffixes) {
/*  793 */     if (isEmpty(str) || ArrayUtil.isEmpty((Object[])suffixes)) {
/*  794 */       return false;
/*      */     }
/*      */     
/*  797 */     for (CharSequence suffix : suffixes) {
/*  798 */       if (startWith(str, suffix, true)) {
/*  799 */         return true;
/*      */       }
/*      */     } 
/*  802 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean endWith(CharSequence str, char c) {
/*  815 */     if (isEmpty(str)) {
/*  816 */       return false;
/*      */     }
/*  818 */     return (c == str.charAt(str.length() - 1));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean endWith(CharSequence str, CharSequence suffix, boolean ignoreCase) {
/*  831 */     return endWith(str, suffix, ignoreCase, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean endWith(CharSequence str, CharSequence suffix, boolean ignoreCase, boolean ignoreEquals) {
/*  846 */     if (null == str || null == suffix) {
/*  847 */       if (ignoreEquals) {
/*  848 */         return false;
/*      */       }
/*  850 */       return (null == str && null == suffix);
/*      */     } 
/*      */     
/*  853 */     int strOffset = str.length() - suffix.length();
/*      */     
/*  855 */     boolean isEndWith = str.toString().regionMatches(ignoreCase, strOffset, suffix.toString(), 0, suffix.length());
/*      */     
/*  857 */     if (isEndWith) {
/*  858 */       return (false == ignoreEquals || false == equals(str, suffix, ignoreCase));
/*      */     }
/*  860 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean endWith(CharSequence str, CharSequence suffix) {
/*  871 */     return endWith(str, suffix, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean endWithIgnoreCase(CharSequence str, CharSequence suffix) {
/*  882 */     return endWith(str, suffix, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean endWithAny(CharSequence str, CharSequence... suffixes) {
/*  895 */     if (isEmpty(str) || ArrayUtil.isEmpty((Object[])suffixes)) {
/*  896 */       return false;
/*      */     }
/*      */     
/*  899 */     for (CharSequence suffix : suffixes) {
/*  900 */       if (endWith(str, suffix, false)) {
/*  901 */         return true;
/*      */       }
/*      */     } 
/*  904 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean endWithAnyIgnoreCase(CharSequence str, CharSequence... suffixes) {
/*  917 */     if (isEmpty(str) || ArrayUtil.isEmpty((Object[])suffixes)) {
/*  918 */       return false;
/*      */     }
/*      */     
/*  921 */     for (CharSequence suffix : suffixes) {
/*  922 */       if (endWith(str, suffix, true)) {
/*  923 */         return true;
/*      */       }
/*      */     } 
/*  926 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean contains(CharSequence str, char searchChar) {
/*  940 */     return (indexOf(str, searchChar) > -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean contains(CharSequence str, CharSequence searchStr) {
/*  952 */     if (null == str || null == searchStr) {
/*  953 */       return false;
/*      */     }
/*  955 */     return str.toString().contains(searchStr);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean containsAny(CharSequence str, CharSequence... testStrs) {
/*  967 */     return (null != getContainsStr(str, testStrs));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean containsAny(CharSequence str, char... testChars) {
/*  979 */     if (false == isEmpty(str)) {
/*  980 */       int len = str.length();
/*  981 */       for (int i = 0; i < len; i++) {
/*  982 */         if (ArrayUtil.contains(testChars, str.charAt(i))) {
/*  983 */           return true;
/*      */         }
/*      */       } 
/*      */     } 
/*  987 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean containsOnly(CharSequence str, char... testChars) {
/*  999 */     if (false == isEmpty(str)) {
/* 1000 */       int len = str.length();
/* 1001 */       for (int i = 0; i < len; i++) {
/* 1002 */         if (false == ArrayUtil.contains(testChars, str.charAt(i))) {
/* 1003 */           return false;
/*      */         }
/*      */       } 
/*      */     } 
/* 1007 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean containsBlank(CharSequence str) {
/* 1019 */     if (null == str) {
/* 1020 */       return false;
/*      */     }
/* 1022 */     int length = str.length();
/* 1023 */     if (0 == length) {
/* 1024 */       return false;
/*      */     }
/*      */     
/* 1027 */     for (int i = 0; i < length; i++) {
/* 1028 */       if (CharUtil.isBlankChar(str.charAt(i))) {
/* 1029 */         return true;
/*      */       }
/*      */     } 
/* 1032 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getContainsStr(CharSequence str, CharSequence... testStrs) {
/* 1044 */     if (isEmpty(str) || ArrayUtil.isEmpty((Object[])testStrs)) {
/* 1045 */       return null;
/*      */     }
/* 1047 */     for (CharSequence checkStr : testStrs) {
/* 1048 */       if (str.toString().contains(checkStr)) {
/* 1049 */         return checkStr.toString();
/*      */       }
/*      */     } 
/* 1052 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean containsIgnoreCase(CharSequence str, CharSequence testStr) {
/* 1063 */     if (null == str)
/*      */     {
/* 1065 */       return (null == testStr);
/*      */     }
/* 1067 */     return (indexOfIgnoreCase(str, testStr) > -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean containsAnyIgnoreCase(CharSequence str, CharSequence... testStrs) {
/* 1080 */     return (null != getContainsStrIgnoreCase(str, testStrs));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getContainsStrIgnoreCase(CharSequence str, CharSequence... testStrs) {
/* 1093 */     if (isEmpty(str) || ArrayUtil.isEmpty((Object[])testStrs)) {
/* 1094 */       return null;
/*      */     }
/* 1096 */     for (CharSequence testStr : testStrs) {
/* 1097 */       if (containsIgnoreCase(str, testStr)) {
/* 1098 */         return testStr.toString();
/*      */       }
/*      */     } 
/* 1101 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int indexOf(CharSequence str, char searchChar) {
/* 1114 */     return indexOf(str, searchChar, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int indexOf(CharSequence str, char searchChar, int start) {
/* 1126 */     if (str instanceof String) {
/* 1127 */       return ((String)str).indexOf(searchChar, start);
/*      */     }
/* 1129 */     return indexOf(str, searchChar, start, -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int indexOf(CharSequence text, char searchChar, int start, int end) {
/* 1143 */     if (isEmpty(text)) {
/* 1144 */       return -1;
/*      */     }
/* 1146 */     return (new CharFinder(searchChar)).setText(text).setEndIndex(end).start(start);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int indexOfIgnoreCase(CharSequence str, CharSequence searchStr) {
/* 1172 */     return indexOfIgnoreCase(str, searchStr, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int indexOfIgnoreCase(CharSequence str, CharSequence searchStr, int fromIndex) {
/* 1199 */     return indexOf(str, searchStr, fromIndex, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int indexOf(CharSequence text, CharSequence searchStr, int from, boolean ignoreCase) {
/* 1213 */     if (isEmpty(text) || isEmpty(searchStr)) {
/* 1214 */       if (StrUtil.equals(text, searchStr)) {
/* 1215 */         return 0;
/*      */       }
/* 1217 */       return -1;
/*      */     } 
/*      */     
/* 1220 */     return (new StrFinder(searchStr, ignoreCase)).setText(text).start(from);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int lastIndexOfIgnoreCase(CharSequence str, CharSequence searchStr) {
/* 1232 */     return lastIndexOfIgnoreCase(str, searchStr, str.length());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int lastIndexOfIgnoreCase(CharSequence str, CharSequence searchStr, int fromIndex) {
/* 1246 */     return lastIndexOf(str, searchStr, fromIndex, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int lastIndexOf(CharSequence text, CharSequence searchStr, int from, boolean ignoreCase) {
/* 1261 */     if (isEmpty(text) || isEmpty(searchStr)) {
/* 1262 */       if (StrUtil.equals(text, searchStr)) {
/* 1263 */         return 0;
/*      */       }
/* 1265 */       return -1;
/*      */     } 
/*      */     
/* 1268 */     return (new StrFinder(searchStr, ignoreCase))
/* 1269 */       .setText(text).setNegative(true).start(from);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int ordinalIndexOf(CharSequence str, CharSequence searchStr, int ordinal) {
/* 1302 */     if (str == null || searchStr == null || ordinal <= 0) {
/* 1303 */       return -1;
/*      */     }
/* 1305 */     if (searchStr.length() == 0) {
/* 1306 */       return 0;
/*      */     }
/* 1308 */     int found = 0;
/* 1309 */     int index = -1;
/*      */     while (true) {
/* 1311 */       index = indexOf(str, searchStr, index + 1, false);
/* 1312 */       if (index < 0) {
/* 1313 */         return index;
/*      */       }
/* 1315 */       found++;
/* 1316 */       if (found >= ordinal) {
/* 1317 */         return index;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String removeAll(CharSequence str, CharSequence strToRemove) {
/* 1332 */     if (isEmpty(str) || isEmpty(strToRemove)) {
/* 1333 */       return str(str);
/*      */     }
/* 1335 */     return str.toString().replace(strToRemove, "");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String removeAny(CharSequence str, CharSequence... strsToRemove) {
/* 1348 */     String result = str(str);
/* 1349 */     if (isNotEmpty(str)) {
/* 1350 */       for (CharSequence strToRemove : strsToRemove) {
/* 1351 */         result = removeAll(result, strToRemove);
/*      */       }
/*      */     }
/* 1354 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String removeAll(CharSequence str, char... chars) {
/* 1366 */     if (null == str || ArrayUtil.isEmpty(chars)) {
/* 1367 */       return str(str);
/*      */     }
/* 1369 */     int len = str.length();
/* 1370 */     if (0 == len) {
/* 1371 */       return str(str);
/*      */     }
/* 1373 */     StringBuilder builder = new StringBuilder(len);
/*      */     
/* 1375 */     for (int i = 0; i < len; i++) {
/* 1376 */       char c = str.charAt(i);
/* 1377 */       if (false == ArrayUtil.contains(chars, c)) {
/* 1378 */         builder.append(c);
/*      */       }
/*      */     } 
/* 1381 */     return builder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String removeAllLineBreaks(CharSequence str) {
/* 1397 */     return removeAll(str, new char[] { '\r', '\n' });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String removePreAndLowerFirst(CharSequence str, int preLength) {
/* 1409 */     if (str == null) {
/* 1410 */       return null;
/*      */     }
/* 1412 */     if (str.length() > preLength) {
/* 1413 */       char first = Character.toLowerCase(str.charAt(preLength));
/* 1414 */       if (str.length() > preLength + 1) {
/* 1415 */         return first + str.toString().substring(preLength + 1);
/*      */       }
/* 1417 */       return String.valueOf(first);
/*      */     } 
/* 1419 */     return str.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String removePreAndLowerFirst(CharSequence str, CharSequence prefix) {
/* 1432 */     return lowerFirst(removePrefix(str, prefix));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String removePrefix(CharSequence str, CharSequence prefix) {
/* 1443 */     if (isEmpty(str) || isEmpty(prefix)) {
/* 1444 */       return str(str);
/*      */     }
/*      */     
/* 1447 */     String str2 = str.toString();
/* 1448 */     if (str2.startsWith(prefix.toString())) {
/* 1449 */       return subSuf(str2, prefix.length());
/*      */     }
/* 1451 */     return str2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String removePrefixIgnoreCase(CharSequence str, CharSequence prefix) {
/* 1462 */     if (isEmpty(str) || isEmpty(prefix)) {
/* 1463 */       return str(str);
/*      */     }
/*      */     
/* 1466 */     String str2 = str.toString();
/* 1467 */     if (startWithIgnoreCase(str, prefix)) {
/* 1468 */       return subSuf(str2, prefix.length());
/*      */     }
/* 1470 */     return str2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String removeSuffix(CharSequence str, CharSequence suffix) {
/* 1481 */     if (isEmpty(str) || isEmpty(suffix)) {
/* 1482 */       return str(str);
/*      */     }
/*      */     
/* 1485 */     String str2 = str.toString();
/* 1486 */     if (str2.endsWith(suffix.toString())) {
/* 1487 */       return subPre(str2, str2.length() - suffix.length());
/*      */     }
/* 1489 */     return str2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String removeSufAndLowerFirst(CharSequence str, CharSequence suffix) {
/* 1500 */     return lowerFirst(removeSuffix(str, suffix));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String removeSuffixIgnoreCase(CharSequence str, CharSequence suffix) {
/* 1511 */     if (isEmpty(str) || isEmpty(suffix)) {
/* 1512 */       return str(str);
/*      */     }
/*      */     
/* 1515 */     String str2 = str.toString();
/* 1516 */     if (endWithIgnoreCase(str, suffix)) {
/* 1517 */       return subPre(str2, str2.length() - suffix.length());
/*      */     }
/* 1519 */     return str2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String cleanBlank(CharSequence str) {
/* 1529 */     return filter(str, c -> (false == CharUtil.isBlankChar(c.charValue())));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String strip(CharSequence str, CharSequence prefixOrSuffix) {
/* 1543 */     if (equals(str, prefixOrSuffix))
/*      */     {
/* 1545 */       return "";
/*      */     }
/* 1547 */     return strip(str, prefixOrSuffix, prefixOrSuffix);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String strip(CharSequence str, CharSequence prefix, CharSequence suffix) {
/* 1560 */     if (isEmpty(str)) {
/* 1561 */       return str(str);
/*      */     }
/*      */     
/* 1564 */     int from = 0;
/* 1565 */     int to = str.length();
/*      */     
/* 1567 */     String str2 = str.toString();
/* 1568 */     if (startWith(str2, prefix)) {
/* 1569 */       from = prefix.length();
/*      */     }
/* 1571 */     if (endWith(str2, suffix)) {
/* 1572 */       to -= suffix.length();
/*      */     }
/*      */     
/* 1575 */     return str2.substring(Math.min(from, to), Math.max(from, to));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String stripIgnoreCase(CharSequence str, CharSequence prefixOrSuffix) {
/* 1587 */     return stripIgnoreCase(str, prefixOrSuffix, prefixOrSuffix);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String stripIgnoreCase(CharSequence str, CharSequence prefix, CharSequence suffix) {
/* 1600 */     if (isEmpty(str)) {
/* 1601 */       return str(str);
/*      */     }
/* 1603 */     int from = 0;
/* 1604 */     int to = str.length();
/*      */     
/* 1606 */     String str2 = str.toString();
/* 1607 */     if (startWithIgnoreCase(str2, prefix)) {
/* 1608 */       from = prefix.length();
/*      */     }
/* 1610 */     if (endWithIgnoreCase(str2, suffix)) {
/* 1611 */       to -= suffix.length();
/*      */     }
/* 1613 */     return str2.substring(from, to);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String addPrefixIfNot(CharSequence str, CharSequence prefix) {
/* 1627 */     return prependIfMissing(str, prefix, new CharSequence[] { prefix });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String addSuffixIfNot(CharSequence str, CharSequence suffix) {
/* 1639 */     return appendIfMissing(str, suffix, new CharSequence[] { suffix });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long[] splitToLong(CharSequence str, char separator) {
/* 1653 */     return (long[])Convert.convert(long[].class, splitTrim(str, separator));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long[] splitToLong(CharSequence str, CharSequence separator) {
/* 1665 */     return (long[])Convert.convert(long[].class, splitTrim(str, separator));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int[] splitToInt(CharSequence str, char separator) {
/* 1677 */     return (int[])Convert.convert(int[].class, splitTrim(str, separator));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int[] splitToInt(CharSequence str, CharSequence separator) {
/* 1689 */     return (int[])Convert.convert(int[].class, splitTrim(str, separator));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> split(CharSequence str, char separator) {
/* 1702 */     return split(str, separator, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] splitToArray(CharSequence str, CharSequence separator) {
/* 1714 */     if (str == null) {
/* 1715 */       return new String[0];
/*      */     }
/*      */     
/* 1718 */     return StrSplitter.splitToArray(str.toString(), str(separator), 0, false, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] splitToArray(CharSequence str, char separator) {
/* 1729 */     return splitToArray(str, separator, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] splitToArray(CharSequence text, char separator, int limit) {
/* 1741 */     Assert.notNull(text, "Text must be not null!", new Object[0]);
/* 1742 */     return StrSplitter.splitToArray(text.toString(), separator, limit, false, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> split(CharSequence str, char separator, int limit) {
/* 1754 */     return split(str, separator, limit, false, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> splitTrim(CharSequence str, char separator) {
/* 1766 */     return splitTrim(str, separator, -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> splitTrim(CharSequence str, CharSequence separator) {
/* 1778 */     return splitTrim(str, separator, -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> splitTrim(CharSequence str, char separator, int limit) {
/* 1791 */     return split(str, separator, limit, true, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> splitTrim(CharSequence str, CharSequence separator, int limit) {
/* 1804 */     return split(str, separator, limit, true, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> split(CharSequence str, char separator, boolean isTrim, boolean ignoreEmpty) {
/* 1818 */     return split(str, separator, 0, isTrim, ignoreEmpty);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> split(CharSequence str, char separator, int limit, boolean isTrim, boolean ignoreEmpty) {
/* 1833 */     return StrSplitter.split(str, separator, limit, isTrim, ignoreEmpty);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <R> List<R> split(CharSequence str, char separator, int limit, boolean ignoreEmpty, Function<String, R> mapping) {
/* 1849 */     return StrSplitter.split(str, separator, limit, ignoreEmpty, mapping);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> split(CharSequence str, CharSequence separator) {
/* 1861 */     return split(str, separator, false, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> split(CharSequence str, CharSequence separator, boolean isTrim, boolean ignoreEmpty) {
/* 1875 */     return split(str, separator, 0, isTrim, ignoreEmpty);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> split(CharSequence str, CharSequence separator, int limit, boolean isTrim, boolean ignoreEmpty) {
/* 1890 */     String separatorStr = (null == separator) ? null : separator.toString();
/* 1891 */     return StrSplitter.split(str, separatorStr, limit, isTrim, ignoreEmpty);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] split(CharSequence str, int len) {
/* 1903 */     return StrSplitter.splitByLength(str, len);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] cut(CharSequence str, int partLength) {
/* 1915 */     if (null == str) {
/* 1916 */       return null;
/*      */     }
/* 1918 */     int len = str.length();
/* 1919 */     if (len < partLength) {
/* 1920 */       return new String[] { str.toString() };
/*      */     }
/* 1922 */     int part = NumberUtil.count(len, partLength);
/* 1923 */     String[] array = new String[part];
/*      */     
/* 1925 */     String str2 = str.toString();
/* 1926 */     for (int i = 0; i < part; i++) {
/* 1927 */       array[i] = str2.substring(i * partLength, (i == part - 1) ? len : (partLength + i * partLength));
/*      */     }
/* 1929 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String sub(CharSequence str, int fromIndexInclude, int toIndexExclude) {
/* 1949 */     if (isEmpty(str)) {
/* 1950 */       return str(str);
/*      */     }
/* 1952 */     int len = str.length();
/*      */     
/* 1954 */     if (fromIndexInclude < 0) {
/* 1955 */       fromIndexInclude = len + fromIndexInclude;
/* 1956 */       if (fromIndexInclude < 0) {
/* 1957 */         fromIndexInclude = 0;
/*      */       }
/* 1959 */     } else if (fromIndexInclude > len) {
/* 1960 */       fromIndexInclude = len;
/*      */     } 
/*      */     
/* 1963 */     if (toIndexExclude < 0) {
/* 1964 */       toIndexExclude = len + toIndexExclude;
/* 1965 */       if (toIndexExclude < 0) {
/* 1966 */         toIndexExclude = len;
/*      */       }
/* 1968 */     } else if (toIndexExclude > len) {
/* 1969 */       toIndexExclude = len;
/*      */     } 
/*      */     
/* 1972 */     if (toIndexExclude < fromIndexInclude) {
/* 1973 */       int tmp = fromIndexInclude;
/* 1974 */       fromIndexInclude = toIndexExclude;
/* 1975 */       toIndexExclude = tmp;
/*      */     } 
/*      */     
/* 1978 */     if (fromIndexInclude == toIndexExclude) {
/* 1979 */       return "";
/*      */     }
/*      */     
/* 1982 */     return str.toString().substring(fromIndexInclude, toIndexExclude);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String subByCodePoint(CharSequence str, int fromIndex, int toIndex) {
/* 1994 */     if (isEmpty(str)) {
/* 1995 */       return str(str);
/*      */     }
/*      */     
/* 1998 */     if (fromIndex < 0 || fromIndex > toIndex) {
/* 1999 */       throw new IllegalArgumentException();
/*      */     }
/*      */     
/* 2002 */     if (fromIndex == toIndex) {
/* 2003 */       return "";
/*      */     }
/*      */     
/* 2006 */     StringBuilder sb = new StringBuilder();
/* 2007 */     int subLen = toIndex - fromIndex;
/* 2008 */     str.toString().codePoints().skip(fromIndex).limit(subLen).forEach(v -> sb.append(Character.toChars(v)));
/* 2009 */     return sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String subPreGbk(CharSequence str, int len, CharSequence suffix) {
/* 2022 */     return subPreGbk(str, len, true) + suffix;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String subPreGbk(CharSequence str, int len, boolean halfUp) {
/* 2036 */     if (isEmpty(str)) {
/* 2037 */       return str(str);
/*      */     }
/*      */     
/* 2040 */     int counterOfDoubleByte = 0;
/* 2041 */     byte[] b = bytes(str, CharsetUtil.CHARSET_GBK);
/* 2042 */     if (b.length <= len) {
/* 2043 */       return str.toString();
/*      */     }
/* 2045 */     for (int i = 0; i < len; i++) {
/* 2046 */       if (b[i] < 0) {
/* 2047 */         counterOfDoubleByte++;
/*      */       }
/*      */     } 
/*      */     
/* 2051 */     if (counterOfDoubleByte % 2 != 0) {
/* 2052 */       if (halfUp) {
/* 2053 */         len++;
/*      */       } else {
/* 2055 */         len--;
/*      */       } 
/*      */     }
/* 2058 */     return new String(b, 0, len, CharsetUtil.CHARSET_GBK);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String subPre(CharSequence string, int toIndexExclude) {
/* 2069 */     return sub(string, 0, toIndexExclude);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String subSuf(CharSequence string, int fromIndex) {
/* 2080 */     if (isEmpty(string)) {
/* 2081 */       return null;
/*      */     }
/* 2083 */     return sub(string, fromIndex, string.length());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String subSufByLength(CharSequence string, int length) {
/* 2105 */     if (isEmpty(string)) {
/* 2106 */       return null;
/*      */     }
/* 2108 */     if (length <= 0) {
/* 2109 */       return "";
/*      */     }
/* 2111 */     return sub(string, -length, string.length());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String subWithLength(String input, int fromIndex, int length) {
/* 2124 */     return sub(input, fromIndex, fromIndex + length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String subBefore(CharSequence string, CharSequence separator, boolean isLastSeparator) {
/* 2150 */     if (isEmpty(string) || separator == null) {
/* 2151 */       return (null == string) ? null : string.toString();
/*      */     }
/*      */     
/* 2154 */     String str = string.toString();
/* 2155 */     String sep = separator.toString();
/* 2156 */     if (sep.isEmpty()) {
/* 2157 */       return "";
/*      */     }
/* 2159 */     int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
/* 2160 */     if (-1 == pos) {
/* 2161 */       return str;
/*      */     }
/* 2163 */     if (0 == pos) {
/* 2164 */       return "";
/*      */     }
/* 2166 */     return str.substring(0, pos);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String subBefore(CharSequence string, char separator, boolean isLastSeparator) {
/* 2190 */     if (isEmpty(string)) {
/* 2191 */       return (null == string) ? null : "";
/*      */     }
/*      */     
/* 2194 */     String str = string.toString();
/* 2195 */     int pos = isLastSeparator ? str.lastIndexOf(separator) : str.indexOf(separator);
/* 2196 */     if (-1 == pos) {
/* 2197 */       return str;
/*      */     }
/* 2199 */     if (0 == pos) {
/* 2200 */       return "";
/*      */     }
/* 2202 */     return str.substring(0, pos);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String subAfter(CharSequence string, CharSequence separator, boolean isLastSeparator) {
/* 2228 */     if (isEmpty(string)) {
/* 2229 */       return (null == string) ? null : "";
/*      */     }
/* 2231 */     if (separator == null) {
/* 2232 */       return "";
/*      */     }
/* 2234 */     String str = string.toString();
/* 2235 */     String sep = separator.toString();
/* 2236 */     int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
/* 2237 */     if (-1 == pos || string.length() - 1 == pos) {
/* 2238 */       return "";
/*      */     }
/* 2240 */     return str.substring(pos + separator.length());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String subAfter(CharSequence string, char separator, boolean isLastSeparator) {
/* 2264 */     if (isEmpty(string)) {
/* 2265 */       return (null == string) ? null : "";
/*      */     }
/* 2267 */     String str = string.toString();
/* 2268 */     int pos = isLastSeparator ? str.lastIndexOf(separator) : str.indexOf(separator);
/* 2269 */     if (-1 == pos) {
/* 2270 */       return "";
/*      */     }
/* 2272 */     return str.substring(pos + 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String subBetween(CharSequence str, CharSequence before, CharSequence after) {
/* 2300 */     if (str == null || before == null || after == null) {
/* 2301 */       return null;
/*      */     }
/*      */     
/* 2304 */     String str2 = str.toString();
/* 2305 */     String before2 = before.toString();
/* 2306 */     String after2 = after.toString();
/*      */     
/* 2308 */     int start = str2.indexOf(before2);
/* 2309 */     if (start != -1) {
/* 2310 */       int end = str2.indexOf(after2, start + before2.length());
/* 2311 */       if (end != -1) {
/* 2312 */         return str2.substring(start + before2.length(), end);
/*      */       }
/*      */     } 
/* 2315 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String subBetween(CharSequence str, CharSequence beforeAndAfter) {
/* 2338 */     return subBetween(str, beforeAndAfter, beforeAndAfter);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] subBetweenAll(CharSequence str, CharSequence prefix, CharSequence suffix) {
/* 2368 */     if (hasEmpty(new CharSequence[] { str, prefix, suffix
/*      */         
/* 2370 */         }) || false == contains(str, prefix)) {
/* 2371 */       return new String[0];
/*      */     }
/*      */     
/* 2374 */     List<String> result = new LinkedList<>();
/* 2375 */     String[] split = splitToArray(str, prefix);
/* 2376 */     if (prefix.equals(suffix)) {
/*      */       
/* 2378 */       for (int i = 1, length = split.length - 1; i < length; i += 2) {
/* 2379 */         result.add(split[i]);
/*      */       }
/*      */     }
/*      */     else {
/*      */       
/* 2384 */       for (int i = 1; i < split.length; i++) {
/* 2385 */         String fragment = split[i];
/* 2386 */         int suffixIndex = fragment.indexOf(suffix.toString());
/* 2387 */         if (suffixIndex > 0) {
/* 2388 */           result.add(fragment.substring(0, suffixIndex));
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 2393 */     return result.<String>toArray(new String[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] subBetweenAll(CharSequence str, CharSequence prefixAndSuffix) {
/* 2420 */     return subBetweenAll(str, prefixAndSuffix, prefixAndSuffix);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String repeat(char c, int count) {
/* 2439 */     if (count <= 0) {
/* 2440 */       return "";
/*      */     }
/*      */     
/* 2443 */     char[] result = new char[count];
/* 2444 */     for (int i = 0; i < count; i++) {
/* 2445 */       result[i] = c;
/*      */     }
/* 2447 */     return new String(result);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String repeat(CharSequence str, int count) {
/* 2458 */     if (null == str) {
/* 2459 */       return null;
/*      */     }
/* 2461 */     if (count <= 0 || str.length() == 0) {
/* 2462 */       return "";
/*      */     }
/* 2464 */     if (count == 1) {
/* 2465 */       return str.toString();
/*      */     }
/*      */ 
/*      */     
/* 2469 */     int len = str.length();
/* 2470 */     long longSize = len * count;
/* 2471 */     int size = (int)longSize;
/* 2472 */     if (size != longSize) {
/* 2473 */       throw new ArrayIndexOutOfBoundsException("Required String length is too large: " + longSize);
/*      */     }
/*      */     
/* 2476 */     char[] array = new char[size];
/* 2477 */     str.toString().getChars(0, len, array, 0);
/*      */     int n;
/* 2479 */     for (n = len; n < size - n; n <<= 1) {
/* 2480 */       System.arraycopy(array, 0, array, n, n);
/*      */     }
/* 2482 */     System.arraycopy(array, 0, array, n, size - n);
/* 2483 */     return new String(array);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String repeatByLength(CharSequence str, int padLen) {
/* 2495 */     if (null == str) {
/* 2496 */       return null;
/*      */     }
/* 2498 */     if (padLen <= 0) {
/* 2499 */       return "";
/*      */     }
/* 2501 */     int strLen = str.length();
/* 2502 */     if (strLen == padLen)
/* 2503 */       return str.toString(); 
/* 2504 */     if (strLen > padLen) {
/* 2505 */       return subPre(str, padLen);
/*      */     }
/*      */ 
/*      */     
/* 2509 */     char[] padding = new char[padLen];
/* 2510 */     for (int i = 0; i < padLen; i++) {
/* 2511 */       padding[i] = str.charAt(i % strLen);
/*      */     }
/* 2513 */     return new String(padding);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String repeatAndJoin(CharSequence str, int count, CharSequence delimiter) {
/* 2532 */     if (count <= 0) {
/* 2533 */       return "";
/*      */     }
/* 2535 */     StringBuilder builder = new StringBuilder(str.length() * count);
/* 2536 */     builder.append(str);
/* 2537 */     count--;
/*      */     
/* 2539 */     boolean isAppendDelimiter = isNotEmpty(delimiter);
/* 2540 */     while (count-- > 0) {
/* 2541 */       if (isAppendDelimiter) {
/* 2542 */         builder.append(delimiter);
/*      */       }
/* 2544 */       builder.append(str);
/*      */     } 
/* 2546 */     return builder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean equals(CharSequence str1, CharSequence str2) {
/* 2567 */     return equals(str1, str2, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
/* 2586 */     return equals(str1, str2, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean equals(CharSequence str1, CharSequence str2, boolean ignoreCase) {
/* 2604 */     if (null == str1)
/*      */     {
/* 2606 */       return (str2 == null);
/*      */     }
/* 2608 */     if (null == str2)
/*      */     {
/* 2610 */       return false;
/*      */     }
/*      */     
/* 2613 */     if (ignoreCase) {
/* 2614 */       return str1.toString().equalsIgnoreCase(str2.toString());
/*      */     }
/* 2616 */     return str1.toString().contentEquals(str2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean equalsAnyIgnoreCase(CharSequence str1, CharSequence... strs) {
/* 2630 */     return equalsAny(str1, true, strs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean equalsAny(CharSequence str1, CharSequence... strs) {
/* 2643 */     return equalsAny(str1, false, strs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean equalsAny(CharSequence str1, boolean ignoreCase, CharSequence... strs) {
/* 2657 */     if (ArrayUtil.isEmpty((Object[])strs)) {
/* 2658 */       return false;
/*      */     }
/*      */     
/* 2661 */     for (CharSequence str : strs) {
/* 2662 */       if (equals(str1, str, ignoreCase)) {
/* 2663 */         return true;
/*      */       }
/*      */     } 
/* 2666 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean equalsCharAt(CharSequence str, int position, char c) {
/* 2682 */     if (null == str || position < 0) {
/* 2683 */       return false;
/*      */     }
/* 2685 */     return (str.length() > position && c == str.charAt(position));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isSubEquals(CharSequence str1, int start1, CharSequence str2, boolean ignoreCase) {
/* 2700 */     return isSubEquals(str1, start1, str2, 0, str2.length(), ignoreCase);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isSubEquals(CharSequence str1, int start1, CharSequence str2, int start2, int length, boolean ignoreCase) {
/* 2717 */     if (null == str1 || null == str2) {
/* 2718 */       return false;
/*      */     }
/*      */     
/* 2721 */     return str1.toString().regionMatches(ignoreCase, start1, str2.toString(), start2, length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String format(CharSequence template, Object... params) {
/* 2740 */     if (null == template) {
/* 2741 */       return "null";
/*      */     }
/* 2743 */     if (ArrayUtil.isEmpty(params) || isBlank(template)) {
/* 2744 */       return template.toString();
/*      */     }
/* 2746 */     return StrFormatter.format(template.toString(), params);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String indexedFormat(CharSequence pattern, Object... arguments) {
/* 2758 */     return MessageFormat.format(pattern.toString(), arguments);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] utf8Bytes(CharSequence str) {
/* 2769 */     return bytes(str, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] bytes(CharSequence str) {
/* 2780 */     return bytes(str, Charset.defaultCharset());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] bytes(CharSequence str, String charset) {
/* 2791 */     return bytes(str, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] bytes(CharSequence str, Charset charset) {
/* 2802 */     if (str == null) {
/* 2803 */       return null;
/*      */     }
/*      */     
/* 2806 */     if (null == charset) {
/* 2807 */       return str.toString().getBytes();
/*      */     }
/* 2809 */     return str.toString().getBytes(charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteBuffer byteBuffer(CharSequence str, String charset) {
/* 2820 */     return ByteBuffer.wrap(bytes(str, charset));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String wrap(CharSequence str, CharSequence prefixAndSuffix) {
/* 2835 */     return wrap(str, prefixAndSuffix, prefixAndSuffix);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String wrap(CharSequence str, CharSequence prefix, CharSequence suffix) {
/* 2847 */     return nullToEmpty(prefix).concat(nullToEmpty(str)).concat(nullToEmpty(suffix));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] wrapAllWithPair(CharSequence prefixAndSuffix, CharSequence... strs) {
/* 2859 */     return wrapAll(prefixAndSuffix, prefixAndSuffix, strs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] wrapAll(CharSequence prefix, CharSequence suffix, CharSequence... strs) {
/* 2872 */     String[] results = new String[strs.length];
/* 2873 */     for (int i = 0; i < strs.length; i++) {
/* 2874 */       results[i] = wrap(strs[i], prefix, suffix);
/*      */     }
/* 2876 */     return results;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String wrapIfMissing(CharSequence str, CharSequence prefix, CharSequence suffix) {
/* 2888 */     int len = 0;
/* 2889 */     if (isNotEmpty(str)) {
/* 2890 */       len += str.length();
/*      */     }
/* 2892 */     if (isNotEmpty(prefix)) {
/* 2893 */       len += prefix.length();
/*      */     }
/* 2895 */     if (isNotEmpty(suffix)) {
/* 2896 */       len += suffix.length();
/*      */     }
/* 2898 */     StringBuilder sb = new StringBuilder(len);
/* 2899 */     if (isNotEmpty(prefix) && false == startWith(str, prefix)) {
/* 2900 */       sb.append(prefix);
/*      */     }
/* 2902 */     if (isNotEmpty(str)) {
/* 2903 */       sb.append(str);
/*      */     }
/* 2905 */     if (isNotEmpty(suffix) && false == endWith(str, suffix)) {
/* 2906 */       sb.append(suffix);
/*      */     }
/* 2908 */     return sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] wrapAllWithPairIfMissing(CharSequence prefixAndSuffix, CharSequence... strs) {
/* 2920 */     return wrapAllIfMissing(prefixAndSuffix, prefixAndSuffix, strs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] wrapAllIfMissing(CharSequence prefix, CharSequence suffix, CharSequence... strs) {
/* 2933 */     String[] results = new String[strs.length];
/* 2934 */     for (int i = 0; i < strs.length; i++) {
/* 2935 */       results[i] = wrapIfMissing(strs[i], prefix, suffix);
/*      */     }
/* 2937 */     return results;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String unWrap(CharSequence str, String prefix, String suffix) {
/* 2950 */     if (isWrap(str, prefix, suffix)) {
/* 2951 */       return sub(str, prefix.length(), str.length() - suffix.length());
/*      */     }
/* 2953 */     return str.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String unWrap(CharSequence str, char prefix, char suffix) {
/* 2966 */     if (isEmpty(str)) {
/* 2967 */       return str(str);
/*      */     }
/* 2969 */     if (str.charAt(0) == prefix && str.charAt(str.length() - 1) == suffix) {
/* 2970 */       return sub(str, 1, str.length() - 1);
/*      */     }
/* 2972 */     return str.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String unWrap(CharSequence str, char prefixAndSuffix) {
/* 2984 */     return unWrap(str, prefixAndSuffix, prefixAndSuffix);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isWrap(CharSequence str, String prefix, String suffix) {
/* 2996 */     if (ArrayUtil.hasNull((Object[])new CharSequence[] { str, prefix, suffix })) {
/* 2997 */       return false;
/*      */     }
/* 2999 */     String str2 = str.toString();
/* 3000 */     return (str2.startsWith(prefix) && str2.endsWith(suffix));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isWrap(CharSequence str, String wrapper) {
/* 3011 */     return isWrap(str, wrapper, wrapper);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isWrap(CharSequence str, char wrapper) {
/* 3022 */     return isWrap(str, wrapper, wrapper);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isWrap(CharSequence str, char prefixChar, char suffixChar) {
/* 3034 */     if (null == str) {
/* 3035 */       return false;
/*      */     }
/*      */     
/* 3038 */     return (str.charAt(0) == prefixChar && str.charAt(str.length() - 1) == suffixChar);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String padPre(CharSequence str, int length, CharSequence padStr) {
/* 3060 */     if (null == str) {
/* 3061 */       return null;
/*      */     }
/* 3063 */     int strLen = str.length();
/* 3064 */     if (strLen == length)
/* 3065 */       return str.toString(); 
/* 3066 */     if (strLen > length)
/*      */     {
/* 3068 */       return subPre(str, length);
/*      */     }
/*      */     
/* 3071 */     return repeatByLength(padStr, length - strLen).concat(str.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String padPre(CharSequence str, int length, char padChar) {
/* 3090 */     if (null == str) {
/* 3091 */       return null;
/*      */     }
/* 3093 */     int strLen = str.length();
/* 3094 */     if (strLen == length)
/* 3095 */       return str.toString(); 
/* 3096 */     if (strLen > length)
/*      */     {
/* 3098 */       return subPre(str, length);
/*      */     }
/*      */     
/* 3101 */     return repeat(padChar, length - strLen).concat(str.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String padAfter(CharSequence str, int length, char padChar) {
/* 3120 */     if (null == str) {
/* 3121 */       return null;
/*      */     }
/* 3123 */     int strLen = str.length();
/* 3124 */     if (strLen == length)
/* 3125 */       return str.toString(); 
/* 3126 */     if (strLen > length)
/*      */     {
/* 3128 */       return sub(str, strLen - length, strLen);
/*      */     }
/*      */     
/* 3131 */     return str.toString().concat(repeat(padChar, length - strLen));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String padAfter(CharSequence str, int length, CharSequence padStr) {
/* 3150 */     if (null == str) {
/* 3151 */       return null;
/*      */     }
/* 3153 */     int strLen = str.length();
/* 3154 */     if (strLen == length)
/* 3155 */       return str.toString(); 
/* 3156 */     if (strLen > length)
/*      */     {
/* 3158 */       return subSufByLength(str, length);
/*      */     }
/*      */     
/* 3161 */     return str.toString().concat(repeatByLength(padStr, length - strLen));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String center(CharSequence str, int size) {
/* 3184 */     return center(str, size, ' ');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String center(CharSequence str, int size, char padChar) {
/* 3208 */     if (str == null || size <= 0) {
/* 3209 */       return str(str);
/*      */     }
/* 3211 */     int strLen = str.length();
/* 3212 */     int pads = size - strLen;
/* 3213 */     if (pads <= 0) {
/* 3214 */       return str.toString();
/*      */     }
/* 3216 */     str = padPre(str, strLen + pads / 2, padChar);
/* 3217 */     str = padAfter(str, size, padChar);
/* 3218 */     return str.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String center(CharSequence str, int size, CharSequence padStr) {
/* 3242 */     if (str == null || size <= 0) {
/* 3243 */       return str(str);
/*      */     }
/* 3245 */     if (isEmpty(padStr)) {
/* 3246 */       padStr = " ";
/*      */     }
/* 3248 */     int strLen = str.length();
/* 3249 */     int pads = size - strLen;
/* 3250 */     if (pads <= 0) {
/* 3251 */       return str.toString();
/*      */     }
/* 3253 */     str = padPre(str, strLen + pads / 2, padStr);
/* 3254 */     str = padAfter(str, size, padStr);
/* 3255 */     return str.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String str(CharSequence cs) {
/* 3267 */     return (null == cs) ? null : cs.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int count(CharSequence content, CharSequence strForSearch) {
/* 3291 */     if (hasEmpty(new CharSequence[] { content, strForSearch }) || strForSearch.length() > content.length()) {
/* 3292 */       return 0;
/*      */     }
/*      */     
/* 3295 */     int count = 0;
/* 3296 */     int idx = 0;
/* 3297 */     String content2 = content.toString();
/* 3298 */     String strForSearch2 = strForSearch.toString();
/* 3299 */     while ((idx = content2.indexOf(strForSearch2, idx)) > -1) {
/* 3300 */       count++;
/* 3301 */       idx += strForSearch.length();
/*      */     } 
/* 3303 */     return count;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int count(CharSequence content, char charForSearch) {
/* 3314 */     int count = 0;
/* 3315 */     if (isEmpty(content)) {
/* 3316 */       return 0;
/*      */     }
/* 3318 */     int contentLength = content.length();
/* 3319 */     for (int i = 0; i < contentLength; i++) {
/* 3320 */       if (charForSearch == content.charAt(i)) {
/* 3321 */         count++;
/*      */       }
/*      */     } 
/* 3324 */     return count;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int compare(CharSequence str1, CharSequence str2, boolean nullIsLess) {
/* 3351 */     if (str1 == str2) {
/* 3352 */       return 0;
/*      */     }
/* 3354 */     if (str1 == null) {
/* 3355 */       return nullIsLess ? -1 : 1;
/*      */     }
/* 3357 */     if (str2 == null) {
/* 3358 */       return nullIsLess ? 1 : -1;
/*      */     }
/* 3360 */     return str1.toString().compareTo(str2.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int compareIgnoreCase(CharSequence str1, CharSequence str2, boolean nullIsLess) {
/* 3387 */     if (str1 == str2) {
/* 3388 */       return 0;
/*      */     }
/* 3390 */     if (str1 == null) {
/* 3391 */       return nullIsLess ? -1 : 1;
/*      */     }
/* 3393 */     if (str2 == null) {
/* 3394 */       return nullIsLess ? 1 : -1;
/*      */     }
/* 3396 */     return str1.toString().compareToIgnoreCase(str2.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int compareVersion(CharSequence version1, CharSequence version2) {
/* 3420 */     return VersionComparator.INSTANCE.compare(str(version1), str(version2));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String appendIfMissing(CharSequence str, CharSequence suffix, CharSequence... suffixes) {
/* 3436 */     return appendIfMissing(str, suffix, false, suffixes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String appendIfMissingIgnoreCase(CharSequence str, CharSequence suffix, CharSequence... suffixes) {
/* 3450 */     return appendIfMissing(str, suffix, true, suffixes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String appendIfMissing(CharSequence str, CharSequence suffix, boolean ignoreCase, CharSequence... testSuffixes) {
/* 3464 */     if (str == null || isEmpty(suffix) || endWith(str, suffix, ignoreCase)) {
/* 3465 */       return str(str);
/*      */     }
/* 3467 */     if (ArrayUtil.isNotEmpty((Object[])testSuffixes)) {
/* 3468 */       for (CharSequence testSuffix : testSuffixes) {
/* 3469 */         if (endWith(str, testSuffix, ignoreCase)) {
/* 3470 */           return str.toString();
/*      */         }
/*      */       } 
/*      */     }
/* 3474 */     return str.toString().concat(suffix.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String prependIfMissing(CharSequence str, CharSequence prefix, CharSequence... prefixes) {
/* 3488 */     return prependIfMissing(str, prefix, false, prefixes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String prependIfMissingIgnoreCase(CharSequence str, CharSequence prefix, CharSequence... prefixes) {
/* 3502 */     return prependIfMissing(str, prefix, true, prefixes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String prependIfMissing(CharSequence str, CharSequence prefix, boolean ignoreCase, CharSequence... prefixes) {
/* 3516 */     if (str == null || isEmpty(prefix) || startWith(str, prefix, ignoreCase)) {
/* 3517 */       return str(str);
/*      */     }
/* 3519 */     if (prefixes != null && prefixes.length > 0) {
/* 3520 */       for (CharSequence s : prefixes) {
/* 3521 */         if (startWith(str, s, ignoreCase)) {
/* 3522 */           return str.toString();
/*      */         }
/*      */       } 
/*      */     }
/* 3526 */     return prefix.toString().concat(str.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String replaceIgnoreCase(CharSequence str, CharSequence searchStr, CharSequence replacement) {
/* 3541 */     return replace(str, 0, searchStr, replacement, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String replace(CharSequence str, CharSequence searchStr, CharSequence replacement) {
/* 3554 */     return replace(str, 0, searchStr, replacement, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String replace(CharSequence str, CharSequence searchStr, CharSequence replacement, boolean ignoreCase) {
/* 3568 */     return replace(str, 0, searchStr, replacement, ignoreCase);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String replace(CharSequence str, int fromIndex, CharSequence searchStr, CharSequence replacement, boolean ignoreCase) {
/* 3583 */     if (isEmpty(str) || isEmpty(searchStr)) {
/* 3584 */       return str(str);
/*      */     }
/* 3586 */     if (null == replacement) {
/* 3587 */       replacement = "";
/*      */     }
/*      */     
/* 3590 */     int strLength = str.length();
/* 3591 */     int searchStrLength = searchStr.length();
/* 3592 */     if (strLength < searchStrLength)
/*      */     {
/* 3594 */       return str(str);
/*      */     }
/*      */     
/* 3597 */     if (fromIndex > strLength)
/* 3598 */       return str(str); 
/* 3599 */     if (fromIndex < 0) {
/* 3600 */       fromIndex = 0;
/*      */     }
/*      */     
/* 3603 */     StringBuilder result = new StringBuilder(strLength - searchStrLength + replacement.length());
/* 3604 */     if (0 != fromIndex) {
/* 3605 */       result.append(str.subSequence(0, fromIndex));
/*      */     }
/*      */     
/* 3608 */     int preIndex = fromIndex;
/*      */     int index;
/* 3610 */     while ((index = indexOf(str, searchStr, preIndex, ignoreCase)) > -1) {
/* 3611 */       result.append(str.subSequence(preIndex, index));
/* 3612 */       result.append(replacement);
/* 3613 */       preIndex = index + searchStrLength;
/*      */     } 
/*      */     
/* 3616 */     if (preIndex < strLength)
/*      */     {
/* 3618 */       result.append(str.subSequence(preIndex, strLength));
/*      */     }
/* 3620 */     return result.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String replace(CharSequence str, int startInclude, int endExclude, char replacedChar) {
/* 3635 */     if (isEmpty(str)) {
/* 3636 */       return str(str);
/*      */     }
/* 3638 */     String originalStr = str(str);
/* 3639 */     int[] strCodePoints = originalStr.codePoints().toArray();
/* 3640 */     int strLength = strCodePoints.length;
/* 3641 */     if (startInclude > strLength) {
/* 3642 */       return originalStr;
/*      */     }
/* 3644 */     if (endExclude > strLength) {
/* 3645 */       endExclude = strLength;
/*      */     }
/* 3647 */     if (startInclude > endExclude)
/*      */     {
/* 3649 */       return originalStr;
/*      */     }
/*      */     
/* 3652 */     StringBuilder stringBuilder = new StringBuilder();
/* 3653 */     for (int i = 0; i < strLength; i++) {
/* 3654 */       if (i >= startInclude && i < endExclude) {
/* 3655 */         stringBuilder.append(replacedChar);
/*      */       } else {
/* 3657 */         stringBuilder.append(new String(strCodePoints, i, 1));
/*      */       } 
/*      */     } 
/* 3660 */     return stringBuilder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String replace(CharSequence str, int startInclude, int endExclude, CharSequence replacedStr) {
/* 3675 */     if (isEmpty(str)) {
/* 3676 */       return str(str);
/*      */     }
/* 3678 */     String originalStr = str(str);
/* 3679 */     int[] strCodePoints = originalStr.codePoints().toArray();
/* 3680 */     int strLength = strCodePoints.length;
/* 3681 */     if (startInclude > strLength) {
/* 3682 */       return originalStr;
/*      */     }
/* 3684 */     if (endExclude > strLength) {
/* 3685 */       endExclude = strLength;
/*      */     }
/* 3687 */     if (startInclude > endExclude)
/*      */     {
/* 3689 */       return originalStr;
/*      */     }
/*      */     
/* 3692 */     StringBuilder stringBuilder = new StringBuilder(); int i;
/* 3693 */     for (i = 0; i < startInclude; i++) {
/* 3694 */       stringBuilder.append(new String(strCodePoints, i, 1));
/*      */     }
/* 3696 */     stringBuilder.append(replacedStr);
/* 3697 */     for (i = endExclude; i < strLength; i++) {
/* 3698 */       stringBuilder.append(new String(strCodePoints, i, 1));
/*      */     }
/* 3700 */     return stringBuilder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String replace(CharSequence str, Pattern pattern, Func1<Matcher, String> replaceFun) {
/* 3720 */     return ReUtil.replaceAll(str, pattern, replaceFun);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String replace(CharSequence str, String regex, Func1<Matcher, String> replaceFun) {
/* 3734 */     return ReUtil.replaceAll(str, regex, replaceFun);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String hide(CharSequence str, int startInclude, int endExclude) {
/* 3758 */     return replace(str, startInclude, endExclude, '*');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String desensitized(CharSequence str, DesensitizedUtil.DesensitizedType desensitizedType) {
/* 3785 */     return DesensitizedUtil.desensitized(str, desensitizedType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String replaceChars(CharSequence str, String chars, CharSequence replacedStr) {
/* 3799 */     if (isEmpty(str) || isEmpty(chars)) {
/* 3800 */       return str(str);
/*      */     }
/* 3802 */     return replaceChars(str, chars.toCharArray(), replacedStr);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String replaceChars(CharSequence str, char[] chars, CharSequence replacedStr) {
/* 3815 */     if (isEmpty(str) || ArrayUtil.isEmpty(chars)) {
/* 3816 */       return str(str);
/*      */     }
/*      */     
/* 3819 */     Set<Character> set = new HashSet<>(chars.length);
/* 3820 */     for (char c : chars) {
/* 3821 */       set.add(Character.valueOf(c));
/*      */     }
/* 3823 */     int strLen = str.length();
/* 3824 */     StringBuilder builder = new StringBuilder();
/*      */     
/* 3826 */     for (int i = 0; i < strLen; i++) {
/* 3827 */       char c = str.charAt(i);
/* 3828 */       builder.append(set.contains(Character.valueOf(c)) ? replacedStr : Character.valueOf(c));
/*      */     } 
/* 3830 */     return builder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int length(CharSequence cs) {
/* 3843 */     return (cs == null) ? 0 : cs.length();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int byteLength(CharSequence cs, Charset charset) {
/* 3855 */     return (cs == null) ? 0 : (cs.toString().getBytes(charset)).length;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int totalLength(CharSequence... strs) {
/* 3867 */     int totalLength = 0;
/* 3868 */     for (CharSequence str : strs) {
/* 3869 */       totalLength += (null == str) ? 0 : str.length();
/*      */     }
/* 3871 */     return totalLength;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String maxLength(CharSequence string, int length) {
/* 3883 */     Assert.isTrue((length > 0));
/* 3884 */     if (null == string) {
/* 3885 */       return null;
/*      */     }
/* 3887 */     if (string.length() <= length) {
/* 3888 */       return string.toString();
/*      */     }
/* 3890 */     return sub(string, 0, length) + "...";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T firstNonNull(T... strs) {
/* 3905 */     return (T)ArrayUtil.firstNonNull((Object[])strs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T firstNonEmpty(T... strs) {
/* 3919 */     return (T)ArrayUtil.firstMatch(CharSequenceUtil::isNotEmpty, (Object[])strs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T firstNonBlank(T... strs) {
/* 3933 */     return (T)ArrayUtil.firstMatch(CharSequenceUtil::isNotBlank, (Object[])strs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String upperFirstAndAddPre(CharSequence str, String preString) {
/* 3946 */     if (str == null || preString == null) {
/* 3947 */       return null;
/*      */     }
/* 3949 */     return preString + upperFirst(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String upperFirst(CharSequence str) {
/* 3960 */     if (null == str) {
/* 3961 */       return null;
/*      */     }
/* 3963 */     if (str.length() > 0) {
/* 3964 */       char firstChar = str.charAt(0);
/* 3965 */       if (Character.isLowerCase(firstChar)) {
/* 3966 */         return Character.toUpperCase(firstChar) + subSuf(str, 1);
/*      */       }
/*      */     } 
/* 3969 */     return str.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String lowerFirst(CharSequence str) {
/* 3980 */     if (null == str) {
/* 3981 */       return null;
/*      */     }
/* 3983 */     if (str.length() > 0) {
/* 3984 */       char firstChar = str.charAt(0);
/* 3985 */       if (Character.isUpperCase(firstChar)) {
/* 3986 */         return Character.toLowerCase(firstChar) + subSuf(str, 1);
/*      */       }
/*      */     } 
/* 3989 */     return str.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String filter(CharSequence str, Filter<Character> filter) {
/* 4003 */     if (str == null || filter == null) {
/* 4004 */       return str(str);
/*      */     }
/*      */     
/* 4007 */     int len = str.length();
/* 4008 */     StringBuilder sb = new StringBuilder(len);
/*      */     
/* 4010 */     for (int i = 0; i < len; i++) {
/* 4011 */       char c = str.charAt(i);
/* 4012 */       if (filter.accept(Character.valueOf(c))) {
/* 4013 */         sb.append(c);
/*      */       }
/*      */     } 
/* 4016 */     return sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isUpperCase(CharSequence str) {
/* 4034 */     if (null == str) {
/* 4035 */       return false;
/*      */     }
/* 4037 */     int len = str.length();
/* 4038 */     for (int i = 0; i < len; i++) {
/* 4039 */       if (Character.isLowerCase(str.charAt(i))) {
/* 4040 */         return false;
/*      */       }
/*      */     } 
/* 4043 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isLowerCase(CharSequence str) {
/* 4059 */     if (null == str) {
/* 4060 */       return false;
/*      */     }
/* 4062 */     int len = str.length();
/* 4063 */     for (int i = 0; i < len; i++) {
/* 4064 */       if (Character.isUpperCase(str.charAt(i))) {
/* 4065 */         return false;
/*      */       }
/*      */     } 
/* 4068 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String swapCase(String str) {
/* 4085 */     if (isEmpty(str)) {
/* 4086 */       return str;
/*      */     }
/*      */     
/* 4089 */     char[] buffer = str.toCharArray();
/*      */     
/* 4091 */     for (int i = 0; i < buffer.length; i++) {
/* 4092 */       char ch = buffer[i];
/* 4093 */       if (Character.isUpperCase(ch)) {
/* 4094 */         buffer[i] = Character.toLowerCase(ch);
/* 4095 */       } else if (Character.isTitleCase(ch)) {
/* 4096 */         buffer[i] = Character.toLowerCase(ch);
/* 4097 */       } else if (Character.isLowerCase(ch)) {
/* 4098 */         buffer[i] = Character.toUpperCase(ch);
/*      */       } 
/*      */     } 
/* 4101 */     return new String(buffer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toUnderlineCase(CharSequence str) {
/* 4119 */     return NamingCase.toUnderlineCase(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toSymbolCase(CharSequence str, char symbol) {
/* 4132 */     return NamingCase.toSymbolCase(str, symbol);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toCamelCase(CharSequence name) {
/* 4144 */     return NamingCase.toCamelCase(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toCamelCase(CharSequence name, char symbol) {
/* 4157 */     return NamingCase.toCamelCase(name, symbol);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isSurround(CharSequence str, CharSequence prefix, CharSequence suffix) {
/* 4171 */     if (StrUtil.isBlank(str)) {
/* 4172 */       return false;
/*      */     }
/* 4174 */     if (str.length() < prefix.length() + suffix.length()) {
/* 4175 */       return false;
/*      */     }
/*      */     
/* 4178 */     String str2 = str.toString();
/* 4179 */     return (str2.startsWith(prefix.toString()) && str2.endsWith(suffix.toString()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isSurround(CharSequence str, char prefix, char suffix) {
/* 4191 */     if (StrUtil.isBlank(str)) {
/* 4192 */       return false;
/*      */     }
/* 4194 */     if (str.length() < 2) {
/* 4195 */       return false;
/*      */     }
/*      */     
/* 4198 */     return (str.charAt(0) == prefix && str.charAt(str.length() - 1) == suffix);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static StringBuilder builder(CharSequence... strs) {
/* 4210 */     StringBuilder sb = new StringBuilder();
/* 4211 */     for (CharSequence str : strs) {
/* 4212 */       sb.append(str);
/*      */     }
/* 4214 */     return sb;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static StrBuilder strBuilder(CharSequence... strs) {
/* 4224 */     return StrBuilder.create(strs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getGeneralField(CharSequence getOrSetMethodName) {
/* 4243 */     String getOrSetMethodNameStr = getOrSetMethodName.toString();
/* 4244 */     if (getOrSetMethodNameStr.startsWith("get") || getOrSetMethodNameStr.startsWith("set"))
/* 4245 */       return removePreAndLowerFirst(getOrSetMethodName, 3); 
/* 4246 */     if (getOrSetMethodNameStr.startsWith("is")) {
/* 4247 */       return removePreAndLowerFirst(getOrSetMethodName, 2);
/*      */     }
/* 4249 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String genSetter(CharSequence fieldName) {
/* 4260 */     return upperFirstAndAddPre(fieldName, "set");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String genGetter(CharSequence fieldName) {
/* 4270 */     return upperFirstAndAddPre(fieldName, "get");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String concat(boolean isNullToEmpty, CharSequence... strs) {
/* 4284 */     StrBuilder sb = new StrBuilder();
/* 4285 */     for (CharSequence str : strs) {
/* 4286 */       sb.append(isNullToEmpty ? nullToEmpty(str) : str);
/*      */     }
/* 4288 */     return sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String brief(CharSequence str, int maxLength) {
/* 4313 */     if (null == str) {
/* 4314 */       return null;
/*      */     }
/* 4316 */     int strLength = str.length();
/* 4317 */     if (maxLength <= 0 || strLength <= maxLength) {
/* 4318 */       return str.toString();
/*      */     }
/*      */ 
/*      */     
/* 4322 */     switch (maxLength) {
/*      */       case 1:
/* 4324 */         return String.valueOf(str.charAt(0));
/*      */       case 2:
/* 4326 */         return str.charAt(0) + ".";
/*      */       case 3:
/* 4328 */         return str.charAt(0) + "." + str.charAt(strLength - 1);
/*      */       case 4:
/* 4330 */         return str.charAt(0) + ".." + str.charAt(strLength - 1);
/*      */     } 
/*      */     
/* 4333 */     int suffixLength = (maxLength - 3) / 2;
/* 4334 */     int preLength = suffixLength + (maxLength - 3) % 2;
/* 4335 */     String str2 = str.toString();
/* 4336 */     return format("{}...{}", new Object[] { str2
/* 4337 */           .substring(0, preLength), str2
/* 4338 */           .substring(strLength - suffixLength) });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String join(CharSequence conjunction, Object... objs) {
/* 4350 */     return ArrayUtil.join(objs, conjunction);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> String join(CharSequence conjunction, Iterable<T> iterable) {
/* 4364 */     return CollUtil.join(iterable, conjunction);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isAllCharMatch(CharSequence value, Matcher<Character> matcher) {
/* 4376 */     if (StrUtil.isBlank(value)) {
/* 4377 */       return false;
/*      */     }
/* 4379 */     for (int i = value.length(); --i >= 0;) {
/* 4380 */       if (false == matcher.match(Character.valueOf(value.charAt(i)))) {
/* 4381 */         return false;
/*      */       }
/*      */     } 
/* 4384 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNumeric(CharSequence str) {
/* 4395 */     return isAllCharMatch(str, Character::isDigit);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String move(CharSequence str, int startInclude, int endExclude, int moveLength) {
/* 4411 */     if (isEmpty(str)) {
/* 4412 */       return str(str);
/*      */     }
/* 4414 */     int len = str.length();
/* 4415 */     if (Math.abs(moveLength) > len)
/*      */     {
/* 4417 */       moveLength %= len;
/*      */     }
/* 4419 */     StringBuilder strBuilder = new StringBuilder(len);
/* 4420 */     if (moveLength > 0) {
/* 4421 */       int endAfterMove = Math.min(endExclude + moveLength, str.length());
/* 4422 */       strBuilder.append(str.subSequence(0, startInclude))
/* 4423 */         .append(str.subSequence(endExclude, endAfterMove))
/* 4424 */         .append(str.subSequence(startInclude, endExclude))
/* 4425 */         .append(str.subSequence(endAfterMove, str.length()));
/* 4426 */     } else if (moveLength < 0) {
/* 4427 */       int startAfterMove = Math.max(startInclude + moveLength, 0);
/* 4428 */       strBuilder.append(str.subSequence(0, startAfterMove))
/* 4429 */         .append(str.subSequence(startInclude, endExclude))
/* 4430 */         .append(str.subSequence(startAfterMove, startInclude))
/* 4431 */         .append(str.subSequence(endExclude, str.length()));
/*      */     } else {
/* 4433 */       return str(str);
/*      */     } 
/* 4435 */     return strBuilder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isCharEquals(CharSequence str) {
/* 4446 */     Assert.notEmpty(str, "Str to check must be not empty!", new Object[0]);
/* 4447 */     return (count(str, str.charAt(0)) == str.length());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String normalize(CharSequence str) {
/* 4460 */     return Normalizer.normalize(str, Normalizer.Form.NFC);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String fixLength(CharSequence str, char fixedChar, int length) {
/* 4474 */     int fixedLength = length - str.length();
/* 4475 */     if (fixedLength <= 0) {
/* 4476 */       return str.toString();
/*      */     }
/* 4478 */     return str + repeat(fixedChar, fixedLength);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\CharSequenceUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */