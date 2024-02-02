package com.mysql.cj.protocol;

import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import java.util.ArrayList;
import java.util.List;

public interface ServerSessionStateController {
   int SESSION_TRACK_SYSTEM_VARIABLES = 0;
   int SESSION_TRACK_SCHEMA = 1;
   int SESSION_TRACK_STATE_CHANGE = 2;
   int SESSION_TRACK_GTIDS = 3;
   int SESSION_TRACK_TRANSACTION_CHARACTERISTICS = 4;
   int SESSION_TRACK_TRANSACTION_STATE = 5;

   default void setSessionStateChanges(ServerSessionStateChanges changes) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   default ServerSessionStateChanges getSessionStateChanges() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   default void addSessionStateChangesListener(SessionStateChangesListener l) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   default void removeSessionStateChangesListener(SessionStateChangesListener l) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public static class SessionStateChange {
      private int type;
      private List<String> values = new ArrayList();

      public SessionStateChange(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }

      public List<String> getValues() {
         return this.values;
      }

      public SessionStateChange addValue(String value) {
         this.values.add(value);
         return this;
      }
   }

   public interface ServerSessionStateChanges {
      List<SessionStateChange> getSessionStateChangesList();
   }

   @FunctionalInterface
   public interface SessionStateChangesListener {
      void handleSessionStateChanges(ServerSessionStateChanges var1);
   }
}
