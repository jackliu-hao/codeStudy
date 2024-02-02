/*     */ package cn.hutool.core.compress;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.ZipUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Enumeration;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipFile;
/*     */ import java.util.zip.ZipInputStream;
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
/*     */ public class ZipReader
/*     */   implements Closeable
/*     */ {
/*     */   private ZipFile zipFile;
/*     */   private ZipInputStream in;
/*     */   
/*     */   public static ZipReader of(File zipFile, Charset charset) {
/*  40 */     return new ZipReader(zipFile, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ZipReader of(InputStream in, Charset charset) {
/*  51 */     return new ZipReader(in, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipReader(File zipFile, Charset charset) {
/*  61 */     this.zipFile = ZipUtil.toZipFile(zipFile, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipReader(ZipFile zipFile) {
/*  70 */     this.zipFile = zipFile;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipReader(InputStream in, Charset charset) {
/*  80 */     this.in = new ZipInputStream(in, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipReader(ZipInputStream zin) {
/*  89 */     this.in = zin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream get(String path) {
/* 100 */     if (null != this.zipFile) {
/* 101 */       ZipFile zipFile = this.zipFile;
/* 102 */       ZipEntry entry = zipFile.getEntry(path);
/* 103 */       if (null != entry) {
/* 104 */         return ZipUtil.getStream(zipFile, entry);
/*     */       }
/*     */     } else {
/*     */       try {
/* 108 */         this.in.reset();
/*     */         ZipEntry zipEntry;
/* 110 */         while (null != (zipEntry = this.in.getNextEntry())) {
/* 111 */           if (zipEntry.getName().equals(path)) {
/* 112 */             return this.in;
/*     */           }
/*     */         } 
/* 115 */       } catch (IOException e) {
/* 116 */         throw new IORuntimeException(e);
/*     */       } 
/*     */     } 
/*     */     
/* 120 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public File readTo(File outFile) throws IORuntimeException {
/* 131 */     return readTo(outFile, null);
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
/*     */   public File readTo(File outFile, Filter<ZipEntry> entryFilter) throws IORuntimeException {
/* 144 */     read(zipEntry -> {
/*     */           if (null == entryFilter || entryFilter.accept(zipEntry)) {
/*     */             String path = zipEntry.getName();
/*     */             
/*     */             if (FileUtil.isWindows()) {
/*     */               path = StrUtil.replace(path, "*", "_");
/*     */             }
/*     */             
/*     */             File outItemFile = FileUtil.file(outFile, path);
/*     */             
/*     */             if (zipEntry.isDirectory()) {
/*     */               outItemFile.mkdirs();
/*     */             } else {
/*     */               InputStream in;
/*     */               
/*     */               if (null != this.zipFile) {
/*     */                 in = ZipUtil.getStream(this.zipFile, zipEntry);
/*     */               } else {
/*     */                 in = this.in;
/*     */               } 
/*     */               
/*     */               FileUtil.writeFromStream(in, outItemFile, false);
/*     */             } 
/*     */           } 
/*     */         });
/*     */     
/* 170 */     return outFile;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipReader read(Consumer<ZipEntry> consumer) throws IORuntimeException {
/* 181 */     if (null != this.zipFile) {
/* 182 */       readFromZipFile(consumer);
/*     */     } else {
/* 184 */       readFromStream(consumer);
/*     */     } 
/* 186 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IORuntimeException {
/* 191 */     if (null != this.zipFile) {
/* 192 */       IoUtil.close(this.zipFile);
/*     */     } else {
/* 194 */       IoUtil.close(this.in);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readFromZipFile(Consumer<ZipEntry> consumer) {
/* 204 */     Enumeration<? extends ZipEntry> em = this.zipFile.entries();
/* 205 */     while (em.hasMoreElements()) {
/* 206 */       consumer.accept(em.nextElement());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void readFromStream(Consumer<ZipEntry> consumer) throws IORuntimeException {
/*     */     try {
/*     */       ZipEntry zipEntry;
/* 219 */       while (null != (zipEntry = this.in.getNextEntry())) {
/* 220 */         consumer.accept(zipEntry);
/*     */       }
/* 222 */     } catch (IOException e) {
/* 223 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\compress\ZipReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */