package cn.hutool.socket.aio;

import cn.hutool.log.StaticLog;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AioServer> {
   public void completed(AsynchronousSocketChannel socketChannel, AioServer aioServer) {
      aioServer.accept();
      IoAction<ByteBuffer> ioAction = aioServer.ioAction;
      AioSession session = new AioSession(socketChannel, ioAction, aioServer.config);
      ioAction.accept(session);
      session.read();
   }

   public void failed(Throwable exc, AioServer aioServer) {
      StaticLog.error(exc);
   }
}
