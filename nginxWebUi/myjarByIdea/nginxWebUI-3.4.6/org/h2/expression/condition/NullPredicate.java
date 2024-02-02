package org.h2.expression.condition;

import java.util.ArrayList;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionList;
import org.h2.expression.ValueExpression;
import org.h2.index.IndexCondition;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;

public final class NullPredicate extends SimplePredicate {
   private boolean optimized;

   public NullPredicate(Expression var1, boolean var2, boolean var3) {
      super(var1, var2, var3);
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return this.getWhenSQL(this.left.getSQL(var1, var2, 0), var2);
   }

   public StringBuilder getWhenSQL(StringBuilder var1, int var2) {
      return var1.append(this.not ? " IS NOT NULL" : " IS NULL");
   }

   public Expression optimize(SessionLocal var1) {
      if (this.optimized) {
         return this;
      } else {
         Expression var2 = super.optimize(var1);
         if (var2 != this) {
            return var2;
         } else {
            this.optimized = true;
            if (!this.whenOperand && this.left instanceof ExpressionList) {
               ExpressionList var3 = (ExpressionList)this.left;
               if (!var3.isArray()) {
                  int var4 = 0;

                  for(int var5 = var3.getSubexpressionCount(); var4 < var5; ++var4) {
                     if (var3.getSubexpression(var4).isNullConstant()) {
                        if (this.not) {
                           return ValueExpression.FALSE;
                        }

                        ArrayList var6 = new ArrayList(var5 - 1);

                        int var7;
                        for(var7 = 0; var7 < var4; ++var7) {
                           var6.add(var3.getSubexpression(var7));
                        }

                        for(var7 = var4 + 1; var7 < var5; ++var7) {
                           Expression var8 = var3.getSubexpression(var7);
                           if (!var8.isNullConstant()) {
                              var6.add(var8);
                           }
                        }

                        this.left = (Expression)(var6.size() == 1 ? (Expression)var6.get(0) : new ExpressionList((Expression[])var6.toArray(new Expression[0]), false));
                        break;
                     }
                  }
               }
            }

            return this;
         }
      }
   }

   public Value getValue(SessionLocal var1) {
      return ValueBoolean.get(this.getValue(this.left.getValue(var1)));
   }

   public boolean getWhenValue(SessionLocal var1, Value var2) {
      return !this.whenOperand ? super.getWhenValue(var1, var2) : this.getValue(var2);
   }

   private boolean getValue(Value var1) {
      if (var1.getType().getValueType() == 41) {
         Value[] var2 = ((ValueRow)var1).getList();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Value var5 = var2[var4];
            if (var5 != ValueNull.INSTANCE ^ this.not) {
               return false;
            }
         }

         return true;
      } else {
         return var1 == ValueNull.INSTANCE ^ this.not;
      }
   }

   public Expression getNotIfPossible(SessionLocal var1) {
      if (this.whenOperand) {
         return null;
      } else {
         Expression var2 = this.optimize(var1);
         if (var2 != this) {
            return var2.getNotIfPossible(var1);
         } else {
            switch (this.left.getType().getValueType()) {
               case -1:
               case 41:
                  return null;
               default:
                  return new NullPredicate(this.left, !this.not, false);
            }
         }
      }
   }

   public void createIndexConditions(SessionLocal var1, TableFilter var2) {
      if (!this.not && !this.whenOperand && var2.getTable().isQueryComparable()) {
         if (this.left instanceof ExpressionColumn) {
            createNullIndexCondition(var2, (ExpressionColumn)this.left);
         } else if (this.left instanceof ExpressionList) {
            ExpressionList var3 = (ExpressionList)this.left;
            if (!var3.isArray()) {
               int var4 = 0;

               for(int var5 = var3.getSubexpressionCount(); var4 < var5; ++var4) {
                  Expression var6 = var3.getSubexpression(var4);
                  if (var6 instanceof ExpressionColumn) {
                     createNullIndexCondition(var2, (ExpressionColumn)var6);
                  }
               }
            }
         }

      }
   }

   private static void createNullIndexCondition(TableFilter var0, ExpressionColumn var1) {
      if (var0 == var1.getTableFilter() && var1.getType().getValueType() != 41) {
         var0.addIndexCondition(IndexCondition.get(6, var1, ValueExpression.NULL));
      }

   }
}
