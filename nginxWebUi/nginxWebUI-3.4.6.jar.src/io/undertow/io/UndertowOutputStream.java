/*     */ package io.undertow.io;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.Methods;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.GatheringByteChannel;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.Buffers;
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
/*     */ public class UndertowOutputStream
/*     */   extends OutputStream
/*     */   implements BufferWritableOutputStream
/*     */ {
/*     */   private final HttpServerExchange exchange;
/*     */   private ByteBuffer buffer;
/*     */   private PooledByteBuffer pooledBuffer;
/*     */   private StreamSinkChannel channel;
/*     */   private int state;
/*     */   private long written;
/*     */   private final long contentLength;
/*     */   private static final int FLAG_CLOSED = 1;
/*     */   private static final int FLAG_WRITE_STARTED = 2;
/*     */   private static final int MAX_BUFFERS_TO_ALLOCATE = 10;
/*     */   
/*     */   public UndertowOutputStream(HttpServerExchange exchange) {
/*  69 */     this.exchange = exchange;
/*  70 */     this.contentLength = exchange.getResponseContentLength();
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
/*     */   public void resetBuffer() {
/*  82 */     if (Bits.anyAreSet(this.state, 2)) {
/*  83 */       throw UndertowMessages.MESSAGES.cannotResetBuffer();
/*     */     }
/*  85 */     this.buffer = null;
/*  86 */     IoUtils.safeClose((Closeable)this.pooledBuffer);
/*  87 */     this.pooledBuffer = null;
/*  88 */     this.written = 0L;
/*     */   }
/*     */   
/*     */   public long getBytesWritten() {
/*  92 */     return this.written;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/*  99 */     write(new byte[] { (byte)b }, 0, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/* 106 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 113 */     if (len < 1) {
/*     */       return;
/*     */     }
/* 116 */     if (this.exchange.isInIoThread()) {
/* 117 */       throw UndertowMessages.MESSAGES.blockingIoFromIOThread();
/*     */     }
/* 119 */     if (Bits.anyAreSet(this.state, 1)) {
/* 120 */       throw UndertowMessages.MESSAGES.streamIsClosed();
/*     */     }
/*     */     
/* 123 */     ByteBuffer buffer = buffer();
/* 124 */     if (len == this.contentLength - this.written || buffer.remaining() < len) {
/* 125 */       if (buffer.remaining() < len) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 132 */         StreamSinkChannel channel = this.channel;
/* 133 */         if (channel == null) {
/* 134 */           this.channel = channel = this.exchange.getResponseChannel();
/*     */         }
/* 136 */         ByteBufferPool bufferPool = this.exchange.getConnection().getByteBufferPool();
/* 137 */         ByteBuffer[] buffers = new ByteBuffer[11];
/* 138 */         PooledByteBuffer[] pooledBuffers = new PooledByteBuffer[10];
/*     */         try {
/* 140 */           buffers[0] = buffer;
/* 141 */           int bytesWritten = 0;
/* 142 */           int rem = buffer.remaining();
/* 143 */           buffer.put(b, bytesWritten + off, rem);
/* 144 */           buffer.flip();
/* 145 */           bytesWritten += rem;
/* 146 */           int bufferCount = 1; int j;
/* 147 */           for (j = 0; j < 10; j++) {
/* 148 */             PooledByteBuffer pooled = bufferPool.allocate();
/* 149 */             pooledBuffers[bufferCount - 1] = pooled;
/* 150 */             buffers[bufferCount++] = pooled.getBuffer();
/* 151 */             ByteBuffer cb = pooled.getBuffer();
/* 152 */             int toWrite = len - bytesWritten;
/* 153 */             if (toWrite > cb.remaining()) {
/* 154 */               rem = cb.remaining();
/* 155 */               cb.put(b, bytesWritten + off, rem);
/* 156 */               cb.flip();
/* 157 */               bytesWritten += rem;
/*     */             } else {
/* 159 */               cb.put(b, bytesWritten + off, len - bytesWritten);
/* 160 */               bytesWritten = len;
/* 161 */               cb.flip();
/*     */               break;
/*     */             } 
/*     */           } 
/* 165 */           Channels.writeBlocking((GatheringByteChannel)channel, buffers, 0, bufferCount);
/* 166 */           while (bytesWritten < len) {
/*     */             
/* 168 */             bufferCount = 0;
/* 169 */             for (j = 0; j < 11; j++) {
/* 170 */               ByteBuffer cb = buffers[j];
/* 171 */               cb.clear();
/* 172 */               bufferCount++;
/* 173 */               int toWrite = len - bytesWritten;
/* 174 */               if (toWrite > cb.remaining()) {
/* 175 */                 rem = cb.remaining();
/* 176 */                 cb.put(b, bytesWritten + off, rem);
/* 177 */                 cb.flip();
/* 178 */                 bytesWritten += rem;
/*     */               } else {
/* 180 */                 cb.put(b, bytesWritten + off, len - bytesWritten);
/* 181 */                 bytesWritten = len;
/* 182 */                 cb.flip();
/*     */                 break;
/*     */               } 
/*     */             } 
/* 186 */             Channels.writeBlocking((GatheringByteChannel)channel, buffers, 0, bufferCount);
/*     */           } 
/* 188 */           buffer.clear();
/*     */         } finally {
/* 190 */           for (int i = 0; i < pooledBuffers.length; i++) {
/* 191 */             PooledByteBuffer p = pooledBuffers[i];
/* 192 */             if (p == null) {
/*     */               break;
/*     */             }
/* 195 */             p.close();
/*     */           } 
/*     */         } 
/*     */       } else {
/* 199 */         buffer.put(b, off, len);
/* 200 */         if (buffer.remaining() == 0) {
/* 201 */           writeBufferBlocking(false);
/*     */         }
/*     */       } 
/*     */     } else {
/* 205 */       buffer.put(b, off, len);
/* 206 */       if (buffer.remaining() == 0) {
/* 207 */         writeBufferBlocking(false);
/*     */       }
/*     */     } 
/* 210 */     updateWritten(len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(ByteBuffer[] buffers) throws IOException {
/* 215 */     if (Bits.anyAreSet(this.state, 1)) {
/* 216 */       throw UndertowMessages.MESSAGES.streamIsClosed();
/*     */     }
/* 218 */     int len = 0;
/* 219 */     for (ByteBuffer buf : buffers) {
/* 220 */       len += buf.remaining();
/*     */     }
/* 222 */     if (len < 1) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 228 */     if (this.written == 0L && len == this.contentLength) {
/* 229 */       if (this.channel == null) {
/* 230 */         this.channel = this.exchange.getResponseChannel();
/*     */       }
/* 232 */       Channels.writeBlocking((GatheringByteChannel)this.channel, buffers, 0, buffers.length);
/* 233 */       this.state |= 0x2;
/*     */     } else {
/* 235 */       ByteBuffer buffer = buffer();
/* 236 */       if (len < buffer.remaining()) {
/* 237 */         Buffers.copy(buffer, buffers, 0, buffers.length);
/*     */       } else {
/* 239 */         if (this.channel == null) {
/* 240 */           this.channel = this.exchange.getResponseChannel();
/*     */         }
/* 242 */         if (buffer.position() == 0) {
/* 243 */           Channels.writeBlocking((GatheringByteChannel)this.channel, buffers, 0, buffers.length);
/*     */         } else {
/* 245 */           ByteBuffer[] newBuffers = new ByteBuffer[buffers.length + 1];
/* 246 */           buffer.flip();
/* 247 */           newBuffers[0] = buffer;
/* 248 */           System.arraycopy(buffers, 0, newBuffers, 1, buffers.length);
/* 249 */           Channels.writeBlocking((GatheringByteChannel)this.channel, newBuffers, 0, newBuffers.length);
/* 250 */           buffer.clear();
/*     */         } 
/* 252 */         this.state |= 0x2;
/*     */       } 
/*     */     } 
/* 255 */     updateWritten(len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(ByteBuffer byteBuffer) throws IOException {
/* 260 */     write(new ByteBuffer[] { byteBuffer });
/*     */   }
/*     */   
/*     */   void updateWritten(long len) throws IOException {
/* 264 */     this.written += len;
/* 265 */     if (this.contentLength != -1L && this.written >= this.contentLength) {
/* 266 */       flush();
/* 267 */       close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 275 */     if (Bits.anyAreSet(this.state, 1)) {
/* 276 */       throw UndertowMessages.MESSAGES.streamIsClosed();
/*     */     }
/* 278 */     if (this.buffer != null && this.buffer.position() != 0) {
/* 279 */       writeBufferBlocking(false);
/*     */     }
/* 281 */     if (this.channel == null) {
/* 282 */       this.channel = this.exchange.getResponseChannel();
/*     */     }
/* 284 */     Channels.flushBlocking((SuspendableWriteChannel)this.channel);
/*     */   }
/*     */   
/*     */   private void writeBufferBlocking(boolean writeFinal) throws IOException {
/* 288 */     if (this.channel == null) {
/* 289 */       this.channel = this.exchange.getResponseChannel();
/*     */     }
/* 291 */     this.buffer.flip();
/*     */     
/* 293 */     while (this.buffer.hasRemaining()) {
/* 294 */       if (writeFinal) {
/* 295 */         this.channel.writeFinal(this.buffer);
/*     */       } else {
/* 297 */         this.channel.write(this.buffer);
/*     */       } 
/* 299 */       if (this.buffer.hasRemaining()) {
/* 300 */         this.channel.awaitWritable();
/*     */       }
/*     */     } 
/* 303 */     this.buffer.clear();
/* 304 */     this.state |= 0x2;
/*     */   }
/*     */ 
/*     */   
/*     */   public void transferFrom(FileChannel source) throws IOException {
/* 309 */     if (Bits.anyAreSet(this.state, 1)) {
/* 310 */       throw UndertowMessages.MESSAGES.streamIsClosed();
/*     */     }
/* 312 */     if (this.buffer != null && this.buffer.position() != 0) {
/* 313 */       writeBufferBlocking(false);
/*     */     }
/* 315 */     if (this.channel == null) {
/* 316 */       this.channel = this.exchange.getResponseChannel();
/*     */     }
/* 318 */     long position = source.position();
/* 319 */     long size = source.size();
/* 320 */     Channels.transferBlocking(this.channel, source, position, size);
/* 321 */     updateWritten(size - position);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 328 */     if (Bits.anyAreSet(this.state, 1))
/*     */       return;  try {
/* 330 */       this.state |= 0x1;
/* 331 */       if (Bits.anyAreClear(this.state, 2) && this.channel == null && 
/*     */         
/* 333 */         !isHeadRequestWithContentLength(this.exchange)) {
/* 334 */         if (this.buffer == null) {
/* 335 */           this.exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, "0");
/*     */         } else {
/* 337 */           this.exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, "" + this.buffer.position());
/*     */         } 
/*     */       }
/* 340 */       if (this.buffer != null) {
/* 341 */         writeBufferBlocking(true);
/*     */       }
/* 343 */       if (this.channel == null) {
/* 344 */         this.channel = this.exchange.getResponseChannel();
/*     */       }
/* 346 */       if (this.channel == null) {
/*     */         return;
/*     */       }
/* 349 */       StreamSinkChannel channel = this.channel;
/* 350 */       channel.shutdownWrites();
/* 351 */       Channels.flushBlocking((SuspendableWriteChannel)channel);
/*     */     } finally {
/* 353 */       if (this.pooledBuffer != null) {
/* 354 */         this.pooledBuffer.close();
/* 355 */         this.buffer = null;
/*     */       } else {
/* 357 */         this.buffer = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isHeadRequestWithContentLength(HttpServerExchange exchange) {
/* 364 */     return (Methods.HEAD.equals(exchange.getRequestMethod()) && exchange
/* 365 */       .getResponseHeaders().contains(Headers.CONTENT_LENGTH));
/*     */   }
/*     */   
/*     */   private ByteBuffer buffer() {
/* 369 */     ByteBuffer buffer = this.buffer;
/* 370 */     if (buffer != null) {
/* 371 */       return buffer;
/*     */     }
/* 373 */     this.pooledBuffer = this.exchange.getConnection().getByteBufferPool().allocate();
/* 374 */     this.buffer = this.pooledBuffer.getBuffer();
/* 375 */     return this.buffer;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\io\UndertowOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */