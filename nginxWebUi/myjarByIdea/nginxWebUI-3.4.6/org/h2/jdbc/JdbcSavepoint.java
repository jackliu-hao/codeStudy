package org.h2.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.message.TraceObject;
import org.h2.util.StringUtils;

public final class JdbcSavepoint extends TraceObject implements Savepoint {
   private static final String SYSTEM_SAVEPOINT_PREFIX = "SYSTEM_SAVEPOINT_";
   private final int savepointId;
   private final String name;
   private JdbcConnection conn;

   JdbcSavepoint(JdbcConnection var1, int var2, String var3, Trace var4, int var5) {
      this.setTrace(var4, 6, var5);
      this.conn = var1;
      this.savepointId = var2;
      this.name = var3;
   }

   void release() {
      this.conn = null;
   }

   static String getName(String var0, int var1) {
      return var0 != null ? StringUtils.quoteJavaString(var0) : "SYSTEM_SAVEPOINT_" + var1;
   }

   void rollback() {
      this.checkValid();
      this.conn.prepareCommand("ROLLBACK TO SAVEPOINT " + getName(this.name, this.savepointId), Integer.MAX_VALUE).executeUpdate((Object)null);
   }

   private void checkValid() {
      if (this.conn == null) {
         throw DbException.get(90063, getName(this.name, this.savepointId));
      }
   }

   public int getSavepointId() throws SQLException {
      try {
         this.debugCodeCall("getSavepointId");
         this.checkValid();
         if (this.name != null) {
            throw DbException.get(90065);
         } else {
            return this.savepointId;
         }
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public String getSavepointName() throws SQLException {
      try {
         this.debugCodeCall("getSavepointName");
         this.checkValid();
         if (this.name == null) {
            throw DbException.get(90064);
         } else {
            return this.name;
         }
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public String toString() {
      return this.getTraceObjectName() + ": id=" + this.savepointId + " name=" + this.name;
   }
}
