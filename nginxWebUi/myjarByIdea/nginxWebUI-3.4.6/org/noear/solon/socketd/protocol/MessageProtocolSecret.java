package org.noear.solon.socketd.protocol;

import java.nio.ByteBuffer;
import org.noear.solon.core.message.Message;

public abstract class MessageProtocolSecret implements MessageProtocol {
   protected MessageProtocol baseProtocol;

   public MessageProtocolSecret() {
      this.baseProtocol = MessageProtocolBase.instance;
   }

   public MessageProtocolSecret(MessageProtocol baseProtocol) {
      this.baseProtocol = MessageProtocolBase.instance;
      this.baseProtocol = baseProtocol;
   }

   public abstract byte[] encrypt(byte[] bytes) throws Exception;

   public abstract byte[] decrypt(byte[] bytes) throws Exception;

   public ByteBuffer encode(Message message) throws Exception {
      ByteBuffer buffer = this.baseProtocol.encode(message);
      byte[] bytes = this.encrypt(buffer.array());
      message = Message.wrapContainer(bytes);
      return this.baseProtocol.encode(message);
   }

   public Message decode(ByteBuffer buffer) throws Exception {
      Message message = this.baseProtocol.decode(buffer);
      if (message.flag() == 1) {
         byte[] bytes = this.decrypt(message.body());
         buffer = ByteBuffer.wrap(bytes);
         message = this.baseProtocol.decode(buffer);
      }

      return message;
   }
}
