package io.undertow.websockets.core;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class CloseMessage {
   private final int code;
   private final String reason;
   public static final int NORMAL_CLOSURE = 1000;
   public static final int GOING_AWAY = 1001;
   public static final int WRONG_CODE = 1002;
   public static final int PROTOCOL_ERROR = 1003;
   public static final int MSG_CONTAINS_INVALID_DATA = 1007;
   public static final int MSG_VIOLATES_POLICY = 1008;
   public static final int MSG_TOO_BIG = 1009;
   public static final int MISSING_EXTENSIONS = 1010;
   public static final int UNEXPECTED_ERROR = 1011;

   public CloseMessage(ByteBuffer buffer) {
      if (buffer.remaining() >= 2) {
         this.code = (buffer.get() & 255) << 8 | buffer.get() & 255;
         this.reason = (new UTF8Output(new ByteBuffer[]{buffer})).extract();
      } else {
         this.code = 1000;
         this.reason = "";
      }

   }

   public CloseMessage(int code, String reason) {
      this.code = code;
      this.reason = reason == null ? "" : reason;
   }

   public CloseMessage(ByteBuffer[] buffers) {
      this(WebSockets.mergeBuffers(buffers));
   }

   public String getReason() {
      return this.reason;
   }

   public int getCode() {
      return this.code;
   }

   public ByteBuffer toByteBuffer() {
      byte[] data = this.reason.getBytes(StandardCharsets.UTF_8);
      ByteBuffer buffer = ByteBuffer.allocate(data.length + 2);
      buffer.putShort((short)this.code);
      buffer.put(data);
      buffer.flip();
      return buffer;
   }

   public static boolean isValid(int code) {
      return (code < 0 || code > 999) && (code < 1004 || code > 1006) && (code < 1012 || code > 2999);
   }
}
