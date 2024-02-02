package org.h2.tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.h2.api.Trigger;
import org.h2.message.DbException;

public abstract class TriggerAdapter implements Trigger {
   protected String schemaName;
   protected String triggerName;
   protected String tableName;
   protected boolean before;
   protected int type;

   public void init(Connection var1, String var2, String var3, String var4, boolean var5, int var6) throws SQLException {
      this.schemaName = var2;
      this.triggerName = var3;
      this.tableName = var4;
      this.before = var5;
      this.type = var6;
   }

   public final void fire(Connection var1, Object[] var2, Object[] var3) throws SQLException {
      throw DbException.getInternalError();
   }

   public abstract void fire(Connection var1, ResultSet var2, ResultSet var3) throws SQLException;
}
