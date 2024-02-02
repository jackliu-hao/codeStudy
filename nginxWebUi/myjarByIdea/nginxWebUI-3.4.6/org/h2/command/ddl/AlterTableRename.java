package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;

public class AlterTableRename extends AlterTable {
   private String newTableName;
   private boolean hidden;

   public AlterTableRename(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setNewTableName(String var1) {
      this.newTableName = var1;
   }

   public long update(Table var1) {
      Database var2 = this.session.getDatabase();
      Table var3 = this.getSchema().findTableOrView(this.session, this.newTableName);
      if (var3 != null && this.hidden && this.newTableName.equals(var1.getName())) {
         if (!var3.isHidden()) {
            var3.setHidden(this.hidden);
            var1.setHidden(true);
            var2.updateMeta(this.session, var1);
         }

         return 0L;
      } else if (var3 == null && !this.newTableName.equals(var1.getName())) {
         if (var1.isTemporary()) {
            throw DbException.getUnsupportedException("temp table");
         } else {
            var2.renameSchemaObject(this.session, var1, this.newTableName);
            return 0L;
         }
      } else {
         throw DbException.get(42101, (String)this.newTableName);
      }
   }

   public int getType() {
      return 15;
   }

   public void setHidden(boolean var1) {
      this.hidden = var1;
   }
}
