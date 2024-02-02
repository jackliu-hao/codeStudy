package org.noear.solon.socketd.protocol;

import java.nio.ByteBuffer;
import org.noear.solon.core.message.Message;

public interface MessageProtocol {
   ByteBuffer encode(Message message) throws Exception;

   Message decode(ByteBuffer buffer) throws Exception;
}
