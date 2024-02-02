package com.zaxxer.hikari;

import java.sql.SQLException;

public interface SQLExceptionOverride {
   default Override adjudicate(SQLException sqlException) {
      return SQLExceptionOverride.Override.CONTINUE_EVICT;
   }

   public static enum Override {
      CONTINUE_EVICT,
      DO_NOT_EVICT;
   }
}
