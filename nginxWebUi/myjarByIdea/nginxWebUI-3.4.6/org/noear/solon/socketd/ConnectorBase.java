package org.noear.solon.socketd;

import java.net.URI;

public abstract class ConnectorBase<T> implements Connector<T> {
   private URI uri;
   private boolean autoReconnect;

   public ConnectorBase(URI uri, boolean autoReconnect) {
      this.uri = uri;
      this.autoReconnect = autoReconnect;
   }

   public URI uri() {
      return this.uri;
   }

   public boolean autoReconnect() {
      return this.autoReconnect;
   }
}
