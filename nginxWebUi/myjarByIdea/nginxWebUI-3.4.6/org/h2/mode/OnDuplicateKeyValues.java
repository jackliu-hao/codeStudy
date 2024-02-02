package org.h2.mode;

import org.h2.command.dml.Update;
import org.h2.engine.SessionLocal;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Operation0;
import org.h2.message.DbException;
import org.h2.table.Column;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public final class OnDuplicateKeyValues extends Operation0 {
   private final Column column;
   private final Update update;

   public OnDuplicateKeyValues(Column var1, Update var2) {
      this.column = var1;
      this.update = var2;
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.update.getOnDuplicateKeyInsert().getOnDuplicateKeyValue(this.column.getColumnId());
      if (var2 == null) {
         throw DbException.getUnsupportedException(this.getTraceSQL());
      } else {
         return var2;
      }
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return this.column.getSQL(var1.append("VALUES("), var2).append(')');
   }

   public boolean isEverything(ExpressionVisitor var1) {
      switch (var1.getType()) {
         case 2:
            return false;
         default:
            return true;
      }
   }

   public TypeInfo getType() {
      return this.column.getType();
   }

   public int getCost() {
      return 1;
   }
}
