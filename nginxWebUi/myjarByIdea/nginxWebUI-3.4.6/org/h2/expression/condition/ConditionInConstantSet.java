package org.h2.expression.condition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.index.IndexCondition;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

public final class ConditionInConstantSet extends Condition {
   private Expression left;
   private final boolean not;
   private final boolean whenOperand;
   private final ArrayList<Expression> valueList;
   private final TreeSet<Value> valueSet;
   private boolean hasNull;
   private final TypeInfo type;

   ConditionInConstantSet(SessionLocal var1, Expression var2, boolean var3, boolean var4, ArrayList<Expression> var5) {
      this.left = var2;
      this.not = var3;
      this.whenOperand = var4;
      this.valueList = var5;
      this.valueSet = new TreeSet(var1.getDatabase().getCompareMode());
      TypeInfo var6 = var2.getType();

      Iterator var7;
      Expression var8;
      for(var7 = var5.iterator(); var7.hasNext(); var6 = TypeInfo.getHigherType(var6, var8.getType())) {
         var8 = (Expression)var7.next();
      }

      this.type = var6;
      var7 = var5.iterator();

      while(var7.hasNext()) {
         var8 = (Expression)var7.next();
         this.add(var8.getValue(var1), var1);
      }

   }

   private void add(Value var1, SessionLocal var2) {
      if ((var1 = var1.convertTo(this.type, var2)).containsNull()) {
         this.hasNull = true;
      } else {
         this.valueSet.add(var1);
      }

   }

   public Value getValue(SessionLocal var1) {
      return this.getValue(this.left.getValue(var1), var1);
   }

   public boolean getWhenValue(SessionLocal var1, Value var2) {
      return !this.whenOperand ? super.getWhenValue(var1, var2) : this.getValue(var2, var1).isTrue();
   }

   private Value getValue(Value var1, SessionLocal var2) {
      if ((var1 = var1.convertTo(this.type, var2)).containsNull()) {
         return ValueNull.INSTANCE;
      } else {
         boolean var3 = this.valueSet.contains(var1);
         return (Value)(!var3 && this.hasNull ? ValueNull.INSTANCE : ValueBoolean.get(this.not ^ var3));
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
      return this;
   }

   public Expression getNotIfPossible(SessionLocal var1) {
      return this.whenOperand ? null : new ConditionInConstantSet(var1, this.left, !this.not, false, this.valueList);
   }

   public void createIndexConditions(SessionLocal var1, TableFilter var2) {
      if (!this.not && !this.whenOperand && this.left instanceof ExpressionColumn) {
         ExpressionColumn var3 = (ExpressionColumn)this.left;
         if (var2 == var3.getTableFilter()) {
            if (var1.getDatabase().getSettings().optimizeInList) {
               TypeInfo var4 = var3.getType();
               if (TypeInfo.haveSameOrdering(var4, TypeInfo.getHigherType(var4, this.type))) {
                  var2.addIndexCondition(IndexCondition.getInList(var3, this.valueList));
               }
            }

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
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.left.isEverything(var1);
   }

   public int getCost() {
      return this.left.getCost();
   }

   Expression getAdditional(SessionLocal var1, Comparison var2) {
      if (!this.not && !this.whenOperand && this.left.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
         Expression var3 = var2.getIfEquals(this.left);
         if (var3 != null && var3.isConstant()) {
            ArrayList var4 = new ArrayList(this.valueList.size() + 1);
            var4.addAll(this.valueList);
            var4.add(var3);
            return new ConditionInConstantSet(var1, this.left, false, false, var4);
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
