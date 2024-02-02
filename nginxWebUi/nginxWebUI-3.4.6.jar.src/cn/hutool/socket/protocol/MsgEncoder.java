package cn.hutool.socket.protocol;

import cn.hutool.socket.aio.AioSession;
import java.nio.ByteBuffer;

public interface MsgEncoder<T> {
  void encode(AioSession paramAioSession, ByteBuffer paramByteBuffer, T paramT);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\protocol\MsgEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */