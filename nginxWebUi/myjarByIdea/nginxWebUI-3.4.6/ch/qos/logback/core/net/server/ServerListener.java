package ch.qos.logback.core.net.server;

import java.io.Closeable;
import java.io.IOException;

public interface ServerListener<T extends Client> extends Closeable {
   T acceptClient() throws IOException, InterruptedException;

   void close();
}
