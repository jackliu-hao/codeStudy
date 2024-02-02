package io.undertow.io;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpServerExchange;
import java.io.Closeable;
import java.io.IOException;
import org.xnio.IoUtils;

public class DefaultIoCallback implements IoCallback {
   private static final IoCallback CALLBACK = new IoCallback() {
      public void onComplete(HttpServerExchange exchange, Sender sender) {
         exchange.endExchange();
      }

      public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
         UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
         exchange.endExchange();
      }
   };

   protected DefaultIoCallback() {
   }

   public void onComplete(HttpServerExchange exchange, Sender sender) {
      sender.close(CALLBACK);
   }

   public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
      UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);

      try {
         exchange.endExchange();
      } finally {
         IoUtils.safeClose((Closeable)exchange.getConnection());
      }

   }
}
