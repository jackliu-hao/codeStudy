/*     */ package io.undertow.io;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.Headers;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.IoUtils;
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
/*     */ public class BlockingSenderImpl
/*     */   implements Sender
/*     */ {
/*     */   private final HttpServerExchange exchange;
/*     */   private final OutputStream outputStream;
/*     */   private volatile Thread inCall;
/*     */   private volatile Thread sendThread;
/*     */   private ByteBuffer[] next;
/*     */   private FileChannel pendingFile;
/*     */   private IoCallback queuedCallback;
/*     */   
/*     */   public BlockingSenderImpl(HttpServerExchange exchange, OutputStream outputStream) {
/*  53 */     this.exchange = exchange;
/*  54 */     this.outputStream = outputStream;
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(ByteBuffer buffer, IoCallback callback) {
/*  59 */     this.sendThread = Thread.currentThread();
/*  60 */     if (this.inCall == Thread.currentThread()) {
/*  61 */       queue(new ByteBuffer[] { buffer }, callback);
/*     */       return;
/*     */     } 
/*  64 */     long responseContentLength = this.exchange.getResponseContentLength();
/*  65 */     if (responseContentLength > 0L && buffer.remaining() > responseContentLength) {
/*  66 */       callback.onException(this.exchange, this, UndertowLogger.ROOT_LOGGER.dataLargerThanContentLength(buffer.remaining(), responseContentLength));
/*     */       return;
/*     */     } 
/*  69 */     if (!this.exchange.isResponseStarted() && callback == IoCallback.END_EXCHANGE && 
/*  70 */       responseContentLength == -1L && !this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
/*  71 */       this.exchange.setResponseContentLength(buffer.remaining());
/*     */     }
/*     */ 
/*     */     
/*  75 */     if (writeBuffer(buffer, callback)) {
/*  76 */       invokeOnComplete(callback);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void send(ByteBuffer[] buffer, IoCallback callback) {
/*  83 */     this.sendThread = Thread.currentThread();
/*  84 */     if (this.inCall == Thread.currentThread()) {
/*  85 */       queue(buffer, callback);
/*     */       return;
/*     */     } 
/*  88 */     long responseContentLength = this.exchange.getResponseContentLength();
/*  89 */     if (responseContentLength > 0L && Buffers.remaining((Buffer[])buffer) > responseContentLength) {
/*  90 */       callback.onException(this.exchange, this, UndertowLogger.ROOT_LOGGER.dataLargerThanContentLength(Buffers.remaining((Buffer[])buffer), responseContentLength));
/*     */       return;
/*     */     } 
/*  93 */     if (!this.exchange.isResponseStarted() && callback == IoCallback.END_EXCHANGE && 
/*  94 */       responseContentLength == -1L && !this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
/*  95 */       this.exchange.setResponseContentLength(Buffers.remaining((Buffer[])buffer));
/*     */     }
/*     */ 
/*     */     
/*  99 */     if (!writeBuffer(buffer, callback)) {
/*     */       return;
/*     */     }
/* 102 */     invokeOnComplete(callback);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(ByteBuffer buffer) {
/* 107 */     send(buffer, IoCallback.END_EXCHANGE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(ByteBuffer[] buffer) {
/* 112 */     send(buffer, IoCallback.END_EXCHANGE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(String data, IoCallback callback) {
/* 117 */     byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
/* 118 */     this.sendThread = Thread.currentThread();
/* 119 */     if (this.inCall == Thread.currentThread()) {
/* 120 */       queue(new ByteBuffer[] { ByteBuffer.wrap(bytes) }, callback);
/*     */       return;
/*     */     } 
/* 123 */     long responseContentLength = this.exchange.getResponseContentLength();
/* 124 */     if (responseContentLength > 0L && bytes.length > responseContentLength) {
/* 125 */       callback.onException(this.exchange, this, UndertowLogger.ROOT_LOGGER.dataLargerThanContentLength(bytes.length, responseContentLength));
/*     */       return;
/*     */     } 
/* 128 */     if (!this.exchange.isResponseStarted() && callback == IoCallback.END_EXCHANGE && 
/* 129 */       responseContentLength == -1L && !this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
/* 130 */       this.exchange.setResponseContentLength(bytes.length);
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 135 */       this.outputStream.write(bytes);
/* 136 */       invokeOnComplete(callback);
/* 137 */     } catch (IOException e) {
/* 138 */       callback.onException(this.exchange, this, e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(String data, Charset charset, IoCallback callback) {
/* 144 */     byte[] bytes = data.getBytes(charset);
/* 145 */     this.sendThread = Thread.currentThread();
/* 146 */     if (this.inCall == Thread.currentThread()) {
/* 147 */       queue(new ByteBuffer[] { ByteBuffer.wrap(bytes) }, callback);
/*     */       return;
/*     */     } 
/* 150 */     long responseContentLength = this.exchange.getResponseContentLength();
/* 151 */     if (responseContentLength > 0L && bytes.length > responseContentLength) {
/* 152 */       callback.onException(this.exchange, this, UndertowLogger.ROOT_LOGGER.dataLargerThanContentLength(bytes.length, responseContentLength));
/*     */       return;
/*     */     } 
/* 155 */     if (!this.exchange.isResponseStarted() && callback == IoCallback.END_EXCHANGE && 
/* 156 */       responseContentLength == -1L && !this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
/* 157 */       this.exchange.setResponseContentLength(bytes.length);
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 162 */       this.outputStream.write(bytes);
/* 163 */       invokeOnComplete(callback);
/* 164 */     } catch (IOException e) {
/* 165 */       callback.onException(this.exchange, this, e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(String data) {
/* 171 */     send(data, IoCallback.END_EXCHANGE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(String data, Charset charset) {
/* 176 */     send(data, charset, IoCallback.END_EXCHANGE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void transferFrom(FileChannel source, IoCallback callback) {
/* 181 */     this.sendThread = Thread.currentThread();
/* 182 */     if (this.inCall == Thread.currentThread()) {
/* 183 */       queue(source, callback);
/*     */       return;
/*     */     } 
/* 186 */     performTransfer(source, callback);
/* 187 */     invokeOnComplete(callback);
/*     */   }
/*     */   
/*     */   private void performTransfer(FileChannel source, IoCallback callback) {
/* 191 */     if (this.outputStream instanceof BufferWritableOutputStream) {
/*     */       try {
/* 193 */         ((BufferWritableOutputStream)this.outputStream).transferFrom(source);
/* 194 */       } catch (IOException e) {
/* 195 */         callback.onException(this.exchange, this, e);
/*     */       } 
/*     */     } else {
/* 198 */       try (PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate()) {
/* 199 */         ByteBuffer buffer = pooled.getBuffer();
/* 200 */         long pos = source.position();
/* 201 */         long size = source.size();
/* 202 */         while (size - pos > 0L) {
/* 203 */           int ret = source.read(buffer);
/* 204 */           if (ret <= 0) {
/*     */             break;
/*     */           }
/* 207 */           pos += ret;
/* 208 */           this.outputStream.write(buffer.array(), buffer.arrayOffset(), ret);
/* 209 */           buffer.clear();
/*     */         } 
/*     */         
/* 212 */         if (pos != size) {
/* 213 */           throw new EOFException("Unexpected EOF reading file");
/*     */         }
/*     */       }
/* 216 */       catch (IOException e) {
/* 217 */         callback.onException(this.exchange, this, e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close(IoCallback callback) {
/*     */     try {
/* 225 */       this.outputStream.close();
/* 226 */       invokeOnComplete(callback);
/* 227 */     } catch (IOException e) {
/* 228 */       callback.onException(this.exchange, this, e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 234 */     IoUtils.safeClose(this.outputStream);
/*     */   }
/*     */   
/*     */   private boolean writeBuffer(ByteBuffer buffer, IoCallback callback) {
/* 238 */     return writeBuffer(new ByteBuffer[] { buffer }, callback);
/*     */   }
/*     */   
/*     */   private boolean writeBuffer(ByteBuffer[] buffers, IoCallback callback) {
/* 242 */     if (this.outputStream instanceof BufferWritableOutputStream) {
/*     */       
/*     */       try {
/* 245 */         ((BufferWritableOutputStream)this.outputStream).write(buffers);
/* 246 */         return true;
/* 247 */       } catch (IOException e) {
/* 248 */         callback.onException(this.exchange, this, e);
/* 249 */         return false;
/*     */       } 
/*     */     }
/* 252 */     for (ByteBuffer buffer : buffers) {
/* 253 */       if (buffer.hasArray()) {
/*     */         try {
/* 255 */           this.outputStream.write(buffer.array(), buffer.arrayOffset(), buffer.remaining());
/* 256 */         } catch (IOException e) {
/* 257 */           callback.onException(this.exchange, this, e);
/* 258 */           return false;
/*     */         } 
/*     */       } else {
/* 261 */         try (PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate()) {
/* 262 */           while (buffer.hasRemaining()) {
/* 263 */             int toRead = Math.min(buffer.remaining(), pooled.getBuffer().remaining());
/* 264 */             buffer.get(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), toRead);
/*     */             try {
/* 266 */               this.outputStream.write(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), toRead);
/* 267 */             } catch (IOException e) {
/* 268 */               callback.onException(this.exchange, this, e);
/* 269 */               return false;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 275 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeOnComplete(IoCallback callback) {
/* 280 */     this.sendThread = null;
/* 281 */     this.inCall = Thread.currentThread();
/*     */     try {
/* 283 */       callback.onComplete(this.exchange, this);
/*     */     } finally {
/* 285 */       this.inCall = null;
/*     */     } 
/* 287 */     if (Thread.currentThread() != this.sendThread) {
/*     */       return;
/*     */     }
/* 290 */     while (this.next != null || this.pendingFile != null) {
/* 291 */       ByteBuffer[] next = this.next;
/* 292 */       IoCallback queuedCallback = this.queuedCallback;
/* 293 */       FileChannel file = this.pendingFile;
/* 294 */       this.next = null;
/* 295 */       this.queuedCallback = null;
/* 296 */       this.pendingFile = null;
/*     */       
/* 298 */       if (next != null) {
/* 299 */         for (ByteBuffer buffer : next) {
/* 300 */           writeBuffer(buffer, queuedCallback);
/*     */         }
/* 302 */       } else if (file != null) {
/* 303 */         performTransfer(file, queuedCallback);
/*     */       } 
/* 305 */       this.sendThread = null;
/* 306 */       this.inCall = Thread.currentThread();
/*     */       try {
/* 308 */         queuedCallback.onComplete(this.exchange, this);
/*     */       } finally {
/* 310 */         this.inCall = null;
/*     */       } 
/* 312 */       if (Thread.currentThread() != this.sendThread) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void queue(ByteBuffer[] byteBuffers, IoCallback ioCallback) {
/* 320 */     if (this.next != null) {
/* 321 */       throw UndertowMessages.MESSAGES.dataAlreadyQueued();
/*     */     }
/* 323 */     this.next = byteBuffers;
/* 324 */     this.queuedCallback = ioCallback;
/*     */   }
/*     */ 
/*     */   
/*     */   private void queue(FileChannel source, IoCallback ioCallback) {
/* 329 */     if (this.pendingFile != null) {
/* 330 */       throw UndertowMessages.MESSAGES.dataAlreadyQueued();
/*     */     }
/* 332 */     this.pendingFile = source;
/* 333 */     this.queuedCallback = ioCallback;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\io\BlockingSenderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */