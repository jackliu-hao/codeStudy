/*     */ package io.undertow.security.impl;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.security.api.NotificationReceiver;
/*     */ import io.undertow.security.api.SecurityContext;
/*     */ import io.undertow.security.api.SecurityNotification;
/*     */ import io.undertow.security.idm.Account;
/*     */ import io.undertow.server.HttpServerExchange;
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
/*     */ public abstract class AbstractSecurityContext
/*     */   implements SecurityContext
/*     */ {
/*     */   private boolean authenticationRequired;
/*     */   protected final HttpServerExchange exchange;
/*  41 */   private Node<NotificationReceiver> notificationReceivers = null;
/*     */   
/*     */   private Account account;
/*     */   private String mechanismName;
/*     */   
/*     */   protected AbstractSecurityContext(HttpServerExchange exchange) {
/*  47 */     this.exchange = exchange;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAuthenticationRequired() {
/*  52 */     this.authenticationRequired = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAuthenticationRequired() {
/*  57 */     return this.authenticationRequired;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAuthenticated() {
/*  62 */     return (this.account != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Account getAuthenticatedAccount() {
/*  67 */     return this.account;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMechanismName() {
/*  75 */     return this.mechanismName;
/*     */   }
/*     */ 
/*     */   
/*     */   public void authenticationComplete(Account account, String mechanism, boolean cachingRequired) {
/*  80 */     authenticationComplete(account, mechanism, false, cachingRequired);
/*     */   }
/*     */   
/*     */   protected void authenticationComplete(Account account, String mechanism, boolean programatic, boolean cachingRequired) {
/*  84 */     this.account = account;
/*  85 */     this.mechanismName = mechanism;
/*     */     
/*  87 */     UndertowLogger.SECURITY_LOGGER.debugf("Authenticated as %s, roles %s", account.getPrincipal().getName(), account.getRoles());
/*  88 */     sendNoticiation(new SecurityNotification(this.exchange, SecurityNotification.EventType.AUTHENTICATED, account, mechanism, programatic, UndertowMessages.MESSAGES
/*  89 */           .userAuthenticated(account.getPrincipal().getName()), cachingRequired));
/*     */   }
/*     */ 
/*     */   
/*     */   public void authenticationFailed(String message, String mechanism) {
/*  94 */     UndertowLogger.SECURITY_LOGGER.debugf("Authentication failed with message %s and mechanism %s for %s", message, mechanism, this.exchange);
/*  95 */     sendNoticiation(new SecurityNotification(this.exchange, SecurityNotification.EventType.FAILED_AUTHENTICATION, null, mechanism, false, message, true));
/*     */   }
/*     */ 
/*     */   
/*     */   public void registerNotificationReceiver(NotificationReceiver receiver) {
/* 100 */     if (this.notificationReceivers == null) {
/* 101 */       this.notificationReceivers = new Node<>(receiver);
/*     */     } else {
/* 103 */       Node<NotificationReceiver> cur = this.notificationReceivers;
/* 104 */       while (cur.next != null) {
/* 105 */         cur = cur.next;
/*     */       }
/* 107 */       cur.next = new Node<>(receiver);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeNotificationReceiver(NotificationReceiver receiver) {
/* 113 */     Node<NotificationReceiver> cur = this.notificationReceivers;
/* 114 */     if (receiver.equals(cur.item)) {
/* 115 */       this.notificationReceivers = cur.next;
/*     */     } else {
/* 117 */       Node<NotificationReceiver> old = cur;
/* 118 */       while (cur.next != null) {
/* 119 */         cur = cur.next;
/* 120 */         if (receiver.equals(cur.item)) {
/* 121 */           old.next = cur.next;
/*     */         }
/* 123 */         old = cur;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void sendNoticiation(SecurityNotification notification) {
/* 129 */     Node<NotificationReceiver> cur = this.notificationReceivers;
/* 130 */     while (cur != null) {
/* 131 */       ((NotificationReceiver)cur.item).handleNotification(notification);
/* 132 */       cur = cur.next;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void logout() {
/* 138 */     if (!isAuthenticated()) {
/*     */       return;
/*     */     }
/* 141 */     UndertowLogger.SECURITY_LOGGER.debugf("Logged out %s", this.exchange);
/* 142 */     sendNoticiation(new SecurityNotification(this.exchange, SecurityNotification.EventType.LOGGED_OUT, this.account, this.mechanismName, true, UndertowMessages.MESSAGES
/* 143 */           .userLoggedOut(this.account.getPrincipal().getName()), true));
/*     */     
/* 145 */     this.account = null;
/* 146 */     this.mechanismName = null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected static final class Node<T>
/*     */   {
/*     */     final T item;
/*     */     
/*     */     Node<T> next;
/*     */ 
/*     */     
/*     */     private Node(T item) {
/* 158 */       this.item = item;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\AbstractSecurityContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */