package org.xnio;

import java.io.Closeable;
import java.io.File;

public interface FileSystemWatcher extends Closeable {
   void watchPath(File var1, FileChangeCallback var2);

   void unwatchPath(File var1, FileChangeCallback var2);
}
