package org.h2.command.ddl;

import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintDomain;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.schema.Schema;

public class AlterDomainDropConstraint extends AlterDomain {
   private String constraintName;
   private final boolean ifConstraintExists;

   public AlterDomainDropConstraint(SessionLocal var1, Schema var2, boolean var3) {
      super(var1, var2);
      this.ifConstraintExists = var3;
   }

   public void setConstraintName(String var1) {
      this.constraintName = var1;
   }

   long update(Schema var1, Domain var2) {
      Constraint var3 = var1.findConstraint(this.session, this.constraintName);
      if (var3 != null && var3.getConstraintType() == Constraint.Type.DOMAIN && ((ConstraintDomain)var3).getDomain() == var2) {
         this.session.getDatabase().removeSchemaObject(this.session, var3);
      } else if (!this.ifConstraintExists) {
         throw DbException.get(90057, this.constraintName);
      }

      return 0L;
   }

   public int getType() {
      return 93;
   }
}
