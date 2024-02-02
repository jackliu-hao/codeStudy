package cn.hutool.socket.nio;

import java.nio.channels.SocketChannel;

@FunctionalInterface
public interface ChannelHandler {
  void handle(SocketChannel paramSocketChannel) throws Exception;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\nio\ChannelHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */