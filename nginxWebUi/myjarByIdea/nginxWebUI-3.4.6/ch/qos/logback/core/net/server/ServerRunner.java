package ch.qos.logback.core.net.server;

import ch.qos.logback.core.spi.ContextAware;
import java.io.IOException;

public interface ServerRunner<T extends Client> extends ContextAware, Runnable {
   boolean isRunning();

   void stop() throws IOException;

   void accept(ClientVisitor<T> var1);
}
