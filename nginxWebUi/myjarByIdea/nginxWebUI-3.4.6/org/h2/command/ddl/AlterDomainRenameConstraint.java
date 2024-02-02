package org.h2.command.ddl;

import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintDomain;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.schema.Schema;

public class AlterDomainRenameConstraint extends AlterDomain {
   private String constraintName;
   private String newConstraintName;

   public AlterDomainRenameConstraint(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setConstraintName(String var1) {
      this.constraintName = var1;
   }

   public void setNewConstraintName(String var1) {
      this.newConstraintName = var1;
   }

   long update(Schema var1, Domain var2) {
      Constraint var3 = this.getSchema().findConstraint(this.session, this.constraintName);
      if (var3 != null && var3.getConstraintType() == Constraint.Type.DOMAIN && ((ConstraintDomain)var3).getDomain() == var2) {
         if (this.getSchema().findConstraint(this.session, this.newConstraintName) == null && !this.newConstraintName.equals(this.constraintName)) {
            this.session.getDatabase().renameSchemaObject(this.session, var3, this.newConstraintName);
            return 0L;
         } else {
            throw DbException.get(90045, this.newConstraintName);
         }
      } else {
         throw DbException.get(90057, this.constraintName);
      }
   }

   public int getType() {
      return 101;
   }
}
