package org.xnio;

import java.io.Closeable;
import java.io.File;

public interface FileSystemWatcher extends Closeable {
  void watchPath(File paramFile, FileChangeCallback paramFileChangeCallback);
  
  void unwatchPath(File paramFile, FileChangeCallback paramFileChangeCallback);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\FileSystemWatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */