package io.undertow.websockets.core.protocol.version07;

import io.undertow.server.protocol.framed.FrameHeaderData;
import io.undertow.websockets.core.WebSocketMessages;
import io.undertow.websockets.core.function.ChannelFunction;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class UTF8Checker implements ChannelFunction {
   private static final int UTF8_ACCEPT = 0;
   private static final int UTF8_REJECT = 12;
   private static final byte[] TYPES = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 10, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 3, 3, 11, 6, 6, 6, 5, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8};
   private static final byte[] STATES = new byte[]{0, 12, 24, 36, 60, 96, 84, 12, 12, 12, 48, 72, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 0, 12, 12, 12, 12, 12, 0, 12, 0, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12};
   private int state = 0;

   private void checkUTF8(int b) throws UnsupportedEncodingException {
      byte type = TYPES[b & 255];
      this.state = STATES[this.state + type];
      if (this.state == 12) {
         throw WebSocketMessages.MESSAGES.invalidTextFrameEncoding();
      }
   }

   private void checkUTF8(ByteBuffer buf, int position, int length) throws UnsupportedEncodingException {
      int limit = position + length;

      for(int i = position; i < limit; ++i) {
         this.checkUTF8(buf.get(i));
      }

   }

   public void newFrame(FrameHeaderData headerData) {
   }

   public void afterRead(ByteBuffer buf, int position, int length) throws IOException {
      this.checkUTF8(buf, position, length);
   }

   public void beforeWrite(ByteBuffer buf, int position, int length) throws UnsupportedEncodingException {
      this.checkUTF8(buf, position, length);
   }

   public void complete() throws UnsupportedEncodingException {
      if (this.state != 0) {
         throw WebSocketMessages.MESSAGES.invalidTextFrameEncoding();
      }
   }
}
