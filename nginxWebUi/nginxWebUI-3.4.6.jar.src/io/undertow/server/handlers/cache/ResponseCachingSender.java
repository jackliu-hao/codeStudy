/*     */ package io.undertow.server.handlers.cache;
/*     */ 
/*     */ import io.undertow.io.IoCallback;
/*     */ import io.undertow.io.Sender;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import org.xnio.Buffers;
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
/*     */ public class ResponseCachingSender
/*     */   implements Sender
/*     */ {
/*     */   private final Sender delegate;
/*     */   private final DirectBufferCache.CacheEntry cacheEntry;
/*     */   private final long length;
/*     */   private long written;
/*     */   
/*     */   public ResponseCachingSender(Sender delegate, DirectBufferCache.CacheEntry cacheEntry, long length) {
/*  41 */     this.delegate = delegate;
/*  42 */     this.cacheEntry = cacheEntry;
/*  43 */     this.length = length;
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(ByteBuffer src, IoCallback callback) {
/*  48 */     ByteBuffer origSrc = src.duplicate();
/*  49 */     handleUpdate(origSrc);
/*  50 */     this.delegate.send(src, callback);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void send(ByteBuffer[] srcs, IoCallback callback) {
/*  56 */     ByteBuffer[] origSrc = new ByteBuffer[srcs.length];
/*  57 */     long total = 0L;
/*  58 */     for (int i = 0; i < srcs.length; i++) {
/*  59 */       origSrc[i] = srcs[i].duplicate();
/*  60 */       total += origSrc[i].remaining();
/*     */     } 
/*  62 */     handleUpdate(origSrc, total);
/*  63 */     this.delegate.send(srcs, callback);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(ByteBuffer src) {
/*  68 */     ByteBuffer origSrc = src.duplicate();
/*  69 */     handleUpdate(origSrc);
/*  70 */     this.delegate.send(src);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(ByteBuffer[] srcs) {
/*  75 */     ByteBuffer[] origSrc = new ByteBuffer[srcs.length];
/*  76 */     long total = 0L;
/*  77 */     for (int i = 0; i < srcs.length; i++) {
/*  78 */       origSrc[i] = srcs[i].duplicate();
/*  79 */       total += origSrc[i].remaining();
/*     */     } 
/*  81 */     handleUpdate(origSrc, total);
/*  82 */     this.delegate.send(srcs);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(String data, IoCallback callback) {
/*  87 */     handleUpdate(ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8)));
/*  88 */     this.delegate.send(data, callback);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(String data, Charset charset, IoCallback callback) {
/*  93 */     handleUpdate(ByteBuffer.wrap(data.getBytes(charset)));
/*  94 */     this.delegate.send(data, charset, callback);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(String data) {
/*  99 */     handleUpdate(ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8)));
/* 100 */     this.delegate.send(data);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(String data, Charset charset) {
/* 105 */     handleUpdate(ByteBuffer.wrap(data.getBytes(charset)));
/* 106 */     this.delegate.send(data, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void transferFrom(FileChannel channel, IoCallback callback) {
/* 112 */     this.delegate.transferFrom(channel, callback);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close(IoCallback callback) {
/* 117 */     if (this.written != this.length) {
/* 118 */       this.cacheEntry.disable();
/* 119 */       this.cacheEntry.dereference();
/*     */     } 
/* 121 */     this.delegate.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 126 */     if (this.written != this.length) {
/* 127 */       this.cacheEntry.disable();
/* 128 */       this.cacheEntry.dereference();
/*     */     } 
/* 130 */     this.delegate.close();
/*     */   }
/*     */   
/*     */   private void handleUpdate(ByteBuffer origSrc) {
/* 134 */     LimitedBufferSlicePool.PooledByteBuffer[] pooled = this.cacheEntry.buffers();
/* 135 */     ByteBuffer[] buffers = new ByteBuffer[pooled.length];
/* 136 */     for (int i = 0; i < buffers.length; i++) {
/* 137 */       buffers[i] = pooled[i].getBuffer();
/*     */     }
/* 139 */     this.written += Buffers.copy(buffers, 0, buffers.length, origSrc);
/* 140 */     if (this.written == this.length) {
/* 141 */       for (ByteBuffer buffer : buffers)
/*     */       {
/* 143 */         buffer.flip();
/*     */       }
/* 145 */       this.cacheEntry.enable();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void handleUpdate(ByteBuffer[] origSrc, long totalWritten) {
/* 150 */     LimitedBufferSlicePool.PooledByteBuffer[] pooled = this.cacheEntry.buffers();
/* 151 */     ByteBuffer[] buffers = new ByteBuffer[pooled.length];
/* 152 */     for (int i = 0; i < buffers.length; i++) {
/* 153 */       buffers[i] = pooled[i].getBuffer();
/*     */     }
/* 155 */     long leftToCopy = totalWritten;
/* 156 */     for (int j = 0; j < origSrc.length; j++) {
/* 157 */       ByteBuffer buf = origSrc[j];
/* 158 */       if (buf.remaining() > leftToCopy) {
/* 159 */         buf.limit((int)(buf.position() + leftToCopy));
/*     */       }
/* 161 */       leftToCopy -= buf.remaining();
/* 162 */       Buffers.copy(buffers, 0, buffers.length, buf);
/* 163 */       if (leftToCopy == 0L) {
/*     */         break;
/*     */       }
/*     */     } 
/* 167 */     this.written += totalWritten;
/* 168 */     if (this.written == this.length) {
/* 169 */       for (ByteBuffer buffer : buffers)
/*     */       {
/* 171 */         buffer.flip();
/*     */       }
/* 173 */       this.cacheEntry.enable();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\cache\ResponseCachingSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */