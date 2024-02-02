/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.predicate.Predicate;
/*     */ import io.undertow.predicate.Predicates;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.HttpUpgradeListener;
/*     */ import io.undertow.util.Methods;
/*     */ import io.undertow.util.SameThreadExecutor;
/*     */ import io.undertow.util.Transfer;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.channels.Channel;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoFuture;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.channels.StreamSinkChannel;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConnectHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler next;
/*     */   private final Predicate allowed;
/*     */   
/*     */   public ConnectHandler(HttpHandler next) {
/*  60 */     this(next, Predicates.truePredicate());
/*     */   }
/*     */   
/*     */   public ConnectHandler(HttpHandler next, Predicate allowed) {
/*  64 */     this.next = next;
/*  65 */     this.allowed = allowed;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(final HttpServerExchange exchange) throws Exception {
/*  70 */     if (exchange.getRequestMethod().equals(Methods.CONNECT)) {
/*  71 */       if (!this.allowed.resolve(exchange)) {
/*  72 */         exchange.setStatusCode(405);
/*     */         return;
/*     */       } 
/*  75 */       String[] parts = exchange.getRequestPath().split(":");
/*  76 */       if (parts.length != 2) {
/*  77 */         exchange.setStatusCode(400);
/*     */         return;
/*     */       } 
/*  80 */       final String host = parts[0];
/*  81 */       final Integer port = Integer.valueOf(Integer.parseInt(parts[1]));
/*  82 */       exchange.dispatch(SameThreadExecutor.INSTANCE, new Runnable()
/*     */           {
/*     */             public void run() {
/*  85 */               exchange.getConnection().getIoThread().openStreamConnection(new InetSocketAddress(host, port.intValue()), new ChannelListener<StreamConnection>()
/*     */                   {
/*     */                     public void handleEvent(final StreamConnection clientChannel) {
/*  88 */                       exchange.acceptConnectRequest(new HttpUpgradeListener()
/*     */                           {
/*     */                             public void handleUpgrade(StreamConnection streamConnection, HttpServerExchange exchange) {
/*  91 */                               ConnectHandler.ClosingExceptionHandler handler = new ConnectHandler.ClosingExceptionHandler(new Closeable[] { (Closeable)streamConnection, (Closeable)this.val$clientChannel });
/*  92 */                               Transfer.initiateTransfer((StreamSourceChannel)clientChannel.getSourceChannel(), (StreamSinkChannel)streamConnection.getSinkChannel(), ChannelListeners.closingChannelListener(), ChannelListeners.writeShutdownChannelListener(ChannelListeners.flushingChannelListener(ChannelListeners.closingChannelListener(), ChannelListeners.closingChannelExceptionHandler()), ChannelListeners.closingChannelExceptionHandler()), handler, handler, exchange.getConnection().getByteBufferPool());
/*  93 */                               Transfer.initiateTransfer((StreamSourceChannel)streamConnection.getSourceChannel(), (StreamSinkChannel)clientChannel.getSinkChannel(), ChannelListeners.closingChannelListener(), ChannelListeners.writeShutdownChannelListener(ChannelListeners.flushingChannelListener(ChannelListeners.closingChannelListener(), ChannelListeners.closingChannelExceptionHandler()), ChannelListeners.closingChannelExceptionHandler()), handler, handler, exchange.getConnection().getByteBufferPool());
/*     */                             }
/*     */                           });
/*  96 */                       exchange.setStatusCode(200);
/*  97 */                       exchange.endExchange();
/*     */                     }
/*  99 */                   }OptionMap.create(Options.TCP_NODELAY, Boolean.valueOf(true))).addNotifier(new IoFuture.Notifier<StreamConnection, Object>()
/*     */                   {
/*     */                     public void notify(IoFuture<? extends StreamConnection> ioFuture, Object attachment) {
/* 102 */                       if (ioFuture.getStatus() == IoFuture.Status.FAILED) {
/* 103 */                         exchange.setStatusCode(503);
/* 104 */                         exchange.endExchange();
/*     */                       } 
/*     */                     }
/*     */                   },  null);
/*     */             }
/*     */           });
/*     */     } else {
/* 111 */       this.next.handleRequest(exchange);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final class ClosingExceptionHandler
/*     */     implements ChannelExceptionHandler<Channel>
/*     */   {
/*     */     private final Closeable[] toClose;
/*     */     
/*     */     private ClosingExceptionHandler(Closeable... toClose) {
/* 121 */       this.toClose = toClose;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void handleException(Channel channel, IOException exception) {
/* 127 */       IoUtils.safeClose(channel);
/* 128 */       IoUtils.safeClose(this.toClose);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\ConnectHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */