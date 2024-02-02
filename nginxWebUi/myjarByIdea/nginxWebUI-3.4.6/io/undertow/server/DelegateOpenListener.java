package io.undertow.server;

import io.undertow.connector.PooledByteBuffer;
import org.xnio.StreamConnection;

public interface DelegateOpenListener extends OpenListener {
   void handleEvent(StreamConnection var1, PooledByteBuffer var2);
}
