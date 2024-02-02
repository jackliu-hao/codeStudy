package org.h2.jdbc;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLNonTransientConnectionException;
import org.h2.message.DbException;

public final class JdbcSQLNonTransientConnectionException extends SQLNonTransientConnectionException implements JdbcException {
   private static final long serialVersionUID = 1L;
   private final String originalMessage;
   private final String stackTrace;
   private String message;
   private String sql;

   public JdbcSQLNonTransientConnectionException(String var1, String var2, String var3, int var4, Throwable var5, String var6) {
      super(var1, var3, var4);
      this.originalMessage = var1;
      this.stackTrace = var6;
      this.setSQL(var2);
      this.initCause(var5);
   }

   public String getMessage() {
      return this.message;
   }

   public String getOriginalMessage() {
      return this.originalMessage;
   }

   public void printStackTrace(PrintWriter var1) {
      super.printStackTrace(var1);
      DbException.printNextExceptions(this, (PrintWriter)var1);
   }

   public void printStackTrace(PrintStream var1) {
      super.printStackTrace(var1);
      DbException.printNextExceptions(this, (PrintStream)var1);
   }

   public String getSQL() {
      return this.sql;
   }

   public void setSQL(String var1) {
      this.sql = var1;
      this.message = DbException.buildMessageForException(this);
   }

   public String toString() {
      return this.stackTrace == null ? super.toString() : this.stackTrace;
   }
}
