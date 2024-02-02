package org.h2.command.ddl;

import java.util.Iterator;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintActionType;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;

public class AlterTableDropConstraint extends AlterTable {
   private String constraintName;
   private final boolean ifExists;
   private ConstraintActionType dropAction;

   public AlterTableDropConstraint(SessionLocal var1, Schema var2, boolean var3) {
      super(var1, var2);
      this.ifExists = var3;
      this.dropAction = var1.getDatabase().getSettings().dropRestrict ? ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
   }

   public void setConstraintName(String var1) {
      this.constraintName = var1;
   }

   public void setDropAction(ConstraintActionType var1) {
      this.dropAction = var1;
   }

   public long update(Table var1) {
      Constraint var2 = this.getSchema().findConstraint(this.session, this.constraintName);
      Constraint.Type var3;
      if (var2 != null && (var3 = var2.getConstraintType()) != Constraint.Type.DOMAIN && var2.getTable() == var1) {
         Table var4 = var2.getRefTable();
         if (var4 != var1) {
            this.session.getUser().checkTableRight(var4, 32);
         }

         if (var3 == Constraint.Type.PRIMARY_KEY || var3 == Constraint.Type.UNIQUE) {
            Iterator var5 = var2.getTable().getConstraints().iterator();

            while(var5.hasNext()) {
               Constraint var6 = (Constraint)var5.next();
               if (var6.getReferencedConstraint() == var2) {
                  if (this.dropAction == ConstraintActionType.RESTRICT) {
                     throw DbException.get(90152, var2.getTraceSQL(), var6.getTraceSQL());
                  }

                  Table var7 = var6.getTable();
                  if (var7 != var1 && var7 != var4) {
                     this.session.getUser().checkTableRight(var7, 32);
                  }
               }
            }
         }

         this.session.getDatabase().removeSchemaObject(this.session, var2);
      } else if (!this.ifExists) {
         throw DbException.get(90057, this.constraintName);
      }

      return 0L;
   }

   public int getType() {
      return 14;
   }
}
