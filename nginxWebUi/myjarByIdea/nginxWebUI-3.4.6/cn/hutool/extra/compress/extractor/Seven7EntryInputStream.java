package cn.hutool.extra.compress.extractor;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

public class Seven7EntryInputStream extends InputStream {
   private final SevenZFile sevenZFile;
   private final long size;
   private long readSize = 0L;

   public Seven7EntryInputStream(SevenZFile sevenZFile, SevenZArchiveEntry entry) {
      this.sevenZFile = sevenZFile;
      this.size = entry.getSize();
   }

   public int available() throws IOException {
      try {
         return Math.toIntExact(this.size);
      } catch (ArithmeticException var2) {
         throw new IOException("Entry size is too large!(max than Integer.MAX)", var2);
      }
   }

   public long getReadSize() {
      return this.readSize;
   }

   public int read() throws IOException {
      ++this.readSize;
      return this.sevenZFile.read();
   }
}
