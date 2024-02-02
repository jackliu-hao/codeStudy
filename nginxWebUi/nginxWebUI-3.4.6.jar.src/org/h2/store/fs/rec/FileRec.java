/*     */ package org.h2.store.fs.rec;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.util.Arrays;
/*     */ import org.h2.store.fs.FileBase;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class FileRec
/*     */   extends FileBase
/*     */ {
/*     */   private final FilePathRec rec;
/*     */   private final FileChannel channel;
/*     */   private final String name;
/*     */   
/*     */   FileRec(FilePathRec paramFilePathRec, FileChannel paramFileChannel, String paramString) {
/*  26 */     this.rec = paramFilePathRec;
/*  27 */     this.channel = paramFileChannel;
/*  28 */     this.name = paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   public void implCloseChannel() throws IOException {
/*  33 */     this.channel.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public long position() throws IOException {
/*  38 */     return this.channel.position();
/*     */   }
/*     */ 
/*     */   
/*     */   public long size() throws IOException {
/*  43 */     return this.channel.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer paramByteBuffer) throws IOException {
/*  48 */     return this.channel.read(paramByteBuffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/*  53 */     return this.channel.read(paramByteBuffer, paramLong);
/*     */   }
/*     */ 
/*     */   
/*     */   public FileChannel position(long paramLong) throws IOException {
/*  58 */     this.channel.position(paramLong);
/*  59 */     return (FileChannel)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FileChannel truncate(long paramLong) throws IOException {
/*  64 */     this.rec.log(7, this.name, null, paramLong);
/*  65 */     this.channel.truncate(paramLong);
/*  66 */     return (FileChannel)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void force(boolean paramBoolean) throws IOException {
/*  71 */     this.channel.force(paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer paramByteBuffer) throws IOException {
/*  76 */     byte[] arrayOfByte = paramByteBuffer.array();
/*  77 */     int i = paramByteBuffer.remaining();
/*  78 */     if (paramByteBuffer.position() != 0 || i != arrayOfByte.length) {
/*  79 */       int k = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
/*  80 */       arrayOfByte = Arrays.copyOfRange(arrayOfByte, k, k + i);
/*     */     } 
/*  82 */     int j = this.channel.write(paramByteBuffer);
/*  83 */     this.rec.log(8, this.name, arrayOfByte, this.channel.position());
/*  84 */     return j;
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/*  89 */     byte[] arrayOfByte = paramByteBuffer.array();
/*  90 */     int i = paramByteBuffer.remaining();
/*  91 */     if (paramByteBuffer.position() != 0 || i != arrayOfByte.length) {
/*  92 */       int k = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
/*  93 */       arrayOfByte = Arrays.copyOfRange(arrayOfByte, k, k + i);
/*     */     } 
/*  95 */     int j = this.channel.write(paramByteBuffer, paramLong);
/*  96 */     this.rec.log(8, this.name, arrayOfByte, paramLong);
/*  97 */     return j;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
/* 103 */     return this.channel.tryLock(paramLong1, paramLong2, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 108 */     return this.name;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\rec\FileRec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */