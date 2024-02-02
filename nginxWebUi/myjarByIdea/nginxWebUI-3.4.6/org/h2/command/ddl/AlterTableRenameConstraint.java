package org.h2.command.ddl;

import org.h2.constraint.Constraint;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;

public class AlterTableRenameConstraint extends AlterTable {
   private String constraintName;
   private String newConstraintName;

   public AlterTableRenameConstraint(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setConstraintName(String var1) {
      this.constraintName = var1;
   }

   public void setNewConstraintName(String var1) {
      this.newConstraintName = var1;
   }

   public long update(Table var1) {
      Constraint var2 = this.getSchema().findConstraint(this.session, this.constraintName);
      Database var3 = this.session.getDatabase();
      if (var2 != null && var2.getConstraintType() != Constraint.Type.DOMAIN && var2.getTable() == var1) {
         if (this.getSchema().findConstraint(this.session, this.newConstraintName) == null && !this.newConstraintName.equals(this.constraintName)) {
            User var4 = this.session.getUser();
            Table var5 = var2.getRefTable();
            if (var5 != var1) {
               var4.checkTableRight(var5, 32);
            }

            var3.renameSchemaObject(this.session, var2, this.newConstraintName);
            return 0L;
         } else {
            throw DbException.get(90045, this.newConstraintName);
         }
      } else {
         throw DbException.get(90057, this.constraintName);
      }
   }

   public int getType() {
      return 85;
   }
}
