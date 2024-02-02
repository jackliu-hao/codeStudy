/*     */ package org.noear.solon.boot.undertow;
/*     */ 
/*     */ import io.undertow.Handlers;
/*     */ import io.undertow.Undertow;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.servlet.Servlets;
/*     */ import io.undertow.servlet.api.DeploymentInfo;
/*     */ import io.undertow.servlet.api.DeploymentManager;
/*     */ import io.undertow.servlet.api.ServletContainer;
/*     */ import io.undertow.servlet.api.ServletInfo;
/*     */ import io.undertow.websockets.WebSocketConnectionCallback;
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.SolonApp;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.boot.ServerLifecycle;
/*     */ import org.noear.solon.boot.ServerProps;
/*     */ import org.noear.solon.boot.ssl.SslContextFactory;
/*     */ import org.noear.solon.boot.undertow.http.UtHandlerJspHandler;
/*     */ import org.noear.solon.boot.undertow.websocket.UtWsConnectionCallback;
/*     */ import org.noear.solon.boot.undertow.websocket._SessionManagerImpl;
/*     */ import org.noear.solon.core.event.EventBus;
/*     */ import org.noear.solon.socketd.SessionManager;
/*     */ 
/*     */ class UndertowServer
/*     */   extends UndertowServerBase
/*     */   implements ServerLifecycle
/*     */ {
/*     */   protected Undertow _server;
/*     */   
/*     */   public void start(String host, int port) {
/*     */     try {
/*  33 */       setup(Solon.app(), host, port);
/*     */       
/*  35 */       this._server.start();
/*  36 */     } catch (RuntimeException e) {
/*  37 */       throw e;
/*  38 */     } catch (Throwable e) {
/*  39 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() throws Throwable {
/*  45 */     if (this._server != null) {
/*  46 */       this._server.stop();
/*  47 */       this._server = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void setup(SolonApp app, String host, int port) throws Throwable {
/*  52 */     HttpHandler httpHandler = buildHandler();
/*     */ 
/*     */     
/*  55 */     Undertow.Builder builder = Undertow.builder();
/*     */     
/*  57 */     builder.setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, Boolean.valueOf(false));
/*     */     
/*  59 */     if (ServerProps.request_maxHeaderSize != 0) {
/*  60 */       builder.setServerOption(UndertowOptions.MAX_HEADER_SIZE, Integer.valueOf(ServerProps.request_maxHeaderSize));
/*     */     }
/*     */     
/*  63 */     if (ServerProps.request_maxFileSize != 0) {
/*  64 */       builder.setServerOption(UndertowOptions.MAX_ENTITY_SIZE, Long.valueOf(ServerProps.request_maxFileSize));
/*     */     }
/*     */     
/*  67 */     if (Utils.isEmpty(host)) {
/*  68 */       host = "0.0.0.0";
/*     */     }
/*     */     
/*  71 */     if (System.getProperty("javax.net.ssl.keyStore") == null) {
/*     */       
/*  73 */       builder.addHttpListener(port, host);
/*     */     } else {
/*     */       
/*  76 */       builder.addHttpsListener(port, host, SslContextFactory.create());
/*     */     } 
/*     */     
/*  79 */     if (app.enableWebSocket()) {
/*  80 */       builder.setHandler((HttpHandler)Handlers.websocket((WebSocketConnectionCallback)new UtWsConnectionCallback(), httpHandler));
/*     */       
/*  82 */       SessionManager.register((SessionManager)new _SessionManagerImpl());
/*     */     } else {
/*  84 */       builder.setHandler(httpHandler);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  89 */     EventBus.push(builder);
/*     */     
/*  91 */     this._server = builder.build();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpHandler buildHandler() throws Exception {
/*  97 */     DeploymentInfo builder = initDeploymentInfo();
/*     */ 
/*     */     
/* 100 */     builder.addServlet((new ServletInfo("ACTServlet", UtHandlerJspHandler.class)).addMapping("/"));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 105 */     ServletContainer container = Servlets.defaultContainer();
/* 106 */     DeploymentManager manager = container.addDeployment(builder);
/* 107 */     manager.deploy();
/*     */     
/* 109 */     return manager.start();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boo\\undertow\UndertowServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */