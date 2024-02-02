package org.xnio;

import java.util.Collection;

public interface FileChangeCallback {
   void handleChanges(Collection<FileChangeEvent> var1);
}
