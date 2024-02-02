package oshi.software.os;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface FileSystem {
   List<OSFileStore> getFileStores();

   List<OSFileStore> getFileStores(boolean var1);

   long getOpenFileDescriptors();

   long getMaxFileDescriptors();
}
