package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;

public abstract class Operation0 extends Expression {
   protected Operation0() {
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
   }

   public Expression optimize(SessionLocal var1) {
      return this;
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
   }

   public void updateAggregate(SessionLocal var1, int var2) {
   }
}
