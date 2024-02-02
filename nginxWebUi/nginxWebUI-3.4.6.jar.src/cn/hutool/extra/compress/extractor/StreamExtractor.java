/*     */ package cn.hutool.extra.compress.extractor;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.extra.compress.CompressException;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.ArchiveException;
/*     */ import org.apache.commons.compress.archivers.ArchiveInputStream;
/*     */ import org.apache.commons.compress.archivers.ArchiveStreamFactory;
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
/*     */ public class StreamExtractor
/*     */   implements Extractor
/*     */ {
/*     */   private final ArchiveInputStream in;
/*     */   
/*     */   public StreamExtractor(Charset charset, File file) {
/*  37 */     this(charset, (String)null, file);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StreamExtractor(Charset charset, String archiverName, File file) {
/*  48 */     this(charset, archiverName, FileUtil.getInputStream(file));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StreamExtractor(Charset charset, InputStream in) {
/*  58 */     this(charset, (String)null, in);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StreamExtractor(Charset charset, String archiverName, InputStream in) {
/*  69 */     ArchiveStreamFactory factory = new ArchiveStreamFactory(charset.name());
/*     */     try {
/*  71 */       in = IoUtil.toBuffered(in);
/*  72 */       if (StrUtil.isBlank(archiverName)) {
/*  73 */         this.in = factory.createArchiveInputStream(in);
/*     */       } else {
/*  75 */         this.in = factory.createArchiveInputStream(archiverName, in);
/*     */       } 
/*  77 */     } catch (ArchiveException e) {
/*  78 */       throw new CompressException(e);
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
/*     */   public void extract(File targetDir, Filter<ArchiveEntry> filter) {
/*     */     try {
/*  91 */       extractInternal(targetDir, filter);
/*  92 */     } catch (IOException e) {
/*  93 */       throw new IORuntimeException(e);
/*     */     } finally {
/*  95 */       close();
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
/*     */   private void extractInternal(File targetDir, Filter<ArchiveEntry> filter) throws IOException {
/* 107 */     Assert.isTrue((null != targetDir && (false == targetDir.exists() || targetDir.isDirectory())), "target must be dir.", new Object[0]);
/* 108 */     ArchiveInputStream in = this.in;
/*     */     
/*     */     ArchiveEntry entry;
/* 111 */     while (null != (entry = in.getNextEntry())) {
/* 112 */       if (null != filter && false == filter.accept(entry)) {
/*     */         continue;
/*     */       }
/* 115 */       if (false == in.canReadEntryData(entry)) {
/*     */         continue;
/*     */       }
/*     */       
/* 119 */       File outItemFile = FileUtil.file(targetDir, entry.getName());
/* 120 */       if (entry.isDirectory()) {
/*     */ 
/*     */         
/* 123 */         outItemFile.mkdirs(); continue;
/*     */       } 
/* 125 */       FileUtil.writeFromStream((InputStream)in, outItemFile, false);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 132 */     IoUtil.close((Closeable)this.in);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\compress\extractor\StreamExtractor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */