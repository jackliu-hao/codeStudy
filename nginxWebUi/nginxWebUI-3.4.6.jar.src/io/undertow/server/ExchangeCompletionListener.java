package io.undertow.server;

public interface ExchangeCompletionListener {
  void exchangeEvent(HttpServerExchange paramHttpServerExchange, NextListener paramNextListener);
  
  public static interface NextListener {
    void proceed();
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\ExchangeCompletionListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */