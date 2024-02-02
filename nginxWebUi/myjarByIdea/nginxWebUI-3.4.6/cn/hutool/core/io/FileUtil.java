package cn.hutool.core.io;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileCopier;
import cn.hutool.core.io.file.FileMode;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.io.file.LineSeparator;
import cn.hutool.core.io.file.PathUtil;
import cn.hutool.core.io.file.Tailer;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.core.util.ZipUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class FileUtil extends PathUtil {
   public static final String CLASS_EXT = ".class";
   public static final String JAR_FILE_EXT = ".jar";
   public static final String JAR_PATH_EXT = ".jar!";
   public static final String PATH_FILE_PRE = "file:";
   public static final String FILE_SEPARATOR;
   public static final String PATH_SEPARATOR;
   private static final Pattern PATTERN_PATH_ABSOLUTE;

   public static boolean isWindows() {
      return '\\' == File.separatorChar;
   }

   public static File[] ls(String path) {
      if (path == null) {
         return null;
      } else {
         File file = file(path);
         if (file.isDirectory()) {
            return file.listFiles();
         } else {
            throw new IORuntimeException(StrUtil.format("Path [{}] is not directory!", new Object[]{path}));
         }
      }
   }

   public static boolean isEmpty(File file) {
      if (null != file && file.exists()) {
         if (file.isDirectory()) {
            String[] subFiles = file.list();
            return ArrayUtil.isEmpty((Object[])subFiles);
         } else if (file.isFile()) {
            return file.length() <= 0L;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean isNotEmpty(File file) {
      return !isEmpty(file);
   }

   public static boolean isDirEmpty(File dir) {
      return isDirEmpty(dir.toPath());
   }

   public static List<File> loopFiles(String path, FileFilter fileFilter) {
      return loopFiles(file(path), fileFilter);
   }

   public static List<File> loopFiles(File file, FileFilter fileFilter) {
      return loopFiles(file, -1, fileFilter);
   }

   public static void walkFiles(File file, Consumer<File> consumer) {
      if (file.isDirectory()) {
         File[] subFiles = file.listFiles();
         if (ArrayUtil.isNotEmpty((Object[])subFiles)) {
            File[] var3 = subFiles;
            int var4 = subFiles.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               File tmp = var3[var5];
               walkFiles(tmp, consumer);
            }
         }
      } else {
         consumer.accept(file);
      }

   }

   public static List<File> loopFiles(File file, int maxDepth, FileFilter fileFilter) {
      return loopFiles(file.toPath(), maxDepth, fileFilter);
   }

   public static List<File> loopFiles(String path) {
      return loopFiles(file(path));
   }

   public static List<File> loopFiles(File file) {
      return loopFiles((File)file, (FileFilter)null);
   }

   public static List<String> listFileNames(String path) throws IORuntimeException {
      if (path == null) {
         return new ArrayList(0);
      } else {
         int index = path.lastIndexOf(".jar!");
         if (index < 0) {
            List<String> paths = new ArrayList();
            File[] files = ls(path);
            File[] var4 = files;
            int var5 = files.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               File file = var4[var6];
               if (file.isFile()) {
                  paths.add(file.getName());
               }
            }

            return paths;
         } else {
            path = getAbsolutePath(path);
            index += ".jar".length();
            JarFile jarFile = null;

            List var3;
            try {
               jarFile = new JarFile(path.substring(0, index));
               var3 = ZipUtil.listFileNames(jarFile, StrUtil.removePrefix(path.substring(index + 1), "/"));
            } catch (IOException var11) {
               throw new IORuntimeException(StrUtil.format("Can not read file path of [{}]", new Object[]{path}), var11);
            } finally {
               IoUtil.close(jarFile);
            }

            return var3;
         }
      }
   }

   public static File newFile(String path) {
      return new File(path);
   }

   public static File file(String path) {
      return null == path ? null : new File(getAbsolutePath(path));
   }

   public static File file(String parent, String path) {
      return file(new File(parent), path);
   }

   public static File file(File parent, String path) {
      if (StrUtil.isBlank(path)) {
         throw new NullPointerException("File path is blank!");
      } else {
         return checkSlip(parent, buildFile(parent, path));
      }
   }

   public static File file(File directory, String... names) {
      Assert.notNull(directory, "directory must not be null");
      if (ArrayUtil.isEmpty((Object[])names)) {
         return directory;
      } else {
         File file = directory;
         String[] var3 = names;
         int var4 = names.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String name = var3[var5];
            if (null != name) {
               file = file(file, name);
            }
         }

         return file;
      }
   }

   public static File file(String... names) {
      if (ArrayUtil.isEmpty((Object[])names)) {
         return null;
      } else {
         File file = null;
         String[] var2 = names;
         int var3 = names.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String name = var2[var4];
            if (file == null) {
               file = file(name);
            } else {
               file = file(file, name);
            }
         }

         return file;
      }
   }

   public static File file(URI uri) {
      if (uri == null) {
         throw new NullPointerException("File uri is null!");
      } else {
         return new File(uri);
      }
   }

   public static File file(URL url) {
      return new File(URLUtil.toURI(url));
   }

   public static String getTmpDirPath() {
      return System.getProperty("java.io.tmpdir");
   }

   public static File getTmpDir() {
      return file(getTmpDirPath());
   }

   public static String getUserHomePath() {
      return System.getProperty("user.home");
   }

   public static File getUserHomeDir() {
      return file(getUserHomePath());
   }

   public static boolean exist(String path) {
      return null != path && file(path).exists();
   }

   public static boolean exist(File file) {
      return null != file && file.exists();
   }

   public static boolean exist(String directory, String regexp) {
      File file = new File(directory);
      if (!file.exists()) {
         return false;
      } else {
         String[] fileList = file.list();
         if (fileList == null) {
            return false;
         } else {
            String[] var4 = fileList;
            int var5 = fileList.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               String fileName = var4[var6];
               if (fileName.matches(regexp)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public static Date lastModifiedTime(File file) {
      return !exist(file) ? null : new Date(file.lastModified());
   }

   public static Date lastModifiedTime(String path) {
      return lastModifiedTime(new File(path));
   }

   public static long size(File file) {
      return size(file, false);
   }

   public static long size(File file, boolean includeDirSize) {
      if (null != file && file.exists() && !isSymlink(file)) {
         if (!file.isDirectory()) {
            return file.length();
         } else {
            long size = includeDirSize ? file.length() : 0L;
            File[] subFiles = file.listFiles();
            if (ArrayUtil.isEmpty((Object[])subFiles)) {
               return 0L;
            } else {
               File[] var5 = subFiles;
               int var6 = subFiles.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  File subFile = var5[var7];
                  size += size(subFile, includeDirSize);
               }

               return size;
            }
         }
      } else {
         return 0L;
      }
   }

   public static int getTotalLines(File file) {
      if (!isFile(file)) {
         throw new IORuntimeException("Input must be a File");
      } else {
         try {
            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file));
            Throwable var2 = null;

            int var3;
            try {
               lineNumberReader.setLineNumber(1);
               lineNumberReader.skip(Long.MAX_VALUE);
               var3 = lineNumberReader.getLineNumber();
            } catch (Throwable var13) {
               var2 = var13;
               throw var13;
            } finally {
               if (lineNumberReader != null) {
                  if (var2 != null) {
                     try {
                        lineNumberReader.close();
                     } catch (Throwable var12) {
                        var2.addSuppressed(var12);
                     }
                  } else {
                     lineNumberReader.close();
                  }
               }

            }

            return var3;
         } catch (IOException var15) {
            throw new IORuntimeException(var15);
         }
      }
   }

   public static boolean newerThan(File file, File reference) {
      return null != reference && reference.exists() ? newerThan(file, reference.lastModified()) : true;
   }

   public static boolean newerThan(File file, long timeMillis) {
      if (null != file && file.exists()) {
         return file.lastModified() > timeMillis;
      } else {
         return false;
      }
   }

   public static File touch(String path) throws IORuntimeException {
      return path == null ? null : touch(file(path));
   }

   public static File touch(File file) throws IORuntimeException {
      if (null == file) {
         return null;
      } else {
         if (!file.exists()) {
            mkParentDirs(file);

            try {
               file.createNewFile();
            } catch (Exception var2) {
               throw new IORuntimeException(var2);
            }
         }

         return file;
      }
   }

   public static File touch(File parent, String path) throws IORuntimeException {
      return touch(file(parent, path));
   }

   public static File touch(String parent, String path) throws IORuntimeException {
      return touch(file(parent, path));
   }

   public static File mkParentDirs(File file) {
      return null == file ? null : mkdir(getParent((File)file, 1));
   }

   public static File mkParentDirs(String path) {
      return path == null ? null : mkParentDirs(file(path));
   }

   public static boolean del(String fullFileOrDirPath) throws IORuntimeException {
      return del(file(fullFileOrDirPath));
   }

   public static boolean del(File file) throws IORuntimeException {
      if (file != null && file.exists()) {
         if (file.isDirectory()) {
            boolean isOk = clean(file);
            if (!isOk) {
               return false;
            }
         }

         Path path = file.toPath();

         try {
            delFile(path);
         } catch (DirectoryNotEmptyException var3) {
            del((Path)path);
         } catch (IOException var4) {
            throw new IORuntimeException(var4);
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean clean(String dirPath) throws IORuntimeException {
      return clean(file(dirPath));
   }

   public static boolean clean(File directory) throws IORuntimeException {
      if (directory != null && directory.exists() && directory.isDirectory()) {
         File[] files = directory.listFiles();
         if (null != files) {
            File[] var2 = files;
            int var3 = files.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               File childFile = var2[var4];
               if (!del(childFile)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean cleanEmpty(File directory) throws IORuntimeException {
      if (directory != null && directory.exists() && directory.isDirectory()) {
         File[] files = directory.listFiles();
         if (ArrayUtil.isEmpty((Object[])files)) {
            return directory.delete();
         } else {
            File[] var2 = files;
            int var3 = files.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               File childFile = var2[var4];
               cleanEmpty(childFile);
            }

            return true;
         }
      } else {
         return true;
      }
   }

   public static File mkdir(String dirPath) {
      if (dirPath == null) {
         return null;
      } else {
         File dir = file(dirPath);
         return mkdir(dir);
      }
   }

   public static File mkdir(File dir) {
      if (dir == null) {
         return null;
      } else {
         if (!dir.exists()) {
            mkdirsSafely(dir, 5, 1L);
         }

         return dir;
      }
   }

   public static boolean mkdirsSafely(File dir, int tryCount, long sleepMillis) {
      if (dir == null) {
         return false;
      } else if (dir.isDirectory()) {
         return true;
      } else {
         for(int i = 1; i <= tryCount; ++i) {
            dir.mkdirs();
            if (dir.exists()) {
               return true;
            }

            ThreadUtil.sleep(sleepMillis);
         }

         return dir.exists();
      }
   }

   public static File createTempFile(File dir) throws IORuntimeException {
      return createTempFile("hutool", (String)null, dir, true);
   }

   public static File createTempFile() throws IORuntimeException {
      return createTempFile("hutool", (String)null, (File)null, true);
   }

   public static File createTempFile(String suffix, boolean isReCreat) throws IORuntimeException {
      return createTempFile("hutool", suffix, (File)null, isReCreat);
   }

   public static File createTempFile(String prefix, String suffix, boolean isReCreat) throws IORuntimeException {
      return createTempFile(prefix, suffix, (File)null, isReCreat);
   }

   public static File createTempFile(File dir, boolean isReCreat) throws IORuntimeException {
      return createTempFile("hutool", (String)null, dir, isReCreat);
   }

   public static File createTempFile(String prefix, String suffix, File dir, boolean isReCreat) throws IORuntimeException {
      int exceptionsCount = 0;

      while(true) {
         try {
            File file = File.createTempFile(prefix, suffix, mkdir(dir)).getCanonicalFile();
            if (isReCreat) {
               file.delete();
               file.createNewFile();
            }

            return file;
         } catch (IOException var6) {
            ++exceptionsCount;
            if (exceptionsCount >= 50) {
               throw new IORuntimeException(var6);
            }
         }
      }
   }

   public static File copyFile(String src, String dest, StandardCopyOption... options) throws IORuntimeException {
      Assert.notBlank(src, "Source File path is blank !");
      Assert.notBlank(dest, "Destination File path is blank !");
      return copyFile((Path)Paths.get(src), (Path)Paths.get(dest), options).toFile();
   }

   public static File copyFile(File src, File dest, StandardCopyOption... options) throws IORuntimeException {
      Assert.notNull(src, "Source File is null !");
      if (!src.exists()) {
         throw new IORuntimeException("File not exist: " + src);
      } else {
         Assert.notNull(dest, "Destination File or directiory is null !");
         if (equals(src, dest)) {
            throw new IORuntimeException("Files '{}' and '{}' are equal", new Object[]{src, dest});
         } else {
            return copyFile((Path)src.toPath(), (Path)dest.toPath(), options).toFile();
         }
      }
   }

   public static File copy(String srcPath, String destPath, boolean isOverride) throws IORuntimeException {
      return copy(file(srcPath), file(destPath), isOverride);
   }

   public static File copy(File src, File dest, boolean isOverride) throws IORuntimeException {
      return FileCopier.create(src, dest).setOverride(isOverride).copy();
   }

   public static File copyContent(File src, File dest, boolean isOverride) throws IORuntimeException {
      return FileCopier.create(src, dest).setCopyContentIfDir(true).setOverride(isOverride).copy();
   }

   public static File copyFilesFromDir(File src, File dest, boolean isOverride) throws IORuntimeException {
      return FileCopier.create(src, dest).setCopyContentIfDir(true).setOnlyCopyFile(true).setOverride(isOverride).copy();
   }

   public static void move(File src, File target, boolean isOverride) throws IORuntimeException {
      Assert.notNull(src, "Src file must be not null!");
      Assert.notNull(target, "target file must be not null!");
      move(src.toPath(), target.toPath(), isOverride);
   }

   public static void moveContent(File src, File target, boolean isOverride) throws IORuntimeException {
      Assert.notNull(src, "Src file must be not null!");
      Assert.notNull(target, "target file must be not null!");
      moveContent(src.toPath(), target.toPath(), isOverride);
   }

   public static File rename(File file, String newName, boolean isOverride) {
      return rename(file, newName, false, isOverride);
   }

   public static File rename(File file, String newName, boolean isRetainExt, boolean isOverride) {
      if (isRetainExt) {
         String extName = extName(file);
         if (StrUtil.isNotBlank(extName)) {
            newName = newName.concat(".").concat(extName);
         }
      }

      return rename(file.toPath(), newName, isOverride).toFile();
   }

   public static String getCanonicalPath(File file) {
      if (null == file) {
         return null;
      } else {
         try {
            return file.getCanonicalPath();
         } catch (IOException var2) {
            throw new IORuntimeException(var2);
         }
      }
   }

   public static String getAbsolutePath(String path, Class<?> baseClass) {
      String normalPath;
      if (path == null) {
         normalPath = "";
      } else {
         normalPath = normalize(path);
         if (isAbsolutePath(normalPath)) {
            return normalPath;
         }
      }

      URL url = ResourceUtil.getResource(normalPath, baseClass);
      if (null != url) {
         return normalize(URLUtil.getDecodedPath(url));
      } else {
         String classPath = ClassUtil.getClassPath();
         return null == classPath ? path : normalize(classPath.concat((String)Objects.requireNonNull(path)));
      }
   }

   public static String getAbsolutePath(String path) {
      return getAbsolutePath(path, (Class)null);
   }

   public static String getAbsolutePath(File file) {
      if (file == null) {
         return null;
      } else {
         try {
            return file.getCanonicalPath();
         } catch (IOException var2) {
            return file.getAbsolutePath();
         }
      }
   }

   public static boolean isAbsolutePath(String path) {
      if (StrUtil.isEmpty(path)) {
         return false;
      } else {
         return '/' == path.charAt(0) || ReUtil.isMatch((Pattern)PATTERN_PATH_ABSOLUTE, path);
      }
   }

   public static boolean isDirectory(String path) {
      return null != path && file(path).isDirectory();
   }

   public static boolean isDirectory(File file) {
      return null != file && file.isDirectory();
   }

   public static boolean isFile(String path) {
      return null != path && file(path).isFile();
   }

   public static boolean isFile(File file) {
      return null != file && file.isFile();
   }

   public static boolean equals(File file1, File file2) throws IORuntimeException {
      Assert.notNull(file1);
      Assert.notNull(file2);
      if (file1.exists() && file2.exists()) {
         return equals(file1.toPath(), file2.toPath());
      } else {
         return !file1.exists() && !file2.exists() && pathEquals(file1, file2);
      }
   }

   public static boolean contentEquals(File file1, File file2) throws IORuntimeException {
      boolean file1Exists = file1.exists();
      if (file1Exists != file2.exists()) {
         return false;
      } else if (!file1Exists) {
         return true;
      } else if (!file1.isDirectory() && !file2.isDirectory()) {
         if (file1.length() != file2.length()) {
            return false;
         } else if (equals(file1, file2)) {
            return true;
         } else {
            InputStream input1 = null;
            InputStream input2 = null;

            boolean var5;
            try {
               input1 = getInputStream(file1);
               input2 = getInputStream(file2);
               var5 = IoUtil.contentEquals((InputStream)input1, (InputStream)input2);
            } finally {
               IoUtil.close(input1);
               IoUtil.close(input2);
            }

            return var5;
         }
      } else {
         throw new IORuntimeException("Can't compare directories, only files");
      }
   }

   public static boolean contentEqualsIgnoreEOL(File file1, File file2, Charset charset) throws IORuntimeException {
      boolean file1Exists = file1.exists();
      if (file1Exists != file2.exists()) {
         return false;
      } else if (!file1Exists) {
         return true;
      } else if (!file1.isDirectory() && !file2.isDirectory()) {
         if (equals(file1, file2)) {
            return true;
         } else {
            Reader input1 = null;
            Reader input2 = null;

            boolean var6;
            try {
               input1 = getReader(file1, charset);
               input2 = getReader(file2, charset);
               var6 = IoUtil.contentEqualsIgnoreEOL(input1, input2);
            } finally {
               IoUtil.close(input1);
               IoUtil.close(input2);
            }

            return var6;
         }
      } else {
         throw new IORuntimeException("Can't compare directories, only files");
      }
   }

   public static boolean pathEquals(File file1, File file2) {
      if (isWindows()) {
         try {
            if (StrUtil.equalsIgnoreCase(file1.getCanonicalPath(), file2.getCanonicalPath())) {
               return true;
            }
         } catch (Exception var3) {
            if (StrUtil.equalsIgnoreCase(file1.getAbsolutePath(), file2.getAbsolutePath())) {
               return true;
            }
         }
      } else {
         try {
            if (StrUtil.equals(file1.getCanonicalPath(), file2.getCanonicalPath())) {
               return true;
            }
         } catch (Exception var4) {
            if (StrUtil.equals(file1.getAbsolutePath(), file2.getAbsolutePath())) {
               return true;
            }
         }
      }

      return false;
   }

   public static int lastIndexOfSeparator(String filePath) {
      if (StrUtil.isNotEmpty(filePath)) {
         int i = filePath.length();

         while(true) {
            --i;
            if (i < 0) {
               break;
            }

            char c = filePath.charAt(i);
            if (CharUtil.isFileSeparator(c)) {
               return i;
            }
         }
      }

      return -1;
   }

   /** @deprecated */
   @Deprecated
   public static boolean isModifed(File file, long lastModifyTime) {
      return isModified(file, lastModifyTime);
   }

   public static boolean isModified(File file, long lastModifyTime) {
      if (null != file && file.exists()) {
         return file.lastModified() != lastModifyTime;
      } else {
         return true;
      }
   }

   public static String normalize(String path) {
      if (path == null) {
         return null;
      } else {
         String pathToUse = StrUtil.removePrefixIgnoreCase(path, "classpath:");
         pathToUse = StrUtil.removePrefixIgnoreCase(pathToUse, "file:");
         if (StrUtil.startWith(pathToUse, '~')) {
            pathToUse = getUserHomePath() + pathToUse.substring(1);
         }

         pathToUse = pathToUse.replaceAll("[/\\\\]+", "/");
         pathToUse = StrUtil.trimStart(pathToUse);
         if (path.startsWith("\\\\")) {
            pathToUse = "\\" + pathToUse;
         }

         String prefix = "";
         int prefixIndex = pathToUse.indexOf(":");
         if (prefixIndex > -1) {
            prefix = pathToUse.substring(0, prefixIndex + 1);
            if (StrUtil.startWith(prefix, '/')) {
               prefix = prefix.substring(1);
            }

            if (!prefix.contains("/")) {
               pathToUse = pathToUse.substring(prefixIndex + 1);
            } else {
               prefix = "";
            }
         }

         if (pathToUse.startsWith("/")) {
            prefix = prefix + "/";
            pathToUse = pathToUse.substring(1);
         }

         List<String> pathList = StrUtil.split(pathToUse, '/');
         List<String> pathElements = new LinkedList();
         int tops = 0;

         for(int i = pathList.size() - 1; i >= 0; --i) {
            String element = (String)pathList.get(i);
            if (!".".equals(element)) {
               if ("..".equals(element)) {
                  ++tops;
               } else if (tops > 0) {
                  --tops;
               } else {
                  pathElements.add(0, element);
               }
            }
         }

         if (tops > 0 && StrUtil.isEmpty(prefix)) {
            while(tops-- > 0) {
               pathElements.add(0, "..");
            }
         }

         return prefix + CollUtil.join((Iterable)pathElements, "/");
      }
   }

   public static String subPath(String rootDir, File file) {
      try {
         return subPath(rootDir, file.getCanonicalPath());
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public static String subPath(String dirPath, String filePath) {
      if (StrUtil.isNotEmpty(dirPath) && StrUtil.isNotEmpty(filePath)) {
         dirPath = StrUtil.removeSuffix(normalize(dirPath), "/");
         filePath = normalize(filePath);
         String result = StrUtil.removePrefixIgnoreCase(filePath, dirPath);
         return StrUtil.removePrefix(result, "/");
      } else {
         return filePath;
      }
   }

   public static String getName(File file) {
      return FileNameUtil.getName(file);
   }

   public static String getName(String filePath) {
      return FileNameUtil.getName(filePath);
   }

   public static String getSuffix(File file) {
      return FileNameUtil.getSuffix(file);
   }

   public static String getSuffix(String fileName) {
      return FileNameUtil.getSuffix(fileName);
   }

   public static String getPrefix(File file) {
      return FileNameUtil.getPrefix(file);
   }

   public static String getPrefix(String fileName) {
      return FileNameUtil.getPrefix(fileName);
   }

   public static String mainName(File file) {
      return FileNameUtil.mainName(file);
   }

   public static String mainName(String fileName) {
      return FileNameUtil.mainName(fileName);
   }

   public static String extName(File file) {
      return FileNameUtil.extName(file);
   }

   public static String extName(String fileName) {
      return FileNameUtil.extName(fileName);
   }

   public static boolean pathEndsWith(File file, String suffix) {
      return file.getPath().toLowerCase().endsWith(suffix);
   }

   public static String getType(File file) throws IORuntimeException {
      return FileTypeUtil.getType(file);
   }

   public static BufferedInputStream getInputStream(File file) throws IORuntimeException {
      return IoUtil.toBuffered((InputStream)IoUtil.toStream(file));
   }

   public static BufferedInputStream getInputStream(String path) throws IORuntimeException {
      return getInputStream(file(path));
   }

   public static BOMInputStream getBOMInputStream(File file) throws IORuntimeException {
      try {
         return new BOMInputStream(new FileInputStream(file));
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public static BufferedReader getBOMReader(File file) {
      return IoUtil.getReader(getBOMInputStream(file));
   }

   public static BufferedReader getUtf8Reader(File file) throws IORuntimeException {
      return getReader(file, CharsetUtil.CHARSET_UTF_8);
   }

   public static BufferedReader getUtf8Reader(String path) throws IORuntimeException {
      return getReader(path, CharsetUtil.CHARSET_UTF_8);
   }

   /** @deprecated */
   @Deprecated
   public static BufferedReader getReader(File file, String charsetName) throws IORuntimeException {
      return IoUtil.getReader(getInputStream(file), (Charset)CharsetUtil.charset(charsetName));
   }

   public static BufferedReader getReader(File file, Charset charset) throws IORuntimeException {
      return IoUtil.getReader(getInputStream(file), (Charset)charset);
   }

   /** @deprecated */
   @Deprecated
   public static BufferedReader getReader(String path, String charsetName) throws IORuntimeException {
      return getReader(path, CharsetUtil.charset(charsetName));
   }

   public static BufferedReader getReader(String path, Charset charset) throws IORuntimeException {
      return getReader(file(path), charset);
   }

   public static byte[] readBytes(File file) throws IORuntimeException {
      return cn.hutool.core.io.file.FileReader.create(file).readBytes();
   }

   public static byte[] readBytes(String filePath) throws IORuntimeException {
      return readBytes(file(filePath));
   }

   public static String readUtf8String(File file) throws IORuntimeException {
      return readString(file, CharsetUtil.CHARSET_UTF_8);
   }

   public static String readUtf8String(String path) throws IORuntimeException {
      return readString(path, CharsetUtil.CHARSET_UTF_8);
   }

   /** @deprecated */
   @Deprecated
   public static String readString(File file, String charsetName) throws IORuntimeException {
      return readString(file, CharsetUtil.charset(charsetName));
   }

   public static String readString(File file, Charset charset) throws IORuntimeException {
      return cn.hutool.core.io.file.FileReader.create(file, charset).readString();
   }

   /** @deprecated */
   @Deprecated
   public static String readString(String path, String charsetName) throws IORuntimeException {
      return readString(path, CharsetUtil.charset(charsetName));
   }

   public static String readString(String path, Charset charset) throws IORuntimeException {
      return readString(file(path), charset);
   }

   /** @deprecated */
   @Deprecated
   public static String readString(URL url, String charsetName) throws IORuntimeException {
      return readString(url, CharsetUtil.charset(charsetName));
   }

   public static String readString(URL url, Charset charset) throws IORuntimeException {
      if (url == null) {
         throw new NullPointerException("Empty url provided!");
      } else {
         InputStream in = null;

         String var3;
         try {
            in = url.openStream();
            var3 = IoUtil.read(in, charset);
         } catch (IOException var7) {
            throw new IORuntimeException(var7);
         } finally {
            IoUtil.close(in);
         }

         return var3;
      }
   }

   public static <T extends Collection<String>> T readUtf8Lines(String path, T collection) throws IORuntimeException {
      return readLines(path, CharsetUtil.CHARSET_UTF_8, collection);
   }

   public static <T extends Collection<String>> T readLines(String path, String charset, T collection) throws IORuntimeException {
      return readLines(file(path), charset, collection);
   }

   public static <T extends Collection<String>> T readLines(String path, Charset charset, T collection) throws IORuntimeException {
      return readLines(file(path), charset, collection);
   }

   public static <T extends Collection<String>> T readUtf8Lines(File file, T collection) throws IORuntimeException {
      return readLines(file, CharsetUtil.CHARSET_UTF_8, collection);
   }

   public static <T extends Collection<String>> T readLines(File file, String charset, T collection) throws IORuntimeException {
      return cn.hutool.core.io.file.FileReader.create(file, CharsetUtil.charset(charset)).readLines(collection);
   }

   public static <T extends Collection<String>> T readLines(File file, Charset charset, T collection) throws IORuntimeException {
      return cn.hutool.core.io.file.FileReader.create(file, charset).readLines(collection);
   }

   public static <T extends Collection<String>> T readUtf8Lines(URL url, T collection) throws IORuntimeException {
      return readLines(url, CharsetUtil.CHARSET_UTF_8, collection);
   }

   /** @deprecated */
   @Deprecated
   public static <T extends Collection<String>> T readLines(URL url, String charsetName, T collection) throws IORuntimeException {
      return readLines(url, CharsetUtil.charset(charsetName), collection);
   }

   public static <T extends Collection<String>> T readLines(URL url, Charset charset, T collection) throws IORuntimeException {
      InputStream in = null;

      Collection var4;
      try {
         in = url.openStream();
         var4 = IoUtil.readLines(in, charset, collection);
      } catch (IOException var8) {
         throw new IORuntimeException(var8);
      } finally {
         IoUtil.close(in);
      }

      return var4;
   }

   public static List<String> readUtf8Lines(URL url) throws IORuntimeException {
      return readLines(url, CharsetUtil.CHARSET_UTF_8);
   }

   /** @deprecated */
   @Deprecated
   public static List<String> readLines(URL url, String charsetName) throws IORuntimeException {
      return readLines(url, CharsetUtil.charset(charsetName));
   }

   public static List<String> readLines(URL url, Charset charset) throws IORuntimeException {
      return (List)readLines((URL)url, (Charset)charset, (Collection)(new ArrayList()));
   }

   public static List<String> readUtf8Lines(String path) throws IORuntimeException {
      return readLines(path, CharsetUtil.CHARSET_UTF_8);
   }

   public static List<String> readLines(String path, String charset) throws IORuntimeException {
      return (List)readLines((String)path, (String)charset, (Collection)(new ArrayList()));
   }

   public static List<String> readLines(String path, Charset charset) throws IORuntimeException {
      return (List)readLines((String)path, (Charset)charset, (Collection)(new ArrayList()));
   }

   public static List<String> readUtf8Lines(File file) throws IORuntimeException {
      return readLines(file, CharsetUtil.CHARSET_UTF_8);
   }

   public static List<String> readLines(File file, String charset) throws IORuntimeException {
      return (List)readLines((File)file, (String)charset, (Collection)(new ArrayList()));
   }

   public static List<String> readLines(File file, Charset charset) throws IORuntimeException {
      return (List)readLines((File)file, (Charset)charset, (Collection)(new ArrayList()));
   }

   public static void readUtf8Lines(File file, LineHandler lineHandler) throws IORuntimeException {
      readLines(file, CharsetUtil.CHARSET_UTF_8, lineHandler);
   }

   public static void readLines(File file, Charset charset, LineHandler lineHandler) throws IORuntimeException {
      cn.hutool.core.io.file.FileReader.create(file, charset).readLines(lineHandler);
   }

   public static void readLines(RandomAccessFile file, Charset charset, LineHandler lineHandler) {
      try {
         String line;
         while((line = file.readLine()) != null) {
            lineHandler.handle(CharsetUtil.convert(line, CharsetUtil.CHARSET_ISO_8859_1, charset));
         }

      } catch (IOException var5) {
         throw new IORuntimeException(var5);
      }
   }

   public static void readLine(RandomAccessFile file, Charset charset, LineHandler lineHandler) {
      String line = readLine(file, charset);
      if (null != line) {
         lineHandler.handle(line);
      }

   }

   public static String readLine(RandomAccessFile file, Charset charset) {
      String line;
      try {
         line = file.readLine();
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }

      return null != line ? CharsetUtil.convert(line, CharsetUtil.CHARSET_ISO_8859_1, charset) : null;
   }

   public static <T> T loadUtf8(String path, cn.hutool.core.io.file.FileReader.ReaderHandler<T> readerHandler) throws IORuntimeException {
      return load(path, CharsetUtil.CHARSET_UTF_8, readerHandler);
   }

   public static <T> T load(String path, String charset, cn.hutool.core.io.file.FileReader.ReaderHandler<T> readerHandler) throws IORuntimeException {
      return cn.hutool.core.io.file.FileReader.create(file(path), CharsetUtil.charset(charset)).read(readerHandler);
   }

   public static <T> T load(String path, Charset charset, cn.hutool.core.io.file.FileReader.ReaderHandler<T> readerHandler) throws IORuntimeException {
      return cn.hutool.core.io.file.FileReader.create(file(path), charset).read(readerHandler);
   }

   public static <T> T loadUtf8(File file, cn.hutool.core.io.file.FileReader.ReaderHandler<T> readerHandler) throws IORuntimeException {
      return load(file, CharsetUtil.CHARSET_UTF_8, readerHandler);
   }

   public static <T> T load(File file, Charset charset, cn.hutool.core.io.file.FileReader.ReaderHandler<T> readerHandler) throws IORuntimeException {
      return cn.hutool.core.io.file.FileReader.create(file, charset).read(readerHandler);
   }

   public static BufferedOutputStream getOutputStream(File file) throws IORuntimeException {
      FileOutputStream out;
      try {
         out = new FileOutputStream(touch(file));
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }

      return IoUtil.toBuffered((OutputStream)out);
   }

   public static BufferedOutputStream getOutputStream(String path) throws IORuntimeException {
      return getOutputStream(touch(path));
   }

   /** @deprecated */
   @Deprecated
   public static BufferedWriter getWriter(String path, String charsetName, boolean isAppend) throws IORuntimeException {
      return getWriter(path, Charset.forName(charsetName), isAppend);
   }

   public static BufferedWriter getWriter(String path, Charset charset, boolean isAppend) throws IORuntimeException {
      return getWriter(touch(path), charset, isAppend);
   }

   /** @deprecated */
   @Deprecated
   public static BufferedWriter getWriter(File file, String charsetName, boolean isAppend) throws IORuntimeException {
      return getWriter(file, Charset.forName(charsetName), isAppend);
   }

   public static BufferedWriter getWriter(File file, Charset charset, boolean isAppend) throws IORuntimeException {
      return FileWriter.create(file, charset).getWriter(isAppend);
   }

   public static PrintWriter getPrintWriter(String path, String charset, boolean isAppend) throws IORuntimeException {
      return new PrintWriter(getWriter(path, charset, isAppend));
   }

   public static PrintWriter getPrintWriter(String path, Charset charset, boolean isAppend) throws IORuntimeException {
      return new PrintWriter(getWriter(path, charset, isAppend));
   }

   public static PrintWriter getPrintWriter(File file, String charset, boolean isAppend) throws IORuntimeException {
      return new PrintWriter(getWriter(file, charset, isAppend));
   }

   public static PrintWriter getPrintWriter(File file, Charset charset, boolean isAppend) throws IORuntimeException {
      return new PrintWriter(getWriter(file, charset, isAppend));
   }

   public static String getLineSeparator() {
      return System.lineSeparator();
   }

   public static File writeUtf8String(String content, String path) throws IORuntimeException {
      return writeString(content, path, CharsetUtil.CHARSET_UTF_8);
   }

   public static File writeUtf8String(String content, File file) throws IORuntimeException {
      return writeString(content, file, CharsetUtil.CHARSET_UTF_8);
   }

   public static File writeString(String content, String path, String charset) throws IORuntimeException {
      return writeString(content, touch(path), charset);
   }

   public static File writeString(String content, String path, Charset charset) throws IORuntimeException {
      return writeString(content, touch(path), charset);
   }

   public static File writeString(String content, File file, String charset) throws IORuntimeException {
      return FileWriter.create(file, CharsetUtil.charset(charset)).write(content);
   }

   public static File writeString(String content, File file, Charset charset) throws IORuntimeException {
      return FileWriter.create(file, charset).write(content);
   }

   public static File appendUtf8String(String content, String path) throws IORuntimeException {
      return appendString(content, path, CharsetUtil.CHARSET_UTF_8);
   }

   public static File appendString(String content, String path, String charset) throws IORuntimeException {
      return appendString(content, touch(path), charset);
   }

   public static File appendString(String content, String path, Charset charset) throws IORuntimeException {
      return appendString(content, touch(path), charset);
   }

   public static File appendUtf8String(String content, File file) throws IORuntimeException {
      return appendString(content, file, CharsetUtil.CHARSET_UTF_8);
   }

   public static File appendString(String content, File file, String charset) throws IORuntimeException {
      return FileWriter.create(file, CharsetUtil.charset(charset)).append(content);
   }

   public static File appendString(String content, File file, Charset charset) throws IORuntimeException {
      return FileWriter.create(file, charset).append(content);
   }

   public static <T> File writeUtf8Lines(Collection<T> list, String path) throws IORuntimeException {
      return writeLines(list, path, CharsetUtil.CHARSET_UTF_8);
   }

   public static <T> File writeUtf8Lines(Collection<T> list, File file) throws IORuntimeException {
      return writeLines(list, file, CharsetUtil.CHARSET_UTF_8);
   }

   public static <T> File writeLines(Collection<T> list, String path, String charset) throws IORuntimeException {
      return writeLines(list, path, charset, false);
   }

   public static <T> File writeLines(Collection<T> list, String path, Charset charset) throws IORuntimeException {
      return writeLines(list, path, charset, false);
   }

   public static <T> File writeLines(Collection<T> list, File file, String charset) throws IORuntimeException {
      return writeLines(list, file, charset, false);
   }

   public static <T> File writeLines(Collection<T> list, File file, Charset charset) throws IORuntimeException {
      return writeLines(list, file, charset, false);
   }

   public static <T> File appendUtf8Lines(Collection<T> list, File file) throws IORuntimeException {
      return appendLines(list, file, CharsetUtil.CHARSET_UTF_8);
   }

   public static <T> File appendUtf8Lines(Collection<T> list, String path) throws IORuntimeException {
      return appendLines(list, path, CharsetUtil.CHARSET_UTF_8);
   }

   public static <T> File appendLines(Collection<T> list, String path, String charset) throws IORuntimeException {
      return writeLines(list, path, charset, true);
   }

   public static <T> File appendLines(Collection<T> list, File file, String charset) throws IORuntimeException {
      return writeLines(list, file, charset, true);
   }

   public static <T> File appendLines(Collection<T> list, String path, Charset charset) throws IORuntimeException {
      return writeLines(list, path, charset, true);
   }

   public static <T> File appendLines(Collection<T> list, File file, Charset charset) throws IORuntimeException {
      return writeLines(list, file, charset, true);
   }

   public static <T> File writeLines(Collection<T> list, String path, String charset, boolean isAppend) throws IORuntimeException {
      return writeLines(list, file(path), charset, isAppend);
   }

   public static <T> File writeLines(Collection<T> list, String path, Charset charset, boolean isAppend) throws IORuntimeException {
      return writeLines(list, file(path), charset, isAppend);
   }

   public static <T> File writeLines(Collection<T> list, File file, String charset, boolean isAppend) throws IORuntimeException {
      return FileWriter.create(file, CharsetUtil.charset(charset)).writeLines(list, isAppend);
   }

   public static <T> File writeLines(Collection<T> list, File file, Charset charset, boolean isAppend) throws IORuntimeException {
      return FileWriter.create(file, charset).writeLines(list, isAppend);
   }

   public static File writeUtf8Map(Map<?, ?> map, File file, String kvSeparator, boolean isAppend) throws IORuntimeException {
      return FileWriter.create(file, CharsetUtil.CHARSET_UTF_8).writeMap(map, kvSeparator, isAppend);
   }

   public static File writeMap(Map<?, ?> map, File file, Charset charset, String kvSeparator, boolean isAppend) throws IORuntimeException {
      return FileWriter.create(file, charset).writeMap(map, kvSeparator, isAppend);
   }

   public static File writeBytes(byte[] data, String path) throws IORuntimeException {
      return writeBytes(data, touch(path));
   }

   public static File writeBytes(byte[] data, File dest) throws IORuntimeException {
      return writeBytes(data, dest, 0, data.length, false);
   }

   public static File writeBytes(byte[] data, File dest, int off, int len, boolean isAppend) throws IORuntimeException {
      return FileWriter.create(dest).write(data, off, len, isAppend);
   }

   public static File writeFromStream(InputStream in, File dest) throws IORuntimeException {
      return writeFromStream(in, dest, true);
   }

   public static File writeFromStream(InputStream in, File dest, boolean isCloseIn) throws IORuntimeException {
      return FileWriter.create(dest).writeFromStream(in, isCloseIn);
   }

   public static File writeFromStream(InputStream in, String fullFilePath) throws IORuntimeException {
      return writeFromStream(in, touch(fullFilePath));
   }

   public static long writeToStream(File file, OutputStream out) throws IORuntimeException {
      return cn.hutool.core.io.file.FileReader.create(file).writeToStream(out);
   }

   public static long writeToStream(String fullFilePath, OutputStream out) throws IORuntimeException {
      return writeToStream(touch(fullFilePath), out);
   }

   public static String readableFileSize(File file) {
      return readableFileSize(file.length());
   }

   public static String readableFileSize(long size) {
      return DataSizeUtil.format(size);
   }

   public static File convertCharset(File file, Charset srcCharset, Charset destCharset) {
      return CharsetUtil.convert(file, srcCharset, destCharset);
   }

   public static File convertLineSeparator(File file, Charset charset, LineSeparator lineSeparator) {
      List<String> lines = readLines(file, charset);
      return FileWriter.create(file, charset).writeLines(lines, lineSeparator, false);
   }

   public static String cleanInvalid(String fileName) {
      return FileNameUtil.cleanInvalid(fileName);
   }

   public static boolean containsInvalid(String fileName) {
      return FileNameUtil.containsInvalid(fileName);
   }

   public static long checksumCRC32(File file) throws IORuntimeException {
      return checksum(file, new CRC32()).getValue();
   }

   public static Checksum checksum(File file, Checksum checksum) throws IORuntimeException {
      Assert.notNull(file, "File is null !");
      if (file.isDirectory()) {
         throw new IllegalArgumentException("Checksums can't be computed on directories");
      } else {
         try {
            return IoUtil.checksum(new FileInputStream(file), checksum);
         } catch (FileNotFoundException var3) {
            throw new IORuntimeException(var3);
         }
      }
   }

   public static File getWebRoot() {
      String classPath = ClassUtil.getClassPath();
      return StrUtil.isNotBlank(classPath) ? getParent((File)file(classPath), 2) : null;
   }

   public static String getParent(String filePath, int level) {
      File parent = getParent(file(filePath), level);

      try {
         return null == parent ? null : parent.getCanonicalPath();
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }
   }

   public static File getParent(File file, int level) {
      if (level >= 1 && null != file) {
         File parentFile;
         try {
            parentFile = file.getCanonicalFile().getParentFile();
         } catch (IOException var4) {
            throw new IORuntimeException(var4);
         }

         return 1 == level ? parentFile : getParent(parentFile, level - 1);
      } else {
         return file;
      }
   }

   public static File checkSlip(File parentFile, File file) throws IllegalArgumentException {
      if (null != parentFile && null != file) {
         String parentCanonicalPath;
         String canonicalPath;
         try {
            parentCanonicalPath = parentFile.getCanonicalPath();
            canonicalPath = file.getCanonicalPath();
         } catch (IOException var5) {
            parentCanonicalPath = parentFile.getAbsolutePath();
            canonicalPath = file.getAbsolutePath();
         }

         if (!canonicalPath.startsWith(parentCanonicalPath)) {
            throw new IllegalArgumentException("New file is outside of the parent dir: " + file.getName());
         }
      }

      return file;
   }

   public static String getMimeType(String filePath) {
      String contentType = URLConnection.getFileNameMap().getContentTypeFor(filePath);
      if (null == contentType) {
         if (StrUtil.endWithIgnoreCase(filePath, ".css")) {
            contentType = "text/css";
         } else if (StrUtil.endWithIgnoreCase(filePath, ".js")) {
            contentType = "application/x-javascript";
         } else if (StrUtil.endWithIgnoreCase(filePath, ".rar")) {
            contentType = "application/x-rar-compressed";
         } else if (StrUtil.endWithIgnoreCase(filePath, ".7z")) {
            contentType = "application/x-7z-compressed";
         }
      }

      if (null == contentType) {
         contentType = getMimeType(Paths.get(filePath));
      }

      return contentType;
   }

   public static boolean isSymlink(File file) {
      return isSymlink(file.toPath());
   }

   public static boolean isSub(File parent, File sub) {
      Assert.notNull(parent);
      Assert.notNull(sub);
      return isSub(parent.toPath(), sub.toPath());
   }

   public static RandomAccessFile createRandomAccessFile(Path path, FileMode mode) {
      return createRandomAccessFile(path.toFile(), mode);
   }

   public static RandomAccessFile createRandomAccessFile(File file, FileMode mode) {
      try {
         return new RandomAccessFile(file, mode.name());
      } catch (FileNotFoundException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public static void tail(File file, LineHandler handler) {
      tail(file, CharsetUtil.CHARSET_UTF_8, handler);
   }

   public static void tail(File file, Charset charset, LineHandler handler) {
      (new Tailer(file, charset, handler)).start();
   }

   public static void tail(File file, Charset charset) {
      tail(file, charset, Tailer.CONSOLE_HANDLER);
   }

   private static File buildFile(File outFile, String fileName) {
      fileName = fileName.replace('\\', '/');
      if (!isWindows() && fileName.lastIndexOf(47, fileName.length() - 2) > 0) {
         List<String> pathParts = StrUtil.split(fileName, '/', false, true);
         int lastPartIndex = pathParts.size() - 1;

         for(int i = 0; i < lastPartIndex; ++i) {
            outFile = new File(outFile, (String)pathParts.get(i));
         }

         outFile.mkdirs();
         fileName = (String)pathParts.get(lastPartIndex);
      }

      return new File(outFile, fileName);
   }

   static {
      FILE_SEPARATOR = File.separator;
      PATH_SEPARATOR = File.pathSeparator;
      PATTERN_PATH_ABSOLUTE = Pattern.compile("^[a-zA-Z]:([/\\\\].*)?");
   }
}
