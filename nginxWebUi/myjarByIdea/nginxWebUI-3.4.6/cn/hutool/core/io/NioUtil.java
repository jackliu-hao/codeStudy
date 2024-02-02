package cn.hutool.core.io;

import cn.hutool.core.io.copy.ChannelCopier;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;

public class NioUtil {
   public static final int DEFAULT_BUFFER_SIZE = 8192;
   public static final int DEFAULT_MIDDLE_BUFFER_SIZE = 16384;
   public static final int DEFAULT_LARGE_BUFFER_SIZE = 32768;
   public static final int EOF = -1;

   public static long copyByNIO(InputStream in, OutputStream out, int bufferSize, StreamProgress streamProgress) throws IORuntimeException {
      return copyByNIO(in, out, bufferSize, -1L, streamProgress);
   }

   public static long copyByNIO(InputStream in, OutputStream out, int bufferSize, long count, StreamProgress streamProgress) throws IORuntimeException {
      return copy(Channels.newChannel(in), Channels.newChannel(out), bufferSize, count, streamProgress);
   }

   public static long copy(FileChannel inChannel, FileChannel outChannel) throws IORuntimeException {
      Assert.notNull(inChannel, "In channel is null!");
      Assert.notNull(outChannel, "Out channel is null!");

      try {
         return copySafely(inChannel, outChannel);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   private static long copySafely(FileChannel inChannel, FileChannel outChannel) throws IOException {
      long totalBytes = inChannel.size();
      long pos = 0L;

      long writeBytes;
      for(long remaining = totalBytes; remaining > 0L; remaining -= writeBytes) {
         writeBytes = inChannel.transferTo(pos, remaining, outChannel);
         pos += writeBytes;
      }

      return totalBytes;
   }

   public static long copy(ReadableByteChannel in, WritableByteChannel out) throws IORuntimeException {
      return copy(in, out, 8192);
   }

   public static long copy(ReadableByteChannel in, WritableByteChannel out, int bufferSize) throws IORuntimeException {
      return copy(in, out, bufferSize, (StreamProgress)null);
   }

   public static long copy(ReadableByteChannel in, WritableByteChannel out, int bufferSize, StreamProgress streamProgress) throws IORuntimeException {
      return copy(in, out, bufferSize, -1L, streamProgress);
   }

   public static long copy(ReadableByteChannel in, WritableByteChannel out, int bufferSize, long count, StreamProgress streamProgress) throws IORuntimeException {
      return (new ChannelCopier(bufferSize, count, streamProgress)).copy(in, out);
   }

   public static String read(ReadableByteChannel channel, Charset charset) throws IORuntimeException {
      FastByteArrayOutputStream out = read(channel);
      return null == charset ? out.toString() : out.toString(charset);
   }

   public static FastByteArrayOutputStream read(ReadableByteChannel channel) throws IORuntimeException {
      FastByteArrayOutputStream out = new FastByteArrayOutputStream();
      copy(channel, Channels.newChannel(out));
      return out;
   }

   public static String readUtf8(FileChannel fileChannel) throws IORuntimeException {
      return read(fileChannel, CharsetUtil.CHARSET_UTF_8);
   }

   public static String read(FileChannel fileChannel, String charsetName) throws IORuntimeException {
      return read(fileChannel, CharsetUtil.charset(charsetName));
   }

   public static String read(FileChannel fileChannel, Charset charset) throws IORuntimeException {
      MappedByteBuffer buffer;
      try {
         buffer = fileChannel.map(MapMode.READ_ONLY, 0L, fileChannel.size()).load();
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }

      return StrUtil.str((ByteBuffer)buffer, (Charset)charset);
   }

   public static void close(AutoCloseable closeable) {
      if (null != closeable) {
         try {
            closeable.close();
         } catch (Exception var2) {
         }
      }

   }
}
