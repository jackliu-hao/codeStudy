package cn.hutool.socket.nio;

import java.nio.channels.SocketChannel;

@FunctionalInterface
public interface ChannelHandler {
   void handle(SocketChannel var1) throws Exception;
}
