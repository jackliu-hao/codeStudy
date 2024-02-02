/*     */ package io.undertow.server.handlers.proxy;
/*     */ 
/*     */ import io.undertow.client.ClientCallback;
/*     */ import io.undertow.client.ClientConnection;
/*     */ import io.undertow.client.UndertowClient;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.ServerConnection;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
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
/*     */ @Deprecated
/*     */ public class SimpleProxyClientProvider
/*     */   implements ProxyClient
/*     */ {
/*     */   private final URI uri;
/*  49 */   private final AttachmentKey<ClientConnection> clientAttachmentKey = AttachmentKey.create(ClientConnection.class);
/*     */   
/*     */   private final UndertowClient client;
/*  52 */   private static final ProxyClient.ProxyTarget TARGET = new ProxyClient.ProxyTarget() {  }
/*     */   ;
/*     */   public SimpleProxyClientProvider(URI uri) {
/*  55 */     this.uri = uri;
/*  56 */     this.client = UndertowClient.getInstance();
/*     */   }
/*     */ 
/*     */   
/*     */   public ProxyClient.ProxyTarget findTarget(HttpServerExchange exchange) {
/*  61 */     return TARGET;
/*     */   }
/*     */ 
/*     */   
/*     */   public void getConnection(ProxyClient.ProxyTarget target, HttpServerExchange exchange, ProxyCallback<ProxyConnection> callback, long timeout, TimeUnit timeUnit) {
/*  66 */     ClientConnection existing = (ClientConnection)exchange.getConnection().getAttachment(this.clientAttachmentKey);
/*  67 */     if (existing != null) {
/*  68 */       if (existing.isOpen()) {
/*     */         
/*  70 */         callback.completed(exchange, new ProxyConnection(existing, (this.uri.getPath() == null) ? "/" : this.uri.getPath()));
/*     */         return;
/*     */       } 
/*  73 */       exchange.getConnection().removeAttachment(this.clientAttachmentKey);
/*     */     } 
/*     */     
/*  76 */     this.client.connect(new ConnectNotifier(callback, exchange), this.uri, exchange.getIoThread(), exchange.getConnection().getByteBufferPool(), OptionMap.EMPTY);
/*     */   }
/*     */   
/*     */   private final class ConnectNotifier implements ClientCallback<ClientConnection> {
/*     */     private final ProxyCallback<ProxyConnection> callback;
/*     */     private final HttpServerExchange exchange;
/*     */     
/*     */     private ConnectNotifier(ProxyCallback<ProxyConnection> callback, HttpServerExchange exchange) {
/*  84 */       this.callback = callback;
/*  85 */       this.exchange = exchange;
/*     */     }
/*     */ 
/*     */     
/*     */     public void completed(final ClientConnection connection) {
/*  90 */       final ServerConnection serverConnection = this.exchange.getConnection();
/*     */       
/*  92 */       serverConnection.putAttachment(SimpleProxyClientProvider.this.clientAttachmentKey, connection);
/*  93 */       serverConnection.addCloseListener(new ServerConnection.CloseListener()
/*     */           {
/*     */             public void closed(ServerConnection serverConnection) {
/*  96 */               IoUtils.safeClose((Closeable)connection);
/*     */             }
/*     */           });
/*  99 */       connection.getCloseSetter().set(new ChannelListener<Channel>()
/*     */           {
/*     */             public void handleEvent(Channel channel) {
/* 102 */               serverConnection.removeAttachment(SimpleProxyClientProvider.this.clientAttachmentKey);
/*     */             }
/*     */           });
/* 105 */       this.callback.completed(this.exchange, new ProxyConnection(connection, (SimpleProxyClientProvider.this.uri.getPath() == null) ? "/" : SimpleProxyClientProvider.this.uri.getPath()));
/*     */     }
/*     */ 
/*     */     
/*     */     public void failed(IOException e) {
/* 110 */       this.callback.failed(this.exchange);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\SimpleProxyClientProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */