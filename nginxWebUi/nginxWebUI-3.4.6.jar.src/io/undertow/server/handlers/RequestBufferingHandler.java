/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.server.protocol.http.HttpContinue;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.StreamSourceChannel;
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
/*     */ public class RequestBufferingHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler next;
/*     */   private final int maxBuffers;
/*     */   
/*     */   public RequestBufferingHandler(HttpHandler next, int maxBuffers) {
/*  50 */     this.next = next;
/*  51 */     this.maxBuffers = maxBuffers;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleRequest(final HttpServerExchange exchange) throws Exception {
/*  57 */     if (!exchange.isRequestComplete() && !HttpContinue.requiresContinueResponse(exchange.getRequestHeaders())) {
/*  58 */       StreamSourceChannel channel = exchange.getRequestChannel();
/*  59 */       int readBuffers = 0;
/*  60 */       final PooledByteBuffer[] bufferedData = new PooledByteBuffer[this.maxBuffers];
/*  61 */       PooledByteBuffer buffer = exchange.getConnection().getByteBufferPool().allocate();
/*     */       
/*     */       try {
/*     */         while (true) {
/*  65 */           ByteBuffer b = buffer.getBuffer();
/*  66 */           int r = channel.read(b);
/*  67 */           if (r == -1) {
/*  68 */             if (b.position() == 0) {
/*  69 */               buffer.close(); break;
/*     */             } 
/*  71 */             b.flip();
/*  72 */             bufferedData[readBuffers] = buffer;
/*     */             break;
/*     */           } 
/*  75 */           if (r == 0) {
/*  76 */             final PooledByteBuffer finalBuffer = buffer;
/*  77 */             final int finalReadBuffers = readBuffers;
/*  78 */             channel.getReadSetter().set(new ChannelListener<StreamSourceChannel>()
/*     */                 {
/*  80 */                   PooledByteBuffer buffer = finalBuffer;
/*  81 */                   int readBuffers = finalReadBuffers;
/*     */ 
/*     */ 
/*     */                   
/*     */                   public void handleEvent(StreamSourceChannel channel) {
/*     */                     try {
/*     */                       while (true) {
/*  88 */                         ByteBuffer b = this.buffer.getBuffer();
/*  89 */                         int r = channel.read(b);
/*  90 */                         if (r == -1) {
/*  91 */                           if (b.position() == 0) {
/*  92 */                             this.buffer.close();
/*     */                           } else {
/*  94 */                             b.flip();
/*  95 */                             bufferedData[this.readBuffers] = this.buffer;
/*     */                           } 
/*  97 */                           Connectors.ungetRequestBytes(exchange, bufferedData);
/*  98 */                           Connectors.resetRequestChannel(exchange);
/*  99 */                           channel.getReadSetter().set(null);
/* 100 */                           channel.suspendReads();
/* 101 */                           Connectors.executeRootHandler(RequestBufferingHandler.this.next, exchange); return;
/*     */                         } 
/* 103 */                         if (r == 0)
/*     */                           return; 
/* 105 */                         if (!b.hasRemaining()) {
/* 106 */                           b.flip();
/* 107 */                           bufferedData[this.readBuffers++] = this.buffer;
/* 108 */                           if (this.readBuffers == RequestBufferingHandler.this.maxBuffers) {
/* 109 */                             Connectors.ungetRequestBytes(exchange, bufferedData);
/* 110 */                             Connectors.resetRequestChannel(exchange);
/* 111 */                             channel.getReadSetter().set(null);
/* 112 */                             channel.suspendReads();
/* 113 */                             Connectors.executeRootHandler(RequestBufferingHandler.this.next, exchange);
/*     */                             return;
/*     */                           } 
/* 116 */                           this.buffer = exchange.getConnection().getByteBufferPool().allocate();
/*     */                         } 
/*     */                       } 
/* 119 */                     } catch (Throwable t) {
/* 120 */                       if (t instanceof IOException) {
/* 121 */                         UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)t);
/*     */                       } else {
/* 123 */                         UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(t);
/*     */                       } 
/* 125 */                       for (int i = 0; i < bufferedData.length; i++) {
/* 126 */                         IoUtils.safeClose((Closeable)bufferedData[i]);
/*     */                       }
/* 128 */                       if (this.buffer != null && this.buffer.isOpen()) {
/* 129 */                         IoUtils.safeClose((Closeable)this.buffer);
/*     */                       }
/* 131 */                       exchange.endExchange();
/*     */                       return;
/*     */                     }  }
/*     */                 });
/* 135 */             channel.resumeReads(); return;
/*     */           } 
/* 137 */           if (!b.hasRemaining()) {
/* 138 */             b.flip();
/* 139 */             bufferedData[readBuffers++] = buffer;
/* 140 */             if (readBuffers == this.maxBuffers) {
/*     */               break;
/*     */             }
/* 143 */             buffer = exchange.getConnection().getByteBufferPool().allocate();
/*     */           } 
/*     */         } 
/* 146 */         Connectors.ungetRequestBytes(exchange, bufferedData);
/* 147 */         Connectors.resetRequestChannel(exchange);
/* 148 */       } catch (Exception|Error e) {
/* 149 */         for (int i = 0; i < bufferedData.length; i++) {
/* 150 */           IoUtils.safeClose((Closeable)bufferedData[i]);
/*     */         }
/* 152 */         if (buffer != null && buffer.isOpen()) {
/* 153 */           IoUtils.safeClose((Closeable)buffer);
/*     */         }
/* 155 */         throw e;
/*     */       } 
/*     */     } 
/* 158 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   public static final class Wrapper
/*     */     implements HandlerWrapper {
/*     */     private final int maxBuffers;
/*     */     
/*     */     public Wrapper(int maxBuffers) {
/* 166 */       this.maxBuffers = maxBuffers;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 171 */       return new RequestBufferingHandler(handler, this.maxBuffers);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 177 */     return "buffer-request( " + this.maxBuffers + " )";
/*     */   }
/*     */   
/*     */   public static final class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 184 */       return "buffer-request";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 189 */       return Collections.singletonMap("buffers", Integer.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 194 */       return Collections.singleton("buffers");
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 199 */       return "buffers";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 204 */       return new RequestBufferingHandler.Wrapper(((Integer)config.get("buffers")).intValue());
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\RequestBufferingHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */