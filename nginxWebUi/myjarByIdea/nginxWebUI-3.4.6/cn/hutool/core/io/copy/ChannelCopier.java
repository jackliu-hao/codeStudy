package cn.hutool.core.io.copy;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.Assert;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class ChannelCopier extends IoCopier<ReadableByteChannel, WritableByteChannel> {
   public ChannelCopier() {
      this(8192);
   }

   public ChannelCopier(int bufferSize) {
      this(bufferSize, -1L);
   }

   public ChannelCopier(int bufferSize, long count) {
      this(bufferSize, count, (StreamProgress)null);
   }

   public ChannelCopier(int bufferSize, long count, StreamProgress progress) {
      super(bufferSize, count, progress);
   }

   public long copy(ReadableByteChannel source, WritableByteChannel target) {
      Assert.notNull(source, "InputStream is null !");
      Assert.notNull(target, "OutputStream is null !");
      StreamProgress progress = this.progress;
      if (null != progress) {
         progress.start();
      }

      long size;
      try {
         size = this.doCopy(source, target, ByteBuffer.allocate(this.bufferSize(this.count)), progress);
      } catch (IOException var7) {
         throw new IORuntimeException(var7);
      }

      if (null != progress) {
         progress.finish();
      }

      return size;
   }

   private long doCopy(ReadableByteChannel source, WritableByteChannel target, ByteBuffer buffer, StreamProgress progress) throws IOException {
      long numToRead = this.count > 0L ? this.count : Long.MAX_VALUE;
      long total = 0L;

      while(numToRead > 0L) {
         int read = source.read(buffer);
         if (read < 0) {
            break;
         }

         buffer.flip();
         target.write(buffer);
         buffer.clear();
         numToRead -= (long)read;
         total += (long)read;
         if (null != progress) {
            progress.progress(this.count, total);
         }
      }

      return total;
   }
}
