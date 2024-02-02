package org.codehaus.plexus.util;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

public final class SelectorUtils {
   public static final String PATTERN_HANDLER_PREFIX = "[";
   public static final String PATTERN_HANDLER_SUFFIX = "]";
   public static final String REGEX_HANDLER_PREFIX = "%regex[";
   public static final String ANT_HANDLER_PREFIX = "%ant[";
   private static SelectorUtils instance = new SelectorUtils();

   private SelectorUtils() {
   }

   public static SelectorUtils getInstance() {
      return instance;
   }

   public static boolean matchPatternStart(String pattern, String str) {
      return matchPatternStart(pattern, str, true);
   }

   public static boolean matchPatternStart(String pattern, String str, boolean isCaseSensitive) {
      if (pattern.length() > "%regex[".length() + "]".length() + 1 && pattern.startsWith("%regex[") && pattern.endsWith("]")) {
         return true;
      } else {
         if (pattern.length() > "%ant[".length() + "]".length() + 1 && pattern.startsWith("%ant[") && pattern.endsWith("]")) {
            pattern = pattern.substring("%ant[".length(), pattern.length() - "]".length());
         }

         String altStr = str.replace('\\', '/');
         return matchAntPathPatternStart(pattern, str, File.separator, isCaseSensitive) || matchAntPathPatternStart(pattern, altStr, "/", isCaseSensitive);
      }
   }

   private static boolean matchAntPathPatternStart(String pattern, String str, String separator, boolean isCaseSensitive) {
      if (str.startsWith(separator) != pattern.startsWith(separator)) {
         return false;
      } else {
         Vector patDirs = tokenizePath(pattern, separator);
         Vector strDirs = tokenizePath(str, separator);
         int patIdxStart = 0;
         int patIdxEnd = patDirs.size() - 1;
         int strIdxStart = 0;

         int strIdxEnd;
         for(strIdxEnd = strDirs.size() - 1; patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd; ++strIdxStart) {
            String patDir = (String)patDirs.elementAt(patIdxStart);
            if (patDir.equals("**")) {
               break;
            }

            if (!match(patDir, (String)strDirs.elementAt(strIdxStart), isCaseSensitive)) {
               return false;
            }

            ++patIdxStart;
         }

         if (strIdxStart > strIdxEnd) {
            return true;
         } else {
            return patIdxStart <= patIdxEnd;
         }
      }
   }

   public static boolean matchPath(String pattern, String str) {
      return matchPath(pattern, str, true);
   }

   public static boolean matchPath(String pattern, String str, boolean isCaseSensitive) {
      if (pattern.length() > "%regex[".length() + "]".length() + 1 && pattern.startsWith("%regex[") && pattern.endsWith("]")) {
         pattern = pattern.substring("%regex[".length(), pattern.length() - "]".length());
         return str.matches(pattern);
      } else {
         if (pattern.length() > "%ant[".length() + "]".length() + 1 && pattern.startsWith("%ant[") && pattern.endsWith("]")) {
            pattern = pattern.substring("%ant[".length(), pattern.length() - "]".length());
         }

         return matchAntPathPattern(pattern, str, isCaseSensitive);
      }
   }

   private static boolean matchAntPathPattern(String pattern, String str, boolean isCaseSensitive) {
      if (str.startsWith(File.separator) != pattern.startsWith(File.separator)) {
         return false;
      } else {
         Vector patDirs = tokenizePath(pattern, File.separator);
         Vector strDirs = tokenizePath(str, File.separator);
         int patIdxStart = 0;
         int patIdxEnd = patDirs.size() - 1;
         int strIdxStart = 0;

         int strIdxEnd;
         String patDir;
         for(strIdxEnd = strDirs.size() - 1; patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd; ++strIdxStart) {
            patDir = (String)patDirs.elementAt(patIdxStart);
            if (patDir.equals("**")) {
               break;
            }

            if (!match(patDir, (String)strDirs.elementAt(strIdxStart), isCaseSensitive)) {
               patDirs = null;
               strDirs = null;
               return false;
            }

            ++patIdxStart;
         }

         int patIdxTmp;
         if (strIdxStart > strIdxEnd) {
            for(patIdxTmp = patIdxStart; patIdxTmp <= patIdxEnd; ++patIdxTmp) {
               if (!patDirs.elementAt(patIdxTmp).equals("**")) {
                  patDirs = null;
                  strDirs = null;
                  return false;
               }
            }

            return true;
         } else if (patIdxStart > patIdxEnd) {
            patDirs = null;
            strDirs = null;
            return false;
         } else {
            while(patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
               patDir = (String)patDirs.elementAt(patIdxEnd);
               if (patDir.equals("**")) {
                  break;
               }

               if (!match(patDir, (String)strDirs.elementAt(strIdxEnd), isCaseSensitive)) {
                  patDirs = null;
                  strDirs = null;
                  return false;
               }

               --patIdxEnd;
               --strIdxEnd;
            }

            if (strIdxStart > strIdxEnd) {
               for(patIdxTmp = patIdxStart; patIdxTmp <= patIdxEnd; ++patIdxTmp) {
                  if (!patDirs.elementAt(patIdxTmp).equals("**")) {
                     patDirs = null;
                     strDirs = null;
                     return false;
                  }
               }

               return true;
            } else {
               while(patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
                  patIdxTmp = -1;

                  int patLength;
                  for(patLength = patIdxStart + 1; patLength <= patIdxEnd; ++patLength) {
                     if (patDirs.elementAt(patLength).equals("**")) {
                        patIdxTmp = patLength;
                        break;
                     }
                  }

                  if (patIdxTmp == patIdxStart + 1) {
                     ++patIdxStart;
                  } else {
                     patLength = patIdxTmp - patIdxStart - 1;
                     int strLength = strIdxEnd - strIdxStart + 1;
                     int foundIdx = -1;
                     int i = 0;

                     label110:
                     while(i <= strLength - patLength) {
                        for(int j = 0; j < patLength; ++j) {
                           String subPat = (String)patDirs.elementAt(patIdxStart + j + 1);
                           String subStr = (String)strDirs.elementAt(strIdxStart + i + j);
                           if (!match(subPat, subStr, isCaseSensitive)) {
                              ++i;
                              continue label110;
                           }
                        }

                        foundIdx = strIdxStart + i;
                        break;
                     }

                     if (foundIdx == -1) {
                        patDirs = null;
                        strDirs = null;
                        return false;
                     }

                     patIdxStart = patIdxTmp;
                     strIdxStart = foundIdx + patLength;
                  }
               }

               for(patIdxTmp = patIdxStart; patIdxTmp <= patIdxEnd; ++patIdxTmp) {
                  if (!patDirs.elementAt(patIdxTmp).equals("**")) {
                     patDirs = null;
                     strDirs = null;
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   public static boolean match(String pattern, String str) {
      return match(pattern, str, true);
   }

   public static boolean match(String pattern, String str, boolean isCaseSensitive) {
      char[] patArr = pattern.toCharArray();
      char[] strArr = str.toCharArray();
      int patIdxStart = 0;
      int patIdxEnd = patArr.length - 1;
      int strIdxStart = 0;
      int strIdxEnd = strArr.length - 1;
      boolean containsStar = false;

      int patIdxTmp;
      for(patIdxTmp = 0; patIdxTmp < patArr.length; ++patIdxTmp) {
         if (patArr[patIdxTmp] == '*') {
            containsStar = true;
            break;
         }
      }

      char ch;
      if (!containsStar) {
         if (patIdxEnd != strIdxEnd) {
            return false;
         } else {
            for(patIdxTmp = 0; patIdxTmp <= patIdxEnd; ++patIdxTmp) {
               ch = patArr[patIdxTmp];
               if (ch != '?' && !equals(ch, strArr[patIdxTmp], isCaseSensitive)) {
                  return false;
               }
            }

            return true;
         }
      } else if (patIdxEnd == 0) {
         return true;
      } else {
         while((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
            if (ch != '?' && !equals(ch, strArr[strIdxStart], isCaseSensitive)) {
               return false;
            }

            ++patIdxStart;
            ++strIdxStart;
         }

         if (strIdxStart > strIdxEnd) {
            for(patIdxTmp = patIdxStart; patIdxTmp <= patIdxEnd; ++patIdxTmp) {
               if (patArr[patIdxTmp] != '*') {
                  return false;
               }
            }

            return true;
         } else {
            while((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
               if (ch != '?' && !equals(ch, strArr[strIdxEnd], isCaseSensitive)) {
                  return false;
               }

               --patIdxEnd;
               --strIdxEnd;
            }

            if (strIdxStart > strIdxEnd) {
               for(patIdxTmp = patIdxStart; patIdxTmp <= patIdxEnd; ++patIdxTmp) {
                  if (patArr[patIdxTmp] != '*') {
                     return false;
                  }
               }

               return true;
            } else {
               while(patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
                  patIdxTmp = -1;

                  int patLength;
                  for(patLength = patIdxStart + 1; patLength <= patIdxEnd; ++patLength) {
                     if (patArr[patLength] == '*') {
                        patIdxTmp = patLength;
                        break;
                     }
                  }

                  if (patIdxTmp == patIdxStart + 1) {
                     ++patIdxStart;
                  } else {
                     patLength = patIdxTmp - patIdxStart - 1;
                     int strLength = strIdxEnd - strIdxStart + 1;
                     int foundIdx = -1;
                     int i = 0;

                     label132:
                     while(i <= strLength - patLength) {
                        for(int j = 0; j < patLength; ++j) {
                           ch = patArr[patIdxStart + j + 1];
                           if (ch != '?' && !equals(ch, strArr[strIdxStart + i + j], isCaseSensitive)) {
                              ++i;
                              continue label132;
                           }
                        }

                        foundIdx = strIdxStart + i;
                        break;
                     }

                     if (foundIdx == -1) {
                        return false;
                     }

                     patIdxStart = patIdxTmp;
                     strIdxStart = foundIdx + patLength;
                  }
               }

               for(patIdxTmp = patIdxStart; patIdxTmp <= patIdxEnd; ++patIdxTmp) {
                  if (patArr[patIdxTmp] != '*') {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   private static boolean equals(char c1, char c2, boolean isCaseSensitive) {
      if (c1 == c2) {
         return true;
      } else {
         return !isCaseSensitive && (Character.toUpperCase(c1) == Character.toUpperCase(c2) || Character.toLowerCase(c1) == Character.toLowerCase(c2));
      }
   }

   public static Vector tokenizePath(String path) {
      return tokenizePath(path, File.separator);
   }

   public static Vector tokenizePath(String path, String separator) {
      Vector ret = new Vector();
      StringTokenizer st = new StringTokenizer(path, separator);

      while(st.hasMoreTokens()) {
         ret.addElement(st.nextToken());
      }

      return ret;
   }

   public static boolean isOutOfDate(File src, File target, int granularity) {
      if (!src.exists()) {
         return false;
      } else if (!target.exists()) {
         return true;
      } else {
         return src.lastModified() - (long)granularity > target.lastModified();
      }
   }

   public static String removeWhitespace(String input) {
      StringBuffer result = new StringBuffer();
      if (input != null) {
         StringTokenizer st = new StringTokenizer(input);

         while(st.hasMoreTokens()) {
            result.append(st.nextToken());
         }
      }

      return result.toString();
   }
}
