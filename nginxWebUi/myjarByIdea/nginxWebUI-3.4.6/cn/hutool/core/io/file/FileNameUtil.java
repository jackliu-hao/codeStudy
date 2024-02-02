package cn.hutool.core.io.file;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.util.regex.Pattern;

public class FileNameUtil {
   public static final String EXT_JAVA = ".java";
   public static final String EXT_CLASS = ".class";
   public static final String EXT_JAR = ".jar";
   public static final char UNIX_SEPARATOR = '/';
   public static final char WINDOWS_SEPARATOR = '\\';
   private static final Pattern FILE_NAME_INVALID_PATTERN_WIN = Pattern.compile("[\\\\/:*?\"<>|]");
   private static final CharSequence[] SPECIAL_SUFFIX = new CharSequence[]{"tar.bz2", "tar.Z", "tar.gz", "tar.xz"};

   public static String getName(File file) {
      return null != file ? file.getName() : null;
   }

   public static String getName(String filePath) {
      if (null == filePath) {
         return null;
      } else {
         int len = filePath.length();
         if (0 == len) {
            return filePath;
         } else {
            if (CharUtil.isFileSeparator(filePath.charAt(len - 1))) {
               --len;
            }

            int begin = 0;

            for(int i = len - 1; i > -1; --i) {
               char c = filePath.charAt(i);
               if (CharUtil.isFileSeparator(c)) {
                  begin = i + 1;
                  break;
               }
            }

            return filePath.substring(begin, len);
         }
      }
   }

   public static String getSuffix(File file) {
      return extName(file);
   }

   public static String getSuffix(String fileName) {
      return extName(fileName);
   }

   public static String getPrefix(File file) {
      return mainName(file);
   }

   public static String getPrefix(String fileName) {
      return mainName(fileName);
   }

   public static String mainName(File file) {
      return file.isDirectory() ? file.getName() : mainName(file.getName());
   }

   public static String mainName(String fileName) {
      if (null == fileName) {
         return null;
      } else {
         int len = fileName.length();
         if (0 == len) {
            return fileName;
         } else {
            if (CharUtil.isFileSeparator(fileName.charAt(len - 1))) {
               --len;
            }

            int begin = 0;
            int end = len;

            for(int i = len - 1; i >= 0; --i) {
               char c = fileName.charAt(i);
               if (len == end && '.' == c) {
                  end = i;
               }

               if (CharUtil.isFileSeparator(c)) {
                  begin = i + 1;
                  break;
               }
            }

            return fileName.substring(begin, end);
         }
      }
   }

   public static String extName(File file) {
      if (null == file) {
         return null;
      } else {
         return file.isDirectory() ? null : extName(file.getName());
      }
   }

   public static String extName(String fileName) {
      if (fileName == null) {
         return null;
      } else {
         int index = fileName.lastIndexOf(".");
         if (index == -1) {
            return "";
         } else {
            int secondToLastIndex = fileName.substring(0, index).lastIndexOf(".");
            String substr = fileName.substring(secondToLastIndex == -1 ? index : secondToLastIndex + 1);
            if (StrUtil.containsAny(substr, SPECIAL_SUFFIX)) {
               return substr;
            } else {
               String ext = fileName.substring(index + 1);
               return StrUtil.containsAny(ext, new char[]{'/', '\\'}) ? "" : ext;
            }
         }
      }
   }

   public static String cleanInvalid(String fileName) {
      return StrUtil.isBlank(fileName) ? fileName : ReUtil.delAll((Pattern)FILE_NAME_INVALID_PATTERN_WIN, fileName);
   }

   public static boolean containsInvalid(String fileName) {
      return !StrUtil.isBlank(fileName) && ReUtil.contains((Pattern)FILE_NAME_INVALID_PATTERN_WIN, fileName);
   }

   public static boolean isType(String fileName, String... extNames) {
      return StrUtil.equalsAnyIgnoreCase(extName(fileName), extNames);
   }
}
