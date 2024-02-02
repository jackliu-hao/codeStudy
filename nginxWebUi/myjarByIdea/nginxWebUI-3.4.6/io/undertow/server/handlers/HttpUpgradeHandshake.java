package io.undertow.server.handlers;

import io.undertow.server.HttpServerExchange;
import java.io.IOException;

public interface HttpUpgradeHandshake {
   boolean handleUpgrade(HttpServerExchange var1) throws IOException;
}
