package cn.hutool.extra.compress.extractor;

import cn.hutool.core.lang.Filter;
import java.io.Closeable;
import java.io.File;
import org.apache.commons.compress.archivers.ArchiveEntry;

public interface Extractor extends Closeable {
   default void extract(File targetDir) {
      this.extract(targetDir, (Filter)null);
   }

   void extract(File var1, Filter<ArchiveEntry> var2);

   void close();
}
