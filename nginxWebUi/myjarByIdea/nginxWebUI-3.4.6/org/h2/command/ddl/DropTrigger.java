package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.TriggerObject;
import org.h2.table.Table;

public class DropTrigger extends SchemaCommand {
   private String triggerName;
   private boolean ifExists;

   public DropTrigger(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setIfExists(boolean var1) {
      this.ifExists = var1;
   }

   public void setTriggerName(String var1) {
      this.triggerName = var1;
   }

   public long update() {
      Database var1 = this.session.getDatabase();
      TriggerObject var2 = this.getSchema().findTrigger(this.triggerName);
      if (var2 == null) {
         if (!this.ifExists) {
            throw DbException.get(90042, this.triggerName);
         }
      } else {
         Table var3 = var2.getTable();
         this.session.getUser().checkTableRight(var3, 32);
         var1.removeSchemaObject(this.session, var2);
      }

      return 0L;
   }

   public int getType() {
      return 45;
   }
}
