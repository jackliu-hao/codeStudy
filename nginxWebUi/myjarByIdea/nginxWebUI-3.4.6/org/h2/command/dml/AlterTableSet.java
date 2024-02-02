package org.h2.command.dml;

import org.h2.command.ddl.SchemaCommand;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;

public class AlterTableSet extends SchemaCommand {
   private boolean ifTableExists;
   private String tableName;
   private final int type;
   private final boolean value;
   private boolean checkExisting;

   public AlterTableSet(SessionLocal var1, Schema var2, int var3, boolean var4) {
      super(var1, var2);
      this.type = var3;
      this.value = var4;
   }

   public void setCheckExisting(boolean var1) {
      this.checkExisting = var1;
   }

   public boolean isTransactional() {
      return true;
   }

   public void setIfTableExists(boolean var1) {
      this.ifTableExists = var1;
   }

   public void setTableName(String var1) {
      this.tableName = var1;
   }

   public long update() {
      Table var1 = this.getSchema().resolveTableOrView(this.session, this.tableName);
      if (var1 == null) {
         if (this.ifTableExists) {
            return 0L;
         } else {
            throw DbException.get(42102, (String)this.tableName);
         }
      } else {
         this.session.getUser().checkTableRight(var1, 32);
         var1.lock(this.session, 2);
         switch (this.type) {
            case 55:
               var1.setCheckForeignKeyConstraints(this.session, this.value, this.value ? this.checkExisting : false);
               return 0L;
            default:
               throw DbException.getInternalError("type=" + this.type);
         }
      }
   }

   public int getType() {
      return this.type;
   }
}
