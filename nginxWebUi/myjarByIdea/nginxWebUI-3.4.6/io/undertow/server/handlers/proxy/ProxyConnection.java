package io.undertow.server.handlers.proxy;

import io.undertow.client.ClientConnection;

public class ProxyConnection {
   private final ClientConnection connection;
   private final String targetPath;

   public ProxyConnection(ClientConnection connection, String targetPath) {
      this.connection = connection;
      this.targetPath = targetPath;
   }

   public ClientConnection getConnection() {
      return this.connection;
   }

   public String getTargetPath() {
      return this.targetPath;
   }
}
