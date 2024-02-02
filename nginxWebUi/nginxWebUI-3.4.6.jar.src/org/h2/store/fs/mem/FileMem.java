/*     */ package org.h2.store.fs.mem;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.nio.channels.NonWritableChannelException;
/*     */ import org.h2.store.fs.FakeFileChannel;
/*     */ import org.h2.store.fs.FileBaseDefault;
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
/*     */ class FileMem
/*     */   extends FileBaseDefault
/*     */ {
/*     */   final FileMemData data;
/*     */   private final boolean readOnly;
/*     */   private volatile boolean closed;
/*     */   
/*     */   FileMem(FileMemData paramFileMemData, boolean paramBoolean) {
/*  30 */     this.data = paramFileMemData;
/*  31 */     this.readOnly = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public long size() {
/*  36 */     return this.data.length();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void implTruncate(long paramLong) throws IOException {
/*  42 */     if (this.readOnly) {
/*  43 */       throw new NonWritableChannelException();
/*     */     }
/*  45 */     if (this.closed) {
/*  46 */       throw new ClosedChannelException();
/*     */     }
/*  48 */     if (paramLong < size()) {
/*  49 */       this.data.touch(this.readOnly);
/*  50 */       this.data.truncate(paramLong);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/*  56 */     if (this.closed) {
/*  57 */       throw new ClosedChannelException();
/*     */     }
/*  59 */     if (this.readOnly) {
/*  60 */       throw new NonWritableChannelException();
/*     */     }
/*  62 */     int i = paramByteBuffer.remaining();
/*  63 */     if (i == 0) {
/*  64 */       return 0;
/*     */     }
/*  66 */     this.data.touch(this.readOnly);
/*  67 */     this.data.readWrite(paramLong, paramByteBuffer.array(), paramByteBuffer
/*  68 */         .arrayOffset() + paramByteBuffer.position(), i, true);
/*  69 */     paramByteBuffer.position(paramByteBuffer.position() + i);
/*  70 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/*  75 */     if (this.closed) {
/*  76 */       throw new ClosedChannelException();
/*     */     }
/*  78 */     int i = paramByteBuffer.remaining();
/*  79 */     if (i == 0) {
/*  80 */       return 0;
/*     */     }
/*  82 */     long l = this.data.readWrite(paramLong, paramByteBuffer.array(), paramByteBuffer
/*  83 */         .arrayOffset() + paramByteBuffer.position(), i, false);
/*  84 */     i = (int)(l - paramLong);
/*  85 */     if (i <= 0) {
/*  86 */       return -1;
/*     */     }
/*  88 */     paramByteBuffer.position(paramByteBuffer.position() + i);
/*  89 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public void implCloseChannel() throws IOException {
/*  94 */     this.closed = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void force(boolean paramBoolean) throws IOException {}
/*     */ 
/*     */ 
/*     */   
/*     */   public FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
/* 105 */     if (this.closed) {
/* 106 */       throw new ClosedChannelException();
/*     */     }
/* 108 */     if (paramBoolean) {
/* 109 */       if (!this.data.lockShared()) {
/* 110 */         return null;
/*     */       }
/*     */     }
/* 113 */     else if (!this.data.lockExclusive()) {
/* 114 */       return null;
/*     */     } 
/*     */ 
/*     */     
/* 118 */     return new FileLock((FileChannel)FakeFileChannel.INSTANCE, paramLong1, paramLong2, paramBoolean)
/*     */       {
/*     */         public boolean isValid()
/*     */         {
/* 122 */           return true;
/*     */         }
/*     */ 
/*     */         
/*     */         public void release() throws IOException {
/* 127 */           FileMem.this.data.unlock();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 134 */     return this.closed ? "<closed>" : this.data.getName();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\mem\FileMem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */