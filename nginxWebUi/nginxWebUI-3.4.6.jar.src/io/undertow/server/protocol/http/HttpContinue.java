/*     */ package io.undertow.server.protocol.http;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.io.IoCallback;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Protocols;
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.channels.StreamSinkChannel;
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
/*     */ public class HttpContinue
/*     */ {
/*     */   private static final Set<HttpString> COMPATIBLE_PROTOCOLS;
/*     */   public static final String CONTINUE = "100-continue";
/*     */   
/*     */   static {
/*  57 */     Set<HttpString> compat = new HashSet<>();
/*  58 */     compat.add(Protocols.HTTP_1_1);
/*  59 */     compat.add(Protocols.HTTP_2_0);
/*  60 */     COMPATIBLE_PROTOCOLS = Collections.unmodifiableSet(compat);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  65 */   private static final AttachmentKey<Boolean> ALREADY_SENT = AttachmentKey.create(Boolean.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean requiresContinueResponse(HttpServerExchange exchange) {
/*  74 */     if (!COMPATIBLE_PROTOCOLS.contains(exchange.getProtocol()) || exchange.isResponseStarted() || !exchange.getConnection().isContinueResponseSupported() || exchange.getAttachment(ALREADY_SENT) != null) {
/*  75 */       return false;
/*     */     }
/*     */     
/*  78 */     HeaderMap requestHeaders = exchange.getRequestHeaders();
/*  79 */     return requiresContinueResponse(requestHeaders);
/*     */   }
/*     */   
/*     */   public static boolean requiresContinueResponse(HeaderMap requestHeaders) {
/*  83 */     HeaderValues headerValues = requestHeaders.get(Headers.EXPECT);
/*  84 */     if (headerValues != null) {
/*  85 */       for (String header : headerValues) {
/*  86 */         if (header.equalsIgnoreCase("100-continue")) {
/*  87 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/*  91 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean isContinueResponseSent(HttpServerExchange exchange) {
/*  95 */     return (exchange.getAttachment(ALREADY_SENT) != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void sendContinueResponse(HttpServerExchange exchange, IoCallback callback) {
/* 105 */     if (!exchange.isResponseChannelAvailable()) {
/* 106 */       callback.onException(exchange, null, UndertowMessages.MESSAGES.cannotSendContinueResponse());
/*     */       return;
/*     */     } 
/* 109 */     internalSendContinueResponse(exchange, callback);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ContinueResponseSender createResponseSender(HttpServerExchange exchange) throws IOException {
/* 119 */     if (!exchange.isResponseChannelAvailable()) {
/* 120 */       throw UndertowMessages.MESSAGES.cannotSendContinueResponse();
/*     */     }
/* 122 */     if (exchange.getAttachment(ALREADY_SENT) != null)
/*     */     {
/* 124 */       return new ContinueResponseSender()
/*     */         {
/*     */           public boolean send() throws IOException {
/* 127 */             return true;
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public void awaitWritable() throws IOException {}
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {}
/*     */         };
/*     */     }
/* 142 */     HttpServerExchange newExchange = exchange.getConnection().sendOutOfBandResponse(exchange);
/* 143 */     exchange.putAttachment(ALREADY_SENT, Boolean.valueOf(true));
/* 144 */     newExchange.setStatusCode(100);
/* 145 */     newExchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, 0L);
/* 146 */     final StreamSinkChannel responseChannel = newExchange.getResponseChannel();
/* 147 */     return new ContinueResponseSender()
/*     */       {
/*     */         boolean shutdown = false;
/*     */         
/*     */         public boolean send() throws IOException {
/* 152 */           if (!this.shutdown) {
/* 153 */             this.shutdown = true;
/* 154 */             responseChannel.shutdownWrites();
/*     */           } 
/* 156 */           return responseChannel.flush();
/*     */         }
/*     */ 
/*     */         
/*     */         public void awaitWritable() throws IOException {
/* 161 */           responseChannel.awaitWritable();
/*     */         }
/*     */ 
/*     */         
/*     */         public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 166 */           responseChannel.awaitWritable(time, timeUnit);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void markContinueResponseSent(HttpServerExchange exchange) {
/* 178 */     exchange.putAttachment(ALREADY_SENT, Boolean.valueOf(true));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void sendContinueResponseBlocking(HttpServerExchange exchange) throws IOException {
/* 187 */     if (!exchange.isResponseChannelAvailable()) {
/* 188 */       throw UndertowMessages.MESSAGES.cannotSendContinueResponse();
/*     */     }
/* 190 */     if (exchange.getAttachment(ALREADY_SENT) != null) {
/*     */       return;
/*     */     }
/* 193 */     HttpServerExchange newExchange = exchange.getConnection().sendOutOfBandResponse(exchange);
/* 194 */     exchange.putAttachment(ALREADY_SENT, Boolean.valueOf(true));
/* 195 */     newExchange.setStatusCode(100);
/* 196 */     newExchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, 0L);
/* 197 */     newExchange.startBlocking();
/* 198 */     newExchange.getOutputStream().close();
/* 199 */     newExchange.getInputStream().close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void rejectExchange(HttpServerExchange exchange) {
/* 208 */     exchange.setStatusCode(417);
/* 209 */     exchange.setPersistent(false);
/* 210 */     exchange.endExchange();
/*     */   }
/*     */ 
/*     */   
/*     */   private static void internalSendContinueResponse(final HttpServerExchange exchange, final IoCallback callback) {
/* 215 */     if (exchange.getAttachment(ALREADY_SENT) != null) {
/* 216 */       callback.onComplete(exchange, null);
/*     */       return;
/*     */     } 
/* 219 */     HttpServerExchange newExchange = exchange.getConnection().sendOutOfBandResponse(exchange);
/* 220 */     exchange.putAttachment(ALREADY_SENT, Boolean.valueOf(true));
/* 221 */     newExchange.setStatusCode(100);
/* 222 */     newExchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, 0L);
/* 223 */     StreamSinkChannel responseChannel = newExchange.getResponseChannel();
/*     */     try {
/* 225 */       responseChannel.shutdownWrites();
/* 226 */       if (!responseChannel.flush()) {
/* 227 */         responseChannel.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkChannel>()
/*     */               {
/*     */                 public void handleEvent(StreamSinkChannel channel)
/*     */                 {
/* 231 */                   channel.suspendWrites();
/* 232 */                   exchange.dispatch(new HttpHandler()
/*     */                       {
/*     */                         public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 235 */                           callback.onComplete(exchange, null);
/*     */                         }
/*     */                       });
/*     */                 }
/*     */               }new ChannelExceptionHandler<Channel>()
/*     */               {
/*     */                 public void handleException(Channel channel, final IOException e) {
/* 242 */                   exchange.dispatch(new HttpHandler()
/*     */                       {
/*     */                         public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 245 */                           callback.onException(exchange, null, e);
/*     */                         }
/*     */                       });
/*     */                 }
/*     */               }));
/* 250 */         responseChannel.resumeWrites();
/* 251 */         exchange.dispatch();
/*     */       } else {
/* 253 */         callback.onComplete(exchange, null);
/*     */       } 
/* 255 */     } catch (IOException e) {
/* 256 */       callback.onException(exchange, null, e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static interface ContinueResponseSender {
/*     */     boolean send() throws IOException;
/*     */     
/*     */     void awaitWritable() throws IOException;
/*     */     
/*     */     void awaitWritable(long param1Long, TimeUnit param1TimeUnit) throws IOException;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http\HttpContinue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */