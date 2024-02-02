package cn.hutool.core.text;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.comparator.VersionComparator;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.text.finder.CharFinder;
import cn.hutool.core.text.finder.StrFinder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharSequenceUtil {
   public static final int INDEX_NOT_FOUND = -1;
   public static final String NULL = "null";
   public static final String EMPTY = "";
   public static final String SPACE = " ";

   public static boolean isBlank(CharSequence str) {
      int length;
      if (str != null && (length = str.length()) != 0) {
         for(int i = 0; i < length; ++i) {
            if (!CharUtil.isBlankChar(str.charAt(i))) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean isNotBlank(CharSequence str) {
      return !isBlank(str);
   }

   public static boolean hasBlank(CharSequence... strs) {
      if (ArrayUtil.isEmpty((Object[])strs)) {
         return true;
      } else {
         CharSequence[] var1 = strs;
         int var2 = strs.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            CharSequence str = var1[var3];
            if (isBlank(str)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isAllBlank(CharSequence... strs) {
      if (ArrayUtil.isEmpty((Object[])strs)) {
         return true;
      } else {
         CharSequence[] var1 = strs;
         int var2 = strs.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            CharSequence str = var1[var3];
            if (isNotBlank(str)) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isEmpty(CharSequence str) {
      return str == null || str.length() == 0;
   }

   public static boolean isNotEmpty(CharSequence str) {
      return !isEmpty(str);
   }

   public static String emptyIfNull(CharSequence str) {
      return nullToEmpty(str);
   }

   public static String nullToEmpty(CharSequence str) {
      return nullToDefault(str, "");
   }

   public static String nullToDefault(CharSequence str, String defaultStr) {
      return str == null ? defaultStr : str.toString();
   }

   public static String emptyToDefault(CharSequence str, String defaultStr) {
      return isEmpty(str) ? defaultStr : str.toString();
   }

   public static String blankToDefault(CharSequence str, String defaultStr) {
      return isBlank(str) ? defaultStr : str.toString();
   }

   public static String emptyToNull(CharSequence str) {
      return isEmpty(str) ? null : str.toString();
   }

   public static boolean hasEmpty(CharSequence... strs) {
      if (ArrayUtil.isEmpty((Object[])strs)) {
         return true;
      } else {
         CharSequence[] var1 = strs;
         int var2 = strs.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            CharSequence str = var1[var3];
            if (isEmpty(str)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isAllEmpty(CharSequence... strs) {
      if (ArrayUtil.isEmpty((Object[])strs)) {
         return true;
      } else {
         CharSequence[] var1 = strs;
         int var2 = strs.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            CharSequence str = var1[var3];
            if (isNotEmpty(str)) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAllNotEmpty(CharSequence... args) {
      return !hasEmpty(args);
   }

   public static boolean isAllNotBlank(CharSequence... args) {
      return !hasBlank(args);
   }

   public static boolean isNullOrUndefined(CharSequence str) {
      return null == str ? true : isNullOrUndefinedStr(str);
   }

   public static boolean isEmptyOrUndefined(CharSequence str) {
      return isEmpty(str) ? true : isNullOrUndefinedStr(str);
   }

   public static boolean isBlankOrUndefined(CharSequence str) {
      return isBlank(str) ? true : isNullOrUndefinedStr(str);
   }

   private static boolean isNullOrUndefinedStr(CharSequence str) {
      String strString = str.toString().trim();
      return "null".equals(strString) || "undefined".equals(strString);
   }

   public static String trim(CharSequence str) {
      return null == str ? null : trim(str, 0);
   }

   public static String trimToEmpty(CharSequence str) {
      return str == null ? "" : trim(str);
   }

   public static String trimToNull(CharSequence str) {
      String trimStr = trim(str);
      return "".equals(trimStr) ? null : trimStr;
   }

   public static String trimStart(CharSequence str) {
      return trim(str, -1);
   }

   public static String trimEnd(CharSequence str) {
      return trim(str, 1);
   }

   public static String trim(CharSequence str, int mode) {
      return trim(str, mode, CharUtil::isBlankChar);
   }

   public static String trim(CharSequence str, int mode, Predicate<Character> predicate) {
      String result;
      if (str == null) {
         result = null;
      } else {
         int length = str.length();
         int start = 0;
         int end = length;
         if (mode <= 0) {
            while(start < end && predicate.test(str.charAt(start))) {
               ++start;
            }
         }

         if (mode >= 0) {
            while(start < end && predicate.test(str.charAt(end - 1))) {
               --end;
            }
         }

         if (start <= 0 && end >= length) {
            result = str.toString();
         } else {
            result = str.toString().substring(start, end);
         }
      }

      return result;
   }

   public static boolean startWith(CharSequence str, char c) {
      if (isEmpty(str)) {
         return false;
      } else {
         return c == str.charAt(0);
      }
   }

   public static boolean startWith(CharSequence str, CharSequence prefix, boolean ignoreCase) {
      return startWith(str, prefix, ignoreCase, false);
   }

   public static boolean startWith(CharSequence str, CharSequence prefix, boolean ignoreCase, boolean ignoreEquals) {
      if (null != str && null != prefix) {
         boolean isStartWith = str.toString().regionMatches(ignoreCase, 0, prefix.toString(), 0, prefix.length());
         if (!isStartWith) {
            return false;
         } else {
            return !ignoreEquals || !equals(str, prefix, ignoreCase);
         }
      } else if (ignoreEquals) {
         return false;
      } else {
         return null == str && null == prefix;
      }
   }

   public static boolean startWith(CharSequence str, CharSequence prefix) {
      return startWith(str, prefix, false);
   }

   public static boolean startWithIgnoreEquals(CharSequence str, CharSequence prefix) {
      return startWith(str, prefix, false, true);
   }

   public static boolean startWithIgnoreCase(CharSequence str, CharSequence prefix) {
      return startWith(str, prefix, true);
   }

   public static boolean startWithAny(CharSequence str, CharSequence... prefixes) {
      if (!isEmpty(str) && !ArrayUtil.isEmpty((Object[])prefixes)) {
         CharSequence[] var2 = prefixes;
         int var3 = prefixes.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence suffix = var2[var4];
            if (startWith(str, suffix, false)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean startWithAnyIgnoreCase(CharSequence str, CharSequence... suffixes) {
      if (!isEmpty(str) && !ArrayUtil.isEmpty((Object[])suffixes)) {
         CharSequence[] var2 = suffixes;
         int var3 = suffixes.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence suffix = var2[var4];
            if (startWith(str, suffix, true)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean endWith(CharSequence str, char c) {
      if (isEmpty(str)) {
         return false;
      } else {
         return c == str.charAt(str.length() - 1);
      }
   }

   public static boolean endWith(CharSequence str, CharSequence suffix, boolean ignoreCase) {
      return endWith(str, suffix, ignoreCase, false);
   }

   public static boolean endWith(CharSequence str, CharSequence suffix, boolean ignoreCase, boolean ignoreEquals) {
      if (null != str && null != suffix) {
         int strOffset = str.length() - suffix.length();
         boolean isEndWith = str.toString().regionMatches(ignoreCase, strOffset, suffix.toString(), 0, suffix.length());
         if (!isEndWith) {
            return false;
         } else {
            return !ignoreEquals || !equals(str, suffix, ignoreCase);
         }
      } else if (ignoreEquals) {
         return false;
      } else {
         return null == str && null == suffix;
      }
   }

   public static boolean endWith(CharSequence str, CharSequence suffix) {
      return endWith(str, suffix, false);
   }

   public static boolean endWithIgnoreCase(CharSequence str, CharSequence suffix) {
      return endWith(str, suffix, true);
   }

   public static boolean endWithAny(CharSequence str, CharSequence... suffixes) {
      if (!isEmpty(str) && !ArrayUtil.isEmpty((Object[])suffixes)) {
         CharSequence[] var2 = suffixes;
         int var3 = suffixes.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence suffix = var2[var4];
            if (endWith(str, suffix, false)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean endWithAnyIgnoreCase(CharSequence str, CharSequence... suffixes) {
      if (!isEmpty(str) && !ArrayUtil.isEmpty((Object[])suffixes)) {
         CharSequence[] var2 = suffixes;
         int var3 = suffixes.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence suffix = var2[var4];
            if (endWith(str, suffix, true)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean contains(CharSequence str, char searchChar) {
      return indexOf(str, searchChar) > -1;
   }

   public static boolean contains(CharSequence str, CharSequence searchStr) {
      return null != str && null != searchStr ? str.toString().contains(searchStr) : false;
   }

   public static boolean containsAny(CharSequence str, CharSequence... testStrs) {
      return null != getContainsStr(str, testStrs);
   }

   public static boolean containsAny(CharSequence str, char... testChars) {
      if (!isEmpty(str)) {
         int len = str.length();

         for(int i = 0; i < len; ++i) {
            if (ArrayUtil.contains(testChars, str.charAt(i))) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean containsOnly(CharSequence str, char... testChars) {
      if (!isEmpty(str)) {
         int len = str.length();

         for(int i = 0; i < len; ++i) {
            if (!ArrayUtil.contains(testChars, str.charAt(i))) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean containsBlank(CharSequence str) {
      if (null == str) {
         return false;
      } else {
         int length = str.length();
         if (0 == length) {
            return false;
         } else {
            for(int i = 0; i < length; ++i) {
               if (CharUtil.isBlankChar(str.charAt(i))) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public static String getContainsStr(CharSequence str, CharSequence... testStrs) {
      if (!isEmpty(str) && !ArrayUtil.isEmpty((Object[])testStrs)) {
         CharSequence[] var2 = testStrs;
         int var3 = testStrs.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence checkStr = var2[var4];
            if (str.toString().contains(checkStr)) {
               return checkStr.toString();
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static boolean containsIgnoreCase(CharSequence str, CharSequence testStr) {
      if (null == str) {
         return null == testStr;
      } else {
         return indexOfIgnoreCase(str, testStr) > -1;
      }
   }

   public static boolean containsAnyIgnoreCase(CharSequence str, CharSequence... testStrs) {
      return null != getContainsStrIgnoreCase(str, testStrs);
   }

   public static String getContainsStrIgnoreCase(CharSequence str, CharSequence... testStrs) {
      if (!isEmpty(str) && !ArrayUtil.isEmpty((Object[])testStrs)) {
         CharSequence[] var2 = testStrs;
         int var3 = testStrs.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence testStr = var2[var4];
            if (containsIgnoreCase(str, testStr)) {
               return testStr.toString();
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static int indexOf(CharSequence str, char searchChar) {
      return indexOf(str, searchChar, 0);
   }

   public static int indexOf(CharSequence str, char searchChar, int start) {
      return str instanceof String ? ((String)str).indexOf(searchChar, start) : indexOf(str, searchChar, start, -1);
   }

   public static int indexOf(CharSequence text, char searchChar, int start, int end) {
      return isEmpty(text) ? -1 : (new CharFinder(searchChar)).setText(text).setEndIndex(end).start(start);
   }

   public static int indexOfIgnoreCase(CharSequence str, CharSequence searchStr) {
      return indexOfIgnoreCase(str, searchStr, 0);
   }

   public static int indexOfIgnoreCase(CharSequence str, CharSequence searchStr, int fromIndex) {
      return indexOf(str, searchStr, fromIndex, true);
   }

   public static int indexOf(CharSequence text, CharSequence searchStr, int from, boolean ignoreCase) {
      if (!isEmpty(text) && !isEmpty(searchStr)) {
         return (new StrFinder(searchStr, ignoreCase)).setText(text).start(from);
      } else {
         return StrUtil.equals(text, searchStr) ? 0 : -1;
      }
   }

   public static int lastIndexOfIgnoreCase(CharSequence str, CharSequence searchStr) {
      return lastIndexOfIgnoreCase(str, searchStr, str.length());
   }

   public static int lastIndexOfIgnoreCase(CharSequence str, CharSequence searchStr, int fromIndex) {
      return lastIndexOf(str, searchStr, fromIndex, true);
   }

   public static int lastIndexOf(CharSequence text, CharSequence searchStr, int from, boolean ignoreCase) {
      if (!isEmpty(text) && !isEmpty(searchStr)) {
         return (new StrFinder(searchStr, ignoreCase)).setText(text).setNegative(true).start(from);
      } else {
         return StrUtil.equals(text, searchStr) ? 0 : -1;
      }
   }

   public static int ordinalIndexOf(CharSequence str, CharSequence searchStr, int ordinal) {
      if (str != null && searchStr != null && ordinal > 0) {
         if (searchStr.length() == 0) {
            return 0;
         } else {
            int found = 0;
            int index = -1;

            do {
               index = indexOf(str, searchStr, index + 1, false);
               if (index < 0) {
                  return index;
               }

               ++found;
            } while(found < ordinal);

            return index;
         }
      } else {
         return -1;
      }
   }

   public static String removeAll(CharSequence str, CharSequence strToRemove) {
      return !isEmpty(str) && !isEmpty(strToRemove) ? str.toString().replace(strToRemove, "") : str(str);
   }

   public static String removeAny(CharSequence str, CharSequence... strsToRemove) {
      String result = str(str);
      if (isNotEmpty(str)) {
         CharSequence[] var3 = strsToRemove;
         int var4 = strsToRemove.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            CharSequence strToRemove = var3[var5];
            result = removeAll(result, (CharSequence)strToRemove);
         }
      }

      return result;
   }

   public static String removeAll(CharSequence str, char... chars) {
      if (null != str && !ArrayUtil.isEmpty((char[])chars)) {
         int len = str.length();
         if (0 == len) {
            return str(str);
         } else {
            StringBuilder builder = new StringBuilder(len);

            for(int i = 0; i < len; ++i) {
               char c = str.charAt(i);
               if (!ArrayUtil.contains(chars, c)) {
                  builder.append(c);
               }
            }

            return builder.toString();
         }
      } else {
         return str(str);
      }
   }

   public static String removeAllLineBreaks(CharSequence str) {
      return removeAll(str, '\r', '\n');
   }

   public static String removePreAndLowerFirst(CharSequence str, int preLength) {
      if (str == null) {
         return null;
      } else if (str.length() > preLength) {
         char first = Character.toLowerCase(str.charAt(preLength));
         return str.length() > preLength + 1 ? first + str.toString().substring(preLength + 1) : String.valueOf(first);
      } else {
         return str.toString();
      }
   }

   public static String removePreAndLowerFirst(CharSequence str, CharSequence prefix) {
      return lowerFirst(removePrefix(str, prefix));
   }

   public static String removePrefix(CharSequence str, CharSequence prefix) {
      if (!isEmpty(str) && !isEmpty(prefix)) {
         String str2 = str.toString();
         return str2.startsWith(prefix.toString()) ? subSuf(str2, prefix.length()) : str2;
      } else {
         return str(str);
      }
   }

   public static String removePrefixIgnoreCase(CharSequence str, CharSequence prefix) {
      if (!isEmpty(str) && !isEmpty(prefix)) {
         String str2 = str.toString();
         return startWithIgnoreCase(str, prefix) ? subSuf(str2, prefix.length()) : str2;
      } else {
         return str(str);
      }
   }

   public static String removeSuffix(CharSequence str, CharSequence suffix) {
      if (!isEmpty(str) && !isEmpty(suffix)) {
         String str2 = str.toString();
         return str2.endsWith(suffix.toString()) ? subPre(str2, str2.length() - suffix.length()) : str2;
      } else {
         return str(str);
      }
   }

   public static String removeSufAndLowerFirst(CharSequence str, CharSequence suffix) {
      return lowerFirst(removeSuffix(str, suffix));
   }

   public static String removeSuffixIgnoreCase(CharSequence str, CharSequence suffix) {
      if (!isEmpty(str) && !isEmpty(suffix)) {
         String str2 = str.toString();
         return endWithIgnoreCase(str, suffix) ? subPre(str2, str2.length() - suffix.length()) : str2;
      } else {
         return str(str);
      }
   }

   public static String cleanBlank(CharSequence str) {
      return filter(str, (c) -> {
         return !CharUtil.isBlankChar(c);
      });
   }

   public static String strip(CharSequence str, CharSequence prefixOrSuffix) {
      return equals(str, prefixOrSuffix) ? "" : strip(str, prefixOrSuffix, prefixOrSuffix);
   }

   public static String strip(CharSequence str, CharSequence prefix, CharSequence suffix) {
      if (isEmpty(str)) {
         return str(str);
      } else {
         int from = 0;
         int to = str.length();
         String str2 = str.toString();
         if (startWith(str2, prefix)) {
            from = prefix.length();
         }

         if (endWith(str2, suffix)) {
            to -= suffix.length();
         }

         return str2.substring(Math.min(from, to), Math.max(from, to));
      }
   }

   public static String stripIgnoreCase(CharSequence str, CharSequence prefixOrSuffix) {
      return stripIgnoreCase(str, prefixOrSuffix, prefixOrSuffix);
   }

   public static String stripIgnoreCase(CharSequence str, CharSequence prefix, CharSequence suffix) {
      if (isEmpty(str)) {
         return str(str);
      } else {
         int from = 0;
         int to = str.length();
         String str2 = str.toString();
         if (startWithIgnoreCase(str2, prefix)) {
            from = prefix.length();
         }

         if (endWithIgnoreCase(str2, suffix)) {
            to -= suffix.length();
         }

         return str2.substring(from, to);
      }
   }

   public static String addPrefixIfNot(CharSequence str, CharSequence prefix) {
      return prependIfMissing(str, prefix, prefix);
   }

   public static String addSuffixIfNot(CharSequence str, CharSequence suffix) {
      return appendIfMissing(str, suffix, suffix);
   }

   public static long[] splitToLong(CharSequence str, char separator) {
      return (long[])Convert.convert((Class)long[].class, splitTrim(str, separator));
   }

   public static long[] splitToLong(CharSequence str, CharSequence separator) {
      return (long[])Convert.convert((Class)long[].class, splitTrim(str, separator));
   }

   public static int[] splitToInt(CharSequence str, char separator) {
      return (int[])Convert.convert((Class)int[].class, splitTrim(str, separator));
   }

   public static int[] splitToInt(CharSequence str, CharSequence separator) {
      return (int[])Convert.convert((Class)int[].class, splitTrim(str, separator));
   }

   public static List<String> split(CharSequence str, char separator) {
      return split(str, separator, 0);
   }

   public static String[] splitToArray(CharSequence str, CharSequence separator) {
      return str == null ? new String[0] : StrSplitter.splitToArray((CharSequence)str.toString(), (String)str(separator), 0, false, false);
   }

   public static String[] splitToArray(CharSequence str, char separator) {
      return splitToArray(str, separator, 0);
   }

   public static String[] splitToArray(CharSequence text, char separator, int limit) {
      Assert.notNull(text, "Text must be not null!");
      return StrSplitter.splitToArray(text.toString(), separator, limit, false, false);
   }

   public static List<String> split(CharSequence str, char separator, int limit) {
      return split(str, separator, limit, false, false);
   }

   public static List<String> splitTrim(CharSequence str, char separator) {
      return splitTrim(str, separator, -1);
   }

   public static List<String> splitTrim(CharSequence str, CharSequence separator) {
      return splitTrim(str, separator, -1);
   }

   public static List<String> splitTrim(CharSequence str, char separator, int limit) {
      return split(str, separator, limit, true, true);
   }

   public static List<String> splitTrim(CharSequence str, CharSequence separator, int limit) {
      return split(str, separator, limit, true, true);
   }

   public static List<String> split(CharSequence str, char separator, boolean isTrim, boolean ignoreEmpty) {
      return split(str, separator, 0, isTrim, ignoreEmpty);
   }

   public static List<String> split(CharSequence str, char separator, int limit, boolean isTrim, boolean ignoreEmpty) {
      return StrSplitter.split(str, separator, limit, isTrim, ignoreEmpty);
   }

   public static <R> List<R> split(CharSequence str, char separator, int limit, boolean ignoreEmpty, Function<String, R> mapping) {
      return StrSplitter.split(str, separator, limit, ignoreEmpty, mapping);
   }

   public static List<String> split(CharSequence str, CharSequence separator) {
      return split(str, separator, false, false);
   }

   public static List<String> split(CharSequence str, CharSequence separator, boolean isTrim, boolean ignoreEmpty) {
      return split(str, separator, 0, isTrim, ignoreEmpty);
   }

   public static List<String> split(CharSequence str, CharSequence separator, int limit, boolean isTrim, boolean ignoreEmpty) {
      String separatorStr = null == separator ? null : separator.toString();
      return StrSplitter.split(str, separatorStr, limit, isTrim, ignoreEmpty);
   }

   public static String[] split(CharSequence str, int len) {
      return StrSplitter.splitByLength(str, len);
   }

   public static String[] cut(CharSequence str, int partLength) {
      if (null == str) {
         return null;
      } else {
         int len = str.length();
         if (len < partLength) {
            return new String[]{str.toString()};
         } else {
            int part = NumberUtil.count(len, partLength);
            String[] array = new String[part];
            String str2 = str.toString();

            for(int i = 0; i < part; ++i) {
               array[i] = str2.substring(i * partLength, i == part - 1 ? len : partLength + i * partLength);
            }

            return array;
         }
      }
   }

   public static String sub(CharSequence str, int fromIndexInclude, int toIndexExclude) {
      if (isEmpty(str)) {
         return str(str);
      } else {
         int len = str.length();
         if (fromIndexInclude < 0) {
            fromIndexInclude += len;
            if (fromIndexInclude < 0) {
               fromIndexInclude = 0;
            }
         } else if (fromIndexInclude > len) {
            fromIndexInclude = len;
         }

         if (toIndexExclude < 0) {
            toIndexExclude += len;
            if (toIndexExclude < 0) {
               toIndexExclude = len;
            }
         } else if (toIndexExclude > len) {
            toIndexExclude = len;
         }

         if (toIndexExclude < fromIndexInclude) {
            int tmp = fromIndexInclude;
            fromIndexInclude = toIndexExclude;
            toIndexExclude = tmp;
         }

         return fromIndexInclude == toIndexExclude ? "" : str.toString().substring(fromIndexInclude, toIndexExclude);
      }
   }

   public static String subByCodePoint(CharSequence str, int fromIndex, int toIndex) {
      if (isEmpty(str)) {
         return str(str);
      } else if (fromIndex >= 0 && fromIndex <= toIndex) {
         if (fromIndex == toIndex) {
            return "";
         } else {
            StringBuilder sb = new StringBuilder();
            int subLen = toIndex - fromIndex;
            str.toString().codePoints().skip((long)fromIndex).limit((long)subLen).forEach((v) -> {
               sb.append(Character.toChars(v));
            });
            return sb.toString();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static String subPreGbk(CharSequence str, int len, CharSequence suffix) {
      return subPreGbk(str, len, true) + suffix;
   }

   public static String subPreGbk(CharSequence str, int len, boolean halfUp) {
      if (isEmpty(str)) {
         return str(str);
      } else {
         int counterOfDoubleByte = 0;
         byte[] b = bytes(str, CharsetUtil.CHARSET_GBK);
         if (b.length <= len) {
            return str.toString();
         } else {
            for(int i = 0; i < len; ++i) {
               if (b[i] < 0) {
                  ++counterOfDoubleByte;
               }
            }

            if (counterOfDoubleByte % 2 != 0) {
               if (halfUp) {
                  ++len;
               } else {
                  --len;
               }
            }

            return new String(b, 0, len, CharsetUtil.CHARSET_GBK);
         }
      }
   }

   public static String subPre(CharSequence string, int toIndexExclude) {
      return sub(string, 0, toIndexExclude);
   }

   public static String subSuf(CharSequence string, int fromIndex) {
      return isEmpty(string) ? null : sub(string, fromIndex, string.length());
   }

   public static String subSufByLength(CharSequence string, int length) {
      if (isEmpty(string)) {
         return null;
      } else {
         return length <= 0 ? "" : sub(string, -length, string.length());
      }
   }

   public static String subWithLength(String input, int fromIndex, int length) {
      return sub(input, fromIndex, fromIndex + length);
   }

   public static String subBefore(CharSequence string, CharSequence separator, boolean isLastSeparator) {
      if (!isEmpty(string) && separator != null) {
         String str = string.toString();
         String sep = separator.toString();
         if (sep.isEmpty()) {
            return "";
         } else {
            int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
            if (-1 == pos) {
               return str;
            } else {
               return 0 == pos ? "" : str.substring(0, pos);
            }
         }
      } else {
         return null == string ? null : string.toString();
      }
   }

   public static String subBefore(CharSequence string, char separator, boolean isLastSeparator) {
      if (isEmpty(string)) {
         return null == string ? null : "";
      } else {
         String str = string.toString();
         int pos = isLastSeparator ? str.lastIndexOf(separator) : str.indexOf(separator);
         if (-1 == pos) {
            return str;
         } else {
            return 0 == pos ? "" : str.substring(0, pos);
         }
      }
   }

   public static String subAfter(CharSequence string, CharSequence separator, boolean isLastSeparator) {
      if (isEmpty(string)) {
         return null == string ? null : "";
      } else if (separator == null) {
         return "";
      } else {
         String str = string.toString();
         String sep = separator.toString();
         int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
         return -1 != pos && string.length() - 1 != pos ? str.substring(pos + separator.length()) : "";
      }
   }

   public static String subAfter(CharSequence string, char separator, boolean isLastSeparator) {
      if (isEmpty(string)) {
         return null == string ? null : "";
      } else {
         String str = string.toString();
         int pos = isLastSeparator ? str.lastIndexOf(separator) : str.indexOf(separator);
         return -1 == pos ? "" : str.substring(pos + 1);
      }
   }

   public static String subBetween(CharSequence str, CharSequence before, CharSequence after) {
      if (str != null && before != null && after != null) {
         String str2 = str.toString();
         String before2 = before.toString();
         String after2 = after.toString();
         int start = str2.indexOf(before2);
         if (start != -1) {
            int end = str2.indexOf(after2, start + before2.length());
            if (end != -1) {
               return str2.substring(start + before2.length(), end);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static String subBetween(CharSequence str, CharSequence beforeAndAfter) {
      return subBetween(str, beforeAndAfter, beforeAndAfter);
   }

   public static String[] subBetweenAll(CharSequence str, CharSequence prefix, CharSequence suffix) {
      if (!hasEmpty(str, prefix, suffix) && contains(str, prefix)) {
         List<String> result = new LinkedList();
         String[] split = splitToArray(str, prefix);
         int suffixIndex;
         if (prefix.equals(suffix)) {
            suffixIndex = 1;

            for(int length = split.length - 1; suffixIndex < length; suffixIndex += 2) {
               result.add(split[suffixIndex]);
            }
         } else {
            for(int i = 1; i < split.length; ++i) {
               String fragment = split[i];
               suffixIndex = fragment.indexOf(suffix.toString());
               if (suffixIndex > 0) {
                  result.add(fragment.substring(0, suffixIndex));
               }
            }
         }

         return (String[])result.toArray(new String[0]);
      } else {
         return new String[0];
      }
   }

   public static String[] subBetweenAll(CharSequence str, CharSequence prefixAndSuffix) {
      return subBetweenAll(str, prefixAndSuffix, prefixAndSuffix);
   }

   public static String repeat(char c, int count) {
      if (count <= 0) {
         return "";
      } else {
         char[] result = new char[count];

         for(int i = 0; i < count; ++i) {
            result[i] = c;
         }

         return new String(result);
      }
   }

   public static String repeat(CharSequence str, int count) {
      if (null == str) {
         return null;
      } else if (count > 0 && str.length() != 0) {
         if (count == 1) {
            return str.toString();
         } else {
            int len = str.length();
            long longSize = (long)len * (long)count;
            int size = (int)longSize;
            if ((long)size != longSize) {
               throw new ArrayIndexOutOfBoundsException("Required String length is too large: " + longSize);
            } else {
               char[] array = new char[size];
               str.toString().getChars(0, len, array, 0);

               int n;
               for(n = len; n < size - n; n <<= 1) {
                  System.arraycopy(array, 0, array, n, n);
               }

               System.arraycopy(array, 0, array, n, size - n);
               return new String(array);
            }
         }
      } else {
         return "";
      }
   }

   public static String repeatByLength(CharSequence str, int padLen) {
      if (null == str) {
         return null;
      } else if (padLen <= 0) {
         return "";
      } else {
         int strLen = str.length();
         if (strLen == padLen) {
            return str.toString();
         } else if (strLen > padLen) {
            return subPre(str, padLen);
         } else {
            char[] padding = new char[padLen];

            for(int i = 0; i < padLen; ++i) {
               padding[i] = str.charAt(i % strLen);
            }

            return new String(padding);
         }
      }
   }

   public static String repeatAndJoin(CharSequence str, int count, CharSequence delimiter) {
      if (count <= 0) {
         return "";
      } else {
         StringBuilder builder = new StringBuilder(str.length() * count);
         builder.append(str);
         --count;

         for(boolean isAppendDelimiter = isNotEmpty(delimiter); count-- > 0; builder.append(str)) {
            if (isAppendDelimiter) {
               builder.append(delimiter);
            }
         }

         return builder.toString();
      }
   }

   public static boolean equals(CharSequence str1, CharSequence str2) {
      return equals(str1, str2, false);
   }

   public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
      return equals(str1, str2, true);
   }

   public static boolean equals(CharSequence str1, CharSequence str2, boolean ignoreCase) {
      if (null == str1) {
         return str2 == null;
      } else if (null == str2) {
         return false;
      } else {
         return ignoreCase ? str1.toString().equalsIgnoreCase(str2.toString()) : str1.toString().contentEquals(str2);
      }
   }

   public static boolean equalsAnyIgnoreCase(CharSequence str1, CharSequence... strs) {
      return equalsAny(str1, true, strs);
   }

   public static boolean equalsAny(CharSequence str1, CharSequence... strs) {
      return equalsAny(str1, false, strs);
   }

   public static boolean equalsAny(CharSequence str1, boolean ignoreCase, CharSequence... strs) {
      if (ArrayUtil.isEmpty((Object[])strs)) {
         return false;
      } else {
         CharSequence[] var3 = strs;
         int var4 = strs.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            CharSequence str = var3[var5];
            if (equals(str1, str, ignoreCase)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean equalsCharAt(CharSequence str, int position, char c) {
      if (null != str && position >= 0) {
         return str.length() > position && c == str.charAt(position);
      } else {
         return false;
      }
   }

   public static boolean isSubEquals(CharSequence str1, int start1, CharSequence str2, boolean ignoreCase) {
      return isSubEquals(str1, start1, str2, 0, str2.length(), ignoreCase);
   }

   public static boolean isSubEquals(CharSequence str1, int start1, CharSequence str2, int start2, int length, boolean ignoreCase) {
      return null != str1 && null != str2 ? str1.toString().regionMatches(ignoreCase, start1, str2.toString(), start2, length) : false;
   }

   public static String format(CharSequence template, Object... params) {
      if (null == template) {
         return "null";
      } else {
         return !ArrayUtil.isEmpty(params) && !isBlank(template) ? StrFormatter.format(template.toString(), params) : template.toString();
      }
   }

   public static String indexedFormat(CharSequence pattern, Object... arguments) {
      return MessageFormat.format(pattern.toString(), arguments);
   }

   public static byte[] utf8Bytes(CharSequence str) {
      return bytes(str, CharsetUtil.CHARSET_UTF_8);
   }

   public static byte[] bytes(CharSequence str) {
      return bytes(str, Charset.defaultCharset());
   }

   public static byte[] bytes(CharSequence str, String charset) {
      return bytes(str, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
   }

   public static byte[] bytes(CharSequence str, Charset charset) {
      if (str == null) {
         return null;
      } else {
         return null == charset ? str.toString().getBytes() : str.toString().getBytes(charset);
      }
   }

   public static ByteBuffer byteBuffer(CharSequence str, String charset) {
      return ByteBuffer.wrap(bytes(str, charset));
   }

   public static String wrap(CharSequence str, CharSequence prefixAndSuffix) {
      return wrap(str, prefixAndSuffix, prefixAndSuffix);
   }

   public static String wrap(CharSequence str, CharSequence prefix, CharSequence suffix) {
      return nullToEmpty(prefix).concat(nullToEmpty(str)).concat(nullToEmpty(suffix));
   }

   public static String[] wrapAllWithPair(CharSequence prefixAndSuffix, CharSequence... strs) {
      return wrapAll(prefixAndSuffix, prefixAndSuffix, strs);
   }

   public static String[] wrapAll(CharSequence prefix, CharSequence suffix, CharSequence... strs) {
      String[] results = new String[strs.length];

      for(int i = 0; i < strs.length; ++i) {
         results[i] = wrap(strs[i], prefix, suffix);
      }

      return results;
   }

   public static String wrapIfMissing(CharSequence str, CharSequence prefix, CharSequence suffix) {
      int len = 0;
      if (isNotEmpty(str)) {
         len += str.length();
      }

      if (isNotEmpty(prefix)) {
         len += prefix.length();
      }

      if (isNotEmpty(suffix)) {
         len += suffix.length();
      }

      StringBuilder sb = new StringBuilder(len);
      if (isNotEmpty(prefix) && !startWith(str, prefix)) {
         sb.append(prefix);
      }

      if (isNotEmpty(str)) {
         sb.append(str);
      }

      if (isNotEmpty(suffix) && !endWith(str, suffix)) {
         sb.append(suffix);
      }

      return sb.toString();
   }

   public static String[] wrapAllWithPairIfMissing(CharSequence prefixAndSuffix, CharSequence... strs) {
      return wrapAllIfMissing(prefixAndSuffix, prefixAndSuffix, strs);
   }

   public static String[] wrapAllIfMissing(CharSequence prefix, CharSequence suffix, CharSequence... strs) {
      String[] results = new String[strs.length];

      for(int i = 0; i < strs.length; ++i) {
         results[i] = wrapIfMissing(strs[i], prefix, suffix);
      }

      return results;
   }

   public static String unWrap(CharSequence str, String prefix, String suffix) {
      return isWrap(str, prefix, suffix) ? sub(str, prefix.length(), str.length() - suffix.length()) : str.toString();
   }

   public static String unWrap(CharSequence str, char prefix, char suffix) {
      if (isEmpty(str)) {
         return str(str);
      } else {
         return str.charAt(0) == prefix && str.charAt(str.length() - 1) == suffix ? sub(str, 1, str.length() - 1) : str.toString();
      }
   }

   public static String unWrap(CharSequence str, char prefixAndSuffix) {
      return unWrap(str, prefixAndSuffix, prefixAndSuffix);
   }

   public static boolean isWrap(CharSequence str, String prefix, String suffix) {
      if (ArrayUtil.hasNull(str, prefix, suffix)) {
         return false;
      } else {
         String str2 = str.toString();
         return str2.startsWith(prefix) && str2.endsWith(suffix);
      }
   }

   public static boolean isWrap(CharSequence str, String wrapper) {
      return isWrap(str, wrapper, wrapper);
   }

   public static boolean isWrap(CharSequence str, char wrapper) {
      return isWrap(str, wrapper, wrapper);
   }

   public static boolean isWrap(CharSequence str, char prefixChar, char suffixChar) {
      if (null == str) {
         return false;
      } else {
         return str.charAt(0) == prefixChar && str.charAt(str.length() - 1) == suffixChar;
      }
   }

   public static String padPre(CharSequence str, int length, CharSequence padStr) {
      if (null == str) {
         return null;
      } else {
         int strLen = str.length();
         if (strLen == length) {
            return str.toString();
         } else {
            return strLen > length ? subPre(str, length) : repeatByLength(padStr, length - strLen).concat(str.toString());
         }
      }
   }

   public static String padPre(CharSequence str, int length, char padChar) {
      if (null == str) {
         return null;
      } else {
         int strLen = str.length();
         if (strLen == length) {
            return str.toString();
         } else {
            return strLen > length ? subPre(str, length) : repeat(padChar, length - strLen).concat(str.toString());
         }
      }
   }

   public static String padAfter(CharSequence str, int length, char padChar) {
      if (null == str) {
         return null;
      } else {
         int strLen = str.length();
         if (strLen == length) {
            return str.toString();
         } else {
            return strLen > length ? sub(str, strLen - length, strLen) : str.toString().concat(repeat(padChar, length - strLen));
         }
      }
   }

   public static String padAfter(CharSequence str, int length, CharSequence padStr) {
      if (null == str) {
         return null;
      } else {
         int strLen = str.length();
         if (strLen == length) {
            return str.toString();
         } else {
            return strLen > length ? subSufByLength(str, length) : str.toString().concat(repeatByLength(padStr, length - strLen));
         }
      }
   }

   public static String center(CharSequence str, int size) {
      return center(str, size, ' ');
   }

   public static String center(CharSequence str, int size, char padChar) {
      if (str != null && size > 0) {
         int strLen = str.length();
         int pads = size - strLen;
         if (pads <= 0) {
            return str.toString();
         } else {
            CharSequence str = padPre(str, strLen + pads / 2, padChar);
            str = padAfter(str, size, padChar);
            return str.toString();
         }
      } else {
         return str(str);
      }
   }

   public static String center(CharSequence str, int size, CharSequence padStr) {
      if (str != null && size > 0) {
         if (isEmpty((CharSequence)padStr)) {
            padStr = " ";
         }

         int strLen = str.length();
         int pads = size - strLen;
         if (pads <= 0) {
            return str.toString();
         } else {
            CharSequence str = padPre(str, strLen + pads / 2, (CharSequence)padStr);
            str = padAfter(str, size, (CharSequence)padStr);
            return str.toString();
         }
      } else {
         return str(str);
      }
   }

   public static String str(CharSequence cs) {
      return null == cs ? null : cs.toString();
   }

   public static int count(CharSequence content, CharSequence strForSearch) {
      if (!hasEmpty(content, strForSearch) && strForSearch.length() <= content.length()) {
         int count = 0;
         int idx = 0;
         String content2 = content.toString();

         for(String strForSearch2 = strForSearch.toString(); (idx = content2.indexOf(strForSearch2, idx)) > -1; idx += strForSearch.length()) {
            ++count;
         }

         return count;
      } else {
         return 0;
      }
   }

   public static int count(CharSequence content, char charForSearch) {
      int count = 0;
      if (isEmpty(content)) {
         return 0;
      } else {
         int contentLength = content.length();

         for(int i = 0; i < contentLength; ++i) {
            if (charForSearch == content.charAt(i)) {
               ++count;
            }
         }

         return count;
      }
   }

   public static int compare(CharSequence str1, CharSequence str2, boolean nullIsLess) {
      if (str1 == str2) {
         return 0;
      } else if (str1 == null) {
         return nullIsLess ? -1 : 1;
      } else if (str2 == null) {
         return nullIsLess ? 1 : -1;
      } else {
         return str1.toString().compareTo(str2.toString());
      }
   }

   public static int compareIgnoreCase(CharSequence str1, CharSequence str2, boolean nullIsLess) {
      if (str1 == str2) {
         return 0;
      } else if (str1 == null) {
         return nullIsLess ? -1 : 1;
      } else if (str2 == null) {
         return nullIsLess ? 1 : -1;
      } else {
         return str1.toString().compareToIgnoreCase(str2.toString());
      }
   }

   public static int compareVersion(CharSequence version1, CharSequence version2) {
      return VersionComparator.INSTANCE.compare(str(version1), str(version2));
   }

   public static String appendIfMissing(CharSequence str, CharSequence suffix, CharSequence... suffixes) {
      return appendIfMissing(str, suffix, false, suffixes);
   }

   public static String appendIfMissingIgnoreCase(CharSequence str, CharSequence suffix, CharSequence... suffixes) {
      return appendIfMissing(str, suffix, true, suffixes);
   }

   public static String appendIfMissing(CharSequence str, CharSequence suffix, boolean ignoreCase, CharSequence... testSuffixes) {
      if (str != null && !isEmpty(suffix) && !endWith(str, suffix, ignoreCase)) {
         if (ArrayUtil.isNotEmpty((Object[])testSuffixes)) {
            CharSequence[] var4 = testSuffixes;
            int var5 = testSuffixes.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               CharSequence testSuffix = var4[var6];
               if (endWith(str, testSuffix, ignoreCase)) {
                  return str.toString();
               }
            }
         }

         return str.toString().concat(suffix.toString());
      } else {
         return str(str);
      }
   }

   public static String prependIfMissing(CharSequence str, CharSequence prefix, CharSequence... prefixes) {
      return prependIfMissing(str, prefix, false, prefixes);
   }

   public static String prependIfMissingIgnoreCase(CharSequence str, CharSequence prefix, CharSequence... prefixes) {
      return prependIfMissing(str, prefix, true, prefixes);
   }

   public static String prependIfMissing(CharSequence str, CharSequence prefix, boolean ignoreCase, CharSequence... prefixes) {
      if (str != null && !isEmpty(prefix) && !startWith(str, prefix, ignoreCase)) {
         if (prefixes != null && prefixes.length > 0) {
            CharSequence[] var4 = prefixes;
            int var5 = prefixes.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               CharSequence s = var4[var6];
               if (startWith(str, s, ignoreCase)) {
                  return str.toString();
               }
            }
         }

         return prefix.toString().concat(str.toString());
      } else {
         return str(str);
      }
   }

   public static String replaceIgnoreCase(CharSequence str, CharSequence searchStr, CharSequence replacement) {
      return replace(str, 0, searchStr, replacement, true);
   }

   public static String replace(CharSequence str, CharSequence searchStr, CharSequence replacement) {
      return replace(str, 0, searchStr, replacement, false);
   }

   public static String replace(CharSequence str, CharSequence searchStr, CharSequence replacement, boolean ignoreCase) {
      return replace(str, 0, searchStr, replacement, ignoreCase);
   }

   public static String replace(CharSequence str, int fromIndex, CharSequence searchStr, CharSequence replacement, boolean ignoreCase) {
      if (!isEmpty(str) && !isEmpty(searchStr)) {
         if (null == replacement) {
            replacement = "";
         }

         int strLength = str.length();
         int searchStrLength = searchStr.length();
         if (strLength < searchStrLength) {
            return str(str);
         } else if (fromIndex > strLength) {
            return str(str);
         } else {
            if (fromIndex < 0) {
               fromIndex = 0;
            }

            StringBuilder result = new StringBuilder(strLength - searchStrLength + ((CharSequence)replacement).length());
            if (0 != fromIndex) {
               result.append(str.subSequence(0, fromIndex));
            }

            int preIndex;
            int index;
            for(preIndex = fromIndex; (index = indexOf(str, searchStr, preIndex, ignoreCase)) > -1; preIndex = index + searchStrLength) {
               result.append(str.subSequence(preIndex, index));
               result.append((CharSequence)replacement);
            }

            if (preIndex < strLength) {
               result.append(str.subSequence(preIndex, strLength));
            }

            return result.toString();
         }
      } else {
         return str(str);
      }
   }

   public static String replace(CharSequence str, int startInclude, int endExclude, char replacedChar) {
      if (isEmpty(str)) {
         return str(str);
      } else {
         String originalStr = str(str);
         int[] strCodePoints = originalStr.codePoints().toArray();
         int strLength = strCodePoints.length;
         if (startInclude > strLength) {
            return originalStr;
         } else {
            if (endExclude > strLength) {
               endExclude = strLength;
            }

            if (startInclude > endExclude) {
               return originalStr;
            } else {
               StringBuilder stringBuilder = new StringBuilder();

               for(int i = 0; i < strLength; ++i) {
                  if (i >= startInclude && i < endExclude) {
                     stringBuilder.append(replacedChar);
                  } else {
                     stringBuilder.append(new String(strCodePoints, i, 1));
                  }
               }

               return stringBuilder.toString();
            }
         }
      }
   }

   public static String replace(CharSequence str, int startInclude, int endExclude, CharSequence replacedStr) {
      if (isEmpty(str)) {
         return str(str);
      } else {
         String originalStr = str(str);
         int[] strCodePoints = originalStr.codePoints().toArray();
         int strLength = strCodePoints.length;
         if (startInclude > strLength) {
            return originalStr;
         } else {
            if (endExclude > strLength) {
               endExclude = strLength;
            }

            if (startInclude > endExclude) {
               return originalStr;
            } else {
               StringBuilder stringBuilder = new StringBuilder();

               int i;
               for(i = 0; i < startInclude; ++i) {
                  stringBuilder.append(new String(strCodePoints, i, 1));
               }

               stringBuilder.append(replacedStr);

               for(i = endExclude; i < strLength; ++i) {
                  stringBuilder.append(new String(strCodePoints, i, 1));
               }

               return stringBuilder.toString();
            }
         }
      }
   }

   public static String replace(CharSequence str, Pattern pattern, Func1<Matcher, String> replaceFun) {
      return ReUtil.replaceAll(str, pattern, replaceFun);
   }

   public static String replace(CharSequence str, String regex, Func1<Matcher, String> replaceFun) {
      return ReUtil.replaceAll(str, regex, replaceFun);
   }

   public static String hide(CharSequence str, int startInclude, int endExclude) {
      return replace(str, startInclude, endExclude, '*');
   }

   public static String desensitized(CharSequence str, DesensitizedUtil.DesensitizedType desensitizedType) {
      return DesensitizedUtil.desensitized(str, desensitizedType);
   }

   public static String replaceChars(CharSequence str, String chars, CharSequence replacedStr) {
      return !isEmpty(str) && !isEmpty(chars) ? replaceChars(str, chars.toCharArray(), replacedStr) : str(str);
   }

   public static String replaceChars(CharSequence str, char[] chars, CharSequence replacedStr) {
      if (!isEmpty(str) && !ArrayUtil.isEmpty((char[])chars)) {
         Set<Character> set = new HashSet(chars.length);
         char[] var4 = chars;
         int var5 = chars.length;

         int i;
         for(int var6 = 0; var6 < var5; ++var6) {
            i = var4[var6];
            set.add(Character.valueOf((char)i));
         }

         int strLen = str.length();
         StringBuilder builder = new StringBuilder();

         for(i = 0; i < strLen; ++i) {
            char c = str.charAt(i);
            builder.append(set.contains(c) ? replacedStr : c);
         }

         return builder.toString();
      } else {
         return str(str);
      }
   }

   public static int length(CharSequence cs) {
      return cs == null ? 0 : cs.length();
   }

   public static int byteLength(CharSequence cs, Charset charset) {
      return cs == null ? 0 : cs.toString().getBytes(charset).length;
   }

   public static int totalLength(CharSequence... strs) {
      int totalLength = 0;
      CharSequence[] var2 = strs;
      int var3 = strs.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         CharSequence str = var2[var4];
         totalLength += null == str ? 0 : str.length();
      }

      return totalLength;
   }

   public static String maxLength(CharSequence string, int length) {
      Assert.isTrue(length > 0);
      if (null == string) {
         return null;
      } else {
         return string.length() <= length ? string.toString() : sub(string, 0, length) + "...";
      }
   }

   public static <T extends CharSequence> T firstNonNull(T... strs) {
      return (CharSequence)ArrayUtil.firstNonNull(strs);
   }

   public static <T extends CharSequence> T firstNonEmpty(T... strs) {
      return (CharSequence)ArrayUtil.firstMatch(CharSequenceUtil::isNotEmpty, strs);
   }

   public static <T extends CharSequence> T firstNonBlank(T... strs) {
      return (CharSequence)ArrayUtil.firstMatch(CharSequenceUtil::isNotBlank, strs);
   }

   public static String upperFirstAndAddPre(CharSequence str, String preString) {
      return str != null && preString != null ? preString + upperFirst(str) : null;
   }

   public static String upperFirst(CharSequence str) {
      if (null == str) {
         return null;
      } else {
         if (str.length() > 0) {
            char firstChar = str.charAt(0);
            if (Character.isLowerCase(firstChar)) {
               return Character.toUpperCase(firstChar) + subSuf(str, 1);
            }
         }

         return str.toString();
      }
   }

   public static String lowerFirst(CharSequence str) {
      if (null == str) {
         return null;
      } else {
         if (str.length() > 0) {
            char firstChar = str.charAt(0);
            if (Character.isUpperCase(firstChar)) {
               return Character.toLowerCase(firstChar) + subSuf(str, 1);
            }
         }

         return str.toString();
      }
   }

   public static String filter(CharSequence str, Filter<Character> filter) {
      if (str != null && filter != null) {
         int len = str.length();
         StringBuilder sb = new StringBuilder(len);

         for(int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            if (filter.accept(c)) {
               sb.append(c);
            }
         }

         return sb.toString();
      } else {
         return str(str);
      }
   }

   public static boolean isUpperCase(CharSequence str) {
      if (null == str) {
         return false;
      } else {
         int len = str.length();

         for(int i = 0; i < len; ++i) {
            if (Character.isLowerCase(str.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isLowerCase(CharSequence str) {
      if (null == str) {
         return false;
      } else {
         int len = str.length();

         for(int i = 0; i < len; ++i) {
            if (Character.isUpperCase(str.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static String swapCase(String str) {
      if (isEmpty(str)) {
         return str;
      } else {
         char[] buffer = str.toCharArray();

         for(int i = 0; i < buffer.length; ++i) {
            char ch = buffer[i];
            if (Character.isUpperCase(ch)) {
               buffer[i] = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
               buffer[i] = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
               buffer[i] = Character.toUpperCase(ch);
            }
         }

         return new String(buffer);
      }
   }

   public static String toUnderlineCase(CharSequence str) {
      return NamingCase.toUnderlineCase(str);
   }

   public static String toSymbolCase(CharSequence str, char symbol) {
      return NamingCase.toSymbolCase(str, symbol);
   }

   public static String toCamelCase(CharSequence name) {
      return NamingCase.toCamelCase(name);
   }

   public static String toCamelCase(CharSequence name, char symbol) {
      return NamingCase.toCamelCase(name, symbol);
   }

   public static boolean isSurround(CharSequence str, CharSequence prefix, CharSequence suffix) {
      if (StrUtil.isBlank(str)) {
         return false;
      } else if (str.length() < prefix.length() + suffix.length()) {
         return false;
      } else {
         String str2 = str.toString();
         return str2.startsWith(prefix.toString()) && str2.endsWith(suffix.toString());
      }
   }

   public static boolean isSurround(CharSequence str, char prefix, char suffix) {
      if (StrUtil.isBlank(str)) {
         return false;
      } else if (str.length() < 2) {
         return false;
      } else {
         return str.charAt(0) == prefix && str.charAt(str.length() - 1) == suffix;
      }
   }

   public static StringBuilder builder(CharSequence... strs) {
      StringBuilder sb = new StringBuilder();
      CharSequence[] var2 = strs;
      int var3 = strs.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         CharSequence str = var2[var4];
         sb.append(str);
      }

      return sb;
   }

   public static StrBuilder strBuilder(CharSequence... strs) {
      return StrBuilder.create(strs);
   }

   public static String getGeneralField(CharSequence getOrSetMethodName) {
      String getOrSetMethodNameStr = getOrSetMethodName.toString();
      if (!getOrSetMethodNameStr.startsWith("get") && !getOrSetMethodNameStr.startsWith("set")) {
         return getOrSetMethodNameStr.startsWith("is") ? removePreAndLowerFirst(getOrSetMethodName, 2) : null;
      } else {
         return removePreAndLowerFirst(getOrSetMethodName, 3);
      }
   }

   public static String genSetter(CharSequence fieldName) {
      return upperFirstAndAddPre(fieldName, "set");
   }

   public static String genGetter(CharSequence fieldName) {
      return upperFirstAndAddPre(fieldName, "get");
   }

   public static String concat(boolean isNullToEmpty, CharSequence... strs) {
      StrBuilder sb = new StrBuilder();
      CharSequence[] var3 = strs;
      int var4 = strs.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         CharSequence str = var3[var5];
         sb.append((CharSequence)(isNullToEmpty ? nullToEmpty(str) : str));
      }

      return sb.toString();
   }

   public static String brief(CharSequence str, int maxLength) {
      if (null == str) {
         return null;
      } else {
         int strLength = str.length();
         if (maxLength > 0 && strLength > maxLength) {
            switch (maxLength) {
               case 1:
                  return String.valueOf(str.charAt(0));
               case 2:
                  return str.charAt(0) + ".";
               case 3:
                  return str.charAt(0) + "." + str.charAt(strLength - 1);
               case 4:
                  return str.charAt(0) + ".." + str.charAt(strLength - 1);
               default:
                  int suffixLength = (maxLength - 3) / 2;
                  int preLength = suffixLength + (maxLength - 3) % 2;
                  String str2 = str.toString();
                  return format("{}...{}", str2.substring(0, preLength), str2.substring(strLength - suffixLength));
            }
         } else {
            return str.toString();
         }
      }
   }

   public static String join(CharSequence conjunction, Object... objs) {
      return ArrayUtil.join(objs, conjunction);
   }

   public static <T> String join(CharSequence conjunction, Iterable<T> iterable) {
      return CollUtil.join(iterable, conjunction);
   }

   public static boolean isAllCharMatch(CharSequence value, cn.hutool.core.lang.Matcher<Character> matcher) {
      if (StrUtil.isBlank(value)) {
         return false;
      } else {
         int i = value.length();

         do {
            --i;
            if (i < 0) {
               return true;
            }
         } while(matcher.match(value.charAt(i)));

         return false;
      }
   }

   public static boolean isNumeric(CharSequence str) {
      return isAllCharMatch(str, Character::isDigit);
   }

   public static String move(CharSequence str, int startInclude, int endExclude, int moveLength) {
      if (isEmpty(str)) {
         return str(str);
      } else {
         int len = str.length();
         if (Math.abs(moveLength) > len) {
            moveLength %= len;
         }

         StringBuilder strBuilder = new StringBuilder(len);
         int startAfterMove;
         if (moveLength > 0) {
            startAfterMove = Math.min(endExclude + moveLength, str.length());
            strBuilder.append(str.subSequence(0, startInclude)).append(str.subSequence(endExclude, startAfterMove)).append(str.subSequence(startInclude, endExclude)).append(str.subSequence(startAfterMove, str.length()));
         } else {
            if (moveLength >= 0) {
               return str(str);
            }

            startAfterMove = Math.max(startInclude + moveLength, 0);
            strBuilder.append(str.subSequence(0, startAfterMove)).append(str.subSequence(startInclude, endExclude)).append(str.subSequence(startAfterMove, startInclude)).append(str.subSequence(endExclude, str.length()));
         }

         return strBuilder.toString();
      }
   }

   public static boolean isCharEquals(CharSequence str) {
      Assert.notEmpty(str, "Str to check must be not empty!");
      return count(str, str.charAt(0)) == str.length();
   }

   public static String normalize(CharSequence str) {
      return Normalizer.normalize(str, Form.NFC);
   }

   public static String fixLength(CharSequence str, char fixedChar, int length) {
      int fixedLength = length - str.length();
      return fixedLength <= 0 ? str.toString() : str + repeat(fixedChar, fixedLength);
   }
}
