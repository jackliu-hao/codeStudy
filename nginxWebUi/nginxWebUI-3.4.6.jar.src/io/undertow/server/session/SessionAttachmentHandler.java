/*     */ package io.undertow.server.session;
/*     */ 
/*     */ import io.undertow.Handlers;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.ResponseCodeHandler;
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
/*     */ public class SessionAttachmentHandler
/*     */   implements HttpHandler
/*     */ {
/*  39 */   private volatile HttpHandler next = (HttpHandler)ResponseCodeHandler.HANDLE_404;
/*     */   
/*     */   private volatile SessionManager sessionManager;
/*     */   
/*     */   private final SessionConfig sessionConfig;
/*     */   
/*     */   public SessionAttachmentHandler(SessionManager sessionManager, SessionConfig sessionConfig) {
/*  46 */     this.sessionConfig = sessionConfig;
/*  47 */     if (sessionManager == null) {
/*  48 */       throw UndertowMessages.MESSAGES.sessionManagerMustNotBeNull();
/*     */     }
/*  50 */     this.sessionManager = sessionManager;
/*     */   }
/*     */   
/*     */   public SessionAttachmentHandler(HttpHandler next, SessionManager sessionManager, SessionConfig sessionConfig) {
/*  54 */     this.sessionConfig = sessionConfig;
/*  55 */     if (sessionManager == null) {
/*  56 */       throw UndertowMessages.MESSAGES.sessionManagerMustNotBeNull();
/*     */     }
/*  58 */     this.next = next;
/*  59 */     this.sessionManager = sessionManager;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  64 */     exchange.putAttachment(SessionManager.ATTACHMENT_KEY, this.sessionManager);
/*  65 */     exchange.putAttachment(SessionConfig.ATTACHMENT_KEY, this.sessionConfig);
/*  66 */     UpdateLastAccessTimeListener handler = new UpdateLastAccessTimeListener(this.sessionConfig, this.sessionManager);
/*  67 */     exchange.addExchangeCompleteListener(handler);
/*  68 */     this.next.handleRequest(exchange);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHandler getNext() {
/*  74 */     return this.next;
/*     */   }
/*     */   
/*     */   public SessionAttachmentHandler setNext(HttpHandler next) {
/*  78 */     Handlers.handlerNotNull(next);
/*  79 */     this.next = next;
/*  80 */     return this;
/*     */   }
/*     */   
/*     */   public SessionManager getSessionManager() {
/*  84 */     return this.sessionManager;
/*     */   }
/*     */   
/*     */   public SessionAttachmentHandler setSessionManager(SessionManager sessionManager) {
/*  88 */     if (sessionManager == null) {
/*  89 */       throw UndertowMessages.MESSAGES.sessionManagerMustNotBeNull();
/*     */     }
/*  91 */     this.sessionManager = sessionManager;
/*  92 */     return this;
/*     */   }
/*     */   
/*     */   private static class UpdateLastAccessTimeListener
/*     */     implements ExchangeCompletionListener {
/*     */     private final SessionConfig sessionConfig;
/*     */     private final SessionManager sessionManager;
/*     */     
/*     */     private UpdateLastAccessTimeListener(SessionConfig sessionConfig, SessionManager sessionManager) {
/* 101 */       this.sessionConfig = sessionConfig;
/* 102 */       this.sessionManager = sessionManager;
/*     */     }
/*     */ 
/*     */     
/*     */     public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener next) {
/*     */       try {
/* 108 */         Session session = this.sessionManager.getSession(exchange, this.sessionConfig);
/* 109 */         if (session != null) {
/* 110 */           session.requestDone(exchange);
/*     */         }
/*     */       } finally {
/* 113 */         next.proceed();
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\session\SessionAttachmentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */