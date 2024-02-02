package cn.hutool.socket.protocol;

import cn.hutool.socket.aio.AioSession;
import java.nio.ByteBuffer;

public interface MsgDecoder<T> {
  T decode(AioSession paramAioSession, ByteBuffer paramByteBuffer);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\protocol\MsgDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */