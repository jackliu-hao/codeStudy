package io.undertow.protocols.ajp;

import io.undertow.util.HttpString;
import java.nio.ByteBuffer;

class AjpUtils {
   static boolean notNull(Boolean attachment) {
      return attachment == null ? false : attachment;
   }

   static int notNull(Integer attachment) {
      return attachment == null ? 0 : attachment;
   }

   static String notNull(String attachment) {
      return attachment == null ? "" : attachment;
   }

   static void putInt(ByteBuffer buf, int value) {
      buf.put((byte)(value >> 8 & 255));
      buf.put((byte)(value & 255));
   }

   static void putString(ByteBuffer buf, String value) {
      int length = value.length();
      putInt(buf, length);

      for(int i = 0; i < length; ++i) {
         buf.put((byte)value.charAt(i));
      }

      buf.put((byte)0);
   }

   static void putHttpString(ByteBuffer buf, HttpString value) {
      int length = value.length();
      putInt(buf, length);
      value.appendTo(buf);
      buf.put((byte)0);
   }
}
