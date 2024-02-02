package io.undertow.server.handlers.resource;

import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;

public interface RangeAwareResource extends Resource {
   void serveRange(Sender var1, HttpServerExchange var2, long var3, long var5, IoCallback var7);

   boolean isRangeSupported();
}
