package org.noear.solon.socketd.protocol;

import java.nio.ByteBuffer;
import org.noear.solon.core.message.Message;

public interface MessageProtocol {
  ByteBuffer encode(Message paramMessage) throws Exception;
  
  Message decode(ByteBuffer paramByteBuffer) throws Exception;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\protocol\MessageProtocol.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */