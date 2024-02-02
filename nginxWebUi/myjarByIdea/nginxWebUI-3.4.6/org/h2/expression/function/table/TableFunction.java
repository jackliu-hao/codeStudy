package org.h2.expression.function.table;

import java.util.Arrays;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionWithVariableParameters;
import org.h2.expression.function.NamedExpression;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.util.HasSQL;

public abstract class TableFunction implements HasSQL, NamedExpression, ExpressionWithVariableParameters {
   protected Expression[] args;
   private int argsCount;

   protected TableFunction(Expression[] var1) {
      this.args = var1;
   }

   public void addParameter(Expression var1) {
      int var2 = this.args.length;
      if (this.argsCount >= var2) {
         this.args = (Expression[])Arrays.copyOf(this.args, var2 * 2);
      }

      this.args[this.argsCount++] = var1;
   }

   public void doneWithParameters() throws DbException {
      if (this.args.length != this.argsCount) {
         this.args = (Expression[])Arrays.copyOf(this.args, this.argsCount);
      }

   }

   public abstract ResultInterface getValue(SessionLocal var1);

   public abstract ResultInterface getValueTemplate(SessionLocal var1);

   public void optimize(SessionLocal var1) {
      int var2 = 0;

      for(int var3 = this.args.length; var2 < var3; ++var2) {
         this.args[var2] = this.args[var2].optimize(var1);
      }

   }

   public abstract boolean isDeterministic();

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return Expression.writeExpressions(var1.append(this.getName()).append('('), this.args, var2).append(')');
   }
}
