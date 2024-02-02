package io.undertow.security.api;

import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;

public class SecurityNotification {
   private final HttpServerExchange exchange;
   private final EventType eventType;
   private final Account account;
   private final String mechanism;
   private final boolean programatic;
   private final String message;
   private final boolean cachingRequired;

   public SecurityNotification(HttpServerExchange exchange, EventType eventType, Account account, String mechanism, boolean programatic, String message, boolean cachingRequired) {
      this.exchange = exchange;
      this.eventType = eventType;
      this.account = account;
      this.mechanism = mechanism;
      this.programatic = programatic;
      this.message = message;
      this.cachingRequired = cachingRequired;
   }

   public HttpServerExchange getExchange() {
      return this.exchange;
   }

   public EventType getEventType() {
      return this.eventType;
   }

   public Account getAccount() {
      return this.account;
   }

   public String getMechanism() {
      return this.mechanism;
   }

   public boolean isProgramatic() {
      return this.programatic;
   }

   public String getMessage() {
      return this.message;
   }

   public boolean isCachingRequired() {
      return this.cachingRequired;
   }

   public static enum EventType {
      AUTHENTICATED,
      FAILED_AUTHENTICATION,
      LOGGED_OUT;
   }
}
