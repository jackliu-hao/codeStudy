/*     */ package cn.hutool.extra.compress.archiver;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.extra.compress.CompressException;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import org.apache.commons.compress.archivers.ArchiveException;
/*     */ import org.apache.commons.compress.archivers.ArchiveOutputStream;
/*     */ import org.apache.commons.compress.archivers.ArchiveStreamFactory;
/*     */ import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
/*     */ import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
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
/*     */ public class StreamArchiver
/*     */   implements Archiver
/*     */ {
/*     */   private final ArchiveOutputStream out;
/*     */   
/*     */   public static StreamArchiver create(Charset charset, String archiverName, File file) {
/*  45 */     return new StreamArchiver(charset, archiverName, file);
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
/*     */   public static StreamArchiver create(Charset charset, String archiverName, OutputStream out) {
/*  57 */     return new StreamArchiver(charset, archiverName, out);
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
/*     */   public StreamArchiver(Charset charset, String archiverName, File file) {
/*  70 */     this(charset, archiverName, FileUtil.getOutputStream(file));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StreamArchiver(Charset charset, String archiverName, OutputStream targetStream) {
/*  81 */     ArchiveStreamFactory factory = new ArchiveStreamFactory(charset.name());
/*     */     try {
/*  83 */       this.out = factory.createArchiveOutputStream(archiverName, targetStream);
/*  84 */     } catch (ArchiveException e) {
/*  85 */       throw new CompressException(e);
/*     */     } 
/*     */ 
/*     */     
/*  89 */     if (this.out instanceof TarArchiveOutputStream) {
/*  90 */       ((TarArchiveOutputStream)this.out).setLongFileMode(2);
/*  91 */     } else if (this.out instanceof ArArchiveOutputStream) {
/*  92 */       ((ArArchiveOutputStream)this.out).setLongFileMode(1);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public StreamArchiver add(File file, String path, Filter<File> filter) throws IORuntimeException {
/*     */     try {
/* 108 */       addInternal(file, path, filter);
/* 109 */     } catch (IOException e) {
/* 110 */       throw new IORuntimeException(e);
/*     */     } 
/* 112 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StreamArchiver finish() {
/*     */     try {
/* 123 */       this.out.finish();
/* 124 */     } catch (IOException e) {
/* 125 */       throw new IORuntimeException(e);
/*     */     } 
/* 127 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/*     */     try {
/* 133 */       finish();
/* 134 */     } catch (Exception exception) {}
/*     */ 
/*     */     
/* 137 */     IoUtil.close((Closeable)this.out);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addInternal(File file, String path, Filter<File> filter) throws IOException {
/*     */     String entryName;
/* 148 */     if (null != filter && false == filter.accept(file)) {
/*     */       return;
/*     */     }
/* 151 */     ArchiveOutputStream out = this.out;
/*     */ 
/*     */     
/* 154 */     if (StrUtil.isNotEmpty(path)) {
/*     */       
/* 156 */       entryName = StrUtil.addSuffixIfNot(path, "/") + file.getName();
/*     */     } else {
/*     */       
/* 159 */       entryName = file.getName();
/*     */     } 
/* 161 */     out.putArchiveEntry(out.createArchiveEntry(file, entryName));
/*     */     
/* 163 */     if (file.isDirectory()) {
/*     */       
/* 165 */       File[] files = file.listFiles();
/* 166 */       if (ArrayUtil.isNotEmpty((Object[])files)) {
/* 167 */         for (File childFile : files) {
/* 168 */           addInternal(childFile, entryName, filter);
/*     */         }
/*     */       }
/*     */     } else {
/* 172 */       if (file.isFile())
/*     */       {
/* 174 */         FileUtil.writeToStream(file, (OutputStream)out);
/*     */       }
/* 176 */       out.closeArchiveEntry();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\compress\archiver\StreamArchiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */