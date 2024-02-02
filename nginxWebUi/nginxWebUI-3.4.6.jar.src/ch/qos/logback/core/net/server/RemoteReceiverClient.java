package ch.qos.logback.core.net.server;

import ch.qos.logback.core.spi.ContextAware;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

interface RemoteReceiverClient extends Client, ContextAware {
  void setQueue(BlockingQueue<Serializable> paramBlockingQueue);
  
  boolean offer(Serializable paramSerializable);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\server\RemoteReceiverClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */