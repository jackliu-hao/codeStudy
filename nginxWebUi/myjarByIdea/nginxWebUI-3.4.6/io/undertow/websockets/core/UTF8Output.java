package io.undertow.websockets.core;

import java.nio.ByteBuffer;
import org.xnio.Buffers;

public final class UTF8Output {
   private static final int UTF8_ACCEPT = 0;
   private static final byte HIGH_BIT = -128;
   private static final byte[] TYPES = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 10, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 3, 3, 11, 6, 6, 6, 5, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8};
   private static final byte[] STATES = new byte[]{0, 12, 24, 36, 60, 96, 84, 12, 12, 12, 48, 72, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 0, 12, 12, 12, 12, 12, 0, 12, 0, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12};
   private byte state = 0;
   private int codep;
   private final StringBuilder stringBuilder;

   public UTF8Output(ByteBuffer... payload) {
      this.stringBuilder = new StringBuilder((int)Buffers.remaining(payload));
      this.write(payload);
   }

   public UTF8Output() {
      this.stringBuilder = new StringBuilder();
   }

   public void write(ByteBuffer... bytes) {
      ByteBuffer[] var2 = bytes;
      int var3 = bytes.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ByteBuffer buf = var2[var4];

         while(buf.hasRemaining()) {
            this.write(buf.get());
         }
      }

   }

   private void write(byte b) {
      if (this.state == 0 && (b & -128) == 0) {
         this.stringBuilder.append((char)b);
      } else {
         byte type = TYPES[b & 255];
         this.codep = this.state != 0 ? b & 63 | this.codep << 6 : 255 >> type & b;
         this.state = STATES[this.state + type];
         if (this.state == 0) {
            char[] var3 = Character.toChars(this.codep);
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               char c = var3[var5];
               this.stringBuilder.append(c);
            }
         }

      }
   }

   public String extract() {
      String text = this.stringBuilder.toString();
      this.stringBuilder.setLength(0);
      return text;
   }

   public boolean hasData() {
      return this.stringBuilder.length() != 0;
   }
}
