package io.undertow.server;

import io.undertow.util.ConduitFactory;
import org.xnio.conduits.Conduit;

public interface ConduitWrapper<T extends Conduit> {
   T wrap(ConduitFactory<T> var1, HttpServerExchange var2);
}
