package org.codehaus.plexus.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
   public static String clean(String str) {
      return str == null ? "" : str.trim();
   }

   public static String trim(String str) {
      return str == null ? null : str.trim();
   }

   public static String deleteWhitespace(String str) {
      StringBuffer buffer = new StringBuffer();
      int sz = str.length();

      for(int i = 0; i < sz; ++i) {
         if (!Character.isWhitespace(str.charAt(i))) {
            buffer.append(str.charAt(i));
         }
      }

      return buffer.toString();
   }

   public static boolean isNotEmpty(String str) {
      return str != null && str.length() > 0;
   }

   public static boolean isEmpty(String str) {
      return str == null || str.trim().length() == 0;
   }

   public static boolean isBlank(String str) {
      int strLen;
      if (str != null && (strLen = str.length()) != 0) {
         for(int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean isNotBlank(String str) {
      return !isBlank(str);
   }

   public static boolean equals(String str1, String str2) {
      return str1 == null ? str2 == null : str1.equals(str2);
   }

   public static boolean equalsIgnoreCase(String str1, String str2) {
      return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
   }

   public static int indexOfAny(String str, String[] searchStrs) {
      if (str != null && searchStrs != null) {
         int sz = searchStrs.length;
         int ret = Integer.MAX_VALUE;
         int tmp = false;

         for(int i = 0; i < sz; ++i) {
            int tmp = str.indexOf(searchStrs[i]);
            if (tmp != -1 && tmp < ret) {
               ret = tmp;
            }
         }

         return ret == Integer.MAX_VALUE ? -1 : ret;
      } else {
         return -1;
      }
   }

   public static int lastIndexOfAny(String str, String[] searchStrs) {
      if (str != null && searchStrs != null) {
         int sz = searchStrs.length;
         int ret = -1;
         int tmp = false;

         for(int i = 0; i < sz; ++i) {
            int tmp = str.lastIndexOf(searchStrs[i]);
            if (tmp > ret) {
               ret = tmp;
            }
         }

         return ret;
      } else {
         return -1;
      }
   }

   public static String substring(String str, int start) {
      if (str == null) {
         return null;
      } else {
         if (start < 0) {
            start += str.length();
         }

         if (start < 0) {
            start = 0;
         }

         return start > str.length() ? "" : str.substring(start);
      }
   }

   public static String substring(String str, int start, int end) {
      if (str == null) {
         return null;
      } else {
         if (end < 0) {
            end += str.length();
         }

         if (start < 0) {
            start += str.length();
         }

         if (end > str.length()) {
            end = str.length();
         }

         if (start > end) {
            return "";
         } else {
            if (start < 0) {
               start = 0;
            }

            if (end < 0) {
               end = 0;
            }

            return str.substring(start, end);
         }
      }
   }

   public static String left(String str, int len) {
      if (len < 0) {
         throw new IllegalArgumentException("Requested String length " + len + " is less than zero");
      } else {
         return str != null && str.length() > len ? str.substring(0, len) : str;
      }
   }

   public static String right(String str, int len) {
      if (len < 0) {
         throw new IllegalArgumentException("Requested String length " + len + " is less than zero");
      } else {
         return str != null && str.length() > len ? str.substring(str.length() - len) : str;
      }
   }

   public static String mid(String str, int pos, int len) {
      if (pos >= 0 && (str == null || pos <= str.length())) {
         if (len < 0) {
            throw new IllegalArgumentException("Requested String length " + len + " is less than zero");
         } else if (str == null) {
            return null;
         } else {
            return str.length() <= pos + len ? str.substring(pos) : str.substring(pos, pos + len);
         }
      } else {
         throw new StringIndexOutOfBoundsException("String index " + pos + " is out of bounds");
      }
   }

   public static String[] split(String str) {
      return split(str, (String)null, -1);
   }

   public static String[] split(String text, String separator) {
      return split(text, separator, -1);
   }

   public static String[] split(String str, String separator, int max) {
      StringTokenizer tok = null;
      if (separator == null) {
         tok = new StringTokenizer(str);
      } else {
         tok = new StringTokenizer(str, separator);
      }

      int listSize = tok.countTokens();
      if (max > 0 && listSize > max) {
         listSize = max;
      }

      String[] list = new String[listSize];
      int i = 0;
      int lastTokenBegin = false;

      for(int lastTokenEnd = 0; tok.hasMoreTokens(); ++i) {
         int lastTokenBegin;
         if (max > 0 && i == listSize - 1) {
            String endToken = tok.nextToken();
            lastTokenBegin = str.indexOf(endToken, lastTokenEnd);
            list[i] = str.substring(lastTokenBegin);
            break;
         }

         list[i] = tok.nextToken();
         lastTokenBegin = str.indexOf(list[i], lastTokenEnd);
         lastTokenEnd = lastTokenBegin + list[i].length();
      }

      return list;
   }

   public static String concatenate(Object[] array) {
      return join(array, "");
   }

   public static String join(Object[] array, String separator) {
      if (separator == null) {
         separator = "";
      }

      int arraySize = array.length;
      int bufSize = arraySize == 0 ? 0 : (array[0].toString().length() + separator.length()) * arraySize;
      StringBuffer buf = new StringBuffer(bufSize);

      for(int i = 0; i < arraySize; ++i) {
         if (i > 0) {
            buf.append(separator);
         }

         buf.append(array[i]);
      }

      return buf.toString();
   }

   public static String join(Iterator iterator, String separator) {
      if (separator == null) {
         separator = "";
      }

      StringBuffer buf = new StringBuffer(256);

      while(iterator.hasNext()) {
         buf.append(iterator.next());
         if (iterator.hasNext()) {
            buf.append(separator);
         }
      }

      return buf.toString();
   }

   public static String replaceOnce(String text, char repl, char with) {
      return replace(text, repl, with, 1);
   }

   public static String replace(String text, char repl, char with) {
      return replace(text, repl, with, -1);
   }

   public static String replace(String text, char repl, char with, int max) {
      return replace(text, String.valueOf(repl), String.valueOf(with), max);
   }

   public static String replaceOnce(String text, String repl, String with) {
      return replace(text, repl, with, 1);
   }

   public static String replace(String text, String repl, String with) {
      return replace(text, repl, with, -1);
   }

   public static String replace(String text, String repl, String with, int max) {
      if (text != null && repl != null && with != null && repl.length() != 0) {
         StringBuffer buf = new StringBuffer(text.length());
         int start = 0;
         int end = false;

         int end;
         while((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();
            --max;
            if (max == 0) {
               break;
            }
         }

         buf.append(text.substring(start));
         return buf.toString();
      } else {
         return text;
      }
   }

   public static String overlayString(String text, String overlay, int start, int end) {
      return (new StringBuffer(start + overlay.length() + text.length() - end + 1)).append(text.substring(0, start)).append(overlay).append(text.substring(end)).toString();
   }

   public static String center(String str, int size) {
      return center(str, size, " ");
   }

   public static String center(String str, int size, String delim) {
      int sz = str.length();
      int p = size - sz;
      if (p < 1) {
         return str;
      } else {
         str = leftPad(str, sz + p / 2, delim);
         str = rightPad(str, size, delim);
         return str;
      }
   }

   public static String chomp(String str) {
      return chomp(str, "\n");
   }

   public static String chomp(String str, String sep) {
      int idx = str.lastIndexOf(sep);
      return idx != -1 ? str.substring(0, idx) : str;
   }

   public static String chompLast(String str) {
      return chompLast(str, "\n");
   }

   public static String chompLast(String str, String sep) {
      if (str.length() == 0) {
         return str;
      } else {
         String sub = str.substring(str.length() - sep.length());
         return sep.equals(sub) ? str.substring(0, str.length() - sep.length()) : str;
      }
   }

   public static String getChomp(String str, String sep) {
      int idx = str.lastIndexOf(sep);
      if (idx == str.length() - sep.length()) {
         return sep;
      } else {
         return idx != -1 ? str.substring(idx) : "";
      }
   }

   public static String prechomp(String str, String sep) {
      int idx = str.indexOf(sep);
      return idx != -1 ? str.substring(idx + sep.length()) : str;
   }

   public static String getPrechomp(String str, String sep) {
      int idx = str.indexOf(sep);
      return idx != -1 ? str.substring(0, idx + sep.length()) : "";
   }

   public static String chop(String str) {
      if ("".equals(str)) {
         return "";
      } else if (str.length() == 1) {
         return "";
      } else {
         int lastIdx = str.length() - 1;
         String ret = str.substring(0, lastIdx);
         char last = str.charAt(lastIdx);
         return last == '\n' && ret.charAt(lastIdx - 1) == '\r' ? ret.substring(0, lastIdx - 1) : ret;
      }
   }

   public static String chopNewline(String str) {
      int lastIdx = str.length() - 1;
      char last = str.charAt(lastIdx);
      if (last == '\n') {
         if (str.charAt(lastIdx - 1) == '\r') {
            --lastIdx;
         }
      } else {
         ++lastIdx;
      }

      return str.substring(0, lastIdx);
   }

   public static String escape(String str) {
      int sz = str.length();
      StringBuffer buffer = new StringBuffer(2 * sz);

      for(int i = 0; i < sz; ++i) {
         char ch = str.charAt(i);
         if (ch > 4095) {
            buffer.append("\\u" + Integer.toHexString(ch));
         } else if (ch > 255) {
            buffer.append("\\u0" + Integer.toHexString(ch));
         } else if (ch > 127) {
            buffer.append("\\u00" + Integer.toHexString(ch));
         } else if (ch < ' ') {
            switch (ch) {
               case '\b':
                  buffer.append('\\');
                  buffer.append('b');
                  break;
               case '\t':
                  buffer.append('\\');
                  buffer.append('t');
                  break;
               case '\n':
                  buffer.append('\\');
                  buffer.append('n');
                  break;
               case '\u000b':
               default:
                  if (ch > 15) {
                     buffer.append("\\u00" + Integer.toHexString(ch));
                  } else {
                     buffer.append("\\u000" + Integer.toHexString(ch));
                  }
                  break;
               case '\f':
                  buffer.append('\\');
                  buffer.append('f');
                  break;
               case '\r':
                  buffer.append('\\');
                  buffer.append('r');
            }
         } else {
            switch (ch) {
               case '"':
                  buffer.append('\\');
                  buffer.append('"');
                  break;
               case '\'':
                  buffer.append('\\');
                  buffer.append('\'');
                  break;
               case '\\':
                  buffer.append('\\');
                  buffer.append('\\');
                  break;
               default:
                  buffer.append(ch);
            }
         }
      }

      return buffer.toString();
   }

   public static String repeat(String str, int repeat) {
      StringBuffer buffer = new StringBuffer(repeat * str.length());

      for(int i = 0; i < repeat; ++i) {
         buffer.append(str);
      }

      return buffer.toString();
   }

   public static String rightPad(String str, int size) {
      return rightPad(str, size, " ");
   }

   public static String rightPad(String str, int size, String delim) {
      size = (size - str.length()) / delim.length();
      if (size > 0) {
         str = str + repeat(delim, size);
      }

      return str;
   }

   public static String leftPad(String str, int size) {
      return leftPad(str, size, " ");
   }

   public static String leftPad(String str, int size, String delim) {
      size = (size - str.length()) / delim.length();
      if (size > 0) {
         str = repeat(delim, size) + str;
      }

      return str;
   }

   public static String strip(String str) {
      return strip(str, (String)null);
   }

   public static String strip(String str, String delim) {
      str = stripStart(str, delim);
      return stripEnd(str, delim);
   }

   public static String[] stripAll(String[] strs) {
      return stripAll(strs, (String)null);
   }

   public static String[] stripAll(String[] strs, String delimiter) {
      if (strs != null && strs.length != 0) {
         int sz = strs.length;
         String[] newArr = new String[sz];

         for(int i = 0; i < sz; ++i) {
            newArr[i] = strip(strs[i], delimiter);
         }

         return newArr;
      } else {
         return strs;
      }
   }

   public static String stripEnd(String str, String strip) {
      if (str == null) {
         return null;
      } else {
         int end = str.length();
         if (strip == null) {
            while(end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
               --end;
            }
         } else {
            while(end != 0 && strip.indexOf(str.charAt(end - 1)) != -1) {
               --end;
            }
         }

         return str.substring(0, end);
      }
   }

   public static String stripStart(String str, String strip) {
      if (str == null) {
         return null;
      } else {
         int start = 0;
         int sz = str.length();
         if (strip == null) {
            while(start != sz && Character.isWhitespace(str.charAt(start))) {
               ++start;
            }
         } else {
            while(start != sz && strip.indexOf(str.charAt(start)) != -1) {
               ++start;
            }
         }

         return str.substring(start);
      }
   }

   public static String upperCase(String str) {
      return str == null ? null : str.toUpperCase();
   }

   public static String lowerCase(String str) {
      return str == null ? null : str.toLowerCase();
   }

   public static String uncapitalise(String str) {
      if (str == null) {
         return null;
      } else {
         return str.length() == 0 ? "" : (new StringBuffer(str.length())).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
      }
   }

   public static String capitalise(String str) {
      if (str == null) {
         return null;
      } else {
         return str.length() == 0 ? "" : (new StringBuffer(str.length())).append(Character.toTitleCase(str.charAt(0))).append(str.substring(1)).toString();
      }
   }

   public static String swapCase(String str) {
      if (str == null) {
         return null;
      } else {
         int sz = str.length();
         StringBuffer buffer = new StringBuffer(sz);
         boolean whitespace = false;
         char ch = false;
         char tmp = false;

         for(int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            char tmp;
            if (Character.isUpperCase(ch)) {
               tmp = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
               tmp = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
               if (whitespace) {
                  tmp = Character.toTitleCase(ch);
               } else {
                  tmp = Character.toUpperCase(ch);
               }
            } else {
               tmp = ch;
            }

            buffer.append(tmp);
            whitespace = Character.isWhitespace(ch);
         }

         return buffer.toString();
      }
   }

   public static String capitaliseAllWords(String str) {
      if (str == null) {
         return null;
      } else {
         int sz = str.length();
         StringBuffer buffer = new StringBuffer(sz);
         boolean space = true;

         for(int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            if (Character.isWhitespace(ch)) {
               buffer.append(ch);
               space = true;
            } else if (space) {
               buffer.append(Character.toTitleCase(ch));
               space = false;
            } else {
               buffer.append(ch);
            }
         }

         return buffer.toString();
      }
   }

   public static String uncapitaliseAllWords(String str) {
      if (str == null) {
         return null;
      } else {
         int sz = str.length();
         StringBuffer buffer = new StringBuffer(sz);
         boolean space = true;

         for(int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            if (Character.isWhitespace(ch)) {
               buffer.append(ch);
               space = true;
            } else if (space) {
               buffer.append(Character.toLowerCase(ch));
               space = false;
            } else {
               buffer.append(ch);
            }
         }

         return buffer.toString();
      }
   }

   public static String getNestedString(String str, String tag) {
      return getNestedString(str, tag, tag);
   }

   public static String getNestedString(String str, String open, String close) {
      if (str == null) {
         return null;
      } else {
         int start = str.indexOf(open);
         if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
               return str.substring(start + open.length(), end);
            }
         }

         return null;
      }
   }

   public static int countMatches(String str, String sub) {
      if (sub.equals("")) {
         return 0;
      } else if (str == null) {
         return 0;
      } else {
         int count = 0;

         for(int idx = 0; (idx = str.indexOf(sub, idx)) != -1; idx += sub.length()) {
            ++count;
         }

         return count;
      }
   }

   public static boolean isAlpha(String str) {
      if (str == null) {
         return false;
      } else {
         int sz = str.length();

         for(int i = 0; i < sz; ++i) {
            if (!Character.isLetter(str.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isWhitespace(String str) {
      if (str == null) {
         return false;
      } else {
         int sz = str.length();

         for(int i = 0; i < sz; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAlphaSpace(String str) {
      if (str == null) {
         return false;
      } else {
         int sz = str.length();

         for(int i = 0; i < sz; ++i) {
            if (!Character.isLetter(str.charAt(i)) && str.charAt(i) != ' ') {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAlphanumeric(String str) {
      if (str == null) {
         return false;
      } else {
         int sz = str.length();

         for(int i = 0; i < sz; ++i) {
            if (!Character.isLetterOrDigit(str.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAlphanumericSpace(String str) {
      if (str == null) {
         return false;
      } else {
         int sz = str.length();

         for(int i = 0; i < sz; ++i) {
            if (!Character.isLetterOrDigit(str.charAt(i)) && str.charAt(i) != ' ') {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isNumeric(String str) {
      if (str == null) {
         return false;
      } else {
         int sz = str.length();

         for(int i = 0; i < sz; ++i) {
            if (!Character.isDigit(str.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isNumericSpace(String str) {
      if (str == null) {
         return false;
      } else {
         int sz = str.length();

         for(int i = 0; i < sz; ++i) {
            if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != ' ') {
               return false;
            }
         }

         return true;
      }
   }

   public static String defaultString(Object obj) {
      return defaultString(obj, "");
   }

   public static String defaultString(Object obj, String defaultString) {
      return obj == null ? defaultString : obj.toString();
   }

   public static String reverse(String str) {
      return str == null ? null : (new StringBuffer(str)).reverse().toString();
   }

   public static String reverseDelimitedString(String str, String delimiter) {
      String[] strs = split(str, delimiter);
      reverseArray(strs);
      return join((Object[])strs, delimiter);
   }

   private static void reverseArray(Object[] array) {
      int i = 0;

      for(int j = array.length - 1; j > i; ++i) {
         Object tmp = array[j];
         array[j] = array[i];
         array[i] = tmp;
         --j;
      }

   }

   public static String abbreviate(String s, int maxWidth) {
      return abbreviate(s, 0, maxWidth);
   }

   public static String abbreviate(String s, int offset, int maxWidth) {
      if (maxWidth < 4) {
         throw new IllegalArgumentException("Minimum abbreviation width is 4");
      } else if (s.length() <= maxWidth) {
         return s;
      } else {
         if (offset > s.length()) {
            offset = s.length();
         }

         if (s.length() - offset < maxWidth - 3) {
            offset = s.length() - (maxWidth - 3);
         }

         if (offset <= 4) {
            return s.substring(0, maxWidth - 3) + "...";
         } else if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
         } else {
            return offset + (maxWidth - 3) < s.length() ? "..." + abbreviate(s.substring(offset), maxWidth - 3) : "..." + s.substring(s.length() - (maxWidth - 3));
         }
      }
   }

   public static String difference(String s1, String s2) {
      int at = differenceAt(s1, s2);
      return at == -1 ? "" : s2.substring(at);
   }

   public static int differenceAt(String s1, String s2) {
      int i;
      for(i = 0; i < s1.length() && i < s2.length() && s1.charAt(i) == s2.charAt(i); ++i) {
      }

      return i >= s2.length() && i >= s1.length() ? -1 : i;
   }

   public static String interpolate(String text, Map namespace) {
      Iterator keys = namespace.keySet().iterator();

      while(keys.hasNext()) {
         String key = keys.next().toString();
         Object obj = namespace.get(key);
         if (obj == null) {
            throw new NullPointerException("The value of the key '" + key + "' is null.");
         }

         String value = obj.toString();
         text = replace(text, "${" + key + "}", value);
         if (key.indexOf(" ") == -1) {
            text = replace(text, "$" + key, value);
         }
      }

      return text;
   }

   public static String removeAndHump(String data, String replaceThis) {
      StringBuffer out = new StringBuffer();
      StringTokenizer st = new StringTokenizer(data, replaceThis);

      while(st.hasMoreTokens()) {
         String element = (String)st.nextElement();
         out.append(capitalizeFirstLetter(element));
      }

      return out.toString();
   }

   public static String capitalizeFirstLetter(String data) {
      char firstLetter = Character.toTitleCase(data.substring(0, 1).charAt(0));
      String restLetters = data.substring(1);
      return firstLetter + restLetters;
   }

   public static String lowercaseFirstLetter(String data) {
      char firstLetter = Character.toLowerCase(data.substring(0, 1).charAt(0));
      String restLetters = data.substring(1);
      return firstLetter + restLetters;
   }

   public static String addAndDeHump(String view) {
      StringBuffer sb = new StringBuffer();

      for(int i = 0; i < view.length(); ++i) {
         if (i != 0 && Character.isUpperCase(view.charAt(i))) {
            sb.append('-');
         }

         sb.append(view.charAt(i));
      }

      return sb.toString().trim().toLowerCase(Locale.ENGLISH);
   }

   public static String quoteAndEscape(String source, char quoteChar) {
      return quoteAndEscape(source, quoteChar, new char[]{quoteChar}, new char[]{' '}, '\\', false);
   }

   public static String quoteAndEscape(String source, char quoteChar, char[] quotingTriggers) {
      return quoteAndEscape(source, quoteChar, new char[]{quoteChar}, quotingTriggers, '\\', false);
   }

   public static String quoteAndEscape(String source, char quoteChar, char[] escapedChars, char escapeChar, boolean force) {
      return quoteAndEscape(source, quoteChar, escapedChars, new char[]{' '}, escapeChar, force);
   }

   public static String quoteAndEscape(String source, char quoteChar, char[] escapedChars, char[] quotingTriggers, char escapeChar, boolean force) {
      if (source == null) {
         return null;
      } else if (!force && source.startsWith(Character.toString(quoteChar)) && source.endsWith(Character.toString(quoteChar))) {
         return source;
      } else {
         String escaped = escape(source, escapedChars, escapeChar);
         boolean quote = false;
         if (force) {
            quote = true;
         } else if (!escaped.equals(source)) {
            quote = true;
         } else {
            for(int i = 0; i < quotingTriggers.length; ++i) {
               if (escaped.indexOf(quotingTriggers[i]) > -1) {
                  quote = true;
                  break;
               }
            }
         }

         return quote ? quoteChar + escaped + quoteChar : escaped;
      }
   }

   public static String escape(String source, char[] escapedChars, char escapeChar) {
      if (source == null) {
         return null;
      } else {
         char[] eqc = new char[escapedChars.length];
         System.arraycopy(escapedChars, 0, eqc, 0, escapedChars.length);
         Arrays.sort(eqc);
         StringBuffer buffer = new StringBuffer(source.length());
         int escapeCount = 0;

         for(int i = 0; i < source.length(); ++i) {
            char c = source.charAt(i);
            int result = Arrays.binarySearch(eqc, c);
            if (result > -1) {
               buffer.append(escapeChar);
               ++escapeCount;
            }

            buffer.append(c);
         }

         return buffer.toString();
      }
   }

   public static String removeDuplicateWhitespace(String s) {
      String patternStr = "\\s+";
      String replaceStr = " ";
      Pattern pattern = Pattern.compile(patternStr);
      Matcher matcher = pattern.matcher(s);
      return matcher.replaceAll(replaceStr);
   }

   public static String unifyLineSeparators(String s) {
      return unifyLineSeparators(s, System.getProperty("line.separator"));
   }

   public static String unifyLineSeparators(String s, String ls) {
      if (s == null) {
         return null;
      } else {
         if (ls == null) {
            ls = System.getProperty("line.separator");
         }

         if (!ls.equals("\n") && !ls.equals("\r") && !ls.equals("\r\n")) {
            throw new IllegalArgumentException("Requested line separator is invalid.");
         } else {
            int length = s.length();
            StringBuffer buffer = new StringBuffer(length);

            for(int i = 0; i < length; ++i) {
               if (s.charAt(i) == '\r') {
                  if (i + 1 < length && s.charAt(i + 1) == '\n') {
                     ++i;
                  }

                  buffer.append(ls);
               } else if (s.charAt(i) == '\n') {
                  buffer.append(ls);
               } else {
                  buffer.append(s.charAt(i));
               }
            }

            return buffer.toString();
         }
      }
   }

   public static boolean contains(String str, char searchChar) {
      if (isEmpty(str)) {
         return false;
      } else {
         return str.indexOf(searchChar) >= 0;
      }
   }

   public static boolean contains(String str, String searchStr) {
      if (str != null && searchStr != null) {
         return str.indexOf(searchStr) >= 0;
      } else {
         return false;
      }
   }
}
