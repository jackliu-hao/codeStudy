package io.undertow.server.session;

import io.undertow.Handlers;
import io.undertow.UndertowMessages;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;

public class SessionAttachmentHandler implements HttpHandler {
   private volatile HttpHandler next;
   private volatile SessionManager sessionManager;
   private final SessionConfig sessionConfig;

   public SessionAttachmentHandler(SessionManager sessionManager, SessionConfig sessionConfig) {
      this.next = ResponseCodeHandler.HANDLE_404;
      this.sessionConfig = sessionConfig;
      if (sessionManager == null) {
         throw UndertowMessages.MESSAGES.sessionManagerMustNotBeNull();
      } else {
         this.sessionManager = sessionManager;
      }
   }

   public SessionAttachmentHandler(HttpHandler next, SessionManager sessionManager, SessionConfig sessionConfig) {
      this.next = ResponseCodeHandler.HANDLE_404;
      this.sessionConfig = sessionConfig;
      if (sessionManager == null) {
         throw UndertowMessages.MESSAGES.sessionManagerMustNotBeNull();
      } else {
         this.next = next;
         this.sessionManager = sessionManager;
      }
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.putAttachment(SessionManager.ATTACHMENT_KEY, this.sessionManager);
      exchange.putAttachment(SessionConfig.ATTACHMENT_KEY, this.sessionConfig);
      UpdateLastAccessTimeListener handler = new UpdateLastAccessTimeListener(this.sessionConfig, this.sessionManager);
      exchange.addExchangeCompleteListener(handler);
      this.next.handleRequest(exchange);
   }

   public HttpHandler getNext() {
      return this.next;
   }

   public SessionAttachmentHandler setNext(HttpHandler next) {
      Handlers.handlerNotNull(next);
      this.next = next;
      return this;
   }

   public SessionManager getSessionManager() {
      return this.sessionManager;
   }

   public SessionAttachmentHandler setSessionManager(SessionManager sessionManager) {
      if (sessionManager == null) {
         throw UndertowMessages.MESSAGES.sessionManagerMustNotBeNull();
      } else {
         this.sessionManager = sessionManager;
         return this;
      }
   }

   private static class UpdateLastAccessTimeListener implements ExchangeCompletionListener {
      private final SessionConfig sessionConfig;
      private final SessionManager sessionManager;

      private UpdateLastAccessTimeListener(SessionConfig sessionConfig, SessionManager sessionManager) {
         this.sessionConfig = sessionConfig;
         this.sessionManager = sessionManager;
      }

      public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener next) {
         try {
            Session session = this.sessionManager.getSession(exchange, this.sessionConfig);
            if (session != null) {
               session.requestDone(exchange);
            }
         } finally {
            next.proceed();
         }

      }

      // $FF: synthetic method
      UpdateLastAccessTimeListener(SessionConfig x0, SessionManager x1, Object x2) {
         this(x0, x1);
      }
   }
}
