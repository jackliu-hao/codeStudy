package io.undertow.server.handlers.resource;

import io.undertow.server.HttpServerExchange;
import java.io.IOException;

public interface ResourceSupplier {
   Resource getResource(HttpServerExchange var1, String var2) throws IOException;
}
