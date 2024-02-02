package ch.qos.logback.core.net.server;

import ch.qos.logback.core.spi.ContextAware;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

interface RemoteReceiverClient extends Client, ContextAware {
   void setQueue(BlockingQueue<Serializable> var1);

   boolean offer(Serializable var1);
}
