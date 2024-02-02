package org.xnio.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.wildfly.common.Assert;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.Option;
import org.xnio.StreamConnection;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.channels.AcceptListenerSettable;
import org.xnio.channels.AcceptingChannel;

final class QueuedNioTcpServer2 extends AbstractNioChannel<QueuedNioTcpServer2> implements AcceptingChannel<StreamConnection>, AcceptListenerSettable<QueuedNioTcpServer2> {
   private final NioTcpServer realServer;
   private final List<Queue<StreamConnection>> acceptQueues;
   private final Runnable acceptTask = this::acceptTask;
   private volatile ChannelListener<? super QueuedNioTcpServer2> acceptListener;

   QueuedNioTcpServer2(NioTcpServer realServer) {
      super(realServer.getWorker());
      this.realServer = realServer;
      NioXnioWorker worker = realServer.getWorker();
      int cnt = worker.getIoThreadCount();
      this.acceptQueues = new ArrayList(cnt);

      for(int i = 0; i < cnt; ++i) {
         this.acceptQueues.add(new LinkedBlockingQueue());
      }

      realServer.getCloseSetter().set((ignored) -> {
         this.invokeCloseHandler();
      });
      realServer.getAcceptSetter().set((ignored) -> {
         this.handleReady();
      });
   }

   public StreamConnection accept() throws IOException {
      WorkerThread current = WorkerThread.getCurrent();
      if (current == null) {
         return null;
      } else {
         Queue<StreamConnection> socketChannels = (Queue)this.acceptQueues.get(current.getNumber());
         StreamConnection connection = (StreamConnection)socketChannels.poll();
         if (connection == null && !this.realServer.isOpen()) {
            throw new ClosedChannelException();
         } else {
            return connection;
         }
      }
   }

   public ChannelListener<? super QueuedNioTcpServer2> getAcceptListener() {
      return this.acceptListener;
   }

   public void setAcceptListener(ChannelListener<? super QueuedNioTcpServer2> listener) {
      this.acceptListener = listener;
   }

   public ChannelListener.Setter<QueuedNioTcpServer2> getAcceptSetter() {
      return new AcceptListenerSettable.Setter(this);
   }

   public SocketAddress getLocalAddress() {
      return this.realServer.getLocalAddress();
   }

   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
      return this.realServer.getLocalAddress(type);
   }

   public void suspendAccepts() {
      this.realServer.suspendAccepts();
   }

   public void resumeAccepts() {
      this.realServer.resumeAccepts();
   }

   public boolean isAcceptResumed() {
      return this.realServer.isAcceptResumed();
   }

   public void wakeupAccepts() {
      this.realServer.wakeupAccepts();
   }

   public void awaitAcceptable() {
      throw Assert.unsupported();
   }

   public void awaitAcceptable(long time, TimeUnit timeUnit) {
      throw Assert.unsupported();
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getAcceptThread() {
      return this.getIoThread();
   }

   public void close() throws IOException {
      this.realServer.close();
   }

   public boolean isOpen() {
      return this.realServer.isOpen();
   }

   public boolean supportsOption(Option<?> option) {
      return this.realServer.supportsOption(option);
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return this.realServer.getOption(option);
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      return this.realServer.setOption(option, value);
   }

   void handleReady() {
      NioTcpServer realServer = this.realServer;

      NioSocketStreamConnection connection;
      try {
         connection = realServer.accept();
      } catch (ClosedChannelException var8) {
         return;
      }

      if (connection != null) {
         int i = 0;
         Runnable acceptTask = this.acceptTask;

         do {
            XnioIoThread thread = connection.getIoThread();
            ((Queue)this.acceptQueues.get(thread.getNumber())).add(connection);
            thread.execute(acceptTask);
            ++i;
            if (i == 128) {
               return;
            }

            try {
               connection = realServer.accept();
            } catch (ClosedChannelException var7) {
               return;
            }
         } while(connection != null);
      }

   }

   void acceptTask() {
      WorkerThread current = WorkerThread.getCurrent();

      assert current != null;

      Queue<StreamConnection> queue = (Queue)this.acceptQueues.get(current.getNumber());
      ChannelListeners.invokeChannelListener(this, this.getAcceptListener());
      if (!queue.isEmpty()) {
         current.execute(this.acceptTask);
      }

   }
}
