package io.undertow.io;

import io.undertow.server.HttpServerExchange;
import java.io.IOException;

public interface IoCallback {
   IoCallback END_EXCHANGE = new DefaultIoCallback();

   void onComplete(HttpServerExchange var1, Sender var2);

   void onException(HttpServerExchange var1, Sender var2, IOException var3);
}
