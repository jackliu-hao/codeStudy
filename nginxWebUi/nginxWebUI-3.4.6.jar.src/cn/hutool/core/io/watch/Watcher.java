package cn.hutool.core.io.watch;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public interface Watcher {
  void onCreate(WatchEvent<?> paramWatchEvent, Path paramPath);
  
  void onModify(WatchEvent<?> paramWatchEvent, Path paramPath);
  
  void onDelete(WatchEvent<?> paramWatchEvent, Path paramPath);
  
  void onOverflow(WatchEvent<?> paramWatchEvent, Path paramPath);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\watch\Watcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */