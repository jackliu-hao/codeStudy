/*      */ package org.codehaus.plexus.util;
/*      */ 
/*      */ import java.util.Arrays;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class StringUtils
/*      */ {
/*      */   public static String clean(String str) {
/*  113 */     return (str == null) ? "" : str.trim();
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
/*      */   public static String trim(String str) {
/*  127 */     return (str == null) ? null : str.trim();
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
/*      */   public static String deleteWhitespace(String str) {
/*  142 */     StringBuffer buffer = new StringBuffer();
/*  143 */     int sz = str.length();
/*  144 */     for (int i = 0; i < sz; i++) {
/*      */       
/*  146 */       if (!Character.isWhitespace(str.charAt(i)))
/*      */       {
/*  148 */         buffer.append(str.charAt(i));
/*      */       }
/*      */     } 
/*  151 */     return buffer.toString();
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
/*      */   public static boolean isNotEmpty(String str) {
/*  163 */     return (str != null && str.length() > 0);
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
/*      */   public static boolean isEmpty(String str) {
/*  179 */     return (str == null || str.trim().length() == 0);
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
/*      */   public static boolean isBlank(String str) {
/*      */     int strLen;
/*  202 */     if (str == null || (strLen = str.length()) == 0)
/*      */     {
/*  204 */       return true;
/*      */     }
/*  206 */     for (int i = 0; i < strLen; i++) {
/*      */       
/*  208 */       if (!Character.isWhitespace(str.charAt(i)))
/*      */       {
/*  210 */         return false;
/*      */       }
/*      */     } 
/*  213 */     return true;
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
/*      */   public static boolean isNotBlank(String str) {
/*  235 */     return !isBlank(str);
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
/*      */   public static boolean equals(String str1, String str2) {
/*  255 */     return (str1 == null) ? ((str2 == null)) : str1.equals(str2);
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
/*      */   public static boolean equalsIgnoreCase(String str1, String str2) {
/*  273 */     return (str1 == null) ? ((str2 == null)) : str1.equalsIgnoreCase(str2);
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
/*      */   public static int indexOfAny(String str, String[] searchStrs) {
/*  288 */     if (str == null || searchStrs == null)
/*      */     {
/*  290 */       return -1;
/*      */     }
/*  292 */     int sz = searchStrs.length;
/*      */ 
/*      */     
/*  295 */     int ret = Integer.MAX_VALUE;
/*      */     
/*  297 */     int tmp = 0;
/*  298 */     for (int i = 0; i < sz; i++) {
/*      */       
/*  300 */       tmp = str.indexOf(searchStrs[i]);
/*  301 */       if (tmp != -1)
/*      */       {
/*      */ 
/*      */ 
/*      */         
/*  306 */         if (tmp < ret)
/*      */         {
/*  308 */           ret = tmp;
/*      */         }
/*      */       }
/*      */     } 
/*  312 */     return (ret == Integer.MAX_VALUE) ? -1 : ret;
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
/*      */   public static int lastIndexOfAny(String str, String[] searchStrs) {
/*  327 */     if (str == null || searchStrs == null)
/*      */     {
/*  329 */       return -1;
/*      */     }
/*  331 */     int sz = searchStrs.length;
/*  332 */     int ret = -1;
/*  333 */     int tmp = 0;
/*  334 */     for (int i = 0; i < sz; i++) {
/*      */       
/*  336 */       tmp = str.lastIndexOf(searchStrs[i]);
/*  337 */       if (tmp > ret)
/*      */       {
/*  339 */         ret = tmp;
/*      */       }
/*      */     } 
/*  342 */     return ret;
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
/*      */   public static String substring(String str, int start) {
/*  361 */     if (str == null)
/*      */     {
/*  363 */       return null;
/*      */     }
/*      */ 
/*      */     
/*  367 */     if (start < 0)
/*      */     {
/*  369 */       start = str.length() + start;
/*      */     }
/*      */     
/*  372 */     if (start < 0)
/*      */     {
/*  374 */       start = 0;
/*      */     }
/*  376 */     if (start > str.length())
/*      */     {
/*  378 */       return "";
/*      */     }
/*      */     
/*  381 */     return str.substring(start);
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
/*      */   public static String substring(String str, int start, int end) {
/*  399 */     if (str == null)
/*      */     {
/*  401 */       return null;
/*      */     }
/*      */ 
/*      */     
/*  405 */     if (end < 0)
/*      */     {
/*  407 */       end = str.length() + end;
/*      */     }
/*  409 */     if (start < 0)
/*      */     {
/*  411 */       start = str.length() + start;
/*      */     }
/*      */ 
/*      */     
/*  415 */     if (end > str.length())
/*      */     {
/*      */       
/*  418 */       end = str.length();
/*      */     }
/*      */ 
/*      */     
/*  422 */     if (start > end)
/*      */     {
/*  424 */       return "";
/*      */     }
/*      */     
/*  427 */     if (start < 0)
/*      */     {
/*  429 */       start = 0;
/*      */     }
/*  431 */     if (end < 0)
/*      */     {
/*  433 */       end = 0;
/*      */     }
/*      */     
/*  436 */     return str.substring(start, end);
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
/*      */   public static String left(String str, int len) {
/*  453 */     if (len < 0)
/*      */     {
/*  455 */       throw new IllegalArgumentException("Requested String length " + len + " is less than zero");
/*      */     }
/*  457 */     if (str == null || str.length() <= len)
/*      */     {
/*  459 */       return str;
/*      */     }
/*      */ 
/*      */     
/*  463 */     return str.substring(0, len);
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
/*      */   public static String right(String str, int len) {
/*  481 */     if (len < 0)
/*      */     {
/*  483 */       throw new IllegalArgumentException("Requested String length " + len + " is less than zero");
/*      */     }
/*  485 */     if (str == null || str.length() <= len)
/*      */     {
/*  487 */       return str;
/*      */     }
/*      */ 
/*      */     
/*  491 */     return str.substring(str.length() - len);
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
/*      */   public static String mid(String str, int pos, int len) {
/*  511 */     if (pos < 0 || (str != null && pos > str.length()))
/*      */     {
/*      */       
/*  514 */       throw new StringIndexOutOfBoundsException("String index " + pos + " is out of bounds");
/*      */     }
/*  516 */     if (len < 0)
/*      */     {
/*  518 */       throw new IllegalArgumentException("Requested String length " + len + " is less than zero");
/*      */     }
/*  520 */     if (str == null)
/*      */     {
/*  522 */       return null;
/*      */     }
/*  524 */     if (str.length() <= pos + len)
/*      */     {
/*  526 */       return str.substring(pos);
/*      */     }
/*      */ 
/*      */     
/*  530 */     return str.substring(pos, pos + len);
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
/*      */   public static String[] split(String str) {
/*  548 */     return split(str, null, -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] split(String text, String separator) {
/*  556 */     return split(text, separator, -1);
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
/*      */   public static String[] split(String str, String separator, int max) {
/*  579 */     StringTokenizer tok = null;
/*  580 */     if (separator == null) {
/*      */ 
/*      */ 
/*      */       
/*  584 */       tok = new StringTokenizer(str);
/*      */     }
/*      */     else {
/*      */       
/*  588 */       tok = new StringTokenizer(str, separator);
/*      */     } 
/*      */     
/*  591 */     int listSize = tok.countTokens();
/*  592 */     if (max > 0 && listSize > max)
/*      */     {
/*  594 */       listSize = max;
/*      */     }
/*      */     
/*  597 */     String[] list = new String[listSize];
/*  598 */     int i = 0;
/*  599 */     int lastTokenBegin = 0;
/*  600 */     int lastTokenEnd = 0;
/*  601 */     while (tok.hasMoreTokens()) {
/*      */       
/*  603 */       if (max > 0 && i == listSize - 1) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  608 */         String endToken = tok.nextToken();
/*  609 */         lastTokenBegin = str.indexOf(endToken, lastTokenEnd);
/*  610 */         list[i] = str.substring(lastTokenBegin);
/*      */         
/*      */         break;
/*      */       } 
/*      */       
/*  615 */       list[i] = tok.nextToken();
/*  616 */       lastTokenBegin = str.indexOf(list[i], lastTokenEnd);
/*  617 */       lastTokenEnd = lastTokenBegin + list[i].length();
/*      */       
/*  619 */       i++;
/*      */     } 
/*  621 */     return list;
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
/*      */   public static String concatenate(Object[] array) {
/*  636 */     return join(array, "");
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
/*      */   public static String join(Object[] array, String separator) {
/*  652 */     if (separator == null)
/*      */     {
/*  654 */       separator = "";
/*      */     }
/*  656 */     int arraySize = array.length;
/*  657 */     int bufSize = (arraySize == 0) ? 0 : ((array[0].toString().length() + separator.length()) * arraySize);
/*      */     
/*  659 */     StringBuffer buf = new StringBuffer(bufSize);
/*      */     
/*  661 */     for (int i = 0; i < arraySize; i++) {
/*      */       
/*  663 */       if (i > 0)
/*      */       {
/*  665 */         buf.append(separator);
/*      */       }
/*  667 */       buf.append(array[i]);
/*      */     } 
/*  669 */     return buf.toString();
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
/*      */   public static String join(Iterator iterator, String separator) {
/*  685 */     if (separator == null)
/*      */     {
/*  687 */       separator = "";
/*      */     }
/*  689 */     StringBuffer buf = new StringBuffer(256);
/*  690 */     while (iterator.hasNext()) {
/*      */       
/*  692 */       buf.append(iterator.next());
/*  693 */       if (iterator.hasNext())
/*      */       {
/*  695 */         buf.append(separator);
/*      */       }
/*      */     } 
/*  698 */     return buf.toString();
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
/*      */   public static String replaceOnce(String text, char repl, char with) {
/*  719 */     return replace(text, repl, with, 1);
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
/*      */   public static String replace(String text, char repl, char with) {
/*  735 */     return replace(text, repl, with, -1);
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
/*      */   public static String replace(String text, char repl, char with, int max) {
/*  752 */     return replace(text, String.valueOf(repl), String.valueOf(with), max);
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
/*      */   public static String replaceOnce(String text, String repl, String with) {
/*  768 */     return replace(text, repl, with, 1);
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
/*      */   public static String replace(String text, String repl, String with) {
/*  784 */     return replace(text, repl, with, -1);
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
/*      */   public static String replace(String text, String repl, String with, int max) {
/*  801 */     if (text == null || repl == null || with == null || repl.length() == 0)
/*      */     {
/*  803 */       return text;
/*      */     }
/*      */     
/*  806 */     StringBuffer buf = new StringBuffer(text.length());
/*  807 */     int start = 0, end = 0;
/*  808 */     while ((end = text.indexOf(repl, start)) != -1) {
/*      */       
/*  810 */       buf.append(text.substring(start, end)).append(with);
/*  811 */       start = end + repl.length();
/*      */       
/*  813 */       if (--max == 0) {
/*      */         break;
/*      */       }
/*      */     } 
/*      */     
/*  818 */     buf.append(text.substring(start));
/*  819 */     return buf.toString();
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
/*      */   public static String overlayString(String text, String overlay, int start, int end) {
/*  834 */     return (new StringBuffer(start + overlay.length() + text.length() - end + 1)).append(text.substring(0, start)).append(overlay).append(text.substring(end)).toString();
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
/*      */   public static String center(String str, int size) {
/*  857 */     return center(str, size, " ");
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
/*      */   public static String center(String str, int size, String delim) {
/*  874 */     int sz = str.length();
/*  875 */     int p = size - sz;
/*  876 */     if (p < 1)
/*      */     {
/*  878 */       return str;
/*      */     }
/*  880 */     str = leftPad(str, sz + p / 2, delim);
/*  881 */     str = rightPad(str, size, delim);
/*  882 */     return str;
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
/*      */   public static String chomp(String str) {
/*  897 */     return chomp(str, "\n");
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
/*      */   public static String chomp(String str, String sep) {
/*  911 */     int idx = str.lastIndexOf(sep);
/*  912 */     if (idx != -1)
/*      */     {
/*  914 */       return str.substring(0, idx);
/*      */     }
/*      */ 
/*      */     
/*  918 */     return str;
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
/*      */   public static String chompLast(String str) {
/*  932 */     return chompLast(str, "\n");
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
/*      */   public static String chompLast(String str, String sep) {
/*  945 */     if (str.length() == 0)
/*      */     {
/*  947 */       return str;
/*      */     }
/*  949 */     String sub = str.substring(str.length() - sep.length());
/*  950 */     if (sep.equals(sub))
/*      */     {
/*  952 */       return str.substring(0, str.length() - sep.length());
/*      */     }
/*      */ 
/*      */     
/*  956 */     return str;
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
/*      */   public static String getChomp(String str, String sep) {
/*  971 */     int idx = str.lastIndexOf(sep);
/*  972 */     if (idx == str.length() - sep.length())
/*      */     {
/*  974 */       return sep;
/*      */     }
/*  976 */     if (idx != -1)
/*      */     {
/*  978 */       return str.substring(idx);
/*      */     }
/*      */ 
/*      */     
/*  982 */     return "";
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
/*      */   public static String prechomp(String str, String sep) {
/*  997 */     int idx = str.indexOf(sep);
/*  998 */     if (idx != -1)
/*      */     {
/* 1000 */       return str.substring(idx + sep.length());
/*      */     }
/*      */ 
/*      */     
/* 1004 */     return str;
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
/*      */   public static String getPrechomp(String str, String sep) {
/* 1019 */     int idx = str.indexOf(sep);
/* 1020 */     if (idx != -1)
/*      */     {
/* 1022 */       return str.substring(0, idx + sep.length());
/*      */     }
/*      */ 
/*      */     
/* 1026 */     return "";
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
/*      */   public static String chop(String str) {
/* 1045 */     if ("".equals(str))
/*      */     {
/* 1047 */       return "";
/*      */     }
/* 1049 */     if (str.length() == 1)
/*      */     {
/* 1051 */       return "";
/*      */     }
/* 1053 */     int lastIdx = str.length() - 1;
/* 1054 */     String ret = str.substring(0, lastIdx);
/* 1055 */     char last = str.charAt(lastIdx);
/* 1056 */     if (last == '\n')
/*      */     {
/* 1058 */       if (ret.charAt(lastIdx - 1) == '\r')
/*      */       {
/* 1060 */         return ret.substring(0, lastIdx - 1);
/*      */       }
/*      */     }
/* 1063 */     return ret;
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
/*      */   public static String chopNewline(String str) {
/* 1076 */     int lastIdx = str.length() - 1;
/* 1077 */     char last = str.charAt(lastIdx);
/* 1078 */     if (last == '\n') {
/*      */       
/* 1080 */       if (str.charAt(lastIdx - 1) == '\r')
/*      */       {
/* 1082 */         lastIdx--;
/*      */       }
/*      */     }
/*      */     else {
/*      */       
/* 1087 */       lastIdx++;
/*      */     } 
/* 1089 */     return str.substring(0, lastIdx);
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
/*      */   public static String escape(String str) {
/* 1111 */     int sz = str.length();
/* 1112 */     StringBuffer buffer = new StringBuffer(2 * sz);
/* 1113 */     for (int i = 0; i < sz; i++) {
/*      */       
/* 1115 */       char ch = str.charAt(i);
/*      */ 
/*      */       
/* 1118 */       if (ch > '࿿') {
/*      */         
/* 1120 */         buffer.append("\\u" + Integer.toHexString(ch));
/*      */       }
/* 1122 */       else if (ch > 'ÿ') {
/*      */         
/* 1124 */         buffer.append("\\u0" + Integer.toHexString(ch));
/*      */       }
/* 1126 */       else if (ch > '') {
/*      */         
/* 1128 */         buffer.append("\\u00" + Integer.toHexString(ch));
/*      */       }
/* 1130 */       else if (ch < ' ') {
/*      */         
/* 1132 */         switch (ch) {
/*      */           
/*      */           case '\b':
/* 1135 */             buffer.append('\\');
/* 1136 */             buffer.append('b');
/*      */             break;
/*      */           case '\n':
/* 1139 */             buffer.append('\\');
/* 1140 */             buffer.append('n');
/*      */             break;
/*      */           case '\t':
/* 1143 */             buffer.append('\\');
/* 1144 */             buffer.append('t');
/*      */             break;
/*      */           case '\f':
/* 1147 */             buffer.append('\\');
/* 1148 */             buffer.append('f');
/*      */             break;
/*      */           case '\r':
/* 1151 */             buffer.append('\\');
/* 1152 */             buffer.append('r');
/*      */             break;
/*      */           default:
/* 1155 */             if (ch > '\017') {
/*      */               
/* 1157 */               buffer.append("\\u00" + Integer.toHexString(ch));
/*      */               
/*      */               break;
/*      */             } 
/* 1161 */             buffer.append("\\u000" + Integer.toHexString(ch));
/*      */             break;
/*      */         } 
/*      */ 
/*      */ 
/*      */       
/*      */       } else {
/* 1168 */         switch (ch) {
/*      */           
/*      */           case '\'':
/* 1171 */             buffer.append('\\');
/* 1172 */             buffer.append('\'');
/*      */             break;
/*      */           case '"':
/* 1175 */             buffer.append('\\');
/* 1176 */             buffer.append('"');
/*      */             break;
/*      */           case '\\':
/* 1179 */             buffer.append('\\');
/* 1180 */             buffer.append('\\');
/*      */             break;
/*      */           default:
/* 1183 */             buffer.append(ch);
/*      */             break;
/*      */         } 
/*      */       } 
/*      */     } 
/* 1188 */     return buffer.toString();
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
/*      */   public static String repeat(String str, int repeat) {
/* 1206 */     StringBuffer buffer = new StringBuffer(repeat * str.length());
/* 1207 */     for (int i = 0; i < repeat; i++)
/*      */     {
/* 1209 */       buffer.append(str);
/*      */     }
/* 1211 */     return buffer.toString();
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
/*      */   public static String rightPad(String str, int size) {
/* 1226 */     return rightPad(str, size, " ");
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
/*      */   public static String rightPad(String str, int size, String delim) {
/* 1243 */     size = (size - str.length()) / delim.length();
/* 1244 */     if (size > 0)
/*      */     {
/* 1246 */       str = str + repeat(delim, size);
/*      */     }
/* 1248 */     return str;
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
/*      */   public static String leftPad(String str, int size) {
/* 1263 */     return leftPad(str, size, " ");
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
/*      */   public static String leftPad(String str, int size, String delim) {
/* 1278 */     size = (size - str.length()) / delim.length();
/* 1279 */     if (size > 0)
/*      */     {
/* 1281 */       str = repeat(delim, size) + str;
/*      */     }
/* 1283 */     return str;
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
/*      */   public static String strip(String str) {
/* 1297 */     return strip(str, null);
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
/*      */   public static String strip(String str, String delim) {
/* 1313 */     str = stripStart(str, delim);
/* 1314 */     return stripEnd(str, delim);
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
/*      */   public static String[] stripAll(String[] strs) {
/* 1326 */     return stripAll(strs, null);
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
/*      */   public static String[] stripAll(String[] strs, String delimiter) {
/* 1339 */     if (strs == null || strs.length == 0)
/*      */     {
/* 1341 */       return strs;
/*      */     }
/* 1343 */     int sz = strs.length;
/* 1344 */     String[] newArr = new String[sz];
/* 1345 */     for (int i = 0; i < sz; i++)
/*      */     {
/* 1347 */       newArr[i] = strip(strs[i], delimiter);
/*      */     }
/* 1349 */     return newArr;
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
/*      */   public static String stripEnd(String str, String strip) {
/* 1364 */     if (str == null)
/*      */     {
/* 1366 */       return null;
/*      */     }
/* 1368 */     int end = str.length();
/*      */     
/* 1370 */     if (strip == null) {
/*      */       
/* 1372 */       while (end != 0 && Character.isWhitespace(str.charAt(end - 1)))
/*      */       {
/* 1374 */         end--;
/*      */       }
/*      */     }
/*      */     else {
/*      */       
/* 1379 */       while (end != 0 && strip.indexOf(str.charAt(end - 1)) != -1)
/*      */       {
/* 1381 */         end--;
/*      */       }
/*      */     } 
/* 1384 */     return str.substring(0, end);
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
/*      */   public static String stripStart(String str, String strip) {
/* 1399 */     if (str == null)
/*      */     {
/* 1401 */       return null;
/*      */     }
/*      */     
/* 1404 */     int start = 0;
/*      */     
/* 1406 */     int sz = str.length();
/*      */     
/* 1408 */     if (strip == null) {
/*      */       
/* 1410 */       while (start != sz && Character.isWhitespace(str.charAt(start)))
/*      */       {
/* 1412 */         start++;
/*      */       }
/*      */     }
/*      */     else {
/*      */       
/* 1417 */       while (start != sz && strip.indexOf(str.charAt(start)) != -1)
/*      */       {
/* 1419 */         start++;
/*      */       }
/*      */     } 
/* 1422 */     return str.substring(start);
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
/*      */   public static String upperCase(String str) {
/* 1437 */     if (str == null)
/*      */     {
/* 1439 */       return null;
/*      */     }
/* 1441 */     return str.toUpperCase();
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
/*      */   public static String lowerCase(String str) {
/* 1453 */     if (str == null)
/*      */     {
/* 1455 */       return null;
/*      */     }
/* 1457 */     return str.toLowerCase();
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
/*      */   public static String uncapitalise(String str) {
/* 1471 */     if (str == null)
/*      */     {
/* 1473 */       return null;
/*      */     }
/* 1475 */     if (str.length() == 0)
/*      */     {
/* 1477 */       return "";
/*      */     }
/*      */ 
/*      */     
/* 1481 */     return (new StringBuffer(str.length())).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
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
/*      */   public static String capitalise(String str) {
/* 1499 */     if (str == null)
/*      */     {
/* 1501 */       return null;
/*      */     }
/* 1503 */     if (str.length() == 0)
/*      */     {
/* 1505 */       return "";
/*      */     }
/*      */ 
/*      */     
/* 1509 */     return (new StringBuffer(str.length())).append(Character.toTitleCase(str.charAt(0))).append(str.substring(1)).toString();
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
/*      */   public static String swapCase(String str) {
/* 1529 */     if (str == null)
/*      */     {
/* 1531 */       return null;
/*      */     }
/* 1533 */     int sz = str.length();
/* 1534 */     StringBuffer buffer = new StringBuffer(sz);
/*      */     
/* 1536 */     boolean whitespace = false;
/* 1537 */     char ch = Character.MIN_VALUE;
/* 1538 */     char tmp = Character.MIN_VALUE;
/*      */     
/* 1540 */     for (int i = 0; i < sz; i++) {
/*      */       
/* 1542 */       ch = str.charAt(i);
/* 1543 */       if (Character.isUpperCase(ch)) {
/*      */         
/* 1545 */         tmp = Character.toLowerCase(ch);
/*      */       }
/* 1547 */       else if (Character.isTitleCase(ch)) {
/*      */         
/* 1549 */         tmp = Character.toLowerCase(ch);
/*      */       }
/* 1551 */       else if (Character.isLowerCase(ch)) {
/*      */         
/* 1553 */         if (whitespace)
/*      */         {
/* 1555 */           tmp = Character.toTitleCase(ch);
/*      */         }
/*      */         else
/*      */         {
/* 1559 */           tmp = Character.toUpperCase(ch);
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/* 1564 */         tmp = ch;
/*      */       } 
/* 1566 */       buffer.append(tmp);
/* 1567 */       whitespace = Character.isWhitespace(ch);
/*      */     } 
/* 1569 */     return buffer.toString();
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
/*      */   public static String capitaliseAllWords(String str) {
/* 1586 */     if (str == null)
/*      */     {
/* 1588 */       return null;
/*      */     }
/* 1590 */     int sz = str.length();
/* 1591 */     StringBuffer buffer = new StringBuffer(sz);
/* 1592 */     boolean space = true;
/* 1593 */     for (int i = 0; i < sz; i++) {
/*      */       
/* 1595 */       char ch = str.charAt(i);
/* 1596 */       if (Character.isWhitespace(ch)) {
/*      */         
/* 1598 */         buffer.append(ch);
/* 1599 */         space = true;
/*      */       }
/* 1601 */       else if (space) {
/*      */         
/* 1603 */         buffer.append(Character.toTitleCase(ch));
/* 1604 */         space = false;
/*      */       }
/*      */       else {
/*      */         
/* 1608 */         buffer.append(ch);
/*      */       } 
/*      */     } 
/* 1611 */     return buffer.toString();
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
/*      */   public static String uncapitaliseAllWords(String str) {
/* 1627 */     if (str == null)
/*      */     {
/* 1629 */       return null;
/*      */     }
/* 1631 */     int sz = str.length();
/* 1632 */     StringBuffer buffer = new StringBuffer(sz);
/* 1633 */     boolean space = true;
/* 1634 */     for (int i = 0; i < sz; i++) {
/*      */       
/* 1636 */       char ch = str.charAt(i);
/* 1637 */       if (Character.isWhitespace(ch)) {
/*      */         
/* 1639 */         buffer.append(ch);
/* 1640 */         space = true;
/*      */       }
/* 1642 */       else if (space) {
/*      */         
/* 1644 */         buffer.append(Character.toLowerCase(ch));
/* 1645 */         space = false;
/*      */       }
/*      */       else {
/*      */         
/* 1649 */         buffer.append(ch);
/*      */       } 
/*      */     } 
/* 1652 */     return buffer.toString();
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
/*      */   public static String getNestedString(String str, String tag) {
/* 1672 */     return getNestedString(str, tag, tag);
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
/*      */   public static String getNestedString(String str, String open, String close) {
/* 1686 */     if (str == null)
/*      */     {
/* 1688 */       return null;
/*      */     }
/* 1690 */     int start = str.indexOf(open);
/* 1691 */     if (start != -1) {
/*      */       
/* 1693 */       int end = str.indexOf(close, start + open.length());
/* 1694 */       if (end != -1)
/*      */       {
/* 1696 */         return str.substring(start + open.length(), end);
/*      */       }
/*      */     } 
/* 1699 */     return null;
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
/*      */   public static int countMatches(String str, String sub) {
/* 1714 */     if (sub.equals(""))
/*      */     {
/* 1716 */       return 0;
/*      */     }
/* 1718 */     if (str == null)
/*      */     {
/* 1720 */       return 0;
/*      */     }
/* 1722 */     int count = 0;
/* 1723 */     int idx = 0;
/* 1724 */     while ((idx = str.indexOf(sub, idx)) != -1) {
/*      */       
/* 1726 */       count++;
/* 1727 */       idx += sub.length();
/*      */     } 
/* 1729 */     return count;
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
/*      */   public static boolean isAlpha(String str) {
/* 1746 */     if (str == null)
/*      */     {
/* 1748 */       return false;
/*      */     }
/* 1750 */     int sz = str.length();
/* 1751 */     for (int i = 0; i < sz; i++) {
/*      */       
/* 1753 */       if (!Character.isLetter(str.charAt(i)))
/*      */       {
/* 1755 */         return false;
/*      */       }
/*      */     } 
/* 1758 */     return true;
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
/*      */   public static boolean isWhitespace(String str) {
/* 1772 */     if (str == null)
/*      */     {
/* 1774 */       return false;
/*      */     }
/* 1776 */     int sz = str.length();
/* 1777 */     for (int i = 0; i < sz; i++) {
/*      */       
/* 1779 */       if (!Character.isWhitespace(str.charAt(i)))
/*      */       {
/* 1781 */         return false;
/*      */       }
/*      */     } 
/* 1784 */     return true;
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
/*      */   public static boolean isAlphaSpace(String str) {
/* 1800 */     if (str == null)
/*      */     {
/* 1802 */       return false;
/*      */     }
/* 1804 */     int sz = str.length();
/* 1805 */     for (int i = 0; i < sz; i++) {
/*      */       
/* 1807 */       if (!Character.isLetter(str.charAt(i)) && str.charAt(i) != ' ')
/*      */       {
/*      */         
/* 1810 */         return false;
/*      */       }
/*      */     } 
/* 1813 */     return true;
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
/*      */   public static boolean isAlphanumeric(String str) {
/* 1828 */     if (str == null)
/*      */     {
/* 1830 */       return false;
/*      */     }
/* 1832 */     int sz = str.length();
/* 1833 */     for (int i = 0; i < sz; i++) {
/*      */       
/* 1835 */       if (!Character.isLetterOrDigit(str.charAt(i)))
/*      */       {
/* 1837 */         return false;
/*      */       }
/*      */     } 
/* 1840 */     return true;
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
/*      */   public static boolean isAlphanumericSpace(String str) {
/* 1856 */     if (str == null)
/*      */     {
/* 1858 */       return false;
/*      */     }
/* 1860 */     int sz = str.length();
/* 1861 */     for (int i = 0; i < sz; i++) {
/*      */       
/* 1863 */       if (!Character.isLetterOrDigit(str.charAt(i)) && str.charAt(i) != ' ')
/*      */       {
/*      */         
/* 1866 */         return false;
/*      */       }
/*      */     } 
/* 1869 */     return true;
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
/*      */   public static boolean isNumeric(String str) {
/* 1883 */     if (str == null)
/*      */     {
/* 1885 */       return false;
/*      */     }
/* 1887 */     int sz = str.length();
/* 1888 */     for (int i = 0; i < sz; i++) {
/*      */       
/* 1890 */       if (!Character.isDigit(str.charAt(i)))
/*      */       {
/* 1892 */         return false;
/*      */       }
/*      */     } 
/* 1895 */     return true;
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
/*      */   public static boolean isNumericSpace(String str) {
/* 1911 */     if (str == null)
/*      */     {
/* 1913 */       return false;
/*      */     }
/* 1915 */     int sz = str.length();
/* 1916 */     for (int i = 0; i < sz; i++) {
/*      */       
/* 1918 */       if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != ' ')
/*      */       {
/*      */         
/* 1921 */         return false;
/*      */       }
/*      */     } 
/* 1924 */     return true;
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
/*      */   public static String defaultString(Object obj) {
/* 1941 */     return defaultString(obj, "");
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
/*      */   public static String defaultString(Object obj, String defaultString) {
/* 1957 */     return (obj == null) ? defaultString : obj.toString();
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
/*      */   public static String reverse(String str) {
/* 1973 */     if (str == null)
/*      */     {
/* 1975 */       return null;
/*      */     }
/* 1977 */     return (new StringBuffer(str)).reverse().toString();
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
/*      */   public static String reverseDelimitedString(String str, String delimiter) {
/* 1995 */     String[] strs = split(str, delimiter);
/* 1996 */     reverseArray((Object[])strs);
/* 1997 */     return join((Object[])strs, delimiter);
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
/*      */   private static void reverseArray(Object[] array) {
/* 2009 */     int i = 0;
/* 2010 */     int j = array.length - 1;
/*      */ 
/*      */     
/* 2013 */     while (j > i) {
/*      */       
/* 2015 */       Object tmp = array[j];
/* 2016 */       array[j] = array[i];
/* 2017 */       array[i] = tmp;
/* 2018 */       j--;
/* 2019 */       i++;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String abbreviate(String s, int maxWidth) {
/* 2040 */     return abbreviate(s, 0, maxWidth);
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
/*      */   public static String abbreviate(String s, int offset, int maxWidth) {
/* 2057 */     if (maxWidth < 4)
/*      */     {
/* 2059 */       throw new IllegalArgumentException("Minimum abbreviation width is 4");
/*      */     }
/* 2061 */     if (s.length() <= maxWidth)
/*      */     {
/* 2063 */       return s;
/*      */     }
/* 2065 */     if (offset > s.length())
/*      */     {
/* 2067 */       offset = s.length();
/*      */     }
/* 2069 */     if (s.length() - offset < maxWidth - 3)
/*      */     {
/* 2071 */       offset = s.length() - maxWidth - 3;
/*      */     }
/* 2073 */     if (offset <= 4)
/*      */     {
/* 2075 */       return s.substring(0, maxWidth - 3) + "...";
/*      */     }
/* 2077 */     if (maxWidth < 7)
/*      */     {
/* 2079 */       throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
/*      */     }
/* 2081 */     if (offset + maxWidth - 3 < s.length())
/*      */     {
/* 2083 */       return "..." + abbreviate(s.substring(offset), maxWidth - 3);
/*      */     }
/* 2085 */     return "..." + s.substring(s.length() - maxWidth - 3);
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
/*      */   public static String difference(String s1, String s2) {
/* 2102 */     int at = differenceAt(s1, s2);
/* 2103 */     if (at == -1)
/*      */     {
/* 2105 */       return "";
/*      */     }
/* 2107 */     return s2.substring(at);
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
/*      */   public static int differenceAt(String s1, String s2) {
/*      */     int i;
/* 2121 */     for (i = 0; i < s1.length() && i < s2.length(); i++) {
/*      */       
/* 2123 */       if (s1.charAt(i) != s2.charAt(i)) {
/*      */         break;
/*      */       }
/*      */     } 
/*      */     
/* 2128 */     if (i < s2.length() || i < s1.length())
/*      */     {
/* 2130 */       return i;
/*      */     }
/* 2132 */     return -1;
/*      */   }
/*      */ 
/*      */   
/*      */   public static String interpolate(String text, Map namespace) {
/* 2137 */     Iterator keys = namespace.keySet().iterator();
/*      */     
/* 2139 */     while (keys.hasNext()) {
/*      */       
/* 2141 */       String key = keys.next().toString();
/*      */       
/* 2143 */       Object obj = namespace.get(key);
/*      */       
/* 2145 */       if (obj == null)
/*      */       {
/* 2147 */         throw new NullPointerException("The value of the key '" + key + "' is null.");
/*      */       }
/*      */       
/* 2150 */       String value = obj.toString();
/*      */       
/* 2152 */       text = replace(text, "${" + key + "}", value);
/*      */       
/* 2154 */       if (key.indexOf(" ") == -1)
/*      */       {
/* 2156 */         text = replace(text, "$" + key, value);
/*      */       }
/*      */     } 
/* 2159 */     return text;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String removeAndHump(String data, String replaceThis) {
/* 2166 */     StringBuffer out = new StringBuffer();
/*      */     
/* 2168 */     String temp = data;
/*      */     
/* 2170 */     StringTokenizer st = new StringTokenizer(temp, replaceThis);
/*      */     
/* 2172 */     while (st.hasMoreTokens()) {
/*      */       
/* 2174 */       String element = (String)st.nextElement();
/*      */       
/* 2176 */       out.append(capitalizeFirstLetter(element));
/*      */     } 
/*      */     
/* 2179 */     return out.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   public static String capitalizeFirstLetter(String data) {
/* 2184 */     char firstLetter = Character.toTitleCase(data.substring(0, 1).charAt(0));
/*      */     
/* 2186 */     String restLetters = data.substring(1);
/*      */     
/* 2188 */     return firstLetter + restLetters;
/*      */   }
/*      */ 
/*      */   
/*      */   public static String lowercaseFirstLetter(String data) {
/* 2193 */     char firstLetter = Character.toLowerCase(data.substring(0, 1).charAt(0));
/*      */     
/* 2195 */     String restLetters = data.substring(1);
/*      */     
/* 2197 */     return firstLetter + restLetters;
/*      */   }
/*      */ 
/*      */   
/*      */   public static String addAndDeHump(String view) {
/* 2202 */     StringBuffer sb = new StringBuffer();
/*      */     
/* 2204 */     for (int i = 0; i < view.length(); i++) {
/*      */       
/* 2206 */       if (i != 0 && Character.isUpperCase(view.charAt(i)))
/*      */       {
/* 2208 */         sb.append('-');
/*      */       }
/*      */       
/* 2211 */       sb.append(view.charAt(i));
/*      */     } 
/*      */     
/* 2214 */     return sb.toString().trim().toLowerCase(Locale.ENGLISH);
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
/*      */   public static String quoteAndEscape(String source, char quoteChar) {
/* 2237 */     return quoteAndEscape(source, quoteChar, new char[] { quoteChar }, new char[] { ' ' }, '\\', false);
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
/*      */   public static String quoteAndEscape(String source, char quoteChar, char[] quotingTriggers) {
/* 2254 */     return quoteAndEscape(source, quoteChar, new char[] { quoteChar }, quotingTriggers, '\\', false);
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
/*      */   public static String quoteAndEscape(String source, char quoteChar, char[] escapedChars, char escapeChar, boolean force) {
/* 2273 */     return quoteAndEscape(source, quoteChar, escapedChars, new char[] { ' ' }, escapeChar, force);
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
/*      */   public static String quoteAndEscape(String source, char quoteChar, char[] escapedChars, char[] quotingTriggers, char escapeChar, boolean force) {
/* 2293 */     if (source == null)
/*      */     {
/* 2295 */       return null;
/*      */     }
/*      */     
/* 2298 */     if (!force && source.startsWith(Character.toString(quoteChar)) && source.endsWith(Character.toString(quoteChar)))
/*      */     {
/*      */       
/* 2301 */       return source;
/*      */     }
/*      */     
/* 2304 */     String escaped = escape(source, escapedChars, escapeChar);
/*      */     
/* 2306 */     boolean quote = false;
/* 2307 */     if (force) {
/*      */       
/* 2309 */       quote = true;
/*      */     }
/* 2311 */     else if (!escaped.equals(source)) {
/*      */       
/* 2313 */       quote = true;
/*      */     }
/*      */     else {
/*      */       
/* 2317 */       for (int i = 0; i < quotingTriggers.length; i++) {
/*      */         
/* 2319 */         if (escaped.indexOf(quotingTriggers[i]) > -1) {
/*      */           
/* 2321 */           quote = true;
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2327 */     if (quote)
/*      */     {
/* 2329 */       return quoteChar + escaped + quoteChar;
/*      */     }
/*      */     
/* 2332 */     return escaped;
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
/*      */   public static String escape(String source, char[] escapedChars, char escapeChar) {
/* 2344 */     if (source == null)
/*      */     {
/* 2346 */       return null;
/*      */     }
/*      */     
/* 2349 */     char[] eqc = new char[escapedChars.length];
/* 2350 */     System.arraycopy(escapedChars, 0, eqc, 0, escapedChars.length);
/* 2351 */     Arrays.sort(eqc);
/*      */     
/* 2353 */     StringBuffer buffer = new StringBuffer(source.length());
/*      */     
/* 2355 */     int escapeCount = 0;
/* 2356 */     for (int i = 0; i < source.length(); i++) {
/*      */       
/* 2358 */       char c = source.charAt(i);
/* 2359 */       int result = Arrays.binarySearch(eqc, c);
/*      */       
/* 2361 */       if (result > -1) {
/*      */         
/* 2363 */         buffer.append(escapeChar);
/* 2364 */         escapeCount++;
/*      */       } 
/*      */       
/* 2367 */       buffer.append(c);
/*      */     } 
/*      */     
/* 2370 */     return buffer.toString();
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
/*      */   public static String removeDuplicateWhitespace(String s) {
/* 2383 */     String patternStr = "\\s+";
/* 2384 */     String replaceStr = " ";
/* 2385 */     Pattern pattern = Pattern.compile(patternStr);
/* 2386 */     Matcher matcher = pattern.matcher(s);
/* 2387 */     return matcher.replaceAll(replaceStr);
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
/*      */   public static String unifyLineSeparators(String s) {
/* 2401 */     return unifyLineSeparators(s, System.getProperty("line.separator"));
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
/*      */   public static String unifyLineSeparators(String s, String ls) {
/* 2416 */     if (s == null)
/*      */     {
/* 2418 */       return null;
/*      */     }
/*      */     
/* 2421 */     if (ls == null)
/*      */     {
/* 2423 */       ls = System.getProperty("line.separator");
/*      */     }
/*      */     
/* 2426 */     if (!ls.equals("\n") && !ls.equals("\r") && !ls.equals("\r\n"))
/*      */     {
/* 2428 */       throw new IllegalArgumentException("Requested line separator is invalid.");
/*      */     }
/*      */     
/* 2431 */     int length = s.length();
/*      */     
/* 2433 */     StringBuffer buffer = new StringBuffer(length);
/* 2434 */     for (int i = 0; i < length; i++) {
/*      */       
/* 2436 */       if (s.charAt(i) == '\r') {
/*      */         
/* 2438 */         if (i + 1 < length && s.charAt(i + 1) == '\n')
/*      */         {
/* 2440 */           i++;
/*      */         }
/*      */         
/* 2443 */         buffer.append(ls);
/*      */       }
/* 2445 */       else if (s.charAt(i) == '\n') {
/*      */         
/* 2447 */         buffer.append(ls);
/*      */       }
/*      */       else {
/*      */         
/* 2451 */         buffer.append(s.charAt(i));
/*      */       } 
/*      */     } 
/*      */     
/* 2455 */     return buffer.toString();
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
/*      */   public static boolean contains(String str, char searchChar) {
/* 2479 */     if (isEmpty(str))
/*      */     {
/* 2481 */       return false;
/*      */     }
/* 2483 */     return (str.indexOf(searchChar) >= 0);
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
/*      */   public static boolean contains(String str, String searchStr) {
/* 2509 */     if (str == null || searchStr == null)
/*      */     {
/* 2511 */       return false;
/*      */     }
/* 2513 */     return (str.indexOf(searchStr) >= 0);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\StringUtils.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */