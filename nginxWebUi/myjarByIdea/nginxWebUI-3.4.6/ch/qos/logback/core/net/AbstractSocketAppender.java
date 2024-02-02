package ch.qos.logback.core.net;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.spi.PreSerializationTransformer;
import ch.qos.logback.core.util.CloseUtil;
import ch.qos.logback.core.util.Duration;
import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;
import javax.net.ssl.SSLHandshakeException;

public abstract class AbstractSocketAppender<E> extends AppenderBase<E> implements SocketConnector.ExceptionHandler {
   public static final int DEFAULT_PORT = 4560;
   public static final int DEFAULT_RECONNECTION_DELAY = 30000;
   public static final int DEFAULT_QUEUE_SIZE = 128;
   private static final int DEFAULT_ACCEPT_CONNECTION_DELAY = 5000;
   private static final int DEFAULT_EVENT_DELAY_TIMEOUT = 100;
   private final ObjectWriterFactory objectWriterFactory;
   private final QueueFactory queueFactory;
   private String remoteHost;
   private int port;
   private InetAddress address;
   private Duration reconnectionDelay;
   private int queueSize;
   private int acceptConnectionTimeout;
   private Duration eventDelayLimit;
   private BlockingDeque<E> deque;
   private String peerId;
   private SocketConnector connector;
   private Future<?> task;
   private volatile Socket socket;

   protected AbstractSocketAppender() {
      this(new QueueFactory(), new ObjectWriterFactory());
   }

   AbstractSocketAppender(QueueFactory queueFactory, ObjectWriterFactory objectWriterFactory) {
      this.port = 4560;
      this.reconnectionDelay = new Duration(30000L);
      this.queueSize = 128;
      this.acceptConnectionTimeout = 5000;
      this.eventDelayLimit = new Duration(100L);
      this.objectWriterFactory = objectWriterFactory;
      this.queueFactory = queueFactory;
   }

   public void start() {
      if (!this.isStarted()) {
         int errorCount = 0;
         if (this.port <= 0) {
            ++errorCount;
            this.addError("No port was configured for appender" + this.name + " For more information, please visit http://logback.qos.ch/codes.html#socket_no_port");
         }

         if (this.remoteHost == null) {
            ++errorCount;
            this.addError("No remote host was configured for appender" + this.name + " For more information, please visit http://logback.qos.ch/codes.html#socket_no_host");
         }

         if (this.queueSize == 0) {
            this.addWarn("Queue size of zero is deprecated, use a size of one to indicate synchronous processing");
         }

         if (this.queueSize < 0) {
            ++errorCount;
            this.addError("Queue size must be greater than zero");
         }

         if (errorCount == 0) {
            try {
               this.address = InetAddress.getByName(this.remoteHost);
            } catch (UnknownHostException var3) {
               this.addError("unknown host: " + this.remoteHost);
               ++errorCount;
            }
         }

         if (errorCount == 0) {
            this.deque = this.queueFactory.newLinkedBlockingDeque(this.queueSize);
            this.peerId = "remote peer " + this.remoteHost + ":" + this.port + ": ";
            this.connector = this.createConnector(this.address, this.port, 0, this.reconnectionDelay.getMilliseconds());
            this.task = this.getContext().getExecutorService().submit(new Runnable() {
               public void run() {
                  AbstractSocketAppender.this.connectSocketAndDispatchEvents();
               }
            });
            super.start();
         }

      }
   }

   public void stop() {
      if (this.isStarted()) {
         CloseUtil.closeQuietly(this.socket);
         this.task.cancel(true);
         super.stop();
      }
   }

   protected void append(E event) {
      if (event != null && this.isStarted()) {
         try {
            boolean inserted = this.deque.offer(event, this.eventDelayLimit.getMilliseconds(), TimeUnit.MILLISECONDS);
            if (!inserted) {
               this.addInfo("Dropping event due to timeout limit of [" + this.eventDelayLimit + "] being exceeded");
            }
         } catch (InterruptedException var3) {
            this.addError("Interrupted while appending event to SocketAppender", var3);
         }

      }
   }

   private void connectSocketAndDispatchEvents() {
      while(true) {
         try {
            if (this.socketConnectionCouldBeEstablished()) {
               try {
                  try {
                     ObjectWriter objectWriter = this.createObjectWriterForSocket();
                     this.addInfo(this.peerId + "connection established");
                     this.dispatchEvents(objectWriter);
                  } catch (SSLHandshakeException var7) {
                     Thread.sleep(30000L);
                  } catch (IOException var8) {
                     this.addInfo(this.peerId + "connection failed: ", var8);
                  }
                  continue;
               } finally {
                  CloseUtil.closeQuietly(this.socket);
                  this.socket = null;
                  this.addInfo(this.peerId + "connection closed");
               }
            }
         } catch (InterruptedException var10) {
         }

         this.addInfo("shutting down");
         return;
      }
   }

   private boolean socketConnectionCouldBeEstablished() throws InterruptedException {
      return (this.socket = this.connector.call()) != null;
   }

   private ObjectWriter createObjectWriterForSocket() throws IOException {
      this.socket.setSoTimeout(this.acceptConnectionTimeout);
      ObjectWriter objectWriter = this.objectWriterFactory.newAutoFlushingObjectWriter(this.socket.getOutputStream());
      this.socket.setSoTimeout(0);
      return objectWriter;
   }

   private SocketConnector createConnector(InetAddress address, int port, int initialDelay, long retryDelay) {
      SocketConnector connector = this.newConnector(address, port, (long)initialDelay, retryDelay);
      connector.setExceptionHandler(this);
      connector.setSocketFactory(this.getSocketFactory());
      return connector;
   }

   private void dispatchEvents(ObjectWriter objectWriter) throws InterruptedException, IOException {
      while(true) {
         E event = this.deque.takeFirst();
         this.postProcessEvent(event);
         Serializable serializableEvent = this.getPST().transform(event);

         try {
            objectWriter.write(serializableEvent);
         } catch (IOException var5) {
            this.tryReAddingEventToFrontOfQueue(event);
            throw var5;
         }
      }
   }

   private void tryReAddingEventToFrontOfQueue(E event) {
      boolean wasInserted = this.deque.offerFirst(event);
      if (!wasInserted) {
         this.addInfo("Dropping event due to socket connection error and maxed out deque capacity");
      }

   }

   public void connectionFailed(SocketConnector connector, Exception ex) {
      if (ex instanceof InterruptedException) {
         this.addInfo("connector interrupted");
      } else if (ex instanceof ConnectException) {
         this.addInfo(this.peerId + "connection refused");
      } else {
         this.addInfo(this.peerId + ex);
      }

   }

   protected SocketConnector newConnector(InetAddress address, int port, long initialDelay, long retryDelay) {
      return new DefaultSocketConnector(address, port, initialDelay, retryDelay);
   }

   protected SocketFactory getSocketFactory() {
      return SocketFactory.getDefault();
   }

   protected abstract void postProcessEvent(E var1);

   protected abstract PreSerializationTransformer<E> getPST();

   public void setRemoteHost(String host) {
      this.remoteHost = host;
   }

   public String getRemoteHost() {
      return this.remoteHost;
   }

   public void setPort(int port) {
      this.port = port;
   }

   public int getPort() {
      return this.port;
   }

   public void setReconnectionDelay(Duration delay) {
      this.reconnectionDelay = delay;
   }

   public Duration getReconnectionDelay() {
      return this.reconnectionDelay;
   }

   public void setQueueSize(int queueSize) {
      this.queueSize = queueSize;
   }

   public int getQueueSize() {
      return this.queueSize;
   }

   public void setEventDelayLimit(Duration eventDelayLimit) {
      this.eventDelayLimit = eventDelayLimit;
   }

   public Duration getEventDelayLimit() {
      return this.eventDelayLimit;
   }

   void setAcceptConnectionTimeout(int acceptConnectionTimeout) {
      this.acceptConnectionTimeout = acceptConnectionTimeout;
   }
}
