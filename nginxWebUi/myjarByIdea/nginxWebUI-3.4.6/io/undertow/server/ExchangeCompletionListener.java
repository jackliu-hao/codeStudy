package io.undertow.server;

public interface ExchangeCompletionListener {
   void exchangeEvent(HttpServerExchange var1, NextListener var2);

   public interface NextListener {
      void proceed();
   }
}
