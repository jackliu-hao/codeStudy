package org.noear.solon.socketd;

import java.nio.ByteBuffer;
import org.noear.solon.core.message.Message;
import org.noear.solon.socketd.protocol.MessageProtocol;
import org.noear.solon.socketd.protocol.MessageProtocolBase;

public class ProtocolManager {
   private static MessageProtocol protocol;

   public static void setProtocol(MessageProtocol protocol) {
      if (protocol != null) {
         ProtocolManager.protocol = protocol;
      }

   }

   public static ByteBuffer encode(Message message) throws IllegalArgumentException {
      try {
         return protocol.encode(message);
      } catch (Throwable var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   public static Message decode(ByteBuffer buffer) throws IllegalArgumentException {
      try {
         return protocol.decode(buffer);
      } catch (Throwable var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   static {
      protocol = MessageProtocolBase.instance;
   }
}
