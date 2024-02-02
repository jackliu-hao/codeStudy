package org.h2.command.dml;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.table.Table;
import org.h2.table.TableFilter;

abstract class FilteredDataChangeStatement extends DataChangeStatement {
   Expression condition;
   TableFilter targetTableFilter;
   Expression fetchExpr;

   FilteredDataChangeStatement(SessionLocal var1) {
      super(var1);
   }

   public final Table getTable() {
      return this.targetTableFilter.getTable();
   }

   public final void setTableFilter(TableFilter var1) {
      this.targetTableFilter = var1;
   }

   public final TableFilter getTableFilter() {
      return this.targetTableFilter;
   }

   public final void setCondition(Expression var1) {
      this.condition = var1;
   }

   public final Expression getCondition() {
      return this.condition;
   }

   public void setFetch(Expression var1) {
      this.fetchExpr = var1;
   }

   final boolean nextRow(long var1, long var3) {
      if (var1 < 0L || var3 < var1) {
         while(this.targetTableFilter.next()) {
            this.setCurrentRowNumber(var3 + 1L);
            if (this.condition == null || this.condition.getBooleanValue(this.session)) {
               return true;
            }
         }
      }

      return false;
   }

   final void appendFilterCondition(StringBuilder var1, int var2) {
      if (this.condition != null) {
         var1.append("\nWHERE ");
         this.condition.getUnenclosedSQL(var1, var2);
      }

      if (this.fetchExpr != null) {
         var1.append("\nFETCH FIRST ");
         String var3 = this.fetchExpr.getSQL(var2, 2);
         if ("1".equals(var3)) {
            var1.append("ROW ONLY");
         } else {
            var1.append(var3).append(" ROWS ONLY");
         }
      }

   }
}
