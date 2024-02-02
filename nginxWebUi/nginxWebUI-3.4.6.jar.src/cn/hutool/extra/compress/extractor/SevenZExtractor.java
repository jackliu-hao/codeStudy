/*     */ package cn.hutool.extra.compress.extractor;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.util.RandomAccess;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.sevenz.SevenZFile;
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
/*     */ 
/*     */ public class SevenZExtractor
/*     */   implements Extractor, RandomAccess
/*     */ {
/*     */   private final SevenZFile sevenZFile;
/*     */   
/*     */   public SevenZExtractor(File file) {
/*  36 */     this(file, (char[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SevenZExtractor(File file, char[] password) {
/*     */     try {
/*  47 */       this.sevenZFile = new SevenZFile(file, password);
/*  48 */     } catch (IOException e) {
/*  49 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SevenZExtractor(InputStream in) {
/*  59 */     this(in, (char[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SevenZExtractor(InputStream in, char[] password) {
/*  69 */     this((SeekableByteChannel)new SeekableInMemoryByteChannel(IoUtil.readBytes(in)), password);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SevenZExtractor(SeekableByteChannel channel) {
/*  78 */     this(channel, (char[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SevenZExtractor(SeekableByteChannel channel, char[] password) {
/*     */     try {
/*  89 */       this.sevenZFile = new SevenZFile(channel, password);
/*  90 */     } catch (IOException e) {
/*  91 */       throw new IORuntimeException(e);
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
/* 104 */       extractInternal(targetDir, filter);
/* 105 */     } catch (IOException e) {
/* 106 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 108 */       close();
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
/*     */   public InputStream getFirst(Filter<ArchiveEntry> filter) {
/* 120 */     SevenZFile sevenZFile = this.sevenZFile;
/* 121 */     for (SevenZArchiveEntry entry : sevenZFile.getEntries()) {
/* 122 */       if (null != filter && false == filter.accept(entry)) {
/*     */         continue;
/*     */       }
/* 125 */       if (entry.isDirectory()) {
/*     */         continue;
/*     */       }
/*     */       
/*     */       try {
/* 130 */         return sevenZFile.getInputStream(entry);
/* 131 */       } catch (IOException e) {
/* 132 */         throw new IORuntimeException(e);
/*     */       } 
/*     */     } 
/*     */     
/* 136 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream get(String entryName) {
/* 147 */     return getFirst(entry -> StrUtil.equals(entryName, entry.getName()));
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
/* 158 */     Assert.isTrue((null != targetDir && (false == targetDir.exists() || targetDir.isDirectory())), "target must be dir.", new Object[0]);
/* 159 */     SevenZFile sevenZFile = this.sevenZFile;
/*     */     
/*     */     SevenZArchiveEntry entry;
/* 162 */     while (null != (entry = this.sevenZFile.getNextEntry())) {
/* 163 */       if (null != filter && false == filter.accept(entry)) {
/*     */         continue;
/*     */       }
/* 166 */       File outItemFile = FileUtil.file(targetDir, entry.getName());
/* 167 */       if (entry.isDirectory()) {
/*     */ 
/*     */         
/* 170 */         outItemFile.mkdirs(); continue;
/* 171 */       }  if (entry.hasStream()) {
/*     */         
/* 173 */         FileUtil.writeFromStream(new Seven7EntryInputStream(sevenZFile, entry), outItemFile);
/*     */         continue;
/*     */       } 
/* 176 */       FileUtil.touch(outItemFile);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 183 */     IoUtil.close((Closeable)this.sevenZFile);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\compress\extractor\SevenZExtractor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */