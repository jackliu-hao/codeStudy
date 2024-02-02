/*      */ package cn.hutool.core.io;
/*      */ 
/*      */ import cn.hutool.core.collection.CollUtil;
/*      */ import cn.hutool.core.io.file.FileCopier;
/*      */ import cn.hutool.core.io.file.FileMode;
/*      */ import cn.hutool.core.io.file.FileNameUtil;
/*      */ import cn.hutool.core.io.file.FileReader;
/*      */ import cn.hutool.core.io.file.FileWriter;
/*      */ import cn.hutool.core.io.file.LineSeparator;
/*      */ import cn.hutool.core.io.file.PathUtil;
/*      */ import cn.hutool.core.io.file.Tailer;
/*      */ import cn.hutool.core.io.resource.ResourceUtil;
/*      */ import cn.hutool.core.io.unit.DataSizeUtil;
/*      */ import cn.hutool.core.lang.Assert;
/*      */ import cn.hutool.core.thread.ThreadUtil;
/*      */ import cn.hutool.core.util.ArrayUtil;
/*      */ import cn.hutool.core.util.CharUtil;
/*      */ import cn.hutool.core.util.CharsetUtil;
/*      */ import cn.hutool.core.util.ClassUtil;
/*      */ import cn.hutool.core.util.ReUtil;
/*      */ import cn.hutool.core.util.StrUtil;
/*      */ import cn.hutool.core.util.URLUtil;
/*      */ import cn.hutool.core.util.ZipUtil;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.BufferedWriter;
/*      */ import java.io.File;
/*      */ import java.io.FileFilter;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.FileReader;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.LineNumberReader;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.RandomAccessFile;
/*      */ import java.io.Reader;
/*      */ import java.net.URI;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.file.DirectoryNotEmptyException;
/*      */ import java.nio.file.Path;
/*      */ import java.nio.file.Paths;
/*      */ import java.nio.file.StandardCopyOption;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Objects;
/*      */ import java.util.function.Consumer;
/*      */ import java.util.jar.JarFile;
/*      */ import java.util.regex.Pattern;
/*      */ import java.util.zip.CRC32;
/*      */ import java.util.zip.Checksum;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class FileUtil
/*      */   extends PathUtil
/*      */ {
/*      */   public static final String CLASS_EXT = ".class";
/*      */   public static final String JAR_FILE_EXT = ".jar";
/*      */   public static final String JAR_PATH_EXT = ".jar!";
/*      */   public static final String PATH_FILE_PRE = "file:";
/*   91 */   public static final String FILE_SEPARATOR = File.separator;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   96 */   public static final String PATH_SEPARATOR = File.pathSeparator;
/*      */ 
/*      */ 
/*      */   
/*  100 */   private static final Pattern PATTERN_PATH_ABSOLUTE = Pattern.compile("^[a-zA-Z]:([/\\\\].*)?");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isWindows() {
/*  110 */     return ('\\' == File.separatorChar);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File[] ls(String path) {
/*  121 */     if (path == null) {
/*  122 */       return null;
/*      */     }
/*      */     
/*  125 */     File file = file(path);
/*  126 */     if (file.isDirectory()) {
/*  127 */       return file.listFiles();
/*      */     }
/*  129 */     throw new IORuntimeException(StrUtil.format("Path [{}] is not directory!", new Object[] { path }));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmpty(File file) {
/*  140 */     if (null == file || false == file.exists()) {
/*  141 */       return true;
/*      */     }
/*      */     
/*  144 */     if (file.isDirectory()) {
/*  145 */       String[] subFiles = file.list();
/*  146 */       return ArrayUtil.isEmpty((Object[])subFiles);
/*  147 */     }  if (file.isFile()) {
/*  148 */       return (file.length() <= 0L);
/*      */     }
/*      */     
/*  151 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotEmpty(File file) {
/*  161 */     return (false == isEmpty(file));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isDirEmpty(File dir) {
/*  171 */     return isDirEmpty(dir.toPath());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<File> loopFiles(String path, FileFilter fileFilter) {
/*  184 */     return loopFiles(file(path), fileFilter);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<File> loopFiles(File file, FileFilter fileFilter) {
/*  196 */     return loopFiles(file, -1, fileFilter);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void walkFiles(File file, Consumer<File> consumer) {
/*  211 */     if (file.isDirectory()) {
/*  212 */       File[] subFiles = file.listFiles();
/*  213 */       if (ArrayUtil.isNotEmpty((Object[])subFiles)) {
/*  214 */         for (File tmp : subFiles) {
/*  215 */           walkFiles(tmp, consumer);
/*      */         }
/*      */       }
/*      */     } else {
/*  219 */       consumer.accept(file);
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
/*      */   public static List<File> loopFiles(File file, int maxDepth, FileFilter fileFilter) {
/*  234 */     return loopFiles(file.toPath(), maxDepth, fileFilter);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<File> loopFiles(String path) {
/*  247 */     return loopFiles(file(path));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<File> loopFiles(File file) {
/*  257 */     return loopFiles(file, (FileFilter)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> listFileNames(String path) throws IORuntimeException {
/*  271 */     if (path == null) {
/*  272 */       return new ArrayList<>(0);
/*      */     }
/*  274 */     int index = path.lastIndexOf(".jar!");
/*  275 */     if (index < 0) {
/*      */       
/*  277 */       List<String> paths = new ArrayList<>();
/*  278 */       File[] files = ls(path);
/*  279 */       for (File file : files) {
/*  280 */         if (file.isFile()) {
/*  281 */           paths.add(file.getName());
/*      */         }
/*      */       } 
/*  284 */       return paths;
/*      */     } 
/*      */ 
/*      */     
/*  288 */     path = getAbsolutePath(path);
/*      */     
/*  290 */     index += ".jar".length();
/*  291 */     JarFile jarFile = null;
/*      */     try {
/*  293 */       jarFile = new JarFile(path.substring(0, index));
/*      */       
/*  295 */       return ZipUtil.listFileNames(jarFile, StrUtil.removePrefix(path.substring(index + 1), "/"));
/*  296 */     } catch (IOException e) {
/*  297 */       throw new IORuntimeException(StrUtil.format("Can not read file path of [{}]", new Object[] { path }), e);
/*      */     } finally {
/*  299 */       IoUtil.close(jarFile);
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
/*      */   public static File newFile(String path) {
/*  311 */     return new File(path);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File file(String path) {
/*  321 */     if (null == path) {
/*  322 */       return null;
/*      */     }
/*  324 */     return new File(getAbsolutePath(path));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File file(String parent, String path) {
/*  336 */     return file(new File(parent), path);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File file(File parent, String path) {
/*  349 */     if (StrUtil.isBlank(path)) {
/*  350 */       throw new NullPointerException("File path is blank!");
/*      */     }
/*  352 */     return checkSlip(parent, buildFile(parent, path));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File file(File directory, String... names) {
/*  365 */     Assert.notNull(directory, "directory must not be null", new Object[0]);
/*  366 */     if (ArrayUtil.isEmpty((Object[])names)) {
/*  367 */       return directory;
/*      */     }
/*      */     
/*  370 */     File file = directory;
/*  371 */     for (String name : names) {
/*  372 */       if (null != name) {
/*  373 */         file = file(file, name);
/*      */       }
/*      */     } 
/*  376 */     return file;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File file(String... names) {
/*  389 */     if (ArrayUtil.isEmpty((Object[])names)) {
/*  390 */       return null;
/*      */     }
/*      */     
/*  393 */     File file = null;
/*  394 */     for (String name : names) {
/*  395 */       if (file == null) {
/*  396 */         file = file(name);
/*      */       } else {
/*  398 */         file = file(file, name);
/*      */       } 
/*      */     } 
/*  401 */     return file;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File file(URI uri) {
/*  411 */     if (uri == null) {
/*  412 */       throw new NullPointerException("File uri is null!");
/*      */     }
/*  414 */     return new File(uri);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File file(URL url) {
/*  424 */     return new File(URLUtil.toURI(url));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getTmpDirPath() {
/*  434 */     return System.getProperty("java.io.tmpdir");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File getTmpDir() {
/*  444 */     return file(getTmpDirPath());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getUserHomePath() {
/*  454 */     return System.getProperty("user.home");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File getUserHomeDir() {
/*  464 */     return file(getUserHomePath());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean exist(String path) {
/*  474 */     return (null != path && file(path).exists());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean exist(File file) {
/*  484 */     return (null != file && file.exists());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean exist(String directory, String regexp) {
/*  495 */     File file = new File(directory);
/*  496 */     if (false == file.exists()) {
/*  497 */       return false;
/*      */     }
/*      */     
/*  500 */     String[] fileList = file.list();
/*  501 */     if (fileList == null) {
/*  502 */       return false;
/*      */     }
/*      */     
/*  505 */     for (String fileName : fileList) {
/*  506 */       if (fileName.matches(regexp)) {
/*  507 */         return true;
/*      */       }
/*      */     } 
/*      */     
/*  511 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Date lastModifiedTime(File file) {
/*  521 */     if (false == exist(file)) {
/*  522 */       return null;
/*      */     }
/*      */     
/*  525 */     return new Date(file.lastModified());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Date lastModifiedTime(String path) {
/*  535 */     return lastModifiedTime(new File(path));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long size(File file) {
/*  548 */     return size(file, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long size(File file, boolean includeDirSize) {
/*  562 */     if (null == file || false == file.exists() || isSymlink(file)) {
/*  563 */       return 0L;
/*      */     }
/*      */     
/*  566 */     if (file.isDirectory()) {
/*  567 */       long size = includeDirSize ? file.length() : 0L;
/*  568 */       File[] subFiles = file.listFiles();
/*  569 */       if (ArrayUtil.isEmpty((Object[])subFiles)) {
/*  570 */         return 0L;
/*      */       }
/*  572 */       for (File subFile : subFiles) {
/*  573 */         size += size(subFile, includeDirSize);
/*      */       }
/*  575 */       return size;
/*      */     } 
/*  577 */     return file.length();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getTotalLines(File file) {
/*  590 */     if (false == isFile(file)) {
/*  591 */       throw new IORuntimeException("Input must be a File");
/*      */     }
/*  593 */     try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file))) {
/*      */       
/*  595 */       lineNumberReader.setLineNumber(1);
/*      */ 
/*      */       
/*  598 */       lineNumberReader.skip(Long.MAX_VALUE);
/*      */       
/*  600 */       return lineNumberReader.getLineNumber();
/*  601 */     } catch (IOException e) {
/*  602 */       throw new IORuntimeException(e);
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
/*      */   public static boolean newerThan(File file, File reference) {
/*  614 */     if (null == reference || false == reference.exists()) {
/*  615 */       return true;
/*      */     }
/*  617 */     return newerThan(file, reference.lastModified());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean newerThan(File file, long timeMillis) {
/*  628 */     if (null == file || false == file.exists()) {
/*  629 */       return false;
/*      */     }
/*  631 */     return (file.lastModified() > timeMillis);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File touch(String path) throws IORuntimeException {
/*  643 */     if (path == null) {
/*  644 */       return null;
/*      */     }
/*  646 */     return touch(file(path));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File touch(File file) throws IORuntimeException {
/*  658 */     if (null == file) {
/*  659 */       return null;
/*      */     }
/*  661 */     if (false == file.exists()) {
/*  662 */       mkParentDirs(file);
/*      */       
/*      */       try {
/*  665 */         file.createNewFile();
/*  666 */       } catch (Exception e) {
/*  667 */         throw new IORuntimeException(e);
/*      */       } 
/*      */     } 
/*  670 */     return file;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File touch(File parent, String path) throws IORuntimeException {
/*  683 */     return touch(file(parent, path));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File touch(String parent, String path) throws IORuntimeException {
/*  696 */     return touch(file(parent, path));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File mkParentDirs(File file) {
/*  706 */     if (null == file) {
/*  707 */       return null;
/*      */     }
/*  709 */     return mkdir(getParent(file, 1));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File mkParentDirs(String path) {
/*  719 */     if (path == null) {
/*  720 */       return null;
/*      */     }
/*  722 */     return mkParentDirs(file(path));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean del(String fullFileOrDirPath) throws IORuntimeException {
/*  735 */     return del(file(fullFileOrDirPath));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean del(File file) throws IORuntimeException {
/*  754 */     if (file == null || false == file.exists())
/*      */     {
/*  756 */       return true;
/*      */     }
/*      */     
/*  759 */     if (file.isDirectory()) {
/*      */       
/*  761 */       boolean isOk = clean(file);
/*  762 */       if (false == isOk) {
/*  763 */         return false;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  768 */     Path path = file.toPath();
/*      */     try {
/*  770 */       delFile(path);
/*  771 */     } catch (DirectoryNotEmptyException e) {
/*      */       
/*  773 */       del(path);
/*  774 */     } catch (IOException e) {
/*  775 */       throw new IORuntimeException(e);
/*      */     } 
/*      */     
/*  778 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean clean(String dirPath) throws IORuntimeException {
/*  792 */     return clean(file(dirPath));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean clean(File directory) throws IORuntimeException {
/*  806 */     if (directory == null || !directory.exists() || false == directory.isDirectory()) {
/*  807 */       return true;
/*      */     }
/*      */     
/*  810 */     File[] files = directory.listFiles();
/*  811 */     if (null != files) {
/*  812 */       for (File childFile : files) {
/*  813 */         if (false == del(childFile))
/*      */         {
/*  815 */           return false;
/*      */         }
/*      */       } 
/*      */     }
/*  819 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean cleanEmpty(File directory) throws IORuntimeException {
/*  833 */     if (directory == null || false == directory.exists() || false == directory.isDirectory()) {
/*  834 */       return true;
/*      */     }
/*      */     
/*  837 */     File[] files = directory.listFiles();
/*  838 */     if (ArrayUtil.isEmpty((Object[])files))
/*      */     {
/*  840 */       return directory.delete();
/*      */     }
/*      */     
/*  843 */     for (File childFile : files) {
/*  844 */       cleanEmpty(childFile);
/*      */     }
/*  846 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File mkdir(String dirPath) {
/*  857 */     if (dirPath == null) {
/*  858 */       return null;
/*      */     }
/*  860 */     File dir = file(dirPath);
/*  861 */     return mkdir(dir);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File mkdir(File dir) {
/*  872 */     if (dir == null) {
/*  873 */       return null;
/*      */     }
/*  875 */     if (false == dir.exists()) {
/*  876 */       mkdirsSafely(dir, 5, 1L);
/*      */     }
/*  878 */     return dir;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean mkdirsSafely(File dir, int tryCount, long sleepMillis) {
/*  900 */     if (dir == null) {
/*  901 */       return false;
/*      */     }
/*  903 */     if (dir.isDirectory()) {
/*  904 */       return true;
/*      */     }
/*  906 */     for (int i = 1; i <= tryCount; i++) {
/*      */ 
/*      */       
/*  909 */       dir.mkdirs();
/*  910 */       if (dir.exists()) {
/*  911 */         return true;
/*      */       }
/*  913 */       ThreadUtil.sleep(sleepMillis);
/*      */     } 
/*  915 */     return dir.exists();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File createTempFile(File dir) throws IORuntimeException {
/*  927 */     return createTempFile("hutool", (String)null, dir, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File createTempFile() throws IORuntimeException {
/*  942 */     return createTempFile("hutool", (String)null, (File)null, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File createTempFile(String suffix, boolean isReCreat) throws IORuntimeException {
/*  959 */     return createTempFile("hutool", suffix, (File)null, isReCreat);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File createTempFile(String prefix, String suffix, boolean isReCreat) throws IORuntimeException {
/*  977 */     return createTempFile(prefix, suffix, (File)null, isReCreat);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File createTempFile(File dir, boolean isReCreat) throws IORuntimeException {
/*  990 */     return createTempFile("hutool", (String)null, dir, isReCreat);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File createTempFile(String prefix, String suffix, File dir, boolean isReCreat) throws IORuntimeException {
/* 1005 */     int exceptionsCount = 0;
/*      */     while (true) {
/*      */       try {
/* 1008 */         File file = File.createTempFile(prefix, suffix, mkdir(dir)).getCanonicalFile();
/* 1009 */         if (isReCreat) {
/*      */           
/* 1011 */           file.delete();
/*      */           
/* 1013 */           file.createNewFile();
/*      */         } 
/* 1015 */         return file;
/* 1016 */       } catch (IOException ioex) {
/* 1017 */         if (++exceptionsCount >= 50) {
/* 1018 */           throw new IORuntimeException(ioex);
/*      */         }
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
/*      */   public static File copyFile(String src, String dest, StandardCopyOption... options) throws IORuntimeException {
/* 1034 */     Assert.notBlank(src, "Source File path is blank !", new Object[0]);
/* 1035 */     Assert.notBlank(dest, "Destination File path is blank !", new Object[0]);
/* 1036 */     return copyFile(Paths.get(src, new String[0]), Paths.get(dest, new String[0]), options).toFile();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File copyFile(File src, File dest, StandardCopyOption... options) throws IORuntimeException {
/* 1050 */     Assert.notNull(src, "Source File is null !", new Object[0]);
/* 1051 */     if (false == src.exists()) {
/* 1052 */       throw new IORuntimeException("File not exist: " + src);
/*      */     }
/* 1054 */     Assert.notNull(dest, "Destination File or directiory is null !", new Object[0]);
/* 1055 */     if (equals(src, dest)) {
/* 1056 */       throw new IORuntimeException("Files '{}' and '{}' are equal", new Object[] { src, dest });
/*      */     }
/* 1058 */     return copyFile(src.toPath(), dest.toPath(), options).toFile();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File copy(String srcPath, String destPath, boolean isOverride) throws IORuntimeException {
/* 1072 */     return copy(file(srcPath), file(destPath), isOverride);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File copy(File src, File dest, boolean isOverride) throws IORuntimeException {
/* 1092 */     return FileCopier.create(src, dest).setOverride(isOverride).copy();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File copyContent(File src, File dest, boolean isOverride) throws IORuntimeException {
/* 1112 */     return FileCopier.create(src, dest).setCopyContentIfDir(true).setOverride(isOverride).copy();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File copyFilesFromDir(File src, File dest, boolean isOverride) throws IORuntimeException {
/* 1133 */     return FileCopier.create(src, dest).setCopyContentIfDir(true).setOnlyCopyFile(true).setOverride(isOverride).copy();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void move(File src, File target, boolean isOverride) throws IORuntimeException {
/* 1146 */     Assert.notNull(src, "Src file must be not null!", new Object[0]);
/* 1147 */     Assert.notNull(target, "target file must be not null!", new Object[0]);
/* 1148 */     move(src.toPath(), target.toPath(), isOverride);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void moveContent(File src, File target, boolean isOverride) throws IORuntimeException {
/* 1162 */     Assert.notNull(src, "Src file must be not null!", new Object[0]);
/* 1163 */     Assert.notNull(target, "target file must be not null!", new Object[0]);
/* 1164 */     moveContent(src.toPath(), target.toPath(), isOverride);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File rename(File file, String newName, boolean isOverride) {
/* 1181 */     return rename(file, newName, false, isOverride);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File rename(File file, String newName, boolean isRetainExt, boolean isOverride) {
/* 1209 */     if (isRetainExt) {
/* 1210 */       String extName = extName(file);
/* 1211 */       if (StrUtil.isNotBlank(extName)) {
/* 1212 */         newName = newName.concat(".").concat(extName);
/*      */       }
/*      */     } 
/* 1215 */     return rename(file.toPath(), newName, isOverride).toFile();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getCanonicalPath(File file) {
/* 1226 */     if (null == file) {
/* 1227 */       return null;
/*      */     }
/*      */     try {
/* 1230 */       return file.getCanonicalPath();
/* 1231 */     } catch (IOException e) {
/* 1232 */       throw new IORuntimeException(e);
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
/*      */   public static String getAbsolutePath(String path, Class<?> baseClass) {
/*      */     String normalPath;
/* 1246 */     if (path == null) {
/* 1247 */       normalPath = "";
/*      */     } else {
/* 1249 */       normalPath = normalize(path);
/* 1250 */       if (isAbsolutePath(normalPath))
/*      */       {
/* 1252 */         return normalPath;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1257 */     URL url = ResourceUtil.getResource(normalPath, baseClass);
/* 1258 */     if (null != url)
/*      */     {
/* 1260 */       return normalize(URLUtil.getDecodedPath(url));
/*      */     }
/*      */ 
/*      */     
/* 1264 */     String classPath = ClassUtil.getClassPath();
/* 1265 */     if (null == classPath)
/*      */     {
/*      */       
/* 1268 */       return path;
/*      */     }
/*      */ 
/*      */     
/* 1272 */     return normalize(classPath.concat(Objects.<String>requireNonNull(path)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getAbsolutePath(String path) {
/* 1284 */     return getAbsolutePath(path, (Class<?>)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getAbsolutePath(File file) {
/* 1294 */     if (file == null) {
/* 1295 */       return null;
/*      */     }
/*      */     
/*      */     try {
/* 1299 */       return file.getCanonicalPath();
/* 1300 */     } catch (IOException e) {
/* 1301 */       return file.getAbsolutePath();
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
/*      */   public static boolean isAbsolutePath(String path) {
/* 1319 */     if (StrUtil.isEmpty(path)) {
/* 1320 */       return false;
/*      */     }
/*      */ 
/*      */     
/* 1324 */     return ('/' == path.charAt(0) || ReUtil.isMatch(PATTERN_PATH_ABSOLUTE, path));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isDirectory(String path) {
/* 1334 */     return (null != path && file(path).isDirectory());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isDirectory(File file) {
/* 1344 */     return (null != file && file.isDirectory());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isFile(String path) {
/* 1354 */     return (null != path && file(path).isFile());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isFile(File file) {
/* 1364 */     return (null != file && file.isFile());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean equals(File file1, File file2) throws IORuntimeException {
/* 1377 */     Assert.notNull(file1);
/* 1378 */     Assert.notNull(file2);
/* 1379 */     if (false == file1.exists() || false == file2.exists())
/*      */     {
/* 1381 */       return (false == file1.exists() && false == file2
/* 1382 */         .exists() && 
/* 1383 */         pathEquals(file1, file2));
/*      */     }
/* 1385 */     return equals(file1.toPath(), file2.toPath());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean contentEquals(File file1, File file2) throws IORuntimeException {
/* 1400 */     boolean file1Exists = file1.exists();
/* 1401 */     if (file1Exists != file2.exists()) {
/* 1402 */       return false;
/*      */     }
/*      */     
/* 1405 */     if (false == file1Exists)
/*      */     {
/* 1407 */       return true;
/*      */     }
/*      */     
/* 1410 */     if (file1.isDirectory() || file2.isDirectory())
/*      */     {
/* 1412 */       throw new IORuntimeException("Can't compare directories, only files");
/*      */     }
/*      */     
/* 1415 */     if (file1.length() != file2.length())
/*      */     {
/* 1417 */       return false;
/*      */     }
/*      */     
/* 1420 */     if (equals(file1, file2))
/*      */     {
/* 1422 */       return true;
/*      */     }
/*      */     
/* 1425 */     InputStream input1 = null;
/* 1426 */     InputStream input2 = null;
/*      */     try {
/* 1428 */       input1 = getInputStream(file1);
/* 1429 */       input2 = getInputStream(file2);
/* 1430 */       return IoUtil.contentEquals(input1, input2);
/*      */     } finally {
/*      */       
/* 1433 */       IoUtil.close(input1);
/* 1434 */       IoUtil.close(input2);
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
/*      */   public static boolean contentEqualsIgnoreEOL(File file1, File file2, Charset charset) throws IORuntimeException {
/* 1453 */     boolean file1Exists = file1.exists();
/* 1454 */     if (file1Exists != file2.exists()) {
/* 1455 */       return false;
/*      */     }
/*      */     
/* 1458 */     if (!file1Exists)
/*      */     {
/* 1460 */       return true;
/*      */     }
/*      */     
/* 1463 */     if (file1.isDirectory() || file2.isDirectory())
/*      */     {
/* 1465 */       throw new IORuntimeException("Can't compare directories, only files");
/*      */     }
/*      */     
/* 1468 */     if (equals(file1, file2))
/*      */     {
/* 1470 */       return true;
/*      */     }
/*      */     
/* 1473 */     Reader input1 = null;
/* 1474 */     Reader input2 = null;
/*      */     try {
/* 1476 */       input1 = getReader(file1, charset);
/* 1477 */       input2 = getReader(file2, charset);
/* 1478 */       return IoUtil.contentEqualsIgnoreEOL(input1, input2);
/*      */     } finally {
/* 1480 */       IoUtil.close(input1);
/* 1481 */       IoUtil.close(input2);
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
/*      */   public static boolean pathEquals(File file1, File file2) {
/* 1495 */     if (isWindows()) {
/*      */       
/*      */       try {
/* 1498 */         if (StrUtil.equalsIgnoreCase(file1.getCanonicalPath(), file2.getCanonicalPath())) {
/* 1499 */           return true;
/*      */         }
/* 1501 */       } catch (Exception e) {
/* 1502 */         if (StrUtil.equalsIgnoreCase(file1.getAbsolutePath(), file2.getAbsolutePath())) {
/* 1503 */           return true;
/*      */         }
/*      */       } 
/*      */     } else {
/*      */       
/*      */       try {
/* 1509 */         if (StrUtil.equals(file1.getCanonicalPath(), file2.getCanonicalPath())) {
/* 1510 */           return true;
/*      */         }
/* 1512 */       } catch (Exception e) {
/* 1513 */         if (StrUtil.equals(file1.getAbsolutePath(), file2.getAbsolutePath())) {
/* 1514 */           return true;
/*      */         }
/*      */       } 
/*      */     } 
/* 1518 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int lastIndexOfSeparator(String filePath) {
/* 1528 */     if (StrUtil.isNotEmpty(filePath)) {
/* 1529 */       int i = filePath.length();
/*      */       
/* 1531 */       while (--i >= 0) {
/* 1532 */         char c = filePath.charAt(i);
/* 1533 */         if (CharUtil.isFileSeparator(c)) {
/* 1534 */           return i;
/*      */         }
/*      */       } 
/*      */     } 
/* 1538 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static boolean isModifed(File file, long lastModifyTime) {
/* 1552 */     return isModified(file, lastModifyTime);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isModified(File file, long lastModifyTime) {
/* 1565 */     if (null == file || false == file.exists()) {
/* 1566 */       return true;
/*      */     }
/* 1568 */     return (file.lastModified() != lastModifyTime);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String normalize(String path) {
/* 1605 */     if (path == null) {
/* 1606 */       return null;
/*      */     }
/*      */ 
/*      */     
/* 1610 */     String pathToUse = StrUtil.removePrefixIgnoreCase(path, "classpath:");
/*      */     
/* 1612 */     pathToUse = StrUtil.removePrefixIgnoreCase(pathToUse, "file:");
/*      */ 
/*      */     
/* 1615 */     if (StrUtil.startWith(pathToUse, '~')) {
/* 1616 */       pathToUse = getUserHomePath() + pathToUse.substring(1);
/*      */     }
/*      */ 
/*      */     
/* 1620 */     pathToUse = pathToUse.replaceAll("[/\\\\]+", "/");
/*      */     
/* 1622 */     pathToUse = StrUtil.trimStart(pathToUse);
/*      */     
/* 1624 */     if (path.startsWith("\\\\")) {
/* 1625 */       pathToUse = "\\" + pathToUse;
/*      */     }
/*      */     
/* 1628 */     String prefix = "";
/* 1629 */     int prefixIndex = pathToUse.indexOf(":");
/* 1630 */     if (prefixIndex > -1) {
/*      */       
/* 1632 */       prefix = pathToUse.substring(0, prefixIndex + 1);
/* 1633 */       if (StrUtil.startWith(prefix, '/'))
/*      */       {
/* 1635 */         prefix = prefix.substring(1);
/*      */       }
/* 1637 */       if (false == prefix.contains("/")) {
/* 1638 */         pathToUse = pathToUse.substring(prefixIndex + 1);
/*      */       } else {
/*      */         
/* 1641 */         prefix = "";
/*      */       } 
/*      */     } 
/* 1644 */     if (pathToUse.startsWith("/")) {
/* 1645 */       prefix = prefix + "/";
/* 1646 */       pathToUse = pathToUse.substring(1);
/*      */     } 
/*      */     
/* 1649 */     List<String> pathList = StrUtil.split(pathToUse, '/');
/*      */     
/* 1651 */     List<String> pathElements = new LinkedList<>();
/* 1652 */     int tops = 0;
/*      */     
/* 1654 */     for (int i = pathList.size() - 1; i >= 0; i--) {
/* 1655 */       String element = pathList.get(i);
/*      */       
/* 1657 */       if (false == ".".equals(element)) {
/* 1658 */         if ("..".equals(element)) {
/* 1659 */           tops++;
/*      */         }
/* 1661 */         else if (tops > 0) {
/*      */           
/* 1663 */           tops--;
/*      */         } else {
/*      */           
/* 1666 */           pathElements.add(0, element);
/*      */         } 
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1673 */     if (tops > 0 && StrUtil.isEmpty(prefix))
/*      */     {
/* 1675 */       while (tops-- > 0)
/*      */       {
/*      */         
/* 1678 */         pathElements.add(0, "..");
/*      */       }
/*      */     }
/*      */     
/* 1682 */     return prefix + CollUtil.join(pathElements, "/");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String subPath(String rootDir, File file) {
/*      */     try {
/* 1701 */       return subPath(rootDir, file.getCanonicalPath());
/* 1702 */     } catch (IOException e) {
/* 1703 */       throw new IORuntimeException(e);
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
/*      */   public static String subPath(String dirPath, String filePath) {
/* 1723 */     if (StrUtil.isNotEmpty(dirPath) && StrUtil.isNotEmpty(filePath)) {
/*      */       
/* 1725 */       dirPath = StrUtil.removeSuffix(normalize(dirPath), "/");
/* 1726 */       filePath = normalize(filePath);
/*      */       
/* 1728 */       String result = StrUtil.removePrefixIgnoreCase(filePath, dirPath);
/* 1729 */       return StrUtil.removePrefix(result, "/");
/*      */     } 
/* 1731 */     return filePath;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getName(File file) {
/* 1745 */     return FileNameUtil.getName(file);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getName(String filePath) {
/* 1761 */     return FileNameUtil.getName(filePath);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getSuffix(File file) {
/* 1773 */     return FileNameUtil.getSuffix(file);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getSuffix(String fileName) {
/* 1785 */     return FileNameUtil.getSuffix(fileName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getPrefix(File file) {
/* 1797 */     return FileNameUtil.getPrefix(file);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getPrefix(String fileName) {
/* 1809 */     return FileNameUtil.getPrefix(fileName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String mainName(File file) {
/* 1820 */     return FileNameUtil.mainName(file);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String mainName(String fileName) {
/* 1831 */     return FileNameUtil.mainName(fileName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String extName(File file) {
/* 1842 */     return FileNameUtil.extName(file);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String extName(String fileName) {
/* 1853 */     return FileNameUtil.extName(fileName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean pathEndsWith(File file, String suffix) {
/* 1866 */     return file.getPath().toLowerCase().endsWith(suffix);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getType(File file) throws IORuntimeException {
/* 1884 */     return FileTypeUtil.getType(file);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedInputStream getInputStream(File file) throws IORuntimeException {
/* 1897 */     return IoUtil.toBuffered(IoUtil.toStream(file));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedInputStream getInputStream(String path) throws IORuntimeException {
/* 1908 */     return getInputStream(file(path));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BOMInputStream getBOMInputStream(File file) throws IORuntimeException {
/*      */     try {
/* 1920 */       return new BOMInputStream(new FileInputStream(file));
/* 1921 */     } catch (IOException e) {
/* 1922 */       throw new IORuntimeException(e);
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
/*      */   public static BufferedReader getBOMReader(File file) {
/* 1934 */     return IoUtil.getReader(getBOMInputStream(file));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedReader getUtf8Reader(File file) throws IORuntimeException {
/* 1945 */     return getReader(file, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedReader getUtf8Reader(String path) throws IORuntimeException {
/* 1956 */     return getReader(path, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static BufferedReader getReader(File file, String charsetName) throws IORuntimeException {
/* 1970 */     return IoUtil.getReader(getInputStream(file), CharsetUtil.charset(charsetName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedReader getReader(File file, Charset charset) throws IORuntimeException {
/* 1982 */     return IoUtil.getReader(getInputStream(file), charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static BufferedReader getReader(String path, String charsetName) throws IORuntimeException {
/* 1996 */     return getReader(path, CharsetUtil.charset(charsetName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedReader getReader(String path, Charset charset) throws IORuntimeException {
/* 2008 */     return getReader(file(path), charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] readBytes(File file) throws IORuntimeException {
/* 2022 */     return FileReader.create(file).readBytes();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] readBytes(String filePath) throws IORuntimeException {
/* 2035 */     return readBytes(file(filePath));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String readUtf8String(File file) throws IORuntimeException {
/* 2046 */     return readString(file, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String readUtf8String(String path) throws IORuntimeException {
/* 2057 */     return readString(path, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String readString(File file, String charsetName) throws IORuntimeException {
/* 2071 */     return readString(file, CharsetUtil.charset(charsetName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String readString(File file, Charset charset) throws IORuntimeException {
/* 2083 */     return FileReader.create(file, charset).readString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String readString(String path, String charsetName) throws IORuntimeException {
/* 2097 */     return readString(path, CharsetUtil.charset(charsetName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String readString(String path, Charset charset) throws IORuntimeException {
/* 2109 */     return readString(file(path), charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String readString(URL url, String charsetName) throws IORuntimeException {
/* 2123 */     return readString(url, CharsetUtil.charset(charsetName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String readString(URL url, Charset charset) throws IORuntimeException {
/* 2136 */     if (url == null) {
/* 2137 */       throw new NullPointerException("Empty url provided!");
/*      */     }
/*      */     
/* 2140 */     InputStream in = null;
/*      */     try {
/* 2142 */       in = url.openStream();
/* 2143 */       return IoUtil.read(in, charset);
/* 2144 */     } catch (IOException e) {
/* 2145 */       throw new IORuntimeException(e);
/*      */     } finally {
/* 2147 */       IoUtil.close(in);
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
/*      */   public static <T extends Collection<String>> T readUtf8Lines(String path, T collection) throws IORuntimeException {
/* 2162 */     return readLines(path, CharsetUtil.CHARSET_UTF_8, collection);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Collection<String>> T readLines(String path, String charset, T collection) throws IORuntimeException {
/* 2176 */     return readLines(file(path), charset, collection);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Collection<String>> T readLines(String path, Charset charset, T collection) throws IORuntimeException {
/* 2190 */     return readLines(file(path), charset, collection);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Collection<String>> T readUtf8Lines(File file, T collection) throws IORuntimeException {
/* 2204 */     return readLines(file, CharsetUtil.CHARSET_UTF_8, collection);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Collection<String>> T readLines(File file, String charset, T collection) throws IORuntimeException {
/* 2218 */     return (T)FileReader.create(file, CharsetUtil.charset(charset)).readLines((Collection)collection);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Collection<String>> T readLines(File file, Charset charset, T collection) throws IORuntimeException {
/* 2232 */     return (T)FileReader.create(file, charset).readLines((Collection)collection);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Collection<String>> T readUtf8Lines(URL url, T collection) throws IORuntimeException {
/* 2245 */     return readLines(url, CharsetUtil.CHARSET_UTF_8, collection);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static <T extends Collection<String>> T readLines(URL url, String charsetName, T collection) throws IORuntimeException {
/* 2261 */     return readLines(url, CharsetUtil.charset(charsetName), collection);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Collection<String>> T readLines(URL url, Charset charset, T collection) throws IORuntimeException {
/* 2276 */     InputStream in = null;
/*      */     try {
/* 2278 */       in = url.openStream();
/* 2279 */       return (T)IoUtil.readLines(in, charset, (Object)collection);
/* 2280 */     } catch (IOException e) {
/* 2281 */       throw new IORuntimeException(e);
/*      */     } finally {
/* 2283 */       IoUtil.close(in);
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
/*      */   public static List<String> readUtf8Lines(URL url) throws IORuntimeException {
/* 2295 */     return readLines(url, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static List<String> readLines(URL url, String charsetName) throws IORuntimeException {
/* 2309 */     return readLines(url, CharsetUtil.charset(charsetName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> readLines(URL url, Charset charset) throws IORuntimeException {
/* 2321 */     return readLines(url, charset, new ArrayList<>());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> readUtf8Lines(String path) throws IORuntimeException {
/* 2333 */     return readLines(path, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> readLines(String path, String charset) throws IORuntimeException {
/* 2345 */     return readLines(path, charset, new ArrayList<>());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> readLines(String path, Charset charset) throws IORuntimeException {
/* 2358 */     return readLines(path, charset, new ArrayList<>());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> readUtf8Lines(File file) throws IORuntimeException {
/* 2370 */     return readLines(file, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> readLines(File file, String charset) throws IORuntimeException {
/* 2382 */     return readLines(file, charset, new ArrayList<>());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> readLines(File file, Charset charset) throws IORuntimeException {
/* 2394 */     return readLines(file, charset, new ArrayList<>());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void readUtf8Lines(File file, LineHandler lineHandler) throws IORuntimeException {
/* 2405 */     readLines(file, CharsetUtil.CHARSET_UTF_8, lineHandler);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void readLines(File file, Charset charset, LineHandler lineHandler) throws IORuntimeException {
/* 2417 */     FileReader.create(file, charset).readLines(lineHandler);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void readLines(RandomAccessFile file, Charset charset, LineHandler lineHandler) {
/*      */     try {
/*      */       String line;
/* 2432 */       while ((line = file.readLine()) != null) {
/* 2433 */         lineHandler.handle(CharsetUtil.convert(line, CharsetUtil.CHARSET_ISO_8859_1, charset));
/*      */       }
/* 2435 */     } catch (IOException e) {
/* 2436 */       throw new IORuntimeException(e);
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
/*      */   public static void readLine(RandomAccessFile file, Charset charset, LineHandler lineHandler) {
/* 2450 */     String line = readLine(file, charset);
/* 2451 */     if (null != line) {
/* 2452 */       lineHandler.handle(line);
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
/*      */   public static String readLine(RandomAccessFile file, Charset charset) {
/*      */     String line;
/*      */     try {
/* 2468 */       line = file.readLine();
/* 2469 */     } catch (IOException e) {
/* 2470 */       throw new IORuntimeException(e);
/*      */     } 
/* 2472 */     if (null != line) {
/* 2473 */       return CharsetUtil.convert(line, CharsetUtil.CHARSET_ISO_8859_1, charset);
/*      */     }
/*      */     
/* 2476 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T loadUtf8(String path, FileReader.ReaderHandler<T> readerHandler) throws IORuntimeException {
/* 2490 */     return load(path, CharsetUtil.CHARSET_UTF_8, readerHandler);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T load(String path, String charset, FileReader.ReaderHandler<T> readerHandler) throws IORuntimeException {
/* 2505 */     return (T)FileReader.create(file(path), CharsetUtil.charset(charset)).read(readerHandler);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T load(String path, Charset charset, FileReader.ReaderHandler<T> readerHandler) throws IORuntimeException {
/* 2520 */     return (T)FileReader.create(file(path), charset).read(readerHandler);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T loadUtf8(File file, FileReader.ReaderHandler<T> readerHandler) throws IORuntimeException {
/* 2534 */     return load(file, CharsetUtil.CHARSET_UTF_8, readerHandler);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T load(File file, Charset charset, FileReader.ReaderHandler<T> readerHandler) throws IORuntimeException {
/* 2549 */     return (T)FileReader.create(file, charset).read(readerHandler);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedOutputStream getOutputStream(File file) throws IORuntimeException {
/*      */     OutputStream out;
/*      */     try {
/* 2564 */       out = new FileOutputStream(touch(file));
/* 2565 */     } catch (IOException e) {
/* 2566 */       throw new IORuntimeException(e);
/*      */     } 
/* 2568 */     return IoUtil.toBuffered(out);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedOutputStream getOutputStream(String path) throws IORuntimeException {
/* 2579 */     return getOutputStream(touch(path));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static BufferedWriter getWriter(String path, String charsetName, boolean isAppend) throws IORuntimeException {
/* 2594 */     return getWriter(path, Charset.forName(charsetName), isAppend);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedWriter getWriter(String path, Charset charset, boolean isAppend) throws IORuntimeException {
/* 2607 */     return getWriter(touch(path), charset, isAppend);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static BufferedWriter getWriter(File file, String charsetName, boolean isAppend) throws IORuntimeException {
/* 2622 */     return getWriter(file, Charset.forName(charsetName), isAppend);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedWriter getWriter(File file, Charset charset, boolean isAppend) throws IORuntimeException {
/* 2635 */     return FileWriter.create(file, charset).getWriter(isAppend);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PrintWriter getPrintWriter(String path, String charset, boolean isAppend) throws IORuntimeException {
/* 2648 */     return new PrintWriter(getWriter(path, charset, isAppend));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PrintWriter getPrintWriter(String path, Charset charset, boolean isAppend) throws IORuntimeException {
/* 2662 */     return new PrintWriter(getWriter(path, charset, isAppend));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PrintWriter getPrintWriter(File file, String charset, boolean isAppend) throws IORuntimeException {
/* 2675 */     return new PrintWriter(getWriter(file, charset, isAppend));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static PrintWriter getPrintWriter(File file, Charset charset, boolean isAppend) throws IORuntimeException {
/* 2689 */     return new PrintWriter(getWriter(file, charset, isAppend));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getLineSeparator() {
/* 2705 */     return System.lineSeparator();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File writeUtf8String(String content, String path) throws IORuntimeException {
/* 2720 */     return writeString(content, path, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File writeUtf8String(String content, File file) throws IORuntimeException {
/* 2732 */     return writeString(content, file, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File writeString(String content, String path, String charset) throws IORuntimeException {
/* 2745 */     return writeString(content, touch(path), charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File writeString(String content, String path, Charset charset) throws IORuntimeException {
/* 2758 */     return writeString(content, touch(path), charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File writeString(String content, File file, String charset) throws IORuntimeException {
/* 2771 */     return FileWriter.create(file, CharsetUtil.charset(charset)).write(content);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File writeString(String content, File file, Charset charset) throws IORuntimeException {
/* 2784 */     return FileWriter.create(file, charset).write(content);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File appendUtf8String(String content, String path) throws IORuntimeException {
/* 2797 */     return appendString(content, path, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File appendString(String content, String path, String charset) throws IORuntimeException {
/* 2810 */     return appendString(content, touch(path), charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File appendString(String content, String path, Charset charset) throws IORuntimeException {
/* 2823 */     return appendString(content, touch(path), charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File appendUtf8String(String content, File file) throws IORuntimeException {
/* 2836 */     return appendString(content, file, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File appendString(String content, File file, String charset) throws IORuntimeException {
/* 2849 */     return FileWriter.create(file, CharsetUtil.charset(charset)).append(content);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File appendString(String content, File file, Charset charset) throws IORuntimeException {
/* 2862 */     return FileWriter.create(file, charset).append(content);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> File writeUtf8Lines(Collection<T> list, String path) throws IORuntimeException {
/* 2876 */     return writeLines(list, path, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> File writeUtf8Lines(Collection<T> list, File file) throws IORuntimeException {
/* 2890 */     return writeLines(list, file, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> File writeLines(Collection<T> list, String path, String charset) throws IORuntimeException {
/* 2904 */     return writeLines(list, path, charset, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> File writeLines(Collection<T> list, String path, Charset charset) throws IORuntimeException {
/* 2918 */     return writeLines(list, path, charset, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> File writeLines(Collection<T> list, File file, String charset) throws IORuntimeException {
/* 2933 */     return writeLines(list, file, charset, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> File writeLines(Collection<T> list, File file, Charset charset) throws IORuntimeException {
/* 2948 */     return writeLines(list, file, charset, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> File appendUtf8Lines(Collection<T> list, File file) throws IORuntimeException {
/* 2962 */     return appendLines(list, file, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> File appendUtf8Lines(Collection<T> list, String path) throws IORuntimeException {
/* 2976 */     return appendLines(list, path, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> File appendLines(Collection<T> list, String path, String charset) throws IORuntimeException {
/* 2990 */     return writeLines(list, path, charset, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> File appendLines(Collection<T> list, File file, String charset) throws IORuntimeException {
/* 3005 */     return writeLines(list, file, charset, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> File appendLines(Collection<T> list, String path, Charset charset) throws IORuntimeException {
/* 3019 */     return writeLines(list, path, charset, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> File appendLines(Collection<T> list, File file, Charset charset) throws IORuntimeException {
/* 3039 */     return writeLines(list, file, charset, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> File writeLines(Collection<T> list, String path, String charset, boolean isAppend) throws IORuntimeException {
/* 3054 */     return writeLines(list, file(path), charset, isAppend);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> File writeLines(Collection<T> list, String path, Charset charset, boolean isAppend) throws IORuntimeException {
/* 3069 */     return writeLines(list, file(path), charset, isAppend);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> File writeLines(Collection<T> list, File file, String charset, boolean isAppend) throws IORuntimeException {
/* 3084 */     return FileWriter.create(file, CharsetUtil.charset(charset)).writeLines(list, isAppend);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> File writeLines(Collection<T> list, File file, Charset charset, boolean isAppend) throws IORuntimeException {
/* 3099 */     return FileWriter.create(file, charset).writeLines(list, isAppend);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File writeUtf8Map(Map<?, ?> map, File file, String kvSeparator, boolean isAppend) throws IORuntimeException {
/* 3114 */     return FileWriter.create(file, CharsetUtil.CHARSET_UTF_8).writeMap(map, kvSeparator, isAppend);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File writeMap(Map<?, ?> map, File file, Charset charset, String kvSeparator, boolean isAppend) throws IORuntimeException {
/* 3130 */     return FileWriter.create(file, charset).writeMap(map, kvSeparator, isAppend);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File writeBytes(byte[] data, String path) throws IORuntimeException {
/* 3143 */     return writeBytes(data, touch(path));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File writeBytes(byte[] data, File dest) throws IORuntimeException {
/* 3155 */     return writeBytes(data, dest, 0, data.length, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File writeBytes(byte[] data, File dest, int off, int len, boolean isAppend) throws IORuntimeException {
/* 3170 */     return FileWriter.create(dest).write(data, off, len, isAppend);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File writeFromStream(InputStream in, File dest) throws IORuntimeException {
/* 3183 */     return writeFromStream(in, dest, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File writeFromStream(InputStream in, File dest, boolean isCloseIn) throws IORuntimeException {
/* 3197 */     return FileWriter.create(dest).writeFromStream(in, isCloseIn);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File writeFromStream(InputStream in, String fullFilePath) throws IORuntimeException {
/* 3210 */     return writeFromStream(in, touch(fullFilePath));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long writeToStream(File file, OutputStream out) throws IORuntimeException {
/* 3222 */     return FileReader.create(file).writeToStream(out);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long writeToStream(String fullFilePath, OutputStream out) throws IORuntimeException {
/* 3234 */     return writeToStream(touch(fullFilePath), out);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String readableFileSize(File file) {
/* 3244 */     return readableFileSize(file.length());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String readableFileSize(long size) {
/* 3256 */     return DataSizeUtil.format(size);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File convertCharset(File file, Charset srcCharset, Charset destCharset) {
/* 3271 */     return CharsetUtil.convert(file, srcCharset, destCharset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File convertLineSeparator(File file, Charset charset, LineSeparator lineSeparator) {
/* 3285 */     List<String> lines = readLines(file, charset);
/* 3286 */     return FileWriter.create(file, charset).writeLines(lines, lineSeparator, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String cleanInvalid(String fileName) {
/* 3298 */     return FileNameUtil.cleanInvalid(fileName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean containsInvalid(String fileName) {
/* 3310 */     return FileNameUtil.containsInvalid(fileName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long checksumCRC32(File file) throws IORuntimeException {
/* 3322 */     return checksum(file, new CRC32()).getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Checksum checksum(File file, Checksum checksum) throws IORuntimeException {
/* 3335 */     Assert.notNull(file, "File is null !", new Object[0]);
/* 3336 */     if (file.isDirectory()) {
/* 3337 */       throw new IllegalArgumentException("Checksums can't be computed on directories");
/*      */     }
/*      */     try {
/* 3340 */       return IoUtil.checksum(new FileInputStream(file), checksum);
/* 3341 */     } catch (FileNotFoundException e) {
/* 3342 */       throw new IORuntimeException(e);
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
/*      */   public static File getWebRoot() {
/* 3354 */     String classPath = ClassUtil.getClassPath();
/* 3355 */     if (StrUtil.isNotBlank(classPath)) {
/* 3356 */       return getParent(file(classPath), 2);
/*      */     }
/* 3358 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getParent(String filePath, int level) {
/* 3377 */     File parent = getParent(file(filePath), level);
/*      */     try {
/* 3379 */       return (null == parent) ? null : parent.getCanonicalPath();
/* 3380 */     } catch (IOException e) {
/* 3381 */       throw new IORuntimeException(e);
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
/*      */   public static File getParent(File file, int level) {
/*      */     File parentFile;
/* 3401 */     if (level < 1 || null == file) {
/* 3402 */       return file;
/*      */     }
/*      */ 
/*      */     
/*      */     try {
/* 3407 */       parentFile = file.getCanonicalFile().getParentFile();
/* 3408 */     } catch (IOException e) {
/* 3409 */       throw new IORuntimeException(e);
/*      */     } 
/* 3411 */     if (1 == level) {
/* 3412 */       return parentFile;
/*      */     }
/* 3414 */     return getParent(parentFile, level - 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File checkSlip(File parentFile, File file) throws IllegalArgumentException {
/* 3428 */     if (null != parentFile && null != file) {
/*      */       String parentCanonicalPath;
/*      */       String canonicalPath;
/*      */       try {
/* 3432 */         parentCanonicalPath = parentFile.getCanonicalPath();
/* 3433 */         canonicalPath = file.getCanonicalPath();
/* 3434 */       } catch (IOException e) {
/*      */ 
/*      */         
/* 3437 */         parentCanonicalPath = parentFile.getAbsolutePath();
/* 3438 */         canonicalPath = file.getAbsolutePath();
/*      */       } 
/* 3440 */       if (false == canonicalPath.startsWith(parentCanonicalPath)) {
/* 3441 */         throw new IllegalArgumentException("New file is outside of the parent dir: " + file.getName());
/*      */       }
/*      */     } 
/* 3444 */     return file;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getMimeType(String filePath) {
/* 3455 */     String contentType = URLConnection.getFileNameMap().getContentTypeFor(filePath);
/* 3456 */     if (null == contentType)
/*      */     {
/* 3458 */       if (StrUtil.endWithIgnoreCase(filePath, ".css")) {
/* 3459 */         contentType = "text/css";
/* 3460 */       } else if (StrUtil.endWithIgnoreCase(filePath, ".js")) {
/* 3461 */         contentType = "application/x-javascript";
/* 3462 */       } else if (StrUtil.endWithIgnoreCase(filePath, ".rar")) {
/* 3463 */         contentType = "application/x-rar-compressed";
/* 3464 */       } else if (StrUtil.endWithIgnoreCase(filePath, ".7z")) {
/* 3465 */         contentType = "application/x-7z-compressed";
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 3470 */     if (null == contentType) {
/* 3471 */       contentType = getMimeType(Paths.get(filePath, new String[0]));
/*      */     }
/*      */     
/* 3474 */     return contentType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isSymlink(File file) {
/* 3485 */     return isSymlink(file.toPath());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isSub(File parent, File sub) {
/* 3497 */     Assert.notNull(parent);
/* 3498 */     Assert.notNull(sub);
/* 3499 */     return isSub(parent.toPath(), sub.toPath());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static RandomAccessFile createRandomAccessFile(Path path, FileMode mode) {
/* 3511 */     return createRandomAccessFile(path.toFile(), mode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static RandomAccessFile createRandomAccessFile(File file, FileMode mode) {
/*      */     try {
/* 3524 */       return new RandomAccessFile(file, mode.name());
/* 3525 */     } catch (FileNotFoundException e) {
/* 3526 */       throw new IORuntimeException(e);
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
/*      */   public static void tail(File file, LineHandler handler) {
/* 3538 */     tail(file, CharsetUtil.CHARSET_UTF_8, handler);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void tail(File file, Charset charset, LineHandler handler) {
/* 3550 */     (new Tailer(file, charset, handler)).start();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void tail(File file, Charset charset) {
/* 3561 */     tail(file, charset, Tailer.CONSOLE_HANDLER);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static File buildFile(File outFile, String fileName) {
/* 3574 */     fileName = fileName.replace('\\', '/');
/* 3575 */     if (false == isWindows() && fileName
/*      */       
/* 3577 */       .lastIndexOf('/', fileName.length() - 2) > 0) {
/*      */ 
/*      */       
/* 3580 */       List<String> pathParts = StrUtil.split(fileName, '/', false, true);
/* 3581 */       int lastPartIndex = pathParts.size() - 1;
/* 3582 */       for (int i = 0; i < lastPartIndex; i++)
/*      */       {
/* 3584 */         outFile = new File(outFile, pathParts.get(i));
/*      */       }
/*      */       
/* 3587 */       outFile.mkdirs();
/*      */       
/* 3589 */       fileName = pathParts.get(lastPartIndex);
/*      */     } 
/* 3591 */     return new File(outFile, fileName);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\FileUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */