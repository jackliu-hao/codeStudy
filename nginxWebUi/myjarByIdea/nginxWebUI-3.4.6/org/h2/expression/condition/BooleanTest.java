package org.h2.expression.condition;

import java.util.ArrayList;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.ValueExpression;
import org.h2.index.IndexCondition;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

public final class BooleanTest extends SimplePredicate {
   private final Boolean right;

   public BooleanTest(Expression var1, boolean var2, boolean var3, Boolean var4) {
      super(var1, var2, var3);
      this.right = var4;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return this.getWhenSQL(this.left.getSQL(var1, var2, 0), var2);
   }

   public StringBuilder getWhenSQL(StringBuilder var1, int var2) {
      return var1.append(this.not ? " IS NOT " : " IS ").append(this.right == null ? "UNKNOWN" : (this.right ? "TRUE" : "FALSE"));
   }

   public Value getValue(SessionLocal var1) {
      return ValueBoolean.get(this.getValue(this.left.getValue(var1)));
   }

   public boolean getWhenValue(SessionLocal var1, Value var2) {
      return !this.whenOperand ? super.getWhenValue(var1, var2) : this.getValue(var2);
   }

   private boolean getValue(Value var1) {
      return (var1 == ValueNull.INSTANCE ? this.right == null : this.right != null && this.right == var1.getBoolean()) ^ this.not;
   }

   public Expression getNotIfPossible(SessionLocal var1) {
      return this.whenOperand ? null : new BooleanTest(this.left, !this.not, false, this.right);
   }

   public void createIndexConditions(SessionLocal var1, TableFilter var2) {
      if (!this.whenOperand && var2.getTable().isQueryComparable()) {
         if (this.left instanceof ExpressionColumn) {
            ExpressionColumn var3 = (ExpressionColumn)this.left;
            if (var3.getType().getValueType() == 8 && var2 == var3.getTableFilter()) {
               if (this.not) {
                  if (this.right == null && var3.getColumn().isNullable()) {
                     ArrayList var4 = new ArrayList(2);
                     var4.add(ValueExpression.FALSE);
                     var4.add(ValueExpression.TRUE);
                     var2.addIndexCondition(IndexCondition.getInList(var3, var4));
                  }
               } else {
                  var2.addIndexCondition(IndexCondition.get(6, var3, (Expression)(this.right == null ? TypedValueExpression.UNKNOWN : ValueExpression.getBoolean(this.right))));
               }
            }
         }

      }
   }
}
