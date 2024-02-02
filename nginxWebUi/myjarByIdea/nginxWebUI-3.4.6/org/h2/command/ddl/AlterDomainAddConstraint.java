package org.h2.command.ddl;

import org.h2.constraint.ConstraintDomain;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.schema.Schema;

public class AlterDomainAddConstraint extends AlterDomain {
   private String constraintName;
   private Expression checkExpression;
   private String comment;
   private boolean checkExisting;
   private final boolean ifNotExists;

   public AlterDomainAddConstraint(SessionLocal var1, Schema var2, boolean var3) {
      super(var1, var2);
      this.ifNotExists = var3;
   }

   private String generateConstraintName(Domain var1) {
      if (this.constraintName == null) {
         this.constraintName = this.getSchema().getUniqueDomainConstraintName(this.session, var1);
      }

      return this.constraintName;
   }

   long update(Schema var1, Domain var2) {
      long var3;
      try {
         var3 = (long)this.tryUpdate(var1, var2);
      } finally {
         this.getSchema().freeUniqueName(this.constraintName);
      }

      return var3;
   }

   private int tryUpdate(Schema var1, Domain var2) {
      if (this.constraintName != null && var1.findConstraint(this.session, this.constraintName) != null) {
         if (this.ifNotExists) {
            return 0;
         } else {
            throw DbException.get(90045, this.constraintName);
         }
      } else {
         Database var3 = this.session.getDatabase();
         var3.lockMeta(this.session);
         int var4 = this.getObjectId();
         String var5 = this.generateConstraintName(var2);
         ConstraintDomain var6 = new ConstraintDomain(var1, var4, var5, var2);
         var6.setExpression(this.session, this.checkExpression);
         if (this.checkExisting) {
            var6.checkExistingData(this.session);
         }

         var6.setComment(this.comment);
         var3.addSchemaObject(this.session, var6);
         var2.addConstraint(var6);
         return 0;
      }
   }

   public void setConstraintName(String var1) {
      this.constraintName = var1;
   }

   public String getConstraintName() {
      return this.constraintName;
   }

   public int getType() {
      return 92;
   }

   public void setCheckExpression(Expression var1) {
      this.checkExpression = var1;
   }

   public void setComment(String var1) {
      this.comment = var1;
   }

   public void setCheckExisting(boolean var1) {
      this.checkExisting = var1;
   }
}
