package cn.hutool.core.io.watch;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

@FunctionalInterface
public interface WatchAction {
   void doAction(WatchEvent<?> var1, Path var2);
}
