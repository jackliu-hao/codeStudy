/*     */ package org.h2.mvstore;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.nio.channels.OverlappingFileLockException;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.h2.mvstore.cache.FilePathCache;
/*     */ import org.h2.store.fs.FilePath;
/*     */ import org.h2.store.fs.encrypt.FileEncrypt;
/*     */ import org.h2.store.fs.encrypt.FilePathEncrypt;
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
/*     */ public class FileStore
/*     */ {
/*  29 */   protected final AtomicLong readCount = new AtomicLong();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  34 */   protected final AtomicLong readBytes = new AtomicLong();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  39 */   protected final AtomicLong writeCount = new AtomicLong();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  44 */   protected final AtomicLong writeBytes = new AtomicLong();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  50 */   protected final FreeSpaceBitSet freeSpace = new FreeSpaceBitSet(2, 4096);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String fileName;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean readOnly;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long fileSize;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private FileChannel file;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private FileChannel encryptedFile;
/*     */ 
/*     */ 
/*     */   
/*     */   private FileLock fileLock;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  85 */     return this.fileName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuffer readFully(long paramLong, int paramInt) {
/*  96 */     ByteBuffer byteBuffer = ByteBuffer.allocate(paramInt);
/*  97 */     DataUtils.readFully(this.file, paramLong, byteBuffer);
/*  98 */     this.readCount.incrementAndGet();
/*  99 */     this.readBytes.addAndGet(paramInt);
/* 100 */     return byteBuffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeFully(long paramLong, ByteBuffer paramByteBuffer) {
/* 110 */     int i = paramByteBuffer.remaining();
/* 111 */     this.fileSize = Math.max(this.fileSize, paramLong + i);
/* 112 */     DataUtils.writeFully(this.file, paramLong, paramByteBuffer);
/* 113 */     this.writeCount.incrementAndGet();
/* 114 */     this.writeBytes.addAndGet(i);
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
/*     */   public void open(String paramString, boolean paramBoolean, char[] paramArrayOfchar) {
/* 127 */     if (this.file != null) {
/*     */       return;
/*     */     }
/*     */     
/* 131 */     FilePathCache.INSTANCE.getScheme();
/* 132 */     this.fileName = paramString;
/* 133 */     FilePath filePath1 = FilePath.get(paramString);
/* 134 */     FilePath filePath2 = filePath1.getParent();
/* 135 */     if (filePath2 != null && !filePath2.exists()) {
/* 136 */       throw DataUtils.newIllegalArgumentException("Directory does not exist: {0}", new Object[] { filePath2 });
/*     */     }
/*     */     
/* 139 */     if (filePath1.exists() && !filePath1.canWrite()) {
/* 140 */       paramBoolean = true;
/*     */     }
/* 142 */     this.readOnly = paramBoolean;
/*     */     
/* 144 */     try { this.file = filePath1.open(paramBoolean ? "r" : "rw");
/* 145 */       if (paramArrayOfchar != null) {
/* 146 */         byte[] arrayOfByte = FilePathEncrypt.getPasswordBytes(paramArrayOfchar);
/* 147 */         this.encryptedFile = this.file;
/* 148 */         this.file = (FileChannel)new FileEncrypt(paramString, arrayOfByte, this.file);
/*     */       } 
/*     */       try {
/* 151 */         if (paramBoolean) {
/* 152 */           this.fileLock = this.file.tryLock(0L, Long.MAX_VALUE, true);
/*     */         } else {
/* 154 */           this.fileLock = this.file.tryLock();
/*     */         } 
/* 156 */       } catch (OverlappingFileLockException overlappingFileLockException) {
/* 157 */         throw DataUtils.newMVStoreException(7, "The file is locked: {0}", new Object[] { paramString, overlappingFileLockException });
/*     */       } 
/*     */ 
/*     */       
/* 161 */       if (this.fileLock == null) { 
/* 162 */         try { close(); } catch (Exception exception) {}
/* 163 */         throw DataUtils.newMVStoreException(7, "The file is locked: {0}", new Object[] { paramString }); }
/*     */ 
/*     */ 
/*     */       
/* 167 */       this.fileSize = this.file.size(); }
/* 168 */     catch (IOException iOException) { 
/* 169 */       try { close(); } catch (Exception exception) {}
/* 170 */       throw DataUtils.newMVStoreException(1, "Could not open file {0}", new Object[] { paramString, iOException }); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/*     */     try {
/* 181 */       if (this.file != null && this.file.isOpen()) {
/* 182 */         if (this.fileLock != null) {
/* 183 */           this.fileLock.release();
/*     */         }
/* 185 */         this.file.close();
/*     */       } 
/* 187 */     } catch (Exception exception) {
/* 188 */       throw DataUtils.newMVStoreException(2, "Closing failed for file {0}", new Object[] { this.fileName, exception });
/*     */     }
/*     */     finally {
/*     */       
/* 192 */       this.fileLock = null;
/* 193 */       this.file = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sync() {
/* 201 */     if (this.file != null) {
/*     */       try {
/* 203 */         this.file.force(true);
/* 204 */       } catch (IOException iOException) {
/* 205 */         throw DataUtils.newMVStoreException(2, "Could not sync file {0}", new Object[] { this.fileName, iOException });
/*     */       } 
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
/*     */   public long size() {
/* 218 */     return this.fileSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void truncate(long paramLong) {
/* 227 */     byte b = 0;
/*     */     while (true) {
/*     */       try {
/* 230 */         this.writeCount.incrementAndGet();
/* 231 */         this.file.truncate(paramLong);
/* 232 */         this.fileSize = Math.min(this.fileSize, paramLong);
/*     */         return;
/* 234 */       } catch (IOException iOException) {
/* 235 */         if (++b == 10) {
/* 236 */           throw DataUtils.newMVStoreException(2, "Could not truncate file {0} to size {1}", new Object[] { this.fileName, 
/*     */ 
/*     */                 
/* 239 */                 Long.valueOf(paramLong), iOException });
/*     */         }
/* 241 */         System.gc();
/* 242 */         Thread.yield();
/*     */       } 
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
/*     */   public FileChannel getFile() {
/* 256 */     return this.file;
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
/*     */   public FileChannel getEncryptedFile() {
/* 268 */     return this.encryptedFile;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getWriteCount() {
/* 278 */     return this.writeCount.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getWriteBytes() {
/* 287 */     return this.writeBytes.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getReadCount() {
/* 297 */     return this.readCount.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getReadBytes() {
/* 306 */     return this.readBytes.get();
/*     */   }
/*     */   
/*     */   public boolean isReadOnly() {
/* 310 */     return this.readOnly;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDefaultRetentionTime() {
/* 319 */     return 45000;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void markUsed(long paramLong, int paramInt) {
/* 329 */     this.freeSpace.markUsed(paramLong, paramInt);
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
/*     */   long allocate(int paramInt, long paramLong1, long paramLong2) {
/* 342 */     return this.freeSpace.allocate(paramInt, paramLong1, paramLong2);
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
/*     */   long predictAllocation(int paramInt, long paramLong1, long paramLong2) {
/* 355 */     return this.freeSpace.predictAllocation(paramInt, paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   boolean isFragmented() {
/* 359 */     return this.freeSpace.isFragmented();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void free(long paramLong, int paramInt) {
/* 369 */     this.freeSpace.free(paramLong, paramInt);
/*     */   }
/*     */   
/*     */   public int getFillRate() {
/* 373 */     return this.freeSpace.getFillRate();
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
/*     */   public int getProjectedFillRate(int paramInt) {
/* 386 */     return this.freeSpace.getProjectedFillRate(paramInt);
/*     */   }
/*     */   
/*     */   long getFirstFree() {
/* 390 */     return this.freeSpace.getFirstFree();
/*     */   }
/*     */   
/*     */   long getFileLengthInUse() {
/* 394 */     return this.freeSpace.getLastFree();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getMovePriority(int paramInt) {
/* 404 */     return this.freeSpace.getMovePriority(paramInt);
/*     */   }
/*     */   
/*     */   long getAfterLastBlock() {
/* 408 */     return this.freeSpace.getAfterLastBlock();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 415 */     this.freeSpace.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFileName() {
/* 424 */     return this.fileName;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\FileStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */