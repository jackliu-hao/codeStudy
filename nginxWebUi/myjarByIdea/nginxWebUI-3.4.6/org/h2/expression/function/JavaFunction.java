package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.schema.FunctionAlias;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public final class JavaFunction extends Expression implements NamedExpression {
   private final FunctionAlias functionAlias;
   private final FunctionAlias.JavaMethod javaMethod;
   private final Expression[] args;

   public JavaFunction(FunctionAlias var1, Expression[] var2) {
      this.functionAlias = var1;
      this.javaMethod = var1.findJavaMethod(var2);
      if (this.javaMethod.getDataType() == null) {
         throw DbException.get(90022, this.getName());
      } else {
         this.args = var2;
      }
   }

   public Value getValue(SessionLocal var1) {
      return this.javaMethod.getValue(var1, this.args, false);
   }

   public TypeInfo getType() {
      return this.javaMethod.getDataType();
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      Expression[] var4 = this.args;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Expression var7 = var4[var6];
         var7.mapColumns(var1, var2, var3);
      }

   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = this.functionAlias.isDeterministic();
      int var3 = 0;

      for(int var4 = this.args.length; var3 < var4; ++var3) {
         Expression var5 = this.args[var3].optimize(var1);
         this.args[var3] = var5;
         var2 &= var5.isConstant();
      }

      return (Expression)(var2 ? ValueExpression.get(this.getValue(var1)) : this);
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      Expression[] var3 = this.args;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Expression var6 = var3[var5];
         if (var6 != null) {
            var6.setEvaluatable(var1, var2);
         }
      }

   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return writeExpressions(this.functionAlias.getSQL(var1, var2).append('('), this.args, var2).append(')');
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      Expression[] var3 = this.args;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Expression var6 = var3[var5];
         if (var6 != null) {
            var6.updateAggregate(var1, var2);
         }
      }

   }

   public String getName() {
      return this.functionAlias.getName();
   }

   public boolean isEverything(ExpressionVisitor var1) {
      switch (var1.getType()) {
         case 2:
         case 5:
         case 8:
            if (!this.functionAlias.isDeterministic()) {
               return false;
            }
         case 3:
         case 4:
         case 6:
         default:
            break;
         case 7:
            var1.addDependency(this.functionAlias);
      }

      Expression[] var2 = this.args;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Expression var5 = var2[var4];
         if (var5 != null && !var5.isEverything(var1)) {
            return false;
         }
      }

      return true;
   }

   public int getCost() {
      int var1 = this.javaMethod.hasConnectionParam() ? 25 : 5;
      Expression[] var2 = this.args;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Expression var5 = var2[var4];
         var1 += var5.getCost();
      }

      return var1;
   }

   public int getSubexpressionCount() {
      return this.args.length;
   }

   public Expression getSubexpression(int var1) {
      return this.args[var1];
   }
}
