package org.h2.command.ddl;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.ColumnTemplate;

public class AlterDomainExpressions extends AlterDomain {
   private final int type;
   private Expression expression;

   public AlterDomainExpressions(SessionLocal var1, Schema var2, int var3) {
      super(var1, var2);
      this.type = var3;
   }

   public void setExpression(Expression var1) {
      this.expression = var1;
   }

   long update(Schema var1, Domain var2) {
      switch (this.type) {
         case 94:
            var2.setDefaultExpression(this.session, this.expression);
            break;
         case 95:
            var2.setOnUpdateExpression(this.session, this.expression);
            break;
         default:
            throw DbException.getInternalError("type=" + this.type);
      }

      if (this.expression != null) {
         forAllDependencies(this.session, var2, this::copyColumn, this::copyDomain, true);
      }

      this.session.getDatabase().updateMeta(this.session, var2);
      return 0L;
   }

   private boolean copyColumn(Domain var1, Column var2) {
      return this.copyExpressions(this.session, var1, var2);
   }

   private boolean copyDomain(Domain var1, Domain var2) {
      return this.copyExpressions(this.session, var1, var2);
   }

   private boolean copyExpressions(SessionLocal var1, Domain var2, ColumnTemplate var3) {
      Expression var4;
      switch (this.type) {
         case 94:
            var4 = var2.getDefaultExpression();
            if (var4 != null && var3.getDefaultExpression() == null) {
               var3.setDefaultExpression(var1, var4);
               return true;
            }
            break;
         case 95:
            var4 = var2.getOnUpdateExpression();
            if (var4 != null && var3.getOnUpdateExpression() == null) {
               var3.setOnUpdateExpression(var1, var4);
               return true;
            }
      }

      return false;
   }

   public int getType() {
      return this.type;
   }
}
