package io.undertow.server;

import io.undertow.util.ConduitFactory;

public interface ConduitWrapper<T extends org.xnio.conduits.Conduit> {
  T wrap(ConduitFactory<T> paramConduitFactory, HttpServerExchange paramHttpServerExchange);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\ConduitWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */