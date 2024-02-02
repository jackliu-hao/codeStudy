package org.h2.store;

import org.h2.message.DbException;
import org.h2.util.SmallLRUCache;
import org.h2.util.TempFileDeleter;
import org.h2.value.CompareMode;

public interface DataHandler {
   String getDatabasePath();

   FileStore openFile(String var1, String var2, boolean var3);

   void checkPowerOff() throws DbException;

   void checkWritingAllowed() throws DbException;

   int getMaxLengthInplaceLob();

   TempFileDeleter getTempFileDeleter();

   Object getLobSyncObject();

   SmallLRUCache<String, String[]> getLobFileListCache();

   LobStorageInterface getLobStorage();

   int readLob(long var1, byte[] var3, long var4, byte[] var6, int var7, int var8);

   CompareMode getCompareMode();
}
