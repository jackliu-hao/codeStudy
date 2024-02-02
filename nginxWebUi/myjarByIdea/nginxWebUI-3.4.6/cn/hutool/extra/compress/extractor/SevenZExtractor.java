package cn.hutool.extra.compress.extractor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;
import java.util.Iterator;
import java.util.RandomAccess;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

public class SevenZExtractor implements Extractor, RandomAccess {
   private final SevenZFile sevenZFile;

   public SevenZExtractor(File file) {
      this((File)file, (char[])null);
   }

   public SevenZExtractor(File file, char[] password) {
      try {
         this.sevenZFile = new SevenZFile(file, password);
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }
   }

   public SevenZExtractor(InputStream in) {
      this((InputStream)in, (char[])null);
   }

   public SevenZExtractor(InputStream in, char[] password) {
      this((SeekableByteChannel)(new SeekableInMemoryByteChannel(IoUtil.readBytes(in))), password);
   }

   public SevenZExtractor(SeekableByteChannel channel) {
      this((SeekableByteChannel)channel, (char[])null);
   }

   public SevenZExtractor(SeekableByteChannel channel, char[] password) {
      try {
         this.sevenZFile = new SevenZFile(channel, password);
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }
   }

   public void extract(File targetDir, Filter<ArchiveEntry> filter) {
      try {
         this.extractInternal(targetDir, filter);
      } catch (IOException var7) {
         throw new IORuntimeException(var7);
      } finally {
         this.close();
      }

   }

   public InputStream getFirst(Filter<ArchiveEntry> filter) {
      SevenZFile sevenZFile = this.sevenZFile;
      Iterator var3 = sevenZFile.getEntries().iterator();

      SevenZArchiveEntry entry;
      do {
         do {
            if (!var3.hasNext()) {
               return null;
            }

            entry = (SevenZArchiveEntry)var3.next();
         } while(null != filter && !filter.accept(entry));
      } while(entry.isDirectory());

      try {
         return sevenZFile.getInputStream(entry);
      } catch (IOException var6) {
         throw new IORuntimeException(var6);
      }
   }

   public InputStream get(String entryName) {
      return this.getFirst((entry) -> {
         return StrUtil.equals(entryName, entry.getName());
      });
   }

   private void extractInternal(File targetDir, Filter<ArchiveEntry> filter) throws IOException {
      Assert.isTrue(null != targetDir && (!targetDir.exists() || targetDir.isDirectory()), "target must be dir.");
      SevenZFile sevenZFile = this.sevenZFile;

      while(true) {
         SevenZArchiveEntry entry;
         do {
            if (null == (entry = this.sevenZFile.getNextEntry())) {
               return;
            }
         } while(null != filter && !filter.accept(entry));

         File outItemFile = FileUtil.file(targetDir, entry.getName());
         if (entry.isDirectory()) {
            outItemFile.mkdirs();
         } else if (entry.hasStream()) {
            FileUtil.writeFromStream(new Seven7EntryInputStream(sevenZFile, entry), (File)outItemFile);
         } else {
            FileUtil.touch(outItemFile);
         }
      }
   }

   public void close() {
      IoUtil.close(this.sevenZFile);
   }
}
