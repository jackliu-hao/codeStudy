package org.xnio.streams;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import org.xnio._private.Messages;

public final class WriterOutputStream extends OutputStream {
   private final Writer writer;
   private final CharsetDecoder decoder;
   private final ByteBuffer byteBuffer;
   private final char[] chars;
   private volatile boolean closed;

   public WriterOutputStream(Writer writer) {
      this(writer, Charset.defaultCharset());
   }

   public WriterOutputStream(Writer writer, CharsetDecoder decoder) {
      this(writer, decoder, 1024);
   }

   public WriterOutputStream(Writer writer, CharsetDecoder decoder, int bufferSize) {
      if (writer == null) {
         throw Messages.msg.nullParameter("writer");
      } else if (decoder == null) {
         throw Messages.msg.nullParameter("decoder");
      } else if (bufferSize < 1) {
         throw Messages.msg.parameterOutOfRange("bufferSize");
      } else {
         this.writer = writer;
         this.decoder = decoder;
         this.byteBuffer = ByteBuffer.allocate(bufferSize);
         this.chars = new char[(int)((float)bufferSize * decoder.maxCharsPerByte() + 0.5F)];
      }
   }

   public WriterOutputStream(Writer writer, Charset charset) {
      this(writer, getDecoder(charset));
   }

   public WriterOutputStream(Writer writer, String charsetName) throws UnsupportedEncodingException {
      this(writer, Streams.getCharset(charsetName));
   }

   private static CharsetDecoder getDecoder(Charset charset) {
      CharsetDecoder decoder = charset.newDecoder();
      decoder.onMalformedInput(CodingErrorAction.REPLACE);
      decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
      decoder.replaceWith("?");
      return decoder;
   }

   public void write(int b) throws IOException {
      if (this.closed) {
         throw Messages.msg.streamClosed();
      } else {
         ByteBuffer byteBuffer = this.byteBuffer;
         if (!byteBuffer.hasRemaining()) {
            this.doFlush(false);
         }

         byteBuffer.put((byte)b);
      }
   }

   public void write(byte[] b, int off, int len) throws IOException {
      if (this.closed) {
         throw Messages.msg.streamClosed();
      } else {
         ByteBuffer byteBuffer = this.byteBuffer;

         while(len > 0) {
            int r = byteBuffer.remaining();
            if (r == 0) {
               this.doFlush(false);
            } else {
               int c = Math.min(len, r);
               byteBuffer.put(b, off, c);
               len -= c;
               off += c;
            }
         }

      }
   }

   private void doFlush(boolean eof) throws IOException {
      CharBuffer charBuffer = CharBuffer.wrap(this.chars);
      ByteBuffer byteBuffer = this.byteBuffer;
      CharsetDecoder decoder = this.decoder;
      byteBuffer.flip();

      while(true) {
         try {
            if (!byteBuffer.hasRemaining()) {
               return;
            }

            CoderResult result = decoder.decode(byteBuffer, charBuffer, eof);
            if (result.isOverflow()) {
               this.writer.write(this.chars, 0, charBuffer.position());
               charBuffer.clear();
               continue;
            }

            if (!result.isUnderflow()) {
               if (result.isError()) {
                  if (result.isMalformed()) {
                     throw Messages.msg.malformedInput();
                  }

                  if (result.isUnmappable()) {
                     throw Messages.msg.unmappableCharacter();
                  }

                  throw Messages.msg.characterDecodingProblem();
               }
               continue;
            }

            int p = charBuffer.position();
            if (p > 0) {
               this.writer.write(this.chars, 0, p);
            }
         } finally {
            byteBuffer.compact();
         }

         return;
      }
   }

   public void flush() throws IOException {
      if (this.closed) {
         throw Messages.msg.streamClosed();
      } else {
         this.doFlush(false);
         this.writer.flush();
      }
   }

   public void close() throws IOException {
      this.closed = true;
      this.doFlush(true);
      this.byteBuffer.clear();
      this.writer.close();
   }

   public String toString() {
      return "Output stream writing to " + this.writer;
   }
}
