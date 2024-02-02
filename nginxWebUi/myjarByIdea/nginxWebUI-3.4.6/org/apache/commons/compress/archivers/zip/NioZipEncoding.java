package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

class NioZipEncoding implements ZipEncoding, CharsetAccessor {
   private final Charset charset;
   private final boolean useReplacement;
   private static final char REPLACEMENT = '?';
   private static final byte[] REPLACEMENT_BYTES = new byte[]{63};
   private static final String REPLACEMENT_STRING = String.valueOf('?');
   private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

   NioZipEncoding(Charset charset, boolean useReplacement) {
      this.charset = charset;
      this.useReplacement = useReplacement;
   }

   public Charset getCharset() {
      return this.charset;
   }

   public boolean canEncode(String name) {
      CharsetEncoder enc = this.newEncoder();
      return enc.canEncode(name);
   }

   public ByteBuffer encode(String name) {
      CharsetEncoder enc = this.newEncoder();
      CharBuffer cb = CharBuffer.wrap(name);
      CharBuffer tmp = null;
      ByteBuffer out = ByteBuffer.allocate(estimateInitialBufferSize(enc, cb.remaining()));

      while(cb.hasRemaining()) {
         CoderResult res = enc.encode(cb, out, false);
         int increment;
         if (!res.isUnmappable() && !res.isMalformed()) {
            if (res.isOverflow()) {
               increment = estimateIncrementalEncodingSize(enc, cb.remaining());
               out = ZipEncodingHelper.growBufferBy(out, increment);
            } else if (res.isUnderflow() || res.isError()) {
               break;
            }
         } else {
            increment = estimateIncrementalEncodingSize(enc, 6 * res.length());
            int i;
            if (increment > out.remaining()) {
               i = 0;

               int totalExtraSpace;
               for(totalExtraSpace = cb.position(); totalExtraSpace < cb.limit(); ++totalExtraSpace) {
                  i += !enc.canEncode(cb.get(totalExtraSpace)) ? 6 : 1;
               }

               totalExtraSpace = estimateIncrementalEncodingSize(enc, i);
               out = ZipEncodingHelper.growBufferBy(out, totalExtraSpace - out.remaining());
            }

            if (tmp == null) {
               tmp = CharBuffer.allocate(6);
            }

            for(i = 0; i < res.length(); ++i) {
               out = encodeFully(enc, encodeSurrogate(tmp, cb.get()), out);
            }
         }
      }

      enc.encode(cb, out, true);
      out.limit(out.position());
      out.rewind();
      return out;
   }

   public String decode(byte[] data) throws IOException {
      return this.newDecoder().decode(ByteBuffer.wrap(data)).toString();
   }

   private static ByteBuffer encodeFully(CharsetEncoder enc, CharBuffer cb, ByteBuffer out) {
      ByteBuffer o = out;

      while(cb.hasRemaining()) {
         CoderResult result = enc.encode(cb, o, false);
         if (result.isOverflow()) {
            int increment = estimateIncrementalEncodingSize(enc, cb.remaining());
            o = ZipEncodingHelper.growBufferBy(o, increment);
         }
      }

      return o;
   }

   private static CharBuffer encodeSurrogate(CharBuffer cb, char c) {
      cb.position(0).limit(6);
      cb.put('%');
      cb.put('U');
      cb.put(HEX_CHARS[c >> 12 & 15]);
      cb.put(HEX_CHARS[c >> 8 & 15]);
      cb.put(HEX_CHARS[c >> 4 & 15]);
      cb.put(HEX_CHARS[c & 15]);
      cb.flip();
      return cb;
   }

   private CharsetEncoder newEncoder() {
      return this.useReplacement ? this.charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith(REPLACEMENT_BYTES) : this.charset.newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
   }

   private CharsetDecoder newDecoder() {
      return !this.useReplacement ? this.charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT) : this.charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith(REPLACEMENT_STRING);
   }

   private static int estimateInitialBufferSize(CharsetEncoder enc, int charChount) {
      float first = enc.maxBytesPerChar();
      float rest = (float)(charChount - 1) * enc.averageBytesPerChar();
      return (int)Math.ceil((double)(first + rest));
   }

   private static int estimateIncrementalEncodingSize(CharsetEncoder enc, int charCount) {
      return (int)Math.ceil((double)((float)charCount * enc.averageBytesPerChar()));
   }
}
