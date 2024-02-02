package org.xnio;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.ShortBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.wildfly.common.ref.CleanerReference;
import org.wildfly.common.ref.Reaper;
import org.wildfly.common.ref.Reference;
import org.xnio._private.Messages;

public final class Buffers {
   private static final byte[] NO_BYTES = new byte[0];
   public static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocate(0);
   public static final Pooled<ByteBuffer> EMPTY_POOLED_BYTE_BUFFER = emptyPooledByteBuffer();

   private Buffers() {
   }

   public static <T extends Buffer> T flip(T buffer) {
      buffer.flip();
      return buffer;
   }

   public static <T extends Buffer> T clear(T buffer) {
      buffer.clear();
      return buffer;
   }

   public static <T extends Buffer> T limit(T buffer, int limit) {
      buffer.limit(limit);
      return buffer;
   }

   public static <T extends Buffer> T mark(T buffer) {
      buffer.mark();
      return buffer;
   }

   public static <T extends Buffer> T position(T buffer, int position) {
      buffer.position(position);
      return buffer;
   }

   public static <T extends Buffer> T reset(T buffer) {
      buffer.reset();
      return buffer;
   }

   public static <T extends Buffer> T rewind(T buffer) {
      buffer.rewind();
      return buffer;
   }

   public static ByteBuffer slice(ByteBuffer buffer, int sliceSize) {
      int oldRem = buffer.remaining();
      if (sliceSize <= oldRem && sliceSize >= -oldRem) {
         int oldPos = buffer.position();
         int oldLim = buffer.limit();
         ByteBuffer var5;
         if (sliceSize < 0) {
            buffer.limit(oldLim + sliceSize);

            try {
               var5 = buffer.slice();
            } finally {
               buffer.limit(oldLim);
               buffer.position(oldLim + sliceSize);
            }

            return var5;
         } else {
            buffer.limit(oldPos + sliceSize);

            try {
               var5 = buffer.slice();
            } finally {
               buffer.limit(oldLim);
               buffer.position(oldPos + sliceSize);
            }

            return var5;
         }
      } else {
         throw Messages.msg.bufferUnderflow();
      }
   }

   public static ByteBuffer copy(ByteBuffer buffer, int count, BufferAllocator<ByteBuffer> allocator) {
      int oldRem = buffer.remaining();
      if (count <= oldRem && count >= -oldRem) {
         int oldPos = buffer.position();
         int oldLim = buffer.limit();
         ByteBuffer target;
         ByteBuffer var7;
         if (count < 0) {
            target = (ByteBuffer)allocator.allocate(-count);
            buffer.position(oldLim + count);

            try {
               target.put(buffer);
               var7 = target;
            } finally {
               buffer.limit(oldLim);
               buffer.position(oldLim + count);
            }

            return var7;
         } else {
            target = (ByteBuffer)allocator.allocate(count);
            buffer.limit(oldPos + count);

            try {
               target.put(buffer);
               var7 = target;
            } finally {
               buffer.limit(oldLim);
               buffer.position(oldPos + count);
            }

            return var7;
         }
      } else {
         throw Messages.msg.bufferUnderflow();
      }
   }

   public static int copy(ByteBuffer destination, ByteBuffer source) {
      int sr = source.remaining();
      int dr = destination.remaining();
      if (dr >= sr) {
         destination.put(source);
         return sr;
      } else {
         destination.put(slice(source, dr));
         return dr;
      }
   }

   public static int copy(ByteBuffer[] destinations, int offset, int length, ByteBuffer source) {
      int t = 0;

      for(int i = 0; i < length; ++i) {
         ByteBuffer buffer = destinations[i + offset];
         int rem = buffer.remaining();
         if (rem != 0) {
            if (rem >= source.remaining()) {
               t += source.remaining();
               buffer.put(source);
               return t;
            }

            buffer.put(slice(source, rem));
            t += rem;
         }
      }

      return t;
   }

   public static int copy(ByteBuffer destination, ByteBuffer[] sources, int offset, int length) {
      int t = 0;

      for(int i = 0; i < length; ++i) {
         ByteBuffer buffer = sources[i + offset];
         int rem = buffer.remaining();
         if (rem != 0) {
            if (rem > destination.remaining()) {
               t += destination.remaining();
               destination.put(slice(buffer, destination.remaining()));
               return t;
            }

            destination.put(buffer);
            t += rem;
         }
      }

      return t;
   }

   public static long copy(ByteBuffer[] destinations, int destOffset, int destLength, ByteBuffer[] sources, int srcOffset, int srcLength) {
      long t = 0L;
      int s = 0;
      int d = 0;
      if (destLength != 0 && srcLength != 0) {
         ByteBuffer var10000 = sources[srcOffset];
         var10000 = destinations[destOffset];

         while(s < srcLength && d < destLength) {
            ByteBuffer source = sources[srcOffset + s];
            ByteBuffer dest = destinations[destOffset + d];
            int sr = source.remaining();
            int dr = dest.remaining();
            if (sr < dr) {
               dest.put(source);
               ++s;
               t += (long)sr;
            } else if (sr > dr) {
               dest.put(slice(source, dr));
               ++d;
               t += (long)dr;
            } else {
               dest.put(source);
               ++s;
               ++d;
               t += (long)sr;
            }
         }

         return t;
      } else {
         return 0L;
      }
   }

   public static int copy(int count, ByteBuffer destination, ByteBuffer source) {
      int cnt = count >= 0 ? Math.min(Math.min(count, source.remaining()), destination.remaining()) : Math.max(Math.max(count, -source.remaining()), -destination.remaining());
      ByteBuffer copy = slice(source, cnt);
      destination.put(copy);
      return copy.position();
   }

   public static int copy(int count, ByteBuffer[] destinations, int offset, int length, ByteBuffer source) {
      if (source.remaining() > count) {
         int oldLimit = source.limit();
         if (count < 0) {
            throw Messages.msg.copyNegative();
         } else {
            int var6;
            try {
               source.limit(source.position() + count);
               var6 = copy(destinations, offset, length, source);
            } finally {
               source.limit(oldLimit);
            }

            return var6;
         }
      } else {
         return copy(destinations, offset, length, source);
      }
   }

   public static int copy(int count, ByteBuffer destination, ByteBuffer[] sources, int offset, int length) {
      if (destination.remaining() > count) {
         if (count < 0) {
            throw Messages.msg.copyNegative();
         } else {
            int oldLimit = destination.limit();

            int var6;
            try {
               destination.limit(destination.position() + Math.min(count, destination.remaining()));
               var6 = copy(destination, sources, offset, length);
            } finally {
               destination.limit(oldLimit);
            }

            return var6;
         }
      } else {
         return copy(destination, sources, offset, length);
      }
   }

   public static long copy(long count, ByteBuffer[] destinations, int destOffset, int destLength, ByteBuffer[] sources, int srcOffset, int srcLength) {
      long t = 0L;
      int s = 0;
      int d = 0;
      if (count < 0L) {
         throw Messages.msg.copyNegative();
      } else if (destLength != 0 && srcLength != 0 && count != 0L) {
         while(s < srcLength && d < destLength) {
            ByteBuffer source = sources[srcOffset + s];
            ByteBuffer dest = destinations[destOffset + d];
            int sr = source.remaining();
            int dr = (int)Math.min(count, (long)dest.remaining());
            if (sr < dr) {
               dest.put(source);
               ++s;
               t += (long)sr;
               count -= (long)sr;
            } else if (sr > dr) {
               dest.put(slice(source, dr));
               ++d;
               t += (long)dr;
               count -= (long)dr;
            } else {
               dest.put(source);
               ++s;
               ++d;
               t += (long)sr;
               count -= (long)sr;
            }
         }

         return t;
      } else {
         return 0L;
      }
   }

   public static ByteBuffer fill(ByteBuffer buffer, int value, int count) {
      if (count > buffer.remaining()) {
         throw Messages.msg.bufferUnderflow();
      } else {
         int i;
         if (buffer.hasArray()) {
            i = buffer.arrayOffset();
            Arrays.fill(buffer.array(), i + buffer.position(), i + buffer.limit(), (byte)value);
            skip(buffer, count);
         } else {
            for(i = count; i > 0; --i) {
               buffer.put((byte)value);
            }
         }

         return buffer;
      }
   }

   public static CharBuffer slice(CharBuffer buffer, int sliceSize) {
      if (sliceSize <= buffer.remaining() && sliceSize >= -buffer.remaining()) {
         int oldPos = buffer.position();
         int oldLim = buffer.limit();
         CharBuffer var4;
         if (sliceSize < 0) {
            buffer.limit(oldLim + sliceSize);

            try {
               var4 = buffer.slice();
            } finally {
               buffer.limit(oldLim);
               buffer.position(oldLim + sliceSize);
            }

            return var4;
         } else {
            buffer.limit(oldPos + sliceSize);

            try {
               var4 = buffer.slice();
            } finally {
               buffer.limit(oldLim);
               buffer.position(oldPos + sliceSize);
            }

            return var4;
         }
      } else {
         throw Messages.msg.bufferUnderflow();
      }
   }

   public static CharBuffer fill(CharBuffer buffer, int value, int count) {
      if (count > buffer.remaining()) {
         throw Messages.msg.bufferUnderflow();
      } else {
         int i;
         if (buffer.hasArray()) {
            i = buffer.arrayOffset();
            Arrays.fill(buffer.array(), i + buffer.position(), i + buffer.limit(), (char)value);
            skip(buffer, count);
         } else {
            for(i = count; i > 0; --i) {
               buffer.put((char)value);
            }
         }

         return buffer;
      }
   }

   public static ShortBuffer slice(ShortBuffer buffer, int sliceSize) {
      if (sliceSize <= buffer.remaining() && sliceSize >= -buffer.remaining()) {
         int oldPos = buffer.position();
         int oldLim = buffer.limit();
         ShortBuffer var4;
         if (sliceSize < 0) {
            buffer.limit(oldLim + sliceSize);

            try {
               var4 = buffer.slice();
            } finally {
               buffer.limit(oldLim);
               buffer.position(oldLim + sliceSize);
            }

            return var4;
         } else {
            buffer.limit(oldPos + sliceSize);

            try {
               var4 = buffer.slice();
            } finally {
               buffer.limit(oldLim);
               buffer.position(oldPos + sliceSize);
            }

            return var4;
         }
      } else {
         throw Messages.msg.bufferUnderflow();
      }
   }

   public static ShortBuffer fill(ShortBuffer buffer, int value, int count) {
      if (count > buffer.remaining()) {
         throw Messages.msg.bufferUnderflow();
      } else {
         int i;
         if (buffer.hasArray()) {
            i = buffer.arrayOffset();
            Arrays.fill(buffer.array(), i + buffer.position(), i + buffer.limit(), (short)value);
            skip(buffer, count);
         } else {
            for(i = count; i > 0; --i) {
               buffer.put((short)value);
            }
         }

         return buffer;
      }
   }

   public static IntBuffer slice(IntBuffer buffer, int sliceSize) {
      if (sliceSize <= buffer.remaining() && sliceSize >= -buffer.remaining()) {
         int oldPos = buffer.position();
         int oldLim = buffer.limit();
         IntBuffer var4;
         if (sliceSize < 0) {
            buffer.limit(oldLim + sliceSize);

            try {
               var4 = buffer.slice();
            } finally {
               buffer.limit(oldLim);
               buffer.position(oldLim + sliceSize);
            }

            return var4;
         } else {
            buffer.limit(oldPos + sliceSize);

            try {
               var4 = buffer.slice();
            } finally {
               buffer.limit(oldLim);
               buffer.position(oldPos + sliceSize);
            }

            return var4;
         }
      } else {
         throw Messages.msg.bufferUnderflow();
      }
   }

   public static IntBuffer fill(IntBuffer buffer, int value, int count) {
      if (count > buffer.remaining()) {
         throw Messages.msg.bufferUnderflow();
      } else {
         int i;
         if (buffer.hasArray()) {
            i = buffer.arrayOffset();
            Arrays.fill(buffer.array(), i + buffer.position(), i + buffer.limit(), value);
            skip(buffer, count);
         } else {
            for(i = count; i > 0; --i) {
               buffer.put(value);
            }
         }

         return buffer;
      }
   }

   public static LongBuffer slice(LongBuffer buffer, int sliceSize) {
      if (sliceSize <= buffer.remaining() && sliceSize >= -buffer.remaining()) {
         int oldPos = buffer.position();
         int oldLim = buffer.limit();
         LongBuffer var4;
         if (sliceSize < 0) {
            buffer.limit(oldLim + sliceSize);

            try {
               var4 = buffer.slice();
            } finally {
               buffer.limit(oldLim);
               buffer.position(oldLim + sliceSize);
            }

            return var4;
         } else {
            buffer.limit(oldPos + sliceSize);

            try {
               var4 = buffer.slice();
            } finally {
               buffer.limit(oldLim);
               buffer.position(oldPos + sliceSize);
            }

            return var4;
         }
      } else {
         throw Messages.msg.bufferUnderflow();
      }
   }

   public static LongBuffer fill(LongBuffer buffer, long value, int count) {
      if (count > buffer.remaining()) {
         throw Messages.msg.bufferUnderflow();
      } else {
         int i;
         if (buffer.hasArray()) {
            i = buffer.arrayOffset();
            Arrays.fill(buffer.array(), i + buffer.position(), i + buffer.limit(), value);
            skip(buffer, count);
         } else {
            for(i = count; i > 0; --i) {
               buffer.put(value);
            }
         }

         return buffer;
      }
   }

   public static <T extends Buffer> T skip(T buffer, int cnt) throws BufferUnderflowException {
      if (cnt < 0) {
         throw Messages.msg.parameterOutOfRange("cnt");
      } else if (cnt > buffer.remaining()) {
         throw Messages.msg.bufferUnderflow();
      } else {
         buffer.position(buffer.position() + cnt);
         return buffer;
      }
   }

   public static int trySkip(Buffer buffer, int cnt) {
      if (cnt < 0) {
         throw Messages.msg.parameterOutOfRange("cnt");
      } else {
         int rem = buffer.remaining();
         if (cnt > rem) {
            cnt = rem;
         }

         buffer.position(buffer.position() + cnt);
         return cnt;
      }
   }

   public static long trySkip(Buffer[] buffers, int offs, int len, long cnt) {
      if (cnt < 0L) {
         throw Messages.msg.parameterOutOfRange("cnt");
      } else if (len < 0) {
         throw Messages.msg.parameterOutOfRange("len");
      } else if (offs < 0) {
         throw Messages.msg.parameterOutOfRange("offs");
      } else if (offs > buffers.length) {
         throw Messages.msg.parameterOutOfRange("offs");
      } else if (offs + len > buffers.length) {
         throw Messages.msg.parameterOutOfRange("offs");
      } else {
         long c = 0L;

         for(int i = 0; i < len; ++i) {
            Buffer buffer = buffers[offs + i];
            int rem = buffer.remaining();
            if ((long)rem >= cnt) {
               buffer.position(buffer.position() + (int)cnt);
               return c + cnt;
            }

            buffer.position(buffer.position() + rem);
            cnt -= (long)rem;
            c += (long)rem;
         }

         return c;
      }
   }

   public static <T extends Buffer> T unget(T buffer, int cnt) {
      if (cnt < 0) {
         throw Messages.msg.parameterOutOfRange("cnt");
      } else if (cnt > buffer.position()) {
         throw Messages.msg.bufferUnderflow();
      } else {
         buffer.position(buffer.position() - cnt);
         return buffer;
      }
   }

   public static byte[] take(ByteBuffer buffer, int cnt) {
      if (cnt < 0) {
         throw Messages.msg.parameterOutOfRange("cnt");
      } else if (buffer.hasArray()) {
         int pos = buffer.position();
         int lim = buffer.limit();
         if (lim - pos < cnt) {
            throw new BufferUnderflowException();
         } else {
            byte[] array = buffer.array();
            int offset = buffer.arrayOffset();
            buffer.position(pos + cnt);
            int start = offset + pos;
            return Arrays.copyOfRange(array, start, start + cnt);
         }
      } else {
         byte[] bytes = new byte[cnt];
         buffer.get(bytes);
         return bytes;
      }
   }

   public static char[] take(CharBuffer buffer, int cnt) {
      if (cnt < 0) {
         throw Messages.msg.parameterOutOfRange("cnt");
      } else if (buffer.hasArray()) {
         int pos = buffer.position();
         int lim = buffer.limit();
         if (lim - pos < cnt) {
            throw new BufferUnderflowException();
         } else {
            char[] array = buffer.array();
            int offset = buffer.arrayOffset();
            buffer.position(pos + cnt);
            int start = offset + pos;
            return Arrays.copyOfRange(array, start, start + cnt);
         }
      } else {
         char[] chars = new char[cnt];
         buffer.get(chars);
         return chars;
      }
   }

   public static short[] take(ShortBuffer buffer, int cnt) {
      if (cnt < 0) {
         throw Messages.msg.parameterOutOfRange("cnt");
      } else if (buffer.hasArray()) {
         int pos = buffer.position();
         int lim = buffer.limit();
         if (lim - pos < cnt) {
            throw new BufferUnderflowException();
         } else {
            short[] array = buffer.array();
            int offset = buffer.arrayOffset();
            buffer.position(pos + cnt);
            int start = offset + pos;
            return Arrays.copyOfRange(array, start, start + cnt);
         }
      } else {
         short[] shorts = new short[cnt];
         buffer.get(shorts);
         return shorts;
      }
   }

   public static int[] take(IntBuffer buffer, int cnt) {
      if (cnt < 0) {
         throw Messages.msg.parameterOutOfRange("cnt");
      } else if (buffer.hasArray()) {
         int pos = buffer.position();
         int lim = buffer.limit();
         if (lim - pos < cnt) {
            throw new BufferUnderflowException();
         } else {
            int[] array = buffer.array();
            int offset = buffer.arrayOffset();
            buffer.position(pos + cnt);
            int start = offset + pos;
            return Arrays.copyOfRange(array, start, start + cnt);
         }
      } else {
         int[] ints = new int[cnt];
         buffer.get(ints);
         return ints;
      }
   }

   public static long[] take(LongBuffer buffer, int cnt) {
      if (cnt < 0) {
         throw Messages.msg.parameterOutOfRange("cnt");
      } else if (buffer.hasArray()) {
         int pos = buffer.position();
         int lim = buffer.limit();
         if (lim - pos < cnt) {
            throw new BufferUnderflowException();
         } else {
            long[] array = buffer.array();
            int offset = buffer.arrayOffset();
            buffer.position(pos + cnt);
            int start = offset + pos;
            return Arrays.copyOfRange(array, start, start + cnt);
         }
      } else {
         long[] longs = new long[cnt];
         buffer.get(longs);
         return longs;
      }
   }

   public static byte[] take(ByteBuffer buffer) {
      int remaining = buffer.remaining();
      if (remaining == 0) {
         return NO_BYTES;
      } else if (buffer.hasArray()) {
         int pos = buffer.position();
         int lim = buffer.limit();
         byte[] array = buffer.array();
         int offset = buffer.arrayOffset();
         buffer.position(lim);
         return Arrays.copyOfRange(array, offset + pos, offset + lim);
      } else {
         byte[] bytes = new byte[remaining];
         buffer.get(bytes);
         return bytes;
      }
   }

   public static byte[] take(ByteBuffer[] buffers, int offs, int len) {
      if (len == 1) {
         return take(buffers[offs]);
      } else {
         long remaining = remaining(buffers, offs, len);
         if (remaining == 0L) {
            return NO_BYTES;
         } else if (remaining > 2147483647L) {
            throw new OutOfMemoryError("Array too large");
         } else {
            byte[] bytes = new byte[(int)remaining];
            int o = 0;

            for(int i = 0; i < len; ++i) {
               ByteBuffer buffer = buffers[i + offs];
               int rem = buffer.remaining();
               buffer.get(bytes, o, rem);
               o += rem;
            }

            return bytes;
         }
      }
   }

   public static char[] take(CharBuffer buffer) {
      char[] chars = new char[buffer.remaining()];
      buffer.get(chars);
      return chars;
   }

   public static short[] take(ShortBuffer buffer) {
      short[] shorts = new short[buffer.remaining()];
      buffer.get(shorts);
      return shorts;
   }

   public static int[] take(IntBuffer buffer) {
      int[] ints = new int[buffer.remaining()];
      buffer.get(ints);
      return ints;
   }

   public static long[] take(LongBuffer buffer) {
      long[] longs = new long[buffer.remaining()];
      buffer.get(longs);
      return longs;
   }

   public static Object createDumper(final ByteBuffer buffer, final int indent, final int columns) {
      if (columns <= 0) {
         throw Messages.msg.parameterOutOfRange("columns");
      } else if (indent < 0) {
         throw Messages.msg.parameterOutOfRange("indent");
      } else {
         return new Object() {
            public String toString() {
               StringBuilder b = new StringBuilder();

               try {
                  Buffers.dump((ByteBuffer)buffer, b, indent, columns);
               } catch (IOException var3) {
               }

               return b.toString();
            }
         };
      }
   }

   public static void dump(ByteBuffer buffer, Appendable dest, int indent, int columns) throws IOException {
      if (columns <= 0) {
         throw Messages.msg.parameterOutOfRange("columns");
      } else if (indent < 0) {
         throw Messages.msg.parameterOutOfRange("indent");
      } else {
         int pos = buffer.position();
         int remaining = buffer.remaining();
         int rowLength = 8 << columns - 1;
         int n = Math.max(Integer.toString(buffer.remaining(), 16).length(), 4);

         for(int idx = 0; idx < remaining; idx += rowLength) {
            for(int i = 0; i < indent; ++i) {
               dest.append(' ');
            }

            String s = Integer.toString(idx, 16);

            for(int i = n - s.length(); i > 0; --i) {
               dest.append('0');
            }

            dest.append(s);
            dest.append(" - ");
            appendHexRow(buffer, dest, pos + idx, columns);
            appendTextRow(buffer, dest, pos + idx, columns);
            dest.append('\n');
         }

      }
   }

   private static void appendHexRow(ByteBuffer buffer, Appendable dest, int startPos, int columns) throws IOException {
      int limit = buffer.limit();
      int pos = startPos;

      for(int c = 0; c < columns; ++c) {
         for(int i = 0; i < 8; ++i) {
            if (pos >= limit) {
               dest.append("  ");
            } else {
               int v = buffer.get(pos++) & 255;
               String hexVal = Integer.toString(v, 16);
               if (v < 16) {
                  dest.append('0');
               }

               dest.append(hexVal);
            }

            dest.append(' ');
         }

         dest.append(' ');
         dest.append(' ');
      }

   }

   private static void appendTextRow(ByteBuffer buffer, Appendable dest, int startPos, int columns) throws IOException {
      int limit = buffer.limit();
      int pos = startPos;
      dest.append('[');
      dest.append(' ');

      for(int c = 0; c < columns; ++c) {
         for(int i = 0; i < 8; ++i) {
            if (pos >= limit) {
               dest.append(' ');
            } else {
               char v = (char)(buffer.get(pos++) & 255);
               if (Character.isISOControl(v)) {
                  dest.append('.');
               } else {
                  dest.append(v);
               }
            }
         }

         dest.append(' ');
      }

      dest.append(']');
   }

   public static Object createDumper(final CharBuffer buffer, final int indent, final int columns) {
      if (columns <= 0) {
         throw Messages.msg.parameterOutOfRange("columns");
      } else if (indent < 0) {
         throw Messages.msg.parameterOutOfRange("indent");
      } else {
         return new Object() {
            public String toString() {
               StringBuilder b = new StringBuilder();

               try {
                  Buffers.dump((CharBuffer)buffer, b, indent, columns);
               } catch (IOException var3) {
               }

               return b.toString();
            }
         };
      }
   }

   public static void dump(CharBuffer buffer, Appendable dest, int indent, int columns) throws IOException {
      if (columns <= 0) {
         throw Messages.msg.parameterOutOfRange("columns");
      } else if (indent < 0) {
         throw Messages.msg.parameterOutOfRange("indent");
      } else {
         int pos = buffer.position();
         int remaining = buffer.remaining();
         int rowLength = 8 << columns - 1;
         int n = Math.max(Integer.toString(buffer.remaining(), 16).length(), 4);

         for(int idx = 0; idx < remaining; idx += rowLength) {
            for(int i = 0; i < indent; ++i) {
               dest.append(' ');
            }

            String s = Integer.toString(idx, 16);

            for(int i = n - s.length(); i > 0; --i) {
               dest.append('0');
            }

            dest.append(s);
            dest.append(" - ");
            appendHexRow(buffer, dest, pos + idx, columns);
            appendTextRow(buffer, dest, pos + idx, columns);
            dest.append('\n');
         }

      }
   }

   private static void appendHexRow(CharBuffer buffer, Appendable dest, int startPos, int columns) throws IOException {
      int limit = buffer.limit();
      int pos = startPos;

      for(int c = 0; c < columns; ++c) {
         for(int i = 0; i < 8; ++i) {
            if (pos >= limit) {
               dest.append("  ");
            } else {
               char v = buffer.get(pos++);
               String hexVal = Integer.toString(v, 16);
               dest.append("0000".substring(hexVal.length()));
               dest.append(hexVal);
            }

            dest.append(' ');
         }

         dest.append(' ');
         dest.append(' ');
      }

   }

   private static void appendTextRow(CharBuffer buffer, Appendable dest, int startPos, int columns) throws IOException {
      int limit = buffer.limit();
      int pos = startPos;
      dest.append('[');
      dest.append(' ');

      for(int c = 0; c < columns; ++c) {
         for(int i = 0; i < 8; ++i) {
            if (pos >= limit) {
               dest.append(' ');
            } else {
               char v = buffer.get(pos++);
               if (!Character.isISOControl(v) && !Character.isHighSurrogate(v) && !Character.isLowSurrogate(v)) {
                  dest.append(v);
               } else {
                  dest.append('.');
               }
            }
         }

         dest.append(' ');
      }

      dest.append(']');
   }

   public static boolean hasRemaining(Buffer[] buffers, int offs, int len) {
      for(int i = 0; i < len; ++i) {
         if (buffers[i + offs].hasRemaining()) {
            return true;
         }
      }

      return false;
   }

   public static boolean hasRemaining(Buffer[] buffers) {
      return hasRemaining(buffers, 0, buffers.length);
   }

   public static long remaining(Buffer[] buffers, int offs, int len) {
      long t = 0L;

      for(int i = 0; i < len; ++i) {
         t += (long)buffers[i + offs].remaining();
      }

      return t;
   }

   public static long remaining(Buffer[] buffers) {
      return remaining(buffers, 0, buffers.length);
   }

   public static ByteBuffer putModifiedUtf8(ByteBuffer dest, String orig) throws BufferOverflowException {
      char[] chars = orig.toCharArray();
      char[] var3 = chars;
      int var4 = chars.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         char c = var3[var5];
         if (c > 0 && c <= 127) {
            dest.put((byte)c);
         } else if (c <= 2047) {
            dest.put((byte)(192 | 31 & c >> 6));
            dest.put((byte)(128 | 63 & c));
         } else {
            dest.put((byte)(224 | 15 & c >> 12));
            dest.put((byte)(128 | 63 & c >> 6));
            dest.put((byte)(128 | 63 & c));
         }
      }

      return dest;
   }

   public static String getModifiedUtf8Z(ByteBuffer src) throws BufferUnderflowException {
      StringBuilder builder = new StringBuilder();

      while(true) {
         int ch = readUTFChar(src);
         if (ch == -1) {
            return builder.toString();
         }

         builder.append((char)ch);
      }
   }

   public static String getModifiedUtf8(ByteBuffer src) throws BufferUnderflowException {
      StringBuilder builder = new StringBuilder();

      while(src.hasRemaining()) {
         int ch = readUTFChar(src);
         if (ch == -1) {
            builder.append('\u0000');
         } else {
            builder.append((char)ch);
         }
      }

      return builder.toString();
   }

   private static int readUTFChar(ByteBuffer src) throws BufferUnderflowException {
      int a = src.get() & 255;
      if (a == 0) {
         return -1;
      } else if (a < 128) {
         return (char)a;
      } else if (a < 192) {
         return 63;
      } else {
         int b;
         if (a < 224) {
            b = src.get() & 255;
            return (b & 192) != 128 ? 63 : (a & 31) << 6 | b & 63;
         } else if (a < 240) {
            b = src.get() & 255;
            if ((b & 192) != 128) {
               return 63;
            } else {
               int c = src.get() & 255;
               return (c & 192) != 128 ? 63 : (a & 15) << 12 | (b & 63) << 6 | c & 63;
            }
         } else {
            return 63;
         }
      }
   }

   public static boolean readAsciiZ(ByteBuffer src, StringBuilder builder) {
      return readAsciiZ(src, builder, '?');
   }

   public static boolean readAsciiZ(ByteBuffer src, StringBuilder builder, char replacement) {
      while(src.hasRemaining()) {
         byte b = src.get();
         if (b == 0) {
            return true;
         }

         builder.append(b < 0 ? replacement : (char)b);
      }

      return false;
   }

   public static boolean readAsciiLine(ByteBuffer src, StringBuilder builder) {
      return readAsciiLine(src, builder, '?', '\n');
   }

   public static boolean readAsciiLine(ByteBuffer src, StringBuilder builder, char replacement) {
      return readAsciiLine(src, builder, replacement, '\n');
   }

   public static boolean readAsciiLine(ByteBuffer src, StringBuilder builder, char replacement, char delimiter) {
      byte b;
      do {
         if (!src.hasRemaining()) {
            return false;
         }

         b = src.get();
         builder.append(b < 0 ? replacement : (char)b);
      } while(b != delimiter);

      return true;
   }

   public static void readAscii(ByteBuffer src, StringBuilder builder) {
      readAscii(src, builder, '?');
   }

   public static void readAscii(ByteBuffer src, StringBuilder builder, char replacement) {
      while(src.hasRemaining()) {
         byte b = src.get();
         builder.append(b < 0 ? replacement : (char)b);
      }

   }

   public static void readAscii(ByteBuffer src, StringBuilder builder, int limit, char replacement) {
      while(limit > 0) {
         if (!src.hasRemaining()) {
            return;
         }

         byte b = src.get();
         builder.append(b < 0 ? replacement : (char)b);
         --limit;
      }

   }

   public static boolean readLatin1Z(ByteBuffer src, StringBuilder builder) {
      while(src.hasRemaining()) {
         byte b = src.get();
         if (b == 0) {
            return true;
         }

         builder.append((char)(b & 255));
      }

      return false;
   }

   public static boolean readLatin1Line(ByteBuffer src, StringBuilder builder) {
      byte b;
      do {
         if (!src.hasRemaining()) {
            return false;
         }

         b = src.get();
         builder.append((char)(b & 255));
      } while(b != 10);

      return true;
   }

   public static boolean readLatin1Line(ByteBuffer src, StringBuilder builder, char delimiter) {
      byte b;
      do {
         if (!src.hasRemaining()) {
            return false;
         }

         b = src.get();
         builder.append((char)(b & 255));
      } while(b != delimiter);

      return true;
   }

   public static void readLatin1(ByteBuffer src, StringBuilder builder) {
      while(src.hasRemaining()) {
         byte b = src.get();
         builder.append((char)(b & 255));
      }

   }

   public static boolean readModifiedUtf8Z(ByteBuffer src, StringBuilder builder) {
      return readModifiedUtf8Z(src, builder, '?');
   }

   public static boolean readModifiedUtf8Z(ByteBuffer src, StringBuilder builder, char replacement) {
      while(src.hasRemaining()) {
         int a = src.get() & 255;
         if (a == 0) {
            return true;
         }

         if (a < 128) {
            builder.append((char)a);
         } else if (a < 192) {
            builder.append(replacement);
         } else {
            int b;
            if (a < 224) {
               if (!src.hasRemaining()) {
                  unget(src, 1);
                  return false;
               }

               b = src.get() & 255;
               if ((b & 192) != 128) {
                  builder.append(replacement);
               } else {
                  builder.append((char)((a & 31) << 6 | b & 63));
               }
            } else if (a < 240) {
               if (src.hasRemaining()) {
                  b = src.get() & 255;
                  if ((b & 192) != 128) {
                     builder.append(replacement);
                     continue;
                  }

                  if (src.hasRemaining()) {
                     int c = src.get() & 255;
                     if ((c & 192) != 128) {
                        builder.append(replacement);
                        continue;
                     }

                     builder.append((char)((a & 15) << 12 | (b & 63) << 6 | c & 63));
                     continue;
                  }

                  unget(src, 2);
                  return false;
               }

               unget(src, 1);
               return false;
            } else {
               builder.append(replacement);
            }
         }
      }

      return false;
   }

   public static boolean readModifiedUtf8Line(ByteBuffer src, StringBuilder builder) {
      return readModifiedUtf8Line(src, builder, '?');
   }

   public static boolean readModifiedUtf8Line(ByteBuffer src, StringBuilder builder, char replacement) {
      return readModifiedUtf8Line(src, builder, replacement, '\n');
   }

   public static boolean readModifiedUtf8Line(ByteBuffer src, StringBuilder builder, char replacement, char delimiter) {
      while(src.hasRemaining()) {
         int a = src.get() & 255;
         if (a < 128) {
            builder.append((char)a);
            if (a == delimiter) {
               return true;
            }
         } else if (a < 192) {
            builder.append(replacement);
         } else {
            int b;
            if (a < 224) {
               if (src.hasRemaining()) {
                  b = src.get() & 255;
                  if ((b & 192) != 128) {
                     builder.append(replacement);
                     continue;
                  }

                  char ch = (char)((a & 31) << 6 | b & 63);
                  builder.append(ch);
                  if (ch != delimiter) {
                     continue;
                  }

                  return true;
               }

               unget(src, 1);
               return false;
            } else if (a < 240) {
               if (src.hasRemaining()) {
                  b = src.get() & 255;
                  if ((b & 192) != 128) {
                     builder.append(replacement);
                     continue;
                  }

                  if (src.hasRemaining()) {
                     int c = src.get() & 255;
                     if ((c & 192) != 128) {
                        builder.append(replacement);
                        continue;
                     }

                     char ch = (char)((a & 15) << 12 | (b & 63) << 6 | c & 63);
                     builder.append(ch);
                     if (ch != delimiter) {
                        continue;
                     }

                     return true;
                  }

                  unget(src, 2);
                  return false;
               }

               unget(src, 1);
               return false;
            } else {
               builder.append(replacement);
            }
         }
      }

      return false;
   }

   public static boolean readLine(ByteBuffer src, StringBuilder builder, CharsetDecoder decoder) {
      return readLine(src, builder, decoder, '\n');
   }

   public static boolean readLine(ByteBuffer src, StringBuilder builder, CharsetDecoder decoder, char delimiter) {
      CharBuffer oneChar = CharBuffer.allocate(1);

      while(true) {
         CoderResult coderResult = decoder.decode(src, oneChar, false);
         if (coderResult.isUnderflow()) {
            if (oneChar.hasRemaining()) {
               return false;
            }
         } else if (oneChar.hasRemaining()) {
            throw new IllegalStateException();
         }

         char ch = oneChar.get(0);
         builder.append(ch);
         if (ch == delimiter) {
            return true;
         }

         oneChar.clear();
      }
   }

   public static <B extends Buffer> Pooled<B> pooledWrapper(final B buffer) {
      return new Pooled<B>() {
         private volatile B buf = buffer;

         public void discard() {
            this.buf = null;
         }

         public void free() {
            this.buf = null;
         }

         public B getResource() throws IllegalStateException {
            B bufferx = this.buf;
            if (bufferx == null) {
               throw new IllegalStateException();
            } else {
               return bufferx;
            }
         }

         public void close() {
            this.free();
         }

         public String toString() {
            return "Pooled wrapper around " + buffer;
         }
      };
   }

   public static Pooled<ByteBuffer> globalPooledWrapper(final ByteBuffer buffer) {
      return new Pooled<ByteBuffer>() {
         private volatile ByteBuffer buf = buffer;

         public void discard() {
            ByteBuffer oldBuf = this.buf;
            if (oldBuf != null) {
               final ByteBuffer buf = oldBuf.duplicate();
               new CleanerReference(this.buf, (Object)null, new Reaper<ByteBuffer, Void>() {
                  public void reap(Reference<ByteBuffer, Void> reference) {
                     ByteBufferPool.free(buf);
                  }
               });
               this.buf = null;
            }
         }

         public void free() {
            ByteBuffer oldBuf = this.buf;
            if (oldBuf != null) {
               ByteBufferPool.free(oldBuf);
               this.buf = null;
            }
         }

         public ByteBuffer getResource() throws IllegalStateException {
            ByteBuffer bufferx = this.buf;
            if (bufferx == null) {
               throw new IllegalStateException();
            } else {
               return bufferx;
            }
         }

         public void close() {
            this.free();
         }

         public String toString() {
            return "Globally pooled wrapper around " + buffer;
         }
      };
   }

   public static Pooled<ByteBuffer> emptyPooledByteBuffer() {
      return new Pooled<ByteBuffer>() {
         public void discard() {
         }

         public void free() {
         }

         public ByteBuffer getResource() throws IllegalStateException {
            return Buffers.EMPTY_BYTE_BUFFER;
         }

         public void close() {
         }
      };
   }

   public static BufferAllocator<ByteBuffer> sliceAllocator(final ByteBuffer buffer) {
      return new BufferAllocator<ByteBuffer>() {
         public ByteBuffer allocate(int size) throws IllegalArgumentException {
            return Buffers.slice(buffer, size);
         }
      };
   }

   public static <B extends Buffer> Pool<B> allocatedBufferPool(final BufferAllocator<B> allocator, final int size) {
      return new Pool<B>() {
         public Pooled<B> allocate() {
            return Buffers.pooledWrapper(allocator.allocate(size));
         }
      };
   }

   public static Pool<ByteBuffer> secureBufferPool(Pool<ByteBuffer> delegate) {
      return new SecureByteBufferPool(delegate);
   }

   public static boolean isSecureBufferPool(Pool<?> pool) {
      return pool instanceof SecureByteBufferPool;
   }

   public static void zero(ByteBuffer buffer) {
      buffer.clear();

      while(buffer.remaining() >= 8) {
         buffer.putLong(0L);
      }

      while(buffer.hasRemaining()) {
         buffer.put((byte)0);
      }

      buffer.clear();
   }

   public static void zero(CharBuffer buffer) {
      buffer.clear();

      while(buffer.remaining() >= 32) {
         buffer.put("\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000");
      }

      while(buffer.hasRemaining()) {
         buffer.put('\u0000');
      }

      buffer.clear();
   }

   public static boolean isDirect(Buffer... buffers) throws IllegalArgumentException {
      return isDirect(buffers, 0, buffers.length);
   }

   public static boolean isDirect(Buffer[] buffers, int offset, int length) {
      boolean foundDirect = false;
      boolean foundHeap = false;

      for(int i = 0; i < length; ++i) {
         Buffer buffer = buffers[i + offset];
         if (buffer == null) {
            throw Messages.msg.nullParameter("buffer");
         }

         if (buffer.isDirect()) {
            if (foundHeap) {
               throw Messages.msg.mixedDirectAndHeap();
            }

            foundDirect = true;
         } else {
            if (foundDirect) {
               throw Messages.msg.mixedDirectAndHeap();
            }

            foundHeap = true;
         }
      }

      return foundDirect;
   }

   public static void assertWritable(Buffer[] buffers, int offs, int len) throws ReadOnlyBufferException {
      for(int i = 0; i < len; ++i) {
         if (buffers[i + offs].isReadOnly()) {
            throw Messages.msg.readOnlyBuffer();
         }
      }

   }

   public static void assertWritable(Buffer... buffers) throws ReadOnlyBufferException {
      assertWritable(buffers, 0, buffers.length);
   }

   public static void addRandom(ByteBuffer target, Random random, int count) {
      byte[] bytes = new byte[count];
      random.nextBytes(bytes);
      target.put(bytes);
   }

   public static void addRandom(ByteBuffer target, int count) {
      addRandom(target, IoUtils.getThreadLocalRandom(), count);
   }

   public static void addRandom(ByteBuffer target, Random random) {
      if (target.remaining() != 0) {
         addRandom(target, random, random.nextInt(target.remaining()));
      }
   }

   public static void addRandom(ByteBuffer target) {
      addRandom(target, IoUtils.getThreadLocalRandom());
   }

   public static int fillFromStream(ByteBuffer target, InputStream source) throws IOException {
      int remaining = target.remaining();
      if (remaining == 0) {
         return 0;
      } else {
         int p = target.position();
         if (target.hasArray()) {
            int res;
            try {
               res = source.read(target.array(), p + target.arrayOffset(), remaining);
            } catch (InterruptedIOException var8) {
               target.position(p + var8.bytesTransferred);
               throw var8;
            }

            if (res > 0) {
               target.position(p + res);
            }

            return res;
         } else {
            byte[] tmp = new byte[remaining];

            int res;
            try {
               res = source.read(tmp);
            } catch (InterruptedIOException var9) {
               int n = var9.bytesTransferred;
               target.put(tmp, 0, n);
               target.position(p + n);
               throw var9;
            }

            if (res > 0) {
               target.put(tmp, 0, res);
            }

            return res;
         }
      }
   }

   public static String debugString(ByteBuffer buffer) {
      StringBuilder b = new StringBuilder();
      b.append("1 buffer of ").append(buffer.remaining()).append(" bytes");
      return b.toString();
   }

   public static String debugString(ByteBuffer[] buffers, int offs, int len) {
      StringBuilder b = new StringBuilder();
      b.append(len).append(" buffer(s)");
      if (len > 0) {
         b.append(" of ").append(remaining(buffers, offs, len)).append(" bytes");
      }

      return b.toString();
   }

   public static void emptyToStream(OutputStream target, ByteBuffer source) throws IOException {
      int remaining = source.remaining();
      if (remaining != 0) {
         int p = source.position();
         if (source.hasArray()) {
            try {
               target.write(source.array(), p + source.arrayOffset(), remaining);
            } catch (InterruptedIOException var6) {
               source.position(p + var6.bytesTransferred);
               throw var6;
            }

            source.position(source.limit());
         } else {
            byte[] tmp = take(source);

            try {
               target.write(tmp);
            } catch (InterruptedIOException var7) {
               source.position(p + var7.bytesTransferred);
               throw var7;
            } catch (IOException var8) {
               source.position(p);
               throw var8;
            }
         }
      }
   }

   private static class SecurePooledByteBuffer implements Pooled<ByteBuffer> {
      private static final AtomicIntegerFieldUpdater<SecurePooledByteBuffer> freedUpdater = AtomicIntegerFieldUpdater.newUpdater(SecurePooledByteBuffer.class, "freed");
      private final Pooled<ByteBuffer> allocated;
      private volatile int freed;

      SecurePooledByteBuffer(Pooled<ByteBuffer> allocated) {
         this.allocated = allocated;
      }

      public void discard() {
         if (freedUpdater.compareAndSet(this, 0, 1)) {
            Buffers.zero((ByteBuffer)this.allocated.getResource());
            this.allocated.discard();
         }

      }

      public void free() {
         if (freedUpdater.compareAndSet(this, 0, 1)) {
            Buffers.zero((ByteBuffer)this.allocated.getResource());
            this.allocated.free();
         }

      }

      public ByteBuffer getResource() throws IllegalStateException {
         return (ByteBuffer)this.allocated.getResource();
      }

      public void close() {
         this.free();
      }

      public String toString() {
         return "Secure wrapper around " + this.allocated;
      }
   }

   private static class SecureByteBufferPool implements Pool<ByteBuffer> {
      private final Pool<ByteBuffer> delegate;

      SecureByteBufferPool(Pool<ByteBuffer> delegate) {
         this.delegate = delegate;
      }

      public Pooled<ByteBuffer> allocate() {
         return new SecurePooledByteBuffer(this.delegate.allocate());
      }
   }
}
