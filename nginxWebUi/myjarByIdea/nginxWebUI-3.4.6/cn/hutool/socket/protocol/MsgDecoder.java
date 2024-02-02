package cn.hutool.socket.protocol;

import cn.hutool.socket.aio.AioSession;
import java.nio.ByteBuffer;

public interface MsgDecoder<T> {
   T decode(AioSession var1, ByteBuffer var2);
}
