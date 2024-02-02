/*     */ package org.h2.store.fs.niomem;
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
/*     */ class FileNioMem
/*     */   extends FileBaseDefault
/*     */ {
/*     */   final FileNioMemData data;
/*     */   private final boolean readOnly;
/*     */   private volatile boolean closed;
/*     */   
/*     */   FileNioMem(FileNioMemData paramFileNioMemData, boolean paramBoolean) {
/*  30 */     this.data = paramFileNioMemData;
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
/*  59 */     this.data.touch(this.readOnly);
/*     */     
/*  61 */     long l = this.data.readWrite(paramLong, paramByteBuffer, 0, paramByteBuffer.remaining(), true);
/*  62 */     int i = (int)(l - paramLong);
/*  63 */     paramByteBuffer.position(paramByteBuffer.position() + i);
/*  64 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/*  69 */     if (this.closed) {
/*  70 */       throw new ClosedChannelException();
/*     */     }
/*  72 */     int i = paramByteBuffer.remaining();
/*  73 */     if (i == 0) {
/*  74 */       return 0;
/*     */     }
/*     */     
/*  77 */     long l = this.data.readWrite(paramLong, paramByteBuffer, paramByteBuffer.position(), i, false);
/*  78 */     i = (int)(l - paramLong);
/*  79 */     if (i <= 0) {
/*  80 */       return -1;
/*     */     }
/*  82 */     paramByteBuffer.position(paramByteBuffer.position() + i);
/*  83 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public void implCloseChannel() throws IOException {
/*  88 */     this.closed = true;
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
/*  99 */     if (this.closed) {
/* 100 */       throw new ClosedChannelException();
/*     */     }
/* 102 */     if (paramBoolean) {
/* 103 */       if (!this.data.lockShared()) {
/* 104 */         return null;
/*     */       }
/*     */     }
/* 107 */     else if (!this.data.lockExclusive()) {
/* 108 */       return null;
/*     */     } 
/*     */ 
/*     */     
/* 112 */     return new FileLock((FileChannel)FakeFileChannel.INSTANCE, paramLong1, paramLong2, paramBoolean)
/*     */       {
/*     */         public boolean isValid()
/*     */         {
/* 116 */           return true;
/*     */         }
/*     */ 
/*     */         
/*     */         public void release() throws IOException {
/* 121 */           FileNioMem.this.data.unlock();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 128 */     return this.closed ? "<closed>" : this.data.getName();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\niomem\FileNioMem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */