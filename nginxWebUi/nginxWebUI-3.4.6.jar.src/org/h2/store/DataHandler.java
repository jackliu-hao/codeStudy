package org.h2.store;

import org.h2.message.DbException;
import org.h2.util.SmallLRUCache;
import org.h2.util.TempFileDeleter;
import org.h2.value.CompareMode;

public interface DataHandler {
  String getDatabasePath();
  
  FileStore openFile(String paramString1, String paramString2, boolean paramBoolean);
  
  void checkPowerOff() throws DbException;
  
  void checkWritingAllowed() throws DbException;
  
  int getMaxLengthInplaceLob();
  
  TempFileDeleter getTempFileDeleter();
  
  Object getLobSyncObject();
  
  SmallLRUCache<String, String[]> getLobFileListCache();
  
  LobStorageInterface getLobStorage();
  
  int readLob(long paramLong1, byte[] paramArrayOfbyte1, long paramLong2, byte[] paramArrayOfbyte2, int paramInt1, int paramInt2);
  
  CompareMode getCompareMode();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\DataHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */