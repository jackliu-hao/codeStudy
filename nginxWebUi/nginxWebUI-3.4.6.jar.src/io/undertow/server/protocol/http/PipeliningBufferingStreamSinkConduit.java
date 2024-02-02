/*     */ package io.undertow.server.protocol.http;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.AbstractServerConnection;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.StreamConnection;
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
/*     */ public class PipeliningBufferingStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*     */   private static final int SHUTDOWN = 1;
/*     */   private static final int DELEGATE_SHUTDOWN = 2;
/*     */   private static final int FLUSHING = 8;
/*     */   private int state;
/*     */   private final ByteBufferPool pool;
/*     */   private PooledByteBuffer buffer;
/*     */   
/*     */   public PipeliningBufferingStreamSinkConduit(StreamSinkConduit next, ByteBufferPool pool) {
/*  68 */     super(next);
/*  69 */     this.pool = pool;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/*  74 */     if (Bits.anyAreSet(this.state, 1)) {
/*  75 */       throw new ClosedChannelException();
/*     */     }
/*  77 */     return src.transferTo(position, count, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/*  82 */     return IoUtils.transfer((ReadableByteChannel)source, count, throughBuffer, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/*  87 */     if (Bits.anyAreSet(this.state, 1)) {
/*  88 */       throw new ClosedChannelException();
/*     */     }
/*  90 */     if (Bits.anyAreSet(this.state, 8)) {
/*  91 */       boolean res = flushBuffer();
/*  92 */       if (!res) {
/*  93 */         return 0L;
/*     */       }
/*     */     } 
/*  96 */     PooledByteBuffer pooled = this.buffer;
/*  97 */     if (pooled == null) {
/*  98 */       this.buffer = pooled = this.pool.allocate();
/*     */     }
/* 100 */     ByteBuffer buffer = pooled.getBuffer();
/*     */     
/* 102 */     long total = Buffers.remaining((Buffer[])srcs, offset, length);
/*     */     
/* 104 */     if (buffer.remaining() > total) {
/* 105 */       long put = total;
/* 106 */       Buffers.copy(buffer, srcs, offset, length);
/* 107 */       return put;
/*     */     } 
/* 109 */     return flushBufferWithUserData(srcs, offset, length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 115 */     if (Bits.anyAreSet(this.state, 1)) {
/* 116 */       throw new ClosedChannelException();
/*     */     }
/* 118 */     if (Bits.anyAreSet(this.state, 8)) {
/* 119 */       boolean res = flushBuffer();
/* 120 */       if (!res) {
/* 121 */         return 0;
/*     */       }
/*     */     } 
/* 124 */     PooledByteBuffer pooled = this.buffer;
/* 125 */     if (pooled == null) {
/* 126 */       this.buffer = pooled = this.pool.allocate();
/*     */     }
/* 128 */     ByteBuffer buffer = pooled.getBuffer();
/* 129 */     if (buffer.remaining() > src.remaining()) {
/* 130 */       int put = src.remaining();
/* 131 */       buffer.put(src);
/* 132 */       return put;
/*     */     } 
/* 134 */     return (int)flushBufferWithUserData(new ByteBuffer[] { src }, 0, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 140 */     return Conduits.writeFinalBasic((StreamSinkConduit)this, src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 145 */     return Conduits.writeFinalBasic((StreamSinkConduit)this, srcs, offset, length);
/*     */   }
/*     */   
/*     */   private long flushBufferWithUserData(ByteBuffer[] byteBuffers, int offset, int length) throws IOException {
/* 149 */     ByteBuffer byteBuffer = this.buffer.getBuffer();
/* 150 */     if (byteBuffer.position() == 0) {
/*     */       try {
/* 152 */         return ((StreamSinkConduit)this.next).write(byteBuffers, offset, length);
/*     */       } finally {
/* 154 */         this.buffer.close();
/* 155 */         this.buffer = null;
/*     */       } 
/*     */     }
/*     */     
/* 159 */     if (!Bits.anyAreSet(this.state, 8)) {
/* 160 */       this.state |= 0x8;
/* 161 */       byteBuffer.flip();
/*     */     } 
/* 163 */     int originalBufferedRemaining = byteBuffer.remaining();
/* 164 */     long toWrite = originalBufferedRemaining;
/* 165 */     ByteBuffer[] writeBufs = new ByteBuffer[length + 1];
/* 166 */     writeBufs[0] = byteBuffer;
/* 167 */     for (int i = offset; i < offset + length; i++) {
/* 168 */       writeBufs[i + 1 - offset] = byteBuffers[i];
/* 169 */       toWrite += byteBuffers[i].remaining();
/*     */     } 
/*     */     
/* 172 */     long res = 0L;
/* 173 */     long written = 0L;
/*     */     while (true) {
/* 175 */       res = ((StreamSinkConduit)this.next).write(writeBufs, 0, writeBufs.length);
/* 176 */       written += res;
/* 177 */       if (res == 0L) {
/* 178 */         if (written > originalBufferedRemaining) {
/* 179 */           this.buffer.close();
/* 180 */           this.buffer = null;
/* 181 */           this.state &= 0xFFFFFFF7;
/* 182 */           return written - originalBufferedRemaining;
/*     */         } 
/* 184 */         return 0L;
/*     */       } 
/* 186 */       if (written >= toWrite) {
/* 187 */         this.buffer.close();
/* 188 */         this.buffer = null;
/* 189 */         this.state &= 0xFFFFFFF7;
/* 190 */         return written - originalBufferedRemaining;
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
/*     */   public boolean flushPipelinedData() throws IOException {
/* 205 */     if (this.buffer == null || (this.buffer.getBuffer().position() == 0 && Bits.allAreClear(this.state, 8))) {
/* 206 */       return ((StreamSinkConduit)this.next).flush();
/*     */     }
/* 208 */     return flushBuffer();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setupPipelineBuffer(HttpServerExchange exchange) {
/* 215 */     ((HttpServerConnection)exchange.getConnection()).getChannel().getSinkChannel().setConduit((StreamSinkConduit)this);
/*     */   }
/*     */   
/*     */   private boolean flushBuffer() throws IOException {
/* 219 */     if (this.buffer == null) {
/* 220 */       return ((StreamSinkConduit)this.next).flush();
/*     */     }
/* 222 */     ByteBuffer byteBuffer = this.buffer.getBuffer();
/* 223 */     if (!Bits.anyAreSet(this.state, 8)) {
/* 224 */       this.state |= 0x8;
/* 225 */       byteBuffer.flip();
/*     */     } 
/* 227 */     while (byteBuffer.hasRemaining()) {
/* 228 */       if (((StreamSinkConduit)this.next).write(byteBuffer) == 0) {
/* 229 */         return false;
/*     */       }
/*     */     } 
/* 232 */     if (!((StreamSinkConduit)this.next).flush()) {
/* 233 */       return false;
/*     */     }
/* 235 */     this.buffer.close();
/* 236 */     this.buffer = null;
/* 237 */     this.state &= 0xFFFFFFF7;
/* 238 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 243 */     if (this.buffer != null && 
/* 244 */       this.buffer.getBuffer().hasRemaining()) {
/*     */       return;
/*     */     }
/*     */     
/* 248 */     ((StreamSinkConduit)this.next).awaitWritable(time, timeUnit);
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 253 */     if (this.buffer != null) {
/* 254 */       if (this.buffer.getBuffer().hasRemaining()) {
/*     */         return;
/*     */       }
/* 257 */       ((StreamSinkConduit)this.next).awaitWritable();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean flush() throws IOException {
/* 263 */     if (Bits.anyAreSet(this.state, 1)) {
/* 264 */       if (!flushBuffer()) {
/* 265 */         return false;
/*     */       }
/* 267 */       if (Bits.anyAreSet(this.state, 1) && 
/* 268 */         Bits.anyAreClear(this.state, 2)) {
/* 269 */         this.state |= 0x2;
/* 270 */         ((StreamSinkConduit)this.next).terminateWrites();
/*     */       } 
/* 272 */       return ((StreamSinkConduit)this.next).flush();
/*     */     } 
/* 274 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 279 */     this.state |= 0x1;
/* 280 */     if (this.buffer == null) {
/* 281 */       this.state |= 0x2;
/* 282 */       ((StreamSinkConduit)this.next).terminateWrites();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void truncateWrites() throws IOException {
/*     */     try {
/* 288 */       ((StreamSinkConduit)this.next).truncateWrites();
/*     */     } finally {
/* 290 */       if (this.buffer != null) {
/* 291 */         this.buffer.close();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void exchangeComplete(HttpServerExchange exchange) {
/* 300 */     HttpServerConnection connection = (HttpServerConnection)exchange.getConnection();
/* 301 */     if (connection.getExtraBytes() == null || exchange.isUpgrade()) {
/* 302 */       performFlush(exchange, connection);
/*     */     } else {
/* 304 */       connection.getReadListener().exchangeComplete(exchange);
/*     */     } 
/*     */   }
/*     */   
/*     */   void performFlush(final HttpServerExchange exchange, final HttpServerConnection connection) {
/*     */     try {
/* 310 */       final AbstractServerConnection.ConduitState oldState = connection.resetChannel();
/* 311 */       if (!flushPipelinedData()) {
/* 312 */         final StreamConnection channel = connection.getChannel();
/* 313 */         channel.getSinkChannel().setWriteListener(new ChannelListener<Channel>()
/*     */             {
/*     */               public void handleEvent(Channel c) {
/*     */                 try {
/* 317 */                   if (PipeliningBufferingStreamSinkConduit.this.flushPipelinedData()) {
/* 318 */                     channel.getSinkChannel().setWriteListener(null);
/* 319 */                     channel.getSinkChannel().suspendWrites();
/* 320 */                     connection.restoreChannel(oldState);
/* 321 */                     connection.getReadListener().exchangeComplete(exchange);
/*     */                   } 
/* 323 */                 } catch (IOException e) {
/* 324 */                   UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 325 */                   IoUtils.safeClose((Closeable)channel);
/* 326 */                 } catch (Throwable t) {
/* 327 */                   UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(t);
/* 328 */                   IoUtils.safeClose((Closeable)channel);
/*     */                 } 
/*     */               }
/*     */             });
/* 332 */         connection.getChannel().getSinkChannel().resumeWrites();
/*     */         return;
/*     */       } 
/* 335 */       connection.restoreChannel(oldState);
/* 336 */       connection.getReadListener().exchangeComplete(exchange);
/*     */     }
/* 338 */     catch (IOException e) {
/* 339 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 340 */       IoUtils.safeClose((Closeable)connection.getChannel());
/* 341 */     } catch (Throwable t) {
/* 342 */       UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(t);
/* 343 */       IoUtils.safeClose((Closeable)connection.getChannel());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http\PipeliningBufferingStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */