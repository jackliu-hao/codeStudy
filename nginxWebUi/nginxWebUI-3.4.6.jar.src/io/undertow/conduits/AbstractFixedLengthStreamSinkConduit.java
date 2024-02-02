/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.channels.FixedLengthOverflowException;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.AbstractStreamSinkConduit;
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
/*     */ public abstract class AbstractFixedLengthStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*     */   private int config;
/*     */   private long state;
/*     */   private boolean broken = false;
/*     */   private static final int CONF_FLAG_CONFIGURABLE = 1;
/*     */   private static final int CONF_FLAG_PASS_CLOSE = 2;
/*     */   private static final long FLAG_CLOSE_REQUESTED = -9223372036854775808L;
/*     */   private static final long FLAG_CLOSE_COMPLETE = 4611686018427387904L;
/*     */   private static final long FLAG_FINISHED_CALLED = 2305843009213693952L;
/*  59 */   private static final long MASK_COUNT = Bits.longBitMask(0, 60);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractFixedLengthStreamSinkConduit(StreamSinkConduit next, long contentLength, boolean configurable, boolean propagateClose) {
/*  70 */     super(next);
/*  71 */     if (contentLength < 0L)
/*  72 */       throw new IllegalArgumentException("Content length must be greater than or equal to zero"); 
/*  73 */     if (contentLength > MASK_COUNT) {
/*  74 */       throw new IllegalArgumentException("Content length is too long");
/*     */     }
/*  76 */     this.config = (configurable ? 1 : 0) | (propagateClose ? 2 : 0);
/*  77 */     this.state = contentLength;
/*     */   }
/*     */   
/*     */   protected void reset(long contentLength, boolean propagateClose) {
/*  81 */     this.state = contentLength;
/*  82 */     if (propagateClose) {
/*  83 */       this.config |= 0x2;
/*     */     } else {
/*  85 */       this.config &= 0xFFFFFFFD;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*  90 */     long val = this.state;
/*  91 */     long remaining = val & MASK_COUNT;
/*  92 */     if (!src.hasRemaining()) {
/*  93 */       return 0;
/*     */     }
/*  95 */     if (Bits.allAreSet(val, Long.MIN_VALUE)) {
/*  96 */       throw new ClosedChannelException();
/*     */     }
/*  98 */     int oldLimit = src.limit();
/*  99 */     if (remaining == 0L)
/* 100 */       throw new FixedLengthOverflowException(); 
/* 101 */     if (src.remaining() > remaining) {
/* 102 */       src.limit((int)(src.position() + remaining));
/*     */     }
/* 104 */     int res = 0;
/*     */     try {
/* 106 */       return res = ((StreamSinkConduit)this.next).write(src);
/* 107 */     } catch (IOException|RuntimeException|Error e) {
/* 108 */       this.broken = true;
/* 109 */       throw e;
/*     */     } finally {
/* 111 */       src.limit(oldLimit);
/* 112 */       exitWrite(val, res);
/*     */     } 
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 117 */     if (length == 0)
/* 118 */       return 0L; 
/* 119 */     if (length == 1) {
/* 120 */       return write(srcs[offset]);
/*     */     }
/* 122 */     long val = this.state;
/* 123 */     long remaining = val & MASK_COUNT;
/* 124 */     if (Bits.allAreSet(val, Long.MIN_VALUE)) {
/* 125 */       throw new ClosedChannelException();
/*     */     }
/* 127 */     long toWrite = Buffers.remaining((Buffer[])srcs, offset, length);
/* 128 */     if (remaining == 0L) {
/* 129 */       throw new FixedLengthOverflowException();
/*     */     }
/* 131 */     int[] limits = null;
/* 132 */     if (toWrite > remaining) {
/* 133 */       limits = new int[length];
/* 134 */       long r = remaining;
/* 135 */       for (int i = offset; i < offset + length; i++) {
/* 136 */         limits[i - offset] = srcs[i].limit();
/* 137 */         int br = srcs[i].remaining();
/* 138 */         if (br < r) {
/* 139 */           r -= br;
/*     */         } else {
/* 141 */           srcs[i].limit((int)(srcs[i].position() + r));
/* 142 */           r = 0L;
/*     */         } 
/*     */       } 
/*     */     } 
/* 146 */     long res = 0L;
/*     */     try {
/* 148 */       return res = ((StreamSinkConduit)this.next).write(srcs, offset, length);
/* 149 */     } catch (IOException|RuntimeException|Error e) {
/* 150 */       this.broken = true;
/* 151 */       throw e;
/*     */     } finally {
/* 153 */       if (limits != null) {
/* 154 */         for (int i = offset; i < offset + length; i++) {
/* 155 */           srcs[i].limit(limits[i - offset]);
/*     */         }
/*     */       }
/* 158 */       exitWrite(val, res);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/*     */     try {
/* 165 */       return Conduits.writeFinalBasic((StreamSinkConduit)this, srcs, offset, length);
/* 166 */     } catch (IOException|RuntimeException|Error e) {
/* 167 */       this.broken = true;
/* 168 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/*     */     try {
/* 175 */       return Conduits.writeFinalBasic((StreamSinkConduit)this, src);
/* 176 */     } catch (IOException|RuntimeException|Error e) {
/* 177 */       this.broken = true;
/* 178 */       throw e;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 183 */     if (count == 0L) return 0L; 
/* 184 */     long val = this.state;
/* 185 */     if (Bits.allAreSet(val, Long.MIN_VALUE)) {
/* 186 */       throw new ClosedChannelException();
/*     */     }
/* 188 */     if (Bits.allAreClear(val, MASK_COUNT)) {
/* 189 */       throw new FixedLengthOverflowException();
/*     */     }
/* 191 */     long res = 0L;
/*     */     try {
/* 193 */       return res = ((StreamSinkConduit)this.next).transferFrom(src, position, Math.min(count, val & MASK_COUNT));
/* 194 */     } catch (IOException|RuntimeException|Error e) {
/* 195 */       this.broken = true;
/* 196 */       throw e;
/*     */     } finally {
/* 198 */       exitWrite(val, res);
/*     */     } 
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 203 */     if (count == 0L) return 0L; 
/* 204 */     long val = this.state;
/* 205 */     if (Bits.allAreSet(val, Long.MIN_VALUE)) {
/* 206 */       throw new ClosedChannelException();
/*     */     }
/* 208 */     if (Bits.allAreClear(val, MASK_COUNT)) {
/* 209 */       throw new FixedLengthOverflowException();
/*     */     }
/* 211 */     long res = 0L;
/*     */     try {
/* 213 */       return res = ((StreamSinkConduit)this.next).transferFrom(source, Math.min(count, val & MASK_COUNT), throughBuffer);
/* 214 */     } catch (IOException|RuntimeException|Error e) {
/* 215 */       this.broken = true;
/* 216 */       throw e;
/*     */     } finally {
/* 218 */       exitWrite(val, res);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/* 223 */     long val = this.state;
/* 224 */     if (Bits.anyAreSet(val, 4611686018427387904L)) {
/* 225 */       return true;
/*     */     }
/* 227 */     boolean flushed = false;
/*     */     try {
/* 229 */       return flushed = ((StreamSinkConduit)this.next).flush();
/* 230 */     } catch (IOException|RuntimeException|Error e) {
/* 231 */       this.broken = true;
/* 232 */       throw e;
/*     */     } finally {
/* 234 */       exitFlush(val, flushed);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWriteResumed() {
/* 240 */     return (Bits.allAreClear(this.state, 4611686018427387904L) && ((StreamSinkConduit)this.next).isWriteResumed());
/*     */   }
/*     */   
/*     */   public void wakeupWrites() {
/* 244 */     long val = this.state;
/* 245 */     if (Bits.anyAreSet(val, 4611686018427387904L)) {
/*     */       return;
/*     */     }
/* 248 */     ((StreamSinkConduit)this.next).wakeupWrites();
/*     */   }
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 252 */     long val = enterShutdown();
/* 253 */     if (Bits.anyAreSet(val, MASK_COUNT) && !this.broken) {
/* 254 */       UndertowLogger.REQUEST_IO_LOGGER.debugf("Fixed length stream closed with with %s bytes remaining", val & MASK_COUNT);
/*     */       try {
/* 256 */         ((StreamSinkConduit)this.next).truncateWrites();
/*     */       } finally {
/* 258 */         if (!Bits.anyAreSet(this.state, 2305843009213693952L)) {
/* 259 */           this.state |= 0x2000000000000000L;
/* 260 */           channelFinished();
/*     */         } 
/*     */       } 
/* 263 */     } else if (Bits.allAreSet(this.config, 2)) {
/* 264 */       ((StreamSinkConduit)this.next).terminateWrites();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void truncateWrites() throws IOException {
/*     */     try {
/* 272 */       if (!Bits.anyAreSet(this.state, 2305843009213693952L)) {
/* 273 */         this.state |= 0x2000000000000000L;
/* 274 */         channelFinished();
/*     */       } 
/*     */     } finally {
/* 277 */       super.truncateWrites();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 282 */     ((StreamSinkConduit)this.next).awaitWritable();
/*     */   }
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 286 */     ((StreamSinkConduit)this.next).awaitWritable(time, timeUnit);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getRemaining() {
/* 296 */     return this.state & MASK_COUNT;
/*     */   }
/*     */   
/*     */   private void exitWrite(long oldVal, long consumed) {
/* 300 */     long newVal = oldVal - consumed;
/* 301 */     this.state = newVal;
/*     */   }
/*     */   
/*     */   private void exitFlush(long oldVal, boolean flushed) {
/* 305 */     long newVal = oldVal;
/* 306 */     boolean callFinish = false;
/* 307 */     if ((Bits.anyAreSet(oldVal, Long.MIN_VALUE) || (newVal & MASK_COUNT) == 0L) && flushed) {
/* 308 */       newVal |= 0x4000000000000000L;
/*     */       
/* 310 */       if (!Bits.anyAreSet(oldVal, 2305843009213693952L) && (newVal & MASK_COUNT) == 0L) {
/* 311 */         newVal |= 0x2000000000000000L;
/* 312 */         callFinish = true;
/*     */       } 
/* 314 */       this.state = newVal;
/* 315 */       if (callFinish) {
/* 316 */         channelFinished();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void channelFinished() {}
/*     */ 
/*     */   
/*     */   private long enterShutdown() {
/* 326 */     long oldVal = this.state;
/* 327 */     if (Bits.anyAreSet(oldVal, -4611686018427387904L))
/*     */     {
/* 329 */       return oldVal;
/*     */     }
/* 331 */     long newVal = oldVal | Long.MIN_VALUE;
/* 332 */     if (Bits.anyAreSet(oldVal, MASK_COUNT))
/*     */     {
/* 334 */       newVal |= 0x4000000000000000L;
/*     */     }
/* 336 */     this.state = newVal;
/* 337 */     return oldVal;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\AbstractFixedLengthStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */