package io.undertow.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

public class FlexBase64 {
   private static final byte[] STANDARD_ENCODING_TABLE;
   private static final byte[] STANDARD_DECODING_TABLE = new byte[80];
   private static final byte[] URL_ENCODING_TABLE;
   private static final byte[] URL_DECODING_TABLE = new byte[80];
   private static final Constructor<String> STRING_CONSTRUCTOR;

   public static Encoder createEncoder(boolean wrap) {
      return new Encoder(wrap, false);
   }

   public static Encoder createURLEncoder(boolean wrap) {
      return new Encoder(wrap, true);
   }

   public static Decoder createDecoder() {
      return new Decoder(false);
   }

   public static Decoder createURLDecoder() {
      return new Decoder(true);
   }

   public static String encodeString(byte[] source, boolean wrap) {
      return FlexBase64.Encoder.encodeString(source, 0, source.length, wrap, false);
   }

   public static String encodeStringURL(byte[] source, boolean wrap) {
      return FlexBase64.Encoder.encodeString(source, 0, source.length, wrap, true);
   }

   public static String encodeString(byte[] source, int pos, int limit, boolean wrap) {
      return FlexBase64.Encoder.encodeString(source, pos, limit, wrap, false);
   }

   public static String encodeStringURL(byte[] source, int pos, int limit, boolean wrap) {
      return FlexBase64.Encoder.encodeString(source, pos, limit, wrap, true);
   }

   public static String encodeString(ByteBuffer source, boolean wrap) {
      return FlexBase64.Encoder.encodeString(source, wrap, false);
   }

   public static String encodeStringURL(ByteBuffer source, boolean wrap) {
      return FlexBase64.Encoder.encodeString(source, wrap, true);
   }

   public static byte[] encodeBytes(byte[] source, int pos, int limit, boolean wrap) {
      return FlexBase64.Encoder.encodeBytes(source, pos, limit, wrap, false);
   }

   public static byte[] encodeBytesURL(byte[] source, int pos, int limit, boolean wrap) {
      return FlexBase64.Encoder.encodeBytes(source, pos, limit, wrap, true);
   }

   public static ByteBuffer decode(String source) throws IOException {
      return FlexBase64.Decoder.decode(source, false);
   }

   public static ByteBuffer decodeURL(String source) throws IOException {
      return FlexBase64.Decoder.decode(source, true);
   }

   public static ByteBuffer decode(ByteBuffer source) throws IOException {
      return FlexBase64.Decoder.decode(source, false);
   }

   public static ByteBuffer decodeURL(ByteBuffer source) throws IOException {
      return FlexBase64.Decoder.decode(source, true);
   }

   public static ByteBuffer decode(byte[] source, int off, int limit) throws IOException {
      return FlexBase64.Decoder.decode(source, off, limit, false);
   }

   public static ByteBuffer decodeURL(byte[] source, int off, int limit) throws IOException {
      return FlexBase64.Decoder.decode(source, off, limit, true);
   }

   public static EncoderInputStream createEncoderInputStream(InputStream source, int bufferSize, boolean wrap) {
      return new EncoderInputStream(source, bufferSize, wrap, false);
   }

   public static EncoderInputStream createEncoderInputStream(InputStream source) {
      return new EncoderInputStream(source);
   }

   public static DecoderInputStream createDecoderInputStream(InputStream source, int bufferSize) {
      return new DecoderInputStream(source, bufferSize);
   }

   public static DecoderInputStream createDecoderInputStream(InputStream source) {
      return new DecoderInputStream(source);
   }

   public static EncoderOutputStream createEncoderOutputStream(OutputStream target, int bufferSize, boolean wrap) {
      return new EncoderOutputStream(target, bufferSize, wrap);
   }

   public static EncoderOutputStream createEncoderOutputStream(OutputStream output) {
      return new EncoderOutputStream(output);
   }

   public static DecoderOutputStream createDecoderOutputStream(OutputStream output, int bufferSize) {
      return new DecoderOutputStream(output, bufferSize);
   }

   public static DecoderOutputStream createDecoderOutputStream(OutputStream output) {
      return new DecoderOutputStream(output);
   }

   static {
      STANDARD_ENCODING_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes(StandardCharsets.US_ASCII);
      URL_ENCODING_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".getBytes(StandardCharsets.US_ASCII);

      int i;
      int v;
      for(i = 0; i < STANDARD_ENCODING_TABLE.length; ++i) {
         v = (STANDARD_ENCODING_TABLE[i] & 255) - 43;
         STANDARD_DECODING_TABLE[v] = (byte)(i + 1);
      }

      for(i = 0; i < URL_ENCODING_TABLE.length; ++i) {
         v = (URL_ENCODING_TABLE[i] & 255) - 43;
         URL_DECODING_TABLE[v] = (byte)(i + 1);
      }

      Constructor<String> c = null;

      try {
         PrivilegedExceptionAction<Constructor<String>> runnable = new PrivilegedExceptionAction<Constructor<String>>() {
            public Constructor<String> run() throws Exception {
               Constructor<String> c = String.class.getDeclaredConstructor(char[].class, Boolean.TYPE);
               c.setAccessible(true);
               return c;
            }
         };
         if (System.getSecurityManager() != null) {
            c = (Constructor)AccessController.doPrivileged(runnable);
         } else {
            c = (Constructor)runnable.run();
         }
      } catch (Throwable var2) {
      }

      STRING_CONSTRUCTOR = c;
   }

   public static class DecoderOutputStream extends OutputStream {
      private final OutputStream output;
      private final byte[] buffer;
      private final Decoder decoder;
      private int pos;
      private byte[] one;

      private DecoderOutputStream(OutputStream output) {
         this(output, 8192);
      }

      private DecoderOutputStream(OutputStream output, int bufferSize) {
         this.pos = 0;
         this.output = output;
         this.buffer = new byte[bufferSize];
         this.decoder = FlexBase64.createDecoder();
      }

      public void write(byte[] b, int off, int len) throws IOException {
         byte[] buffer = this.buffer;
         Decoder decoder = this.decoder;
         int pos = this.pos;
         int limit = off + len;

         int last;
         for(int ipos = off; ipos < limit; ipos = last) {
            pos = decoder.decode(b, ipos, limit, buffer, pos, buffer.length);
            last = decoder.getLastInputPosition();
            if (last == ipos || pos >= buffer.length) {
               this.output.write(buffer, 0, pos);
               pos = 0;
            }
         }

         this.pos = pos;
      }

      public void write(int b) throws IOException {
         byte[] one = this.one;
         if (one == null) {
            this.one = one = new byte[1];
         }

         one[0] = (byte)b;
         this.write(one, 0, 1);
      }

      public void flush() throws IOException {
         OutputStream output = this.output;
         output.write(this.buffer, 0, this.pos);
         output.flush();
      }

      public void close() throws IOException {
         try {
            this.flush();
         } catch (IOException var3) {
         }

         try {
            this.output.flush();
         } catch (IOException var2) {
         }

         this.output.close();
      }

      // $FF: synthetic method
      DecoderOutputStream(OutputStream x0, int x1, Object x2) {
         this(x0, x1);
      }

      // $FF: synthetic method
      DecoderOutputStream(OutputStream x0, Object x1) {
         this(x0);
      }
   }

   public static class EncoderOutputStream extends OutputStream {
      private final OutputStream output;
      private final byte[] buffer;
      private final Encoder encoder;
      private int pos;
      private byte[] one;

      private EncoderOutputStream(OutputStream output) {
         this(output, 8192, true);
      }

      private EncoderOutputStream(OutputStream output, int bufferSize, boolean wrap) {
         this.pos = 0;
         this.output = output;
         this.buffer = new byte[bufferSize];
         this.encoder = FlexBase64.createEncoder(wrap);
      }

      public void write(byte[] b, int off, int len) throws IOException {
         byte[] buffer = this.buffer;
         Encoder encoder = this.encoder;
         int pos = this.pos;
         int limit = off + len;

         int last;
         for(int ipos = off; ipos < limit; ipos = last) {
            pos = encoder.encode(b, ipos, limit, buffer, pos, buffer.length);
            last = encoder.getLastInputPosition();
            if (last == ipos || pos >= buffer.length) {
               this.output.write(buffer, 0, pos);
               pos = 0;
            }
         }

         this.pos = pos;
      }

      public void write(int b) throws IOException {
         byte[] one = this.one;
         if (one == null) {
            this.one = one = new byte[1];
         }

         one[0] = (byte)b;
         this.write(one, 0, 1);
      }

      public void flush() throws IOException {
         OutputStream output = this.output;
         output.write(this.buffer, 0, this.pos);
         output.flush();
      }

      public void complete() throws IOException {
         OutputStream output = this.output;
         byte[] buffer = this.buffer;
         int pos = this.pos;
         boolean completed = false;
         if (buffer.length - pos >= (this.encoder.wrap ? 2 : 4)) {
            this.pos = this.encoder.complete(buffer, pos);
            completed = true;
         }

         this.flush();
         if (!completed) {
            int len = this.encoder.complete(buffer, 0);
            output.write(buffer, 0, len);
            output.flush();
         }

      }

      public void close() throws IOException {
         try {
            this.complete();
         } catch (IOException var3) {
         }

         try {
            this.output.flush();
         } catch (IOException var2) {
         }

         this.output.close();
      }

      // $FF: synthetic method
      EncoderOutputStream(OutputStream x0, int x1, boolean x2, Object x3) {
         this(x0, x1, x2);
      }

      // $FF: synthetic method
      EncoderOutputStream(OutputStream x0, Object x1) {
         this(x0);
      }
   }

   public static class EncoderInputStream extends InputStream {
      private final InputStream input;
      private final byte[] buffer;
      private final byte[] overflow;
      private int overflowPos;
      private int overflowLimit;
      private final Encoder encoder;
      private int pos;
      private int limit;
      private byte[] one;
      private boolean complete;

      private EncoderInputStream(InputStream input) {
         this(input, 8192, true, false);
      }

      private EncoderInputStream(InputStream input, int bufferSize, boolean wrap, boolean url) {
         this.overflow = new byte[6];
         this.pos = 0;
         this.limit = 0;
         this.input = input;
         this.buffer = new byte[bufferSize];
         this.encoder = new Encoder(wrap, url);
      }

      private int fill() throws IOException {
         byte[] buffer = this.buffer;
         int read = this.input.read(buffer, 0, buffer.length);
         this.pos = 0;
         this.limit = read;
         return read;
      }

      public int read() throws IOException {
         byte[] one = this.one;
         if (one == null) {
            one = this.one = new byte[1];
         }

         int read = this.read(one, 0, 1);
         return read > 0 ? one[0] & 255 : -1;
      }

      public int read(byte[] b, int off, int len) throws IOException {
         byte[] buffer = this.buffer;
         byte[] overflow = this.overflow;
         int overflowPos = this.overflowPos;
         int overflowLimit = this.overflowLimit;
         boolean complete = this.complete;
         boolean wrap = this.encoder.wrap;
         int copy = 0;
         if (overflowPos < overflowLimit) {
            copy = this.copyOverflow(b, off, len, overflow, overflowPos, overflowLimit);
            if (len <= copy || complete) {
               return copy;
            }

            len -= copy;
            off += copy;
         } else if (complete) {
            return -1;
         }

         int ret;
         do {
            byte[] source = buffer;
            int pos = this.pos;
            int limit = this.limit;
            boolean setPos = true;
            if (pos >= limit) {
               if (len > buffer.length) {
                  ret = len / 4 * 3 - 3;
                  if (wrap) {
                     ret -= ret / 76 * 2 + 2;
                  }

                  source = new byte[ret];
                  limit = this.input.read(source, 0, ret);
                  pos = 0;
                  setPos = false;
               } else {
                  limit = this.fill();
                  pos = 0;
               }

               if (limit <= 0) {
                  this.complete = true;
                  if (len < (wrap ? 4 : 2)) {
                     overflowLimit = this.encoder.complete(overflow, 0);
                     this.overflowLimit = overflowLimit;
                     ret = this.copyOverflow(b, off, len, overflow, 0, overflowLimit) + copy;
                     return ret == 0 ? -1 : ret;
                  }

                  ret = this.encoder.complete(b, off) - off + copy;
                  return ret == 0 ? -1 : ret;
               }
            }

            if (len < (wrap ? 6 : 4)) {
               overflowLimit = this.encoder.encode(source, pos, limit, overflow, 0, overflow.length);
               this.overflowLimit = overflowLimit;
               this.pos = this.encoder.getLastInputPosition();
               return this.copyOverflow(b, off, len, overflow, 0, overflowLimit) + copy;
            }

            ret = this.encoder.encode(source, pos, limit, b, off, off + len) - off;
            if (setPos) {
               this.pos = this.encoder.getLastInputPosition();
            }
         } while(ret <= 0);

         return ret + copy;
      }

      private int copyOverflow(byte[] b, int off, int len, byte[] overflow, int pos, int limit) {
         limit -= pos;
         len = limit <= len ? limit : len;
         System.arraycopy(overflow, pos, b, off, len);
         this.overflowPos = pos + len;
         return len;
      }

      // $FF: synthetic method
      EncoderInputStream(InputStream x0, int x1, boolean x2, boolean x3, Object x4) {
         this(x0, x1, x2, x3);
      }

      // $FF: synthetic method
      EncoderInputStream(InputStream x0, Object x1) {
         this(x0);
      }
   }

   public static class DecoderInputStream extends InputStream {
      private final InputStream input;
      private final byte[] buffer;
      private final Decoder decoder;
      private int pos;
      private int limit;
      private byte[] one;

      private DecoderInputStream(InputStream input) {
         this(input, 8192);
      }

      private DecoderInputStream(InputStream input, int bufferSize) {
         this.decoder = FlexBase64.createDecoder();
         this.pos = 0;
         this.limit = 0;
         this.input = input;
         this.buffer = new byte[bufferSize];
      }

      private int fill() throws IOException {
         byte[] buffer = this.buffer;
         int read = this.input.read(buffer, 0, buffer.length);
         this.pos = 0;
         this.limit = read;
         return read;
      }

      public int read(byte[] b, int off, int len) throws IOException {
         int read;
         do {
            byte[] source = this.buffer;
            int pos = this.pos;
            int limit = this.limit;
            boolean setPos = true;
            if (pos >= limit) {
               if (len > source.length) {
                  source = new byte[len];
                  limit = this.input.read(source, 0, len);
                  pos = 0;
                  setPos = false;
               } else {
                  limit = this.fill();
                  pos = 0;
               }

               if (limit == -1) {
                  return -1;
               }
            }

            int requested = len + pos;
            limit = limit > requested ? requested : limit;
            read = this.decoder.decode(source, pos, limit, b, off, off + len) - off;
            if (setPos) {
               this.pos = this.decoder.getLastInputPosition();
            }
         } while(read <= 0);

         return read;
      }

      public int read() throws IOException {
         byte[] one = this.one;
         if (one == null) {
            one = this.one = new byte[1];
         }

         int read = this.read(one, 0, 1);
         return read > 0 ? one[0] & 255 : -1;
      }

      public void close() throws IOException {
         this.input.close();
      }

      // $FF: synthetic method
      DecoderInputStream(InputStream x0, int x1, Object x2) {
         this(x0, x1);
      }

      // $FF: synthetic method
      DecoderInputStream(InputStream x0, Object x1) {
         this(x0);
      }
   }

   public static final class Decoder {
      private int state;
      private int last;
      private int lastPos;
      private final byte[] decodingTable;
      private static final int SKIP = 64768;
      private static final int MARK = 65024;
      private static final int DONE = 65280;
      private static final int ERROR = 983040;

      private Decoder(boolean url) {
         this.decodingTable = url ? FlexBase64.URL_DECODING_TABLE : FlexBase64.STANDARD_DECODING_TABLE;
      }

      private int nextByte(ByteBuffer buffer, int state, int last, boolean ignoreErrors) throws IOException {
         return this.nextByte(buffer.get() & 255, state, last, ignoreErrors);
      }

      private int nextByte(Object source, int pos, int state, int last, boolean ignoreErrors) throws IOException {
         int c;
         if (source instanceof byte[]) {
            c = ((byte[])((byte[])source))[pos] & 255;
         } else {
            if (!(source instanceof String)) {
               throw new IllegalArgumentException();
            }

            c = ((String)source).charAt(pos) & 255;
         }

         return this.nextByte(c, state, last, ignoreErrors);
      }

      private int nextByte(int c, int state, int last, boolean ignoreErrors) throws IOException {
         if (last == 65024) {
            if (c != 61) {
               throw new IOException("Expected padding character");
            } else {
               return 65280;
            }
         } else if (c == 61) {
            if (state == 2) {
               return 65024;
            } else if (state == 3) {
               return 65280;
            } else {
               throw new IOException("Unexpected padding character");
            }
         } else if (c != 32 && c != 9 && c != 13 && c != 10) {
            if (c >= 43 && c <= 122) {
               int b = (this.decodingTable[c - 43] & 255) - 1;
               if (b < 0) {
                  if (ignoreErrors) {
                     return 983040;
                  } else {
                     throw new IOException("Invalid base64 character encountered: " + c);
                  }
               } else {
                  return b;
               }
            } else if (ignoreErrors) {
               return 983040;
            } else {
               throw new IOException("Invalid base64 character encountered: " + c);
            }
         } else {
            return 64768;
         }
      }

      public void decode(ByteBuffer source, ByteBuffer target) throws IOException {
         if (target == null) {
            throw new IllegalStateException();
         } else {
            int last = this.last;
            int state = this.state;
            int remaining = source.remaining();
            int targetRemaining = target.remaining();
            int b = 0;

            while(remaining-- > 0 && targetRemaining > 0) {
               b = this.nextByte(source, state, last, false);
               if (b == 65024) {
                  last = 65024;
                  --remaining;
                  if (remaining <= 0) {
                     break;
                  }

                  b = this.nextByte(source, state, last, false);
               }

               if (b == 65280) {
                  state = 0;
                  last = 0;
                  break;
               }

               if (b != 64768) {
                  if (state == 0) {
                     last = b << 2;
                     ++state;
                     if (remaining-- <= 0) {
                        break;
                     }

                     b = this.nextByte(source, state, last, false);
                     if ((b & '\uf000') != 0) {
                        source.position(source.position() - 1);
                        continue;
                     }
                  }

                  if (state == 1) {
                     target.put((byte)(last | b >>> 4));
                     last = (b & 15) << 4;
                     ++state;
                     if (remaining-- <= 0) {
                        break;
                     }

                     --targetRemaining;
                     if (targetRemaining <= 0) {
                        break;
                     }

                     b = this.nextByte(source, state, last, false);
                     if ((b & '\uf000') != 0) {
                        source.position(source.position() - 1);
                        continue;
                     }
                  }

                  if (state == 2) {
                     target.put((byte)(last | b >>> 2));
                     last = (b & 3) << 6;
                     ++state;
                     if (remaining-- <= 0) {
                        break;
                     }

                     --targetRemaining;
                     if (targetRemaining <= 0) {
                        break;
                     }

                     b = this.nextByte(source, state, last, false);
                     if ((b & '\uf000') != 0) {
                        source.position(source.position() - 1);
                        continue;
                     }
                  }

                  if (state == 3) {
                     target.put((byte)(last | b));
                     state = 0;
                     last = 0;
                     --targetRemaining;
                  }
               }
            }

            if (remaining > 0) {
               this.drain(source, b, state, last);
            }

            this.last = last;
            this.state = state;
            this.lastPos = source.position();
         }
      }

      private void drain(ByteBuffer source, int b, int state, int last) {
         while(true) {
            if (b != 65280 && source.remaining() > 0) {
               try {
                  b = this.nextByte(source, state, last, true);
               } catch (IOException var6) {
                  b = 0;
               }

               if (b == 65024) {
                  last = 65024;
                  continue;
               }

               if ((b & '\uf000') != 0) {
                  continue;
               }

               source.position(source.position() - 1);
            }

            if (b == 65280) {
               while(source.remaining() > 0) {
                  int b = source.get();
                  if (b == 10) {
                     break;
                  }

                  if (b != 32 && b != 9 && b != 13) {
                     source.position(source.position() - 1);
                     break;
                  }
               }
            }

            return;
         }
      }

      private int drain(Object source, int pos, int limit, int b, int state, int last) {
         while(true) {
            if (b != 65280 && limit > pos) {
               try {
                  b = this.nextByte(source, pos++, state, last, true);
               } catch (IOException var8) {
                  b = 0;
               }

               if (b == 65024) {
                  last = 65024;
                  continue;
               }

               if ((b & '\uf000') != 0) {
                  continue;
               }

               --pos;
            }

            if (b == 65280) {
               while(limit > pos) {
                  if (source instanceof byte[]) {
                     b = ((byte[])((byte[])source))[pos++] & 255;
                  } else {
                     if (!(source instanceof String)) {
                        throw new IllegalArgumentException();
                     }

                     b = ((String)source).charAt(pos++) & 255;
                  }

                  if (b == 10) {
                     break;
                  }

                  if (b != 32 && b != 9 && b != 13) {
                     --pos;
                     break;
                  }
               }
            }

            return pos;
         }
      }

      private int decode(Object source, int sourcePos, int sourceLimit, byte[] target, int targetPos, int targetLimit) throws IOException {
         if (target == null) {
            throw new IllegalStateException();
         } else {
            int last = this.last;
            int state = this.state;
            int pos = sourcePos;
            int opos = targetPos;
            int limit = sourceLimit;
            int olimit = targetLimit;
            int b = 0;

            while(limit > pos && olimit > opos) {
               b = this.nextByte(source, pos++, state, last, false);
               if (b == 65024) {
                  last = 65024;
                  if (pos >= limit) {
                     break;
                  }

                  b = this.nextByte(source, pos++, state, last, false);
               }

               if (b == 65280) {
                  state = 0;
                  last = 0;
                  break;
               }

               if (b != 64768) {
                  if (state == 0) {
                     last = b << 2;
                     ++state;
                     if (pos >= limit) {
                        break;
                     }

                     b = this.nextByte(source, pos++, state, last, false);
                     if ((b & '\uf000') != 0) {
                        --pos;
                        continue;
                     }
                  }

                  if (state == 1) {
                     target[opos++] = (byte)(last | b >>> 4);
                     last = (b & 15) << 4;
                     ++state;
                     if (pos >= limit || opos >= olimit) {
                        break;
                     }

                     b = this.nextByte(source, pos++, state, last, false);
                     if ((b & '\uf000') != 0) {
                        --pos;
                        continue;
                     }
                  }

                  if (state == 2) {
                     target[opos++] = (byte)(last | b >>> 2);
                     last = (b & 3) << 6;
                     ++state;
                     if (pos >= limit || opos >= olimit) {
                        break;
                     }

                     b = this.nextByte(source, pos++, state, last, false);
                     if ((b & '\uf000') != 0) {
                        --pos;
                        continue;
                     }
                  }

                  if (state == 3) {
                     target[opos++] = (byte)(last | b);
                     state = 0;
                     last = 0;
                  }
               }
            }

            if (limit > pos) {
               pos = this.drain(source, pos, limit, b, state, last);
            }

            this.last = last;
            this.state = state;
            this.lastPos = pos;
            return opos;
         }
      }

      public int getLastInputPosition() {
         return this.lastPos;
      }

      public int decode(String source, int sourcePos, int sourceLimit, byte[] target, int targetPos, int targetLimit) throws IOException {
         return this.decode((Object)source, sourcePos, sourceLimit, target, targetPos, targetLimit);
      }

      public int decode(String source, byte[] target) throws IOException {
         return this.decode((String)source, 0, source.length(), target, 0, target.length);
      }

      public int decode(byte[] source, int sourcePos, int sourceLimit, byte[] target, int targetPos, int targetLimit) throws IOException {
         return this.decode((Object)source, sourcePos, sourceLimit, target, targetPos, targetLimit);
      }

      private static ByteBuffer decode(String source, boolean url) throws IOException {
         int remainder = source.length() % 4;
         int size = (source.length() / 4 + (remainder == 0 ? 0 : 4 - remainder)) * 3;
         byte[] buffer = new byte[size];
         int actual = (new Decoder(url)).decode((String)source, 0, source.length(), buffer, 0, size);
         return ByteBuffer.wrap(buffer, 0, actual);
      }

      private static ByteBuffer decode(byte[] source, int off, int limit, boolean url) throws IOException {
         int len = limit - off;
         int remainder = len % 4;
         int size = (len / 4 + (remainder == 0 ? 0 : 4 - remainder)) * 3;
         byte[] buffer = new byte[size];
         int actual = (new Decoder(url)).decode((byte[])source, off, limit, buffer, 0, size);
         return ByteBuffer.wrap(buffer, 0, actual);
      }

      private static ByteBuffer decode(ByteBuffer source, boolean url) throws IOException {
         int len = source.remaining();
         int remainder = len % 4;
         int size = (len / 4 + (remainder == 0 ? 0 : 4 - remainder)) * 3;
         ByteBuffer buffer = ByteBuffer.allocate(size);
         (new Decoder(url)).decode(source, buffer);
         buffer.flip();
         return buffer;
      }

      // $FF: synthetic method
      Decoder(boolean x0, Object x1) {
         this(x0);
      }
   }

   public static final class Encoder {
      private int state;
      private int last;
      private int count;
      private final boolean wrap;
      private int lastPos;
      private final byte[] encodingTable;

      private Encoder(boolean wrap, boolean url) {
         this.wrap = wrap;
         this.encodingTable = url ? FlexBase64.URL_ENCODING_TABLE : FlexBase64.STANDARD_ENCODING_TABLE;
      }

      public void encode(ByteBuffer source, ByteBuffer target) {
         if (target == null) {
            throw new IllegalStateException();
         } else {
            int last = this.last;
            int state = this.state;
            boolean wrap = this.wrap;
            int count = this.count;
            byte[] ENCODING_TABLE = this.encodingTable;
            int remaining = source.remaining();

            while(remaining > 0) {
               int require = 4 - state;
               require = wrap && count >= 72 ? require + 2 : require;
               if (target.remaining() < require) {
                  break;
               }

               int b = source.get() & 255;
               if (state == 0) {
                  target.put(ENCODING_TABLE[b >>> 2]);
                  last = (b & 3) << 4;
                  ++state;
                  --remaining;
                  if (remaining <= 0) {
                     break;
                  }

                  b = source.get() & 255;
               }

               if (state == 1) {
                  target.put(ENCODING_TABLE[last | b >>> 4]);
                  last = (b & 15) << 2;
                  ++state;
                  --remaining;
                  if (remaining <= 0) {
                     break;
                  }

                  b = source.get() & 255;
               }

               if (state == 2) {
                  target.put(ENCODING_TABLE[last | b >>> 6]);
                  target.put(ENCODING_TABLE[b & 63]);
                  state = 0;
                  last = 0;
                  --remaining;
               }

               if (wrap) {
                  count += 4;
                  if (count >= 76) {
                     count = 0;
                     target.putShort((short)3338);
                  }
               }
            }

            this.count = count;
            this.last = last;
            this.state = state;
            this.lastPos = source.position();
         }
      }

      public int encode(byte[] source, int pos, int limit, byte[] target, int opos, int olimit) {
         if (target == null) {
            throw new IllegalStateException();
         } else {
            int last = this.last;
            int state = this.state;
            int count = this.count;
            boolean wrap = this.wrap;
            byte[] ENCODING_TABLE = this.encodingTable;

            while(limit > pos) {
               int require = 4 - state;
               require = wrap && count >= 72 ? require + 2 : require;
               if (require + opos > olimit) {
                  break;
               }

               int b = source[pos++] & 255;
               if (state == 0) {
                  target[opos++] = ENCODING_TABLE[b >>> 2];
                  last = (b & 3) << 4;
                  ++state;
                  if (pos >= limit) {
                     break;
                  }

                  b = source[pos++] & 255;
               }

               if (state == 1) {
                  target[opos++] = ENCODING_TABLE[last | b >>> 4];
                  last = (b & 15) << 2;
                  ++state;
                  if (pos >= limit) {
                     break;
                  }

                  b = source[pos++] & 255;
               }

               if (state == 2) {
                  target[opos++] = ENCODING_TABLE[last | b >>> 6];
                  target[opos++] = ENCODING_TABLE[b & 63];
                  state = 0;
                  last = 0;
               }

               if (wrap) {
                  count += 4;
                  if (count >= 76) {
                     count = 0;
                     target[opos++] = 13;
                     target[opos++] = 10;
                  }
               }
            }

            this.count = count;
            this.last = last;
            this.state = state;
            this.lastPos = pos;
            return opos;
         }
      }

      private static String encodeString(byte[] source, int pos, int limit, boolean wrap, boolean url) {
         int olimit = limit - pos;
         int remainder = olimit % 3;
         olimit = (olimit + (remainder == 0 ? 0 : 3 - remainder)) / 3 * 4;
         olimit += wrap ? olimit / 76 * 2 + 2 : 0;
         char[] target = new char[olimit];
         int opos = 0;
         int last = 0;
         int count = 0;
         int state = 0;
         byte[] ENCODING_TABLE = url ? FlexBase64.URL_ENCODING_TABLE : FlexBase64.STANDARD_ENCODING_TABLE;

         while(limit > pos) {
            int b = source[pos++] & 255;
            target[opos++] = (char)ENCODING_TABLE[b >>> 2];
            last = (b & 3) << 4;
            if (pos >= limit) {
               state = 1;
               break;
            }

            b = source[pos++] & 255;
            target[opos++] = (char)ENCODING_TABLE[last | b >>> 4];
            last = (b & 15) << 2;
            if (pos >= limit) {
               state = 2;
               break;
            }

            b = source[pos++] & 255;
            target[opos++] = (char)ENCODING_TABLE[last | b >>> 6];
            target[opos++] = (char)ENCODING_TABLE[b & 63];
            if (wrap) {
               count += 4;
               if (count >= 76) {
                  count = 0;
                  target[opos++] = '\r';
                  target[opos++] = '\n';
               }
            }
         }

         complete((char[])target, opos, state, last, wrap, url);

         try {
            if (FlexBase64.STRING_CONSTRUCTOR != null) {
               return (String)FlexBase64.STRING_CONSTRUCTOR.newInstance(target, Boolean.TRUE);
            }
         } catch (Exception var14) {
         }

         return new String(target);
      }

      private static byte[] encodeBytes(byte[] source, int pos, int limit, boolean wrap, boolean url) {
         int olimit = limit - pos;
         int remainder = olimit % 3;
         olimit = (olimit + (remainder == 0 ? 0 : 3 - remainder)) / 3 * 4;
         olimit += wrap ? olimit / 76 * 2 + 2 : 0;
         byte[] target = new byte[olimit];
         int opos = 0;
         int count = 0;
         int last = 0;
         int state = 0;
         byte[] ENCODING_TABLE = url ? FlexBase64.URL_ENCODING_TABLE : FlexBase64.STANDARD_ENCODING_TABLE;

         while(limit > pos) {
            int b = source[pos++] & 255;
            target[opos++] = ENCODING_TABLE[b >>> 2];
            last = (b & 3) << 4;
            if (pos >= limit) {
               state = 1;
               break;
            }

            b = source[pos++] & 255;
            target[opos++] = ENCODING_TABLE[last | b >>> 4];
            last = (b & 15) << 2;
            if (pos >= limit) {
               state = 2;
               break;
            }

            b = source[pos++] & 255;
            target[opos++] = ENCODING_TABLE[last | b >>> 6];
            target[opos++] = ENCODING_TABLE[b & 63];
            if (wrap) {
               count += 4;
               if (count >= 76) {
                  count = 0;
                  target[opos++] = 13;
                  target[opos++] = 10;
               }
            }
         }

         complete((byte[])target, opos, state, last, wrap, url);
         return target;
      }

      private static String encodeString(ByteBuffer source, boolean wrap, boolean url) {
         int remaining = source.remaining();
         int remainder = remaining % 3;
         int olimit = (remaining + (remainder == 0 ? 0 : 3 - remainder)) / 3 * 4;
         olimit += wrap ? olimit / 76 * 2 + 2 : 0;
         char[] target = new char[olimit];
         int opos = 0;
         int last = 0;
         int state = 0;
         int count = 0;
         byte[] ENCODING_TABLE = url ? FlexBase64.URL_ENCODING_TABLE : FlexBase64.STANDARD_ENCODING_TABLE;

         while(remaining > 0) {
            int b = source.get() & 255;
            target[opos++] = (char)ENCODING_TABLE[b >>> 2];
            last = (b & 3) << 4;
            --remaining;
            if (remaining <= 0) {
               state = 1;
               break;
            }

            b = source.get() & 255;
            target[opos++] = (char)ENCODING_TABLE[last | b >>> 4];
            last = (b & 15) << 2;
            --remaining;
            if (remaining <= 0) {
               state = 2;
               break;
            }

            b = source.get() & 255;
            target[opos++] = (char)ENCODING_TABLE[last | b >>> 6];
            target[opos++] = (char)ENCODING_TABLE[b & 63];
            --remaining;
            if (wrap) {
               count += 4;
               if (count >= 76) {
                  count = 0;
                  target[opos++] = '\r';
                  target[opos++] = '\n';
               }
            }
         }

         complete((char[])target, opos, state, last, wrap, url);

         try {
            if (FlexBase64.STRING_CONSTRUCTOR != null) {
               return (String)FlexBase64.STRING_CONSTRUCTOR.newInstance(target, Boolean.TRUE);
            }
         } catch (Exception var13) {
         }

         return new String(target);
      }

      public int getLastInputPosition() {
         return this.lastPos;
      }

      public int complete(byte[] target, int pos) {
         if (this.state > 0) {
            target[pos++] = this.encodingTable[this.last];

            for(int i = this.state; i < 3; ++i) {
               target[pos++] = 61;
            }

            this.last = this.state = 0;
         }

         if (this.wrap) {
            target[pos++] = 13;
            target[pos++] = 10;
         }

         return pos;
      }

      private static int complete(char[] target, int pos, int state, int last, boolean wrap, boolean url) {
         if (state > 0) {
            target[pos++] = (char)(url ? FlexBase64.URL_ENCODING_TABLE : FlexBase64.STANDARD_ENCODING_TABLE)[last];

            for(int i = state; i < 3; ++i) {
               target[pos++] = '=';
            }
         }

         if (wrap) {
            target[pos++] = '\r';
            target[pos++] = '\n';
         }

         return pos;
      }

      private static int complete(byte[] target, int pos, int state, int last, boolean wrap, boolean url) {
         if (state > 0) {
            target[pos++] = (url ? FlexBase64.URL_ENCODING_TABLE : FlexBase64.STANDARD_ENCODING_TABLE)[last];

            for(int i = state; i < 3; ++i) {
               target[pos++] = 61;
            }
         }

         if (wrap) {
            target[pos++] = 13;
            target[pos++] = 10;
         }

         return pos;
      }

      public void complete(ByteBuffer target) {
         if (this.state > 0) {
            target.put(this.encodingTable[this.last]);

            for(int i = this.state; i < 3; ++i) {
               target.put((byte)61);
            }

            this.last = this.state = 0;
         }

         if (this.wrap) {
            target.putShort((short)3338);
         }

         this.count = 0;
      }

      // $FF: synthetic method
      Encoder(boolean x0, boolean x1, Object x2) {
         this(x0, x1);
      }
   }
}
