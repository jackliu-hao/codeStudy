package org.h2.command.dml;

import java.util.ArrayList;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.util.Utils;

public abstract class CommandWithValues extends DataChangeStatement {
   protected final ArrayList<Expression[]> valuesExpressionList = Utils.newSmallArrayList();

   protected CommandWithValues(SessionLocal var1) {
      super(var1);
   }

   public void addRow(Expression[] var1) {
      this.valuesExpressionList.add(var1);
   }
}
