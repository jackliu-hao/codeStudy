package cn.hutool.socket.aio;

import cn.hutool.socket.ChannelUtil;
import cn.hutool.socket.SocketConfig;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class AioClient implements Closeable {
   private final AioSession session;

   public AioClient(InetSocketAddress address, IoAction<ByteBuffer> ioAction) {
      this(address, ioAction, new SocketConfig());
   }

   public AioClient(InetSocketAddress address, IoAction<ByteBuffer> ioAction, SocketConfig config) {
      this(createChannel(address, config.getThreadPoolSize()), ioAction, config);
   }

   public AioClient(AsynchronousSocketChannel channel, IoAction<ByteBuffer> ioAction, SocketConfig config) {
      this.session = new AioSession(channel, ioAction, config);
      ioAction.accept(this.session);
   }

   public <T> AioClient setOption(SocketOption<T> name, T value) throws IOException {
      this.session.getChannel().setOption(name, value);
      return this;
   }

   public IoAction<ByteBuffer> getIoAction() {
      return this.session.getIoAction();
   }

   public AioClient read() {
      this.session.read();
      return this;
   }

   public AioClient write(ByteBuffer data) {
      this.session.write(data);
      return this;
   }

   public void close() {
      this.session.close();
   }

   private static AsynchronousSocketChannel createChannel(InetSocketAddress address, int poolSize) {
      return ChannelUtil.connect(ChannelUtil.createFixedGroup(poolSize), address);
   }
}
