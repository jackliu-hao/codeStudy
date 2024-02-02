/*     */ package org.h2.store.fs.split;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileLock;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.DataUtils;
/*     */ import org.h2.store.fs.FileBaseDefault;
/*     */ import org.h2.store.fs.FilePath;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class FileSplit
/*     */   extends FileBaseDefault
/*     */ {
/*     */   private final FilePathSplit filePath;
/*     */   private final String mode;
/*     */   private final long maxLength;
/*     */   private FileChannel[] list;
/*     */   private volatile long length;
/*     */   
/*     */   FileSplit(FilePathSplit paramFilePathSplit, String paramString, FileChannel[] paramArrayOfFileChannel, long paramLong1, long paramLong2) {
/*  30 */     this.filePath = paramFilePathSplit;
/*  31 */     this.mode = paramString;
/*  32 */     this.list = paramArrayOfFileChannel;
/*  33 */     this.length = paramLong1;
/*  34 */     this.maxLength = paramLong2;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void implCloseChannel() throws IOException {
/*  39 */     for (FileChannel fileChannel : this.list) {
/*  40 */       fileChannel.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public long size() {
/*  46 */     return this.length;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized int read(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/*  52 */     int i = paramByteBuffer.remaining();
/*  53 */     if (i == 0) {
/*  54 */       return 0;
/*     */     }
/*  56 */     i = (int)Math.min(i, this.length - paramLong);
/*  57 */     if (i <= 0) {
/*  58 */       return -1;
/*     */     }
/*  60 */     long l = paramLong % this.maxLength;
/*  61 */     i = (int)Math.min(i, this.maxLength - l);
/*  62 */     FileChannel fileChannel = getFileChannel(paramLong);
/*  63 */     return fileChannel.read(paramByteBuffer, l);
/*     */   }
/*     */   
/*     */   private FileChannel getFileChannel(long paramLong) throws IOException {
/*  67 */     int i = (int)(paramLong / this.maxLength);
/*  68 */     while (i >= this.list.length) {
/*  69 */       int j = this.list.length;
/*  70 */       FileChannel[] arrayOfFileChannel = new FileChannel[j + 1];
/*  71 */       System.arraycopy(this.list, 0, arrayOfFileChannel, 0, j);
/*  72 */       FilePath filePath = this.filePath.getBase(j);
/*  73 */       arrayOfFileChannel[j] = filePath.open(this.mode);
/*  74 */       this.list = arrayOfFileChannel;
/*     */     } 
/*  76 */     return this.list[i];
/*     */   }
/*     */ 
/*     */   
/*     */   protected void implTruncate(long paramLong) throws IOException {
/*  81 */     if (paramLong >= this.length) {
/*     */       return;
/*     */     }
/*  84 */     int i = 1 + (int)(paramLong / this.maxLength);
/*  85 */     if (i < this.list.length) {
/*     */       
/*  87 */       FileChannel[] arrayOfFileChannel = new FileChannel[i];
/*     */       
/*  89 */       for (int j = this.list.length - 1; j >= i; j--) {
/*     */         
/*  91 */         this.list[j].truncate(0L);
/*  92 */         this.list[j].close();
/*     */         try {
/*  94 */           this.filePath.getBase(j).delete();
/*  95 */         } catch (DbException dbException) {
/*  96 */           throw DataUtils.convertToIOException(dbException);
/*     */         } 
/*     */       } 
/*  99 */       System.arraycopy(this.list, 0, arrayOfFileChannel, 0, arrayOfFileChannel.length);
/* 100 */       this.list = arrayOfFileChannel;
/*     */     } 
/* 102 */     long l = paramLong - this.maxLength * (i - 1);
/* 103 */     this.list[this.list.length - 1].truncate(l);
/* 104 */     this.length = paramLong;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void force(boolean paramBoolean) throws IOException {
/* 109 */     for (FileChannel fileChannel : this.list) {
/* 110 */       fileChannel.force(paramBoolean);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized int write(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/* 116 */     if (paramLong >= this.length && paramLong > this.maxLength) {
/*     */       
/* 118 */       long l1 = paramLong;
/* 119 */       long l2 = this.length - this.length % this.maxLength + this.maxLength;
/* 120 */       for (; l2 < paramLong; l2 += this.maxLength) {
/* 121 */         if (l2 > this.length) {
/*     */           
/* 123 */           position(l2 - 1L);
/* 124 */           write(ByteBuffer.wrap(new byte[1]));
/*     */         } 
/* 126 */         paramLong = l1;
/*     */       } 
/*     */     } 
/* 129 */     long l = paramLong % this.maxLength;
/* 130 */     int i = paramByteBuffer.remaining();
/* 131 */     FileChannel fileChannel = getFileChannel(paramLong);
/* 132 */     int j = (int)Math.min(i, this.maxLength - l);
/* 133 */     if (j == i) {
/* 134 */       j = fileChannel.write(paramByteBuffer, l);
/*     */     } else {
/* 136 */       int k = paramByteBuffer.limit();
/* 137 */       paramByteBuffer.limit(paramByteBuffer.position() + j);
/* 138 */       j = fileChannel.write(paramByteBuffer, l);
/* 139 */       paramByteBuffer.limit(k);
/*     */     } 
/* 141 */     this.length = Math.max(this.length, paramLong + j);
/* 142 */     return j;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
/* 148 */     return this.list[0].tryLock(paramLong1, paramLong2, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 153 */     return this.filePath.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\split\FileSplit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */