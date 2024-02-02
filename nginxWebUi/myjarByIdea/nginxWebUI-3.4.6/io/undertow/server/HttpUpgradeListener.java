package io.undertow.server;

import org.xnio.StreamConnection;

public interface HttpUpgradeListener {
   void handleUpgrade(StreamConnection var1, HttpServerExchange var2);
}
