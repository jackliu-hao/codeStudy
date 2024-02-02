package cn.hutool.socket.aio;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.socket.SocketConfig;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;

public class AioServer implements Closeable {
   private static final Log log = LogFactory.get();
   private static final AcceptHandler ACCEPT_HANDLER = new AcceptHandler();
   private AsynchronousChannelGroup group;
   private AsynchronousServerSocketChannel channel;
   protected IoAction<ByteBuffer> ioAction;
   protected final SocketConfig config;

   public AioServer(int port) {
      this(new InetSocketAddress(port), new SocketConfig());
   }

   public AioServer(InetSocketAddress address, SocketConfig config) {
      this.config = config;
      this.init(address);
   }

   public AioServer init(InetSocketAddress address) {
      try {
         this.group = AsynchronousChannelGroup.withFixedThreadPool(this.config.getThreadPoolSize(), ThreadFactoryBuilder.create().setNamePrefix("Hutool-socket-").build());
         this.channel = AsynchronousServerSocketChannel.open(this.group).bind(address);
         return this;
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public void start(boolean sync) {
      this.doStart(sync);
   }

   public <T> AioServer setOption(SocketOption<T> name, T value) throws IOException {
      this.channel.setOption(name, value);
      return this;
   }

   public IoAction<ByteBuffer> getIoAction() {
      return this.ioAction;
   }

   public AioServer setIoAction(IoAction<ByteBuffer> ioAction) {
      this.ioAction = ioAction;
      return this;
   }

   public AsynchronousServerSocketChannel getChannel() {
      return this.channel;
   }

   public AioServer accept() {
      this.channel.accept(this, ACCEPT_HANDLER);
      return this;
   }

   public boolean isOpen() {
      return null != this.channel && this.channel.isOpen();
   }

   public void close() {
      IoUtil.close(this.channel);
      if (null != this.group && !this.group.isShutdown()) {
         try {
            this.group.shutdownNow();
         } catch (IOException var4) {
         }
      }

      synchronized(this) {
         this.notify();
      }
   }

   private void doStart(boolean sync) {
      log.debug("Aio Server started, waiting for accept.", new Object[0]);
      this.accept();
      if (sync) {
         ThreadUtil.sync(this);
      }

   }
}
