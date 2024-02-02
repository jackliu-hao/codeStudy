/*     */ package org.xnio.ssl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio._private.Messages;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.AbstractStreamSinkConduit;
/*     */ import org.xnio.conduits.ConduitWritableByteChannel;
/*     */ import org.xnio.conduits.Conduits;
/*     */ import org.xnio.conduits.StreamSinkConduit;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class JsseSslStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*     */   private final JsseSslConduitEngine sslEngine;
/*     */   private volatile boolean tls;
/*     */   
/*     */   protected JsseSslStreamSinkConduit(StreamSinkConduit next, JsseSslConduitEngine sslEngine, boolean tls) {
/*  45 */     super(next);
/*  46 */     if (sslEngine == null) {
/*  47 */       throw Messages.msg.nullParameter("sslEngine");
/*     */     }
/*  49 */     this.sslEngine = sslEngine;
/*  50 */     this.tls = tls;
/*     */   }
/*     */   
/*     */   public void enableTls() {
/*  54 */     this.tls = true;
/*  55 */     if (isWriteResumed()) {
/*  56 */       wakeupWrites();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/*  62 */     return src.transferTo(position, count, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/*  67 */     return Conduits.transfer((ReadableByteChannel)source, count, throughBuffer, (StreamSinkConduit)this);
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*  72 */     return write(src, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
/*  77 */     return write(srcs, offs, len, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/*  82 */     return write(src, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/*  87 */     return write(srcs, offset, length, true);
/*     */   }
/*     */   
/*     */   private int write(ByteBuffer src, boolean writeFinal) throws IOException {
/*  91 */     if (!this.tls) {
/*  92 */       if (writeFinal) {
/*  93 */         return ((StreamSinkConduit)this.next).writeFinal(src);
/*     */       }
/*  95 */       return ((StreamSinkConduit)this.next).write(src);
/*     */     } 
/*     */     
/*  98 */     int wrappedBytes = this.sslEngine.wrap(src);
/*  99 */     if (wrappedBytes > 0) {
/* 100 */       writeWrappedBuffer(writeFinal);
/*     */     }
/* 102 */     return wrappedBytes;
/*     */   }
/*     */   
/*     */   private long write(ByteBuffer[] srcs, int offs, int len, boolean writeFinal) throws IOException {
/* 106 */     if (!this.tls) {
/* 107 */       if (writeFinal) {
/* 108 */         return super.writeFinal(srcs, offs, len);
/*     */       }
/* 110 */       return super.write(srcs, offs, len);
/*     */     } 
/*     */     
/* 113 */     long wrappedBytes = this.sslEngine.wrap(srcs, offs, len);
/* 114 */     if (wrappedBytes > 0L) {
/* 115 */       writeWrappedBuffer(writeFinal);
/*     */     }
/* 117 */     return wrappedBytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public void resumeWrites() {
/* 122 */     if (this.tls && this.sslEngine.isFirstHandshake()) {
/* 123 */       wakeupWrites();
/*     */     } else {
/* 125 */       super.resumeWrites();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 131 */     if (!this.tls) {
/* 132 */       super.terminateWrites();
/*     */       return;
/*     */     } 
/*     */     try {
/* 136 */       this.sslEngine.closeOutbound();
/* 137 */       flush();
/* 138 */     } catch (IOException e) {
/*     */       try {
/* 140 */         super.truncateWrites();
/* 141 */       } catch (IOException e2) {
/* 142 */         e2.addSuppressed(e);
/* 143 */         throw e2;
/*     */       } 
/* 145 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 151 */     if (this.tls) {
/* 152 */       this.sslEngine.awaitCanWrap();
/*     */     }
/* 154 */     super.awaitWritable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 159 */     if (!this.tls) {
/* 160 */       super.awaitWritable(time, timeUnit);
/*     */       return;
/*     */     } 
/* 163 */     long duration = timeUnit.toNanos(time);
/* 164 */     long awaited = System.nanoTime();
/* 165 */     this.sslEngine.awaitCanWrap(time, timeUnit);
/* 166 */     awaited = System.nanoTime() - awaited;
/* 167 */     if (awaited > duration) {
/*     */       return;
/*     */     }
/* 170 */     super.awaitWritable(duration - awaited, TimeUnit.NANOSECONDS);
/*     */   }
/*     */ 
/*     */   
/*     */   public void truncateWrites() throws IOException {
/* 175 */     if (this.tls) {
/* 176 */       try { this.sslEngine.closeOutbound(); }
/*     */       finally
/*     */       { try {
/* 179 */           super.truncateWrites();
/* 180 */         } catch (IOException iOException) {} }
/*     */     
/*     */     }
/* 183 */     super.truncateWrites();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean flush() throws IOException {
/* 188 */     if (!this.tls) {
/* 189 */       return super.flush();
/*     */     }
/* 191 */     if (this.sslEngine.isOutboundClosed()) {
/* 192 */       if (this.sslEngine.flush() && writeWrappedBuffer(false) && super.flush()) {
/* 193 */         super.terminateWrites();
/* 194 */         return true;
/*     */       } 
/* 196 */       return false;
/*     */     } 
/*     */     
/* 199 */     return (this.sslEngine.flush() && writeWrappedBuffer(false) && super.flush());
/*     */   }
/*     */   
/*     */   private boolean writeWrappedBuffer(boolean writeFinal) throws IOException {
/* 203 */     synchronized (this.sslEngine.getWrapLock()) {
/* 204 */       ByteBuffer wrapBuffer = this.sslEngine.getWrappedBuffer();
/*     */       
/*     */       try { while (true)
/* 207 */         { if (!wrapBuffer.flip().hasRemaining()) {
/* 208 */             if (writeFinal) {
/* 209 */               terminateWrites();
/*     */             }
/* 211 */             return true;
/*     */           } 
/* 213 */           if (writeFinal) {
/* 214 */             if (super.writeFinal(wrapBuffer) == 0) {
/* 215 */               return false;
/*     */             }
/*     */           }
/* 218 */           else if (super.write(wrapBuffer) == 0) {
/* 219 */             return false;
/*     */           } 
/*     */ 
/*     */           
/* 223 */           wrapBuffer.compact(); }  } finally { wrapBuffer.compact(); }
/*     */     
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ssl\JsseSslStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */