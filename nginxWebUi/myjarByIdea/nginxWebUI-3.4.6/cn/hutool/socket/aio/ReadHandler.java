package cn.hutool.socket.aio;

import cn.hutool.socket.SocketRuntimeException;
import java.nio.channels.CompletionHandler;

public class ReadHandler implements CompletionHandler<Integer, AioSession> {
   public void completed(Integer result, AioSession session) {
      session.callbackRead();
   }

   public void failed(Throwable exc, AioSession session) {
      throw new SocketRuntimeException(exc);
   }
}
