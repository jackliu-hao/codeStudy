package org.h2.expression.condition;

import java.util.Arrays;
import org.h2.command.query.Query;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ValueExpression;
import org.h2.result.LocalResult;
import org.h2.result.ResultTarget;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

public class UniquePredicate extends PredicateWithSubquery {
   public UniquePredicate(Query var1) {
      super(var1);
   }

   public Expression optimize(SessionLocal var1) {
      super.optimize(var1);
      return (Expression)(this.query.isStandardDistinct() ? ValueExpression.TRUE : this);
   }

   public Value getValue(SessionLocal var1) {
      this.query.setSession(var1);
      int var2 = this.query.getColumnCount();
      LocalResult var3 = new LocalResult(var1, (Expression[])this.query.getExpressions().toArray(new Expression[0]), var2, var2);
      var3.setDistinct();
      Target var4 = new Target(var2, var3);
      this.query.query(2147483647L, var4);
      var3.close();
      return ValueBoolean.get(!var4.hasDuplicates);
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return super.getUnenclosedSQL(var1.append("UNIQUE"), var2);
   }

   private static final class Target implements ResultTarget {
      private final int columnCount;
      private final LocalResult result;
      boolean hasDuplicates;

      Target(int var1, LocalResult var2) {
         this.columnCount = var1;
         this.result = var2;
      }

      public void limitsWereApplied() {
      }

      public long getRowCount() {
         return 0L;
      }

      public void addRow(Value... var1) {
         if (!this.hasDuplicates) {
            for(int var2 = 0; var2 < this.columnCount; ++var2) {
               if (var1[var2] == ValueNull.INSTANCE) {
                  return;
               }
            }

            if (var1.length != this.columnCount) {
               var1 = (Value[])Arrays.copyOf(var1, this.columnCount);
            }

            long var4 = this.result.getRowCount() + 1L;
            this.result.addRow(var1);
            if (var4 != this.result.getRowCount()) {
               this.hasDuplicates = true;
               this.result.close();
            }

         }
      }
   }
}
