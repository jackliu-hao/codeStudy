package org.h2.api;

import java.sql.SQLException;
import java.util.EventListener;

public interface DatabaseEventListener extends EventListener {
   int STATE_SCAN_FILE = 0;
   int STATE_CREATE_INDEX = 1;
   int STATE_RECOVER = 2;
   int STATE_BACKUP_FILE = 3;
   int STATE_RECONNECTED = 4;
   int STATE_STATEMENT_START = 5;
   int STATE_STATEMENT_END = 6;
   int STATE_STATEMENT_PROGRESS = 7;

   default void init(String var1) {
   }

   default void opened() {
   }

   default void exceptionThrown(SQLException var1, String var2) {
   }

   default void setProgress(int var1, String var2, long var3, long var5) {
   }

   default void closingDatabase() {
   }
}
