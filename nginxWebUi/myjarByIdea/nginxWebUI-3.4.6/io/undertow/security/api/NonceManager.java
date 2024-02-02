package io.undertow.security.api;

import io.undertow.server.HttpServerExchange;

public interface NonceManager {
   String nextNonce(String var1, HttpServerExchange var2);

   boolean validateNonce(String var1, int var2, HttpServerExchange var3);
}
