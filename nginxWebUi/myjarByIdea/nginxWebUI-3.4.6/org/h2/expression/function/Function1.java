package org.h2.expression.function;

import org.h2.expression.Expression;
import org.h2.expression.Operation1;

public abstract class Function1 extends Operation1 implements NamedExpression {
   protected Function1(Expression var1) {
      super(var1);
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return this.arg.getUnenclosedSQL(var1.append(this.getName()).append('('), var2).append(')');
   }
}
