package org.h2.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

public class CountingReaderInputStream extends InputStream {
   private final Reader reader;
   private final CharBuffer charBuffer = CharBuffer.allocate(4096);
   private final CharsetEncoder encoder;
   private ByteBuffer byteBuffer;
   private long length;
   private long remaining;

   public CountingReaderInputStream(Reader var1, long var2) {
      this.encoder = StandardCharsets.UTF_8.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
      this.byteBuffer = ByteBuffer.allocate(0);
      this.reader = var1;
      this.remaining = var2;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (!this.fetch()) {
         return -1;
      } else {
         var3 = Math.min(var3, this.byteBuffer.remaining());
         this.byteBuffer.get(var1, var2, var3);
         return var3;
      }
   }

   public int read() throws IOException {
      return !this.fetch() ? -1 : this.byteBuffer.get() & 255;
   }

   private boolean fetch() throws IOException {
      if (this.byteBuffer != null && this.byteBuffer.remaining() == 0) {
         this.fillBuffer();
      }

      return this.byteBuffer != null;
   }

   private void fillBuffer() throws IOException {
      int var1 = (int)Math.min((long)(this.charBuffer.capacity() - this.charBuffer.position()), this.remaining);
      if (var1 > 0) {
         var1 = this.reader.read(this.charBuffer.array(), this.charBuffer.position(), var1);
      }

      if (var1 > 0) {
         this.remaining -= (long)var1;
      } else {
         var1 = 0;
         this.remaining = 0L;
      }

      this.length += (long)var1;
      this.charBuffer.limit(this.charBuffer.position() + var1);
      this.charBuffer.rewind();
      this.byteBuffer = ByteBuffer.allocate(4096);
      boolean var2 = this.remaining == 0L;
      this.encoder.encode(this.charBuffer, this.byteBuffer, var2);
      if (var2 && this.byteBuffer.position() == 0) {
         this.byteBuffer = null;
      } else {
         this.byteBuffer.flip();
         this.charBuffer.compact();
         this.charBuffer.flip();
         this.charBuffer.position(this.charBuffer.limit());
      }
   }

   public long getLength() {
      return this.length;
   }

   public void close() throws IOException {
      this.reader.close();
   }
}
