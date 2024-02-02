package org.h2.command.ddl;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;

public abstract class AlterTable extends SchemaCommand {
   String tableName;
   boolean ifTableExists;

   AlterTable(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public final void setTableName(String var1) {
      this.tableName = var1;
   }

   public final void setIfTableExists(boolean var1) {
      this.ifTableExists = var1;
   }

   public final long update() {
      Table var1 = this.getSchema().findTableOrView(this.session, this.tableName);
      if (var1 == null) {
         if (this.ifTableExists) {
            return 0L;
         } else {
            throw DbException.get(42102, (String)this.tableName);
         }
      } else {
         this.session.getUser().checkTableRight(var1, 32);
         return this.update(var1);
      }
   }

   abstract long update(Table var1);
}
