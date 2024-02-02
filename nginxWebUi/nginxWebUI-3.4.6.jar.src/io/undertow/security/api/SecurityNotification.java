/*    */ package io.undertow.security.api;
/*    */ 
/*    */ import io.undertow.security.idm.Account;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SecurityNotification
/*    */ {
/*    */   private final HttpServerExchange exchange;
/*    */   private final EventType eventType;
/*    */   private final Account account;
/*    */   private final String mechanism;
/*    */   private final boolean programatic;
/*    */   private final String message;
/*    */   private final boolean cachingRequired;
/*    */   
/*    */   public SecurityNotification(HttpServerExchange exchange, EventType eventType, Account account, String mechanism, boolean programatic, String message, boolean cachingRequired) {
/* 39 */     this.exchange = exchange;
/* 40 */     this.eventType = eventType;
/* 41 */     this.account = account;
/* 42 */     this.mechanism = mechanism;
/* 43 */     this.programatic = programatic;
/* 44 */     this.message = message;
/* 45 */     this.cachingRequired = cachingRequired;
/*    */   }
/*    */   
/*    */   public HttpServerExchange getExchange() {
/* 49 */     return this.exchange;
/*    */   }
/*    */   
/*    */   public EventType getEventType() {
/* 53 */     return this.eventType;
/*    */   }
/*    */   
/*    */   public Account getAccount() {
/* 57 */     return this.account;
/*    */   }
/*    */   
/*    */   public String getMechanism() {
/* 61 */     return this.mechanism;
/*    */   }
/*    */   
/*    */   public boolean isProgramatic() {
/* 65 */     return this.programatic;
/*    */   }
/*    */   
/*    */   public String getMessage() {
/* 69 */     return this.message;
/*    */   }
/*    */   
/*    */   public boolean isCachingRequired() {
/* 73 */     return this.cachingRequired;
/*    */   }
/*    */   
/*    */   public enum EventType {
/* 77 */     AUTHENTICATED, FAILED_AUTHENTICATION, LOGGED_OUT;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\api\SecurityNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */