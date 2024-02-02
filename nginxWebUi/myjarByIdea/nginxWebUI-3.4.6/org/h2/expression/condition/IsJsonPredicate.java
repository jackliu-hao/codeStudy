package org.h2.expression.condition;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.util.json.JSONBytesSource;
import org.h2.util.json.JSONItemType;
import org.h2.util.json.JSONStringSource;
import org.h2.util.json.JSONTarget;
import org.h2.util.json.JSONValidationTargetWithUniqueKeys;
import org.h2.util.json.JSONValidationTargetWithoutUniqueKeys;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueJson;
import org.h2.value.ValueNull;

public final class IsJsonPredicate extends Condition {
   private Expression left;
   private final boolean not;
   private final boolean whenOperand;
   private final boolean withUniqueKeys;
   private final JSONItemType itemType;

   public IsJsonPredicate(Expression var1, boolean var2, boolean var3, boolean var4, JSONItemType var5) {
      this.left = var1;
      this.whenOperand = var3;
      this.not = var2;
      this.withUniqueKeys = var4;
      this.itemType = var5;
   }

   public boolean needParentheses() {
      return true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return this.getWhenSQL(this.left.getSQL(var1, var2, 0), var2);
   }

   public StringBuilder getWhenSQL(StringBuilder var1, int var2) {
      var1.append(" IS");
      if (this.not) {
         var1.append(" NOT");
      }

      var1.append(" JSON");
      switch (this.itemType) {
         case VALUE:
            break;
         case ARRAY:
            var1.append(" ARRAY");
            break;
         case OBJECT:
            var1.append(" OBJECT");
            break;
         case SCALAR:
            var1.append(" SCALAR");
            break;
         default:
            throw DbException.getInternalError("itemType=" + this.itemType);
      }

      if (this.withUniqueKeys) {
         var1.append(" WITH UNIQUE KEYS");
      }

      return var1;
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      return (Expression)(!this.whenOperand && this.left.isConstant() ? ValueExpression.getBoolean(this.getValue(var1)) : this);
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.left.getValue(var1);
      return (Value)(var2 == ValueNull.INSTANCE ? ValueNull.INSTANCE : ValueBoolean.get(this.getValue(var2)));
   }

   public boolean getWhenValue(SessionLocal var1, Value var2) {
      if (!this.whenOperand) {
         return super.getWhenValue(var1, var2);
      } else {
         return var2 == ValueNull.INSTANCE ? false : this.getValue(var2);
      }
   }

   private boolean getValue(Value var1) {
      boolean var2;
      Object var4;
      switch (var1.getValueType()) {
         case 1:
         case 2:
         case 3:
         case 4:
            break;
         case 5:
         case 6:
         case 7:
            byte[] var8 = var1.getBytesNoCopy();
            var4 = this.withUniqueKeys ? new JSONValidationTargetWithUniqueKeys() : new JSONValidationTargetWithoutUniqueKeys();

            try {
               var2 = this.itemType.includes((JSONItemType)JSONBytesSource.parse(var8, (JSONTarget)var4)) ^ this.not;
            } catch (RuntimeException var7) {
               var2 = this.not;
            }

            return var2;
         case 38:
            JSONItemType var3 = ((ValueJson)var1).getItemType();
            if (!this.itemType.includes(var3)) {
               var2 = this.not;
               return var2;
            }

            if (this.withUniqueKeys && var3 != JSONItemType.SCALAR) {
               break;
            }

            var2 = !this.not;
            return var2;
         default:
            var2 = this.not;
            return var2;
      }

      String var9 = var1.getString();
      var4 = this.withUniqueKeys ? new JSONValidationTargetWithUniqueKeys() : new JSONValidationTargetWithoutUniqueKeys();

      try {
         var2 = this.itemType.includes((JSONItemType)JSONStringSource.parse(var9, (JSONTarget)var4)) ^ this.not;
      } catch (RuntimeException var6) {
         var2 = this.not;
      }

      return var2;
   }

   public boolean isWhenConditionOperand() {
      return this.whenOperand;
   }

   public Expression getNotIfPossible(SessionLocal var1) {
      return this.whenOperand ? null : new IsJsonPredicate(this.left, !this.not, false, this.withUniqueKeys, this.itemType);
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.left.setEvaluatable(var1, var2);
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.left.updateAggregate(var1, var2);
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.left.mapColumns(var1, var2, var3);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.left.isEverything(var1);
   }

   public int getCost() {
      int var1 = this.left.getCost();
      if (this.left.getType().getValueType() != 38 || this.withUniqueKeys && this.itemType != JSONItemType.SCALAR) {
         var1 += 10;
      } else {
         ++var1;
      }

      return var1;
   }

   public int getSubexpressionCount() {
      return 1;
   }

   public Expression getSubexpression(int var1) {
      if (var1 == 0) {
         return this.left;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }
}
