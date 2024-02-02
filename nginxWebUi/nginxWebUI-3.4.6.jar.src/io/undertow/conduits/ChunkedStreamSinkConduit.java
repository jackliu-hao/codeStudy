/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.protocol.http.HttpAttachments;
/*     */ import io.undertow.util.Attachable;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.ImmediatePooledByteBuffer;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Collection;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.function.Supplier;
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
/*     */ public class ChunkedStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*     */   @Deprecated
/*  65 */   public static final AttachmentKey<HeaderMap> TRAILERS = HttpAttachments.RESPONSE_TRAILERS;
/*     */ 
/*     */   
/*     */   private final HeaderMap responseHeaders;
/*     */ 
/*     */   
/*     */   private final ConduitListener<? super ChunkedStreamSinkConduit> finishListener;
/*     */   
/*     */   private final int config;
/*     */   
/*     */   private final ByteBufferPool bufferPool;
/*     */   
/*  77 */   private static final byte[] LAST_CHUNK = new byte[] { 48, 13, 10 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  82 */   private static final byte[] CRLF = new byte[] { 13, 10 };
/*     */   
/*     */   private final Attachable attachable;
/*     */   private int state;
/*  86 */   private int chunkleft = 0;
/*     */   
/*  88 */   private final ByteBuffer chunkingBuffer = ByteBuffer.allocate(12);
/*     */ 
/*     */   
/*     */   private final ByteBuffer chunkingSepBuffer;
/*     */ 
/*     */   
/*     */   private PooledByteBuffer lastChunkBuffer;
/*     */ 
/*     */   
/*     */   private static final int CONF_FLAG_CONFIGURABLE = 1;
/*     */ 
/*     */   
/*     */   private static final int CONF_FLAG_PASS_CLOSE = 2;
/*     */ 
/*     */   
/*     */   private static final int FLAG_WRITES_SHUTDOWN = 1;
/*     */ 
/*     */   
/*     */   private static final int FLAG_NEXT_SHUTDOWN = 4;
/*     */   
/*     */   private static final int FLAG_WRITTEN_FIRST_CHUNK = 8;
/*     */   
/*     */   private static final int FLAG_FIRST_DATA_WRITTEN = 16;
/*     */   
/*     */   private static final int FLAG_FINISHED = 32;
/*     */ 
/*     */   
/*     */   public ChunkedStreamSinkConduit(StreamSinkConduit next, ByteBufferPool bufferPool, boolean configurable, boolean passClose, HeaderMap responseHeaders, ConduitListener<? super ChunkedStreamSinkConduit> finishListener, Attachable attachable) {
/* 116 */     super(next);
/* 117 */     this.bufferPool = bufferPool;
/* 118 */     this.responseHeaders = responseHeaders;
/* 119 */     this.finishListener = finishListener;
/* 120 */     this.attachable = attachable;
/* 121 */     this.config = (configurable ? 1 : 0) | (passClose ? 2 : 0);
/* 122 */     this.chunkingSepBuffer = ByteBuffer.allocate(2);
/* 123 */     this.chunkingSepBuffer.flip();
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 128 */     return doWrite(src);
/*     */   }
/*     */ 
/*     */   
/*     */   int doWrite(ByteBuffer src) throws IOException {
/* 133 */     if (Bits.anyAreSet(this.state, 1)) {
/* 134 */       throw new ClosedChannelException();
/*     */     }
/* 136 */     if (src.remaining() == 0) {
/* 137 */       return 0;
/*     */     }
/* 139 */     this.state |= 0x10;
/* 140 */     int oldLimit = src.limit();
/* 141 */     boolean dataRemaining = false;
/* 142 */     if (this.chunkleft == 0 && !this.chunkingSepBuffer.hasRemaining()) {
/* 143 */       this.chunkingBuffer.clear();
/* 144 */       putIntAsHexString(this.chunkingBuffer, src.remaining());
/* 145 */       this.chunkingBuffer.put(CRLF);
/* 146 */       this.chunkingBuffer.flip();
/* 147 */       this.chunkingSepBuffer.clear();
/* 148 */       this.chunkingSepBuffer.put(CRLF);
/* 149 */       this.chunkingSepBuffer.flip();
/* 150 */       this.state |= 0x8;
/* 151 */       this.chunkleft = src.remaining();
/*     */     }
/* 153 */     else if (src.remaining() > this.chunkleft) {
/* 154 */       dataRemaining = true;
/* 155 */       src.limit(this.chunkleft + src.position());
/*     */     } 
/*     */     
/*     */     try {
/* 159 */       int chunkingSize = this.chunkingBuffer.remaining();
/* 160 */       int chunkingSepSize = this.chunkingSepBuffer.remaining();
/* 161 */       if (chunkingSize > 0 || chunkingSepSize > 0 || this.lastChunkBuffer != null) {
/* 162 */         long l; int originalRemaining = src.remaining();
/*     */         
/* 164 */         if (this.lastChunkBuffer == null || dataRemaining) {
/* 165 */           ByteBuffer[] buf = { this.chunkingBuffer, src, this.chunkingSepBuffer };
/* 166 */           l = ((StreamSinkConduit)this.next).write(buf, 0, buf.length);
/*     */         } else {
/* 168 */           ByteBuffer[] buf = { this.chunkingBuffer, src, this.lastChunkBuffer.getBuffer() };
/* 169 */           if (Bits.anyAreSet(this.state, 2)) {
/* 170 */             l = ((StreamSinkConduit)this.next).writeFinal(buf, 0, buf.length);
/*     */           } else {
/* 172 */             l = ((StreamSinkConduit)this.next).write(buf, 0, buf.length);
/*     */           } 
/* 174 */           if (!src.hasRemaining()) {
/* 175 */             this.state |= 0x1;
/*     */           }
/* 177 */           if (!this.lastChunkBuffer.getBuffer().hasRemaining()) {
/* 178 */             this.state |= 0x4;
/* 179 */             this.lastChunkBuffer.close();
/*     */           } 
/*     */         } 
/* 182 */         int srcWritten = originalRemaining - src.remaining();
/* 183 */         this.chunkleft -= srcWritten;
/* 184 */         if (l < chunkingSize) {
/* 185 */           return 0;
/*     */         }
/* 187 */         return srcWritten;
/*     */       } 
/*     */       
/* 190 */       int result = ((StreamSinkConduit)this.next).write(src);
/* 191 */       this.chunkleft -= result;
/* 192 */       return result;
/*     */     }
/*     */     finally {
/*     */       
/* 196 */       src.limit(oldLimit);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void truncateWrites() throws IOException {
/*     */     try {
/* 204 */       if (this.lastChunkBuffer != null) {
/* 205 */         this.lastChunkBuffer.close();
/*     */       }
/* 207 */       if (Bits.allAreClear(this.state, 32)) {
/* 208 */         invokeFinishListener();
/*     */       }
/*     */     } finally {
/* 211 */       super.truncateWrites();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 217 */     for (int i = offset; i < length; i++) {
/* 218 */       if (srcs[i].hasRemaining()) {
/* 219 */         return write(srcs[i]);
/*     */       }
/*     */     } 
/* 222 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 227 */     return Conduits.writeFinalBasic((StreamSinkConduit)this, srcs, offset, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 233 */     if (!src.hasRemaining()) {
/* 234 */       terminateWrites();
/* 235 */       return 0;
/*     */     } 
/* 237 */     if (this.lastChunkBuffer == null) {
/* 238 */       createLastChunk(true);
/*     */     }
/* 240 */     return doWrite(src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 245 */     if (Bits.anyAreSet(this.state, 1)) {
/* 246 */       throw new ClosedChannelException();
/*     */     }
/* 248 */     return src.transferTo(position, count, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 253 */     if (Bits.anyAreSet(this.state, 1)) {
/* 254 */       throw new ClosedChannelException();
/*     */     }
/* 256 */     return IoUtils.transfer((ReadableByteChannel)source, count, throughBuffer, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean flush() throws IOException {
/* 261 */     this.state |= 0x10;
/* 262 */     if (Bits.anyAreSet(this.state, 1)) {
/* 263 */       if (Bits.anyAreSet(this.state, 4)) {
/* 264 */         boolean val = ((StreamSinkConduit)this.next).flush();
/* 265 */         if (val && Bits.allAreClear(this.state, 32)) {
/* 266 */           invokeFinishListener();
/*     */         }
/* 268 */         return val;
/*     */       } 
/* 270 */       ((StreamSinkConduit)this.next).write(this.lastChunkBuffer.getBuffer());
/* 271 */       if (!this.lastChunkBuffer.getBuffer().hasRemaining()) {
/* 272 */         this.lastChunkBuffer.close();
/* 273 */         if (Bits.anyAreSet(this.config, 2)) {
/* 274 */           ((StreamSinkConduit)this.next).terminateWrites();
/*     */         }
/* 276 */         this.state |= 0x4;
/* 277 */         boolean val = ((StreamSinkConduit)this.next).flush();
/* 278 */         if (val && Bits.allAreClear(this.state, 32)) {
/* 279 */           invokeFinishListener();
/*     */         }
/* 281 */         return val;
/*     */       } 
/* 283 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 287 */     return ((StreamSinkConduit)this.next).flush();
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeFinishListener() {
/* 292 */     this.state |= 0x20;
/* 293 */     if (this.finishListener != null) {
/* 294 */       this.finishListener.handleEvent(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 300 */     if (Bits.anyAreSet(this.state, 1)) {
/*     */       return;
/*     */     }
/* 303 */     if (this.chunkleft != 0) {
/* 304 */       UndertowLogger.REQUEST_IO_LOGGER.debugf("Channel closed mid-chunk", new Object[0]);
/* 305 */       ((StreamSinkConduit)this.next).truncateWrites();
/*     */     } 
/* 307 */     if (!Bits.anyAreSet(this.state, 16)) {
/*     */ 
/*     */ 
/*     */       
/* 311 */       this.responseHeaders.put(Headers.CONTENT_LENGTH, "0");
/* 312 */       this.responseHeaders.remove(Headers.TRANSFER_ENCODING);
/* 313 */       this.state |= 0x5;
/* 314 */       if (Bits.anyAreSet(this.state, 2)) {
/* 315 */         ((StreamSinkConduit)this.next).terminateWrites();
/*     */       }
/*     */     } else {
/* 318 */       createLastChunk(false);
/* 319 */       this.state |= 0x1;
/*     */     } 
/*     */   }
/*     */   private void createLastChunk(boolean writeFinal) throws UnsupportedEncodingException {
/*     */     HeaderMap trailers;
/* 324 */     PooledByteBuffer lastChunkBufferPooled = this.bufferPool.allocate();
/* 325 */     ByteBuffer lastChunkBuffer = lastChunkBufferPooled.getBuffer();
/* 326 */     if (writeFinal) {
/* 327 */       lastChunkBuffer.put(CRLF);
/* 328 */     } else if (this.chunkingSepBuffer.hasRemaining()) {
/*     */ 
/*     */       
/* 331 */       lastChunkBuffer.put(this.chunkingSepBuffer);
/*     */     } 
/* 333 */     lastChunkBuffer.put(LAST_CHUNK);
/*     */     
/* 335 */     HeaderMap attachment = (HeaderMap)this.attachable.getAttachment(HttpAttachments.RESPONSE_TRAILERS);
/*     */     
/* 337 */     Supplier<HeaderMap> supplier = (Supplier<HeaderMap>)this.attachable.getAttachment(HttpAttachments.RESPONSE_TRAILER_SUPPLIER);
/* 338 */     if (attachment != null && supplier == null) {
/* 339 */       trailers = attachment;
/* 340 */     } else if (attachment == null && supplier != null) {
/* 341 */       trailers = supplier.get();
/* 342 */     } else if (attachment != null) {
/* 343 */       HeaderMap supplied = supplier.get();
/* 344 */       for (HeaderValues k : supplied) {
/* 345 */         attachment.putAll(k.getHeaderName(), (Collection)k);
/*     */       }
/* 347 */       trailers = attachment;
/*     */     } else {
/* 349 */       trailers = null;
/*     */     } 
/* 351 */     if (trailers != null && trailers.size() != 0) {
/* 352 */       for (HeaderValues trailer : trailers) {
/* 353 */         for (String val : trailer) {
/* 354 */           trailer.getHeaderName().appendTo(lastChunkBuffer);
/* 355 */           lastChunkBuffer.put((byte)58);
/* 356 */           lastChunkBuffer.put((byte)32);
/* 357 */           lastChunkBuffer.put(val.getBytes(StandardCharsets.US_ASCII));
/* 358 */           lastChunkBuffer.put(CRLF);
/*     */         } 
/*     */       } 
/* 361 */       lastChunkBuffer.put(CRLF);
/*     */     } else {
/* 363 */       lastChunkBuffer.put(CRLF);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 368 */     lastChunkBuffer.flip();
/* 369 */     ByteBuffer data = ByteBuffer.allocate(lastChunkBuffer.remaining());
/* 370 */     data.put(lastChunkBuffer);
/* 371 */     data.flip();
/* 372 */     this.lastChunkBuffer = (PooledByteBuffer)new ImmediatePooledByteBuffer(data);
/*     */     
/* 374 */     lastChunkBufferPooled.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 379 */     ((StreamSinkConduit)this.next).awaitWritable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 384 */     ((StreamSinkConduit)this.next).awaitWritable(time, timeUnit);
/*     */   }
/*     */   
/*     */   private static void putIntAsHexString(ByteBuffer buf, int v) {
/* 388 */     byte int3 = (byte)(v >> 24);
/* 389 */     byte int2 = (byte)(v >> 16);
/* 390 */     byte int1 = (byte)(v >> 8);
/* 391 */     byte int0 = (byte)v;
/* 392 */     boolean nonZeroFound = false;
/* 393 */     if (int3 != 0) {
/* 394 */       buf.put(DIGITS[(0xF0 & int3) >>> 4])
/* 395 */         .put(DIGITS[0xF & int3]);
/* 396 */       nonZeroFound = true;
/*     */     } 
/* 398 */     if (nonZeroFound || int2 != 0) {
/* 399 */       buf.put(DIGITS[(0xF0 & int2) >>> 4])
/* 400 */         .put(DIGITS[0xF & int2]);
/* 401 */       nonZeroFound = true;
/*     */     } 
/* 403 */     if (nonZeroFound || int1 != 0) {
/* 404 */       buf.put(DIGITS[(0xF0 & int1) >>> 4])
/* 405 */         .put(DIGITS[0xF & int1]);
/*     */     }
/* 407 */     buf.put(DIGITS[(0xF0 & int0) >>> 4])
/* 408 */       .put(DIGITS[0xF & int0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 414 */   private static final byte[] DIGITS = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\ChunkedStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */