/*     */ package io.undertow.server.protocol.ajp;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.conduits.AbstractFramedStreamSinkConduit;
/*     */ import io.undertow.conduits.ConduitListener;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.StatusCodes;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.ConduitWritableByteChannel;
/*     */ import org.xnio.conduits.StreamSinkConduit;
/*     */ import org.xnio.conduits.WriteReadyHandler;
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
/*     */ final class AjpServerResponseConduit
/*     */   extends AbstractFramedStreamSinkConduit
/*     */ {
/*  63 */   private static final Logger log = Logger.getLogger("io.undertow.server.channel.ajp.response");
/*     */   
/*     */   private static final int DEFAULT_MAX_DATA_SIZE = 8192;
/*     */   private static final Map<HttpString, Integer> HEADER_MAP;
/*     */   private static final int FLAG_START = 1;
/*     */   private static final int FLAG_WRITE_RESUMED = 4;
/*  69 */   private static final ByteBuffer FLUSH_PACKET = ByteBuffer.allocateDirect(8);
/*     */   
/*     */   static {
/*  72 */     Map<HttpString, Integer> headers = new HashMap<>();
/*  73 */     headers.put(Headers.CONTENT_TYPE, Integer.valueOf(40961));
/*  74 */     headers.put(Headers.CONTENT_LANGUAGE, Integer.valueOf(40962));
/*  75 */     headers.put(Headers.CONTENT_LENGTH, Integer.valueOf(40963));
/*  76 */     headers.put(Headers.DATE, Integer.valueOf(40964));
/*  77 */     headers.put(Headers.LAST_MODIFIED, Integer.valueOf(40965));
/*  78 */     headers.put(Headers.LOCATION, Integer.valueOf(40966));
/*  79 */     headers.put(Headers.SET_COOKIE, Integer.valueOf(40967));
/*  80 */     headers.put(Headers.SET_COOKIE2, Integer.valueOf(40968));
/*  81 */     headers.put(Headers.SERVLET_ENGINE, Integer.valueOf(40969));
/*  82 */     headers.put(Headers.STATUS, Integer.valueOf(40970));
/*  83 */     headers.put(Headers.WWW_AUTHENTICATE, Integer.valueOf(40971));
/*  84 */     HEADER_MAP = Collections.unmodifiableMap(headers);
/*     */     
/*  86 */     FLUSH_PACKET.put((byte)65);
/*  87 */     FLUSH_PACKET.put((byte)66);
/*  88 */     FLUSH_PACKET.put((byte)0);
/*  89 */     FLUSH_PACKET.put((byte)4);
/*  90 */     FLUSH_PACKET.put((byte)3);
/*  91 */     FLUSH_PACKET.put((byte)0);
/*  92 */     FLUSH_PACKET.put((byte)0);
/*  93 */     FLUSH_PACKET.put((byte)0);
/*  94 */     FLUSH_PACKET.flip();
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
/* 108 */     ByteBuffer buffer = ByteBuffer.wrap(new byte[6]);
/* 109 */     buffer.put((byte)65);
/* 110 */     buffer.put((byte)66);
/* 111 */     buffer.put((byte)0);
/* 112 */     buffer.put((byte)2);
/* 113 */     buffer.put((byte)5);
/* 114 */     buffer.put((byte)1);
/* 115 */     buffer.flip();
/* 116 */     CLOSE_FRAME_PERSISTENT = buffer;
/* 117 */     buffer = ByteBuffer.wrap(new byte[6]);
/* 118 */     buffer.put(CLOSE_FRAME_PERSISTENT.duplicate());
/* 119 */     buffer.put(5, (byte)0);
/* 120 */     buffer.flip();
/* 121 */     CLOSE_FRAME_NON_PERSISTENT = buffer;
/*     */   }
/*     */ 
/*     */   
/*     */   private static final int FLAG_WRITE_READ_BODY_CHUNK_FROM_LISTENER = 8;
/*     */   
/*     */   private static final int FLAG_WRITE_SHUTDOWN = 16;
/*     */   
/*     */   private static final int FLAG_READS_DONE = 32;
/* 130 */   private int state = 1; private static final int FLAG_FLUSH_QUEUED = 64;
/*     */   private static final ByteBuffer CLOSE_FRAME_PERSISTENT;
/*     */   private static final ByteBuffer CLOSE_FRAME_NON_PERSISTENT;
/*     */   private final ByteBufferPool pool;
/*     */   private final HttpServerExchange exchange;
/*     */   private final ConduitListener<? super AjpServerResponseConduit> finishListener;
/*     */   private final boolean headRequest;
/*     */   
/*     */   AjpServerResponseConduit(StreamSinkConduit next, ByteBufferPool pool, HttpServerExchange exchange, ConduitListener<? super AjpServerResponseConduit> finishListener, boolean headRequest) {
/* 139 */     super(next);
/* 140 */     this.pool = pool;
/* 141 */     this.exchange = exchange;
/* 142 */     this.finishListener = finishListener;
/* 143 */     this.headRequest = headRequest;
/* 144 */     this.state = 1;
/*     */   }
/*     */   
/*     */   private static void putInt(ByteBuffer buf, int value) {
/* 148 */     buf.put((byte)(value >> 8 & 0xFF));
/* 149 */     buf.put((byte)(value & 0xFF));
/*     */   }
/*     */   
/*     */   private static void putString(ByteBuffer buf, String value) {
/* 153 */     int length = value.length();
/* 154 */     putInt(buf, length);
/* 155 */     for (int i = 0; i < length; i++) {
/* 156 */       char c = value.charAt(i);
/* 157 */       if (c != '\r' && c != '\n') {
/* 158 */         buf.put((byte)c);
/*     */       } else {
/* 160 */         buf.put((byte)32);
/*     */       } 
/*     */     } 
/* 163 */     buf.put((byte)0);
/*     */   }
/*     */   
/*     */   private void putHttpString(ByteBuffer buf, HttpString value) {
/* 167 */     int length = value.length();
/* 168 */     putInt(buf, length);
/* 169 */     value.appendTo(buf);
/* 170 */     buf.put((byte)0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void processAJPHeader() {
/* 179 */     int oldState = this.state;
/* 180 */     if (Bits.anyAreSet(oldState, 1)) {
/*     */       
/* 182 */       PooledByteBuffer[] byteBuffers = null;
/*     */ 
/*     */       
/* 185 */       Connectors.flattenCookies(this.exchange);
/*     */       
/* 187 */       PooledByteBuffer pooled = this.pool.allocate();
/* 188 */       ByteBuffer buffer = pooled.getBuffer();
/* 189 */       buffer.put((byte)65);
/* 190 */       buffer.put((byte)66);
/* 191 */       buffer.put((byte)0);
/* 192 */       buffer.put((byte)0);
/* 193 */       buffer.put((byte)4);
/* 194 */       putInt(buffer, this.exchange.getStatusCode());
/* 195 */       String reason = this.exchange.getReasonPhrase();
/* 196 */       if (reason == null) {
/* 197 */         reason = StatusCodes.getReason(this.exchange.getStatusCode());
/*     */       }
/* 199 */       if (reason.length() + 4 > buffer.remaining()) {
/* 200 */         pooled.close();
/* 201 */         throw UndertowMessages.MESSAGES.reasonPhraseToLargeForBuffer(reason);
/*     */       } 
/* 203 */       putString(buffer, reason);
/*     */       
/* 205 */       int headers = 0;
/*     */       
/* 207 */       HeaderMap responseHeaders = this.exchange.getResponseHeaders();
/* 208 */       for (HttpString name : responseHeaders.getHeaderNames()) {
/* 209 */         headers += responseHeaders.get(name).size();
/*     */       }
/*     */       
/* 212 */       putInt(buffer, headers);
/*     */ 
/*     */       
/* 215 */       for (HttpString header : responseHeaders.getHeaderNames()) {
/* 216 */         for (String headerValue : responseHeaders.get(header)) {
/* 217 */           if (buffer.remaining() < header.length() + headerValue.length() + 6) {
/*     */             
/* 219 */             buffer.flip();
/* 220 */             if (byteBuffers == null) {
/* 221 */               byteBuffers = new PooledByteBuffer[2];
/* 222 */               byteBuffers[0] = pooled;
/*     */             } else {
/* 224 */               PooledByteBuffer[] old = byteBuffers;
/* 225 */               byteBuffers = new PooledByteBuffer[old.length + 1];
/* 226 */               System.arraycopy(old, 0, byteBuffers, 0, old.length);
/*     */             } 
/* 228 */             pooled = this.pool.allocate();
/* 229 */             byteBuffers[byteBuffers.length - 1] = pooled;
/* 230 */             buffer = pooled.getBuffer();
/*     */           } 
/*     */           
/* 233 */           Integer headerCode = HEADER_MAP.get(header);
/* 234 */           if (headerCode != null) {
/* 235 */             putInt(buffer, headerCode.intValue());
/*     */           } else {
/* 237 */             putHttpString(buffer, header);
/*     */           } 
/* 239 */           putString(buffer, headerValue);
/*     */         } 
/*     */       } 
/* 242 */       if (byteBuffers == null) {
/* 243 */         int dataLength = buffer.position() - 4;
/* 244 */         buffer.put(2, (byte)(dataLength >> 8 & 0xFF));
/* 245 */         buffer.put(3, (byte)(dataLength & 0xFF));
/* 246 */         buffer.flip();
/* 247 */         queueFrame((AbstractFramedStreamSinkConduit.FrameCallBack)new AbstractFramedStreamSinkConduit.PooledBufferFrameCallback(pooled), new ByteBuffer[] { buffer });
/*     */       } else {
/* 249 */         ByteBuffer[] bufs = new ByteBuffer[byteBuffers.length];
/* 250 */         for (int i = 0; i < bufs.length; i++) {
/* 251 */           bufs[i] = byteBuffers[i].getBuffer();
/*     */         }
/* 253 */         int dataLength = (int)(Buffers.remaining((Buffer[])bufs) - 4L);
/* 254 */         bufs[0].put(2, (byte)(dataLength >> 8 & 0xFF));
/* 255 */         bufs[0].put(3, (byte)(dataLength & 0xFF));
/* 256 */         buffer.flip();
/* 257 */         queueFrame((AbstractFramedStreamSinkConduit.FrameCallBack)new AbstractFramedStreamSinkConduit.PooledBuffersFrameCallback(byteBuffers), bufs);
/*     */       } 
/* 259 */       this.state &= 0xFFFFFFFE;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void queueCloseFrames() {
/* 266 */     processAJPHeader();
/* 267 */     ByteBuffer buffer = this.exchange.isPersistent() ? CLOSE_FRAME_PERSISTENT.duplicate() : CLOSE_FRAME_NON_PERSISTENT.duplicate();
/* 268 */     queueFrame(null, new ByteBuffer[] { buffer });
/*     */   }
/*     */   
/*     */   private void queueRemainingBytes(ByteBuffer src, ByteBuffer[] buffers) {
/* 272 */     List<PooledByteBuffer> pools = new ArrayList<>(4);
/*     */     
/*     */     try {
/* 275 */       PooledByteBuffer newPooledBuffer = this.pool.allocate();
/* 276 */       pools.add(newPooledBuffer);
/* 277 */       while (src.remaining() > newPooledBuffer.getBuffer().remaining()) {
/* 278 */         ByteBuffer dupSrc = src.duplicate();
/* 279 */         dupSrc.limit(dupSrc.position() + newPooledBuffer.getBuffer().remaining());
/* 280 */         newPooledBuffer.getBuffer().put(dupSrc);
/* 281 */         src.position(dupSrc.position());
/* 282 */         newPooledBuffer.getBuffer().flip();
/* 283 */         newPooledBuffer = this.pool.allocate();
/* 284 */         pools.add(newPooledBuffer);
/*     */       } 
/* 286 */       newPooledBuffer.getBuffer().put(src);
/* 287 */       newPooledBuffer.getBuffer().flip();
/*     */       
/* 289 */       ByteBuffer[] savedBuffers = new ByteBuffer[pools.size() + 2];
/* 290 */       int i = 0;
/* 291 */       savedBuffers[i++] = buffers[0];
/* 292 */       for (PooledByteBuffer p : pools) {
/* 293 */         savedBuffers[i++] = p.getBuffer();
/*     */       }
/* 295 */       savedBuffers[i] = buffers[2];
/* 296 */       queueFrame((AbstractFramedStreamSinkConduit.FrameCallBack)new AbstractFramedStreamSinkConduit.PooledBuffersFrameCallback(pools.<PooledByteBuffer>toArray(new PooledByteBuffer[0])), savedBuffers);
/* 297 */     } catch (RuntimeException|Error e) {
/* 298 */       for (PooledByteBuffer p : pools) {
/* 299 */         p.close();
/*     */       }
/* 301 */       throw e;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 306 */     if (queuedDataLength() > 0L)
/*     */     {
/*     */       
/* 309 */       if (!flushQueuedData()) {
/* 310 */         return 0;
/*     */       }
/*     */     }
/* 313 */     processAJPHeader();
/* 314 */     if (this.headRequest) {
/* 315 */       int remaining = src.remaining();
/* 316 */       src.position(src.position() + remaining);
/* 317 */       return remaining;
/*     */     } 
/* 319 */     int limit = src.limit();
/*     */     try {
/* 321 */       int maxData = this.exchange.getConnection().getUndertowOptions().get(UndertowOptions.MAX_AJP_PACKET_SIZE, 8192) - 8;
/* 322 */       if (src.remaining() > maxData) {
/* 323 */         src.limit(src.position() + maxData);
/*     */       }
/* 325 */       int writeSize = src.remaining();
/* 326 */       ByteBuffer[] buffers = createHeader(src);
/* 327 */       int toWrite = 0;
/* 328 */       for (ByteBuffer buffer : buffers) {
/* 329 */         toWrite += buffer.remaining();
/*     */       }
/* 331 */       int originalPayloadSize = writeSize;
/* 332 */       long r = 0L;
/*     */       while (true)
/* 334 */       { r = super.write(buffers, 0, buffers.length);
/* 335 */         toWrite = (int)(toWrite - r);
/* 336 */         if (r == -1L)
/* 337 */           throw new ClosedChannelException(); 
/* 338 */         if (r == 0L) {
/*     */           
/* 340 */           queueRemainingBytes(src, buffers);
/* 341 */           return originalPayloadSize;
/*     */         } 
/* 343 */         if (toWrite <= 0)
/* 344 */           return originalPayloadSize;  } 
/* 345 */     } catch (IOException|RuntimeException e) {
/* 346 */       IoUtils.safeClose((Closeable)this.exchange.getConnection());
/* 347 */       throw e;
/*     */     } finally {
/* 349 */       src.limit(limit);
/*     */     } 
/*     */   }
/*     */   
/*     */   private ByteBuffer[] createHeader(ByteBuffer src) {
/* 354 */     int remaining = src.remaining();
/* 355 */     int chunkSize = remaining + 4;
/* 356 */     byte[] header = new byte[7];
/* 357 */     header[0] = 65;
/* 358 */     header[1] = 66;
/* 359 */     header[2] = (byte)(chunkSize >> 8 & 0xFF);
/* 360 */     header[3] = (byte)(chunkSize & 0xFF);
/* 361 */     header[4] = 3;
/* 362 */     header[5] = (byte)(remaining >> 8 & 0xFF);
/* 363 */     header[6] = (byte)(remaining & 0xFF);
/*     */     
/* 365 */     byte[] footer = new byte[1];
/* 366 */     footer[0] = 0;
/*     */     
/* 368 */     ByteBuffer[] buffers = new ByteBuffer[3];
/* 369 */     buffers[0] = ByteBuffer.wrap(header);
/* 370 */     buffers[1] = src;
/* 371 */     buffers[2] = ByteBuffer.wrap(footer);
/* 372 */     return buffers;
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs) throws IOException {
/* 376 */     return write(srcs, 0, srcs.length);
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 380 */     long total = 0L;
/* 381 */     for (int i = offset; i < offset + length; i++) {
/* 382 */       while (srcs[i].hasRemaining()) {
/* 383 */         int written = write(srcs[i]);
/* 384 */         if (written == 0) {
/* 385 */           return total;
/*     */         }
/* 387 */         total += written;
/*     */       } 
/*     */     } 
/* 390 */     return total;
/*     */   }
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 394 */     return src.transferTo(position, count, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 398 */     return IoUtils.transfer((ReadableByteChannel)source, count, throughBuffer, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void finished() {
/* 403 */     if (this.finishListener != null) {
/* 404 */       this.finishListener.handleEvent((Conduit)this);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWriteReadyHandler(WriteReadyHandler handler) {
/* 410 */     ((StreamSinkConduit)this.next).setWriteReadyHandler(new AjpServerWriteReadyHandler(handler));
/*     */   }
/*     */   
/*     */   public void suspendWrites() {
/* 414 */     log.trace("suspend");
/* 415 */     this.state &= 0xFFFFFFFB;
/* 416 */     if (Bits.allAreClear(this.state, 8)) {
/* 417 */       ((StreamSinkConduit)this.next).suspendWrites();
/*     */     }
/*     */   }
/*     */   
/*     */   public void resumeWrites() {
/* 422 */     log.trace("resume");
/* 423 */     this.state |= 0x4;
/* 424 */     ((StreamSinkConduit)this.next).resumeWrites();
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/* 428 */     processAJPHeader();
/* 429 */     if (Bits.allAreClear(this.state, 64) && !isWritesTerminated()) {
/* 430 */       queueFrame(new AbstractFramedStreamSinkConduit.FrameCallBack()
/*     */           {
/*     */             public void done() {
/* 433 */               AjpServerResponseConduit.this.state = AjpServerResponseConduit.this.state & 0xFFFFFFBF;
/*     */             }
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             public void failed(IOException e) {}
/* 440 */           },  new ByteBuffer[] { FLUSH_PACKET.duplicate() });
/* 441 */       this.state |= 0x40;
/*     */     } 
/* 443 */     return flushQueuedData();
/*     */   }
/*     */   public boolean isWriteResumed() {
/* 446 */     return Bits.anyAreSet(this.state, 4);
/*     */   }
/*     */   
/*     */   public void wakeupWrites() {
/* 450 */     log.trace("wakeup");
/* 451 */     this.state |= 0x4;
/* 452 */     ((StreamSinkConduit)this.next).wakeupWrites();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doTerminateWrites() throws IOException {
/*     */     try {
/* 458 */       if (!this.exchange.isPersistent()) {
/* 459 */         ((StreamSinkConduit)this.next).terminateWrites();
/*     */       }
/* 461 */       this.state |= 0x10;
/* 462 */     } catch (IOException|RuntimeException e) {
/* 463 */       IoUtils.safeClose((Closeable)this.exchange.getConnection());
/* 464 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWriteShutdown() {
/* 470 */     return (super.isWriteShutdown() || Bits.anyAreSet(this.state, 16));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean doGetRequestBodyChunk(ByteBuffer buffer, final AjpServerRequestConduit requestChannel) throws IOException {
/* 476 */     if (isWriteShutdown()) {
/* 477 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 479 */     super.write(buffer);
/* 480 */     if (buffer.hasRemaining()) {
/*     */       
/* 482 */       this.state |= 0x8;
/* 483 */       queueFrame(new AbstractFramedStreamSinkConduit.FrameCallBack()
/*     */           {
/*     */             public void done()
/*     */             {
/* 487 */               AjpServerResponseConduit.this.state = AjpServerResponseConduit.this.state & 0xFFFFFFF7;
/* 488 */               if (Bits.allAreClear(AjpServerResponseConduit.this.state, 4)) {
/* 489 */                 ((StreamSinkConduit)AjpServerResponseConduit.this.next).suspendWrites();
/*     */               }
/*     */             }
/*     */ 
/*     */             
/*     */             public void failed(IOException e) {
/* 495 */               requestChannel.setReadBodyChunkError(e);
/*     */             }
/*     */           }new ByteBuffer[] { buffer });
/* 498 */       ((StreamSinkConduit)this.next).resumeWrites();
/* 499 */       return false;
/*     */     } 
/* 501 */     return true;
/*     */   }
/*     */   
/*     */   private final class AjpServerWriteReadyHandler
/*     */     implements WriteReadyHandler {
/*     */     private final WriteReadyHandler delegate;
/*     */     
/*     */     private AjpServerWriteReadyHandler(WriteReadyHandler delegate) {
/* 509 */       this.delegate = delegate;
/*     */     }
/*     */ 
/*     */     
/*     */     public void writeReady() {
/* 514 */       if (Bits.anyAreSet(AjpServerResponseConduit.this.state, 8)) {
/*     */         try {
/* 516 */           AjpServerResponseConduit.this.flushQueuedData();
/* 517 */         } catch (IOException e) {
/* 518 */           AjpServerResponseConduit.log.debug("Error flushing when doing async READ_BODY_CHUNK flush", e);
/*     */         } 
/*     */       }
/* 521 */       if (Bits.anyAreSet(AjpServerResponseConduit.this.state, 4)) {
/* 522 */         this.delegate.writeReady();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void forceTermination() {
/* 528 */       this.delegate.forceTermination();
/*     */     }
/*     */ 
/*     */     
/*     */     public void terminated() {
/* 533 */       this.delegate.terminated();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\ajp\AjpServerResponseConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */