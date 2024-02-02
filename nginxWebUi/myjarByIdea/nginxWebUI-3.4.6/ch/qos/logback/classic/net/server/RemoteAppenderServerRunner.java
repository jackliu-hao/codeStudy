package ch.qos.logback.classic.net.server;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.net.server.ConcurrentServerRunner;
import ch.qos.logback.core.net.server.ServerListener;
import java.util.concurrent.Executor;

class RemoteAppenderServerRunner extends ConcurrentServerRunner<RemoteAppenderClient> {
   public RemoteAppenderServerRunner(ServerListener<RemoteAppenderClient> listener, Executor executor) {
      super(listener, executor);
   }

   protected boolean configureClient(RemoteAppenderClient client) {
      client.setLoggerContext((LoggerContext)this.getContext());
      return true;
   }
}
