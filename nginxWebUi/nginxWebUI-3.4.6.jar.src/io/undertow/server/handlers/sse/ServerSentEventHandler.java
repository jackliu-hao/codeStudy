/*     */ package io.undertow.server.handlers.sse;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.PathTemplateMatch;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.Executor;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
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
/*     */ public class ServerSentEventHandler
/*     */   implements HttpHandler
/*     */ {
/*  44 */   private static final HttpString LAST_EVENT_ID = new HttpString("Last-Event-ID");
/*     */   
/*     */   private final ServerSentEventConnectionCallback callback;
/*     */   
/*  48 */   private final Set<ServerSentEventConnection> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());
/*     */   
/*     */   public ServerSentEventHandler(ServerSentEventConnectionCallback callback) {
/*  51 */     this.callback = callback;
/*     */   }
/*     */   
/*     */   public ServerSentEventHandler() {
/*  55 */     this.callback = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(final HttpServerExchange exchange) throws Exception {
/*  60 */     exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/event-stream; charset=UTF-8");
/*  61 */     exchange.setPersistent(false);
/*  62 */     final StreamSinkChannel sink = exchange.getResponseChannel();
/*  63 */     if (!sink.flush()) {
/*  64 */       sink.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkChannel>()
/*     */             {
/*     */               public void handleEvent(StreamSinkChannel channel) {
/*  67 */                 ServerSentEventHandler.this.handleConnect(channel, exchange);
/*     */               }
/*     */             }new ChannelExceptionHandler<StreamSinkChannel>()
/*     */             {
/*     */               public void handleException(StreamSinkChannel channel, IOException exception) {
/*  72 */                 IoUtils.safeClose((Closeable)exchange.getConnection());
/*     */               }
/*     */             }));
/*  75 */       sink.resumeWrites();
/*     */     } else {
/*  77 */       exchange.dispatch((Executor)exchange.getIoThread(), new Runnable()
/*     */           {
/*     */             public void run() {
/*  80 */               ServerSentEventHandler.this.handleConnect(sink, exchange);
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */   
/*     */   private void handleConnect(StreamSinkChannel channel, HttpServerExchange exchange) {
/*  87 */     UndertowLogger.REQUEST_LOGGER.debugf("Opened SSE connection to %s", exchange);
/*  88 */     final ServerSentEventConnection connection = new ServerSentEventConnection(exchange, channel);
/*  89 */     PathTemplateMatch pt = (PathTemplateMatch)exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
/*  90 */     if (pt != null) {
/*  91 */       for (Map.Entry<String, String> p : (Iterable<Map.Entry<String, String>>)pt.getParameters().entrySet()) {
/*  92 */         connection.setParameter(p.getKey(), p.getValue());
/*     */       }
/*     */     }
/*  95 */     this.connections.add(connection);
/*  96 */     connection.addCloseTask(new ChannelListener<ServerSentEventConnection>()
/*     */         {
/*     */           public void handleEvent(ServerSentEventConnection channel) {
/*  99 */             ServerSentEventHandler.this.connections.remove(connection);
/*     */           }
/*     */         });
/* 102 */     if (this.callback != null) {
/* 103 */       this.callback.connected(connection, exchange.getRequestHeaders().getLast(LAST_EVENT_ID));
/*     */     }
/*     */   }
/*     */   
/*     */   public Set<ServerSentEventConnection> getConnections() {
/* 108 */     return Collections.unmodifiableSet(this.connections);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\sse\ServerSentEventHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */