/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.Handlers;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.HttpUpgradeListener;
/*     */ import io.undertow.util.CopyOnWriteMap;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.Methods;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.StreamConnection;
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
/*     */ public final class ChannelUpgradeHandler
/*     */   implements HttpHandler
/*     */ {
/*  43 */   private final CopyOnWriteMap<String, List<Holder>> handlers = new CopyOnWriteMap();
/*  44 */   private volatile HttpHandler nonUpgradeHandler = ResponseCodeHandler.HANDLE_404;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void addProtocol(String productString, ChannelListener<? super StreamConnection> openListener, HttpUpgradeHandshake handshake) {
/*  54 */     addProtocol(productString, null, openListener, handshake);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void addProtocol(String productString, HttpUpgradeListener openListener, HttpUpgradeHandshake handshake) {
/*  65 */     addProtocol(productString, openListener, null, handshake);
/*     */   }
/*     */   
/*     */   private synchronized void addProtocol(String productString, HttpUpgradeListener openListener, final ChannelListener<? super StreamConnection> channelListener, HttpUpgradeHandshake handshake) {
/*  69 */     if (productString == null) {
/*  70 */       throw new IllegalArgumentException("productString is null");
/*     */     }
/*  72 */     if (openListener == null && channelListener == null) {
/*  73 */       throw new IllegalArgumentException("openListener is null");
/*     */     }
/*  75 */     if (openListener == null) {
/*  76 */       openListener = new HttpUpgradeListener()
/*     */         {
/*     */           public void handleUpgrade(StreamConnection streamConnection, HttpServerExchange exchange) {
/*  79 */             ChannelListeners.invokeChannelListener((Channel)streamConnection, channelListener);
/*     */           }
/*     */         };
/*     */     }
/*     */     
/*  84 */     List<Holder> list = (List<Holder>)this.handlers.get(productString);
/*  85 */     if (list == null) {
/*  86 */       this.handlers.put(productString, list = new CopyOnWriteArrayList<>());
/*     */     }
/*  88 */     list.add(new Holder(openListener, handshake, channelListener));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addProtocol(String productString, ChannelListener<? super StreamConnection> openListener) {
/*  98 */     addProtocol(productString, openListener, (HttpUpgradeHandshake)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addProtocol(String productString, HttpUpgradeListener openListener) {
/* 108 */     addProtocol(productString, openListener, (HttpUpgradeHandshake)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void removeProtocol(String productString) {
/* 117 */     this.handlers.remove(productString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void removeProtocol(String productString, ChannelListener<? super StreamConnection> openListener) {
/* 127 */     List<Holder> holders = (List<Holder>)this.handlers.get(productString);
/* 128 */     if (holders == null) {
/*     */       return;
/*     */     }
/* 131 */     Iterator<Holder> it = holders.iterator();
/* 132 */     while (it.hasNext()) {
/* 133 */       Holder holder = it.next();
/* 134 */       if (holder.channelListener == openListener) {
/* 135 */         holders.remove(holder);
/*     */         break;
/*     */       } 
/*     */     } 
/* 139 */     if (holders.isEmpty()) {
/* 140 */       this.handlers.remove(productString);
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
/*     */   public synchronized void removeProtocol(String productString, HttpUpgradeListener upgradeListener) {
/* 152 */     List<Holder> holders = (List<Holder>)this.handlers.get(productString);
/* 153 */     if (holders == null) {
/*     */       return;
/*     */     }
/* 156 */     Iterator<Holder> it = holders.iterator();
/* 157 */     while (it.hasNext()) {
/* 158 */       Holder holder = it.next();
/* 159 */       if (holder.listener == upgradeListener) {
/* 160 */         holders.remove(holder);
/*     */         break;
/*     */       } 
/*     */     } 
/* 164 */     if (holders.isEmpty()) {
/* 165 */       this.handlers.remove(productString);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHandler getNonUpgradeHandler() {
/* 175 */     return this.nonUpgradeHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelUpgradeHandler setNonUpgradeHandler(HttpHandler nonUpgradeHandler) {
/* 184 */     Handlers.handlerNotNull(nonUpgradeHandler);
/* 185 */     this.nonUpgradeHandler = nonUpgradeHandler;
/* 186 */     return this;
/*     */   }
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 190 */     HeaderValues headerValues = exchange.getRequestHeaders().get(Headers.UPGRADE);
/* 191 */     if (headerValues != null && exchange.getRequestMethod().equals(Methods.GET)) {
/* 192 */       for (String string : headerValues) {
/* 193 */         List<Holder> holders = (List<Holder>)this.handlers.get(string);
/* 194 */         if (holders != null) {
/* 195 */           for (Holder holder : holders) {
/* 196 */             HttpUpgradeListener listener = holder.listener;
/* 197 */             if (holder.handshake != null && 
/* 198 */               !holder.handshake.handleUpgrade(exchange)) {
/*     */               continue;
/*     */             }
/*     */ 
/*     */ 
/*     */             
/* 204 */             exchange.upgradeChannel(string, listener);
/* 205 */             exchange.endExchange();
/*     */             return;
/*     */           } 
/*     */         }
/*     */       } 
/*     */     }
/* 211 */     this.nonUpgradeHandler.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   private static final class Holder
/*     */   {
/*     */     final HttpUpgradeListener listener;
/*     */     final HttpUpgradeHandshake handshake;
/*     */     final ChannelListener<? super StreamConnection> channelListener;
/*     */     
/*     */     private Holder(HttpUpgradeListener listener, HttpUpgradeHandshake handshake, ChannelListener<? super StreamConnection> channelListener) {
/* 221 */       this.listener = listener;
/* 222 */       this.handshake = handshake;
/* 223 */       this.channelListener = channelListener;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\ChannelUpgradeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */