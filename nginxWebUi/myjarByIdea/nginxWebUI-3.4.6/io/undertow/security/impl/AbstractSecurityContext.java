package io.undertow.security.impl;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityNotification;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;

public abstract class AbstractSecurityContext implements SecurityContext {
   private boolean authenticationRequired;
   protected final HttpServerExchange exchange;
   private Node<NotificationReceiver> notificationReceivers = null;
   private Account account;
   private String mechanismName;

   protected AbstractSecurityContext(HttpServerExchange exchange) {
      this.exchange = exchange;
   }

   public void setAuthenticationRequired() {
      this.authenticationRequired = true;
   }

   public boolean isAuthenticationRequired() {
      return this.authenticationRequired;
   }

   public boolean isAuthenticated() {
      return this.account != null;
   }

   public Account getAuthenticatedAccount() {
      return this.account;
   }

   public String getMechanismName() {
      return this.mechanismName;
   }

   public void authenticationComplete(Account account, String mechanism, boolean cachingRequired) {
      this.authenticationComplete(account, mechanism, false, cachingRequired);
   }

   protected void authenticationComplete(Account account, String mechanism, boolean programatic, boolean cachingRequired) {
      this.account = account;
      this.mechanismName = mechanism;
      UndertowLogger.SECURITY_LOGGER.debugf("Authenticated as %s, roles %s", account.getPrincipal().getName(), account.getRoles());
      this.sendNoticiation(new SecurityNotification(this.exchange, SecurityNotification.EventType.AUTHENTICATED, account, mechanism, programatic, UndertowMessages.MESSAGES.userAuthenticated(account.getPrincipal().getName()), cachingRequired));
   }

   public void authenticationFailed(String message, String mechanism) {
      UndertowLogger.SECURITY_LOGGER.debugf("Authentication failed with message %s and mechanism %s for %s", message, mechanism, this.exchange);
      this.sendNoticiation(new SecurityNotification(this.exchange, SecurityNotification.EventType.FAILED_AUTHENTICATION, (Account)null, mechanism, false, message, true));
   }

   public void registerNotificationReceiver(NotificationReceiver receiver) {
      if (this.notificationReceivers == null) {
         this.notificationReceivers = new Node(receiver);
      } else {
         Node cur;
         for(cur = this.notificationReceivers; cur.next != null; cur = cur.next) {
         }

         cur.next = new Node(receiver);
      }

   }

   public void removeNotificationReceiver(NotificationReceiver receiver) {
      Node<NotificationReceiver> cur = this.notificationReceivers;
      if (receiver.equals(cur.item)) {
         this.notificationReceivers = cur.next;
      } else {
         for(Node<NotificationReceiver> old = cur; cur.next != null; old = cur) {
            cur = cur.next;
            if (receiver.equals(cur.item)) {
               old.next = cur.next;
            }
         }
      }

   }

   private void sendNoticiation(SecurityNotification notification) {
      for(Node<NotificationReceiver> cur = this.notificationReceivers; cur != null; cur = cur.next) {
         ((NotificationReceiver)cur.item).handleNotification(notification);
      }

   }

   public void logout() {
      if (this.isAuthenticated()) {
         UndertowLogger.SECURITY_LOGGER.debugf("Logged out %s", this.exchange);
         this.sendNoticiation(new SecurityNotification(this.exchange, SecurityNotification.EventType.LOGGED_OUT, this.account, this.mechanismName, true, UndertowMessages.MESSAGES.userLoggedOut(this.account.getPrincipal().getName()), true));
         this.account = null;
         this.mechanismName = null;
      }
   }

   protected static final class Node<T> {
      final T item;
      Node<T> next;

      private Node(T item) {
         this.item = item;
      }

      // $FF: synthetic method
      Node(Object x0, Object x1) {
         this(x0);
      }
   }
}
