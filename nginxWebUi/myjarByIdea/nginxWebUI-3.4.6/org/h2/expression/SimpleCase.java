package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public final class SimpleCase extends Expression {
   private Expression operand;
   private SimpleWhen when;
   private Expression elseResult;
   private TypeInfo type;

   public SimpleCase(Expression var1, SimpleWhen var2, Expression var3) {
      this.operand = var1;
      this.when = var2;
      this.elseResult = var3;
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.operand.getValue(var1);

      for(SimpleWhen var3 = this.when; var3 != null; var3 = var3.next) {
         Expression[] var4 = var3.operands;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Expression var7 = var4[var6];
            if (var7.getWhenValue(var1, var2)) {
               return var3.result.getValue(var1).convertTo(this.type, var1);
            }
         }
      }

      if (this.elseResult != null) {
         return this.elseResult.getValue(var1).convertTo(this.type, var1);
      } else {
         return ValueNull.INSTANCE;
      }
   }

   public Expression optimize(SessionLocal var1) {
      TypeInfo var2 = TypeInfo.TYPE_UNKNOWN;
      this.operand = this.operand.optimize(var1);
      boolean var3 = this.operand.isConstant();
      Value var4 = null;
      if (var3) {
         var4 = this.operand.getValue(var1);
      }

      TypeInfo var5 = this.operand.getType();

      for(SimpleWhen var6 = this.when; var6 != null; var6 = var6.next) {
         Expression[] var7 = var6.operands;

         for(int var8 = 0; var8 < var7.length; ++var8) {
            Expression var9 = var7[var8].optimize(var1);
            if (!var9.isWhenConditionOperand()) {
               TypeInfo.checkComparable(var5, var9.getType());
            }

            if (var3) {
               if (var9.isConstant()) {
                  if (var9.getWhenValue(var1, var4)) {
                     return var6.result.optimize(var1);
                  }
               } else {
                  var3 = false;
               }
            }

            var7[var8] = var9;
         }

         var6.result = var6.result.optimize(var1);
         var2 = combineTypes(var2, var6.result);
      }

      if (this.elseResult != null) {
         this.elseResult = this.elseResult.optimize(var1);
         if (var3) {
            return this.elseResult;
         }

         var2 = combineTypes(var2, this.elseResult);
      } else if (var3) {
         return ValueExpression.NULL;
      }

      if (var2.getValueType() == -1) {
         var2 = TypeInfo.TYPE_VARCHAR;
      }

      this.type = var2;
      return this;
   }

   static TypeInfo combineTypes(TypeInfo var0, Expression var1) {
      if (!var1.isNullConstant()) {
         TypeInfo var2 = var1.getType();
         int var3 = var2.getValueType();
         if (var3 != -1 && var3 != 0) {
            var0 = TypeInfo.getHigherType(var0, var2);
         }
      }

      return var0;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      this.operand.getUnenclosedSQL(var1.append("CASE "), var2);

      for(SimpleWhen var3 = this.when; var3 != null; var3 = var3.next) {
         var1.append(" WHEN");
         Expression[] var4 = var3.operands;
         int var5 = 0;

         for(int var6 = var4.length; var5 < var6; ++var5) {
            if (var5 > 0) {
               var1.append(',');
            }

            var4[var5].getWhenSQL(var1, var2);
         }

         var3.result.getUnenclosedSQL(var1.append(" THEN "), var2);
      }

      if (this.elseResult != null) {
         this.elseResult.getUnenclosedSQL(var1.append(" ELSE "), var2);
      }

      return var1.append(" END");
   }

   public TypeInfo getType() {
      return this.type;
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.operand.mapColumns(var1, var2, var3);

      for(SimpleWhen var4 = this.when; var4 != null; var4 = var4.next) {
         Expression[] var5 = var4.operands;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Expression var8 = var5[var7];
            var8.mapColumns(var1, var2, var3);
         }

         var4.result.mapColumns(var1, var2, var3);
      }

      if (this.elseResult != null) {
         this.elseResult.mapColumns(var1, var2, var3);
      }

   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.operand.setEvaluatable(var1, var2);

      for(SimpleWhen var3 = this.when; var3 != null; var3 = var3.next) {
         Expression[] var4 = var3.operands;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Expression var7 = var4[var6];
            var7.setEvaluatable(var1, var2);
         }

         var3.result.setEvaluatable(var1, var2);
      }

      if (this.elseResult != null) {
         this.elseResult.setEvaluatable(var1, var2);
      }

   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.operand.updateAggregate(var1, var2);

      for(SimpleWhen var3 = this.when; var3 != null; var3 = var3.next) {
         Expression[] var4 = var3.operands;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Expression var7 = var4[var6];
            var7.updateAggregate(var1, var2);
         }

         var3.result.updateAggregate(var1, var2);
      }

      if (this.elseResult != null) {
         this.elseResult.updateAggregate(var1, var2);
      }

   }

   public boolean isEverything(ExpressionVisitor var1) {
      if (!this.operand.isEverything(var1)) {
         return false;
      } else {
         for(SimpleWhen var2 = this.when; var2 != null; var2 = var2.next) {
            Expression[] var3 = var2.operands;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Expression var6 = var3[var5];
               if (!var6.isEverything(var1)) {
                  return false;
               }
            }

            if (!var2.result.isEverything(var1)) {
               return false;
            }
         }

         if (this.elseResult != null && !this.elseResult.isEverything(var1)) {
            return false;
         } else {
            return true;
         }
      }
   }

   public int getCost() {
      int var1 = 1;
      int var2 = 0;
      var1 += this.operand.getCost();

      for(SimpleWhen var3 = this.when; var3 != null; var3 = var3.next) {
         Expression[] var4 = var3.operands;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Expression var7 = var4[var6];
            var1 += var7.getCost();
         }

         var2 = Math.max(var2, var3.result.getCost());
      }

      if (this.elseResult != null) {
         var2 = Math.max(var2, this.elseResult.getCost());
      }

      return var1 + var2;
   }

   public int getSubexpressionCount() {
      int var1 = 1;

      for(SimpleWhen var2 = this.when; var2 != null; var2 = var2.next) {
         var1 += var2.operands.length + 1;
      }

      if (this.elseResult != null) {
         ++var1;
      }

      return var1;
   }

   public Expression getSubexpression(int var1) {
      if (var1 >= 0) {
         if (var1 == 0) {
            return this.operand;
         }

         int var2 = 1;

         for(SimpleWhen var3 = this.when; var3 != null; var3 = var3.next) {
            Expression[] var4 = var3.operands;
            int var5 = var4.length;
            int var6 = var1 - var2;
            if (var6 < var5) {
               return var4[var6];
            }

            var2 += var5;
            if (var1 == var2++) {
               return var3.result;
            }
         }

         if (this.elseResult != null && var1 == var2) {
            return this.elseResult;
         }
      }

      throw new IndexOutOfBoundsException();
   }

   public static final class SimpleWhen {
      Expression[] operands;
      Expression result;
      SimpleWhen next;

      public SimpleWhen(Expression var1, Expression var2) {
         this(new Expression[]{var1}, var2);
      }

      public SimpleWhen(Expression[] var1, Expression var2) {
         this.operands = var1;
         this.result = var2;
      }

      public void setWhen(SimpleWhen var1) {
         this.next = var1;
      }
   }
}
