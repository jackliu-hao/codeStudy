/*     */ package cn.hutool.cache.file;
/*     */ 
/*     */ import cn.hutool.cache.Cache;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import java.io.File;
/*     */ import java.io.Serializable;
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
/*     */ public abstract class AbstractFileCache
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final int capacity;
/*     */   protected final int maxFileSize;
/*     */   protected final long timeout;
/*     */   protected final Cache<File, byte[]> cache;
/*     */   protected int usedSize;
/*     */   
/*     */   public AbstractFileCache(int capacity, int maxFileSize, long timeout) {
/*  37 */     this.capacity = capacity;
/*  38 */     this.maxFileSize = maxFileSize;
/*  39 */     this.timeout = timeout;
/*  40 */     this.cache = initCache();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int capacity() {
/*  47 */     return this.capacity;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getUsedSize() {
/*  54 */     return this.usedSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int maxFileSize() {
/*  61 */     return this.maxFileSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCachedFilesCount() {
/*  68 */     return this.cache.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long timeout() {
/*  75 */     return this.timeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/*  82 */     this.cache.clear();
/*  83 */     this.usedSize = 0;
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
/*     */   public byte[] getFileBytes(String path) throws IORuntimeException {
/*  95 */     return getFileBytes(new File(path));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getFileBytes(File file) throws IORuntimeException {
/* 105 */     byte[] bytes = (byte[])this.cache.get(file);
/* 106 */     if (bytes != null) {
/* 107 */       return bytes;
/*     */     }
/*     */ 
/*     */     
/* 111 */     bytes = FileUtil.readBytes(file);
/*     */     
/* 113 */     if (this.maxFileSize != 0 && file.length() > this.maxFileSize)
/*     */     {
/* 115 */       return bytes;
/*     */     }
/*     */     
/* 118 */     this.usedSize += bytes.length;
/*     */ 
/*     */     
/* 121 */     this.cache.put(file, bytes);
/*     */     
/* 123 */     return bytes;
/*     */   }
/*     */   
/*     */   protected abstract Cache<File, byte[]> initCache();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\file\AbstractFileCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */