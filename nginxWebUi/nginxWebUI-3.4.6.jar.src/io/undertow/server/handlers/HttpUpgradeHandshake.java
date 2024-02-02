package io.undertow.server.handlers;

import io.undertow.server.HttpServerExchange;
import java.io.IOException;

public interface HttpUpgradeHandshake {
  boolean handleUpgrade(HttpServerExchange paramHttpServerExchange) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\HttpUpgradeHandshake.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */