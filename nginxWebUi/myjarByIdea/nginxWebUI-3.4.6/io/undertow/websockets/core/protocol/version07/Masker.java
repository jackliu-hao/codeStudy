package io.undertow.websockets.core.protocol.version07;

import io.undertow.server.protocol.framed.FrameHeaderData;
import io.undertow.websockets.core.function.ChannelFunction;
import java.nio.ByteBuffer;

public final class Masker implements ChannelFunction {
   private byte[] maskingKey;
   int m;

   Masker(int maskingKey) {
      this.maskingKey = createsMaskingKey(maskingKey);
   }

   public void setMaskingKey(int maskingKey) {
      this.maskingKey = createsMaskingKey(maskingKey);
      this.m = 0;
   }

   private static byte[] createsMaskingKey(int maskingKey) {
      byte[] key = new byte[]{(byte)(maskingKey >> 24 & 255), (byte)(maskingKey >> 16 & 255), (byte)(maskingKey >> 8 & 255), (byte)(maskingKey & 255)};
      return key;
   }

   private void mask(ByteBuffer buf, int position, int length) {
      int limit = position + length;

      for(int i = position; i < limit; ++i) {
         buf.put(i, (byte)(buf.get(i) ^ this.maskingKey[this.m++]));
         this.m %= 4;
      }

   }

   public void newFrame(FrameHeaderData headerData) {
      WebSocket07Channel.WebSocketFrameHeader header = (WebSocket07Channel.WebSocketFrameHeader)headerData;
      this.setMaskingKey(header.getMaskingKey());
   }

   public void afterRead(ByteBuffer buf, int position, int length) {
      this.mask(buf, position, length);
   }

   public void beforeWrite(ByteBuffer buf, int position, int length) {
      this.mask(buf, position, length);
   }

   public void complete() {
   }
}
