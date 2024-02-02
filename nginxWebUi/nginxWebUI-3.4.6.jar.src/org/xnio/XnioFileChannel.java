/*     */ package org.xnio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.MappedByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
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
/*     */ final class XnioFileChannel
/*     */   extends FileChannel
/*     */ {
/*     */   private final FileChannel delegate;
/*     */   
/*     */   XnioFileChannel(FileChannel delegate) {
/*  36 */     this.delegate = delegate;
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/*  40 */     return this.delegate.read(dst);
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/*  44 */     return this.delegate.read(dsts, offset, length);
/*     */   }
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*  48 */     return this.delegate.write(src);
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/*  52 */     return this.delegate.write(srcs, offset, length);
/*     */   }
/*     */   
/*     */   public long position() throws IOException {
/*  56 */     return this.delegate.position();
/*     */   }
/*     */   
/*     */   public FileChannel position(long newPosition) throws IOException {
/*  60 */     return this.delegate.position(newPosition);
/*     */   }
/*     */   
/*     */   public long size() throws IOException {
/*  64 */     return this.delegate.size();
/*     */   }
/*     */   
/*     */   public FileChannel truncate(long size) throws IOException {
/*  68 */     return this.delegate.truncate(size);
/*     */   }
/*     */   
/*     */   public void force(boolean metaData) throws IOException {
/*  72 */     this.delegate.force(metaData);
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
/*  76 */     if (target instanceof StreamSinkChannel) {
/*  77 */       return ((StreamSinkChannel)target).transferFrom(this.delegate, position, count);
/*     */     }
/*  79 */     return this.delegate.transferTo(position, count, target);
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
/*  84 */     if (src instanceof StreamSourceChannel) {
/*  85 */       return ((StreamSourceChannel)src).transferTo(position, count, this.delegate);
/*     */     }
/*  87 */     return this.delegate.transferFrom(src, position, count);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer dst, long position) throws IOException {
/*  92 */     return this.delegate.read(dst, position);
/*     */   }
/*     */   
/*     */   public int write(ByteBuffer src, long position) throws IOException {
/*  96 */     return this.delegate.write(src, position);
/*     */   }
/*     */   
/*     */   public MappedByteBuffer map(FileChannel.MapMode mode, long position, long size) throws IOException {
/* 100 */     return this.delegate.map(mode, position, size);
/*     */   }
/*     */   
/*     */   public FileLock lock(long position, long size, boolean shared) throws IOException {
/* 104 */     return this.delegate.lock(position, size, shared);
/*     */   }
/*     */   
/*     */   public FileLock tryLock(long position, long size, boolean shared) throws IOException {
/* 108 */     return this.delegate.tryLock(position, size, shared);
/*     */   }
/*     */   
/*     */   public void implCloseChannel() throws IOException {
/* 112 */     this.delegate.close();
/*     */   }
/*     */   
/*     */   FileChannel getDelegate() {
/* 116 */     return this.delegate;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\XnioFileChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */