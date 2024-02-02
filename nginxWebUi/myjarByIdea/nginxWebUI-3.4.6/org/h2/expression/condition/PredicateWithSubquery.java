package org.h2.expression.condition;

import org.h2.command.query.Query;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.util.StringUtils;

abstract class PredicateWithSubquery extends Condition {
   final Query query;

   PredicateWithSubquery(Query var1) {
      this.query = var1;
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.query.mapColumns(var1, var2 + 1);
   }

   public Expression optimize(SessionLocal var1) {
      this.query.prepare();
      return this;
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.query.setEvaluatable(var1, var2);
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return StringUtils.indent(var1.append('('), this.query.getPlanSQL(var2), 4, false).append(')');
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.query.updateAggregate(var1, var2);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.query.isEverything(var1);
   }

   public int getCost() {
      return this.query.getCostAsExpression();
   }
}
