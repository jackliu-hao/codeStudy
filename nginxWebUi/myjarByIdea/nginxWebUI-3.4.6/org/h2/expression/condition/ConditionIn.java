package org.h2.expression.condition;

import java.util.ArrayList;
import java.util.Iterator;
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
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

public final class ConditionIn extends Condition {
   private Expression left;
   private final boolean not;
   private final boolean whenOperand;
   private final ArrayList<Expression> valueList;

   public ConditionIn(Expression var1, boolean var2, boolean var3, ArrayList<Expression> var4) {
      this.left = var1;
      this.not = var2;
      this.whenOperand = var3;
      this.valueList = var4;
   }

   public Value getValue(SessionLocal var1) {
      return this.getValue(var1, this.left.getValue(var1));
   }

   public boolean getWhenValue(SessionLocal var1, Value var2) {
      return !this.whenOperand ? super.getWhenValue(var1, var2) : this.getValue(var1, var2).isTrue();
   }

   private Value getValue(SessionLocal var1, Value var2) {
      if (var2.containsNull()) {
         return ValueNull.INSTANCE;
      } else {
         boolean var3 = false;
         Iterator var4 = this.valueList.iterator();

         while(var4.hasNext()) {
            Expression var5 = (Expression)var4.next();
            Value var6 = var5.getValue(var1);
            Value var7 = Comparison.compare(var1, var2, var6, 0);
            if (var7 == ValueNull.INSTANCE) {
               var3 = true;
            } else if (var7 == ValueBoolean.TRUE) {
               return ValueBoolean.get(!this.not);
            }
         }

         if (var3) {
            return ValueNull.INSTANCE;
         } else {
            return ValueBoolean.get(this.not);
         }
      }
   }

   public boolean isWhenConditionOperand() {
      return this.whenOperand;
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.left.mapColumns(var1, var2, var3);
      Iterator var4 = this.valueList.iterator();

      while(var4.hasNext()) {
         Expression var5 = (Expression)var4.next();
         var5.mapColumns(var1, var2, var3);
      }

   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      boolean var2 = !this.whenOperand && this.left.isConstant();
      if (var2 && this.left.isNullConstant()) {
         return TypedValueExpression.UNKNOWN;
      } else {
         boolean var3 = true;
         boolean var4 = true;
         TypeInfo var5 = this.left.getType();
         int var6 = 0;

         for(int var7 = this.valueList.size(); var6 < var7; ++var6) {
            Expression var8 = (Expression)this.valueList.get(var6);
            var8 = var8.optimize(var1);
            TypeInfo.checkComparable(var5, var8.getType());
            if (var8.isConstant() && !var8.getValue(var1).containsNull()) {
               var4 = false;
            }

            if (var3 && !var8.isConstant()) {
               var3 = false;
            }

            if (this.left instanceof ExpressionColumn && var8 instanceof Parameter) {
               ((Parameter)var8).setColumn(((ExpressionColumn)this.left).getColumn());
            }

            this.valueList.set(var6, var8);
         }

         return this.optimize2(var1, var2, var3, var4, this.valueList);
      }
   }

   private Expression optimize2(SessionLocal var1, boolean var2, boolean var3, boolean var4, ArrayList<Expression> var5) {
      if (var2 && var3) {
         return ValueExpression.getBoolean(this.getValue(var1));
      } else if (var5.size() == 1) {
         return (new Comparison(this.not ? 1 : 0, this.left, (Expression)var5.get(0), this.whenOperand)).optimize(var1);
      } else if (var3 && !var4) {
         int var6 = this.left.getType().getValueType();
         if (var6 == -1) {
            return this;
         } else {
            return (Expression)(var6 == 36 && !(this.left instanceof ExpressionColumn) ? this : (new ConditionInConstantSet(var1, this.left, this.not, this.whenOperand, var5)).optimize(var1));
         }
      } else {
         return this;
      }
   }

   public Expression getNotIfPossible(SessionLocal var1) {
      return this.whenOperand ? null : new ConditionIn(this.left, !this.not, false, this.valueList);
   }

   public void createIndexConditions(SessionLocal var1, TableFilter var2) {
      if (!this.not && !this.whenOperand && this.left instanceof ExpressionColumn) {
         ExpressionColumn var3 = (ExpressionColumn)this.left;
         if (var2 == var3.getTableFilter()) {
            if (var1.getDatabase().getSettings().optimizeInList) {
               ExpressionVisitor var4 = ExpressionVisitor.getNotFromResolverVisitor(var2);
               TypeInfo var5 = var3.getType();
               Iterator var6 = this.valueList.iterator();

               while(true) {
                  if (!var6.hasNext()) {
                     var2.addIndexCondition(IndexCondition.getInList(var3, this.valueList));
                     break;
                  }

                  Expression var7 = (Expression)var6.next();
                  if (!var7.isEverything(var4) || !TypeInfo.haveSameOrdering(var5, TypeInfo.getHigherType(var5, var7.getType()))) {
                     return;
                  }
               }
            }

         }
      }
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.left.setEvaluatable(var1, var2);
      Iterator var3 = this.valueList.iterator();

      while(var3.hasNext()) {
         Expression var4 = (Expression)var3.next();
         var4.setEvaluatable(var1, var2);
      }

   }

   public boolean needParentheses() {
      return true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return this.getWhenSQL(this.left.getSQL(var1, var2, 0), var2);
   }

   public StringBuilder getWhenSQL(StringBuilder var1, int var2) {
      if (this.not) {
         var1.append(" NOT");
      }

      return writeExpressions(var1.append(" IN("), this.valueList, var2).append(')');
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.left.updateAggregate(var1, var2);
      Iterator var3 = this.valueList.iterator();

      while(var3.hasNext()) {
         Expression var4 = (Expression)var3.next();
         var4.updateAggregate(var1, var2);
      }

   }

   public boolean isEverything(ExpressionVisitor var1) {
      return !this.left.isEverything(var1) ? false : this.areAllValues(var1);
   }

   private boolean areAllValues(ExpressionVisitor var1) {
      Iterator var2 = this.valueList.iterator();

      Expression var3;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (Expression)var2.next();
      } while(var3.isEverything(var1));

      return false;
   }

   public int getCost() {
      int var1 = this.left.getCost();

      Expression var3;
      for(Iterator var2 = this.valueList.iterator(); var2.hasNext(); var1 += var3.getCost()) {
         var3 = (Expression)var2.next();
      }

      return var1;
   }

   Expression getAdditional(Comparison var1) {
      if (!this.not && !this.whenOperand && this.left.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
         Expression var2 = var1.getIfEquals(this.left);
         if (var2 != null) {
            ArrayList var3 = new ArrayList(this.valueList.size() + 1);
            var3.addAll(this.valueList);
            var3.add(var2);
            return new ConditionIn(this.left, false, false, var3);
         }
      }

      return null;
   }

   public int getSubexpressionCount() {
      return 1 + this.valueList.size();
   }

   public Expression getSubexpression(int var1) {
      if (var1 == 0) {
         return this.left;
      } else if (var1 > 0 && var1 <= this.valueList.size()) {
         return (Expression)this.valueList.get(var1 - 1);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }
}
