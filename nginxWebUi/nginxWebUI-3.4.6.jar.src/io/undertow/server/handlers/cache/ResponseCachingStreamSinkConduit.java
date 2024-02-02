/*     */ package io.undertow.server.handlers.cache;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.IoUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ResponseCachingStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*     */   private final DirectBufferCache.CacheEntry cacheEntry;
/*     */   private final long length;
/*     */   private long written;
/*     */   
/*     */   public ResponseCachingStreamSinkConduit(StreamSinkConduit next, DirectBufferCache.CacheEntry cacheEntry, long length) {
/*  50 */     super(next);
/*  51 */     this.cacheEntry = cacheEntry;
/*  52 */     this.length = length;
/*  53 */     for (LimitedBufferSlicePool.PooledByteBuffer buffer : cacheEntry.buffers()) {
/*  54 */       buffer.getBuffer().clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/*  60 */     return src.transferTo(position, count, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/*  65 */     return IoUtils.transfer((ReadableByteChannel)source, count, throughBuffer, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*  70 */     ByteBuffer origSrc = src.duplicate();
/*  71 */     int totalWritten = super.write(src);
/*  72 */     if (totalWritten > 0) {
/*  73 */       LimitedBufferSlicePool.PooledByteBuffer[] pooled = this.cacheEntry.buffers();
/*  74 */       ByteBuffer[] buffers = new ByteBuffer[pooled.length];
/*  75 */       for (int i = 0; i < buffers.length; i++) {
/*  76 */         buffers[i] = pooled[i].getBuffer();
/*     */       }
/*  78 */       origSrc.limit(origSrc.position() + totalWritten);
/*  79 */       this.written += Buffers.copy(buffers, 0, buffers.length, origSrc);
/*  80 */       if (this.written == this.length) {
/*  81 */         for (ByteBuffer buffer : buffers)
/*     */         {
/*  83 */           buffer.flip();
/*     */         }
/*  85 */         this.cacheEntry.enable();
/*     */       } 
/*     */     } 
/*  88 */     return totalWritten;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
/*  94 */     ByteBuffer[] origSrc = new ByteBuffer[srcs.length];
/*  95 */     for (int i = 0; i < srcs.length; i++) {
/*  96 */       origSrc[i] = srcs[i].duplicate();
/*     */     }
/*  98 */     long totalWritten = super.write(srcs, offs, len);
/*  99 */     if (totalWritten > 0L) {
/* 100 */       LimitedBufferSlicePool.PooledByteBuffer[] pooled = this.cacheEntry.buffers();
/* 101 */       ByteBuffer[] buffers = new ByteBuffer[pooled.length];
/* 102 */       for (int j = 0; j < buffers.length; j++) {
/* 103 */         buffers[j] = pooled[j].getBuffer();
/*     */       }
/* 105 */       long leftToCopy = totalWritten;
/* 106 */       for (int k = 0; k < len; k++) {
/* 107 */         ByteBuffer buf = origSrc[offs + k];
/* 108 */         if (buf.remaining() > leftToCopy) {
/* 109 */           buf.limit((int)(buf.position() + leftToCopy));
/*     */         }
/* 111 */         leftToCopy -= buf.remaining();
/* 112 */         Buffers.copy(buffers, 0, buffers.length, buf);
/* 113 */         if (leftToCopy == 0L) {
/*     */           break;
/*     */         }
/*     */       } 
/* 117 */       this.written += totalWritten;
/* 118 */       if (this.written == this.length) {
/* 119 */         for (ByteBuffer buffer : buffers)
/*     */         {
/* 121 */           buffer.flip();
/*     */         }
/* 123 */         this.cacheEntry.enable();
/*     */       } 
/*     */     } 
/* 126 */     return totalWritten;
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 131 */     if (this.written != this.length) {
/* 132 */       this.cacheEntry.disable();
/* 133 */       this.cacheEntry.dereference();
/*     */     } 
/* 135 */     super.terminateWrites();
/*     */   }
/*     */ 
/*     */   
/*     */   public void truncateWrites() throws IOException {
/* 140 */     if (this.written != this.length) {
/* 141 */       this.cacheEntry.disable();
/* 142 */       this.cacheEntry.dereference();
/*     */     } 
/* 144 */     super.truncateWrites();
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 149 */     return Conduits.writeFinalBasic((StreamSinkConduit)this, srcs, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 154 */     return Conduits.writeFinalBasic((StreamSinkConduit)this, src);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\cache\ResponseCachingStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */