/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import org.xnio.Bits;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class HeadStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*     */   private final ConduitListener<? super HeadStreamSinkConduit> finishListener;
/*     */   private int state;
/*     */   private final boolean shutdownDelegate;
/*     */   private static final int FLAG_CLOSE_REQUESTED = 1;
/*     */   private static final int FLAG_CLOSE_COMPLETE = 2;
/*     */   private static final int FLAG_FINISHED_CALLED = 4;
/*     */   
/*     */   public HeadStreamSinkConduit(StreamSinkConduit next, ConduitListener<? super HeadStreamSinkConduit> finishListener) {
/*  60 */     this(next, finishListener, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HeadStreamSinkConduit(StreamSinkConduit next, ConduitListener<? super HeadStreamSinkConduit> finishListener, boolean shutdownDelegate) {
/*  70 */     super(next);
/*  71 */     this.finishListener = finishListener;
/*  72 */     this.shutdownDelegate = shutdownDelegate;
/*     */   }
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*  76 */     if (Bits.anyAreSet(this.state, 2)) {
/*  77 */       throw new ClosedChannelException();
/*     */     }
/*  79 */     int remaining = src.remaining();
/*  80 */     src.position(src.position() + remaining);
/*  81 */     return remaining;
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/*  85 */     if (Bits.anyAreSet(this.state, 2)) {
/*  86 */       throw new ClosedChannelException();
/*     */     }
/*  88 */     long total = 0L;
/*  89 */     for (int i = offset; i < offset + length; i++) {
/*  90 */       ByteBuffer src = srcs[i];
/*  91 */       int remaining = src.remaining();
/*  92 */       total += remaining;
/*  93 */       src.position(src.position() + remaining);
/*     */     } 
/*  95 */     return total;
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 100 */     return Conduits.writeFinalBasic((StreamSinkConduit)this, src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 105 */     return Conduits.writeFinalBasic((StreamSinkConduit)this, srcs, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 110 */     if (Bits.anyAreSet(this.state, 2)) {
/* 111 */       throw new ClosedChannelException();
/*     */     }
/* 113 */     return src.transferTo(position, count, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 118 */     if (Bits.anyAreSet(this.state, 2)) {
/* 119 */       throw new ClosedChannelException();
/*     */     }
/* 121 */     return IoUtils.transfer((ReadableByteChannel)source, count, throughBuffer, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/* 125 */     int val = this.state;
/* 126 */     if (Bits.anyAreSet(val, 2)) {
/* 127 */       return true;
/*     */     }
/* 129 */     boolean flushed = false;
/*     */     try {
/* 131 */       return flushed = ((StreamSinkConduit)this.next).flush();
/*     */     } finally {
/* 133 */       exitFlush(val, flushed);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void suspendWrites() {
/* 138 */     long val = this.state;
/* 139 */     if (Bits.anyAreSet(val, 2L)) {
/*     */       return;
/*     */     }
/* 142 */     ((StreamSinkConduit)this.next).suspendWrites();
/*     */   }
/*     */   
/*     */   public void resumeWrites() {
/* 146 */     long val = this.state;
/* 147 */     if (Bits.anyAreSet(val, 2L)) {
/*     */       return;
/*     */     }
/* 150 */     ((StreamSinkConduit)this.next).resumeWrites();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWriteResumed() {
/* 155 */     return (Bits.allAreClear(this.state, 2) && ((StreamSinkConduit)this.next).isWriteResumed());
/*     */   }
/*     */   
/*     */   public void wakeupWrites() {
/* 159 */     long val = this.state;
/* 160 */     if (Bits.anyAreSet(val, 2L)) {
/*     */       return;
/*     */     }
/* 163 */     ((StreamSinkConduit)this.next).wakeupWrites();
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 168 */     int oldVal = this.state;
/* 169 */     if (Bits.anyAreSet(oldVal, 3)) {
/*     */       return;
/*     */     }
/*     */     
/* 173 */     int newVal = oldVal | 0x1;
/* 174 */     this.state = newVal;
/* 175 */     if (this.shutdownDelegate) {
/* 176 */       ((StreamSinkConduit)this.next).terminateWrites();
/*     */     }
/*     */   }
/*     */   
/*     */   private void exitFlush(int oldVal, boolean flushed) {
/* 181 */     int newVal = oldVal;
/* 182 */     boolean callFinish = false;
/* 183 */     if (Bits.anyAreSet(oldVal, 1) && flushed) {
/* 184 */       newVal |= 0x2;
/* 185 */       if (!Bits.anyAreSet(oldVal, 4)) {
/* 186 */         newVal |= 0x4;
/* 187 */         callFinish = true;
/*     */       } 
/* 189 */       this.state = newVal;
/* 190 */       if (callFinish && 
/* 191 */         this.finishListener != null)
/* 192 */         this.finishListener.handleEvent(this); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\HeadStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */