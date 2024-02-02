package io.undertow.server;

import io.undertow.connector.ByteBufferPool;
import org.xnio.ChannelListener;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;

public interface OpenListener extends ChannelListener<StreamConnection> {
  HttpHandler getRootHandler();
  
  void setRootHandler(HttpHandler paramHttpHandler);
  
  OptionMap getUndertowOptions();
  
  void setUndertowOptions(OptionMap paramOptionMap);
  
  ByteBufferPool getBufferPool();
  
  ConnectorStatistics getConnectorStatistics();
  
  default void closeConnections() {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\OpenListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */