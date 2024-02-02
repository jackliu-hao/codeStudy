/*      */ package cn.hutool.core.util;
/*      */ 
/*      */ import cn.hutool.core.collection.EnumerationIter;
/*      */ import cn.hutool.core.compress.Deflate;
/*      */ import cn.hutool.core.compress.Gzip;
/*      */ import cn.hutool.core.compress.ZipCopyVisitor;
/*      */ import cn.hutool.core.compress.ZipReader;
/*      */ import cn.hutool.core.compress.ZipWriter;
/*      */ import cn.hutool.core.exceptions.UtilException;
/*      */ import cn.hutool.core.io.FastByteArrayOutputStream;
/*      */ import cn.hutool.core.io.FileUtil;
/*      */ import cn.hutool.core.io.IORuntimeException;
/*      */ import cn.hutool.core.io.IoUtil;
/*      */ import cn.hutool.core.io.file.FileSystemUtil;
/*      */ import cn.hutool.core.io.file.PathUtil;
/*      */ import cn.hutool.core.io.resource.Resource;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileFilter;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.file.CopyOption;
/*      */ import java.nio.file.FileAlreadyExistsException;
/*      */ import java.nio.file.FileSystem;
/*      */ import java.nio.file.FileVisitor;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.Path;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.function.Consumer;
/*      */ import java.util.zip.ZipEntry;
/*      */ import java.util.zip.ZipFile;
/*      */ import java.util.zip.ZipInputStream;
/*      */ import java.util.zip.ZipOutputStream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ZipUtil
/*      */ {
/*      */   private static final int DEFAULT_BYTE_ARRAY_LENGTH = 32;
/*   53 */   private static final Charset DEFAULT_CHARSET = CharsetUtil.defaultCharset();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ZipFile toZipFile(File file, Charset charset) {
/*      */     try {
/*   64 */       return new ZipFile(file, ObjectUtil.<Charset>defaultIfNull(charset, CharsetUtil.CHARSET_UTF_8));
/*   65 */     } catch (IOException e) {
/*   66 */       throw new IORuntimeException(e);
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
/*      */   public static InputStream getStream(ZipFile zipFile, ZipEntry zipEntry) {
/*      */     try {
/*   80 */       return zipFile.getInputStream(zipEntry);
/*   81 */     } catch (IOException e) {
/*   82 */       throw new IORuntimeException(e);
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
/*      */   public static ZipOutputStream getZipOutputStream(OutputStream out, Charset charset) {
/*   95 */     if (out instanceof ZipOutputStream) {
/*   96 */       return (ZipOutputStream)out;
/*      */     }
/*   98 */     return new ZipOutputStream(out, charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void append(Path zipPath, Path appendFilePath, CopyOption... options) throws IORuntimeException {
/*  113 */     try (FileSystem zipFileSystem = FileSystemUtil.createZip(zipPath.toString())) {
/*  114 */       if (Files.isDirectory(appendFilePath, new java.nio.file.LinkOption[0])) {
/*  115 */         Path source = appendFilePath.getParent();
/*  116 */         if (null == source)
/*      */         {
/*  118 */           source = appendFilePath;
/*      */         }
/*  120 */         Files.walkFileTree(appendFilePath, (FileVisitor<? super Path>)new ZipCopyVisitor(source, zipFileSystem, options));
/*      */       } else {
/*  122 */         Files.copy(appendFilePath, zipFileSystem.getPath(PathUtil.getName(appendFilePath), new String[0]), options);
/*      */       } 
/*  124 */     } catch (FileAlreadyExistsException fileAlreadyExistsException) {
/*      */     
/*  126 */     } catch (IOException e) {
/*  127 */       throw new IORuntimeException(e);
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
/*      */   public static File zip(String srcPath) throws UtilException {
/*  139 */     return zip(srcPath, DEFAULT_CHARSET);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File zip(String srcPath, Charset charset) throws UtilException {
/*  151 */     return zip(FileUtil.file(srcPath), charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File zip(File srcFile) throws UtilException {
/*  162 */     return zip(srcFile, DEFAULT_CHARSET);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File zip(File srcFile, Charset charset) throws UtilException {
/*  174 */     File zipFile = FileUtil.file(srcFile.getParentFile(), FileUtil.mainName(srcFile) + ".zip");
/*  175 */     zip(zipFile, charset, false, new File[] { srcFile });
/*  176 */     return zipFile;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File zip(String srcPath, String zipPath) throws UtilException {
/*  189 */     return zip(srcPath, zipPath, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File zip(String srcPath, String zipPath, boolean withSrcDir) throws UtilException {
/*  202 */     return zip(srcPath, zipPath, DEFAULT_CHARSET, withSrcDir);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File zip(String srcPath, String zipPath, Charset charset, boolean withSrcDir) throws UtilException {
/*  216 */     File srcFile = FileUtil.file(srcPath);
/*  217 */     File zipFile = FileUtil.file(zipPath);
/*  218 */     zip(zipFile, charset, withSrcDir, new File[] { srcFile });
/*  219 */     return zipFile;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File zip(File zipFile, boolean withSrcDir, File... srcFiles) throws UtilException {
/*  233 */     return zip(zipFile, DEFAULT_CHARSET, withSrcDir, srcFiles);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File zip(File zipFile, Charset charset, boolean withSrcDir, File... srcFiles) throws UtilException {
/*  247 */     return zip(zipFile, charset, withSrcDir, (FileFilter)null, srcFiles);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File zip(File zipFile, Charset charset, boolean withSrcDir, FileFilter filter, File... srcFiles) throws IORuntimeException {
/*  263 */     validateFiles(zipFile, srcFiles);
/*  264 */     ZipWriter.of(zipFile, charset).add(withSrcDir, filter, srcFiles).close();
/*  265 */     return zipFile;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void zip(OutputStream out, Charset charset, boolean withSrcDir, FileFilter filter, File... srcFiles) throws IORuntimeException {
/*  280 */     ZipWriter.of(out, charset).add(withSrcDir, filter, srcFiles).close();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
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
/*      */   public static void zip(ZipOutputStream zipOutputStream, boolean withSrcDir, FileFilter filter, File... srcFiles) throws IORuntimeException {
/*  296 */     try (ZipWriter zipWriter = new ZipWriter(zipOutputStream)) {
/*  297 */       zipWriter.add(withSrcDir, filter, srcFiles);
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
/*      */   public static File zip(File zipFile, String path, String data) throws UtilException {
/*  312 */     return zip(zipFile, path, data, DEFAULT_CHARSET);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File zip(File zipFile, String path, String data, Charset charset) throws UtilException {
/*  327 */     return zip(zipFile, path, IoUtil.toStream(data, charset), charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File zip(File zipFile, String path, InputStream in) throws UtilException {
/*  342 */     return zip(zipFile, path, in, DEFAULT_CHARSET);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File zip(File zipFile, String path, InputStream in, Charset charset) throws UtilException {
/*  357 */     return zip(zipFile, new String[] { path }, new InputStream[] { in }, charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File zip(File zipFile, String[] paths, InputStream[] ins) throws UtilException {
/*  372 */     return zip(zipFile, paths, ins, DEFAULT_CHARSET);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File zip(File zipFile, String[] paths, InputStream[] ins, Charset charset) throws UtilException {
/*  388 */     try (ZipWriter zipWriter = ZipWriter.of(zipFile, charset)) {
/*  389 */       zipWriter.add(paths, ins);
/*      */     } 
/*      */     
/*  392 */     return zipFile;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void zip(OutputStream out, String[] paths, InputStream[] ins) {
/*  404 */     zip(getZipOutputStream(out, DEFAULT_CHARSET), paths, ins);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void zip(ZipOutputStream zipOutputStream, String[] paths, InputStream[] ins) throws IORuntimeException {
/*  417 */     try (ZipWriter zipWriter = new ZipWriter(zipOutputStream)) {
/*  418 */       zipWriter.add(paths, ins);
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
/*      */   public static File zip(File zipFile, Charset charset, Resource... resources) throws UtilException {
/*  434 */     ZipWriter.of(zipFile, charset).add(resources).close();
/*  435 */     return zipFile;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File unzip(String zipFilePath) throws UtilException {
/*  448 */     return unzip(zipFilePath, DEFAULT_CHARSET);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File unzip(String zipFilePath, Charset charset) throws UtilException {
/*  461 */     return unzip(FileUtil.file(zipFilePath), charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File unzip(File zipFile) throws UtilException {
/*  473 */     return unzip(zipFile, DEFAULT_CHARSET);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File unzip(File zipFile, Charset charset) throws UtilException {
/*  486 */     File destDir = FileUtil.file(zipFile.getParentFile(), FileUtil.mainName(zipFile));
/*  487 */     return unzip(zipFile, destDir, charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File unzip(String zipFilePath, String outFileDir) throws UtilException {
/*  499 */     return unzip(zipFilePath, outFileDir, DEFAULT_CHARSET);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File unzip(String zipFilePath, String outFileDir, Charset charset) throws UtilException {
/*  512 */     return unzip(FileUtil.file(zipFilePath), FileUtil.mkdir(outFileDir), charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File unzip(File zipFile, File outFile) throws UtilException {
/*  524 */     return unzip(zipFile, outFile, DEFAULT_CHARSET);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File unzip(File zipFile, File outFile, Charset charset) {
/*  537 */     return unzip(toZipFile(zipFile, charset), outFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File unzip(ZipFile zipFile, File outFile) throws IORuntimeException {
/*  550 */     if (outFile.exists() && outFile.isFile()) {
/*  551 */       throw new IllegalArgumentException(
/*  552 */           StrUtil.format("Target path [{}] exist!", new Object[] { outFile.getAbsolutePath() }));
/*      */     }
/*      */     
/*  555 */     try (ZipReader reader = new ZipReader(zipFile)) {
/*  556 */       reader.readTo(outFile);
/*      */     } 
/*  558 */     return outFile;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static InputStream get(File zipFile, Charset charset, String path) {
/*  571 */     return get(toZipFile(zipFile, charset), path);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static InputStream get(ZipFile zipFile, String path) {
/*  583 */     ZipEntry entry = zipFile.getEntry(path);
/*  584 */     if (null != entry) {
/*  585 */       return getStream(zipFile, entry);
/*      */     }
/*  587 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void read(ZipFile zipFile, Consumer<ZipEntry> consumer) {
/*  598 */     try (ZipReader reader = new ZipReader(zipFile)) {
/*  599 */       reader.read(consumer);
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
/*      */   public static File unzip(InputStream in, File outFile, Charset charset) throws UtilException {
/*  615 */     if (null == charset) {
/*  616 */       charset = DEFAULT_CHARSET;
/*      */     }
/*  618 */     return unzip(new ZipInputStream(in, charset), outFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File unzip(ZipInputStream zipStream, File outFile) throws UtilException {
/*  632 */     try (ZipReader reader = new ZipReader(zipStream)) {
/*  633 */       reader.readTo(outFile);
/*      */     } 
/*  635 */     return outFile;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void read(ZipInputStream zipStream, Consumer<ZipEntry> consumer) {
/*  646 */     try (ZipReader reader = new ZipReader(zipStream)) {
/*  647 */       reader.read(consumer);
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
/*      */   public static byte[] unzipFileBytes(String zipFilePath, String name) {
/*  660 */     return unzipFileBytes(zipFilePath, DEFAULT_CHARSET, name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] unzipFileBytes(String zipFilePath, Charset charset, String name) {
/*  673 */     return unzipFileBytes(FileUtil.file(zipFilePath), charset, name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] unzipFileBytes(File zipFile, String name) {
/*  685 */     return unzipFileBytes(zipFile, DEFAULT_CHARSET, name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] unzipFileBytes(File zipFile, Charset charset, String name) {
/*  698 */     try (ZipReader reader = ZipReader.of(zipFile, charset)) {
/*  699 */       return IoUtil.readBytes(reader.get(name));
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
/*      */   public static byte[] gzip(String content, String charset) throws UtilException {
/*  714 */     return gzip(StrUtil.bytes(content, charset));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] gzip(byte[] buf) throws UtilException {
/*  725 */     return gzip(new ByteArrayInputStream(buf), buf.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] gzip(File file) throws UtilException {
/*  736 */     BufferedInputStream in = null;
/*      */     try {
/*  738 */       in = FileUtil.getInputStream(file);
/*  739 */       return gzip(in, (int)file.length());
/*      */     } finally {
/*  741 */       IoUtil.close(in);
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
/*      */   public static byte[] gzip(InputStream in) throws UtilException {
/*  754 */     return gzip(in, 32);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] gzip(InputStream in, int length) throws UtilException {
/*  767 */     ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
/*  768 */     Gzip.of(in, bos).gzip().close();
/*  769 */     return bos.toByteArray();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String unGzip(byte[] buf, String charset) throws UtilException {
/*  781 */     return StrUtil.str(unGzip(buf), charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] unGzip(byte[] buf) throws UtilException {
/*  792 */     return unGzip(new ByteArrayInputStream(buf), buf.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] unGzip(InputStream in) throws UtilException {
/*  803 */     return unGzip(in, 32);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] unGzip(InputStream in, int length) throws UtilException {
/*  816 */     FastByteArrayOutputStream bos = new FastByteArrayOutputStream(length);
/*  817 */     Gzip.of(in, (OutputStream)bos).unGzip().close();
/*  818 */     return bos.toByteArray();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] zlib(String content, String charset, int level) {
/*  833 */     return zlib(StrUtil.bytes(content, charset), level);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] zlib(File file, int level) {
/*  845 */     BufferedInputStream in = null;
/*      */     try {
/*  847 */       in = FileUtil.getInputStream(file);
/*  848 */       return zlib(in, level, (int)file.length());
/*      */     } finally {
/*  850 */       IoUtil.close(in);
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
/*      */   public static byte[] zlib(byte[] buf, int level) {
/*  863 */     return zlib(new ByteArrayInputStream(buf), level, buf.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] zlib(InputStream in, int level) {
/*  875 */     return zlib(in, level, 32);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] zlib(InputStream in, int level, int length) {
/*  888 */     ByteArrayOutputStream out = new ByteArrayOutputStream(length);
/*  889 */     Deflate.of(in, out, false).deflater(level);
/*  890 */     return out.toByteArray();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String unZlib(byte[] buf, String charset) {
/*  902 */     return StrUtil.str(unZlib(buf), charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] unZlib(byte[] buf) {
/*  913 */     return unZlib(new ByteArrayInputStream(buf), buf.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] unZlib(InputStream in) {
/*  924 */     return unZlib(in, 32);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] unZlib(InputStream in, int length) {
/*  936 */     ByteArrayOutputStream out = new ByteArrayOutputStream(length);
/*  937 */     Deflate.of(in, out, false).inflater();
/*  938 */     return out.toByteArray();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> listFileNames(ZipFile zipFile, String dir) {
/*  951 */     if (StrUtil.isNotBlank(dir))
/*      */     {
/*  953 */       dir = StrUtil.addSuffixIfNot(dir, "/");
/*      */     }
/*      */     
/*  956 */     List<String> fileNames = new ArrayList<>();
/*      */     
/*  958 */     for (ZipEntry entry : new EnumerationIter(zipFile.entries())) {
/*  959 */       String name = entry.getName();
/*  960 */       if (StrUtil.isEmpty(dir) || name.startsWith(dir)) {
/*  961 */         String nameSuffix = StrUtil.removePrefix(name, dir);
/*  962 */         if (StrUtil.isNotEmpty(nameSuffix) && false == StrUtil.contains(nameSuffix, '/')) {
/*  963 */           fileNames.add(nameSuffix);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  968 */     return fileNames;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void validateFiles(File zipFile, File... srcFiles) throws UtilException {
/*  980 */     if (zipFile.isDirectory()) {
/*  981 */       throw new UtilException("Zip file [{}] must not be a directory !", new Object[] { zipFile.getAbsoluteFile() });
/*      */     }
/*      */     
/*  984 */     for (File srcFile : srcFiles) {
/*  985 */       if (null != srcFile) {
/*      */         File parentFile;
/*      */         
/*  988 */         if (false == srcFile.exists()) {
/*  989 */           throw new UtilException(StrUtil.format("File [{}] not exist!", new Object[] { srcFile.getAbsolutePath() }));
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         try {
/*  996 */           parentFile = zipFile.getCanonicalFile().getParentFile();
/*  997 */         } catch (IOException e) {
/*  998 */           parentFile = zipFile.getParentFile();
/*      */         } 
/*      */ 
/*      */         
/* 1002 */         if (srcFile.isDirectory() && FileUtil.isSub(srcFile, parentFile))
/* 1003 */           throw new UtilException("Zip file path [{}] must not be the child directory of [{}] !", new Object[] { zipFile.getPath(), srcFile.getPath() }); 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\ZipUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */