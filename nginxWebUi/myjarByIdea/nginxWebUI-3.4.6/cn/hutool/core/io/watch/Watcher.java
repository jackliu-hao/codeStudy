package cn.hutool.core.io.watch;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public interface Watcher {
   void onCreate(WatchEvent<?> var1, Path var2);

   void onModify(WatchEvent<?> var1, Path var2);

   void onDelete(WatchEvent<?> var1, Path var2);

   void onOverflow(WatchEvent<?> var1, Path var2);
}
