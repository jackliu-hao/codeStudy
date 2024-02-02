package io.undertow.server.handlers;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public final class ResponseCodeHandler implements HttpHandler {
   private static final boolean debugEnabled;
   public static final ResponseCodeHandler HANDLE_200;
   public static final ResponseCodeHandler HANDLE_403;
   public static final ResponseCodeHandler HANDLE_404;
   public static final ResponseCodeHandler HANDLE_405;
   public static final ResponseCodeHandler HANDLE_406;
   public static final ResponseCodeHandler HANDLE_500;
   private final int responseCode;

   public ResponseCodeHandler(int responseCode) {
      this.responseCode = responseCode;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.setStatusCode(this.responseCode);
      if (debugEnabled) {
         UndertowLogger.PREDICATE_LOGGER.debugf("Response code set to [%s] for %s.", this.responseCode, exchange);
      }

   }

   public String toString() {
      return "response-code( " + this.responseCode + " )";
   }

   static {
      debugEnabled = UndertowLogger.PREDICATE_LOGGER.isDebugEnabled();
      HANDLE_200 = new ResponseCodeHandler(200);
      HANDLE_403 = new ResponseCodeHandler(403);
      HANDLE_404 = new ResponseCodeHandler(404);
      HANDLE_405 = new ResponseCodeHandler(405);
      HANDLE_406 = new ResponseCodeHandler(406);
      HANDLE_500 = new ResponseCodeHandler(500);
   }
}
