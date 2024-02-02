/*     */ package io.undertow.servlet.websockets;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.HttpUpgradeListener;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import io.undertow.websockets.WebSocketConnectionCallback;
/*     */ import io.undertow.websockets.core.WebSocketChannel;
/*     */ import io.undertow.websockets.core.protocol.Handshake;
/*     */ import io.undertow.websockets.core.protocol.version07.Hybi07Handshake;
/*     */ import io.undertow.websockets.core.protocol.version08.Hybi08Handshake;
/*     */ import io.undertow.websockets.core.protocol.version13.Hybi13Handshake;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebSocketServlet
/*     */   extends HttpServlet
/*     */ {
/*     */   public static final String SESSION_HANDLER = "io.undertow.handler";
/*     */   private final List<Handshake> handshakes;
/*     */   private WebSocketConnectionCallback callback;
/*     */   private Set<WebSocketChannel> peerConnections;
/*     */   
/*     */   public WebSocketServlet() {
/*  60 */     this.handshakes = handshakes();
/*     */   }
/*     */   
/*     */   public WebSocketServlet(WebSocketConnectionCallback callback) {
/*  64 */     this.callback = callback;
/*  65 */     this.handshakes = handshakes();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(ServletConfig config) throws ServletException {
/*  71 */     super.init(config);
/*  72 */     this.peerConnections = Collections.newSetFromMap(new ConcurrentHashMap<>());
/*     */     try {
/*  74 */       String sessionHandler = config.getInitParameter("io.undertow.handler");
/*  75 */       if (sessionHandler != null) {
/*  76 */         Class<?> clazz = Class.forName(sessionHandler, true, Thread.currentThread().getContextClassLoader());
/*  77 */         Object handler = clazz.newInstance();
/*  78 */         this.callback = (WebSocketConnectionCallback)handler;
/*     */       }
/*     */     
/*     */     }
/*  82 */     catch (ClassNotFoundException e) {
/*  83 */       throw new ServletException(e);
/*  84 */     } catch (InstantiationException e) {
/*  85 */       throw new ServletException(e);
/*  86 */     } catch (IllegalAccessException e) {
/*  87 */       throw new ServletException(e);
/*     */     } 
/*  89 */     if (this.callback == null) {
/*  90 */       throw UndertowServletMessages.MESSAGES.noWebSocketHandler();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/*  97 */     final ServletWebSocketHttpExchange facade = new ServletWebSocketHttpExchange(req, resp, this.peerConnections);
/*  98 */     Handshake handshaker = null;
/*  99 */     for (Handshake method : this.handshakes) {
/* 100 */       if (method.matches(facade)) {
/* 101 */         handshaker = method;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 106 */     if (handshaker == null) {
/* 107 */       UndertowLogger.REQUEST_LOGGER.debug("Could not find hand shaker for web socket request");
/* 108 */       resp.sendError(400);
/*     */       return;
/*     */     } 
/* 111 */     final Handshake selected = handshaker;
/* 112 */     facade.upgradeChannel(new HttpUpgradeListener()
/*     */         {
/*     */           public void handleUpgrade(StreamConnection streamConnection, HttpServerExchange exchange) {
/* 115 */             WebSocketChannel channel = selected.createChannel(facade, streamConnection, facade.getBufferPool());
/* 116 */             WebSocketServlet.this.peerConnections.add(channel);
/* 117 */             WebSocketServlet.this.callback.onConnect(facade, channel);
/*     */           }
/*     */         });
/* 120 */     handshaker.handshake(facade);
/*     */   }
/*     */   
/*     */   protected List<Handshake> handshakes() {
/* 124 */     List<Handshake> handshakes = new ArrayList<>();
/* 125 */     handshakes.add(new Hybi13Handshake());
/* 126 */     handshakes.add(new Hybi08Handshake());
/* 127 */     handshakes.add(new Hybi07Handshake());
/* 128 */     return handshakes;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\websockets\WebSocketServlet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */