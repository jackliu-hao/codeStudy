package cn.hutool.extra.compress.archiver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

public class SevenZArchiver implements Archiver {
   private final SevenZOutputFile sevenZOutputFile;
   private SeekableByteChannel channel;
   private OutputStream out;

   public SevenZArchiver(File file) {
      try {
         this.sevenZOutputFile = new SevenZOutputFile(file);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public SevenZArchiver(OutputStream out) {
      this.out = out;
      this.channel = new SeekableInMemoryByteChannel();

      try {
         this.sevenZOutputFile = new SevenZOutputFile(this.channel);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public SevenZArchiver(SeekableByteChannel channel) {
      try {
         this.sevenZOutputFile = new SevenZOutputFile(channel);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public SevenZOutputFile getSevenZOutputFile() {
      return this.sevenZOutputFile;
   }

   public SevenZArchiver add(File file, String path, Filter<File> filter) {
      try {
         this.addInternal(file, path, filter);
         return this;
      } catch (IOException var5) {
         throw new IORuntimeException(var5);
      }
   }

   public SevenZArchiver finish() {
      try {
         this.sevenZOutputFile.finish();
         return this;
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public void close() {
      try {
         this.finish();
      } catch (Exception var3) {
      }

      if (null != this.out && this.channel instanceof SeekableInMemoryByteChannel) {
         try {
            this.out.write(((SeekableInMemoryByteChannel)this.channel).array());
         } catch (IOException var2) {
            throw new IORuntimeException(var2);
         }
      }

      IoUtil.close(this.sevenZOutputFile);
   }

   private void addInternal(File file, String path, Filter<File> filter) throws IOException {
      if (null == filter || filter.accept(file)) {
         SevenZOutputFile out = this.sevenZOutputFile;
         String entryName;
         if (StrUtil.isNotEmpty(path)) {
            entryName = StrUtil.addSuffixIfNot(path, "/") + file.getName();
         } else {
            entryName = file.getName();
         }

         out.putArchiveEntry(out.createArchiveEntry(file, entryName));
         if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (ArrayUtil.isNotEmpty((Object[])files)) {
               File[] var7 = files;
               int var8 = files.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  File childFile = var7[var9];
                  this.addInternal(childFile, entryName, filter);
               }
            }
         } else {
            if (file.isFile()) {
               out.write(FileUtil.readBytes(file));
            }

            out.closeArchiveEntry();
         }

      }
   }
}
