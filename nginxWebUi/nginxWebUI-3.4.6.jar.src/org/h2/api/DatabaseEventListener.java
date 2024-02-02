package org.h2.api;

import java.sql.SQLException;
import java.util.EventListener;

public interface DatabaseEventListener extends EventListener {
  public static final int STATE_SCAN_FILE = 0;
  
  public static final int STATE_CREATE_INDEX = 1;
  
  public static final int STATE_RECOVER = 2;
  
  public static final int STATE_BACKUP_FILE = 3;
  
  public static final int STATE_RECONNECTED = 4;
  
  public static final int STATE_STATEMENT_START = 5;
  
  public static final int STATE_STATEMENT_END = 6;
  
  public static final int STATE_STATEMENT_PROGRESS = 7;
  
  default void init(String paramString) {}
  
  default void opened() {}
  
  default void exceptionThrown(SQLException paramSQLException, String paramString) {}
  
  default void setProgress(int paramInt, String paramString, long paramLong1, long paramLong2) {}
  
  default void closingDatabase() {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\api\DatabaseEventListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */