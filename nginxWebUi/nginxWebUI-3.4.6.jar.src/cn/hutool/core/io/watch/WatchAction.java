package cn.hutool.core.io.watch;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

@FunctionalInterface
public interface WatchAction {
  void doAction(WatchEvent<?> paramWatchEvent, Path paramPath);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\watch\WatchAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */