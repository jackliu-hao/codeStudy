/*     */ package cn.hutool.core.compress;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.io.resource.Resource;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.ZipUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipOutputStream;
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
/*     */ public class ZipWriter
/*     */   implements Closeable
/*     */ {
/*     */   private final ZipOutputStream out;
/*     */   
/*     */   public static ZipWriter of(File zipFile, Charset charset) {
/*  37 */     return new ZipWriter(zipFile, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ZipWriter of(OutputStream out, Charset charset) {
/*  48 */     return new ZipWriter(out, charset);
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
/*     */   public ZipWriter(File zipFile, Charset charset) {
/*  60 */     this.out = getZipOutputStream(zipFile, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipWriter(OutputStream out, Charset charset) {
/*  70 */     this.out = ZipUtil.getZipOutputStream(out, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipWriter(ZipOutputStream out) {
/*  79 */     this.out = out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipWriter setLevel(int level) {
/*  89 */     this.out.setLevel(level);
/*  90 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipWriter setComment(String comment) {
/* 100 */     this.out.setComment(comment);
/* 101 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipOutputStream getOut() {
/* 110 */     return this.out;
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
/*     */   public ZipWriter add(boolean withSrcDir, FileFilter filter, File... files) throws IORuntimeException {
/* 124 */     for (File file : files) {
/*     */       String srcRootDir;
/*     */       
/*     */       try {
/* 128 */         srcRootDir = file.getCanonicalPath();
/* 129 */         if (false == file.isDirectory() || withSrcDir)
/*     */         {
/* 131 */           srcRootDir = file.getCanonicalFile().getParentFile().getCanonicalPath();
/*     */         }
/* 133 */       } catch (IOException e) {
/* 134 */         throw new IORuntimeException(e);
/*     */       } 
/*     */       
/* 137 */       _add(file, srcRootDir, filter);
/*     */     } 
/* 139 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipWriter add(Resource... resources) throws IORuntimeException {
/* 150 */     for (Resource resource : resources) {
/* 151 */       if (null != resource) {
/* 152 */         add(resource.getName(), resource.getStream());
/*     */       }
/*     */     } 
/* 155 */     return this;
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
/*     */   public ZipWriter add(String path, InputStream in) throws IORuntimeException {
/* 168 */     path = StrUtil.nullToEmpty(path);
/* 169 */     if (null == in) {
/*     */       
/* 171 */       path = StrUtil.addSuffixIfNot(path, "/");
/* 172 */       if (StrUtil.isBlank(path)) {
/* 173 */         return this;
/*     */       }
/*     */     } 
/*     */     
/* 177 */     return putEntry(path, in);
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
/*     */   public ZipWriter add(String[] paths, InputStream[] ins) throws IORuntimeException {
/* 191 */     if (ArrayUtil.isEmpty((Object[])paths) || ArrayUtil.isEmpty((Object[])ins)) {
/* 192 */       throw new IllegalArgumentException("Paths or ins is empty !");
/*     */     }
/* 194 */     if (paths.length != ins.length) {
/* 195 */       throw new IllegalArgumentException("Paths length is not equals to ins length !");
/*     */     }
/*     */     
/* 198 */     for (int i = 0; i < paths.length; i++) {
/* 199 */       add(paths[i], ins[i]);
/*     */     }
/*     */     
/* 202 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IORuntimeException {
/*     */     try {
/* 208 */       this.out.finish();
/* 209 */     } catch (IOException e) {
/* 210 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 212 */       IoUtil.close(this.out);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ZipOutputStream getZipOutputStream(File zipFile, Charset charset) {
/* 224 */     return ZipUtil.getZipOutputStream(FileUtil.getOutputStream(zipFile), charset);
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
/*     */   private ZipWriter _add(File file, String srcRootDir, FileFilter filter) throws IORuntimeException {
/* 238 */     if (null == file || (null != filter && false == filter.accept(file))) {
/* 239 */       return this;
/*     */     }
/*     */ 
/*     */     
/* 243 */     String subPath = FileUtil.subPath(srcRootDir, file);
/* 244 */     if (file.isDirectory()) {
/*     */       
/* 246 */       File[] files = file.listFiles();
/* 247 */       if (ArrayUtil.isEmpty((Object[])files)) {
/*     */         
/* 249 */         add(subPath, (InputStream)null);
/*     */       } else {
/*     */         
/* 252 */         for (File childFile : files) {
/* 253 */           _add(childFile, srcRootDir, filter);
/*     */         }
/*     */       } 
/*     */     } else {
/*     */       
/* 258 */       putEntry(subPath, FileUtil.getInputStream(file));
/*     */     } 
/* 260 */     return this;
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
/*     */   private ZipWriter putEntry(String path, InputStream in) throws IORuntimeException {
/*     */     try {
/* 273 */       this.out.putNextEntry(new ZipEntry(path));
/* 274 */       if (null != in) {
/* 275 */         IoUtil.copy(in, this.out);
/*     */       }
/* 277 */       this.out.closeEntry();
/* 278 */     } catch (IOException e) {
/* 279 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 281 */       IoUtil.close(in);
/*     */     } 
/*     */     
/* 284 */     IoUtil.flush(this.out);
/* 285 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\compress\ZipWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */