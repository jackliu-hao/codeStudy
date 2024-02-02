/*     */ package io.undertow.server.protocol.ajp;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.conduits.ConduitListener;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.RequestTooBigException;
/*     */ import io.undertow.util.ImmediatePooledByteBuffer;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.conduits.AbstractStreamSourceConduit;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.ConduitReadableByteChannel;
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
/*     */ public class AjpServerRequestConduit
/*     */   extends AbstractStreamSourceConduit<StreamSourceConduit>
/*     */ {
/*     */   private static final ByteBuffer READ_BODY_CHUNK;
/*     */   private static final int HEADER_LENGTH = 6;
/*     */   private static final long STATE_READING = -9223372036854775808L;
/*     */   private static final long STATE_SEND_REQUIRED = 4611686018427387904L;
/*     */   private static final long STATE_FINISHED = 2305843009213693952L;
/*     */   
/*     */   static {
/*  51 */     ByteBuffer readBody = ByteBuffer.allocateDirect(7);
/*  52 */     readBody.put((byte)65);
/*  53 */     readBody.put((byte)66);
/*  54 */     readBody.put((byte)0);
/*  55 */     readBody.put((byte)3);
/*  56 */     readBody.put((byte)6);
/*  57 */     readBody.put((byte)31);
/*  58 */     readBody.put((byte)-6);
/*  59 */     readBody.flip();
/*  60 */     READ_BODY_CHUNK = readBody;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  82 */   private static final long STATE_MASK = Bits.longBitMask(0, 60);
/*     */ 
/*     */ 
/*     */   
/*     */   private final HttpServerExchange exchange;
/*     */ 
/*     */   
/*     */   private final AjpServerResponseConduit ajpResponseConduit;
/*     */ 
/*     */   
/*  92 */   private final ByteBuffer headerBuffer = ByteBuffer.allocateDirect(6);
/*     */ 
/*     */ 
/*     */   
/*     */   private final ConduitListener<? super AjpServerRequestConduit> finishListener;
/*     */ 
/*     */ 
/*     */   
/*     */   private long remaining;
/*     */ 
/*     */ 
/*     */   
/*     */   private long state;
/*     */ 
/*     */ 
/*     */   
/*     */   private long totalRead;
/*     */ 
/*     */ 
/*     */   
/*     */   public AjpServerRequestConduit(StreamSourceConduit delegate, HttpServerExchange exchange, AjpServerResponseConduit ajpResponseConduit, Long size, ConduitListener<? super AjpServerRequestConduit> finishListener) {
/* 113 */     super(delegate);
/* 114 */     this.exchange = exchange;
/* 115 */     this.ajpResponseConduit = ajpResponseConduit;
/* 116 */     this.finishListener = finishListener;
/* 117 */     if (size == null) {
/* 118 */       this.state = 4611686018427387904L;
/* 119 */       this.remaining = -1L;
/* 120 */     } else if (size.longValue() == 0L) {
/* 121 */       this.state = 2305843009213693952L;
/* 122 */       this.remaining = 0L;
/*     */     } else {
/* 124 */       this.state = Long.MIN_VALUE;
/* 125 */       this.remaining = size.longValue();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*     */     try {
/* 132 */       return target.transferFrom((ReadableByteChannel)new ConduitReadableByteChannel((StreamSourceConduit)this), position, count);
/* 133 */     } catch (IOException|RuntimeException e) {
/* 134 */       IoUtils.safeClose((Closeable)this.exchange.getConnection());
/* 135 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*     */     try {
/* 142 */       return IoUtils.transfer((ReadableByteChannel)new ConduitReadableByteChannel((StreamSourceConduit)this), count, throughBuffer, (WritableByteChannel)target);
/* 143 */     } catch (IOException|RuntimeException e) {
/* 144 */       IoUtils.safeClose((Closeable)this.exchange.getConnection());
/* 145 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateReads() throws IOException {
/* 151 */     if (this.exchange.isPersistent() && Bits.anyAreSet(this.state, 2305843009213693952L)) {
/*     */       return;
/*     */     }
/* 154 */     super.terminateReads();
/*     */   }
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/*     */     try {
/* 160 */       long total = 0L;
/* 161 */       for (int i = offset; i < length; i++) {
/* 162 */         while (dsts[i].hasRemaining()) {
/* 163 */           int r = read(dsts[i]);
/* 164 */           if (r <= 0 && total > 0L)
/* 165 */             return total; 
/* 166 */           if (r <= 0) {
/* 167 */             return r;
/*     */           }
/* 169 */           total += r;
/*     */         } 
/*     */       } 
/*     */       
/* 173 */       return total;
/* 174 */     } catch (IOException|RuntimeException e) {
/* 175 */       IoUtils.safeClose((Closeable)this.exchange.getConnection());
/* 176 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/*     */     try {
/* 183 */       long state = this.state;
/* 184 */       if (Bits.anyAreSet(state, 2305843009213693952L))
/* 185 */         return -1; 
/* 186 */       if (Bits.anyAreSet(state, 4611686018427387904L)) {
/* 187 */         state = this.state = state & STATE_MASK | Long.MIN_VALUE;
/* 188 */         if (this.ajpResponseConduit.isWriteShutdown()) {
/* 189 */           this.state = 2305843009213693952L;
/* 190 */           if (this.finishListener != null) {
/* 191 */             this.finishListener.handleEvent((Conduit)this);
/*     */           }
/* 193 */           return -1;
/*     */         } 
/* 195 */         if (!this.ajpResponseConduit.doGetRequestBodyChunk(READ_BODY_CHUNK.duplicate(), this)) {
/* 196 */           return 0;
/*     */         }
/*     */       } 
/*     */       
/* 200 */       if (Bits.anyAreSet(state, Long.MIN_VALUE)) {
/* 201 */         return doRead(dst, state);
/*     */       }
/* 203 */       assert 2305843009213693952L == state;
/* 204 */       return -1;
/* 205 */     } catch (IOException|RuntimeException e) {
/* 206 */       IoUtils.safeClose((Closeable)this.exchange.getConnection());
/* 207 */       throw e;
/*     */     } 
/*     */   }
/*     */   private int doRead(ByteBuffer dst, long state) throws IOException {
/*     */     long chunkRemaining;
/* 212 */     ByteBuffer headerBuffer = this.headerBuffer;
/* 213 */     long headerRead = (6 - headerBuffer.remaining());
/* 214 */     long remaining = this.remaining;
/* 215 */     if (remaining == 0L) {
/* 216 */       this.state = 2305843009213693952L;
/* 217 */       if (this.finishListener != null) {
/* 218 */         this.finishListener.handleEvent((Conduit)this);
/*     */       }
/* 220 */       return -1;
/*     */     } 
/*     */     
/* 223 */     if (headerRead != 6L) {
/* 224 */       int read = ((StreamSourceConduit)this.next).read(headerBuffer);
/* 225 */       if (read == -1) {
/* 226 */         this.state = 2305843009213693952L;
/* 227 */         if (this.finishListener != null) {
/* 228 */           this.finishListener.handleEvent((Conduit)this);
/*     */         }
/* 230 */         throw new ClosedChannelException();
/* 231 */       }  if (headerBuffer.hasRemaining()) {
/* 232 */         if (headerBuffer.remaining() <= 2) {
/*     */ 
/*     */           
/* 235 */           byte b3 = headerBuffer.get(0);
/* 236 */           byte b4 = headerBuffer.get(1);
/* 237 */           if (b3 != 18 || b4 != 52) {
/* 238 */             throw UndertowMessages.MESSAGES.wrongMagicNumber((b3 & 0xFF) << 8 | b4 & 0xFF);
/*     */           }
/* 240 */           b3 = headerBuffer.get(2);
/* 241 */           b4 = headerBuffer.get(3);
/* 242 */           int i = (b3 & 0xFF) << 8 | b4 & 0xFF;
/* 243 */           if (i == 0) {
/* 244 */             if (headerBuffer.remaining() < 2) {
/* 245 */               byte[] data = new byte[1];
/* 246 */               ByteBuffer bb = ByteBuffer.wrap(data);
/* 247 */               bb.put(headerBuffer.get(4));
/* 248 */               bb.flip();
/* 249 */               Connectors.ungetRequestBytes(this.exchange, new PooledByteBuffer[] { (PooledByteBuffer)new ImmediatePooledByteBuffer(bb) });
/*     */             } 
/* 251 */             this.remaining = 0L;
/* 252 */             this.state = 2305843009213693952L;
/*     */             
/* 254 */             if (this.finishListener != null) {
/* 255 */               this.finishListener.handleEvent((Conduit)this);
/*     */             }
/* 257 */             return -1;
/*     */           } 
/*     */         } 
/*     */         
/* 261 */         return 0;
/*     */       } 
/* 263 */       headerBuffer.flip();
/* 264 */       byte b1 = headerBuffer.get();
/* 265 */       byte b2 = headerBuffer.get();
/* 266 */       if (b1 != 18 || b2 != 52) {
/* 267 */         throw UndertowMessages.MESSAGES.wrongMagicNumber((b1 & 0xFF) << 8 | b2 & 0xFF);
/*     */       }
/* 269 */       b1 = headerBuffer.get();
/* 270 */       b2 = headerBuffer.get();
/* 271 */       int totalSize = (b1 & 0xFF) << 8 | b2 & 0xFF;
/* 272 */       if (totalSize == 0) {
/* 273 */         byte[] data = new byte[2];
/* 274 */         ByteBuffer bb = ByteBuffer.wrap(data);
/* 275 */         bb.put(headerBuffer);
/* 276 */         bb.flip();
/* 277 */         Connectors.ungetRequestBytes(this.exchange, new PooledByteBuffer[] { (PooledByteBuffer)new ImmediatePooledByteBuffer(bb) });
/* 278 */         this.remaining = 0L;
/* 279 */         this.state = 2305843009213693952L;
/*     */         
/* 281 */         if (this.finishListener != null) {
/* 282 */           this.finishListener.handleEvent((Conduit)this);
/*     */         }
/* 284 */         return -1;
/*     */       } 
/*     */       
/* 287 */       b1 = headerBuffer.get();
/* 288 */       b2 = headerBuffer.get();
/* 289 */       chunkRemaining = ((b1 & 0xFF) << 8 | b2 & 0xFF);
/* 290 */       if (chunkRemaining == 0L) {
/* 291 */         this.remaining = 0L;
/* 292 */         this.state = 2305843009213693952L;
/*     */         
/* 294 */         if (this.finishListener != null) {
/* 295 */           this.finishListener.handleEvent((Conduit)this);
/*     */         }
/* 297 */         return -1;
/*     */       } 
/*     */     } else {
/*     */       
/* 301 */       chunkRemaining = this.state & STATE_MASK;
/*     */     } 
/*     */     
/* 304 */     int limit = dst.limit();
/* 305 */     Throwable originalException = null;
/* 306 */     int returnValue = 0;
/*     */     try {
/* 308 */       if (dst.remaining() > chunkRemaining) {
/* 309 */         dst.limit((int)(dst.position() + chunkRemaining));
/*     */       }
/* 311 */       int read = ((StreamSourceConduit)this.next).read(dst);
/* 312 */       chunkRemaining -= read;
/* 313 */       if (remaining != -1L) {
/* 314 */         remaining -= read;
/*     */       }
/* 316 */       this.totalRead += read;
/* 317 */       if (remaining != 0L) {
/* 318 */         if (chunkRemaining == 0L) {
/* 319 */           headerBuffer.clear();
/* 320 */           this.state = 4611686018427387904L;
/*     */         } else {
/* 322 */           this.state = state & (STATE_MASK ^ 0xFFFFFFFFFFFFFFFFL) | chunkRemaining;
/*     */         } 
/*     */       }
/* 325 */       returnValue = read;
/* 326 */     } catch (Throwable t) {
/* 327 */       originalException = t;
/*     */     } finally {
/* 329 */       Throwable throwable = originalException;
/*     */       try {
/* 331 */         this.remaining = remaining;
/* 332 */         dst.limit(limit);
/* 333 */         long maxEntitySize = this.exchange.getMaxEntitySize();
/* 334 */         if (maxEntitySize > 0L && 
/* 335 */           this.totalRead > maxEntitySize) {
/*     */           
/* 337 */           terminateReads();
/* 338 */           this.exchange.setPersistent(false);
/* 339 */           RequestTooBigException requestTooBigException = UndertowMessages.MESSAGES.requestEntityWasTooLarge(maxEntitySize);
/* 340 */           if (originalException != null) {
/* 341 */             originalException.addSuppressed((Throwable)requestTooBigException);
/* 342 */             throwable = originalException;
/*     */           }
/*     */         
/*     */         } 
/* 346 */       } catch (Throwable t) {
/* 347 */         if (throwable != null) {
/* 348 */           throwable.addSuppressed(t);
/*     */         } else {
/* 350 */           throwable = t;
/*     */         } 
/*     */       } 
/* 353 */       if (throwable != null) {
/* 354 */         if (throwable instanceof RuntimeException) {
/* 355 */           throw (RuntimeException)throwable;
/*     */         }
/* 357 */         if (throwable instanceof Error) {
/* 358 */           throw (Error)throwable;
/*     */         }
/* 360 */         if (throwable instanceof IOException) {
/* 361 */           throw (IOException)throwable;
/*     */         }
/*     */       } 
/*     */     } 
/* 365 */     return returnValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitReadable() throws IOException {
/*     */     try {
/* 371 */       if (Bits.anyAreSet(this.state, Long.MIN_VALUE)) {
/* 372 */         ((StreamSourceConduit)this.next).awaitReadable();
/*     */       }
/* 374 */     } catch (IOException|RuntimeException e) {
/* 375 */       IoUtils.safeClose((Closeable)this.exchange.getConnection());
/* 376 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/*     */     try {
/* 383 */       if (Bits.anyAreSet(this.state, Long.MIN_VALUE)) {
/* 384 */         ((StreamSourceConduit)this.next).awaitReadable(time, timeUnit);
/*     */       }
/* 386 */     } catch (IOException|RuntimeException e) {
/* 387 */       IoUtils.safeClose((Closeable)this.exchange.getConnection());
/* 388 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setReadBodyChunkError(IOException e) {
/* 397 */     IoUtils.safeClose((Closeable)this.exchange.getConnection());
/* 398 */     if (isReadResumed())
/* 399 */       wakeupReads(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\ajp\AjpServerRequestConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */