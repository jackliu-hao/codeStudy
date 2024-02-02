package org.noear.solon.socketd.protocol;

import java.nio.ByteBuffer;
import org.noear.solon.core.message.Message;
import org.noear.solon.socketd.protocol.util.GzipUtil;

public class MessageProtocolCompress implements MessageProtocol {
   protected MessageProtocol baseProtocol;
   protected int allowCompressSize;

   public MessageProtocolCompress() {
      this.baseProtocol = MessageProtocolBase.instance;
      this.allowCompressSize = 1024;
   }

   public MessageProtocolCompress(int allowCompressSize) {
      this.baseProtocol = MessageProtocolBase.instance;
      this.allowCompressSize = 1024;
      this.allowCompressSize = allowCompressSize;
   }

   public MessageProtocolCompress(MessageProtocol baseProtocol) {
      this.baseProtocol = MessageProtocolBase.instance;
      this.allowCompressSize = 1024;
      this.baseProtocol = baseProtocol;
   }

   public MessageProtocolCompress(int allowCompressSize, MessageProtocol baseProtocol) {
      this.baseProtocol = MessageProtocolBase.instance;
      this.allowCompressSize = 1024;
      this.baseProtocol = baseProtocol;
      this.allowCompressSize = allowCompressSize;
   }

   public boolean allowCompress(int byteSize) {
      return byteSize > this.allowCompressSize;
   }

   public byte[] compress(byte[] bytes) throws Exception {
      return GzipUtil.compress(bytes);
   }

   public byte[] uncompress(byte[] bytes) throws Exception {
      return GzipUtil.uncompress(bytes);
   }

   public ByteBuffer encode(Message message) throws Exception {
      ByteBuffer buffer = this.baseProtocol.encode(message);
      if (this.allowCompress(buffer.array().length)) {
         byte[] bytes = this.compress(buffer.array());
         message = Message.wrapContainer(bytes);
         buffer = this.baseProtocol.encode(message);
      }

      return buffer;
   }

   public Message decode(ByteBuffer buffer) throws Exception {
      Message message = this.baseProtocol.decode(buffer);
      if (message.flag() == 1) {
         byte[] bytes = this.uncompress(message.body());
         buffer = ByteBuffer.wrap(bytes);
         message = this.baseProtocol.decode(buffer);
      }

      return message;
   }
}
