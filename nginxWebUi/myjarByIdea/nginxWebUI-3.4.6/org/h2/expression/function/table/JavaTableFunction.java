package org.h2.expression.function.table;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.schema.FunctionAlias;

public final class JavaTableFunction extends TableFunction {
   private final FunctionAlias functionAlias;
   private final FunctionAlias.JavaMethod javaMethod;

   public JavaTableFunction(FunctionAlias var1, Expression[] var2) {
      super(var2);
      this.functionAlias = var1;
      this.javaMethod = var1.findJavaMethod(var2);
      if (this.javaMethod.getDataType() != null) {
         throw DbException.get(90000, this.getName());
      }
   }

   public ResultInterface getValue(SessionLocal var1) {
      return this.javaMethod.getTableValue(var1, this.args, false);
   }

   public ResultInterface getValueTemplate(SessionLocal var1) {
      return this.javaMethod.getTableValue(var1, this.args, true);
   }

   public void optimize(SessionLocal var1) {
      super.optimize(var1);
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return Expression.writeExpressions(this.functionAlias.getSQL(var1, var2).append('('), this.args, var2).append(')');
   }

   public String getName() {
      return this.functionAlias.getName();
   }

   public boolean isDeterministic() {
      return this.functionAlias.isDeterministic();
   }
}
