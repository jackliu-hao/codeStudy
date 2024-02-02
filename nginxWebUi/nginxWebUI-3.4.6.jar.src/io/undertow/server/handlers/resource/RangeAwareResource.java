package io.undertow.server.handlers.resource;

import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;

public interface RangeAwareResource extends Resource {
  void serveRange(Sender paramSender, HttpServerExchange paramHttpServerExchange, long paramLong1, long paramLong2, IoCallback paramIoCallback);
  
  boolean isRangeSupported();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\RangeAwareResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */