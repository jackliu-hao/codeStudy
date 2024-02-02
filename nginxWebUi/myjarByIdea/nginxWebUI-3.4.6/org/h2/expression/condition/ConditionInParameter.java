package org.h2.expression.condition;

import java.util.AbstractList;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.ValueExpression;
import org.h2.index.IndexCondition;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

public final class ConditionInParameter extends Condition {
   private Expression left;
   private boolean not;
   private boolean whenOperand;
   private final Parameter parameter;

   static Value getValue(SessionLocal var0, Value var1, boolean var2, Value var3) {
      boolean var4 = false;
      if (var3.containsNull()) {
         var4 = true;
      } else {
         Value[] var5 = var3.convertToAnyArray(var0).getList();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Value var8 = var5[var7];
            Value var9 = Comparison.compare(var0, var1, var8, 0);
            if (var9 == ValueNull.INSTANCE) {
               var4 = true;
            } else if (var9 == ValueBoolean.TRUE) {
               return ValueBoolean.get(!var2);
            }
         }
      }

      return (Value)(var4 ? ValueNull.INSTANCE : ValueBoolean.get(var2));
   }

   public ConditionInParameter(Expression var1, boolean var2, boolean var3, Parameter var4) {
      this.left = var1;
      this.not = var2;
      this.whenOperand = var3;
      this.parameter = var4;
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.left.getValue(var1);
      return (Value)(var2 == ValueNull.INSTANCE ? ValueNull.INSTANCE : getValue(var1, var2, this.not, this.parameter.getValue(var1)));
   }

   public boolean getWhenValue(SessionLocal var1, Value var2) {
      if (!this.whenOperand) {
         return super.getWhenValue(var1, var2);
      } else {
         return var2 == ValueNull.INSTANCE ? false : getValue(var1, var2, this.not, this.parameter.getValue(var1)).isTrue();
      }
   }

   public boolean isWhenConditionOperand() {
      return this.whenOperand;
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.left.mapColumns(var1, var2, var3);
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      return (Expression)(!this.whenOperand && this.left.isNullConstant() ? TypedValueExpression.UNKNOWN : this);
   }

   public Expression getNotIfPossible(SessionLocal var1) {
      return this.whenOperand ? null : new ConditionInParameter(this.left, !this.not, false, this.parameter);
   }

   public void createIndexConditions(SessionLocal var1, TableFilter var2) {
      if (!this.not && !this.whenOperand && this.left instanceof ExpressionColumn) {
         ExpressionColumn var3 = (ExpressionColumn)this.left;
         if (var2 == var3.getTableFilter()) {
            var2.addIndexCondition(IndexCondition.getInList(var3, new ParameterList(this.parameter)));
         }
      }
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.left.setEvaluatable(var1, var2);
   }

   public boolean needParentheses() {
      return true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      if (this.not) {
         var1.append("NOT (");
      }

      this.left.getSQL(var1, var2, 0);
      this.parameter.getSQL(var1.append(" = ANY("), var2, 0).append(')');
      if (this.not) {
         var1.append(')');
      }

      return var1;
   }

   public StringBuilder getWhenSQL(StringBuilder var1, int var2) {
      if (this.not) {
         var1.append(" NOT IN(UNNEST(");
         this.parameter.getSQL(var1, var2, 0).append("))");
      } else {
         var1.append(" = ANY(");
         this.parameter.getSQL(var1, var2, 0).append(')');
      }

      return var1;
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.left.updateAggregate(var1, var2);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.left.isEverything(var1) && this.parameter.isEverything(var1);
   }

   public int getCost() {
      return this.left.getCost();
   }

   private static final class ParameterList extends AbstractList<Expression> {
      private final Parameter parameter;

      ParameterList(Parameter var1) {
         this.parameter = var1;
      }

      public Expression get(int var1) {
         Value var2 = this.parameter.getParamValue();
         if (var2 instanceof ValueArray) {
            return ValueExpression.get(((ValueArray)var2).getList()[var1]);
         } else if (var1 != 0) {
            throw new IndexOutOfBoundsException();
         } else {
            return ValueExpression.get(var2);
         }
      }

      public int size() {
         if (!this.parameter.isValueSet()) {
            return 0;
         } else {
            Value var1 = this.parameter.getParamValue();
            return var1 instanceof ValueArray ? ((ValueArray)var1).getList().length : 1;
         }
      }
   }
}
