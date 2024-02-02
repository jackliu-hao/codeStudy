package cn.hutool.core.util;

import cn.hutool.core.collection.EnumerationIter;
import cn.hutool.core.compress.Deflate;
import cn.hutool.core.compress.Gzip;
import cn.hutool.core.compress.ZipCopyVisitor;
import cn.hutool.core.compress.ZipReader;
import cn.hutool.core.compress.ZipWriter;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileSystemUtil;
import cn.hutool.core.io.file.PathUtil;
import cn.hutool.core.io.resource.Resource;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
   private static final int DEFAULT_BYTE_ARRAY_LENGTH = 32;
   private static final Charset DEFAULT_CHARSET = CharsetUtil.defaultCharset();

   public static ZipFile toZipFile(File file, Charset charset) {
      try {
         return new ZipFile(file, (Charset)ObjectUtil.defaultIfNull(charset, (Object)CharsetUtil.CHARSET_UTF_8));
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public static InputStream getStream(ZipFile zipFile, ZipEntry zipEntry) {
      try {
         return zipFile.getInputStream(zipEntry);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public static ZipOutputStream getZipOutputStream(OutputStream out, Charset charset) {
      return out instanceof ZipOutputStream ? (ZipOutputStream)out : new ZipOutputStream(out, charset);
   }

   public static void append(Path zipPath, Path appendFilePath, CopyOption... options) throws IORuntimeException {
      try {
         FileSystem zipFileSystem = FileSystemUtil.createZip(zipPath.toString());
         Throwable var4 = null;

         try {
            if (Files.isDirectory(appendFilePath, new LinkOption[0])) {
               Path source = appendFilePath.getParent();
               if (null == source) {
                  source = appendFilePath;
               }

               Files.walkFileTree(appendFilePath, new ZipCopyVisitor(source, zipFileSystem, options));
            } else {
               Files.copy(appendFilePath, zipFileSystem.getPath(PathUtil.getName(appendFilePath)), options);
            }
         } catch (Throwable var15) {
            var4 = var15;
            throw var15;
         } finally {
            if (zipFileSystem != null) {
               if (var4 != null) {
                  try {
                     zipFileSystem.close();
                  } catch (Throwable var14) {
                     var4.addSuppressed(var14);
                  }
               } else {
                  zipFileSystem.close();
               }
            }

         }
      } catch (FileAlreadyExistsException var17) {
      } catch (IOException var18) {
         throw new IORuntimeException(var18);
      }

   }

   public static File zip(String srcPath) throws UtilException {
      return zip(srcPath, DEFAULT_CHARSET);
   }

   public static File zip(String srcPath, Charset charset) throws UtilException {
      return zip(FileUtil.file(srcPath), charset);
   }

   public static File zip(File srcFile) throws UtilException {
      return zip(srcFile, DEFAULT_CHARSET);
   }

   public static File zip(File srcFile, Charset charset) throws UtilException {
      File zipFile = FileUtil.file(srcFile.getParentFile(), FileUtil.mainName(srcFile) + ".zip");
      zip(zipFile, charset, false, srcFile);
      return zipFile;
   }

   public static File zip(String srcPath, String zipPath) throws UtilException {
      return zip(srcPath, zipPath, false);
   }

   public static File zip(String srcPath, String zipPath, boolean withSrcDir) throws UtilException {
      return zip(srcPath, zipPath, DEFAULT_CHARSET, withSrcDir);
   }

   public static File zip(String srcPath, String zipPath, Charset charset, boolean withSrcDir) throws UtilException {
      File srcFile = FileUtil.file(srcPath);
      File zipFile = FileUtil.file(zipPath);
      zip(zipFile, charset, withSrcDir, srcFile);
      return zipFile;
   }

   public static File zip(File zipFile, boolean withSrcDir, File... srcFiles) throws UtilException {
      return zip(zipFile, DEFAULT_CHARSET, withSrcDir, srcFiles);
   }

   public static File zip(File zipFile, Charset charset, boolean withSrcDir, File... srcFiles) throws UtilException {
      return zip((File)zipFile, charset, withSrcDir, (FileFilter)null, srcFiles);
   }

   public static File zip(File zipFile, Charset charset, boolean withSrcDir, FileFilter filter, File... srcFiles) throws IORuntimeException {
      validateFiles(zipFile, srcFiles);
      ZipWriter.of(zipFile, charset).add(withSrcDir, filter, srcFiles).close();
      return zipFile;
   }

   public static void zip(OutputStream out, Charset charset, boolean withSrcDir, FileFilter filter, File... srcFiles) throws IORuntimeException {
      ZipWriter.of(out, charset).add(withSrcDir, filter, srcFiles).close();
   }

   /** @deprecated */
   @Deprecated
   public static void zip(ZipOutputStream zipOutputStream, boolean withSrcDir, FileFilter filter, File... srcFiles) throws IORuntimeException {
      ZipWriter zipWriter = new ZipWriter(zipOutputStream);
      Throwable var5 = null;

      try {
         zipWriter.add(withSrcDir, filter, srcFiles);
      } catch (Throwable var14) {
         var5 = var14;
         throw var14;
      } finally {
         if (zipWriter != null) {
            if (var5 != null) {
               try {
                  zipWriter.close();
               } catch (Throwable var13) {
                  var5.addSuppressed(var13);
               }
            } else {
               zipWriter.close();
            }
         }

      }

   }

   public static File zip(File zipFile, String path, String data) throws UtilException {
      return zip(zipFile, path, data, DEFAULT_CHARSET);
   }

   public static File zip(File zipFile, String path, String data, Charset charset) throws UtilException {
      return zip(zipFile, (String)path, (InputStream)IoUtil.toStream(data, charset), charset);
   }

   public static File zip(File zipFile, String path, InputStream in) throws UtilException {
      return zip(zipFile, path, in, DEFAULT_CHARSET);
   }

   public static File zip(File zipFile, String path, InputStream in, Charset charset) throws UtilException {
      return zip(zipFile, new String[]{path}, new InputStream[]{in}, charset);
   }

   public static File zip(File zipFile, String[] paths, InputStream[] ins) throws UtilException {
      return zip(zipFile, paths, ins, DEFAULT_CHARSET);
   }

   public static File zip(File zipFile, String[] paths, InputStream[] ins, Charset charset) throws UtilException {
      ZipWriter zipWriter = ZipWriter.of(zipFile, charset);
      Throwable var5 = null;

      try {
         zipWriter.add(paths, ins);
      } catch (Throwable var14) {
         var5 = var14;
         throw var14;
      } finally {
         if (zipWriter != null) {
            if (var5 != null) {
               try {
                  zipWriter.close();
               } catch (Throwable var13) {
                  var5.addSuppressed(var13);
               }
            } else {
               zipWriter.close();
            }
         }

      }

      return zipFile;
   }

   public static void zip(OutputStream out, String[] paths, InputStream[] ins) {
      zip(getZipOutputStream(out, DEFAULT_CHARSET), paths, ins);
   }

   public static void zip(ZipOutputStream zipOutputStream, String[] paths, InputStream[] ins) throws IORuntimeException {
      ZipWriter zipWriter = new ZipWriter(zipOutputStream);
      Throwable var4 = null;

      try {
         zipWriter.add(paths, ins);
      } catch (Throwable var13) {
         var4 = var13;
         throw var13;
      } finally {
         if (zipWriter != null) {
            if (var4 != null) {
               try {
                  zipWriter.close();
               } catch (Throwable var12) {
                  var4.addSuppressed(var12);
               }
            } else {
               zipWriter.close();
            }
         }

      }

   }

   public static File zip(File zipFile, Charset charset, Resource... resources) throws UtilException {
      ZipWriter.of(zipFile, charset).add(resources).close();
      return zipFile;
   }

   public static File unzip(String zipFilePath) throws UtilException {
      return unzip(zipFilePath, DEFAULT_CHARSET);
   }

   public static File unzip(String zipFilePath, Charset charset) throws UtilException {
      return unzip(FileUtil.file(zipFilePath), charset);
   }

   public static File unzip(File zipFile) throws UtilException {
      return unzip(zipFile, DEFAULT_CHARSET);
   }

   public static File unzip(File zipFile, Charset charset) throws UtilException {
      File destDir = FileUtil.file(zipFile.getParentFile(), FileUtil.mainName(zipFile));
      return unzip(zipFile, destDir, charset);
   }

   public static File unzip(String zipFilePath, String outFileDir) throws UtilException {
      return unzip(zipFilePath, outFileDir, DEFAULT_CHARSET);
   }

   public static File unzip(String zipFilePath, String outFileDir, Charset charset) throws UtilException {
      return unzip(FileUtil.file(zipFilePath), FileUtil.mkdir(outFileDir), charset);
   }

   public static File unzip(File zipFile, File outFile) throws UtilException {
      return unzip(zipFile, outFile, DEFAULT_CHARSET);
   }

   public static File unzip(File zipFile, File outFile, Charset charset) {
      return unzip(toZipFile(zipFile, charset), outFile);
   }

   public static File unzip(ZipFile zipFile, File outFile) throws IORuntimeException {
      if (outFile.exists() && outFile.isFile()) {
         throw new IllegalArgumentException(StrUtil.format("Target path [{}] exist!", new Object[]{outFile.getAbsolutePath()}));
      } else {
         ZipReader reader = new ZipReader(zipFile);
         Throwable var3 = null;

         try {
            reader.readTo(outFile);
         } catch (Throwable var12) {
            var3 = var12;
            throw var12;
         } finally {
            if (reader != null) {
               if (var3 != null) {
                  try {
                     reader.close();
                  } catch (Throwable var11) {
                     var3.addSuppressed(var11);
                  }
               } else {
                  reader.close();
               }
            }

         }

         return outFile;
      }
   }

   public static InputStream get(File zipFile, Charset charset, String path) {
      return get(toZipFile(zipFile, charset), path);
   }

   public static InputStream get(ZipFile zipFile, String path) {
      ZipEntry entry = zipFile.getEntry(path);
      return null != entry ? getStream(zipFile, entry) : null;
   }

   public static void read(ZipFile zipFile, Consumer<ZipEntry> consumer) {
      ZipReader reader = new ZipReader(zipFile);
      Throwable var3 = null;

      try {
         reader.read(consumer);
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (reader != null) {
            if (var3 != null) {
               try {
                  reader.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               reader.close();
            }
         }

      }

   }

   public static File unzip(InputStream in, File outFile, Charset charset) throws UtilException {
      if (null == charset) {
         charset = DEFAULT_CHARSET;
      }

      return unzip(new ZipInputStream(in, charset), outFile);
   }

   public static File unzip(ZipInputStream zipStream, File outFile) throws UtilException {
      ZipReader reader = new ZipReader(zipStream);
      Throwable var3 = null;

      try {
         reader.readTo(outFile);
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (reader != null) {
            if (var3 != null) {
               try {
                  reader.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               reader.close();
            }
         }

      }

      return outFile;
   }

   public static void read(ZipInputStream zipStream, Consumer<ZipEntry> consumer) {
      ZipReader reader = new ZipReader(zipStream);
      Throwable var3 = null;

      try {
         reader.read(consumer);
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (reader != null) {
            if (var3 != null) {
               try {
                  reader.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               reader.close();
            }
         }

      }

   }

   public static byte[] unzipFileBytes(String zipFilePath, String name) {
      return unzipFileBytes(zipFilePath, DEFAULT_CHARSET, name);
   }

   public static byte[] unzipFileBytes(String zipFilePath, Charset charset, String name) {
      return unzipFileBytes(FileUtil.file(zipFilePath), charset, name);
   }

   public static byte[] unzipFileBytes(File zipFile, String name) {
      return unzipFileBytes(zipFile, DEFAULT_CHARSET, name);
   }

   public static byte[] unzipFileBytes(File zipFile, Charset charset, String name) {
      ZipReader reader = ZipReader.of(zipFile, charset);
      Throwable var4 = null;

      byte[] var5;
      try {
         var5 = IoUtil.readBytes(reader.get(name));
      } catch (Throwable var14) {
         var4 = var14;
         throw var14;
      } finally {
         if (reader != null) {
            if (var4 != null) {
               try {
                  reader.close();
               } catch (Throwable var13) {
                  var4.addSuppressed(var13);
               }
            } else {
               reader.close();
            }
         }

      }

      return var5;
   }

   public static byte[] gzip(String content, String charset) throws UtilException {
      return gzip(StrUtil.bytes(content, charset));
   }

   public static byte[] gzip(byte[] buf) throws UtilException {
      return gzip(new ByteArrayInputStream(buf), buf.length);
   }

   public static byte[] gzip(File file) throws UtilException {
      BufferedInputStream in = null;

      byte[] var2;
      try {
         in = FileUtil.getInputStream(file);
         var2 = gzip(in, (int)file.length());
      } finally {
         IoUtil.close(in);
      }

      return var2;
   }

   public static byte[] gzip(InputStream in) throws UtilException {
      return gzip(in, 32);
   }

   public static byte[] gzip(InputStream in, int length) throws UtilException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
      Gzip.of(in, bos).gzip().close();
      return bos.toByteArray();
   }

   public static String unGzip(byte[] buf, String charset) throws UtilException {
      return StrUtil.str(unGzip(buf), charset);
   }

   public static byte[] unGzip(byte[] buf) throws UtilException {
      return unGzip(new ByteArrayInputStream(buf), buf.length);
   }

   public static byte[] unGzip(InputStream in) throws UtilException {
      return unGzip(in, 32);
   }

   public static byte[] unGzip(InputStream in, int length) throws UtilException {
      FastByteArrayOutputStream bos = new FastByteArrayOutputStream(length);
      Gzip.of(in, bos).unGzip().close();
      return bos.toByteArray();
   }

   public static byte[] zlib(String content, String charset, int level) {
      return zlib(StrUtil.bytes(content, charset), level);
   }

   public static byte[] zlib(File file, int level) {
      BufferedInputStream in = null;

      byte[] var3;
      try {
         in = FileUtil.getInputStream(file);
         var3 = zlib(in, level, (int)file.length());
      } finally {
         IoUtil.close(in);
      }

      return var3;
   }

   public static byte[] zlib(byte[] buf, int level) {
      return zlib(new ByteArrayInputStream(buf), level, buf.length);
   }

   public static byte[] zlib(InputStream in, int level) {
      return zlib(in, level, 32);
   }

   public static byte[] zlib(InputStream in, int level, int length) {
      ByteArrayOutputStream out = new ByteArrayOutputStream(length);
      Deflate.of(in, out, false).deflater(level);
      return out.toByteArray();
   }

   public static String unZlib(byte[] buf, String charset) {
      return StrUtil.str(unZlib(buf), charset);
   }

   public static byte[] unZlib(byte[] buf) {
      return unZlib(new ByteArrayInputStream(buf), buf.length);
   }

   public static byte[] unZlib(InputStream in) {
      return unZlib(in, 32);
   }

   public static byte[] unZlib(InputStream in, int length) {
      ByteArrayOutputStream out = new ByteArrayOutputStream(length);
      Deflate.of(in, out, false).inflater();
      return out.toByteArray();
   }

   public static List<String> listFileNames(ZipFile zipFile, String dir) {
      if (StrUtil.isNotBlank(dir)) {
         dir = StrUtil.addSuffixIfNot(dir, "/");
      }

      List<String> fileNames = new ArrayList();
      Iterator var4 = (new EnumerationIter(zipFile.entries())).iterator();

      while(true) {
         String name;
         do {
            if (!var4.hasNext()) {
               return fileNames;
            }

            ZipEntry entry = (ZipEntry)var4.next();
            name = entry.getName();
         } while(!StrUtil.isEmpty(dir) && !name.startsWith(dir));

         String nameSuffix = StrUtil.removePrefix(name, dir);
         if (StrUtil.isNotEmpty(nameSuffix) && !StrUtil.contains(nameSuffix, '/')) {
            fileNames.add(nameSuffix);
         }
      }
   }

   private static void validateFiles(File zipFile, File... srcFiles) throws UtilException {
      if (zipFile.isDirectory()) {
         throw new UtilException("Zip file [{}] must not be a directory !", new Object[]{zipFile.getAbsoluteFile()});
      } else {
         File[] var2 = srcFiles;
         int var3 = srcFiles.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            File srcFile = var2[var4];
            if (null != srcFile) {
               if (!srcFile.exists()) {
                  throw new UtilException(StrUtil.format("File [{}] not exist!", new Object[]{srcFile.getAbsolutePath()}));
               }

               File parentFile;
               try {
                  parentFile = zipFile.getCanonicalFile().getParentFile();
               } catch (IOException var8) {
                  parentFile = zipFile.getParentFile();
               }

               if (srcFile.isDirectory() && FileUtil.isSub(srcFile, parentFile)) {
                  throw new UtilException("Zip file path [{}] must not be the child directory of [{}] !", new Object[]{zipFile.getPath(), srcFile.getPath()});
               }
            }
         }

      }
   }
}
