package io.undertow.server;

import io.undertow.connector.ByteBufferPool;
import org.xnio.ChannelListener;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;

public interface OpenListener extends ChannelListener<StreamConnection> {
   HttpHandler getRootHandler();

   void setRootHandler(HttpHandler var1);

   OptionMap getUndertowOptions();

   void setUndertowOptions(OptionMap var1);

   ByteBufferPool getBufferPool();

   ConnectorStatistics getConnectorStatistics();

   default void closeConnections() {
   }
}
