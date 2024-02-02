package cn.hutool.extra.compress.archiver;

import cn.hutool.core.lang.Filter;
import java.io.Closeable;
import java.io.File;

public interface Archiver extends Closeable {
   default Archiver add(File file) {
      return this.add(file, (Filter)null);
   }

   default Archiver add(File file, Filter<File> filter) {
      return this.add(file, "/", filter);
   }

   Archiver add(File var1, String var2, Filter<File> var3);

   Archiver finish();

   void close();
}
