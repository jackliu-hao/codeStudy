package ch.qos.logback.core.net.server;

import java.io.Closeable;
import java.io.IOException;

public interface ServerListener<T extends Client> extends Closeable {
  T acceptClient() throws IOException, InterruptedException;
  
  void close();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\server\ServerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */