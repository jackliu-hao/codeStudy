package io.undertow.server;

import io.undertow.connector.PooledByteBuffer;
import org.xnio.StreamConnection;

public interface DelegateOpenListener extends OpenListener {
  void handleEvent(StreamConnection paramStreamConnection, PooledByteBuffer paramPooledByteBuffer);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\DelegateOpenListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */