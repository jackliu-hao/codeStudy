package cn.hutool.socket.nio;

import cn.hutool.core.io.IORuntimeException;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;

public class NioUtil {
   public static void registerChannel(Selector selector, SelectableChannel channel, Operation ops) {
      if (channel != null) {
         try {
            channel.configureBlocking(false);
            channel.register(selector, ops.getValue());
         } catch (IOException var4) {
            throw new IORuntimeException(var4);
         }
      }
   }
}
