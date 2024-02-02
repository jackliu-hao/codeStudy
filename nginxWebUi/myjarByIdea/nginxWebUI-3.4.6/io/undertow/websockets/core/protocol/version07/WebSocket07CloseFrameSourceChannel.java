package io.undertow.websockets.core.protocol.version07;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketFrameType;
import io.undertow.websockets.core.WebSocketMessages;
import java.io.IOException;
import java.nio.ByteBuffer;

class WebSocket07CloseFrameSourceChannel extends StreamSourceFrameChannel {
   WebSocket07CloseFrameSourceChannel(WebSocket07Channel wsChannel, int rsv, Masker masker, PooledByteBuffer pooled, long frameLength) {
      super(wsChannel, WebSocketFrameType.CLOSE, rsv, true, pooled, frameLength, masker, new CloseFrameValidatorChannelFunction(wsChannel));
   }

   WebSocket07CloseFrameSourceChannel(WebSocket07Channel wsChannel, int rsv, PooledByteBuffer pooled, long frameLength) {
      super(wsChannel, WebSocketFrameType.CLOSE, rsv, true, pooled, frameLength, (Masker)null, new CloseFrameValidatorChannelFunction(wsChannel));
   }

   public static class CloseFrameValidatorChannelFunction extends UTF8Checker {
      private final WebSocket07Channel wsChannel;
      private int statusBytesRead;
      private int status;

      CloseFrameValidatorChannelFunction(WebSocket07Channel wsChannel) {
         this.wsChannel = wsChannel;
      }

      public void afterRead(ByteBuffer buf, int position, int length) throws IOException {
         int i = 0;
         if (this.statusBytesRead < 2) {
            while(true) {
               if (this.statusBytesRead >= 2 || i >= length) {
                  if (this.statusBytesRead == 2 && (this.status >= 0 && this.status <= 999 || this.status >= 1004 && this.status <= 1006 || this.status >= 1012 && this.status <= 2999 || this.status >= 5000)) {
                     IOException exception = WebSocketMessages.MESSAGES.invalidCloseFrameStatusCode(this.status);
                     this.wsChannel.markReadsBroken(exception);
                     throw exception;
                  }
                  break;
               }

               this.status <<= 8;
               this.status += buf.get(position + i) & 255;
               ++this.statusBytesRead;
               ++i;
            }
         }

         super.afterRead(buf, position + i, length - i);
      }
   }
}
