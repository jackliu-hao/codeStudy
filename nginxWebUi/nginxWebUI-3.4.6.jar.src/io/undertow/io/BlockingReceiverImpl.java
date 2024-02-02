/*     */ package io.undertow.io;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.Headers;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ public class BlockingReceiverImpl
/*     */   implements Receiver
/*     */ {
/*  40 */   private static final Receiver.ErrorCallback END_EXCHANGE = new Receiver.ErrorCallback()
/*     */     {
/*     */       public void error(HttpServerExchange exchange, IOException e) {
/*  43 */         if (!exchange.isResponseStarted()) {
/*  44 */           exchange.setStatusCode(500);
/*     */         }
/*  46 */         exchange.setPersistent(false);
/*  47 */         UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/*  48 */         exchange.endExchange();
/*     */       }
/*     */     };
/*  51 */   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
/*     */   
/*     */   private final HttpServerExchange exchange;
/*     */   
/*     */   private final InputStream inputStream;
/*  56 */   private int maxBufferSize = -1;
/*     */   private boolean done = false;
/*     */   
/*     */   public BlockingReceiverImpl(HttpServerExchange exchange, InputStream inputStream) {
/*  60 */     this.exchange = exchange;
/*  61 */     this.inputStream = inputStream;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxBufferSize(int maxBufferSize) {
/*  66 */     this.maxBufferSize = maxBufferSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public void receiveFullString(Receiver.FullStringCallback callback, Receiver.ErrorCallback errorCallback) {
/*  71 */     receiveFullString(callback, errorCallback, StandardCharsets.ISO_8859_1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void receiveFullString(Receiver.FullStringCallback callback) {
/*  76 */     receiveFullString(callback, END_EXCHANGE, StandardCharsets.ISO_8859_1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void receivePartialString(Receiver.PartialStringCallback callback, Receiver.ErrorCallback errorCallback) {
/*  81 */     receivePartialString(callback, errorCallback, StandardCharsets.ISO_8859_1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void receivePartialString(Receiver.PartialStringCallback callback) {
/*  86 */     receivePartialString(callback, END_EXCHANGE, StandardCharsets.ISO_8859_1);
/*     */   }
/*     */   public void receiveFullString(Receiver.FullStringCallback callback, Receiver.ErrorCallback errorCallback, Charset charset) {
/*     */     long contentLength;
/*     */     ByteArrayOutputStream sb;
/*  91 */     if (this.done) {
/*  92 */       throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
/*     */     }
/*  94 */     Receiver.ErrorCallback error = (errorCallback == null) ? END_EXCHANGE : errorCallback;
/*  95 */     if (callback == null) {
/*  96 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
/*     */     }
/*  98 */     if (this.exchange.isRequestComplete()) {
/*  99 */       callback.handle(this.exchange, "");
/*     */       return;
/*     */     } 
/* 102 */     String contentLengthString = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
/*     */ 
/*     */     
/* 105 */     if (contentLengthString != null) {
/* 106 */       contentLength = Long.parseLong(contentLengthString);
/* 107 */       if (contentLength > 2147483647L) {
/* 108 */         error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */         return;
/*     */       } 
/* 111 */       sb = new ByteArrayOutputStream((int)contentLength);
/*     */     } else {
/* 113 */       contentLength = -1L;
/* 114 */       sb = new ByteArrayOutputStream();
/*     */     } 
/* 116 */     if (this.maxBufferSize > 0 && 
/* 117 */       contentLength > this.maxBufferSize) {
/* 118 */       error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 123 */     try (PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate()) {
/* 124 */       int s; while ((s = this.inputStream.read(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), pooled.getBuffer().remaining())) > 0) {
/* 125 */         sb.write(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), s);
/*     */       }
/* 127 */       callback.handle(this.exchange, sb.toString(charset.name()));
/* 128 */     } catch (IOException e) {
/* 129 */       error.error(this.exchange, e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void receiveFullString(Receiver.FullStringCallback callback, Charset charset) {
/* 136 */     receiveFullString(callback, END_EXCHANGE, charset);
/*     */   }
/*     */   
/*     */   public void receivePartialString(Receiver.PartialStringCallback callback, Receiver.ErrorCallback errorCallback, Charset charset) {
/*     */     long contentLength;
/* 141 */     if (this.done) {
/* 142 */       throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
/*     */     }
/* 144 */     Receiver.ErrorCallback error = (errorCallback == null) ? END_EXCHANGE : errorCallback;
/* 145 */     if (callback == null) {
/* 146 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
/*     */     }
/* 148 */     if (this.exchange.isRequestComplete()) {
/* 149 */       callback.handle(this.exchange, "", true);
/*     */       return;
/*     */     } 
/* 152 */     String contentLengthString = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
/*     */     
/* 154 */     if (contentLengthString != null) {
/* 155 */       contentLength = Long.parseLong(contentLengthString);
/* 156 */       if (contentLength > 2147483647L) {
/* 157 */         error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */         return;
/*     */       } 
/*     */     } else {
/* 161 */       contentLength = -1L;
/*     */     } 
/* 163 */     if (this.maxBufferSize > 0 && 
/* 164 */       contentLength > this.maxBufferSize) {
/* 165 */       error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */       
/*     */       return;
/*     */     } 
/* 169 */     CharsetDecoder decoder = charset.newDecoder();
/*     */     
/* 171 */     try (PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate()) {
/* 172 */       int s; while ((s = this.inputStream.read(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), pooled.getBuffer().remaining())) > 0) {
/* 173 */         pooled.getBuffer().limit(s);
/* 174 */         CharBuffer res = decoder.decode(pooled.getBuffer());
/* 175 */         callback.handle(this.exchange, res.toString(), false);
/* 176 */         pooled.getBuffer().clear();
/*     */       } 
/* 178 */       callback.handle(this.exchange, "", true);
/* 179 */     } catch (IOException e) {
/* 180 */       error.error(this.exchange, e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void receivePartialString(Receiver.PartialStringCallback callback, Charset charset) {
/* 187 */     receivePartialString(callback, END_EXCHANGE, charset);
/*     */   }
/*     */   public void receiveFullBytes(Receiver.FullBytesCallback callback, Receiver.ErrorCallback errorCallback) {
/*     */     long contentLength;
/*     */     ByteArrayOutputStream sb;
/* 192 */     if (this.done) {
/* 193 */       throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
/*     */     }
/* 195 */     Receiver.ErrorCallback error = (errorCallback == null) ? END_EXCHANGE : errorCallback;
/* 196 */     if (callback == null) {
/* 197 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
/*     */     }
/* 199 */     if (this.exchange.isRequestComplete()) {
/* 200 */       callback.handle(this.exchange, EMPTY_BYTE_ARRAY);
/*     */       return;
/*     */     } 
/* 203 */     String contentLengthString = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
/*     */ 
/*     */     
/* 206 */     if (contentLengthString != null) {
/* 207 */       contentLength = Long.parseLong(contentLengthString);
/* 208 */       if (contentLength > 2147483647L) {
/* 209 */         error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */         return;
/*     */       } 
/* 212 */       sb = new ByteArrayOutputStream((int)contentLength);
/*     */     } else {
/* 214 */       contentLength = -1L;
/* 215 */       sb = new ByteArrayOutputStream();
/*     */     } 
/* 217 */     if (this.maxBufferSize > 0 && 
/* 218 */       contentLength > this.maxBufferSize) {
/* 219 */       error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 224 */     try (PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate()) {
/* 225 */       int s; while ((s = this.inputStream.read(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), pooled.getBuffer().remaining())) > 0) {
/* 226 */         sb.write(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), s);
/*     */       }
/* 228 */       callback.handle(this.exchange, sb.toByteArray());
/* 229 */     } catch (IOException e) {
/* 230 */       error.error(this.exchange, e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void receiveFullBytes(Receiver.FullBytesCallback callback) {
/* 237 */     receiveFullBytes(callback, END_EXCHANGE);
/*     */   }
/*     */   
/*     */   public void receivePartialBytes(Receiver.PartialBytesCallback callback, Receiver.ErrorCallback errorCallback) {
/*     */     long contentLength;
/* 242 */     if (this.done) {
/* 243 */       throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
/*     */     }
/* 245 */     Receiver.ErrorCallback error = (errorCallback == null) ? END_EXCHANGE : errorCallback;
/* 246 */     if (callback == null) {
/* 247 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
/*     */     }
/* 249 */     if (this.exchange.isRequestComplete()) {
/* 250 */       callback.handle(this.exchange, EMPTY_BYTE_ARRAY, true);
/*     */       return;
/*     */     } 
/* 253 */     String contentLengthString = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
/*     */     
/* 255 */     if (contentLengthString != null) {
/* 256 */       contentLength = Long.parseLong(contentLengthString);
/* 257 */       if (contentLength > 2147483647L) {
/* 258 */         error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */         return;
/*     */       } 
/*     */     } else {
/* 262 */       contentLength = -1L;
/*     */     } 
/* 264 */     if (this.maxBufferSize > 0 && 
/* 265 */       contentLength > this.maxBufferSize) {
/* 266 */       error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 271 */     try (PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate()) {
/* 272 */       int s; while ((s = this.inputStream.read(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), pooled.getBuffer().remaining())) > 0) {
/* 273 */         byte[] newData = new byte[s];
/* 274 */         System.arraycopy(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), newData, 0, s);
/* 275 */         callback.handle(this.exchange, newData, false);
/*     */       } 
/* 277 */       callback.handle(this.exchange, EMPTY_BYTE_ARRAY, true);
/* 278 */     } catch (IOException e) {
/* 279 */       error.error(this.exchange, e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void receivePartialBytes(Receiver.PartialBytesCallback callback) {
/* 285 */     receivePartialBytes(callback, END_EXCHANGE);
/*     */   }
/*     */   
/*     */   public void pause() {}
/*     */   
/*     */   public void resume() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\io\BlockingReceiverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */