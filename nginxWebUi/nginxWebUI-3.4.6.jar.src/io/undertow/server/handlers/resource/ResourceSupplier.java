package io.undertow.server.handlers.resource;

import io.undertow.server.HttpServerExchange;
import java.io.IOException;

public interface ResourceSupplier {
  Resource getResource(HttpServerExchange paramHttpServerExchange, String paramString) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\ResourceSupplier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */