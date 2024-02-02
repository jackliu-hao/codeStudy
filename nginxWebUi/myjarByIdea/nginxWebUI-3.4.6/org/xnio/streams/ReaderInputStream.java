package org.xnio.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import org.xnio.Buffers;
import org.xnio._private.Messages;

public final class ReaderInputStream extends InputStream {
   private final Reader reader;
   private final CharsetEncoder encoder;
   private final CharBuffer charBuffer;
   private final ByteBuffer byteBuffer;

   public ReaderInputStream(Reader reader) {
      this(reader, Charset.defaultCharset());
   }

   public ReaderInputStream(Reader reader, String charsetName) throws UnsupportedEncodingException {
      this(reader, Streams.getCharset(charsetName));
   }

   public ReaderInputStream(Reader reader, Charset charset) {
      this(reader, getEncoder(charset));
   }

   public ReaderInputStream(Reader reader, CharsetEncoder encoder) {
      this(reader, encoder, 1024);
   }

   public ReaderInputStream(Reader reader, CharsetEncoder encoder, int bufferSize) {
      if (reader == null) {
         throw Messages.msg.nullParameter("writer");
      } else if (encoder == null) {
         throw Messages.msg.nullParameter("decoder");
      } else if (bufferSize < 1) {
         throw Messages.msg.parameterOutOfRange("bufferSize");
      } else {
         this.reader = reader;
         this.encoder = encoder;
         this.charBuffer = CharBuffer.wrap(new char[bufferSize]);
         this.byteBuffer = ByteBuffer.wrap(new byte[(int)((float)bufferSize * encoder.averageBytesPerChar() + 0.5F)]);
         this.charBuffer.flip();
         this.byteBuffer.flip();
      }
   }

   private static CharsetEncoder getEncoder(Charset charset) {
      CharsetEncoder encoder = charset.newEncoder();
      encoder.onMalformedInput(CodingErrorAction.REPLACE);
      encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
      return encoder;
   }

   public int read() throws IOException {
      ByteBuffer byteBuffer = this.byteBuffer;
      return !byteBuffer.hasRemaining() && !this.fill() ? -1 : byteBuffer.get() & 255;
   }

   public int read(byte[] b, int off, int len) throws IOException {
      ByteBuffer byteBuffer = this.byteBuffer;
      int cnt = 0;

      while(len > 0) {
         int r = byteBuffer.remaining();
         if (r == 0) {
            if (!this.fill()) {
               return cnt == 0 ? -1 : cnt;
            }
         } else {
            int c = Math.min(r, len);
            byteBuffer.get(b, off, c);
            cnt += c;
            off += c;
            len -= c;
         }
      }

      return cnt;
   }

   private boolean fill() throws IOException {
      CharBuffer charBuffer = this.charBuffer;
      ByteBuffer byteBuffer = this.byteBuffer;
      byteBuffer.compact();
      boolean filled = false;

      try {
         while(byteBuffer.hasRemaining()) {
            boolean var5;
            while(charBuffer.hasRemaining()) {
               CoderResult result = this.encoder.encode(charBuffer, byteBuffer, false);
               if (result.isOverflow()) {
                  var5 = true;
                  return var5;
               }

               if (result.isUnderflow()) {
                  filled = true;
                  break;
               }

               if (result.isError()) {
                  if (result.isMalformed()) {
                     throw Messages.msg.malformedInput();
                  }

                  if (result.isUnmappable()) {
                     throw Messages.msg.unmappableCharacter();
                  }

                  throw Messages.msg.characterDecodingProblem();
               }
            }

            charBuffer.compact();

            try {
               int cnt = this.reader.read(charBuffer);
               if (cnt == -1) {
                  var5 = filled;
                  return var5;
               }

               if (cnt > 0) {
                  filled = true;
               }
            } finally {
               charBuffer.flip();
            }
         }

         boolean var15 = true;
         return var15;
      } finally {
         byteBuffer.flip();
      }
   }

   public long skip(long n) throws IOException {
      ByteBuffer byteBuffer = this.byteBuffer;
      int cnt = 0;

      while(n > 0L) {
         int r = byteBuffer.remaining();
         if (r == 0) {
            if (!this.fill()) {
               return (long)cnt;
            }
         } else {
            int c = Math.min(r, n > 2147483647L ? Integer.MAX_VALUE : (int)n);
            Buffers.skip(byteBuffer, c);
            cnt += c;
            n -= (long)c;
         }
      }

      return (long)cnt;
   }

   public int available() throws IOException {
      return this.byteBuffer.remaining();
   }

   public void close() throws IOException {
      this.byteBuffer.clear().flip();
      this.charBuffer.clear().flip();
      this.reader.close();
   }

   public String toString() {
      return "ReaderInputStream over " + this.reader;
   }
}
