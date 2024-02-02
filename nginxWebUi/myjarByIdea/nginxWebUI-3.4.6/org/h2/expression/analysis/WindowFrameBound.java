package org.h2.expression.analysis;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.table.ColumnResolver;

public class WindowFrameBound {
   private final WindowFrameBoundType type;
   private Expression value;
   private boolean isVariable;
   private int expressionIndex = -1;

   public WindowFrameBound(WindowFrameBoundType var1, Expression var2) {
      this.type = var1;
      if (var1 != WindowFrameBoundType.PRECEDING && var1 != WindowFrameBoundType.FOLLOWING) {
         this.value = null;
      } else {
         this.value = var2;
      }

   }

   public WindowFrameBoundType getType() {
      return this.type;
   }

   public Expression getValue() {
      return this.value;
   }

   public boolean isParameterized() {
      return this.type == WindowFrameBoundType.PRECEDING || this.type == WindowFrameBoundType.FOLLOWING;
   }

   public boolean isVariable() {
      return this.isVariable;
   }

   public int getExpressionIndex() {
      return this.expressionIndex;
   }

   void setExpressionIndex(int var1) {
      this.expressionIndex = var1;
   }

   void mapColumns(ColumnResolver var1, int var2, int var3) {
      if (this.value != null) {
         this.value.mapColumns(var1, var2, var3);
      }

   }

   void optimize(SessionLocal var1) {
      if (this.value != null) {
         this.value = this.value.optimize(var1);
         if (!this.value.isConstant()) {
            this.isVariable = true;
         }
      }

   }

   void updateAggregate(SessionLocal var1, int var2) {
      if (this.value != null) {
         this.value.updateAggregate(var1, var2);
      }

   }

   public StringBuilder getSQL(StringBuilder var1, boolean var2, int var3) {
      if (this.type == WindowFrameBoundType.PRECEDING || this.type == WindowFrameBoundType.FOLLOWING) {
         this.value.getUnenclosedSQL(var1, var3).append(' ');
      }

      return var1.append(this.type.getSQL());
   }
}
