package cn.hutool.socket;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

public class ChannelUtil {
   public static AsynchronousChannelGroup createFixedGroup(int poolSize) {
      try {
         return AsynchronousChannelGroup.withFixedThreadPool(poolSize, ThreadFactoryBuilder.create().setNamePrefix("Huool-socket-").build());
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public static AsynchronousSocketChannel connect(AsynchronousChannelGroup group, InetSocketAddress address) {
      AsynchronousSocketChannel channel;
      try {
         channel = AsynchronousSocketChannel.open(group);
      } catch (IOException var5) {
         throw new IORuntimeException(var5);
      }

      try {
         channel.connect(address).get();
         return channel;
      } catch (ExecutionException | InterruptedException var4) {
         IoUtil.close(channel);
         throw new SocketRuntimeException(var4);
      }
   }
}
