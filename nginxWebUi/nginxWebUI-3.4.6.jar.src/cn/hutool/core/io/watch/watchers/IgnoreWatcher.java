package cn.hutool.core.io.watch.watchers;

import cn.hutool.core.io.watch.Watcher;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

public class IgnoreWatcher implements Watcher {
  public void onCreate(WatchEvent<?> event, Path currentPath) {}
  
  public void onModify(WatchEvent<?> event, Path currentPath) {}
  
  public void onDelete(WatchEvent<?> event, Path currentPath) {}
  
  public void onOverflow(WatchEvent<?> event, Path currentPath) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\watch\watchers\IgnoreWatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */