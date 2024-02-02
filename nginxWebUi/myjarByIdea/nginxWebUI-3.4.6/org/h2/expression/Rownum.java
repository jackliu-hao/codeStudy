package org.h2.expression;

import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;

public final class Rownum extends Operation0 {
   private final Prepared prepared;
   private boolean singleRow;

   public Rownum(Prepared var1) {
      if (var1 == null) {
         throw DbException.getInternalError();
      } else {
         this.prepared = var1;
      }
   }

   public Value getValue(SessionLocal var1) {
      return ValueBigint.get(this.prepared.getCurrentRowNumber());
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_BIGINT;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return var1.append("ROWNUM()");
   }

   public Expression optimize(SessionLocal var1) {
      return (Expression)(this.singleRow ? ValueExpression.get(ValueBigint.get(1L)) : this);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      switch (var1.getType()) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 8:
            return false;
         case 11:
            if (var1.getQueryLevel() > 0) {
               this.singleRow = true;
            }
         case 4:
         case 5:
         case 6:
         case 7:
         case 9:
         case 10:
         default:
            return true;
      }
   }

   public int getCost() {
      return 0;
   }
}
