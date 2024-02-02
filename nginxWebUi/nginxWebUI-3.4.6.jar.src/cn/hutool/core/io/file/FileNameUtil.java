/*     */ package cn.hutool.core.io.file;
/*     */ 
/*     */ import cn.hutool.core.util.CharUtil;
/*     */ import cn.hutool.core.util.ReUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.File;
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
/*     */ public class FileNameUtil
/*     */ {
/*     */   public static final String EXT_JAVA = ".java";
/*     */   public static final String EXT_CLASS = ".class";
/*     */   public static final String EXT_JAR = ".jar";
/*     */   public static final char UNIX_SEPARATOR = '/';
/*     */   public static final char WINDOWS_SEPARATOR = '\\';
/*  43 */   private static final Pattern FILE_NAME_INVALID_PATTERN_WIN = Pattern.compile("[\\\\/:*?\"<>|]");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  48 */   private static final CharSequence[] SPECIAL_SUFFIX = new CharSequence[] { "tar.bz2", "tar.Z", "tar.gz", "tar.xz" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getName(File file) {
/*  61 */     return (null != file) ? file.getName() : null;
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
/*     */   public static String getName(String filePath) {
/*  76 */     if (null == filePath) {
/*  77 */       return null;
/*     */     }
/*  79 */     int len = filePath.length();
/*  80 */     if (0 == len) {
/*  81 */       return filePath;
/*     */     }
/*  83 */     if (CharUtil.isFileSeparator(filePath.charAt(len - 1)))
/*     */     {
/*  85 */       len--;
/*     */     }
/*     */     
/*  88 */     int begin = 0;
/*     */     
/*  90 */     for (int i = len - 1; i > -1; i--) {
/*  91 */       char c = filePath.charAt(i);
/*  92 */       if (CharUtil.isFileSeparator(c)) {
/*     */         
/*  94 */         begin = i + 1;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/*  99 */     return filePath.substring(begin, len);
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
/*     */   public static String getSuffix(File file) {
/* 111 */     return extName(file);
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
/*     */   public static String getSuffix(String fileName) {
/* 123 */     return extName(fileName);
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
/*     */   public static String getPrefix(File file) {
/* 135 */     return mainName(file);
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
/*     */   public static String getPrefix(String fileName) {
/* 147 */     return mainName(fileName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String mainName(File file) {
/* 157 */     if (file.isDirectory()) {
/* 158 */       return file.getName();
/*     */     }
/* 160 */     return mainName(file.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String mainName(String fileName) {
/* 170 */     if (null == fileName) {
/* 171 */       return null;
/*     */     }
/* 173 */     int len = fileName.length();
/* 174 */     if (0 == len) {
/* 175 */       return fileName;
/*     */     }
/* 177 */     if (CharUtil.isFileSeparator(fileName.charAt(len - 1))) {
/* 178 */       len--;
/*     */     }
/*     */     
/* 181 */     int begin = 0;
/* 182 */     int end = len;
/*     */     
/* 184 */     for (int i = len - 1; i >= 0; i--) {
/* 185 */       char c = fileName.charAt(i);
/* 186 */       if (len == end && '.' == c)
/*     */       {
/* 188 */         end = i;
/*     */       }
/*     */       
/* 191 */       if (CharUtil.isFileSeparator(c)) {
/* 192 */         begin = i + 1;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 197 */     return fileName.substring(begin, end);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String extName(File file) {
/* 207 */     if (null == file) {
/* 208 */       return null;
/*     */     }
/* 210 */     if (file.isDirectory()) {
/* 211 */       return null;
/*     */     }
/* 213 */     return extName(file.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String extName(String fileName) {
/* 223 */     if (fileName == null) {
/* 224 */       return null;
/*     */     }
/* 226 */     int index = fileName.lastIndexOf(".");
/* 227 */     if (index == -1) {
/* 228 */       return "";
/*     */     }
/*     */     
/* 231 */     int secondToLastIndex = fileName.substring(0, index).lastIndexOf(".");
/* 232 */     String substr = fileName.substring((secondToLastIndex == -1) ? index : (secondToLastIndex + 1));
/* 233 */     if (StrUtil.containsAny(substr, SPECIAL_SUFFIX)) {
/* 234 */       return substr;
/*     */     }
/*     */     
/* 237 */     String ext = fileName.substring(index + 1); return 
/*     */       
/* 239 */       StrUtil.containsAny(ext, new char[] { '/', '\\' }) ? "" : ext;
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
/*     */   public static String cleanInvalid(String fileName) {
/* 251 */     return StrUtil.isBlank(fileName) ? fileName : ReUtil.delAll(FILE_NAME_INVALID_PATTERN_WIN, fileName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean containsInvalid(String fileName) {
/* 262 */     return (false == StrUtil.isBlank(fileName) && ReUtil.contains(FILE_NAME_INVALID_PATTERN_WIN, fileName));
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
/*     */   public static boolean isType(String fileName, String... extNames) {
/* 274 */     return StrUtil.equalsAnyIgnoreCase(extName(fileName), (CharSequence[])extNames);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\file\FileNameUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */