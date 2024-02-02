package io.undertow.protocols.http2;

import java.nio.ByteBuffer;

class Http2ProtocolUtils {
   public static void putInt(ByteBuffer buffer, int value) {
      buffer.put((byte)(value >> 24));
      buffer.put((byte)(value >> 16));
      buffer.put((byte)(value >> 8));
      buffer.put((byte)value);
   }

   public static void putInt(ByteBuffer buffer, int value, int position) {
      buffer.put(position, (byte)(value >> 24));
      buffer.put(position + 1, (byte)(value >> 16));
      buffer.put(position + 2, (byte)(value >> 8));
      buffer.put(position + 3, (byte)value);
   }

   public static int readInt(ByteBuffer buffer) {
      int id = (buffer.get() & 255) << 24;
      id += (buffer.get() & 255) << 16;
      id += (buffer.get() & 255) << 8;
      id += buffer.get() & 255;
      return id;
   }

   private Http2ProtocolUtils() {
   }
}
