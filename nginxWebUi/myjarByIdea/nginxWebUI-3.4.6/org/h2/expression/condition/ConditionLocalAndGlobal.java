package org.h2.expression.condition;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

public class ConditionLocalAndGlobal extends Condition {
   private Expression local;
   private Expression global;

   public ConditionLocalAndGlobal(Expression var1, Expression var2) {
      if (var2 == null) {
         throw DbException.getInternalError();
      } else {
         this.local = var1;
         this.global = var2;
      }
   }

   public boolean needParentheses() {
      return this.local != null || this.global.needParentheses();
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      if (this.local == null) {
         return this.global.getUnenclosedSQL(var1, var2);
      } else {
         this.local.getSQL(var1, var2, 0);
         var1.append("\n    _LOCAL_AND_GLOBAL_ ");
         return this.global.getSQL(var1, var2, 0);
      }
   }

   public void createIndexConditions(SessionLocal var1, TableFilter var2) {
      if (this.local != null) {
         this.local.createIndexConditions(var1, var2);
      }

      this.global.createIndexConditions(var1, var2);
   }

   public Value getValue(SessionLocal var1) {
      if (this.local == null) {
         return this.global.getValue(var1);
      } else {
         Value var2 = this.local.getValue(var1);
         Value var3;
         if (!var2.isFalse() && !(var3 = this.global.getValue(var1)).isFalse()) {
            return (Value)(var2 != ValueNull.INSTANCE && var3 != ValueNull.INSTANCE ? ValueBoolean.TRUE : ValueNull.INSTANCE);
         } else {
            return ValueBoolean.FALSE;
         }
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.global = this.global.optimize(var1);
      if (this.local != null) {
         this.local = this.local.optimize(var1);
         Expression var2 = ConditionAndOr.optimizeIfConstant(var1, 0, this.local, this.global);
         if (var2 != null) {
            return var2;
         }
      }

      return this;
   }

   public void addFilterConditions(TableFilter var1) {
      if (this.local != null) {
         this.local.addFilterConditions(var1);
      }

      this.global.addFilterConditions(var1);
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      if (this.local != null) {
         this.local.mapColumns(var1, var2, var3);
      }

      this.global.mapColumns(var1, var2, var3);
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      if (this.local != null) {
         this.local.setEvaluatable(var1, var2);
      }

      this.global.setEvaluatable(var1, var2);
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      if (this.local != null) {
         this.local.updateAggregate(var1, var2);
      }

      this.global.updateAggregate(var1, var2);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return (this.local == null || this.local.isEverything(var1)) && this.global.isEverything(var1);
   }

   public int getCost() {
      int var1 = this.global.getCost();
      if (this.local != null) {
         var1 += this.local.getCost();
      }

      return var1;
   }

   public int getSubexpressionCount() {
      return this.local == null ? 1 : 2;
   }

   public Expression getSubexpression(int var1) {
      switch (var1) {
         case 0:
            return this.local != null ? this.local : this.global;
         case 1:
            if (this.local != null) {
               return this.global;
            }
         default:
            throw new IndexOutOfBoundsException();
      }
   }
}
