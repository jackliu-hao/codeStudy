package org.h2.jdbc;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.BatchUpdateException;
import java.sql.SQLException;

public final class JdbcBatchUpdateException extends BatchUpdateException {
   private static final long serialVersionUID = 1L;

   JdbcBatchUpdateException(SQLException var1, int[] var2) {
      super(var1.getMessage(), var1.getSQLState(), var1.getErrorCode(), var2);
      this.setNextException(var1);
   }

   JdbcBatchUpdateException(SQLException var1, long[] var2) {
      super(var1.getMessage(), var1.getSQLState(), var1.getErrorCode(), var2, (Throwable)null);
      this.setNextException(var1);
   }

   public void printStackTrace() {
      this.printStackTrace(System.err);
   }

   public void printStackTrace(PrintWriter var1) {
      if (var1 != null) {
         super.printStackTrace(var1);
         if (this.getNextException() != null) {
            this.getNextException().printStackTrace(var1);
         }
      }

   }

   public void printStackTrace(PrintStream var1) {
      if (var1 != null) {
         super.printStackTrace(var1);
         if (this.getNextException() != null) {
            this.getNextException().printStackTrace(var1);
         }
      }

   }
}
