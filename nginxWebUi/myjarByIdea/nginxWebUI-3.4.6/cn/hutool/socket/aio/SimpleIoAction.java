package cn.hutool.socket.aio;

import cn.hutool.log.StaticLog;
import java.nio.ByteBuffer;

public abstract class SimpleIoAction implements IoAction<ByteBuffer> {
   public void accept(AioSession session) {
   }

   public void failed(Throwable exc, AioSession session) {
      StaticLog.error(exc);
   }
}
