package cn.hutool.socket.protocol;

import cn.hutool.socket.aio.AioSession;
import java.nio.ByteBuffer;

public interface MsgEncoder<T> {
   void encode(AioSession var1, ByteBuffer var2, T var3);
}
