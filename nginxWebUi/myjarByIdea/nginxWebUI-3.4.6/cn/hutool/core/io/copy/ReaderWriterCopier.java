package cn.hutool.core.io.copy;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.Assert;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class ReaderWriterCopier extends IoCopier<Reader, Writer> {
   public ReaderWriterCopier() {
      this(8192);
   }

   public ReaderWriterCopier(int bufferSize) {
      this(bufferSize, -1L);
   }

   public ReaderWriterCopier(int bufferSize, long count) {
      this(bufferSize, count, (StreamProgress)null);
   }

   public ReaderWriterCopier(int bufferSize, long count, StreamProgress progress) {
      super(bufferSize, count, progress);
   }

   public long copy(Reader source, Writer target) {
      Assert.notNull(source, "InputStream is null !");
      Assert.notNull(target, "OutputStream is null !");
      StreamProgress progress = this.progress;
      if (null != progress) {
         progress.start();
      }

      long size;
      try {
         size = this.doCopy(source, target, new char[this.bufferSize(this.count)], progress);
         target.flush();
      } catch (IOException var7) {
         throw new IORuntimeException(var7);
      }

      if (null != progress) {
         progress.finish();
      }

      return size;
   }

   private long doCopy(Reader source, Writer target, char[] buffer, StreamProgress progress) throws IOException {
      long numToRead = this.count > 0L ? this.count : Long.MAX_VALUE;
      long total = 0L;

      while(numToRead > 0L) {
         int read = source.read(buffer, 0, this.bufferSize(numToRead));
         if (read < 0) {
            break;
         }

         target.write(buffer, 0, read);
         if (this.flushEveryBuffer) {
            target.flush();
         }

         numToRead -= (long)read;
         total += (long)read;
         if (null != progress) {
            progress.progress(this.count, total);
         }
      }

      return total;
   }
}
