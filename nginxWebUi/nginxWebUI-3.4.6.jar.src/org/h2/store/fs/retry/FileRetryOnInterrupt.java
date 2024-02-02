/*     */ package org.h2.store.fs.retry;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import org.h2.store.fs.FileBase;
/*     */ import org.h2.store.fs.FileUtils;
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
/*     */ class FileRetryOnInterrupt
/*     */   extends FileBase
/*     */ {
/*     */   private final String fileName;
/*     */   private final String mode;
/*     */   private FileChannel channel;
/*     */   private FileLockRetry lock;
/*     */   
/*     */   FileRetryOnInterrupt(String paramString1, String paramString2) throws IOException {
/*  29 */     this.fileName = paramString1;
/*  30 */     this.mode = paramString2;
/*  31 */     open();
/*     */   }
/*     */   
/*     */   private void open() throws IOException {
/*  35 */     this.channel = FileUtils.open(this.fileName, this.mode);
/*     */   }
/*     */   
/*     */   private void reopen(int paramInt, IOException paramIOException) throws IOException {
/*  39 */     if (paramInt > 20) {
/*  40 */       throw paramIOException;
/*     */     }
/*  42 */     if (!(paramIOException instanceof java.nio.channels.ClosedByInterruptException) && !(paramIOException instanceof java.nio.channels.ClosedChannelException))
/*     */     {
/*  44 */       throw paramIOException;
/*     */     }
/*     */     
/*  47 */     Thread.interrupted();
/*  48 */     FileChannel fileChannel = this.channel;
/*     */ 
/*     */ 
/*     */     
/*  52 */     synchronized (this) {
/*  53 */       if (fileChannel == this.channel) {
/*  54 */         open();
/*  55 */         reLock();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void reLock() throws IOException {
/*  61 */     if (this.lock == null) {
/*     */       return;
/*     */     }
/*     */     try {
/*  65 */       this.lock.base.release();
/*  66 */     } catch (IOException iOException) {}
/*     */ 
/*     */     
/*  69 */     FileLock fileLock = this.channel.tryLock(this.lock.position(), this.lock.size(), this.lock.isShared());
/*  70 */     if (fileLock == null) {
/*  71 */       throw new IOException("Re-locking failed");
/*     */     }
/*  73 */     this.lock.base = fileLock;
/*     */   }
/*     */ 
/*     */   
/*     */   public void implCloseChannel() throws IOException {
/*     */     try {
/*  79 */       this.channel.close();
/*  80 */     } catch (IOException iOException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long position() throws IOException {
/*  87 */     for (byte b = 0;; b++) {
/*     */       try {
/*  89 */         return this.channel.position();
/*  90 */       } catch (IOException iOException) {
/*  91 */         reopen(b, iOException);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long size() throws IOException {
/*  98 */     for (byte b = 0;; b++) {
/*     */       try {
/* 100 */         return this.channel.size();
/* 101 */       } catch (IOException iOException) {
/* 102 */         reopen(b, iOException);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer paramByteBuffer) throws IOException {
/* 109 */     long l = position();
/* 110 */     for (byte b = 0;; b++) {
/*     */       try {
/* 112 */         return this.channel.read(paramByteBuffer);
/* 113 */       } catch (IOException iOException) {
/* 114 */         reopen(b, iOException);
/* 115 */         position(l);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/* 122 */     for (byte b = 0;; b++) {
/*     */       try {
/* 124 */         return this.channel.read(paramByteBuffer, paramLong);
/* 125 */       } catch (IOException iOException) {
/* 126 */         reopen(b, iOException);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public FileChannel position(long paramLong) throws IOException {
/* 133 */     for (byte b = 0;; b++) {
/*     */       try {
/* 135 */         this.channel.position(paramLong);
/* 136 */         return (FileChannel)this;
/* 137 */       } catch (IOException iOException) {
/* 138 */         reopen(b, iOException);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public FileChannel truncate(long paramLong) throws IOException {
/* 145 */     for (byte b = 0;; b++) {
/*     */       try {
/* 147 */         this.channel.truncate(paramLong);
/* 148 */         return (FileChannel)this;
/* 149 */       } catch (IOException iOException) {
/* 150 */         reopen(b, iOException);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void force(boolean paramBoolean) throws IOException {
/* 157 */     for (byte b = 0;; b++) {
/*     */       try {
/* 159 */         this.channel.force(paramBoolean);
/*     */         return;
/* 161 */       } catch (IOException iOException) {
/* 162 */         reopen(b, iOException);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer paramByteBuffer) throws IOException {
/* 169 */     long l = position();
/* 170 */     for (byte b = 0;; b++) {
/*     */       try {
/* 172 */         return this.channel.write(paramByteBuffer);
/* 173 */       } catch (IOException iOException) {
/* 174 */         reopen(b, iOException);
/* 175 */         position(l);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/* 182 */     for (byte b = 0;; b++) {
/*     */       try {
/* 184 */         return this.channel.write(paramByteBuffer, paramLong);
/* 185 */       } catch (IOException iOException) {
/* 186 */         reopen(b, iOException);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
/* 194 */     FileLock fileLock = this.channel.tryLock(paramLong1, paramLong2, paramBoolean);
/* 195 */     if (fileLock == null) {
/* 196 */       return null;
/*     */     }
/* 198 */     this.lock = new FileLockRetry(fileLock, (FileChannel)this);
/* 199 */     return this.lock;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class FileLockRetry
/*     */     extends FileLock
/*     */   {
/*     */     FileLock base;
/*     */ 
/*     */ 
/*     */     
/*     */     protected FileLockRetry(FileLock param1FileLock, FileChannel param1FileChannel) {
/* 213 */       super(param1FileChannel, param1FileLock.position(), param1FileLock.size(), param1FileLock.isShared());
/* 214 */       this.base = param1FileLock;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isValid() {
/* 219 */       return this.base.isValid();
/*     */     }
/*     */ 
/*     */     
/*     */     public void release() throws IOException {
/* 224 */       this.base.release();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 231 */     return "retry:" + this.fileName;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\retry\FileRetryOnInterrupt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */