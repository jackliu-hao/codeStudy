/*     */ package cn.hutool.extra.compress.archiver;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
/*     */ import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
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
/*     */ public class SevenZArchiver
/*     */   implements Archiver
/*     */ {
/*     */   private final SevenZOutputFile sevenZOutputFile;
/*     */   private SeekableByteChannel channel;
/*     */   private OutputStream out;
/*     */   
/*     */   public SevenZArchiver(File file) {
/*     */     try {
/*  36 */       this.sevenZOutputFile = new SevenZOutputFile(file);
/*  37 */     } catch (IOException e) {
/*  38 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SevenZArchiver(OutputStream out) {
/*  48 */     this.out = out;
/*  49 */     this.channel = (SeekableByteChannel)new SeekableInMemoryByteChannel();
/*     */     try {
/*  51 */       this.sevenZOutputFile = new SevenZOutputFile(this.channel);
/*  52 */     } catch (IOException e) {
/*  53 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SevenZArchiver(SeekableByteChannel channel) {
/*     */     try {
/*  64 */       this.sevenZOutputFile = new SevenZOutputFile(channel);
/*  65 */     } catch (IOException e) {
/*  66 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SevenZOutputFile getSevenZOutputFile() {
/*  76 */     return this.sevenZOutputFile;
/*     */   }
/*     */ 
/*     */   
/*     */   public SevenZArchiver add(File file, String path, Filter<File> filter) {
/*     */     try {
/*  82 */       addInternal(file, path, filter);
/*  83 */     } catch (IOException e) {
/*  84 */       throw new IORuntimeException(e);
/*     */     } 
/*  86 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SevenZArchiver finish() {
/*     */     try {
/*  92 */       this.sevenZOutputFile.finish();
/*  93 */     } catch (IOException e) {
/*  94 */       throw new IORuntimeException(e);
/*     */     } 
/*  96 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/*     */     try {
/* 102 */       finish();
/* 103 */     } catch (Exception exception) {}
/*     */ 
/*     */     
/* 106 */     if (null != this.out && this.channel instanceof SeekableInMemoryByteChannel) {
/*     */       try {
/* 108 */         this.out.write(((SeekableInMemoryByteChannel)this.channel).array());
/* 109 */       } catch (IOException e) {
/* 110 */         throw new IORuntimeException(e);
/*     */       } 
/*     */     }
/* 113 */     IoUtil.close((Closeable)this.sevenZOutputFile);
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
/* 124 */     if (null != filter && false == filter.accept(file)) {
/*     */       return;
/*     */     }
/* 127 */     SevenZOutputFile out = this.sevenZOutputFile;
/*     */ 
/*     */     
/* 130 */     if (StrUtil.isNotEmpty(path)) {
/*     */       
/* 132 */       entryName = StrUtil.addSuffixIfNot(path, "/") + file.getName();
/*     */     } else {
/*     */       
/* 135 */       entryName = file.getName();
/*     */     } 
/* 137 */     out.putArchiveEntry((ArchiveEntry)out.createArchiveEntry(file, entryName));
/*     */     
/* 139 */     if (file.isDirectory()) {
/*     */       
/* 141 */       File[] files = file.listFiles();
/* 142 */       if (ArrayUtil.isNotEmpty((Object[])files)) {
/* 143 */         for (File childFile : files) {
/* 144 */           addInternal(childFile, entryName, filter);
/*     */         }
/*     */       }
/*     */     } else {
/* 148 */       if (file.isFile())
/*     */       {
/* 150 */         out.write(FileUtil.readBytes(file));
/*     */       }
/* 152 */       out.closeArchiveEntry();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\compress\archiver\SevenZArchiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */