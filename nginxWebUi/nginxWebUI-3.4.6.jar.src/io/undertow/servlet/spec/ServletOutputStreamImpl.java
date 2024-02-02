/*     */ package io.undertow.servlet.spec;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.io.BufferWritableOutputStream;
/*     */ import io.undertow.server.protocol.http.HttpAttachments;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import io.undertow.servlet.handlers.ServletRequestContext;
/*     */ import io.undertow.util.Headers;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.GatheringByteChannel;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import javax.servlet.DispatcherType;
/*     */ import javax.servlet.ServletOutputStream;
/*     */ import javax.servlet.WriteListener;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.Channels;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.SuspendableWriteChannel;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServletOutputStreamImpl
/*     */   extends ServletOutputStream
/*     */   implements BufferWritableOutputStream
/*     */ {
/*     */   private final ServletRequestContext servletRequestContext;
/*     */   private PooledByteBuffer pooledBuffer;
/*     */   private ByteBuffer buffer;
/*     */   private Integer bufferSize;
/*     */   private StreamSinkChannel channel;
/*     */   private long written;
/*     */   private volatile int state;
/*     */   private volatile boolean asyncIoStarted;
/*     */   private AsyncContextImpl asyncContext;
/*     */   private WriteListener listener;
/*     */   private WriteChannelListener internalListener;
/*     */   private ByteBuffer[] buffersToWrite;
/*     */   private FileChannel pendingFile;
/*     */   private static final int FLAG_CLOSED = 1;
/*     */   private static final int FLAG_WRITE_STARTED = 2;
/*     */   private static final int FLAG_READY = 4;
/*     */   private static final int FLAG_DELEGATE_SHUTDOWN = 8;
/*     */   private static final int FLAG_IN_CALLBACK = 16;
/*     */   private static final int MAX_BUFFERS_TO_ALLOCATE = 6;
/* 101 */   private static final AtomicIntegerFieldUpdater<ServletOutputStreamImpl> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(ServletOutputStreamImpl.class, "state");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletOutputStreamImpl(ServletRequestContext servletRequestContext) {
/* 108 */     this.servletRequestContext = servletRequestContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletOutputStreamImpl(ServletRequestContext servletRequestContext, int bufferSize) {
/* 115 */     this.bufferSize = Integer.valueOf(bufferSize);
/* 116 */     this.servletRequestContext = servletRequestContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 123 */     write(new byte[] { (byte)b }, 0, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/* 130 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 137 */     if (Bits.anyAreSet(this.state, 1) || this.servletRequestContext.getOriginalResponse().isTreatAsCommitted()) {
/* 138 */       throw UndertowServletMessages.MESSAGES.streamIsClosed();
/*     */     }
/* 140 */     if (len < 1) {
/*     */       return;
/*     */     }
/*     */     
/* 144 */     if (this.listener == null) {
/* 145 */       ByteBuffer buffer = buffer();
/* 146 */       if (buffer.remaining() < len) {
/* 147 */         writeTooLargeForBuffer(b, off, len, buffer);
/*     */       } else {
/* 149 */         buffer.put(b, off, len);
/* 150 */         if (buffer.remaining() == 0) {
/* 151 */           writeBufferBlocking(false);
/*     */         }
/*     */       } 
/* 154 */       updateWritten(len);
/*     */     } else {
/* 156 */       writeAsync(b, off, len);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeTooLargeForBuffer(byte[] b, int off, int len, ByteBuffer buffer) throws IOException {
/* 166 */     StreamSinkChannel channel = this.channel;
/* 167 */     if (channel == null) {
/* 168 */       this.channel = channel = this.servletRequestContext.getExchange().getResponseChannel();
/*     */     }
/* 170 */     ByteBufferPool bufferPool = this.servletRequestContext.getExchange().getConnection().getByteBufferPool();
/* 171 */     ByteBuffer[] buffers = new ByteBuffer[7];
/* 172 */     PooledByteBuffer[] pooledBuffers = new PooledByteBuffer[6];
/*     */     try {
/* 174 */       buffers[0] = buffer;
/* 175 */       int bytesWritten = 0;
/* 176 */       int rem = buffer.remaining();
/* 177 */       buffer.put(b, bytesWritten + off, rem);
/* 178 */       buffer.flip();
/* 179 */       bytesWritten += rem;
/* 180 */       int bufferCount = 1; int j;
/* 181 */       for (j = 0; j < 6; j++) {
/* 182 */         PooledByteBuffer pooled = bufferPool.allocate();
/* 183 */         pooledBuffers[bufferCount - 1] = pooled;
/* 184 */         buffers[bufferCount++] = pooled.getBuffer();
/* 185 */         ByteBuffer cb = pooled.getBuffer();
/* 186 */         int toWrite = len - bytesWritten;
/* 187 */         if (toWrite > cb.remaining()) {
/* 188 */           rem = cb.remaining();
/* 189 */           cb.put(b, bytesWritten + off, rem);
/* 190 */           cb.flip();
/* 191 */           bytesWritten += rem;
/*     */         } else {
/* 193 */           cb.put(b, bytesWritten + off, toWrite);
/* 194 */           bytesWritten = len;
/* 195 */           cb.flip();
/*     */           break;
/*     */         } 
/*     */       } 
/* 199 */       Channels.writeBlocking((GatheringByteChannel)channel, buffers, 0, bufferCount);
/* 200 */       while (bytesWritten < len) {
/*     */         
/* 202 */         bufferCount = 0;
/* 203 */         for (j = 0; j < 7; j++) {
/* 204 */           ByteBuffer cb = buffers[j];
/* 205 */           cb.clear();
/* 206 */           bufferCount++;
/* 207 */           int toWrite = len - bytesWritten;
/* 208 */           if (toWrite > cb.remaining()) {
/* 209 */             rem = cb.remaining();
/* 210 */             cb.put(b, bytesWritten + off, rem);
/* 211 */             cb.flip();
/* 212 */             bytesWritten += rem;
/*     */           } else {
/* 214 */             cb.put(b, bytesWritten + off, toWrite);
/* 215 */             bytesWritten = len;
/* 216 */             cb.flip();
/*     */             break;
/*     */           } 
/*     */         } 
/* 220 */         Channels.writeBlocking((GatheringByteChannel)channel, buffers, 0, bufferCount);
/*     */       } 
/* 222 */       buffer.clear();
/*     */     } finally {
/* 224 */       for (int i = 0; i < pooledBuffers.length; i++) {
/* 225 */         PooledByteBuffer p = pooledBuffers[i];
/* 226 */         if (p == null) {
/*     */           break;
/*     */         }
/* 229 */         p.close();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeAsync(byte[] b, int off, int len) throws IOException {
/* 235 */     if (Bits.anyAreClear(this.state, 4)) {
/* 236 */       throw UndertowServletMessages.MESSAGES.streamNotReady();
/*     */     }
/*     */ 
/*     */     
/* 240 */     try { ByteBuffer buffer = buffer();
/* 241 */       if (buffer.remaining() > len)
/* 242 */       { buffer.put(b, off, len); }
/*     */       else
/* 244 */       { buffer.flip();
/* 245 */         ByteBuffer userBuffer = ByteBuffer.wrap(b, off, len);
/* 246 */         ByteBuffer[] bufs = { buffer, userBuffer };
/* 247 */         long toWrite = Buffers.remaining((Buffer[])bufs);
/*     */         
/* 249 */         long written = 0L;
/* 250 */         createChannel();
/* 251 */         setFlags(2);
/*     */         while (true)
/* 253 */         { long res = this.channel.write(bufs);
/* 254 */           written += res;
/* 255 */           if (res == 0L) {
/*     */ 
/*     */             
/* 258 */             ByteBuffer copy = ByteBuffer.allocate(userBuffer.remaining());
/* 259 */             copy.put(userBuffer);
/* 260 */             copy.flip();
/*     */             
/* 262 */             this.buffersToWrite = new ByteBuffer[] { buffer, copy };
/* 263 */             clearFlags(4);
/*     */             return;
/*     */           } 
/* 266 */           if (written >= toWrite) {
/* 267 */             buffer.clear();
/*     */           } else {
/*     */             continue;
/* 270 */           }  updateWrittenAsync(len); }  }  } finally { updateWrittenAsync(len); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(ByteBuffer[] buffers) throws IOException {
/* 277 */     if (Bits.anyAreSet(this.state, 1) || this.servletRequestContext.getOriginalResponse().isTreatAsCommitted()) {
/* 278 */       throw UndertowServletMessages.MESSAGES.streamIsClosed();
/*     */     }
/* 280 */     int len = 0;
/* 281 */     for (ByteBuffer buf : buffers) {
/* 282 */       len += buf.remaining();
/*     */     }
/* 284 */     if (len < 1) {
/*     */       return;
/*     */     }
/*     */     
/* 288 */     if (this.listener == null) {
/*     */ 
/*     */       
/* 291 */       if (this.written == 0L && len == this.servletRequestContext.getOriginalResponse().getContentLength()) {
/* 292 */         if (this.channel == null) {
/* 293 */           this.channel = this.servletRequestContext.getExchange().getResponseChannel();
/*     */         }
/* 295 */         Channels.writeBlocking((GatheringByteChannel)this.channel, buffers, 0, buffers.length);
/* 296 */         setFlags(2);
/*     */       } else {
/* 298 */         ByteBuffer buffer = buffer();
/* 299 */         if (len < buffer.remaining()) {
/* 300 */           Buffers.copy(buffer, buffers, 0, buffers.length);
/*     */         } else {
/* 302 */           if (this.channel == null) {
/* 303 */             this.channel = this.servletRequestContext.getExchange().getResponseChannel();
/*     */           }
/* 305 */           if (buffer.position() == 0) {
/* 306 */             Channels.writeBlocking((GatheringByteChannel)this.channel, buffers, 0, buffers.length);
/*     */           } else {
/* 308 */             ByteBuffer[] newBuffers = new ByteBuffer[buffers.length + 1];
/* 309 */             buffer.flip();
/* 310 */             newBuffers[0] = buffer;
/* 311 */             System.arraycopy(buffers, 0, newBuffers, 1, buffers.length);
/* 312 */             Channels.writeBlocking((GatheringByteChannel)this.channel, newBuffers, 0, newBuffers.length);
/* 313 */             buffer.clear();
/*     */           } 
/* 315 */           setFlags(2);
/*     */         } 
/*     */       } 
/*     */       
/* 319 */       updateWritten(len);
/*     */     } else {
/* 321 */       if (Bits.anyAreClear(this.state, 4)) {
/* 322 */         throw UndertowServletMessages.MESSAGES.streamNotReady();
/*     */       }
/*     */ 
/*     */       
/* 326 */       try { ByteBuffer buffer = buffer();
/* 327 */         if (buffer.remaining() > len)
/* 328 */         { Buffers.copy(buffer, buffers, 0, buffers.length); }
/*     */         else
/* 330 */         { ByteBuffer[] bufs = new ByteBuffer[buffers.length + 1];
/* 331 */           buffer.flip();
/* 332 */           bufs[0] = buffer;
/* 333 */           System.arraycopy(buffers, 0, bufs, 1, buffers.length);
/* 334 */           long toWrite = Buffers.remaining((Buffer[])bufs);
/*     */           
/* 336 */           long written = 0L;
/* 337 */           createChannel();
/* 338 */           setFlags(2);
/*     */           while (true)
/* 340 */           { long res = this.channel.write(bufs);
/* 341 */             written += res;
/* 342 */             if (res == 0L) {
/*     */ 
/*     */ 
/*     */               
/* 346 */               ByteBuffer copy = ByteBuffer.allocate((int)Buffers.remaining((Buffer[])buffers));
/* 347 */               Buffers.copy(copy, buffers, 0, buffers.length);
/* 348 */               copy.flip();
/* 349 */               this.buffersToWrite = new ByteBuffer[] { buffer, copy };
/* 350 */               clearFlags(4);
/* 351 */               this.channel.resumeWrites();
/*     */               return;
/*     */             } 
/* 354 */             if (written >= toWrite) {
/* 355 */               buffer.clear();
/*     */             } else {
/*     */               continue;
/* 358 */             }  updateWrittenAsync(len); }  }  } finally { updateWrittenAsync(len); }
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(ByteBuffer byteBuffer) throws IOException {
/* 365 */     write(new ByteBuffer[] { byteBuffer });
/*     */   }
/*     */   
/*     */   void updateWritten(long len) throws IOException {
/* 369 */     this.written += len;
/* 370 */     long contentLength = this.servletRequestContext.getOriginalResponse().getContentLength();
/* 371 */     if (contentLength != -1L && this.written >= contentLength) {
/* 372 */       close();
/*     */     }
/*     */   }
/*     */   
/*     */   void updateWrittenAsync(long len) throws IOException {
/* 377 */     this.written += len;
/* 378 */     long contentLength = this.servletRequestContext.getOriginalResponse().getContentLength();
/* 379 */     if (contentLength != -1L && this.written >= contentLength) {
/* 380 */       setFlags(1);
/*     */ 
/*     */       
/* 383 */       if (this.buffersToWrite == null && this.pendingFile == null && 
/* 384 */         flushBufferAsync(true)) {
/* 385 */         this.channel.shutdownWrites();
/* 386 */         setFlags(8);
/* 387 */         this.channel.flush();
/* 388 */         if (this.pooledBuffer != null) {
/* 389 */           this.pooledBuffer.close();
/* 390 */           this.buffer = null;
/* 391 */           this.pooledBuffer = null;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean flushBufferAsync(boolean writeFinal) throws IOException {
/* 400 */     ByteBuffer[] bufs = this.buffersToWrite;
/* 401 */     if (bufs == null) {
/* 402 */       ByteBuffer buffer = this.buffer;
/* 403 */       if (buffer == null || buffer.position() == 0) {
/* 404 */         return true;
/*     */       }
/* 406 */       buffer.flip();
/* 407 */       bufs = new ByteBuffer[] { buffer };
/*     */     } 
/* 409 */     long toWrite = Buffers.remaining((Buffer[])bufs);
/* 410 */     if (toWrite == 0L) {
/*     */       
/* 412 */       this.buffer.clear();
/* 413 */       return true;
/*     */     } 
/* 415 */     setFlags(2);
/* 416 */     createChannel();
/*     */     
/* 418 */     long written = 0L; while (true) {
/*     */       long res;
/* 420 */       if (writeFinal) {
/* 421 */         res = this.channel.writeFinal(bufs);
/*     */       } else {
/* 423 */         res = this.channel.write(bufs);
/*     */       } 
/* 425 */       written += res;
/* 426 */       if (res == 0L) {
/*     */         
/* 428 */         clearFlags(4);
/* 429 */         this.buffersToWrite = bufs;
/* 430 */         this.channel.resumeWrites();
/* 431 */         return false;
/*     */       } 
/* 433 */       if (written >= toWrite) {
/* 434 */         this.buffer.clear();
/* 435 */         return true;
/*     */       } 
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
/*     */ 
/*     */ 
/*     */   
/*     */   ByteBuffer underlyingBuffer() {
/* 453 */     if (Bits.anyAreSet(this.state, 1)) {
/* 454 */       return null;
/*     */     }
/* 456 */     return buffer();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 464 */     if (this.servletRequestContext.getOriginalRequest().getDispatcherType() == DispatcherType.INCLUDE || this.servletRequestContext
/* 465 */       .getOriginalResponse().isTreatAsCommitted()) {
/*     */       return;
/*     */     }
/* 468 */     if (this.servletRequestContext.getDeployment().getDeploymentInfo().isIgnoreFlush() && this.servletRequestContext
/* 469 */       .getExchange().isRequestComplete() && this.servletRequestContext
/* 470 */       .getOriginalResponse().getHeader("Transfer-Encoding") == null) {
/*     */ 
/*     */ 
/*     */       
/* 474 */       this.servletRequestContext.getOriginalResponse().setIgnoredFlushPerformed(true);
/*     */       return;
/*     */     } 
/*     */     try {
/* 478 */       flushInternal();
/* 479 */     } catch (IOException ioe) {
/* 480 */       HttpServletRequestImpl request = this.servletRequestContext.getOriginalRequest();
/* 481 */       if (request.isAsyncStarted() || request.getDispatcherType() == DispatcherType.ASYNC) {
/* 482 */         this.servletRequestContext.getExchange().unDispatch();
/* 483 */         this.servletRequestContext.getOriginalRequest().getAsyncContextInternal().handleError(ioe);
/* 484 */         throw ioe;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flushInternal() throws IOException {
/* 493 */     if (this.listener == null) {
/* 494 */       if (Bits.anyAreSet(this.state, 1)) {
/*     */         return;
/*     */       }
/*     */       
/* 498 */       if (this.buffer != null && this.buffer.position() != 0) {
/* 499 */         writeBufferBlocking(false);
/*     */       }
/* 501 */       if (this.channel == null) {
/* 502 */         this.channel = this.servletRequestContext.getExchange().getResponseChannel();
/*     */       }
/* 504 */       Channels.flushBlocking((SuspendableWriteChannel)this.channel);
/*     */     } else {
/* 506 */       long res; if (Bits.anyAreClear(this.state, 4)) {
/*     */         return;
/*     */       }
/* 509 */       createChannel();
/* 510 */       if (this.buffer == null || this.buffer.position() == 0) {
/*     */ 
/*     */         
/* 513 */         this.channel.flush();
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 518 */       setFlags(2);
/* 519 */       this.buffer.flip();
/*     */       
/*     */       do {
/* 522 */         res = this.channel.write(this.buffer);
/* 523 */       } while (this.buffer.hasRemaining() && res != 0L);
/* 524 */       if (!this.buffer.hasRemaining()) {
/* 525 */         this.channel.flush();
/*     */       }
/* 527 */       this.buffer.compact();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void transferFrom(FileChannel source) throws IOException {
/* 533 */     if (Bits.anyAreSet(this.state, 1) || this.servletRequestContext.getOriginalResponse().isTreatAsCommitted()) {
/* 534 */       throw UndertowServletMessages.MESSAGES.streamIsClosed();
/*     */     }
/* 536 */     if (this.listener == null) {
/* 537 */       if (this.buffer != null && this.buffer.position() != 0) {
/* 538 */         writeBufferBlocking(false);
/*     */       }
/* 540 */       if (this.channel == null) {
/* 541 */         this.channel = this.servletRequestContext.getExchange().getResponseChannel();
/*     */       }
/* 543 */       long position = source.position();
/* 544 */       long count = source.size() - position;
/* 545 */       Channels.transferBlocking(this.channel, source, position, count);
/* 546 */       updateWritten(count);
/*     */     } else {
/* 548 */       setFlags(2);
/* 549 */       createChannel();
/*     */       
/* 551 */       long pos = 0L;
/*     */       try {
/* 553 */         long size = source.size();
/* 554 */         pos = source.position();
/*     */         
/* 556 */         while (size - pos > 0L) {
/* 557 */           long ret = this.channel.transferFrom(this.pendingFile, pos, size - pos);
/* 558 */           if (ret <= 0L) {
/* 559 */             clearFlags(4);
/* 560 */             this.pendingFile = source;
/* 561 */             source.position(pos);
/* 562 */             this.channel.resumeWrites();
/*     */             return;
/*     */           } 
/* 565 */           pos += ret;
/*     */         } 
/*     */       } finally {
/* 568 */         updateWrittenAsync(pos - source.position());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeBufferBlocking(boolean writeFinal) throws IOException {
/* 576 */     if (this.channel == null) {
/* 577 */       this.channel = this.servletRequestContext.getExchange().getResponseChannel();
/*     */     }
/* 579 */     this.buffer.flip();
/* 580 */     while (this.buffer.hasRemaining()) {
/* 581 */       if (writeFinal) {
/* 582 */         this.channel.writeFinal(this.buffer);
/*     */       } else {
/* 584 */         this.channel.write(this.buffer);
/*     */       } 
/* 586 */       if (this.buffer.hasRemaining()) {
/* 587 */         this.channel.awaitWritable();
/*     */       }
/*     */     } 
/* 590 */     this.buffer.clear();
/* 591 */     setFlags(2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 598 */     if (this.servletRequestContext.getOriginalRequest().getDispatcherType() == DispatcherType.INCLUDE || this.servletRequestContext
/* 599 */       .getOriginalResponse().isTreatAsCommitted()) {
/*     */       return;
/*     */     }
/* 602 */     if (this.listener == null) {
/* 603 */       if (Bits.anyAreSet(this.state, 1))
/* 604 */         return;  setFlags(1);
/* 605 */       clearFlags(4);
/* 606 */       if (Bits.allAreClear(this.state, 2) && this.channel == null && this.servletRequestContext.getOriginalResponse().getHeader("Content-Length") == null && 
/* 607 */         this.servletRequestContext.getOriginalResponse().getHeader("Transfer-Encoding") == null && this.servletRequestContext
/* 608 */         .getExchange().getAttachment(HttpAttachments.RESPONSE_TRAILER_SUPPLIER) == null && this.servletRequestContext
/* 609 */         .getExchange().getAttachment(HttpAttachments.RESPONSE_TRAILERS) == null) {
/* 610 */         if (this.buffer == null) {
/* 611 */           this.servletRequestContext.getExchange().getResponseHeaders().put(Headers.CONTENT_LENGTH, "0");
/*     */         } else {
/* 613 */           this.servletRequestContext.getExchange().getResponseHeaders().put(Headers.CONTENT_LENGTH, Integer.toString(this.buffer.position()));
/*     */         } 
/*     */       }
/*     */       
/*     */       try {
/* 618 */         if (this.buffer != null) {
/* 619 */           writeBufferBlocking(true);
/*     */         }
/* 621 */         if (this.channel == null) {
/* 622 */           this.channel = this.servletRequestContext.getExchange().getResponseChannel();
/*     */         }
/* 624 */         setFlags(8);
/* 625 */         StreamSinkChannel channel = this.channel;
/* 626 */         if (channel != null) {
/* 627 */           channel.shutdownWrites();
/* 628 */           Channels.flushBlocking((SuspendableWriteChannel)channel);
/*     */         } 
/* 630 */       } catch (IOException|RuntimeException|Error e) {
/* 631 */         IoUtils.safeClose((Closeable)this.channel);
/* 632 */         throw e;
/*     */       } finally {
/* 634 */         if (this.pooledBuffer != null) {
/* 635 */           this.pooledBuffer.close();
/* 636 */           this.buffer = null;
/*     */         } else {
/* 638 */           this.buffer = null;
/*     */         } 
/*     */       } 
/*     */     } else {
/* 642 */       closeAsync();
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
/*     */   public void closeAsync() throws IOException {
/* 656 */     if (Bits.anyAreSet(this.state, 1) || this.servletRequestContext.getOriginalResponse().isTreatAsCommitted()) {
/*     */       return;
/*     */     }
/* 659 */     if (!this.servletRequestContext.getExchange().isInIoThread()) {
/* 660 */       this.servletRequestContext.getExchange().getIoThread().execute(new Runnable()
/*     */           {
/*     */             public void run() {
/*     */               try {
/* 664 */                 ServletOutputStreamImpl.this.closeAsync();
/* 665 */               } catch (IOException e) {
/* 666 */                 UndertowLogger.REQUEST_IO_LOGGER.closeAsyncFailed(e);
/*     */               } 
/*     */             }
/*     */           });
/*     */       
/*     */       return;
/*     */     } 
/*     */     try {
/* 674 */       setFlags(1);
/* 675 */       clearFlags(4);
/* 676 */       if (Bits.allAreClear(this.state, 2) && this.channel == null)
/*     */       {
/* 678 */         if (this.servletRequestContext.getOriginalResponse().getHeader("Transfer-Encoding") == null) {
/* 679 */           if (this.buffer == null) {
/* 680 */             this.servletRequestContext.getOriginalResponse().setHeader(Headers.CONTENT_LENGTH, "0");
/*     */           } else {
/* 682 */             this.servletRequestContext.getOriginalResponse().setHeader(Headers.CONTENT_LENGTH, Integer.toString(this.buffer.position()));
/*     */           } 
/*     */         }
/*     */       }
/* 686 */       createChannel();
/* 687 */       if (this.buffer != null) {
/* 688 */         if (!flushBufferAsync(true)) {
/*     */           return;
/*     */         }
/*     */         
/* 692 */         if (this.pooledBuffer != null) {
/* 693 */           this.pooledBuffer.close();
/* 694 */           this.buffer = null;
/*     */         } else {
/* 696 */           this.buffer = null;
/*     */         } 
/*     */       } 
/* 699 */       this.channel.shutdownWrites();
/* 700 */       setFlags(8);
/* 701 */       if (!this.channel.flush()) {
/* 702 */         this.channel.resumeWrites();
/*     */       }
/* 704 */     } catch (IOException|RuntimeException|Error e) {
/* 705 */       if (this.pooledBuffer != null) {
/* 706 */         this.pooledBuffer.close();
/* 707 */         this.pooledBuffer = null;
/* 708 */         this.buffer = null;
/*     */       } 
/* 710 */       throw e;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void createChannel() {
/* 715 */     if (this.channel == null) {
/* 716 */       this.channel = this.servletRequestContext.getExchange().getResponseChannel();
/* 717 */       if (this.internalListener != null) {
/* 718 */         this.channel.getWriteSetter().set(this.internalListener);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private ByteBuffer buffer() {
/* 725 */     ByteBuffer buffer = this.buffer;
/* 726 */     if (buffer != null) {
/* 727 */       return buffer;
/*     */     }
/* 729 */     if (this.bufferSize != null) {
/* 730 */       this.buffer = ByteBuffer.allocateDirect(this.bufferSize.intValue());
/* 731 */       return this.buffer;
/*     */     } 
/* 733 */     this.pooledBuffer = this.servletRequestContext.getExchange().getConnection().getByteBufferPool().allocate();
/* 734 */     this.buffer = this.pooledBuffer.getBuffer();
/* 735 */     return this.buffer;
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetBuffer() {
/* 740 */     if (Bits.allAreClear(this.state, 2)) {
/* 741 */       if (this.pooledBuffer != null) {
/* 742 */         this.pooledBuffer.close();
/* 743 */         this.pooledBuffer = null;
/*     */       } 
/* 745 */       this.buffer = null;
/* 746 */       this.written = 0L;
/*     */     } else {
/* 748 */       throw UndertowServletMessages.MESSAGES.responseAlreadyCommited();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setBufferSize(int size) {
/* 753 */     if (this.buffer != null || this.servletRequestContext.getOriginalResponse().isTreatAsCommitted()) {
/* 754 */       throw UndertowServletMessages.MESSAGES.contentHasBeenWritten();
/*     */     }
/* 756 */     this.bufferSize = Integer.valueOf(size);
/*     */   }
/*     */   
/*     */   public boolean isClosed() {
/* 760 */     return Bits.anyAreSet(this.state, 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReady() {
/* 765 */     if (this.listener == null)
/*     */     {
/* 767 */       throw UndertowServletMessages.MESSAGES.streamNotInAsyncMode();
/*     */     }
/* 769 */     if (!this.asyncIoStarted)
/*     */     {
/*     */       
/* 772 */       return false;
/*     */     }
/* 774 */     if (!Bits.anyAreSet(this.state, 4)) {
/* 775 */       if (this.channel != null) {
/* 776 */         this.channel.resumeWrites();
/*     */       }
/* 778 */       return false;
/*     */     } 
/* 780 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWriteListener(WriteListener writeListener) {
/* 785 */     if (writeListener == null) {
/* 786 */       throw UndertowServletMessages.MESSAGES.listenerCannotBeNull();
/*     */     }
/* 788 */     if (this.listener != null) {
/* 789 */       throw UndertowServletMessages.MESSAGES.listenerAlreadySet();
/*     */     }
/* 791 */     HttpServletRequestImpl httpServletRequestImpl = this.servletRequestContext.getOriginalRequest();
/* 792 */     if (!httpServletRequestImpl.isAsyncStarted()) {
/* 793 */       throw UndertowServletMessages.MESSAGES.asyncNotStarted();
/*     */     }
/* 795 */     this.asyncContext = (AsyncContextImpl)httpServletRequestImpl.getAsyncContext();
/* 796 */     this.listener = writeListener;
/*     */ 
/*     */ 
/*     */     
/* 800 */     this.internalListener = new WriteChannelListener();
/* 801 */     if (this.channel != null) {
/* 802 */       this.channel.getWriteSetter().set(this.internalListener);
/*     */     }
/*     */     
/* 805 */     this.asyncContext.addAsyncTask(new Runnable()
/*     */         {
/*     */           public void run() {
/* 808 */             ServletOutputStreamImpl.this.asyncIoStarted = true;
/* 809 */             if (ServletOutputStreamImpl.this.channel == null) {
/* 810 */               ServletOutputStreamImpl.this.servletRequestContext.getExchange().getIoThread().execute(new Runnable()
/*     */                   {
/*     */                     public void run() {
/* 813 */                       ServletOutputStreamImpl.this.internalListener.handleEvent((StreamSinkChannel)null);
/*     */                     }
/*     */                   });
/*     */             } else {
/* 817 */               ServletOutputStreamImpl.this.channel.resumeWrites();
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   ServletRequestContext getServletRequestContext() {
/* 824 */     return this.servletRequestContext;
/*     */   }
/*     */   
/*     */   private class WriteChannelListener
/*     */     implements ChannelListener<StreamSinkChannel>
/*     */   {
/*     */     private WriteChannelListener() {}
/*     */     
/*     */     public void handleEvent(StreamSinkChannel aChannel) {
/* 833 */       if (Bits.anyAreSet(ServletOutputStreamImpl.this.state, 8)) {
/*     */         
/*     */         try {
/*     */           
/* 837 */           ServletOutputStreamImpl.this.channel.flush();
/*     */           return;
/* 839 */         } catch (Throwable t) {
/* 840 */           handleError(t);
/*     */           
/*     */           return;
/*     */         } 
/*     */       }
/* 845 */       if (ServletOutputStreamImpl.this.buffersToWrite != null) {
/* 846 */         long toWrite = Buffers.remaining((Buffer[])ServletOutputStreamImpl.this.buffersToWrite);
/* 847 */         long written = 0L;
/*     */         
/* 849 */         if (toWrite > 0L) {
/*     */           do {
/*     */             try {
/* 852 */               long res = ServletOutputStreamImpl.this.channel.write(ServletOutputStreamImpl.this.buffersToWrite);
/* 853 */               written += res;
/* 854 */               if (res == 0L) {
/*     */                 return;
/*     */               }
/* 857 */             } catch (Throwable t) {
/* 858 */               handleError(t);
/*     */               return;
/*     */             } 
/* 861 */           } while (written < toWrite);
/*     */         }
/* 863 */         ServletOutputStreamImpl.this.buffersToWrite = null;
/* 864 */         ServletOutputStreamImpl.this.buffer.clear();
/*     */       } 
/* 866 */       if (ServletOutputStreamImpl.this.pendingFile != null) {
/*     */         try {
/* 868 */           long size = ServletOutputStreamImpl.this.pendingFile.size();
/* 869 */           long pos = ServletOutputStreamImpl.this.pendingFile.position();
/*     */           
/* 871 */           while (size - pos > 0L) {
/* 872 */             long ret = ServletOutputStreamImpl.this.channel.transferFrom(ServletOutputStreamImpl.this.pendingFile, pos, size - pos);
/* 873 */             if (ret <= 0L) {
/* 874 */               ServletOutputStreamImpl.this.pendingFile.position(pos);
/*     */               return;
/*     */             } 
/* 877 */             pos += ret;
/*     */           } 
/* 879 */           ServletOutputStreamImpl.this.pendingFile = null;
/* 880 */         } catch (Throwable t) {
/* 881 */           handleError(t);
/*     */           return;
/*     */         } 
/*     */       }
/* 885 */       if (Bits.anyAreSet(ServletOutputStreamImpl.this.state, 1)) {
/*     */         
/*     */         try {
/* 888 */           if (ServletOutputStreamImpl.this.pooledBuffer != null) {
/* 889 */             ServletOutputStreamImpl.this.pooledBuffer.close();
/* 890 */             ServletOutputStreamImpl.this.buffer = null;
/*     */           } else {
/* 892 */             ServletOutputStreamImpl.this.buffer = null;
/*     */           } 
/* 894 */           ServletOutputStreamImpl.this.channel.shutdownWrites();
/* 895 */           ServletOutputStreamImpl.this.setFlags(8);
/* 896 */           ServletOutputStreamImpl.this.channel.flush();
/* 897 */         } catch (Throwable t) {
/* 898 */           handleError(t);
/*     */           
/*     */           return;
/*     */         } 
/*     */       } else {
/* 903 */         if (ServletOutputStreamImpl.this.asyncContext.isDispatched()) {
/*     */ 
/*     */ 
/*     */           
/* 907 */           ServletOutputStreamImpl.this.channel.suspendWrites();
/*     */           
/*     */           return;
/*     */         } 
/* 911 */         ServletOutputStreamImpl.this.setFlags(4);
/*     */         try {
/* 913 */           ServletOutputStreamImpl.this.setFlags(16);
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 918 */           if (ServletOutputStreamImpl.this.channel != null) {
/* 919 */             ServletOutputStreamImpl.this.channel.suspendWrites();
/*     */           }
/* 921 */           ServletOutputStreamImpl.this.servletRequestContext.getCurrentServletContext().invokeOnWritePossible(ServletOutputStreamImpl.this.servletRequestContext.getExchange(), ServletOutputStreamImpl.this.listener);
/* 922 */         } catch (Throwable e) {
/* 923 */           IoUtils.safeClose((Closeable)ServletOutputStreamImpl.this.channel);
/*     */         } finally {
/* 925 */           ServletOutputStreamImpl.this.clearFlags(16);
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private void handleError(final Throwable t) {
/*     */       try {
/* 934 */         ServletOutputStreamImpl.this.servletRequestContext.getCurrentServletContext().invokeRunnable(ServletOutputStreamImpl.this.servletRequestContext.getExchange(), new Runnable()
/*     */             {
/*     */               public void run() {
/* 937 */                 ServletOutputStreamImpl.this.listener.onError(t);
/*     */               }
/*     */             });
/*     */       } finally {
/* 941 */         IoUtils.safeClose(new Closeable[] { (Closeable)ServletOutputStreamImpl.access$200(this.this$0), (Closeable)ServletOutputStreamImpl.access$400(this.this$0).getExchange().getConnection() });
/* 942 */         if (ServletOutputStreamImpl.this.pooledBuffer != null) {
/* 943 */           ServletOutputStreamImpl.this.pooledBuffer.close();
/* 944 */           ServletOutputStreamImpl.this.pooledBuffer = null;
/* 945 */           ServletOutputStreamImpl.this.buffer = null;
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void setFlags(int flags) {
/*     */     int old;
/*     */     do {
/* 954 */       old = this.state;
/* 955 */     } while (!stateUpdater.compareAndSet(this, old, old | flags));
/*     */   }
/*     */   
/*     */   private void clearFlags(int flags) {
/*     */     int old;
/*     */     do {
/* 961 */       old = this.state;
/* 962 */     } while (!stateUpdater.compareAndSet(this, old, old & (flags ^ 0xFFFFFFFF)));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\ServletOutputStreamImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */