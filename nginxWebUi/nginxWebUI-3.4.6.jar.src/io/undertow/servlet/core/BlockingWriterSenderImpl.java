/*     */ package io.undertow.servlet.core;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.io.IoCallback;
/*     */ import io.undertow.io.Sender;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.servlet.handlers.ServletRequestContext;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.charset.CharacterCodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import javax.servlet.DispatcherType;
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
/*     */ public class BlockingWriterSenderImpl
/*     */   implements Sender
/*     */ {
/*     */   public static final int BUFFER_SIZE = 128;
/*     */   private final CharsetDecoder charsetDecoder;
/*     */   private final HttpServerExchange exchange;
/*     */   private final PrintWriter writer;
/*     */   private FileChannel pendingFile;
/*     */   private volatile Thread inCall;
/*     */   private volatile Thread sendThread;
/*     */   private String next;
/*     */   private IoCallback queuedCallback;
/*     */   
/*     */   public BlockingWriterSenderImpl(HttpServerExchange exchange, PrintWriter writer, String charset) {
/*  65 */     this.exchange = exchange;
/*  66 */     this.writer = writer;
/*  67 */     this.charsetDecoder = Charset.forName(charset).newDecoder();
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(ByteBuffer buffer, IoCallback callback) {
/*  72 */     this.sendThread = Thread.currentThread();
/*  73 */     if (this.inCall == Thread.currentThread()) {
/*  74 */       queue(new ByteBuffer[] { buffer }, callback);
/*     */       return;
/*     */     } 
/*  77 */     if (writeBuffer(buffer, callback)) {
/*  78 */       invokeOnComplete(callback);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void send(ByteBuffer[] buffer, IoCallback callback) {
/*  85 */     this.sendThread = Thread.currentThread();
/*  86 */     if (this.inCall == Thread.currentThread()) {
/*  87 */       queue(buffer, callback);
/*     */       return;
/*     */     } 
/*  90 */     for (ByteBuffer b : buffer) {
/*  91 */       if (!writeBuffer(b, callback)) {
/*     */         return;
/*     */       }
/*     */     } 
/*  95 */     invokeOnComplete(callback);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(String data, IoCallback callback) {
/* 100 */     this.sendThread = Thread.currentThread();
/* 101 */     if (this.inCall == Thread.currentThread()) {
/* 102 */       queue(data, callback);
/*     */       return;
/*     */     } 
/* 105 */     this.writer.write(data);
/*     */     
/* 107 */     invokeOnComplete(callback);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(ByteBuffer buffer) {
/* 112 */     send(buffer, IoCallback.END_EXCHANGE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(ByteBuffer[] buffer) {
/* 117 */     send(buffer, IoCallback.END_EXCHANGE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(String data, Charset charset, IoCallback callback) {
/* 122 */     this.sendThread = Thread.currentThread();
/* 123 */     if (this.inCall == Thread.currentThread()) {
/* 124 */       queue(new ByteBuffer[] { ByteBuffer.wrap(data.getBytes(charset)) }, callback);
/*     */       return;
/*     */     } 
/* 127 */     this.writer.write(data);
/* 128 */     invokeOnComplete(callback);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(String data) {
/* 133 */     send(data, IoCallback.END_EXCHANGE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(String data, Charset charset) {
/* 138 */     send(data, charset, IoCallback.END_EXCHANGE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void transferFrom(FileChannel source, IoCallback callback) {
/* 143 */     this.sendThread = Thread.currentThread();
/* 144 */     if (this.inCall == Thread.currentThread()) {
/* 145 */       queue(source, callback);
/*     */       return;
/*     */     } 
/* 148 */     performTransfer(source, callback);
/*     */   }
/*     */ 
/*     */   
/*     */   private void performTransfer(FileChannel source, IoCallback callback) {
/* 153 */     ByteBuffer buffer = ByteBuffer.allocate(128);
/*     */     try {
/* 155 */       long pos = source.position();
/* 156 */       long size = source.size();
/* 157 */       while (size - pos > 0L) {
/* 158 */         int ret = source.read(buffer);
/* 159 */         if (ret <= 0) {
/*     */           break;
/*     */         }
/* 162 */         pos += ret;
/* 163 */         buffer.flip();
/* 164 */         if (!writeBuffer(buffer, callback)) {
/*     */           return;
/*     */         }
/* 167 */         buffer.clear();
/*     */       } 
/*     */       
/* 170 */       if (pos != size) {
/* 171 */         throw new EOFException("Unexpected EOF reading file");
/*     */       }
/*     */     }
/* 174 */     catch (IOException e) {
/* 175 */       callback.onException(this.exchange, this, e);
/*     */     } 
/* 177 */     invokeOnComplete(callback);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(IoCallback callback) {
/* 183 */     this.writer.close();
/* 184 */     if (this.writer.checkError()) {
/* 185 */       callback.onException(this.exchange, this, new IOException());
/*     */     } else {
/* 187 */       invokeOnComplete(callback);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 193 */     if (((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getDispatcherType() != DispatcherType.INCLUDE) {
/* 194 */       IoUtils.safeClose(this.writer);
/*     */     }
/* 196 */     this.writer.checkError();
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean writeBuffer(ByteBuffer buffer, IoCallback callback) {
/* 201 */     StringBuilder builder = new StringBuilder();
/*     */     try {
/* 203 */       builder.append(this.charsetDecoder.decode(buffer));
/* 204 */     } catch (CharacterCodingException e) {
/* 205 */       callback.onException(this.exchange, this, e);
/* 206 */       return false;
/*     */     } 
/* 208 */     String data = builder.toString();
/* 209 */     this.writer.write(data);
/* 210 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeOnComplete(IoCallback callback) {
/* 215 */     this.sendThread = null;
/* 216 */     this.inCall = Thread.currentThread();
/*     */     try {
/* 218 */       callback.onComplete(this.exchange, this);
/*     */     } finally {
/* 220 */       this.inCall = null;
/*     */     } 
/* 222 */     if (Thread.currentThread() != this.sendThread) {
/*     */       return;
/*     */     }
/* 225 */     while (this.next != null) {
/* 226 */       String next = this.next;
/* 227 */       IoCallback queuedCallback = this.queuedCallback;
/* 228 */       this.next = null;
/* 229 */       this.queuedCallback = null;
/* 230 */       this.writer.write(next);
/* 231 */       if (this.writer.checkError()) {
/* 232 */         queuedCallback.onException(this.exchange, this, new IOException()); continue;
/*     */       } 
/* 234 */       this.sendThread = null;
/* 235 */       this.inCall = Thread.currentThread();
/*     */       try {
/* 237 */         queuedCallback.onComplete(this.exchange, this);
/*     */       } finally {
/* 239 */         this.inCall = null;
/*     */       } 
/* 241 */       if (Thread.currentThread() != this.sendThread) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void queue(ByteBuffer[] byteBuffers, IoCallback ioCallback) {
/* 250 */     if (this.next != null || this.pendingFile != null) {
/* 251 */       throw UndertowMessages.MESSAGES.dataAlreadyQueued();
/*     */     }
/* 253 */     StringBuilder builder = new StringBuilder();
/* 254 */     for (ByteBuffer buffer : byteBuffers) {
/*     */       try {
/* 256 */         builder.append(this.charsetDecoder.decode(buffer));
/* 257 */       } catch (CharacterCodingException e) {
/* 258 */         ioCallback.onException(this.exchange, this, e);
/*     */         return;
/*     */       } 
/*     */     } 
/* 262 */     this.next = builder.toString();
/* 263 */     this.queuedCallback = ioCallback;
/*     */   }
/*     */ 
/*     */   
/*     */   private void queue(String data, IoCallback callback) {
/* 268 */     if (this.next != null || this.pendingFile != null) {
/* 269 */       throw UndertowMessages.MESSAGES.dataAlreadyQueued();
/*     */     }
/* 271 */     this.next = data;
/* 272 */     this.queuedCallback = callback;
/*     */   }
/*     */   
/*     */   private void queue(FileChannel data, IoCallback callback) {
/* 276 */     if (this.next != null || this.pendingFile != null) {
/* 277 */       throw UndertowMessages.MESSAGES.dataAlreadyQueued();
/*     */     }
/* 279 */     this.pendingFile = data;
/* 280 */     this.queuedCallback = callback;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\BlockingWriterSenderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */