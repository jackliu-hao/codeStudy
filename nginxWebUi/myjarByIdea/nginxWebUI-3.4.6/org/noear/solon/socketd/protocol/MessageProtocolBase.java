package org.noear.solon.socketd.protocol;

import java.nio.ByteBuffer;
import org.noear.solon.core.message.Message;

public class MessageProtocolBase implements MessageProtocol {
   public static final MessageProtocol instance = new MessageProtocolBase();

   public ByteBuffer encode(Message message) throws Exception {
      if (message.flag() == 1) {
         int len = message.body().length + 4 + 4;
         ByteBuffer buffer = ByteBuffer.allocate(len);
         buffer.putInt(len);
         buffer.putInt(message.flag());
         buffer.put(message.body());
         buffer.flip();
         return buffer;
      } else {
         byte[] keyB = message.key().getBytes(message.getCharset());
         byte[] resourceDescriptorB = message.resourceDescriptor().getBytes(message.getCharset());
         byte[] headerB = message.header().getBytes(message.getCharset());
         int len = keyB.length + resourceDescriptorB.length + headerB.length + message.body().length + 6 + 4 + 4;
         ByteBuffer buffer = ByteBuffer.allocate(len);
         buffer.putInt(len);
         buffer.putInt(message.flag());
         buffer.put(keyB);
         buffer.putChar('\n');
         buffer.put(resourceDescriptorB);
         buffer.putChar('\n');
         buffer.put(headerB);
         buffer.putChar('\n');
         buffer.put(message.body());
         buffer.flip();
         return buffer;
      }
   }

   public Message decode(ByteBuffer buffer) throws Exception {
      int len0 = buffer.getInt();
      if (len0 > buffer.remaining() + 4) {
         return null;
      } else {
         int flag = buffer.getInt();
         if (flag == 1) {
            int len = len0 - buffer.position();
            byte[] body = new byte[len];
            if (len > 0) {
               buffer.get(body, 0, len);
            }

            return new Message(flag, (String)null, (String)null, (String)null, body);
         } else {
            ByteBuffer sb = ByteBuffer.allocate(Math.min(4096, buffer.limit()));
            String key = this.decodeString(buffer, sb, 256);
            if (key == null) {
               return null;
            } else {
               String resourceDescriptor = this.decodeString(buffer, sb, 512);
               if (resourceDescriptor == null) {
                  return null;
               } else {
                  String header = this.decodeString(buffer, sb, 0);
                  if (header == null) {
                     return null;
                  } else {
                     int len = len0 - buffer.position();
                     byte[] body = new byte[len];
                     if (len > 0) {
                        buffer.get(body, 0, len);
                     }

                     return new Message(flag, key, resourceDescriptor, header, body);
                  }
               }
            }
         }
      }
   }

   protected String decodeString(ByteBuffer buffer, ByteBuffer sb, int maxLen) {
      sb.clear();

      do {
         byte c = buffer.get();
         if (c == 10) {
            sb.flip();
            if (sb.limit() < 1) {
               return "";
            }

            return new String(sb.array(), 0, sb.limit());
         }

         if (c != 0) {
            sb.put(c);
         }
      } while(maxLen <= 0 || maxLen >= sb.position());

      return null;
   }
}
