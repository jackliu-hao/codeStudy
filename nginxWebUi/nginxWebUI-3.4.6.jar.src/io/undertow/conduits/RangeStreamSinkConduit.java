/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
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
/*     */ public class RangeStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*     */   private final long start;
/*     */   private final long end;
/*     */   private final long originalResponseLength;
/*     */   private long written;
/*     */   
/*     */   public RangeStreamSinkConduit(StreamSinkConduit next, long start, long end, long originalResponseLength) {
/*  43 */     super(next);
/*  44 */     this.start = start;
/*  45 */     this.end = end;
/*  46 */     this.originalResponseLength = originalResponseLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*  51 */     boolean currentInclude = (this.written >= this.start && this.written <= this.end);
/*  52 */     long bytesRemaining = (this.written < this.start) ? (this.start - this.written) : ((this.written <= this.end) ? (this.end - this.written + 1L) : Long.MAX_VALUE);
/*  53 */     if (currentInclude) {
/*  54 */       int written, old = src.limit();
/*  55 */       src.limit((int)Math.min(src.position() + bytesRemaining, src.limit()));
/*     */       
/*  57 */       int toConsume = 0;
/*     */       try {
/*  59 */         written = super.write(src);
/*  60 */         this.written += written;
/*     */       } finally {
/*  62 */         if (!src.hasRemaining()) {
/*     */           
/*  64 */           src.limit(old);
/*  65 */           if (src.hasRemaining()) {
/*  66 */             toConsume = src.remaining();
/*     */             
/*  68 */             this.written += toConsume;
/*  69 */             src.position(src.limit());
/*     */           } 
/*     */         } else {
/*  72 */           src.limit(old);
/*     */         } 
/*     */       } 
/*  75 */       return written + toConsume;
/*     */     } 
/*  77 */     if (src.remaining() <= bytesRemaining) {
/*  78 */       int rem = src.remaining();
/*  79 */       this.written += rem;
/*  80 */       src.position(src.limit());
/*  81 */       return rem;
/*     */     } 
/*  83 */     this.written += bytesRemaining;
/*  84 */     src.position((int)(src.position() + bytesRemaining));
/*  85 */     return (int)bytesRemaining + write(src);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
/*  92 */     long ret = 0L;
/*     */     
/*  94 */     for (int i = offs; i < offs + len; i++) {
/*  95 */       ByteBuffer buf = srcs[i];
/*  96 */       if (buf.remaining() > 0) {
/*  97 */         ret += write(buf);
/*  98 */         if (buf.hasRemaining()) {
/*  99 */           return ret;
/*     */         }
/*     */       } 
/*     */     } 
/* 103 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 109 */     return IoUtils.transfer((ReadableByteChannel)source, count, throughBuffer, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 114 */     return src.transferTo(position, count, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 119 */     return Conduits.writeFinalBasic((StreamSinkConduit)this, srcs, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 124 */     return Conduits.writeFinalBasic((StreamSinkConduit)this, src);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\RangeStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */