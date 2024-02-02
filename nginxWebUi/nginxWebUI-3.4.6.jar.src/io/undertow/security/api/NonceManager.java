package io.undertow.security.api;

import io.undertow.server.HttpServerExchange;

public interface NonceManager {
  String nextNonce(String paramString, HttpServerExchange paramHttpServerExchange);
  
  boolean validateNonce(String paramString, int paramInt, HttpServerExchange paramHttpServerExchange);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\api\NonceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */