/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.conduits.AbstractStreamSourceConduit;
/*     */ import org.xnio.conduits.StreamSourceConduit;
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
/*     */ public final class FixedLengthStreamSourceConduit
/*     */   extends AbstractStreamSourceConduit<StreamSourceConduit>
/*     */ {
/*     */   private final ConduitListener<? super FixedLengthStreamSourceConduit> finishListener;
/*     */   private long state;
/*     */   private static final long FLAG_CLOSED = -9223372036854775808L;
/*     */   private static final long FLAG_FINISHED = 4611686018427387904L;
/*     */   private static final long FLAG_LENGTH_CHECKED = 2305843009213693952L;
/*  68 */   private static final long MASK_COUNT = Bits.longBitMask(0, 60);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final HttpServerExchange exchange;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FixedLengthStreamSourceConduit(StreamSourceConduit next, long contentLength, ConduitListener<? super FixedLengthStreamSourceConduit> finishListener, HttpServerExchange exchange) {
/*  87 */     super(next);
/*  88 */     this.finishListener = finishListener;
/*  89 */     if (contentLength < 0L)
/*  90 */       throw new IllegalArgumentException("Content length must be greater than or equal to zero"); 
/*  91 */     if (contentLength > MASK_COUNT) {
/*  92 */       throw new IllegalArgumentException("Content length is too long");
/*     */     }
/*  94 */     this.state = contentLength;
/*  95 */     this.exchange = exchange;
/*     */   }
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
/*     */   public FixedLengthStreamSourceConduit(StreamSourceConduit next, long contentLength, ConduitListener<? super FixedLengthStreamSourceConduit> finishListener) {
/* 112 */     this(next, contentLength, finishListener, null);
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 116 */     long val = this.state;
/* 117 */     checkMaxSize(val);
/* 118 */     if (Bits.anyAreSet(val, -4611686018427387904L) || Bits.allAreClear(val, MASK_COUNT)) {
/* 119 */       if (Bits.allAreClear(val, 4611686018427387904L)) {
/* 120 */         invokeFinishListener();
/*     */       }
/* 122 */       return -1L;
/*     */     } 
/* 124 */     long res = 0L;
/* 125 */     Throwable transferError = null;
/*     */     try {
/* 127 */       return res = ((StreamSourceConduit)this.next).transferTo(position, Math.min(count, val & MASK_COUNT), target);
/* 128 */     } catch (IOException|RuntimeException|Error e) {
/* 129 */       closeConnection();
/* 130 */       transferError = e;
/* 131 */       throw e;
/*     */     } finally {
/* 133 */       exitRead(res, transferError);
/*     */     } 
/*     */   }
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 138 */     if (count == 0L) {
/* 139 */       return 0L;
/*     */     }
/* 141 */     long val = this.state;
/* 142 */     checkMaxSize(val);
/* 143 */     if (Bits.anyAreSet(val, -4611686018427387904L) || Bits.allAreClear(val, MASK_COUNT)) {
/* 144 */       if (Bits.allAreClear(val, 4611686018427387904L)) {
/* 145 */         invokeFinishListener();
/*     */       }
/* 147 */       return -1L;
/*     */     } 
/* 149 */     long res = 0L;
/* 150 */     Throwable transferError = null;
/*     */     try {
/* 152 */       return res = ((StreamSourceConduit)this.next).transferTo(Math.min(count, val & MASK_COUNT), throughBuffer, target);
/* 153 */     } catch (IOException|RuntimeException|Error e) {
/* 154 */       closeConnection();
/* 155 */       transferError = e;
/* 156 */       throw e;
/*     */     } finally {
/* 158 */       exitRead(res + throughBuffer.remaining(), transferError);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void checkMaxSize(long state) throws IOException {
/* 163 */     if (Bits.anyAreClear(state, 2305843009213693952L)) {
/* 164 */       HttpServerExchange exchange = this.exchange;
/* 165 */       if (exchange != null && 
/* 166 */         exchange.getMaxEntitySize() > 0L && exchange.getMaxEntitySize() < (state & MASK_COUNT)) {
/*     */ 
/*     */         
/* 169 */         Connectors.terminateRequest(exchange);
/* 170 */         exchange.setPersistent(false);
/* 171 */         this.finishListener.handleEvent(this);
/* 172 */         this.state |= 0xC000000000000000L;
/* 173 */         throw UndertowMessages.MESSAGES.requestEntityWasTooLarge(exchange.getMaxEntitySize());
/*     */       } 
/*     */       
/* 176 */       this.state |= 0x2000000000000000L;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 181 */     if (length == 0)
/* 182 */       return 0L; 
/* 183 */     if (length == 1) {
/* 184 */       return read(dsts[offset]);
/*     */     }
/* 186 */     long val = this.state;
/* 187 */     checkMaxSize(val);
/* 188 */     if (Bits.allAreSet(val, Long.MIN_VALUE) || Bits.allAreClear(val, MASK_COUNT)) {
/* 189 */       if (Bits.allAreClear(val, 4611686018427387904L)) {
/* 190 */         invokeFinishListener();
/*     */       }
/* 192 */       return -1L;
/*     */     } 
/* 194 */     long res = 0L;
/* 195 */     Throwable readError = null;
/*     */     try {
/* 197 */       if ((val & MASK_COUNT) == 0L) {
/* 198 */         return -1L;
/*     */       }
/*     */ 
/*     */       
/* 202 */       long t = 0L;
/* 203 */       for (int i = 0; i < length; i++) {
/* 204 */         ByteBuffer buffer = dsts[i + offset];
/*     */         
/*     */         int lim;
/* 207 */         t += ((lim = buffer.limit()) - buffer.position());
/* 208 */         if (t > (val & MASK_COUNT)) {
/*     */           
/* 210 */           buffer.limit(lim - (int)(t - (val & MASK_COUNT)));
/*     */           try {
/* 212 */             return res = ((StreamSourceConduit)this.next).read(dsts, offset, i + 1);
/*     */           } finally {
/*     */             
/* 215 */             buffer.limit(lim);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 220 */       return res = ((StreamSourceConduit)this.next).read(dsts, offset, length);
/* 221 */     } catch (IOException|RuntimeException|Error e) {
/* 222 */       closeConnection();
/* 223 */       readError = e;
/* 224 */       throw e;
/*     */     } finally {
/* 226 */       exitRead(res, readError);
/*     */     } 
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts) throws IOException {
/* 231 */     return read(dsts, 0, dsts.length);
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/* 235 */     long val = this.state;
/* 236 */     checkMaxSize(val);
/* 237 */     if (Bits.allAreSet(val, Long.MIN_VALUE) || Bits.allAreClear(val, MASK_COUNT)) {
/* 238 */       if (Bits.allAreClear(val, 4611686018427387904L)) {
/* 239 */         invokeFinishListener();
/*     */       }
/* 241 */       return -1;
/*     */     } 
/* 243 */     int res = 0;
/* 244 */     long remaining = val & MASK_COUNT;
/* 245 */     Throwable readError = null;
/*     */     try {
/* 247 */       int lim = dst.limit();
/* 248 */       int pos = dst.position();
/* 249 */       if ((lim - pos) > remaining) {
/* 250 */         dst.limit((int)(remaining + pos));
/*     */         try {
/* 252 */           return res = ((StreamSourceConduit)this.next).read(dst);
/*     */         } finally {
/* 254 */           dst.limit(lim);
/*     */         } 
/*     */       } 
/* 257 */       return res = ((StreamSourceConduit)this.next).read(dst);
/*     */     }
/* 259 */     catch (IOException|RuntimeException|Error e) {
/* 260 */       closeConnection();
/* 261 */       readError = e;
/* 262 */       throw e;
/*     */     } finally {
/* 264 */       exitRead(res, readError);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isReadResumed() {
/* 269 */     return (Bits.allAreClear(this.state, Long.MIN_VALUE) && ((StreamSourceConduit)this.next).isReadResumed());
/*     */   }
/*     */   
/*     */   public void wakeupReads() {
/* 273 */     long val = this.state;
/* 274 */     if (Bits.anyAreSet(val, -4611686018427387904L)) {
/*     */       return;
/*     */     }
/* 277 */     ((StreamSourceConduit)this.next).wakeupReads();
/*     */   }
/*     */ 
/*     */   
/*     */   public void resumeReads() {
/* 282 */     long val = this.state;
/* 283 */     if (Bits.anyAreSet(val, -4611686018427387904L)) {
/*     */       return;
/*     */     }
/* 286 */     if (Bits.allAreClear(val, MASK_COUNT)) {
/* 287 */       ((StreamSourceConduit)this.next).wakeupReads();
/*     */     } else {
/* 289 */       ((StreamSourceConduit)this.next).resumeReads();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateReads() throws IOException {
/* 295 */     long val = enterShutdownReads();
/* 296 */     if (Bits.allAreSet(val, Long.MIN_VALUE)) {
/*     */       return;
/*     */     }
/* 299 */     exitShutdownReads(val);
/*     */   }
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 303 */     long val = this.state;
/* 304 */     if (Bits.allAreSet(val, Long.MIN_VALUE) || val == 0L) {
/*     */       return;
/*     */     }
/* 307 */     ((StreamSourceConduit)this.next).awaitReadable();
/*     */   }
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 311 */     long val = this.state;
/* 312 */     if (Bits.allAreSet(val, Long.MIN_VALUE) || val == 0L) {
/*     */       return;
/*     */     }
/*     */     try {
/* 316 */       ((StreamSourceConduit)this.next).awaitReadable(time, timeUnit);
/* 317 */     } catch (IOException|RuntimeException|Error e) {
/* 318 */       closeConnection();
/* 319 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getRemaining() {
/* 329 */     return this.state & MASK_COUNT;
/*     */   }
/*     */ 
/*     */   
/*     */   private long enterShutdownReads() {
/* 334 */     long oldVal = this.state;
/* 335 */     if (Bits.anyAreSet(oldVal, Long.MIN_VALUE)) {
/* 336 */       return oldVal;
/*     */     }
/* 338 */     long newVal = oldVal | Long.MIN_VALUE;
/* 339 */     this.state = newVal;
/* 340 */     return oldVal;
/*     */   }
/*     */   
/*     */   private void exitShutdownReads(long oldVal) {
/* 344 */     if (!Bits.allAreClear(oldVal, MASK_COUNT)) {
/* 345 */       invokeFinishListener();
/*     */     }
/*     */   }
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
/*     */   private void exitRead(long consumed, Throwable readError) throws IOException {
/* 359 */     long oldVal = this.state;
/* 360 */     if (consumed == -1L) {
/* 361 */       if (Bits.anyAreSet(oldVal, MASK_COUNT)) {
/* 362 */         invokeFinishListener();
/* 363 */         this.state &= MASK_COUNT ^ 0xFFFFFFFFFFFFFFFFL;
/* 364 */         IOException couldNotReadAll = UndertowMessages.MESSAGES.couldNotReadContentLengthData();
/* 365 */         if (readError != null) {
/* 366 */           couldNotReadAll.addSuppressed(readError);
/*     */         }
/* 368 */         throw couldNotReadAll;
/*     */       } 
/*     */       return;
/*     */     } 
/* 372 */     long newVal = oldVal - consumed;
/* 373 */     this.state = newVal;
/*     */   }
/*     */   
/*     */   private void invokeFinishListener() {
/* 377 */     this.state |= 0x4000000000000000L;
/* 378 */     this.finishListener.handleEvent(this);
/*     */   }
/*     */   
/*     */   private void closeConnection() {
/* 382 */     HttpServerExchange exchange = this.exchange;
/* 383 */     if (exchange != null)
/* 384 */       IoUtils.safeClose((Closeable)exchange.getConnection()); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\FixedLengthStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */